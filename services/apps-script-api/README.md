# Apps Script API temporal

Esta carpeta contiene una implementación temporal para usar Google Sheets como fuente de preguntas y como registro de respuestas.

## Hojas necesarias

Crea una hoja de cálculo con estas pestañas:

- `tests`
- `questions`
- `responses`

Puedes partir de las plantillas CSV en [templates](C:/Users/lui8p/Desktop/SKILLNEA/services/apps-script-api/templates).

## Despliegue

1. Abre [SkillneaSurveyApi.gs](C:/Users/lui8p/Desktop/SKILLNEA/services/apps-script-api/SkillneaSurveyApi.gs) en un proyecto de Apps Script.
2. Vincula el script a tu Google Sheet.
3. Despliega como `Web app`.
4. Permisos recomendados:
   - ejecutar como: tú
   - acceso: cualquier usuario con el enlace, mientras estemos en fase temporal

## Endpoints soportados

- `GET ?action=tests`
- `GET ?action=questions&testId=...`
- `POST ?action=submit`

## Nota importante para el panel admin

El panel web no debería apoyarse para siempre en Apps Script directo. Para localhost y futura publicación, el camino correcto es:

`admin-web -> admin-api -> Apps Script temporal / backend real`

Así evitamos acoplar el panel a CORS, permisos y formatos transitorios.
