# PRD — LectoStart

**"Empieza pequeño. Comprende más."**

Versión: 0.1 (borrador inicial)
Fecha: 2026-09-03
Autor: Daniel (con apoyo de Claude Code)
Estado: Borrador — pendiente de validación

---

## 1. Resumen

LectoStart es una app móvil (Android) que ayuda a estudiantes universitarios a **empezar** lecturas académicas que tienden a posponer, y a **comprobar** cuánto comprendieron después de leer. Es también el instrumento de intervención de un trabajo de investigación de maestría sobre procrastinación académica y comprensión lectora, por lo que el producto y el diseño de investigación están acoplados: cada función del MVP debe poder justificarse desde una de las dos variables del estudio.

## 2. Problema

Estudiantes universitarios posponen el inicio de tareas de lectura (procrastinación académica), lo que reduce el tiempo real dedicado a leer y afecta negativamente la comprensión lectora. El problema no es solo "leer mejor", es **empezar**: la barrera de arranque es el punto de fricción que la app ataca primero.

## 3. Objetivo del producto

Reducir la barrera de inicio de una sesión de lectura mediante sesiones cortas y de bajo compromiso ("solo 5 minutos"), fragmentación de la tarea, y verificación inmediata de comprensión — de forma que el estudiante lea con más frecuencia y comprenda mejor lo que lee.

## 4. Vínculo con la investigación

El producto es también el instrumento de intervención de la tesis. Esto impone una restricción de diseño: **no eliminar ni simplificar en exceso los puntos de medición**, aunque sí se puede simplificar la UI alrededor de ellos.

| Variable | Rol | Cómo la aborda LectoStart |
|---|---|---|
| Procrastinación académica | Independiente (se interviene) | Sesiones de 5 min, fragmentación de la lectura, Modo Rescate, metas pequeñas, registro de sesiones/rachas |
| Comprensión lectora | Dependiente (se mide) | Preguntas literales, inferenciales y críticas al final de cada sesión; resultado de comprensión (%) |

Modelo conceptual:

```
Procrastinación → intervención (LectoStart) → actividad lectora → evaluación de comprensión
```

**Implicación de datos:** cada sesión de lectura debe quedar registrada con timestamps, tiempo real de lectura, y resultado de comprensión, de forma exportable, para que sirva como dato de investigación (ver §9).

## 5. Usuarios objetivo

**Usuario primario:** estudiante universitario (18–28 años aprox.) con lecturas académicas pendientes (artículos, capítulos, apuntes) que reconoce que las posterga.

**Perfil de uso esperado:** sesiones cortas, en momentos de baja motivación ("tengo que leer esto pero no quiero empezar"), probablemente antes de un examen o entrega.

No se diseña (por ahora) para: lectura recreativa, niños, lectura de libros completos/extensos, uso institucional/docente.

## 6. Objetivos y métricas de éxito

| Objetivo | Métrica | Meta orientativa MVP |
|---|---|---|
| El estudiante logra *empezar* sesiones que antes posponía | % de sesiones iniciadas que se completan (al menos los 5 min) | ≥ 70% |
| La app se usa de forma recurrente | Sesiones por usuario activo / semana | ≥ 3 |
| La comprensión es medible y razonable | % promedio de respuestas correctas | Dato descriptivo (no hay meta fija, es variable dependiente del estudio) |
| El Modo Rescate es útil en momentos de bloqueo | % de aperturas de Modo Rescate que terminan en una sesión iniciada | ≥ 40% |

Estas métricas son también las que alimentarán el análisis de la tesis, no solo el producto.

## 7. Alcance

### 7.1 Dentro de alcance (MVP)
Ver [`MVP.md`](./MVP.md) para el detalle. En resumen: onboarding, diagnóstico de procrastinación, alta de lectura (texto pegado como mínimo), sesión con temporizador "solo 5 minutos", pantalla de lectura simple, preguntas de comprensión, resultado, historial/progreso básico, Modo Rescate simple.

