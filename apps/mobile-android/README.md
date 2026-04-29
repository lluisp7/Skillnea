# Skillnea Android

Aplicacion Android de `Skillnea` en Kotlin + Jetpack Compose con estructura MVVM.

## Estructura actual

```text
app/src/main/java/com/skillnea/mobile
├── config
├── data
│   ├── remote
│   └── repository
├── model
├── view
│   ├── access
│   ├── result
│   ├── survey
│   └── theme
└── viewmodel
```

## Configuracion rapida

1. Abre esta carpeta en Android Studio.
2. Copia [secret.properties.example](./secret.properties.example) a `secret.properties`.
3. Define la URL del Apps Script:

```properties
APPS_SCRIPT_BASE_URL=https://script.google.com/macros/s/TU_DEPLOYMENT/exec
APPS_SCRIPT_DEPLOYMENT_ID=TU_DEPLOYMENT
```

4. `secret.properties` esta ignorado por Git.

## Build

```bash
./gradlew :app:assembleDebug
```
