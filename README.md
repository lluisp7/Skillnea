# Skillnea

Monorepo base para `Skillnea`, pensado para separar desde el inicio los servicios que van a crecer con el producto:

- `apps/mobile-android`: app Android Studio en Kotlin + MVVM.
- `apps/admin-web`: panel web de administración para localhost/Docker.
- `services/admin-api`: puerta de entrada local para el panel y futura capa backend.
- `services/apps-script-api`: API temporal basada en Google Sheets + Apps Script.
- `services/evaluator-agent`: espacio reservado para el futuro agente evaluador.
- `packages/contracts`: contrato compartido de datos y endpoints.
- `infra/docker`: compose local para levantar el panel en contenedores.
- `docs`: decisiones técnicas, arquitectura y versionado.

## Estado actual

Este primer entregable deja:

1. La app Android preparada para abrirse en Android Studio con una base MVVM.
2. Pantallas iniciales para acceso, catálogo de tests, cuestionario y resultado.
3. Integración lista para un `Apps Script Web App`, con modo demo si la URL aún no está configurada.
4. Un `admin-web` inicial y un `admin-api` local para evolucionar el panel sin acoplarlo a la app.
5. Un contrato OpenAPI inicial para alinear Android, web y servicios futuros.

## Estructura

```text
.
├── apps
│   ├── admin-web
│   └── mobile-android
├── docs
├── infra
│   └── docker
├── packages
│   └── contracts
└── services
    ├── admin-api
    ├── apps-script-api
    └── evaluator-agent
```

## Primeros pasos

### Android

1. Abre [apps/mobile-android](C:/Users/lui8p/Desktop/SKILLNEA/apps/mobile-android) en Android Studio.
2. Si vas a usar Apps Script ya desplegado, rellena `APPS_SCRIPT_BASE_URL` y opcionalmente `APPS_SCRIPT_DEPLOYMENT_ID` en [gradle.properties](C:/Users/lui8p/Desktop/SKILLNEA/apps/mobile-android/gradle.properties).
3. Sin esa configuración, la app arranca en modo demo con datos locales.

### Panel admin en localhost

1. Ajusta `APPS_SCRIPT_BASE_URL` como variable de entorno si quieres exponerla al gateway local.
2. Levanta Docker Compose:

```bash
docker compose -f infra/docker/compose.local.yml up --build
```

3. El panel web quedará en `http://localhost:5173`.
4. El `admin-api` local quedará en `http://localhost:8080`.

## Contrato de datos

El contrato inicial está en [packages/contracts/skillnea-survey.openapi.yaml](C:/Users/lui8p/Desktop/SKILLNEA/packages/contracts/skillnea-survey.openapi.yaml).

La idea de arquitectura es:

- Android consume temporalmente `Apps Script`.
- El panel web consume `admin-api`.
- `admin-api` acabará encapsulando autenticación, permisos, proxy a la fuente de preguntas y lógica operativa.
- El futuro `evaluator-agent` se conectará contra el mismo contrato y eventos de evaluaciones.

## Versionado

La estrategia está documentada en [docs/versioning.md](C:/Users/lui8p/Desktop/SKILLNEA/docs/versioning.md). Resumen:

- Android: `versionName` semántico + `versionCode` incremental.
- Web y servicios Node: `package.json.version`.
- Tags por servicio: `mobile/vX.Y.Z`, `admin-web/vX.Y.Z`, `admin-api/vX.Y.Z`, `contracts/vX.Y.Z`.
