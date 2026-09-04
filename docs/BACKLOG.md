# Backlog priorizado — LectoStart

Tickets accionables derivados de [`USER_STORIES.md`](./USER_STORIES.md) y [`ARCHITECTURE.md`](./ARCHITECTURE.md), en el orden en que conviene pedírselos a Claude Code. Cada ticket indica de qué depende, para no bloquearse a mitad de implementación.

Versión: 0.1
Fecha: 2026-09-03

---

## Cómo usar este backlog

- Los tickets están numerados en orden de ejecución sugerido dentro de cada fase.
- **"Depende de"** son tickets que deben estar terminados antes de empezar ese ticket.
- **DoD** (Definición de terminado) es el criterio mínimo para marcar el ticket como hecho — no sustituye los criterios de aceptación completos de la historia de usuario referenciada, los complementa a nivel de implementación.
- Fase 1 termina en un hito concreto (T-020): el flujo principal completo, de punta a punta, con datos reales. Ese es el primer punto en que hay algo demostrable.

---

## Fase 0 — Fundaciones ✅ (completada 2026-09-03)

T-001 a T-006 completados y verificados: build exitoso (`./gradlew assembleDebug`) e instalado/recorrido de punta a punta en un emulador real (AVD `medium_phone`), incluyendo el desvío a Modo Rescate. Sin crashes en logcat durante el recorrido completo. Ver [`README.md`](../README.md) para cómo compilar/correr.

Notas para quien continúe (Fase 1 en adelante):
- El template inicial trajo **Navigation 3** (`androidx.navigation3`), no la Navigation Compose clásica — cumple el mismo rol descrito en `ARCHITECTURE.md` §4.
- `hilt-navigation-compose` quedó fijado en `1.3.0` (no `1.4.0`): esa versión exige `compileSdk 37` + AGP 9.1.0, y el proyecto está en `compileSdk 36` / AGP 9.0.1. Si se sube el `compileSdk` más adelante, se puede revisar subir esta dependencia también.
- `LectoStartDatabase` tiene una `PlaceholderEntity` temporal (Room exige al menos una entidad declarada). Se elimina en T-007 al agregar las entidades reales.
- Las 13 pantallas del flujo están wireadas en `core/navigation/LectoStartNavigation.kt` con UI placeholder (`core/ui/PlaceholderScreen.kt`); cada ticket de Fase 1 reemplaza su placeholder por la implementación real siguiendo `USER_STORIES.md`.
- El repo aún no tiene `git init` — pendiente de decisión del usuario.


### T-001 — Setup del proyecto Android
Crear proyecto Kotlin + Compose (Empty Activity), configurar Gradle con las dependencias de `ARCHITECTURE.md` §2: Compose/Material 3, Navigation Compose, Room, Hilt, Coroutines, `kotlinx.serialization`, y dependencias de testing (JUnit, Turbine, Room testing).

- **Depende de:** —
- **DoD:** el proyecto compila y corre un "Hello LectoStart" en un emulador/dispositivo.

### T-002 — Estructura de paquetes
Crear el árbol de carpetas de `ARCHITECTURE.md` §3 (`core`, `onboarding`, `procrastination`, `reading`, `comprehension`, `progress`, `rescue`), cada una con subcarpetas `data/` y `ui/` vacías (o con un `.gitkeep`/README breve).

- **Depende de:** T-001
- **DoD:** estructura de carpetas presente en el repo, coincide con `ARCHITECTURE.md` §3.

### T-003 — Theme base de Compose
Definir `core/ui/theme`: paleta de colores mínima, tipografía, `LectoStartTheme` envolviendo la app. No requiere diseño visual final, solo una base consistente.

- **Depende de:** T-002
- **DoD:** `MainActivity` renderiza cualquier pantalla dentro de `LectoStartTheme` sin usar estilos de Material por defecto.

