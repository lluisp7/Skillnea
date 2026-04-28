package com.skillnea.mobile.data

import com.skillnea.mobile.core.config.ApiConfig
import com.skillnea.mobile.core.model.SubmissionReceipt
import com.skillnea.mobile.core.model.SurveyQuestion
import com.skillnea.mobile.core.model.SurveySubmission
import com.skillnea.mobile.core.model.TestSummary
import com.skillnea.mobile.core.network.AppsScriptApiClient

interface SurveyRepository {
    val isRemoteConfigured: Boolean

    suspend fun getTests(): List<TestSummary>

    suspend fun getQuestions(testId: String): List<SurveyQuestion>

    suspend fun submitSurvey(submission: SurveySubmission): SubmissionReceipt
}

class SurveyRepositoryImpl(
    private val apiClient: AppsScriptApiClient,
) : SurveyRepository {
    override val isRemoteConfigured: Boolean
        get() = apiClient.isEnabled

    override suspend fun getTests(): List<TestSummary> {
        if (!apiClient.isEnabled) {
            return SampleData.tests
        }

        return runCatching { apiClient.fetchTests() }
            .getOrElse { SampleData.tests }
            .ifEmpty { SampleData.tests }
    }

    override suspend fun getQuestions(testId: String): List<SurveyQuestion> {
        if (!apiClient.isEnabled) {
            return SampleData.questionsForTest(testId)
        }

        return runCatching { apiClient.fetchQuestions(testId) }
            .getOrElse { SampleData.questionsForTest(testId) }
            .ifEmpty { SampleData.questionsForTest(testId) }
    }

    override suspend fun submitSurvey(submission: SurveySubmission): SubmissionReceipt {
        if (!apiClient.isEnabled) {
            return SampleData.demoSubmissionReceipt()
        }

        return runCatching { apiClient.submitSurvey(submission) }
            .getOrElse { SampleData.demoSubmissionReceipt() }
    }
}

object SurveyRepositoryFactory {
    fun create(): SurveyRepository = SurveyRepositoryImpl(
        apiClient = AppsScriptApiClient(ApiConfig.fromBuildConfig()),
    )
}
