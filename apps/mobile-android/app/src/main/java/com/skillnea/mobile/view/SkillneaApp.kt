package com.skillnea.mobile.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skillnea.mobile.data.repository.SurveyRepositoryFactory
import com.skillnea.mobile.model.SurveyOutcome
import com.skillnea.mobile.view.access.AccessScreen
import com.skillnea.mobile.view.result.ResultScreen
import com.skillnea.mobile.view.survey.SurveyScreen
import com.skillnea.mobile.viewmodel.AccessViewModel
import com.skillnea.mobile.viewmodel.SurveyViewModel

private enum class SkillneaRoute {
    ACCESS,
    SURVEY,
    RESULT,
}

@Composable
fun SkillneaApp() {
    val repository = remember { SurveyRepositoryFactory.create() }
    var route by rememberSaveable { mutableStateOf(SkillneaRoute.ACCESS.name) }
    var participantEmail by rememberSaveable { mutableStateOf("") }
    var selectedTestId by rememberSaveable { mutableStateOf("") }
    var selectedTestTitle by rememberSaveable { mutableStateOf("") }
    var sessionNonce by rememberSaveable { mutableStateOf(0) }
    var latestOutcome by remember { mutableStateOf<SurveyOutcome?>(null) }

    when (SkillneaRoute.valueOf(route)) {
        SkillneaRoute.ACCESS -> {
            val viewModel: AccessViewModel = viewModel(
                key = "access-$sessionNonce",
                factory = AccessViewModel.factory(repository),
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.sessionReady) {
                val session = uiState.sessionReady ?: return@LaunchedEffect
                participantEmail = session.email
                selectedTestId = session.testId
                selectedTestTitle = session.testTitle
                viewModel.consumeSession()
                route = SkillneaRoute.SURVEY.name
            }

            AccessScreen(
                uiState = uiState,
                onEmailChanged = viewModel::onEmailChanged,
                onJoinStudy = viewModel::startSurvey,
                onRetry = viewModel::loadAssessment,
            )
        }

        SkillneaRoute.SURVEY -> {
            if (selectedTestId.isBlank()) {
                LaunchedEffect(Unit) {
                    route = SkillneaRoute.ACCESS.name
                }
            } else {
                val viewModel: SurveyViewModel = viewModel(
                    key = "survey-$selectedTestId-$sessionNonce",
                    factory = SurveyViewModel.factory(
                        repository = repository,
                        testId = selectedTestId,
                        testTitle = selectedTestTitle,
                    ),
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                SurveyScreen(
                    uiState = uiState,
                    participantEmail = participantEmail,
                    testTitle = selectedTestTitle,
                    onAnswerChanged = viewModel::updateAnswer,
                    onPrevious = viewModel::previousQuestion,
                    onNext = viewModel::nextQuestion,
                    onSubmit = { viewModel.submit(participantEmail) },
                    onBack = {
                        selectedTestId = ""
                        selectedTestTitle = ""
                        route = SkillneaRoute.ACCESS.name
                    },
                    onRetry = viewModel::loadQuestions,
                    onCompleted = { outcome ->
                        latestOutcome = outcome
                        viewModel.clearCompletedOutcome()
                        route = SkillneaRoute.RESULT.name
                    },
                )
            }
        }

        SkillneaRoute.RESULT -> {
            val outcome = latestOutcome
            if (outcome == null) {
                LaunchedEffect(Unit) {
                    route = SkillneaRoute.ACCESS.name
                }
            } else {
                ResultScreen(
                    outcome = outcome,
                    onBackToStart = {
                        latestOutcome = null
                        participantEmail = ""
                        selectedTestId = ""
                        selectedTestTitle = ""
                        sessionNonce += 1
                        route = SkillneaRoute.ACCESS.name
                    },
                )
            }
        }
    }
}