### T-004 — Base de datos Room
Crear `LectoStartDatabase` (vacía de entidades por ahora, o con un placeholder), configurar Hilt para proveerla (`core/di/DatabaseModule`).

- **Depende de:** T-002
- **DoD:** la app arranca con la base de datos inicializada vía Hilt, sin crashear.

### T-005 — Setup de Hilt
`LectoStartApp : Application()` con `@HiltAndroidApp`, `MainActivity` con `@AndroidEntryPoint`.

- **Depende de:** T-001
- **DoD:** un ViewModel de prueba `@HiltViewModel` se inyecta correctamente en una pantalla.

### T-006 — Esqueleto de navegación
`NavHost` central en `core/navigation` con **todas** las rutas del flujo (`ARCHITECTURE.md` §4) apuntando a pantallas placeholder (texto con el nombre de la ruta + botón "Siguiente"). Esto permite recorrer el flujo completo de principio a fin aunque no tenga funcionalidad real todavía.

- **Depende de:** T-003
- **DoD:** se puede navegar de `welcome` a `progress` tocando "Siguiente" en cada pantalla placeholder, incluyendo el desvío a `rescueMode`.

**Hito Fase 0:** app que compila, con estructura de paquetes, DB y DI listos, y un esqueleto navegable de extremo a extremo (sin lógica real).

---

## Fase 1 — Flujo principal end-to-end

**T-007 completado (2026-09-03).** Las 7 entidades de `ARCHITECTURE.md` §5 y sus DAOs están implementados, cada uno en el paquete de su feature (`onboarding/data`, `procrastination/data`, `reading/data`, `comprehension/data`), registrados en `LectoStartDatabase` y expuestos vía Hilt (`DatabaseModule`). Verificado con 7 tests instrumentados (Room in-memory, `androidTest`) corridos en el emulador — los 7 pasan (`./gradlew connectedDebugAndroidTest`).

**T-008 completado (2026-09-03).** `UserRepository`, `DiagnosticRepository`, `ReadingRepository`, `SessionRepository`, `ComprehensionRepository` — interfaz + implementación Room, bindeadas vía `@Binds` en `core/di/RepositoryModule.kt`. Se montó infraestructura de testing con Hilt (`HiltTestRunner`, `TestDatabaseModule` con DB en memoria) que se reutilizará para los tests de ViewModels de Fase 1 (`ARCHITECTURE.md` §11). 13 tests instrumentados pasan en el emulador (7 de T-007 + 6 de `RepositoryInjectionTest`, que prueba inyección + round-trip real de cada repositorio). `DiagnosticEntity.answersJson` ahora sí es JSON real (`kotlinx-serialization-json`), no el placeholder de coma-separado que se había considerado.

**T-009 completado (2026-09-03).** `assets/seed_readings.json` con 2 lecturas de prueba ("La importancia de la educación inclusiva", "El aprendizaje activo en la universidad"), 4 preguntas cada una (mezcla literal/inferencial/crítica). `SeedLoader` (`core/data/seed`) las parsea con `kotlinx-serialization-json` y las inserta vía `ReadingRepository`; es idempotente (no repuebla si ya hay lecturas). Se dispara desde `MainActivity.onCreate` (inyectado vía Hilt) — se evaluó inyectarlo en `LectoStartApp` con un `@EntryPoint`, pero para una sola Activity no aportaba nada sobre inyectarlo directamente ahí. Verificado dos veces: 2 tests instrumentados (15/15 en total) y, además, instalación limpia real en el emulador (`pm clear` + relanzar) con inspección directa del `lectostart.db` del dispositivo vía `sqlite3` — las 2 lecturas y 8 preguntas están ahí sin intervención manual.

