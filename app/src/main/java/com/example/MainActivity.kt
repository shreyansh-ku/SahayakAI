package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.OrthoScreenViewModel
import com.example.ui.Screen
import com.example.ui.screens.*
import com.example.ui.screens.clinix.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: OrthoScreenViewModel = viewModel()
                OrthoScreenApp(viewModel)
            }
        }
    }
}

@Composable
fun OrthoScreenApp(viewModel: OrthoScreenViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    BackHandler(enabled = currentScreen !is Screen.Dashboard && currentScreen !is Screen.Login) {
        when (val screen = currentScreen) {
            is Screen.Registration -> viewModel.navigateTo(Screen.Dashboard)
            is Screen.Questionnaire -> viewModel.navigateTo(Screen.Registration)
            is Screen.MovementAssessment -> viewModel.navigateTo(Screen.Questionnaire)
            is Screen.Result -> viewModel.navigateTo(Screen.Dashboard)
            is Screen.SyncCenter -> viewModel.navigateTo(Screen.Dashboard)
            is Screen.PatientDetail -> viewModel.navigateTo(Screen.Dashboard)
            // ClinixAI Module Back Navigation
            is Screen.ClinixOverview -> viewModel.navigateTo(Screen.Dashboard)
            is Screen.ClinixCases -> viewModel.navigateTo(Screen.ClinixOverview)
            is Screen.ClinixCaseDetail -> viewModel.navigateTo(Screen.ClinixCases)
            is Screen.ClinixSimilarCases -> viewModel.navigateTo(Screen.ClinixCaseDetail(screen.caseId))
            is Screen.ClinixCohorts -> viewModel.navigateTo(Screen.ClinixOverview)
            is Screen.ClinixCohortDetail -> viewModel.navigateTo(Screen.ClinixCohorts)
            is Screen.ClinixOutliers -> viewModel.navigateTo(Screen.ClinixOverview)
            is Screen.ClinixProfile -> viewModel.navigateTo(Screen.ClinixOverview)
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is Screen.Login -> LoginScreen(viewModel = viewModel)
            is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
            is Screen.Registration -> PatientRegistrationScreen(viewModel = viewModel)
            is Screen.Questionnaire -> QuestionnaireScreen(viewModel = viewModel)
            is Screen.MovementAssessment -> MovementAssessmentScreen(viewModel = viewModel)
            is Screen.Result -> ResultScreen(viewModel = viewModel)
            is Screen.SyncCenter -> SyncCenterScreen(viewModel = viewModel)
            is Screen.PatientDetail -> PatientDetailScreen(viewModel = viewModel, screening = screen.screening)
            // ClinixAI Module Screens
            is Screen.ClinixOverview -> ClinixOverviewScreen(viewModel = viewModel)
            is Screen.ClinixCases -> ClinixCaseQueueScreen(viewModel = viewModel)
            is Screen.ClinixCaseDetail -> ClinixCaseDetailScreen(viewModel = viewModel, caseId = screen.caseId)
            is Screen.ClinixSimilarCases -> ClinixSimilarCasesScreen(viewModel = viewModel, caseId = screen.caseId, initialTopK = screen.topK)
            is Screen.ClinixCohorts -> ClinixCohortsScreen(viewModel = viewModel)
            is Screen.ClinixCohortDetail -> ClinixCohortDetailScreen(viewModel = viewModel, cohortType = screen.cohortType)
            is Screen.ClinixOutliers -> ClinixOutliersScreen(viewModel = viewModel)
            is Screen.ClinixProfile -> ClinixProfileScreen(viewModel = viewModel)
        }
    }
}
