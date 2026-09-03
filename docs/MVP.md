# MVP — LectoStart

Documento vivo para ir plasmando el alcance funcional del MVP antes de codear. Complementa a [`PRD.md`](./PRD.md).

Versión: 0.1 (borrador inicial)
Fecha: 2026-09-03

---

## 1. Principio rector

> No implementar desde el día 1 todas las funciones posibles. El núcleo mínimo es: **empezar una lectura pequeña → comprobar comprensión → registrar progreso.** Todo lo demás espera.

## 2. Flujo principal (happy path)

```
Onboarding (bienvenida + consentimiento informado + perfil)
  → Diagnóstico de procrastinación
  → Agregar lectura (pegar texto)
  → Elegir duración de sesión (5/10/15/20 min)
  → Pantalla "Solo 5 minutos" (arranque de bajo compromiso)
  → Lectura (con temporizador y progreso)
  → Preguntas de comprensión
  → Resultado de sesión
  → Historial / Mi progreso
```

Camino alterno (bloqueo/abandono):

```
Estudiante no quiere empezar / se bloquea
  → Modo Rescate (selecciona motivo)
  → Micro-estrategia sugerida
  → Botón "Comenzar 5 minutos" → se une al flujo principal
```

## 3. Alcance del MVP — pantallas y funciones núcleo

| # | Pantalla | Función mínima incluida | Explícitamente fuera del MVP |
|---|---|---|---|
| 1 | Bienvenida / Onboarding | Logo, tagline, botón "Comenzar" | Ilustraciones elaboradas, animaciones |
| 1b | Consentimiento informado | Texto de consentimiento del estudio, checkbox/botón de aceptación explícita, registro con timestamp | Firma digital, versionado de consentimiento |
| 2 | Crear perfil | Genera UUID anónimo; nombre/apodo (no identificante), edad, carrera, semestre, objetivo principal | Login social, verificación de email |
| 3 | Diagnóstico de procrastinación | 4–6 preguntas tipo Likert, cálculo de "nivel de inicio" simple | Escalas psicométricas validadas complejas, comparación con normas poblacionales |
| 4 | Agregar lectura | Pegar texto (obligatorio); título; opcional autor/fuente | Subir PDF, foto/OCR, enlaces web con scraping |
| 5 | Elegir duración | Chips 5/10/15/20 min | Duraciones personalizadas, recomendación automática por IA |
| 6 | "Solo 5 minutos" | Mensaje de bajo compromiso + botón de inicio + temporizador | — |
| 7 | Pantalla de lectura | Texto, barra de progreso, cronómetro, botón "Terminar sesión" | Subrayado, notas, modo oscuro, tamaño de letra ajustable (nice-to-have si hay tiempo, no bloqueante) |
| 8 | Preguntas de comprensión | 3–5 preguntas asociadas al texto (predefinidas por quien carga la lectura), mezcla literal/inferencial/crítica | Generación automática de preguntas por IA |
| 9 | Resultado de sesión | Tiempo leído, % comprensión, mensaje motivacional simple | Gráficas elaboradas, comparativas históricas complejas |
| 10 | Historial / Mi progreso | Lista de sesiones pasadas con fecha, tiempo, % comprensión; racha simple (días consecutivos) | Insignias múltiples, sistema de puntos, gráficas |
| 11 | Modo Rescate | 5 motivos predefinidos → 1 micro-estrategia de texto por motivo → botón a sesión de 5 min | Personalización de estrategias, historial de uso de Modo Rescate |

**Nota sobre preguntas de comprensión:** para el MVP, dado que no hay generación automática por IA, las preguntas se cargan junto con el texto (el propio investigador/autor de la lectura las define al crear el contenido de prueba). Esto es suficiente para validar el flujo y es coherente con "evitar sobreingeniería".

## 4. Fuera de alcance del MVP (recordatorio, ver PRD §7.2)

OCR/foto, PDF, generación de preguntas por IA, gamificación avanzada, funciones sociales, sync multi-dispositivo, notificaciones inteligentes, multi-idioma.

## 5. Modelo de datos (borrador funcional, no técnico todavía)

