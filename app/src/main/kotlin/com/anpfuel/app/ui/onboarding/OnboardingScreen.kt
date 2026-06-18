package com.anpfuel.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anpfuel.app.R
import com.anpfuel.app.mapper.AppErrorMapper
import com.anpfuel.app.ui.accessibility.headingSemantics
import com.anpfuel.app.ui.components.AnpAttributionFooter
import com.anpfuel.app.ui.components.ErrorState
import com.anpfuel.app.ui.components.LoadingState
import com.anpfuel.app.ui.theme.AnpFuelTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLocation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.navigation.collect { destination ->
            when (destination) {
                OnboardingNavigation.ToHome -> onNavigateToHome()
                OnboardingNavigation.ToLocation -> onNavigateToLocation()
            }
        }
    }

    OnboardingContent(
        uiState = uiState,
        onNextPage = viewModel::onNextPage,
        onPreviousPage = viewModel::onPreviousPage,
        onPageSelected = viewModel::onPageSelected,
        onStartSync = viewModel::startSync,
        onSkipSync = viewModel::skipSync,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onStartSync: () -> Unit,
    onSkipSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.pageIndex,
        pageCount = { uiState.pageCount },
    )

    LaunchedEffect(uiState.pageIndex) {
        if (pagerState.currentPage != uiState.pageIndex) {
            pagerState.animateScrollToPage(uiState.pageIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect(onPageSelected)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.onboarding_title_welcome)) },
            )
        },
        bottomBar = { AnpAttributionFooter() },
    ) { innerPadding ->
        when {
            uiState.isSyncing -> {
                LoadingState(
                    message = stringResource(R.string.sync_progress_discovering),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) { page ->
                        OnboardingPageContent(pageIndex = page)
                    }

                    if (uiState.error != null) {
                        ErrorState(
                            message = stringResource(AppErrorMapper.toStringRes(uiState.error)),
                            onRetry = onStartSync,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    OnboardingActions(
                        uiState = uiState,
                        onNextPage = onNextPage,
                        onPreviousPage = onPreviousPage,
                        onStartSync = onStartSync,
                        onSkipSync = onSkipSync,
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(pageIndex: Int) {
    val (titleRes, bodyRes) = when (pageIndex) {
        0 -> R.string.onboarding_title_welcome to R.string.onboarding_body_anp_source
        1 -> R.string.onboarding_title_offline to R.string.onboarding_body_offline
        else -> R.string.onboarding_title_ready to R.string.onboarding_body_ready
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.headingSemantics(),
        )
        Text(
            text = stringResource(bodyRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun OnboardingActions(
    uiState: OnboardingUiState,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onStartSync: () -> Unit,
    onSkipSync: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (uiState.isOnLastPage) {
            Button(
                onClick = onStartSync,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.onboarding_action_sync_now))
            }
            TextButton(
                onClick = onSkipSync,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.onboarding_action_skip))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (uiState.pageIndex > 0) {
                    TextButton(onClick = onPreviousPage) {
                        Text(text = stringResource(R.string.action_back))
                    }
                }
                Button(
                    onClick = onNextPage,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.action_next))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPreview() {
    AnpFuelTheme {
        OnboardingContent(
            uiState = OnboardingUiState(pageIndex = 2),
            onNextPage = {},
            onPreviousPage = {},
            onPageSelected = {},
            onStartSync = {},
            onSkipSync = {},
        )
    }
}
