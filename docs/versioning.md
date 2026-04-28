# Versionado

## Objetivo

Mantener ciclos de release independientes para app, panel y servicios sin romper el monorepo.

## Convención recomendada

- Android: `major.minor.patch` en `versionName` y entero incremental en `versionCode`.
- Web y servicios Node: `package.json.version` siguiendo SemVer.
- Contratos: `info.version` en OpenAPI.
- Tags Git por servicio:
  - `mobile/v0.1.0`
  - `admin-web/v0.1.0`
  - `admin-api/v0.1.0`
  - `contracts/v0.1.0`

## Ramas

- `main`: estable.
- `feature/*`: trabajo funcional corto.
- `release/*`: preparación de entrega.
- `hotfix/*`: correcciones urgentes.

## Reglas prácticas

### Android

- `patch`: fixes visuales o de lógica sin cambiar experiencia principal.
- `minor`: nuevas pantallas, tests o integraciones compatibles.
- `major`: cambios de navegación, contrato o arquitectura con impacto.
- `versionCode`: subir en cada publicación generada.

### Web/Admin

- `patch`: correcciones de interfaz o bugs.
- `minor`: nuevos módulos del panel o nuevos filtros/vistas.
- `major`: cambios incompatibles de rutas, auth o contrato.

### Contratos

- `patch`: descripciones, ejemplos o campos opcionales compatibles.
- `minor`: nuevos endpoints o campos nuevos no rompientes.
- `major`: campos renombrados, cambios de shape o eliminación de compatibilidad.

## Commits

Convención sugerida:

- `feat(android): add survey result screen`
- `feat(admin-web): scaffold response dashboard`
- `fix(admin-api): expose apps script config`
- `docs(contracts): update submission payload example`

## Relación entre servicios

Un release de `apps/mobile-android` no obliga a versionar `apps/admin-web`, salvo si cambió el contrato compartido. Si cambia el contrato, etiqueta también `contracts/vX.Y.Z`.