### 7.2 Fuera de alcance (post-MVP)
- OCR / foto de texto
- Subida de PDF con extracción automática de texto
- Generación automática de preguntas por IA
- Gamificación avanzada (insignias múltiples, sistema de puntos complejo)
- Funcionalidades sociales (compartir, comparar con otros)
- Sincronización multi-dispositivo / cuenta en la nube
- Notificaciones/recordatorios inteligentes
- Multi-idioma

Estas quedan documentadas para no perderlas, pero **no se implementan hasta validar el flujo principal**.

## 8. Principios de diseño del MVP

1. **Evitar sobreingeniería.** Preferir la solución más simple que permita validar el valor central.
2. **El núcleo es empezar + comprender.** Todo lo demás (gamificación, IA, backend complejo) es secundario.
3. **Cada pantalla del flujo principal debe poder recorrerse en una sesión de prueba de usuario sin fricción.**
4. **Los datos de sesión son de investigación, no solo de producto** — deben ser completos y exportables desde el día uno, aunque la UI que los muestra sea mínima.

## 9. Consideraciones de datos e investigación

- Cada sesión de lectura debe registrar: usuario (id anónimo o perfil), texto/lectura asociada, tiempo objetivo elegido, tiempo real leído, si se completó o se abandonó, resultados de comprensión (respuestas y % de acierto, por nivel: literal/inferencial/crítica), si se usó Modo Rescate y qué opción se eligió.
- **Decisión tomada:** almacenamiento local en el dispositivo (sin backend) para el MVP, con exportación de datos (JSON/CSV) para análisis. La capa de datos se construye con patrón repositorio para poder migrar a backend remoto más adelante sin rediseñar la app. Ver detalle en `MVP.md` §6.
- **Identificación de participantes:** cada usuario se identifica con un UUID anónimo autogenerado por la app (no con datos identificantes). El onboarding incluye una pantalla de consentimiento informado que el participante acepta explícitamente antes de crear su perfil; la aceptación queda registrada con timestamp. Esto permite que la app sea descargable libremente y que el estudio escale más allá del piloto inicial sin coordinación manual de códigos por participante.
- El diagnóstico inicial de procrastinación debe guardarse como línea base por usuario, para poder comparar comportamiento "antes" vs "durante el uso".

## 10. Restricciones y supuestos

- Plataforma: Android nativo (no se especifica iOS por ahora).
- Equipo: un ingeniero de software implementando con apoyo de Claude Code.
- Proyecto de alcance de tesis de maestría — priorizar tiempo de entrega y capacidad de generar datos analizables sobre robustez de producción.
- No hay presupuesto/infraestructura de backend confirmada — por defecto se asume local-first (ver §9), diseñado para poder escalar a backend después.
- Los textos de lectura pueden ser cortos/medianos (artículos, capítulos); no se optimiza para libros completos en el MVP.
- Piloto inicial estimado en ~10 participantes, con la app pensada para ser descargable libremente y el estudio poder crecer más allá de ese número sin cambios de arquitectura.
- El texto/formato exacto del consentimiento informado queda pendiente de validación con el comité de ética correspondiente (no es una decisión de producto).

## 11. Riesgos

| Riesgo | Impacto | Mitigación |
|---|---|---|
| Generar preguntas de comprensión manualmente no escala para pruebas con muchos usuarios/textos | Medio | MVP permite carga manual de preguntas junto con el texto (autor define las lecturas de prueba) |
| Sin backend, se pierden datos si el usuario cambia de dispositivo o desinstala | Alto para investigación | Exportación local + posible respaldo manual antes de fin de estudio |
| Alcance crece por "features interesantes" (gamificación, IA) | Alto | Este PRD fija el alcance MVP; cualquier extra pasa por backlog post-MVP |
| Tiempo limitado de tesis | Alto | Priorizar flujo principal end-to-end sobre pulido visual |

## 12. Siguientes documentos

1. `MVP.md` — definición funcional del MVP, flujos, modelo de datos, fases (este repo, `docs/MVP.md`).
2. User stories y criterios de aceptación por pantalla.
3. Arquitectura técnica de la app Android (capas, stack, almacenamiento).
4. Backlog priorizado por fases.

---

*Este documento es un borrador vivo. Se debe actualizar conforme se tomen decisiones (ver "Decisiones pendientes" en `MVP.md`).*