**T-010 completado (2026-09-03).** `WelcomeScreen` (US-01) real: logo textual + tagline + botón "Comenzar". La lógica de "saltar onboarding si ya hay perfil" vive en `AppStartViewModel` (`core/navigation`), que observa `UserRepository.observeUser()` y decide la ruta inicial (`Onboarding` → `Welcome`, `Home` → `Progress`) antes de montar el `NavDisplay`; mientras resuelve muestra un `CircularProgressIndicator` breve. Verificado con 2 tests unitarios (Turbine, `UserRepository` falso) que cubren ambos casos, y visualmente en el emulador (instalación limpia → pantalla real → botón navega a Consentimiento). Nota: `hiltViewModel()` de `hilt-navigation-compose:1.3.0` salió deprecado (movido a un paquete nuevo en 1.4.0+) — no se actualizó porque 1.4.0 exige `compileSdk 37` (ver nota de T-005/T-006); revisar cuando se suba el compileSdk.

**T-011 completado (2026-09-03).** `ConsentScreen` (US-02) real: texto de consentimiento (borrador, ver `PRD.md` §10) en columna con scroll, botones "Aceptar y continuar" / "No aceptar". `ConsentViewModel` genera el `userId` (UUID) al aceptar, llama a `UserRepository.recordConsent` y expone `ConsentStatus` (`Pending`/`Accepted`/`Declined`). "No aceptar" muestra un cierre respetuoso sin forzar nada, con opción de volver. El `userId` generado viaja hacia `CreateProfile` como argumento de navegación (`CreateProfile` pasó de `data object` a `data class(userId)` en `NavigationKeys.kt`) para que T-012 cree el perfil con el mismo id. El bloqueo de avance sin aceptar es estructural: el grafo de navegación no tiene ningún atajo que salte `Consent`. Verificado con 2 tests unitarios (Turbine) y en el emulador con instalación limpia: revisé el `lectostart.db` real del dispositivo y confirmé el `ConsentEntity` persistido tras aceptar, y probé el camino de "No aceptar" por separado.

**T-012 completado (2026-09-03).** `CreateProfileScreen` (US-03) real: formulario (apodo, edad, carrera, semestre, objetivo principal como radio buttons) con validación bloqueante de edad y objetivo principal (los dos campos que la historia marca como obligatorios); usa el `userId` generado en el consentimiento (T-011) para crear el `UserEntity`. **Bug real encontrado y corregido durante la verificación manual:** `AppStartViewModel` observaba `UserRepository.observeUser()` de forma continua durante toda la sesión, así que en cuanto se creaba el perfil a mitad del onboarding, la app saltaba de golpe a "Mi progreso" abandonando Diagnóstico/Lectura/etc. — la decisión de ruta inicial debe tomarse una sola vez al arrancar (`take(1)`), no reactivamente; corregido y reverificado en el emulador (crear perfil ya no salta el flujo; reabrir la app con perfil existente sí salta directo a "Mi progreso", como corresponde). También corregí que solo el círculo del `RadioButton` respondía al toque, no toda la fila (`Modifier.selectable` en el `Row`). 7/7 tests unitarios pasan, incluyendo `MainDispatcherRule` (nueva, `core/`) que ahora usan los tres ViewModels de onboarding para que `viewModelScope.launch` sea determinista en tests JVM.

**T-013 completado (2026-09-03).** `DiagnosticScreen` (US-04) real: 5 preguntas Likert en una sola pantalla con scroll, botón "Finalizar diagnóstico" deshabilitado hasta responder todas. `DiagnosticScorer` (función pura, `procrastination/data`) suma las 5 respuestas (rango 5–25) y las divide en tercios (Bajo/Intermedio/Alto) — cortes son borrador, ajustables con el investigador. `DiagnosticResultScreen` recibe el nivel calculado como argumento de navegación (no vía repo, es dato transitorio de la pantalla anterior) y muestra "Tu punto de partida". El `userId` se obtiene de `UserRepository.getUser()` dentro del ViewModel (no se threadea por navegación desde Diagnostic en adelante — el usuario ya existe en este punto). 9 tests unitarios nuevos (7 de `DiagnosticScorer` cubriendo cada nivel + límites, 2 de `DiagnosticViewModel`). Verificado en el emulador con instalación limpia: las 5 preguntas respondidas con "Siempre" dieron nivel "Alto" tanto en la UI como en el `DiagnosticEntity` persistido (`answersJson=[5,5,5,5,5]`).

