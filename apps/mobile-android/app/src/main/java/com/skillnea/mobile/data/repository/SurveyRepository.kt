package com.skillnea.mobile.data.repository

import com.skillnea.mobile.config.ApiConfig
import com.skillnea.mobile.data.remote.AppsScriptApiClient
import com.skillnea.mobile.data.remote.NetworkModule
import com.skillnea.mobile.model.SubmissionReceipt
import com.skillnea.mobile.model.SurveyQuestion
import com.skillnea.mobile.model.SurveySubmission
import com.skillnea.mobile.model.TestSummary

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
        check(apiClient.isEnabled) {
            "Apps Script API is not configured."
        }
        return apiClient.fetchTests()
            .filter(TestSummary::active)
    }

    override suspend fun getQuestions(testId: String): List<SurveyQuestion> {
        check(apiClient.isEnabled) {
            "Apps Script API is not configured."
        }
        return apiClient.fetchQuestions(testId)
            .sortedBy(SurveyQuestion::order)
    }

    override suspend fun submitSurvey(submission: SurveySubmission): SubmissionReceipt {
        check(apiClient.isEnabled) {
            "Apps Script API is not configured."
        }
        return apiClient.submitSurvey(submission)
    }
}

object SurveyRepositoryFactory {
    fun create(): SurveyRepository = SurveyRepositoryImpl(
        apiClient = AppsScriptApiClient(
            config = ApiConfig.fromBuildConfig(),
            service = NetworkModule.createAppsScriptService(),
        ),
    )
}
