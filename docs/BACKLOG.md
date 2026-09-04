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
