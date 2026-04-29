# Admin API

Gateway local muy ligero para el panel de administración.

## Rol actual

- exponer health/config para el panel
- servir como punto único de entrada local
- aislar el panel del detalle temporal de Apps Script

## Configuración local

1. Copia [.env.example](./.env.example) a `.env`.
2. Rellena `APPS_SCRIPT_BASE_URL` y `APPS_SCRIPT_DEPLOYMENT_ID`.
3. `.env` está ignorado por Git.

## Ejecucion

```bash
npm run dev
```

## Evolución esperada

Más adelante este servicio debería añadir:

- autenticación
- CRUD de tests y preguntas
- lectura de respuestas
- proxy o migración desde Apps Script a backend real
- auditoría y permisos
