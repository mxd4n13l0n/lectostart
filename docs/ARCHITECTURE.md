# Arquitectura técnica — LectoStart (Android)

Documento derivado de [`PRD.md`](./PRD.md), [`MVP.md`](./MVP.md) y [`USER_STORIES.md`](./USER_STORIES.md). Define cómo se construye la app, no qué hace (eso ya está definido en los documentos anteriores).

Versión: 0.1
Fecha: 2026-09-03

---

## 1. Principios arquitectónicos

1. **Un solo módulo Gradle (`:app`), organizado por feature.** Nada de multi-módulo ni Clean Architecture de 4 capas — es sobreingeniería para un proyecto petite. Se gana separación de responsabilidades con paquetes claros, no con módulos.
2. **Repositorio como frontera de datos.** Cada feature habla con una interfaz `XxxRepository`. Hoy la implementación es Room (local-only, decisión tomada en `MVP.md` §6). El día que exista backend, se agrega una implementación remota detrás de la misma interfaz — el resto de la app no cambia.
3. **MVVM simple.** `Composable` (UI) → `ViewModel` (estado + lógica de presentación) → `Repository` (datos). Sin capa de "casos de uso" separada salvo que una lógica se reutilice en más de un ViewModel (ej. cálculo de nivel de procrastinación, cálculo de % de comprensión).
4. **Estado unidireccional.** Cada pantalla expone un único `StateFlow<UiState>` desde su ViewModel; los eventos de usuario son funciones que el ViewModel expone (no hay Elm/Redux completo, es demasiado para el alcance).
5. **Los datos de investigación son ciudadanos de primera clase.** Nada de "ya lo agrego después" para timestamps, `usuario_id`, o resultados de comprensión — se modelan desde la Fase 1 aunque la UI que los muestra sea mínima.

---

## 2. Stack tecnológico

| Capa | Elección | Motivo |
|---|---|---|
| Lenguaje | Kotlin | Decidido en `MVP.md` §6 |
| UI | Jetpack Compose + Material 3 | Estándar actual, menos código que Views/XML |
| Navegación | Navigation Compose | Integración directa con Compose, maneja el flujo lineal del MVP sin esfuerzo extra |
| Persistencia | Room | SQLite con capa type-safe; encaja con la decisión local-only |
| Concurrencia/estado | Kotlin Coroutines + `StateFlow` | Estándar para Compose + Room |
| Inyección de dependencias | Hilt | Reduce boilerplate de construir ViewModels/Repositories a mano; costo de setup bajo y ya viene integrado con `ViewModel` de Compose |
| Serialización (export) | `kotlinx.serialization` | Para exportar sesiones/respuestas a JSON |
| Testing | JUnit + Turbine (Flow) + Room in-memory DB | Cobertura mínima pero real sobre ViewModels y Repositories |

No se introduce backend, networking (Retrofit/Ktor), ni autenticación en el MVP — coherente con la decisión local-only.

---

## 3. Estructura de paquetes

Organización **por feature**, no por capa técnica, para que cada carpeta se corresponda con una épica de `USER_STORIES.md`:

