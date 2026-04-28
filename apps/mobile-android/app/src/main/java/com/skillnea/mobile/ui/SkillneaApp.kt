package com.skillnea.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skillnea.mobile.core.model.SurveyOutcome
import com.skillnea.mobile.core.model.TestSummary
import com.skillnea.mobile.data.SurveyRepositoryFactory
import com.skillnea.mobile.feature.access.AccessScreen
import com.skillnea.mobile.feature.access.AccessViewModel
import com.skillnea.mobile.feature.dashboard.DashboardScreen
import com.skillnea.mobile.feature.dashboard.DashboardViewModel
import com.skillnea.mobile.feature.result.ResultScreen
import com.skillnea.mobile.feature.survey.SurveyScreen
import com.skillnea.mobile.feature.survey.SurveyViewModel

private enum class SkillneaRoute {
    ACCESS,
    CATALOG,
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
    var latestOutcome by remember { mutableStateOf<SurveyOutcome?>(null) }

    when (SkillneaRoute.valueOf(route)) {
        SkillneaRoute.ACCESS -> {
            val viewModel: AccessViewModel = viewModel(factory = AccessViewModel.factory())
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AccessScreen(
                uiState = uiState,
                isRemoteConfigured = repository.isRemoteConfigured,
                onEmailChanged = viewModel::onEmailChanged,
                onJoinStudy = {
                    val validEmail = viewModel.validateAndGetEmail()
                    if (validEmail != null) {
                        participantEmail = validEmail
                        route = SkillneaRoute.CATALOG.name
                    }
                },
            )
        }

        SkillneaRoute.CATALOG -> {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.factory(repository),
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            DashboardScreen(
                uiState = uiState,
                participantEmail = participantEmail,
                isRemoteConfigured = repository.isRemoteConfigured,
                onRefresh = viewModel::refresh,
                onStartTest = { test: TestSummary ->
                    selectedTestId = test.id
                    selectedTestTitle = test.title
                    route = SkillneaRoute.SURVEY.name
                },
                onBackToAccess = {
                    participantEmail = ""
                    route = SkillneaRoute.ACCESS.name
                },
            )
        }

        SkillneaRoute.SURVEY -> {
            if (selectedTestId.isBlank()) {
                LaunchedEffect(Unit) {
                    route = SkillneaRoute.CATALOG.name
                }
            } else {
                val viewModel: SurveyViewModel = viewModel(
                    key = "survey-$selectedTestId",
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
                    onSelectOption = viewModel::selectOption,
                    onPrevious = viewModel::previousQuestion,
                    onNext = viewModel::nextQuestion,
                    onSubmit = { viewModel.submit(participantEmail) },
                    onBack = { route = SkillneaRoute.CATALOG.name },
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
                    route = SkillneaRoute.CATALOG.name
                }
            } else {
                ResultScreen(
                    outcome = outcome,
                    onBackToCatalog = {
                        latestOutcome = null
                        route = SkillneaRoute.CATALOG.name
                    },
                    onExitSession = {
                        latestOutcome = null
                        participantEmail = ""
                        selectedTestId = ""
                        selectedTestTitle = ""
                        route = SkillneaRoute.ACCESS.name
                    },
                )
            }
        }
    }
}