**T-014 completado (2026-09-03).** `ReadingListScreen` (US-05) real: lista de las lecturas sembradas (T-009), cada una en un `Card` clickeable mostrando título y fuente. Al seleccionar, navega a `DurationPicker(readingId)` — `DurationPicker` pasó de `data object` a `data class(readingId)` en `NavigationKeys.kt`, porque a diferencia del usuario no existe un concepto de "lectura actual" persistido; tiene que viajar por navegación. Sin tests unitarios nuevos (el ViewModel es un `stateIn` trivial, ya cubierto por los tests de `ReadingRepository` en T-008). Verificado en el emulador: ambas lecturas se ven con título y fuente, seleccionar una navega correctamente a "¿Cuánto tiempo tienes?".

**T-015 completado (2026-09-03).** `DurationPickerScreen` (US-06) real: 4 `FilterChip` (5/10/15/20 min), sin ViewModel (selección de UI pura, sin persistencia propia). "Continuar" deshabilitado hasta elegir una opción. `StartFiveMin` pasó a `data class(readingId, durationMin)` y `RescueMode` a `data class(readingId)` en `NavigationKeys.kt`, para que el desvío de Modo Rescate (US-13) pueda forzar 5 min con la misma lectura al llegar a `StartFiveMin`. Verificado en el emulador: los 4 chips se ven, seleccionar una duración navega a "Solo 5 minutos", y el desvío `DurationPicker → Modo Rescate → StartFiveMin` funciona correctamente.

**T-016 completado (2026-09-04).** `StartFiveMinScreen` (US-07) real: mensaje de bajo compromiso + "EMPEZAR 5 MINUTOS", crea la `SesionLectura` (vía `SessionRepository.startSession`) al tocar el botón y navega a Lectura con el `sessionId` generado. `ReadingSession` pasó a `data class(sessionId)`. `StartFiveMin` ahora también lleva `viaRescueMode`/`rescueReason` (default false/null) para que Modo Rescate (US-13, T-023 aún pendiente) pueda registrarlos sin tocar esta pantalla de nuevo. 2 tests unitarios nuevos. Verificado en el emulador: `SessionEntity` persistida con `readingId`, `chosenDurationMin`, `viaRescueMode=0`, timestamps correctos.

**T-017 completado (2026-09-04).** `ReadingSessionScreen` (US-08) real, con el patrón MVI acotado de `ARCHITECTURE.md` §10.1: `ReadingSessionIntent` (`Start/Tick/Pause/Resume/Finish/Abandon`) + reducer puro `reduceReadingSession` (sin Android, sin mocks) en `ReadingSessionState.kt`, y `ReadingSessionViewModel` orquestando el timer (coroutine con `delay(1000)`) y la persistencia. Progreso basado en tiempo transcurrido vs. duración elegida (resuelve la decisión abierta de `ARCHITECTURE.md` §12). Detección de abandono vía `ON_STOP` del lifecycle (no vía `onDispose`, para no confundir "cerrar la app" con "navegar hacia adelante"). 11 tests unitarios (8 del reducer cubriendo cada transición + 3 del ViewModel). **Verificado a fondo en el emulador** — el caso más completo hasta ahora: (1) flujo normal, cronómetro corriendo en tiempo real (00:02 → 00:17 en ~15s reales), texto completo, tip visible, "Terminar sesión" persiste `completed=1` con el tiempo real; (2) camino de abandono real — background la app a mitad de sesión (botón Home) y la `SessionEntity` queda con `completed=0` y el tiempo parcial (`actualTimeReadSec=18`) correctamente capturado, no solo simulado en test.

