import { useEffect, useMemo, useState } from 'react'

type GatewayConfig = {
  status: string
  apiMode: string
  appsScriptConfigured: boolean
  appsScriptBaseUrl: string
}

type TestSummary = {
  id: string
  title: string
  description: string
  estimatedMinutes: number
  category: string
  questionCount: number
  active: boolean
  dispositionCount?: number
  responseType?: string
}

type SurveyQuestion = {
  id: string
  testId: string
  order: number
  prompt: string
  helper: string
  dimension: string
  disposition: string
  responseType: string
  rubricLevel: number
  rubric: string
}

type ApiError = {
  status: string
  message: string
}

const apiBaseUrl = (import.meta.env.VITE_ADMIN_API_BASE_URL || '/api').replace(/\/$/, '')

export default function App() {
  const [config, setConfig] = useState<GatewayConfig | null>(null)
  const [configError, setConfigError] = useState<string | null>(null)
  const [tests, setTests] = useState<TestSummary[]>([])
  const [testsError, setTestsError] = useState<ApiError | null>(null)
  const [questions, setQuestions] = useState<SurveyQuestion[]>([])
  const [questionsError, setQuestionsError] = useState<ApiError | null>(null)
  const [selectedTestId, setSelectedTestId] = useState<string>('')
  const [loadingConfig, setLoadingConfig] = useState(true)
  const [loadingTests, setLoadingTests] = useState(true)
  const [loadingQuestions, setLoadingQuestions] = useState(false)

  useEffect(() => {
    void loadConfig()
    void loadTests()
  }, [])

  useEffect(() => {
    if (!selectedTestId) {
      return
    }
    void loadQuestions(selectedTestId)
  }, [selectedTestId])

  const selectedTest = useMemo(
    () => tests.find((test) => test.id === selectedTestId) ?? tests[0] ?? null,
    [tests, selectedTestId],
  )

  const groupedQuestions = useMemo(() => {
    const grouped = new Map<string, SurveyQuestion[]>()
    questions.forEach((question) => {
      const key = question.disposition || question.dimension || 'General'
      const bucket = grouped.get(key) ?? []
      bucket.push(question)
      grouped.set(key, bucket)
    })
    return Array.from(grouped.entries())
  }, [questions])

  const apiStatus = useMemo(() => {
    if (loadingConfig || loadingTests) {
      return 'checking'
    }
    if (config?.appsScriptConfigured && !testsError) {
      return 'on'
    }
    return 'off'
  }, [config?.appsScriptConfigured, loadingConfig, loadingTests, testsError])

  const overviewTiles = useMemo(
    () => [
      {
        title: 'Tests',
        value: String(tests.length),
        hint: selectedTest?.title ?? (loadingTests ? 'Cargando' : 'Sin test'),
      },
      {
        title: 'Questions',
        value: loadingQuestions ? '...' : String(questions.length),
        hint: selectedTest ? `${selectedTest.questionCount} esperadas` : 'Sin selección',
      },
      {
        title: 'Blocks',
        value: String(groupedQuestions.length),
        hint: groupedQuestions[0]?.[0] ?? 'Sin grupos',
      },
    ],
    [groupedQuestions, loadingQuestions, loadingTests, questions.length, selectedTest, tests.length],
  )

  async function loadConfig() {
    setLoadingConfig(true)
    setConfigError(null)
    try {
      const response = await fetch(`${apiBaseUrl}/config`)
      if (!response.ok) {
        throw new Error(`Gateway responded with ${response.status}`)
      }

      const payload = (await response.json()) as GatewayConfig
      setConfig(payload)
    } catch (error) {
      setConfigError(error instanceof Error ? error.message : 'Error al leer la API')
    } finally {
      setLoadingConfig(false)
    }
  }

  async function loadTests() {
    setLoadingTests(true)
    setTestsError(null)
    try {
      const response = await fetch(`${apiBaseUrl}/tests`)
      const payload = await response.json()
      if (!response.ok || payload.status !== 'ok') {
        setTestsError(payload as ApiError)
        setTests([])
        setSelectedTestId('')
        return
      }

      const nextTests = ((payload.data as TestSummary[]) ?? []).map(normalizeTest)
      setTests(nextTests)
      setSelectedTestId((currentSelected) => currentSelected || nextTests[0]?.id || '')
    } catch (error) {
      setTestsError({
        status: 'error',
        message: error instanceof Error ? error.message : 'Error al cargar los tests',
      })
      setTests([])
      setSelectedTestId('')
    } finally {
      setLoadingTests(false)
    }
  }

  async function loadQuestions(testId: string) {
    setLoadingQuestions(true)
    setQuestionsError(null)
    try {
      const response = await fetch(`${apiBaseUrl}/questions?testId=${encodeURIComponent(testId)}`)
      const payload = await response.json()
      if (!response.ok || payload.status !== 'ok') {
        setQuestionsError(payload as ApiError)
        setQuestions([])
        return
      }

      setQuestions(((payload.data as SurveyQuestion[]) ?? []).map(normalizeQuestion))
    } catch (error) {
      setQuestionsError({
        status: 'error',
        message: error instanceof Error ? error.message : 'Error al cargar las preguntas',
      })
      setQuestions([])
    } finally {
      setLoadingQuestions(false)
    }
  }

  return (
    <main className="admin-shell">
      <section className="admin-frame">
        <header className="admin-header">
          <div className="brand">skillnea</div>
          <p>admin panel</p>
        </header>

        <section className="status-strip">
          <div className="status-cell status-cell--compact">
            <span className="status-label">api</span>
            <div className="status-switch">
              <span className={`status-dot ${apiStatus === 'off' ? 'status-dot--off' : ''}`} />
              <strong>{apiStatus}</strong>
            </div>
          </div>

          <div className="status-cell">
            <span className="status-label">test</span>
            <strong className="status-title">{selectedTest?.title ?? 'Sin test cargado'}</strong>
            {selectedTest?.description && <p className="status-copy">{selectedTest.description}</p>}
          </div>

          <div className="status-cell status-cell--compact">
            <span className="status-label">questions</span>
            <strong className="status-value">{loadingQuestions ? '...' : questions.length}</strong>
          </div>
        </section>

        <section className="overview-grid">
          {overviewTiles.map((tile) => (
            <OverviewTile key={tile.title} title={tile.title} value={tile.value} hint={tile.hint} />
          ))}
        </section>

        <section className="notice-stack">
          {configError && <NoticeBox title="API" message={configError} />}
          {testsError && <NoticeBox title="Tests" message={testsError.message} />}
          {questionsError && <NoticeBox title="Questions" message={questionsError.message} />}
        </section>

        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <h2>Tests</h2>
              <button className="action-button" onClick={() => void loadTests()}>
                Actualizar
              </button>
            </div>

            {loadingTests && <p className="muted">Cargando tests...</p>}
            {!loadingTests && !testsError && tests.length === 0 && (
              <p className="muted">No hay tests visibles.</p>
            )}

            <div className="test-list">
              {tests.map((test) => (
                <button
                  key={test.id}
                  className={`test-item ${selectedTestId === test.id ? 'test-item--active' : ''}`}
                  onClick={() => setSelectedTestId(test.id)}
                >
                  <span className="test-kicker">{test.category}</span>
                  <strong>{test.title}</strong>
                  <span>{test.description}</span>
                  <small>
                    {test.questionCount} preguntas · {test.estimatedMinutes} min
                  </small>
                </button>
              ))}
            </div>
          </article>

          <article className="panel panel--questions">
            <div className="panel-head">
              <h2>Questions</h2>
              {selectedTestId && (
                <button className="action-button" onClick={() => void loadQuestions(selectedTestId)}>
                  Recargar
                </button>
              )}
            </div>

            {!selectedTestId && <p className="muted">Selecciona un test.</p>}
            {loadingQuestions && <p className="muted">Cargando preguntas...</p>}
            {!loadingQuestions && !questionsError && selectedTestId && groupedQuestions.length === 0 && (
              <p className="muted">Este test no ha devuelto preguntas.</p>
            )}

            <div className="question-sections">
              {groupedQuestions.map(([group, items]) => (
                <section key={group} className="question-section">
                  <div className="question-section__head">
                    <h3>{group}</h3>
                    <span>{items.length}</span>
                  </div>
                  <div className="question-lines">
                    {items.map((question) => (
                      <article key={question.id} className="question-line">
                        <div className="question-meta">
                          <span>#{question.order}</span>
                          <span>Level {question.rubricLevel}</span>
                        </div>
                        <p>{question.prompt}</p>
                        {question.rubric && <small>{question.rubric}</small>}
                      </article>
                    ))}
                  </div>
                </section>
              ))}
            </div>
          </article>
        </section>
      </section>
    </main>
  )
}

