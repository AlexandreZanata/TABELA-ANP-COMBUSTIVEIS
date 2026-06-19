package com.anpfuel.app.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anpfuel.app.R
import com.anpfuel.app.mapper.AppErrorMapper
import com.anpfuel.app.mapper.SurveyWeekFormatter
import com.anpfuel.app.ui.accessibility.headingSemantics
import com.anpfuel.app.ui.components.AnpAttributionFooter
import com.anpfuel.app.ui.components.ErrorState
import com.anpfuel.app.ui.components.LoadingState
import com.anpfuel.app.ui.theme.AnpFuelTheme
import com.anpfuel.domain.model.SurveyWeekCatalogEntry
import com.anpfuel.domain.valueobject.SurveyWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
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
        onProceedToWeekPicker = viewModel::proceedToWeekPicker,
        onUseLatestWeekAndSync = viewModel::useLatestWeekAndSync,
        onSelectWeek = viewModel::selectWeekAndSync,
        onRetryCatalog = viewModel::retryCatalogDiscovery,
        onRetrySync = viewModel::retrySync,
        onSkipSync = viewModel::skipSync,
        onBackToIntro = viewModel::backToIntro,
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
    onProceedToWeekPicker: () -> Unit,
    onUseLatestWeekAndSync: () -> Unit,
    onSelectWeek: (SurveyWeekCatalogEntry) -> Unit,
    onRetryCatalog: () -> Unit,
    onRetrySync: () -> Unit,
    onSkipSync: () -> Unit,
    onBackToIntro: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleRes = when (uiState.step) {
        OnboardingStep.INTRO -> R.string.onboarding_title_welcome
        OnboardingStep.WEEK_PICKER, OnboardingStep.SYNCING -> R.string.week_picker_title
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(titleRes)) },
                navigationIcon = {
                    if (uiState.step == OnboardingStep.WEEK_PICKER && !uiState.isLoadingCatalog) {
                        TextButton(onClick = onBackToIntro) {
                            Text(text = stringResource(R.string.action_back))
                        }
                    }
                },
            )
        },
        bottomBar = { AnpAttributionFooter() },
    ) { innerPadding ->
        when (uiState.step) {
            OnboardingStep.SYNCING -> {
                LoadingState(
                    message = stringResource(R.string.sync_progress_discovering),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            OnboardingStep.WEEK_PICKER -> {
                OnboardingWeekPickerContent(
                    uiState = uiState,
                    onUseLatestWeekAndSync = onUseLatestWeekAndSync,
                    onSelectWeek = onSelectWeek,
                    onRetryCatalog = onRetryCatalog,
                    onRetrySync = onRetrySync,
                    onSkipSync = onSkipSync,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            OnboardingStep.INTRO -> {
                OnboardingIntroContent(
                    uiState = uiState,
                    onNextPage = onNextPage,
                    onPreviousPage = onPreviousPage,
                    onPageSelected = onPageSelected,
                    onProceedToWeekPicker = onProceedToWeekPicker,
                    onSkipSync = onSkipSync,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun OnboardingIntroContent(
    uiState: OnboardingUiState,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onProceedToWeekPicker: () -> Unit,
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

    Column(
        modifier = modifier
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

        OnboardingIntroActions(
            uiState = uiState,
            onNextPage = onNextPage,
            onPreviousPage = onPreviousPage,
            onProceedToWeekPicker = onProceedToWeekPicker,
            onSkipSync = onSkipSync,
        )
    }
}

@Composable
private fun OnboardingWeekPickerContent(
    uiState: OnboardingUiState,
    onUseLatestWeekAndSync: () -> Unit,
    onSelectWeek: (SurveyWeekCatalogEntry) -> Unit,
    onRetryCatalog: () -> Unit,
    onRetrySync: () -> Unit,
    onSkipSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when {
            uiState.isLoadingCatalog -> {
                LoadingState(
                    message = stringResource(R.string.sync_progress_discovering),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.catalogError != null -> {
                ErrorState(
                    message = stringResource(AppErrorMapper.toStringRes(uiState.catalogError)),
                    onRetry = onRetryCatalog,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            else -> {
                if (uiState.catalog.isNotEmpty()) {
                    Button(
                        onClick = onUseLatestWeekAndSync,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.week_picker_latest))
                    }
                }

                if (uiState.error != null) {
                    ErrorState(
                        message = stringResource(AppErrorMapper.toStringRes(uiState.error)),
                        onRetry = onRetrySync,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(uiState.catalog) { index, entry ->
                        WeekPickerRow(
                            entry = entry,
                            isLatest = index == 0,
                            locale = locale,
                            onClick = { onSelectWeek(entry) },
                        )
                    }
                }

                TextButton(
                    onClick = onSkipSync,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.onboarding_action_skip))
                }
            }
        }
    }
}

@Composable
private fun WeekPickerRow(
    entry: SurveyWeekCatalogEntry,
    isLatest: Boolean,
    locale: Locale,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = SurveyWeekFormatter.formatRange(entry.surveyWeek, locale),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (isLatest) {
                AssistChip(
                    onClick = onClick,
                    label = { Text(text = stringResource(R.string.active_week_label)) },
                )
            }
        }

        entry.publishedAt?.let { publishedAt ->
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            Text(
                text = stringResource(
                    R.string.week_picker_updated_at,
                    publishedAt.format(formatter),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        entry.operationalNote?.let { note ->
            Text(
                text = stringResource(R.string.week_picker_operational_note, note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 4.dp),
            )
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
private fun OnboardingIntroActions(
    uiState: OnboardingUiState,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    onProceedToWeekPicker: () -> Unit,
    onSkipSync: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (uiState.isOnLastPage) {
            Button(
                onClick = onProceedToWeekPicker,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.onboarding_action_get_started))
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
private fun OnboardingIntroContentPreview() {
    AnpFuelTheme {
        OnboardingContent(
            uiState = OnboardingUiState(pageIndex = 2),
            onNextPage = {},
            onPreviousPage = {},
            onPageSelected = {},
            onProceedToWeekPicker = {},
            onUseLatestWeekAndSync = {},
            onSelectWeek = {},
            onRetryCatalog = {},
            onRetrySync = {},
            onSkipSync = {},
            onBackToIntro = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWeekPickerPreview() {
    val week = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
    AnpFuelTheme {
        OnboardingContent(
            uiState = OnboardingUiState(
                step = OnboardingStep.WEEK_PICKER,
                catalog = listOf(
                    SurveyWeekCatalogEntry.create(
                        surveyWeek = week,
                        summaryUrl = "https://example.com/summary.xlsx",
                        stationUrl = "https://example.com/station.xlsx",
                        publishedAt = LocalDate.parse("2026-06-12"),
                    ),
                ),
            ),
            onNextPage = {},
            onPreviousPage = {},
            onPageSelected = {},
            onProceedToWeekPicker = {},
            onUseLatestWeekAndSync = {},
            onSelectWeek = {},
            onRetryCatalog = {},
            onRetrySync = {},
            onSkipSync = {},
            onBackToIntro = {},
        )
    }
}