```
com.lectostart.app/
├── LectoStartApp.kt                 // Application class (Hilt entry point)
├── MainActivity.kt                  // Host de Compose + NavHost
│
├── core/
│   ├── data/
│   │   ├── LectoStartDatabase.kt    // RoomDatabase
│   │   ├── seed/                    // Carga de lecturas/preguntas fijas (assets JSON)
│   │   └── export/                  // Exportador JSON/CSV de sesiones
│   ├── di/                          // Módulos Hilt (DatabaseModule, RepositoryModule)
│   ├── navigation/                  // Rutas y NavGraph central
│   └── ui/theme/                    // Theme, colores, tipografía Compose
│
├── onboarding/                      // US-01, US-02, US-03 (bienvenida, consentimiento, perfil)
│   ├── data/                        // UserEntity, UserDao, ConsentEntity, ConsentDao, UserRepository
│   └── ui/                          // WelcomeScreen, ConsentScreen, CreateProfileScreen + ViewModels
│
├── procrastination/                 // US-04 (diagnóstico)
│   ├── data/                        // DiagnosticEntity, DiagnosticDao, DiagnosticRepository
│   └── ui/                          // DiagnosticScreen, DiagnosticResultScreen + ViewModel
│
├── reading/                         // US-05 a US-08 (lecturas + sesión + temporizador)
│   ├── data/                        // ReadingEntity, ReadingDao, SessionEntity, SessionDao, ReadingRepository, SessionRepository
│   └── ui/                          // ReadingListScreen, DurationPickerScreen, StartFiveMinScreen, ReadingSessionScreen + ViewModels
│
├── comprehension/                   // US-09, US-10 (preguntas + resultado)
│   ├── data/                        // QuestionEntity, QuestionDao, AnswerEntity, AnswerDao, ComprehensionRepository
│   └── ui/                          // QuestionsScreen, SessionResultScreen + ViewModels
│
├── progress/                        // US-11, US-12 (historial + racha)
│   ├── data/                        // (reutiliza SessionRepository)
│   └── ui/                          // ProgressScreen + ViewModel
│
└── rescue/                          // US-13 (Modo Rescate)
    ├── data/                        // RescueReason (enum/sealed), estrategias estáticas
    └── ui/                          // RescueModeScreen + ViewModel
```

**Regla de dependencia:** `ui` depende de `data` dentro del mismo feature; un feature puede depender de `core`, pero no debería depender de `data` de otro feature directamente — si dos features necesitan lo mismo (ej. `reading` y `progress` ambos necesitan sesiones), ese repositorio vive en el feature "dueño" del dato (`reading`) y el otro lo consume por inyección de la interfaz.

---

## 4. Navegación

Un único `NavHost` en `core/navigation`, con rutas que reflejan el flujo de `MVP.md` §2:

```
welcome → consent → createProfile → diagnostic → diagnosticResult
  → readingList → durationPicker → startFiveMin → readingSession
  → questions → sessionResult → progress (destino "home" tras completar)

rescueMode (accesible desde durationPicker y startFiveMin) → startFiveMin (con duración=5 preseleccionada)
```

- `welcome` y `consent` solo se muestran si no existe un `UserEntity` local (primera vez). Si ya hay perfil, la app abre directo en `progress` (pantalla "home").
- Las rutas que dependen de datos (`readingSession`, `questions`, `sessionResult`) reciben `sessionId`/`readingId` como argumentos de navegación, no estado global compartido — evita bugs de estado obsoleto al navegar hacia atrás.
- `rescueMode` es un destino "satélite": puede abrirse desde varios puntos y siempre regresa al flujo normal en `startFiveMin`.

---

## 5. Modelo de datos (Room)

Traducción directa del modelo funcional de `MVP.md` §5 a entidades Room. Tipos simplificados; se ajustan durante implementación.

```kotlin
@Entity
data class UserEntity(
    @PrimaryKey val id: String,              // UUID generado en el dispositivo
    val nickname: String,
    val age: Int,
    val major: String,
    val semester: String,
    val mainGoal: String,                    // enum serializado como String
    val createdAt: Long
)

@Entity
data class ConsentEntity(
    @PrimaryKey val userId: String,
    val accepted: Boolean,
    val acceptedAt: Long,
    val textVersion: String
)

@Entity
data class DiagnosticEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val answersJson: String,                 // respuestas Likert serializadas
    val level: String,                       // Bajo | Intermedio | Alto
    val createdAt: Long
)

@Entity
data class ReadingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val text: String,
    val source: String?,
    val createdAt: Long
)

@Entity
data class QuestionEntity(
    @PrimaryKey val id: String,
    val readingId: String,
    val prompt: String,
    val type: String,                        // literal | inferencial | critica
    val expectedAnswer: String?               // criterio de corrección
)

@Entity
data class SessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val readingId: String,
    val chosenDurationMin: Int,
    val actualTimeReadSec: Long,
    val completed: Boolean,
    val viaRescueMode: Boolean,
    val rescueReason: String?,
    val startedAt: Long,
    val endedAt: Long?
)

@Entity
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val questionId: String,
    val userAnswer: String,
    val isCorrect: Boolean
)
```

`ProgresoUsuario` (racha, promedio de comprensión, total de sesiones) **no es una tabla**: se calcula en el repositorio a partir de `SessionEntity`/`AnswerEntity` (query agregada o cálculo en memoria sobre la lista de sesiones del usuario).

