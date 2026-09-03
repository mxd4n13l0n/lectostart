# User Stories — LectoStart

Documento derivado de [`MVP.md`](./MVP.md). Cada historia mapea a una pantalla/función del MVP y queda etiquetada con la fase de [`MVP.md` §7](./MVP.md#7-fases-de-implementación-propuestas) a la que pertenece, para poder convertirlas directamente en backlog.

Versión: 0.1
Fecha: 2026-09-03

Formato: `Como [rol], quiero [acción], para [beneficio]`, con criterios de aceptación en Given/When/Then.

Roles usados: **Participante** (estudiante usando la app), **Investigador** (quien prepara el contenido del estudio, en este MVP también administra las lecturas fijas).

---

## Épica 1 — Onboarding y consentimiento
*Fase 1*

### US-01 — Pantalla de bienvenida
Como **participante**, quiero ver una pantalla de bienvenida clara al abrir la app por primera vez, para entender de qué trata LectoStart antes de comprometerme a nada.

**Criterios de aceptación:**
- Dado que abro la app por primera vez, cuando carga, entonces veo el logo, el tagline "Empieza pequeño. Comprende más." y un botón "Comenzar".
- Dado que ya completé el onboarding antes, cuando abro la app, entonces no vuelvo a ver la bienvenida (voy directo a la pantalla principal / última sesión).
- El botón "Comenzar" navega a la pantalla de Consentimiento informado (US-02).

---

### US-02 — Consentimiento informado
Como **participante**, quiero leer y aceptar (o rechazar) explícitamente el consentimiento informado del estudio antes de crear mi perfil, para que mi participación sea voluntaria e informada.

**Criterios de aceptación:**
- Dado que estoy en la pantalla de consentimiento, cuando la veo, entonces se muestra el texto completo de consentimiento (contenido definido por el investigador/comité de ética — placeholder en MVP hasta validación formal) y dos acciones: "Aceptar y continuar" / "No aceptar".
- Dado que toco "Aceptar y continuar", cuando confirmo, entonces se registra `usuario_id` (aún no existe, se crea junto con el perfil en US-03), `aceptado = true`, `fecha_aceptacion` y `version_texto`, y avanzo a Crear perfil.
- Dado que toco "No aceptar", cuando confirmo, entonces la app no continúa el onboarding y muestra un mensaje de cierre respetuoso (no se fuerza a aceptar).
- No es posible llegar a ninguna otra pantalla del flujo (diagnóstico, lectura, etc.) sin haber aceptado el consentimiento.

**Nota:** el texto legal/formato exacto es una decisión de investigación, no de producto (ver `PRD.md` §10). La pantalla debe soportar texto largo con scroll.

---

### US-03 — Crear perfil
Como **participante**, quiero crear un perfil básico con datos no identificantes, para que la app personalice mi experiencia sin comprometer mi anonimato.

**Criterios de aceptación:**
- Dado que acepté el consentimiento, cuando llego a "Crear perfil", entonces la app genera automáticamente un UUID como `usuario_id` (no visible/editable por el usuario).
- Dado que estoy en el formulario, cuando lo completo, entonces puedo ingresar: nombre o apodo, edad, carrera, semestre/cuatrimestre, y seleccionar un objetivo principal (comenzar mis lecturas / evitar distracciones / organizar mi tiempo / comprender mejor).
- Dado que dejo campos obligatorios vacíos (edad, objetivo principal), cuando toco "Continuar", entonces la app muestra validación y no avanza.
- Dado que completo el formulario válidamente, cuando toco "Continuar", entonces el perfil se guarda localmente vinculado al `usuario_id` y avanzo al Diagnóstico de procrastinación.

---

## Épica 2 — Diagnóstico de procrastinación
*Fase 1*

### US-04 — Responder diagnóstico inicial
Como **participante**, quiero responder un breve cuestionario sobre mis hábitos de procrastinación al leer, para que la app (y el estudio) tengan una línea base de mi comportamiento.

**Criterios de aceptación:**
- Dado que termino de crear mi perfil, cuando llego al diagnóstico, entonces veo entre 4 y 6 preguntas tipo Likert (Nunca / Casi nunca / Algunas veces / Casi siempre / Siempre), una a la vez o en una sola pantalla con scroll (decisión de UI, no bloqueante).
- Dado que respondo todas las preguntas, cuando toco "Finalizar diagnóstico", entonces la app calcula un `nivel_calculado` (p. ej. Bajo/Intermedio/Alto) con una regla simple y determinista (a definir en arquitectura técnica).
- Dado que se calculó el nivel, cuando veo la pantalla de resultado del diagnóstico, entonces se muestra el mensaje "Tu punto de partida: Nivel [X]" junto con el mensaje "No necesitas terminar todo de una vez. Vamos a comenzar con 5 minutos."
- El diagnóstico y su resultado quedan guardados como línea base asociada al `usuario_id`, con fecha.
- No se puede volver a repetir el diagnóstico inicial desde este flujo (es de una sola vez); no bloqueante para el MVP si se decide permitir repetirlo más adelante.

---

## Épica 3 — Lecturas (contenido fijo)
*Fase 1*

### US-05 — Ver lecturas disponibles
Como **participante**, quiero ver una lista de lecturas disponibles cargadas por el investigador, para elegir cuál voy a trabajar.

**Criterios de aceptación:**
- Dado que termino el diagnóstico (o vuelvo a la pantalla principal en una sesión posterior), cuando llego a "Agregar lectura" / "Elegir lectura", entonces veo una lista de lecturas fijas empaquetadas en la app, cada una con título y, si existe, autor/fuente.
- Dado que toco una lectura de la lista, cuando la selecciono, entonces avanzo a "Elegir duración de sesión" (US-06) con esa lectura asociada.
- Para el MVP no existe opción de pegar texto propio en la UI principal (queda documentada como extensión futura en `MVP.md`, no se implementa en Fase 1).

**Nota técnica para el Investigador:** en el MVP, las lecturas y sus preguntas (US-05b) se cargan como datos empaquetados en la app (seed data), no mediante una UI de administración. Una UI de administración es candidata post-MVP.

---

## Épica 4 — Sesión de lectura
*Fase 1*

### US-06 — Elegir duración de sesión
Como **participante**, quiero elegir cuánto tiempo tengo disponible para leer, para que la sesión se ajuste a mi realidad y no se sienta como un compromiso grande.

**Criterios de aceptación:**
- Dado que seleccioné una lectura, cuando llego a "¿Cuánto tiempo tienes?", entonces veo 4 opciones: 5, 10, 15 y 20 minutos.
- Dado que selecciono una duración, cuando toco "Continuar", entonces avanzo a la pantalla "Solo 5 minutos" (US-07), independientemente de la duración elegida (el mensaje de bajo compromiso siempre enmarca el inicio como "solo unos minutos").

---

### US-07 — Iniciar con "Solo 5 minutos"
Como **participante**, quiero que la app me anime a empezar con un compromiso mínimo, para vencer la resistencia inicial a comenzar la lectura.

**Criterios de aceptación:**
- Dado que elegí una duración, cuando llego a esta pantalla, entonces veo el mensaje "No tienes que terminar la lectura. Solo empieza durante 5 minutos." y un botón "EMPEZAR 5 MINUTOS" (o "EMPEZAR [N] MINUTOS" si se decide reflejar la duración elegida — a definir en diseño visual).
- Dado que toco el botón de inicio, cuando se confirma, entonces se crea un registro `SesionLectura` con `fecha_inicio`, `duracion_elegida`, `lectura_id`, `usuario_id`, y navego a la pantalla de Lectura (US-08) con el temporizador corriendo.

---

### US-08 — Leer con temporizador y progreso
Como **participante**, quiero ver el texto con un temporizador y una barra de progreso mientras leo, para tener una noción clara de cuánto llevo y cuánto falta.

**Criterios de aceptación:**
- Dado que inicié la sesión, cuando estoy en la pantalla de lectura, entonces veo el texto de la lectura, un cronómetro corriendo (cuenta el tiempo real leído) y una barra de progreso (basada en scroll o en tiempo transcurrido vs. duración elegida — a definir en diseño técnico).
- Dado que estoy leyendo, cuando llega al menos un momento definido del flujo, entonces puedo ver el tip contextual "Si te distraes, regresa a la última oración que recuerdes y continúa desde ahí." (estático, no dinámico, en MVP).
- Dado que quiero terminar antes de lo planeado o después de completar la duración elegida, cuando toco "Terminar sesión", entonces se registra `tiempo_real_leido`, `fecha_fin`, `completada = true`, y avanzo a Preguntas de comprensión (US-09).
- Dado que cierro la app o salgo de la pantalla sin tocar "Terminar sesión", cuando vuelvo a abrir la app, entonces la sesión queda marcada como `completada = false` (abandonada) con el tiempo parcial registrado si es técnicamente viable; si no, se documenta como limitación conocida del MVP.
- Modo oscuro, tamaño de letra ajustable, subrayado y notas quedan fuera del MVP (ver `MVP.md` §3) — no son criterio de aceptación de esta historia.

---

## Épica 5 — Comprensión lectora
*Fase 1*

### US-09 — Responder preguntas de comprensión
Como **participante**, quiero responder preguntas sobre lo que leí inmediatamente después de la sesión, para que se evalúe mi comprensión mientras el contenido está fresco.

**Criterios de aceptación:**
- Dado que terminé una sesión de lectura, cuando llego a "¿Cuánto comprendiste?", entonces veo entre 3 y 5 preguntas asociadas a la lectura (`PreguntaComprension`), con una mezcla de tipos literal/inferencial/crítica.
- Dado que respondo todas las preguntas, cuando toco "Enviar respuestas", entonces cada respuesta se guarda como `RespuestaSesion` vinculada a `sesion_id` y `pregunta_id`, con su evaluación de corrección (`correcta` o score, según el criterio definido por pregunta).
- Dado que intento enviar con preguntas sin responder, cuando toco "Enviar respuestas", entonces la app me indica qué preguntas faltan y no avanza.
- Al enviar respuestas válidas, avanzo a Resultado de sesión (US-10).

---

### US-10 — Ver resultado de la sesión
Como **participante**, quiero ver un resumen de mi sesión al terminar, para saber cómo me fue y sentirme motivado a continuar.

**Criterios de aceptación:**
- Dado que envié mis respuestas, cuando llego a "Sesión completada", entonces veo: tiempo de lectura real, % de comprensión (calculado a partir de `RespuestaSesion`), y cuántas preguntas respondí correctamente (ej. "4/5").
- Dado que veo el resultado, cuando la pantalla carga, entonces se muestra un mensaje motivacional simple (texto estático o elegido de un set pequeño predefinido, no generado dinámicamente en MVP).
- Dado que toco "Continuar mañana" (o el CTA equivalente), cuando confirmo, entonces vuelvo a la pantalla principal de la app.

---

## Épica 6 — Progreso e historial
*Fase 2*

### US-11 — Ver historial de sesiones
Como **participante**, quiero ver una lista de mis sesiones pasadas, para notar mi propio avance en el tiempo.

**Criterios de aceptación:**
- Dado que tengo al menos una sesión completada, cuando entro a "Mi progreso", entonces veo una lista de sesiones pasadas ordenadas por fecha (más reciente primero), cada una con fecha, lectura, tiempo leído y % de comprensión.
- Dado que no tengo ninguna sesión completada todavía, cuando entro a "Mi progreso", entonces veo un estado vacío que invita a iniciar la primera sesión.

---

### US-12 — Ver racha de constancia
Como **participante**, quiero ver cuántos días consecutivos he usado la app, para sentir un incentivo simple a mantener el hábito.

**Criterios de aceptación:**
- Dado que completé sesiones en días consecutivos, cuando entro a "Mi progreso", entonces veo un contador de "racha actual" en días.
- Dado que dejé pasar un día sin completar ninguna sesión, cuando vuelvo a abrir la app, entonces la racha se reinicia a 0 (o a 1 si completo una sesión ese mismo día).
- Insignias múltiples y sistema de puntos quedan fuera del MVP (ver `MVP.md` §3).

---

## Épica 7 — Modo Rescate
*Fase 2*

### US-13 — Activar Modo Rescate
Como **participante**, quiero poder pedir ayuda cuando no quiero empezar a leer, para recibir una estrategia concreta en vez de simplemente abandonar la tarea.

**Criterios de aceptación:**
- Dado que estoy en cualquier punto del flujo antes de iniciar una sesión (p. ej. en "Elegir duración" o "Solo 5 minutos"), cuando toco "Modo Rescate", entonces veo la pregunta "¿Qué está pasando?" con 5 opciones: Estoy cansado / Me estoy distrayendo / Tengo demasiado que leer / No tengo tiempo / Simplemente no quiero empezar.
- Dado que selecciono un motivo, cuando confirmo, entonces veo una micro-estrategia de texto específica para ese motivo (contenido estático predefinido, 1 estrategia por motivo en MVP).
- Dado que veo la micro-estrategia, cuando toco "Comenzar 5 minutos", entonces se registra que la sesión que inicia tiene `via_modo_rescate = true` y `motivo_rescate = [motivo elegido]`, y entro al flujo normal de sesión (US-07 en adelante) con duración de 5 minutos preseleccionada.

---

## Resumen por fase (para backlog)

| Fase | Historias |
|---|---|
| Fase 1 — Flujo principal end-to-end | US-01, US-02, US-03, US-04, US-05, US-06, US-07, US-08, US-09, US-10 |
| Fase 2 — Progreso y Modo Rescate | US-11, US-12, US-13 |
| Fase 3 — Pulido y datos de investigación | (sin historias de producto nuevas; incluye exportación de datos JSON/CSV, a detallar como tarea técnica, no historia de usuario) |

---

## Siguiente documento

Arquitectura técnica de la app Android: capas, navegación (Compose Navigation), modelo de datos en Room, estructura de módulos, y cómo se aísla la capa de datos para soportar el futuro backend (ver `MVP.md` §6, decisión 1).
