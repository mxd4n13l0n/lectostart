# LectoStart

> "Empieza pequeño. Comprende más."

App Android (MVP) para ayudar a estudiantes universitarios a empezar lecturas académicas que posponen, y comprobar su comprensión al terminar. Es también el instrumento de intervención de un trabajo de investigación de maestría sobre procrastinación y comprensión lectora.

## Documentación del proyecto

Antes de tocar código, lee esto en orden:

1. [`docs/PRD.md`](docs/PRD.md) — problema, objetivo, alcance, vínculo con la investigación.
2. [`docs/MVP.md`](docs/MVP.md) — alcance funcional del MVP, decisiones tomadas, fases.
3. [`docs/USER_STORIES.md`](docs/USER_STORIES.md) — historias de usuario con criterios de aceptación.
4. [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — arquitectura técnica (MVVM, Room, Hilt, navegación).
5. [`docs/BACKLOG.md`](docs/BACKLOG.md) — backlog priorizado, ticket por ticket, en el orden en que se implementan.

## Estado actual

Fase 0 (fundaciones) completa: proyecto Android compilable, con estructura de paquetes por feature, Room y Hilt configurados, y un esqueleto de navegación recorrible de punta a punta con pantallas placeholder (ver `docs/BACKLOG.md` T-001 a T-006). Aún no hay lógica de negocio real: eso empieza en Fase 1.

## Stack

Kotlin + Jetpack Compose (Material 3) · Navigation 3 (`androidx.navigation3`) · Room · Hilt · Coroutines/`StateFlow` · KSP.

## Requisitos para desarrollar

- **Android Studio** (incluye el SDK y el emulador). Instalado en `/Applications/Android Studio.app`.
- **Android SDK command-line tools**, instaladas vía Homebrew:
  ```
  brew install --cask android-commandlinetools
  ```
  Se simlinkearon dentro del SDK gestionado por Android Studio en `~/Library/Android/sdk/cmdline-tools/latest`, porque el `avdmanager` clásico de esa cask tiene un bug creando AVDs cuando vive fuera de esa ruta convencional. El SDK trae además un CLI unificado más nuevo (`android`, en esa misma carpeta `bin/`) que reemplaza a `sdkmanager`/`avdmanager` para crear proyectos y AVDs — es el que se usó para generar el esqueleto inicial del proyecto y el emulador de pruebas.

El proyecto no depende de `JAVA_HOME`/`ANDROID_HOME` estando exportados globalmente en tu shell; Android Studio los resuelve solo. Si compilas por línea de comandos (`./gradlew`) sin abrir Android Studio, expórtalos primero:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
```

## Compilar y correr

```bash
./gradlew assembleDebug          # compila el APK debug
./gradlew testDebugUnitTest      # corre los tests unitarios (JVM)
```

Para correr en el emulador de pruebas (`medium_phone`, ya creado):

```bash
"$ANDROID_HOME/cmdline-tools/latest/bin/android" emulator start medium_phone
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.lectostart.app/.MainActivity
```

O simplemente abre el proyecto en Android Studio y usa el botón Run — Studio maneja el JDK y el emulador automáticamente.

## Estructura de paquetes

Un solo módulo (`:app`), organizado por feature — no por capa técnica. Ver el detalle y el razonamiento en [`docs/ARCHITECTURE.md` §3](docs/ARCHITECTURE.md#3-estructura-de-paquetes).

```
com.lectostart.app/
├── core/            # DI, navegación central, theme, base de datos Room
├── onboarding/       # bienvenida, consentimiento, crear perfil
├── procrastination/  # diagnóstico de procrastinación
├── reading/          # lecturas, duración de sesión, "solo 5 minutos", lectura
├── comprehension/     # preguntas de comprensión, resultado de sesión
├── progress/          # historial y racha
└── rescue/            # Modo Rescate
```
