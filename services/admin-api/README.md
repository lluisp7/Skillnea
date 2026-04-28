# Admin API

Gateway local muy ligero para el panel de administración.

## Rol actual

- exponer health/config para el panel
- servir como punto único de entrada local
- aislar el panel del detalle temporal de Apps Script

## Evolución esperada

Más adelante este servicio debería añadir:

- autenticación
- CRUD de tests y preguntas
- lectura de respuestas
- proxy o migración desde Apps Script a backend real
- auditoría y permisos
