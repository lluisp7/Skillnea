# Arquitectura base

## Principios

- Monorepo por fronteras de negocio, no por tecnología aislada.
- Contrato compartido en `packages/contracts`.
- Android desacoplado del panel admin.
- Fuente temporal de preguntas en Google Sheets + Apps Script.
- Evolución prevista hacia `admin-api` y un `evaluator-agent`.

## Mapa de servicios

### `apps/mobile-android`

Responsable de:

- acceso del participante
- consulta de tests
- ejecución del cuestionario
- envío de respuestas

No debe contener:

- reglas de administración
- lógica de permisos internos
- lógica propia del futuro agente evaluador

### `apps/admin-web`

Responsable de:

- gestión visual de tests y preguntas
- visualización de respuestas y sesiones
- administración operativa

Se conecta preferentemente a `services/admin-api`, no directamente a Apps Script.

### `services/admin-api`

Responsable de:

- exponer endpoints estables al panel
- encapsular la fuente temporal de datos
- resolver CORS, autenticación, auditoría y permisos
- ser el punto natural de integración con hosting futuro

### `services/apps-script-api`

Responsable de:

- proveer una API temporal rápida
- leer tests y preguntas desde Google Sheets
- registrar respuestas sin montar backend completo

Debe tratarse como una capa provisional.

### `services/evaluator-agent`

Responsable futuro de:

- análisis de resultados
- interpretación de patrones
- generación de resúmenes o recomendaciones

## MVVM en Android

La app móvil queda separada en:

- `core`: configuración y modelos
- `data`: repositorios y datos demo/remotos
- `feature/*`: pantallas y viewmodels
- `ui`: tema y componentes reutilizables

## Flujo actual

1. El participante entra con email.
2. La app carga los tests disponibles.
3. El participante realiza el cuestionario.
4. La app envía respuestas a Apps Script o usa modo demo si no hay URL configurada.
5. El panel admin podrá leer vía `admin-api` las mismas estructuras de datos.