**T-018 completado (2026-09-04).** `QuestionsScreen` (US-09) real. **Decisión pendiente resuelta** (`ARCHITECTURE.md` §6): preguntas con `expectedAnswer` definido se evalúan por coincidencia simple (`contains`, sin distinguir mayúsculas); preguntas abiertas (`expectedAnswer == null` — el caso de las 8 preguntas sembradas en T-009) se autoevalúan con "¿Sientes que respondiste bien?" Sí/No, tal como sugería la arquitectura. Validación bloquea el envío si falta texto o autoevaluación en cualquier pregunta, mostrando qué falta. 4 tests unitarios (validación, autoevaluación, coincidencia correcta/incorrecta con `expectedAnswer`). Verificado en el emulador: las 4 preguntas de una lectura se muestran con su campo y chips Sí/No, el envío vacío bloquea con mensaje de error, y tras responder todo se guardan 4 `AnswerEntity` en la base de datos real con `isCorrect` correcto y navega a Resultado de sesión.

**T-019 completado (2026-09-04).** `SessionResultScreen` (US-10) real: tiempo de lectura, % de comprensión y preguntas correctas calculados desde `AnswerEntity` (`SessionResultViewModel`), mensaje motivacional elegido de un set estático de 3 (no generado dinámicamente, según el MVP). 2 tests unitarios (cálculo de % con respuestas mixtas, y caso sin respuestas sin división por cero). Verificado en el emulador con una sesión real completa: "Comprensión: 100%", "Preguntas correctas: 4/4", "Continuar mañana" navega a "Mi progreso".

**🎯 Hito Fase 1 alcanzado (T-020, 2026-09-04).** Recorrí el flujo principal completo de punta a punta en el emulador, con datos reales en cada paso: Bienvenida → Consentimiento → Crear perfil → Diagnóstico de procrastinación → Elegir lectura → Elegir duración → Solo 5 minutos → Lectura (cronómetro + progreso reales) → Preguntas de comprensión → Resultado de sesión → Mi progreso. Cada entidad (`UserEntity`, `ConsentEntity`, `DiagnosticEntity`, `SessionEntity`, `AnswerEntity`) se verificó directamente en el `lectostart.db` del dispositivo, no solo en tests. Esto demuestra el valor central de LectoStart tal como lo define `MVP.md`: reducir la barrera de inicio con sesiones cortas y comprobar comprensión inmediatamente después. 35 tests unitarios + 15 instrumentados pasan (`./gradlew testDebugUnitTest connectedDebugAndroidTest`).

**T-023 completado (2026-09-04) — Fase 2 completa.** `RescueModeScreen` (US-13) real: 5 motivos (`RescueReason`, `rescue/data`) con su micro-estrategia estática cada uno — "Tengo demasiado que leer" reutiliza textualmente el ejemplo de `MVP.md`. Sin ViewModel (selección de UI pura, como `DurationPickerScreen`); la persistencia de `viaRescueMode`/`rescueReason` ya existía desde T-016 en `StartFiveMinViewModel`, así que esta pantalla solo necesitaba pasar el motivo elegido por navegación hasta `StartFiveMin`. Verificado en el emulador de punta a punta: `DurationPicker → Modo Rescate → "Tengo demasiado que leer" → estrategia mostrada → EMPEZAR 5 MINUTOS`, y la `SessionEntity` resultante quedó con `chosenDurationMin=5, viaRescueMode=1, rescueReason=TOO_MUCH_TO_READ` en la base de datos real.

Con esto la app cubre el alcance completo del MVP definido en `MVP.md` §3 (Fases 1 y 2). Pendiente: Fase 3 (T-024/T-025, exportación de datos — herramienta de investigador, no bloqueante para el piloto) y T-026/T-027 (post-piloto/opcional).