type OverviewTileProps = {
  title: string
  value: string
  hint: string
}

function OverviewTile({ title, value, hint }: OverviewTileProps) {
  return (
    <article className="overview-tile">
      <span className="overview-tile__title">{title}</span>
      <strong>{value}</strong>
      <p>{hint}</p>
    </article>
  )
}

function NoticeBox({ title, message }: { title: string; message: string }) {
  return (
    <div className="notice-box">
      <strong>{title}</strong>
      <p>{message}</p>
    </div>
  )
}

function normalizeTest(test: TestSummary): TestSummary {
  return {
    ...test,
    title: normalizeRemoteText(test.title),
    description: normalizeRemoteText(test.description),
    category: normalizeRemoteText(test.category),
  }
}

function normalizeQuestion(question: SurveyQuestion): SurveyQuestion {
  return {
    ...question,
    prompt: normalizeRemoteText(question.prompt),
    helper: normalizeRemoteText(question.helper),
    dimension: normalizeRemoteText(question.dimension),
    disposition: normalizeRemoteText(question.disposition),
    rubric: normalizeRemoteText(question.rubric),
  }
}

function normalizeRemoteText(value: string): string {
  const source = value.trim()
  if (!source || (!source.includes('Ã') && !source.includes('Â'))) {
    return source
  }

  try {
    const bytes = Uint8Array.from(source, (character) => character.charCodeAt(0) & 0xff)
    const normalized = new TextDecoder('utf-8').decode(bytes)
    return normalized.includes('�') ? source : normalized
  } catch {
    return source
  }
}