DAOs siguen el patrón estándar de Room (`@Insert`, `@Query` con `Flow<List<T>>` para observar cambios reactivamente desde el ViewModel).

---

## 6. Contenido semilla (lecturas y preguntas fijas)

Decisión tomada en `MVP.md` §6.2: contenido fijo, igual para todos los participantes.

**Mecanismo:** un archivo `assets/seed_readings.json` con lecturas y sus preguntas, cargado y **prepoblado en Room en el primer arranque** (`core/data/seed/SeedLoader`). Ventaja frente a hardcodear en Kotlin: el investigador puede editar/agregar lecturas de prueba sin tocar código, solo el JSON.

```json
[
  {
    "id": "reading-01",
    "title": "La importancia de la educación inclusiva",
    "source": "...",
    "text": "...",
    "questions": [
      { "id": "q1", "prompt": "¿Cuál es la idea principal del texto?", "type": "literal" },
      { "id": "q2", "prompt": "¿Qué propósito tiene el autor?", "type": "inferencial" },
      { "id": "q3", "prompt": "¿Estás de acuerdo con el planteamiento? ¿Por qué?", "type": "critica" }
    ]
  }
]
```

`expectedAnswer`/criterio de corrección: para preguntas de opción implícita o corta se puede definir coincidencia simple; para preguntas abiertas ("escribe con tus palabras..."), en el MVP se marca como autoevaluada por el propio participante (ej. "¿sientes que respondiste bien? sí/no") o se deja sin scoring automático y se analiza cualitativamente después — **esto es una decisión de diseño de evaluación pendiente, no bloqueante para avanzar con el resto de la arquitectura.**

---

## 7. Cálculo del nivel de procrastinación

Regla simple y determinista para el MVP (ajustable sin tocar arquitectura):

1. Cada respuesta Likert vale 1 (Nunca) a 5 (Siempre).
2. Se suman las respuestas de las 4–6 preguntas → puntaje total.
3. Se mapea a un nivel por rangos, ej.: Bajo (puntaje bajo), Intermedio (puntaje medio), Alto (puntaje alto) — los cortes exactos se definen junto con el investigador al construir el cuestionario final, viven como constantes en `procrastination/data`.

Esta lógica vive en un `DiagnosticScorer` puro (función sin dependencias de Android), fácil de testear unitariamente y de ajustar si el criterio del estudio cambia.

---

## 8. Exportación de datos

Decisión tomada en `MVP.md` §6.1: exportación manual mientras no hay backend.

- `core/data/export/DataExporter`: lee todas las tablas relevantes (`UserEntity`, `DiagnosticEntity`, `SessionEntity`, `AnswerEntity`) vía Room, las serializa a JSON (`kotlinx.serialization`) y opcionalmente a CSV plano por tabla.
- Se dispara desde una pantalla simple (puede vivir dentro de "Mi progreso" o una pantalla de configuración mínima, no listada como historia de usuario por ahora — es una herramienta de investigador, no del participante).
- El archivo se comparte vía `Intent.ACTION_SEND` (compartir a Drive, correo, etc.) — no requiere permisos de almacenamiento adicionales si se usa `FileProvider` + caché de la app.

---

## 9. Inyección de dependencias (Hilt)

- `LectoStartApp : Application()` anotada `@HiltAndroidApp`.
- `core/di/DatabaseModule`: provee `LectoStartDatabase` y cada DAO.
- `core/di/RepositoryModule`: bindea cada interfaz `XxxRepository` a su implementación Room (`@Binds`), dejando el punto de extensión listo para una futura `RemoteXxxRepository`.
- ViewModels anotados `@HiltViewModel`, inyectan repositorios por constructor.

---

## 10. Manejo de estado en UI

Patrón único por pantalla:

```kotlin
data class ReadingSessionUiState(
    val reading: ReadingUi? = null,
    val elapsedSec: Long = 0,
    val progress: Float = 0f,
    val isLoading: Boolean = true
)

class ReadingSessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val readingRepository: ReadingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingSessionUiState())
    val uiState: StateFlow<ReadingSessionUiState> = _uiState.asStateFlow()

    fun onFinishSession() { /* ... */ }
}
```

