package com.skillnea.mobile.data

import com.skillnea.mobile.core.model.SubmissionReceipt
import com.skillnea.mobile.core.model.SurveyOption
import com.skillnea.mobile.core.model.SurveyQuestion
import com.skillnea.mobile.core.model.TestSummary

object SampleData {
    private fun standardScaleOptions(): List<SurveyOption> = listOf(
        SurveyOption(id = "never", label = "Nunca", value = 1),
        SurveyOption(id = "sometimes", label = "A veces", value = 2),
        SurveyOption(id = "often", label = "A menudo", value = 3),
        SurveyOption(id = "always", label = "Siempre", value = 4),
    )

    val tests: List<TestSummary> = listOf(
        TestSummary(
            id = "rrhh-001",
            title = "Workstyle Pulse",
            description = "Un test breve para medir organización, adaptabilidad y consistencia de trabajo.",
            estimatedMinutes = 12,
            category = "Behavior",
            questionCount = 4,
            active = true,
        ),
        TestSummary(
            id = "rrhh-002",
            title = "Team Signals",
            description = "Señales rápidas de colaboración, empatía y ajuste al ritmo del equipo.",
            estimatedMinutes = 10,
            category = "HR",
            questionCount = 4,
            active = true,
        ),
    )

    private val questionsByTest: Map<String, List<SurveyQuestion>> = mapOf(
        "rrhh-001" to listOf(
            SurveyQuestion(
                id = "q-001",
                testId = "rrhh-001",
                order = 1,
                prompt = "Prefiero comenzar mi jornada con una lista de prioridades clara.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Organization",
                options = standardScaleOptions(),
            ),
            SurveyQuestion(
                id = "q-002",
                testId = "rrhh-001",
                order = 2,
                prompt = "Me siento cómodo ajustando decisiones cuando cambia el contexto.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Adaptability",
                options = standardScaleOptions(),
            ),
            SurveyQuestion(
                id = "q-003",
                testId = "rrhh-001",
                order = 3,
                prompt = "Busco retroalimentación antes de cerrar un entregable importante.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Feedback",
                options = standardScaleOptions(),
            ),
            SurveyQuestion(
                id = "q-004",
                testId = "rrhh-001",
                order = 4,
                prompt = "Me resulta natural mantener la calma en situaciones ambiguas.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Resilience",
                options = standardScaleOptions(),
            ),
        ),
        "rrhh-002" to listOf(
            SurveyQuestion(
                id = "q-101",
                testId = "rrhh-002",
                order = 1,
                prompt = "Comparto contexto suficiente cuando delego una tarea.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Communication",
                options = standardScaleOptions(),
            ),
            SurveyQuestion(
                id = "q-102",
                testId = "rrhh-002",
                order = 2,
                prompt = "Identifico rápido cuándo un compañero necesita apoyo.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Empathy",
                options = standardScaleOptions(),
            ),
            SurveyQuestion(
                id = "q-103",
                testId = "rrhh-002",
                order = 3,
                prompt = "Prefiero resolver tensiones antes de que crezcan.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Conflict Management",
                options = standardScaleOptions(),
            ),
            SurveyQuestion(
                id = "q-104",
                testId = "rrhh-002",
                order = 4,
                prompt = "Adapto mi forma de colaborar según el ritmo del equipo.",
                helper = "Selecciona la opción que mejor te represente.",
                dimension = "Teamwork",
                options = standardScaleOptions(),
            ),
        ),
    )

    fun questionsForTest(testId: String): List<SurveyQuestion> = questionsByTest[testId].orEmpty()

    fun demoSubmissionReceipt(): SubmissionReceipt = SubmissionReceipt(
        status = "demo",
        message = "Survey stored locally in demo mode",
        submissionId = "demo-local",
    )
}