### T-007 — Entidades y DAOs Room
Implementar las entidades de `ARCHITECTURE.md` §5 (`UserEntity`, `ConsentEntity`, `DiagnosticEntity`, `ReadingEntity`, `QuestionEntity`, `SessionEntity`, `AnswerEntity`) con sus DAOs (`@Insert`, queries con `Flow`), registradas en `LectoStartDatabase`.

- **Depende de:** T-004
- **DoD:** cada entidad tiene su DAO con al menos insert + query básica; test de Room in-memory confirma insert/lectura de cada una.

### T-008 — Repositorios (interfaces + implementación Room)
`UserRepository`, `DiagnosticRepository`, `ReadingRepository`, `SessionRepository`, `ComprehensionRepository`, bindeados vía Hilt (`RepositoryModule`).

- **Depende de:** T-007
- **DoD:** cada repositorio expone funciones suspend/`Flow` sobre su(s) entidad(es); inyectable en un ViewModel de prueba.

### T-009 — Contenido semilla (lecturas fijas)
`assets/seed_readings.json` con al menos 1–2 lecturas de prueba (título, texto, preguntas), y `SeedLoader` que las prepobla en Room en el primer arranque (`ARCHITECTURE.md` §6).

- **Depende de:** T-008
- **DoD:** al instalar la app desde cero, `ReadingEntity`/`QuestionEntity` contienen las lecturas del JSON sin intervención manual.

### T-010 — US-01: Pantalla de bienvenida
Implementar `WelcomeScreen` real (logo, tagline, botón "Comenzar"), con la lógica de "saltar si ya hay perfil" (`ARCHITECTURE.md` §4).

- **Depende de:** T-006, T-008
- **DoD:** cumple los criterios de aceptación de US-01.

### T-011 — US-02: Consentimiento informado
`ConsentScreen` con texto de consentimiento (placeholder hasta validación con comité de ética — ver `PRD.md` §10), botones Aceptar/No aceptar, persistencia en `ConsentEntity`. Bloqueo de navegación si no se acepta.

- **Depende de:** T-010
- **DoD:** cumple los criterios de aceptación de US-02, incluyendo el bloqueo de avance sin aceptación.

### T-012 — US-03: Crear perfil
`CreateProfileScreen`: genera UUID, formulario con validación de campos obligatorios, guarda `UserEntity`.

- **Depende de:** T-011
- **DoD:** cumple los criterios de aceptación de US-03.

### T-013 — US-04: Diagnóstico de procrastinación
`DiagnosticScreen` (preguntas Likert) + `DiagnosticScorer` (función pura, testeada unitariamente, `ARCHITECTURE.md` §7) + `DiagnosticResultScreen` ("Tu punto de partida").

- **Depende de:** T-012
- **DoD:** cumple los criterios de aceptación de US-04; `DiagnosticScorer` tiene tests unitarios con casos de cada nivel.

### T-014 — US-05: Lista de lecturas
`ReadingListScreen` mostrando las lecturas sembradas (T-009).

- **Depende de:** T-013, T-009
- **DoD:** cumple los criterios de aceptación de US-05.

### T-015 — US-06: Elegir duración
`DurationPickerScreen` (chips 5/10/15/20 min).

- **Depende de:** T-014
- **DoD:** cumple los criterios de aceptación de US-06.

### T-016 — US-07: "Solo 5 minutos"
`StartFiveMinScreen`: mensaje de bajo compromiso, botón de inicio que crea `SessionEntity` (`fecha_inicio`, `duracion_elegida`, etc.).

- **Depende de:** T-015
- **DoD:** cumple los criterios de aceptación de US-07.

### T-017 — US-08: Sesión de lectura (timer + MVI)
`ReadingSessionScreen` + `ReadingSessionViewModel` con el patrón MVI acotado de `ARCHITECTURE.md` §10.1 (`Intent`s `Start/Tick/Pause/Resume/Finish/Abandon`, reducer puro testeado sin mocks). Incluye barra de progreso, cronómetro, tip estático, y manejo de abandono (ver decisión abierta en `USER_STORIES.md` US-08).

