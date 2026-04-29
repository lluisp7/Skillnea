package com.skillnea.mobile.data.remote

import com.skillnea.mobile.data.remote.dto.ApiEnvelopeDto
import com.skillnea.mobile.data.remote.dto.SubmissionReceiptDto
import com.skillnea.mobile.data.remote.dto.SurveyQuestionDto
import com.skillnea.mobile.data.remote.dto.SurveySubmissionRequestDto
import com.skillnea.mobile.data.remote.dto.TestSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AppsScriptApiService {
    @GET
    suspend fun getTests(
        @Url url: String,
    ): ApiEnvelopeDto<List<TestSummaryDto>>

    @GET
    suspend fun getQuestions(
        @Url url: String,
    ): ApiEnvelopeDto<List<SurveyQuestionDto>>

    @POST
    suspend fun submitSurvey(
        @Url url: String,
        @Body body: SurveySubmissionRequestDto,
    ): SubmissionReceiptDto
}
