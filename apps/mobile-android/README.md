# Skillnea Android

Base Android Studio con:

- Kotlin
- Jetpack Compose
- arquitectura MVVM
- repositorio preparado para Apps Script
- modo demo local si la API temporal no está configurada

## Configuración rápida

1. Abre esta carpeta en Android Studio.
2. Revisa [gradle.properties](C:/Users/lui8p/Desktop/SKILLNEA/apps/mobile-android/gradle.properties).
3. Si ya tienes desplegado el Apps Script, define:

```properties
APPS_SCRIPT_BASE_URL=https://script.google.com/macros/s/TU_DEPLOYMENT/exec
```

4. Si no, la app arrancará con datos locales de muestra.