- **Depende de:** T-016
- **DoD:** cumple los criterios de aceptación de US-08; el reducer tiene tests unitarios cubriendo cada transición (`Start→Tick`, `Pause→Resume`, `Finish`, `Abandon`).

### T-018 — US-09: Preguntas de comprensión
`QuestionsScreen`: muestra preguntas de la lectura activa, valida respuestas completas, guarda `AnswerEntity` con evaluación de corrección.

- **Depende de:** T-017
- **DoD:** cumple los criterios de aceptación de US-09. Nota: para preguntas abiertas, aplicar el criterio pendiente de `ARCHITECTURE.md` §6 (autoevaluación o sin scoring automático) — resolver antes de este ticket si aún no está definido.

### T-019 — US-10: Resultado de sesión
`SessionResultScreen`: tiempo leído, % comprensión (calculado desde `AnswerEntity`), preguntas correctas, mensaje motivacional.

- **Depende de:** T-018
- **DoD:** cumple los criterios de aceptación de US-10.

### T-020 — Hito: QA manual del flujo principal
Recorrer manualmente el flujo completo (`welcome` → `sessionResult`) en un dispositivo/emulador real, con datos reales persistidos, verificando que cada entidad se escribe correctamente (inspección de la DB con App Inspector o similar).

- **Depende de:** T-010 a T-019
- **DoD:** flujo principal completo funcionando de punta a punta sin crashes; datos verificados en la base de datos. **Este es el hito que demuestra el valor central del producto (ver `MVP.md` Fase 1).**

---

## Fase 2 — Progreso y Modo Rescate

**T-021 completado (2026-09-04).** `ProgressScreen` (US-11) real: lista de sesiones pasadas (más reciente primero, ya venía ordenado por `SessionRepository` desde T-007) con lectura, fecha, tiempo leído y % de comprensión; estado vacío cuando no hay sesiones. Agregué un botón "Nueva lectura" que faltaba en la navegación — sin él, la app quedaba en un callejón sin salida tras la primera sesión (`MVP.md` describe "Volver a leer" como parte del flujo principal, pero ninguna pantalla lo implementaba todavía). **Bug real encontrado y corregido:** `Progress` es un `data object` (singleton); empujarlo dos veces al backstack (una vez al iniciar una lectura nueva "reseteando" el backstack, otra vez al volver de `SessionResult`) hacía que Navigation3 reutilizara el `ViewModelStore` de la primera entrada en vez de crear uno nuevo, mostrando en el historial la comprensión de la sesión anterior al momento de la creación (0%, antes de que la sesión existiera) en vez del valor real (100%) — verificado comparándolo contra el mismo dato ya correcto en `SessionResultScreen`. Se corrigió restructurando la navegación para que solo exista una entrada `Progress` viva a la vez (documentado con una nota en el código). Verificado en el emulador sin reiniciar la app (el escenario exacto donde ocurría el bug): historial coincide con el resultado real tras completar la sesión.

**T-022 completado (2026-09-04).** `StreakCalculator` (función pura, `progress/data`) cuenta días consecutivos con al menos una sesión **completada**, hacia atrás desde hoy, deteniéndose en el primer hueco — si hoy no hay sesión, la racha es 0 aunque haya una racha previa. 6 tests unitarios (racha activa, roto por hueco, sin sesión hoy, primer día, sin sesiones, mismo día no infla la racha). `ProgressScreen` muestra "🔥 Racha actual: N día(s)" (singular/plural correcto). Verificado en el emulador: "Racha actual: 1 día" tras una sesión completada hoy.

### T-021 — US-11: Historial de sesiones
`ProgressScreen` (lista): sesiones pasadas ordenadas por fecha, estado vacío si no hay ninguna.