```
ConsentimientoInformado
 - usuario_id
 - aceptado (bool)
 - fecha_aceptacion
 - version_texto

Usuario
 - id (UUID autogenerado, anónimo)
 - nombre/apodo (no identificante, solo personalización)
 - edad
 - carrera
 - semestre
 - objetivo_principal
 - fecha_registro

DiagnosticoProcrastinacion
 - id
 - usuario_id
 - respuestas[] (pregunta, valor Likert)
 - nivel_calculado
 - fecha

Lectura
 - id
 - usuario_id (quién la agregó)
 - titulo
 - texto
 - fuente/autor (opcional)
 - fecha_creacion

PreguntaComprension
 - id
 - lectura_id
 - enunciado
 - tipo (literal | inferencial | critica)
 - respuesta_esperada / criterio

SesionLectura
 - id
 - usuario_id
 - lectura_id
 - duracion_elegida (5/10/15/20)
 - tiempo_real_leido
 - completada (bool)
 - via_modo_rescate (bool)
 - motivo_rescate (si aplica)
 - fecha_inicio / fecha_fin

RespuestaSesion
 - id
 - sesion_id
 - pregunta_id
 - respuesta_usuario
 - correcta (bool o score)

ProgresoUsuario (derivado, no necesariamente tabla propia)
 - racha_actual
 - total_sesiones
 - promedio_comprension
```

## 6. Decisiones tomadas

Estas decisiones ya están cerradas (2026-09-03) y guían la arquitectura técnica:

1. **Persistencia: local-only (Room/SQLite), con miras a backend futuro.**
   Proyecto petite, sin necesidad de mover datos entre dispositivos en esta etapa. Para no bloquear el escalamiento futuro, la capa de datos se construye con un patrón repositorio (`ReadingRepository`, `SessionRepository`, etc.) que hoy habla con Room y mañana puede hablar con una API remota sin tocar el resto de la app. Se agrega exportación (JSON/CSV) para sacar los datos del dispositivo mientras no hay backend.

2. **Contenido de lecturas: fijo por ahora, con miras a lecturas cargadas por el investigador o el usuario.**
   Para el MVP, las lecturas y sus preguntas de comprensión vienen **empaquetadas con la app** (contenido fijo, igual para todos los participantes — permite comparar resultados entre ellos). El modelo de datos ya trata `Lectura` como una entidad independiente del usuario que la creó, así que agregar un flujo de "cargar tu propia lectura" más adelante es una extensión, no un rediseño.

3. **Identificación de participante y consentimiento: ID anónimo autogenerado + consentimiento in-app.**
   Al crear el perfil, la app genera un `UUID` local (no se pide nombre real como campo obligatorio — el "nombre/apodo" es solo para personalizar la experiencia, no para identificar). Se agrega una **pantalla de consentimiento informado** al inicio del onboarding (antes de "Crear perfil") que el participante debe aceptar explícitamente para continuar; la aceptación se registra con timestamp. Esto permite que la app sea descargable libremente y el estudio escale sin que el investigador tenga que gestionar códigos manualmente. *(Pendiente validar el texto legal/formato del consentimiento con el comité de ética correspondiente — el contenido del texto no es una decisión técnica.)*

4. **Stack técnico Android: Kotlin + Jetpack Compose.**

5. **Piloto inicial: ~10 participantes**, con la app diseñada para no depender de ese número — la arquitectura (repositorio + exportación + ID anónimo) es la misma si el estudio crece más adelante.

**Implicación directa en el flujo (§2):** el onboarding queda como `Bienvenida → Consentimiento informado → Crear perfil (genera UUID) → Diagnóstico de procrastinación → ...`

## 7. Fases de implementación propuestas

**Fase 0 — Fundaciones**
Setup del proyecto Android, navegación entre pantallas vacías (skeleton), definición de modelo de datos local.

**Fase 1 — Flujo principal end-to-end**
Onboarding → Diagnóstico → Agregar lectura (texto pegado) → Elegir duración → Solo 5 min → Lectura → Preguntas → Resultado. Todo funcional con datos reales guardados localmente. **Este es el hito que demuestra el valor central del producto.**

**Fase 2 — Progreso y Modo Rescate**
Historial/Mi progreso (lista + racha simple) y Modo Rescate (5 motivos + micro-estrategias).

**Fase 3 — Pulido y datos de investigación**
Exportación de datos (JSON/CSV), ajustes de UX detectados en pruebas, mejoras menores de lectura (modo oscuro/tamaño de letra si hay tiempo).

**Fase 4 (post-MVP, no planificada aún)**
OCR, PDF, generación de preguntas por IA, gamificación avanzada, backend/sync.

## 8. Próximos documentos a producir (en orden)

1. User stories + criterios de aceptación por pantalla (a partir de la tabla de §3).
2. Arquitectura técnica de la app Android (capas, navegación, almacenamiento local, librerías).
3. Backlog priorizado por fase (tickets accionables para Claude Code).
4. Plan de pruebas (manual/E2E del flujo principal).

---

*Actualizar este documento conforme se cierren las decisiones pendientes (§6) y se avance de fase.*
