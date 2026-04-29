# Apps Script API temporal

Esta carpeta contiene una implementación temporal para usar Google Sheets como fuente de preguntas y como registro de respuestas.

## Estructura actual esperada

El script ya está adaptado a tu Google Sheet actual, con una hoja que contiene estas columnas:

- `id`
- `disposition`
- `level`
- `rubric`
- `question`

No hace falta una pestaña `tests` ni una pestaña `questions` separadas.

La pestaña `responses` se crea sola cuando llegue la primera entrega de respuestas.

## Despliegue

1. Abre [SkillneaSurveyApi.gs](./SkillneaSurveyApi.gs) en un proyecto de Apps Script.
2. Pega el contenido de [SkillneaSurveyApi.gs](./SkillneaSurveyApi.gs).
3. Despliega como `Web app`.
4. Permisos recomendados:
   - ejecutar como: tú
   - acceso: cualquier usuario con el enlace, mientras estemos en fase temporal

## Endpoints soportados

- `GET ?action=tests`
- `GET ?action=dispositions`
- `GET ?action=questions&testId=critical-thinking-rubric`
- `GET ?action=questions&disposition=Truth-Seeking`
- `GET ?action=schema`
- `POST ?action=submit`

## Modelo de respuesta

Esta versión del Apps Script ya no está pensada para preguntas cerradas con opciones, sino para preguntas abiertas con rúbrica.

Cada pregunta se expone con:

- `prompt`
- `dimension`
- `disposition`
- `rubricLevel`
- `rubric`
- `responseType: "text"`

Si la app móvil va a consumir esta hoja tal cual, habrá que adaptar también la UI móvil para respuestas abiertas y evaluación posterior por rúbrica.

## Nota importante para el panel admin

El panel web no debería apoyarse para siempre en Apps Script directo. Para localhost y futura publicación, el camino correcto es:

`admin-web -> admin-api -> Apps Script temporal / backend real`

Así evitamos acoplar el panel a CORS, permisos y formatos transitorios.