Sin librerías de state management adicionales (no MVI completo, no Redux) — es más de lo que este alcance necesita.

### 10.1 Excepción: `ReadingSessionViewModel` usa un MVI acotado

Se evaluó MVVM simple vs. MVI para toda la app (análisis 2026-09-03). Conclusión: **MVVM se mantiene como patrón general** (menor ceremonia, coincide con "evitar sobreingeniería" — la mayoría de pantallas son formularios/listas donde un reducer explícito no aporta nada). La única excepción es `ReadingSessionViewModel`, por dos razones específicas de esa pantalla:

1. Tiene una máquina de estados real (cronómetro corriendo, pausa/reanudación en background, detección de abandono) donde mezclar efectos secundarios y transición de estado en el mismo método (como en MVVM simple) hace más difícil testear cada transición de forma aislada.
2. Los eventos de esa pantalla (`Start`, `Tick`, `Pause`, `Resume`, `Finish`, `Abandon`) son exactamente los datos de investigación que alimentan la tesis (tiempo real leído, si se completó o abandonó la sesión) — modelarlos como `Intent`s explícitos con un reducer puro `(estado, intent) -> estado` da, gratis, un log auditable de lo que hizo el participante, y el reducer se testea sin mocks.

```kotlin
sealed interface ReadingSessionIntent {
    data object Start : ReadingSessionIntent
    data object Tick : ReadingSessionIntent
    data object Pause : ReadingSessionIntent
    data object Resume : ReadingSessionIntent
    data object Finish : ReadingSessionIntent
    data object Abandon : ReadingSessionIntent
}

// Reducer puro, testeable sin Android ni mocks
fun reduce(state: ReadingSessionUiState, intent: ReadingSessionIntent): ReadingSessionUiState

class ReadingSessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingSessionUiState())
    val uiState: StateFlow<ReadingSessionUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ReadingSessionIntent) {
        _uiState.update { reduce(it, intent) }
        // efectos secundarios (persistir en SessionRepository) se disparan aparte del reducer puro
    }
}
```

Ningún otro ViewModel del MVP adopta este patrón salvo que, al implementarlo, aparezca una complejidad de estado comparable.

---

## 11. Testing (mínimo viable)

- **`DiagnosticScorer` y cálculos de comprensión/progreso:** tests unitarios puros (sin Android).
- **Repositories:** tests con Room en memoria (`inMemoryDatabaseBuilder`) para verificar inserciones/queries clave (crear sesión, registrar respuestas, calcular racha).
- **ViewModels críticos** (flujo principal: `ReadingSessionViewModel`, `DiagnosticViewModel`): tests de estado con Turbine.
- **UI:** pruebas manuales del flujo principal antes de cada hito de fase (no se prioriza Compose UI testing automatizado en el MVP, dado el tiempo disponible).

---

## 12. Decisiones abiertas para cuando se empiece a codear

1. **Criterio de corrección para preguntas abiertas** (§6) — definir con el investigador antes de implementar `comprehension`.
2. **Cortes exactos del `DiagnosticScorer`** (§7) — depende del cuestionario final de procrastinación que se use en el estudio.
3. **Formato final de la barra de progreso en lectura** (por scroll vs. por tiempo) — decisión de UX, no bloquea el modelo de datos.
4. **Dónde vive el botón/pantalla de exportación de datos** (§8) — sugerido dentro de una pantalla de configuración mínima no listada aún como historia de usuario.
5. **Set exacto de `Intent`s de `ReadingSessionViewModel`** (§10.1) — el listado (`Start`/`Tick`/`Pause`/`Resume`/`Finish`/`Abandon`) es un borrador razonable; puede ajustarse al implementar según cómo se termine resolviendo la detección de abandono (§ criterio pendiente en `USER_STORIES.md` US-08).

Ninguna de estas bloquea empezar Fase 0 (setup del proyecto + skeleton de navegación).

---

## 13. Siguiente paso

Con PRD, MVP, user stories y arquitectura cerrados, el siguiente documento natural es el **backlog priorizado** (tickets accionables Fase 0 → Fase 1, en el orden en que se le van a pedir a Claude Code), seguido del setup real del proyecto Android (Fase 0).
