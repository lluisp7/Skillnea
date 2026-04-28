import { useEffect, useState } from 'react'

type GatewayConfig = {
  status: string
  apiMode: string
  appsScriptConfigured: boolean
  appsScriptBaseUrl: string
  versions: {
    adminApi: string
    contracts: string
  }
  futureModules: string[]
}

const apiBaseUrl = import.meta.env.VITE_ADMIN_API_BASE_URL || 'http://localhost:8080'
const appVersion = import.meta.env.VITE_APP_VERSION || '0.1.0'

export default function App() {
  const [config, setConfig] = useState<GatewayConfig | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const loadConfig = async () => {
      try {
        const response = await fetch(`${apiBaseUrl}/config`)
        if (!response.ok) {
          throw new Error(`Gateway responded with ${response.status}`)
        }

        const payload = (await response.json()) as GatewayConfig
        setConfig(payload)
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Unknown error')
      }
    }

    loadConfig()
  }, [])

  return (
    <main className="shell">
      <section className="hero">
        <div className="eyebrow">SKILLNEA ADMIN</div>
        <h1>Panel base para tests, preguntas y respuestas.</h1>
        <p>
          Esta primera versión deja el frontend del panel desacoplado de la app
          Android y preparado para crecer contra un gateway local.
        </p>
        <div className="hero-metrics">
          <StatCard label="Version web" value={appVersion} />
          <StatCard
            label="Modo API"
            value={config?.apiMode ?? 'pending'}
          />
          <StatCard
            label="Apps Script"
            value={config?.appsScriptConfigured ? 'connected' : 'demo'}
          />
        </div>
      </section>

      <section className="panel-grid">
        <article className="panel-card">
          <h2>Gateway local</h2>
          {error && <p className="error">No se pudo leer `/config`: {error}</p>}
          {!error && !config && <p>Cargando configuración del gateway local...</p>}
          {config && (
            <>
              <p>
                `admin-api` expone una capa estable para el panel y evita que la
                web dependa directamente de Apps Script.
              </p>
              <ul>
                <li>Version admin-api: {config.versions.adminApi}</li>
                <li>Version contracts: {config.versions.contracts}</li>
                <li>
                  Apps Script URL:{' '}
                  {config.appsScriptBaseUrl || 'sin configurar'}
                </li>
              </ul>
            </>
          )}
        </article>

        <article className="panel-card">
          <h2>Módulos previstos</h2>
          <ul>
            <li>Catálogo de tests</li>
            <li>Editor de preguntas</li>
            <li>Dashboard de respuestas</li>
            <li>Sesiones y trazabilidad</li>
            <li>Integración con evaluator-agent</li>
          </ul>
        </article>

        <article className="panel-card accent">
          <h2>Ruta de crecimiento</h2>
          <p>
            Mientras la app móvil consume Apps Script de forma temporal, el panel
            ya queda orientado a una arquitectura más seria:
          </p>
          <ol>
            <li>admin-web</li>
            <li>admin-api</li>
            <li>fuente temporal o backend final</li>
          </ol>
        </article>
      </section>
    </main>
  )
}

type StatCardProps = {
  label: string
  value: string
}

function StatCard({ label, value }: StatCardProps) {
  return (
    <div className="stat-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  )
}