- **Depende de:** T-020
- **DoD:** cumple los criterios de aceptación de US-11.

### T-022 — US-12: Racha de constancia
Cálculo de racha (días consecutivos) sobre `SessionEntity`, mostrado en `ProgressScreen`.

- **Depende de:** T-021
- **DoD:** cumple los criterios de aceptación de US-12; lógica de racha testeada unitariamente (casos: racha activa, racha rota, primer día).

### T-023 — US-13: Modo Rescate
`RescueModeScreen`: 5 motivos, estrategia estática por motivo, botón que inicia sesión con `via_modo_rescate = true` y `motivo_rescate` seteado, duración 5 min preseleccionada. Acceso desde `durationPicker` y `startFiveMin`.

- **Depende de:** T-016 (necesita el flujo de inicio de sesión ya funcional)
- **DoD:** cumple los criterios de aceptación de US-13.

**Hito Fase 2:** app completa según alcance MVP definido en `MVP.md` §3, lista para piloto con los ~10 participantes.

---

## Fase 3 — Pulido y datos de investigación

### T-024 — Exportador de datos
`DataExporter` (`ARCHITECTURE.md` §8): serializa `UserEntity`, `DiagnosticEntity`, `SessionEntity`, `AnswerEntity` a JSON/CSV, comparte vía `Intent.ACTION_SEND` + `FileProvider`.

- **Depende de:** T-020
- **DoD:** desde la app se puede generar y compartir un archivo con todos los datos de un usuario (o de todos los usuarios del dispositivo, si aplica a pruebas locales).

### T-025 — Acceso a la exportación
Pantalla mínima (configuración o similar) desde donde disparar T-024. No requiere ser vistosa.

- **Depende de:** T-024
- **DoD:** hay un punto de entrada en la UI para exportar datos, sin necesidad de tocar código/logs para obtenerlos.

### T-026 — Ajustes de UX post-piloto
Placeholder: se llena con hallazgos concretos después de correr el piloto con los ~10 participantes (Fase 2 completada). No estimable de antemano.

- **Depende de:** piloto ejecutado (fuera del desarrollo, actividad de investigación)
- **DoD:** N/A hasta tener hallazgos.

### T-027 (opcional, si hay tiempo) — Mejoras de lectura
Modo oscuro y/o tamaño de letra ajustable en `ReadingSessionScreen` (marcadas como nice-to-have en `MVP.md` §3, no bloqueantes).

- **Depende de:** T-017
- **DoD:** ambas opciones funcionan si se implementan; ninguna es requisito para considerar el MVP completo.

---

## Fase 4 — Post-MVP (no priorizado aún)

OCR/foto, subida de PDF, generación automática de preguntas por IA, gamificación avanzada, funciones sociales, sincronización/backend remoto, notificaciones inteligentes, multi-idioma, UI de administración de lecturas. Ver `PRD.md` §7.2 y `MVP.md` §4. No se crean tickets hasta validar el flujo principal con el piloto.

---

## Resumen de hitos

| Hito | Tickets | Qué demuestra |
|---|---|---|
| Fin de Fase 0 | T-001 a T-006 | App con esqueleto navegable, sin lógica real |
| Fin de Fase 1 | T-007 a T-020 | **Flujo principal completo y funcional** — el valor central de LectoStart |
| Fin de Fase 2 | T-021 a T-023 | MVP completo, listo para piloto con participantes |
| Fin de Fase 3 | T-024 a T-027 | Datos exportables + ajustes post-piloto |

---

## Siguiente paso

Con PRD, MVP, user stories, arquitectura y backlog cerrados, ya hay base suficiente para empezar a codear desde **T-001**. El siguiente documento pendiente en la lista original (`LectoStart_Conversation_Export.md`) sería el **README del proyecto** — útil para dejarlo listo justo antes o justo después de T-001, cuando ya exista el repo Android real.
