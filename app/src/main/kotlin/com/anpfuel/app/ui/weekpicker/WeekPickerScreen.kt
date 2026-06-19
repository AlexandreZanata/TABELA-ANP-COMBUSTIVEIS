package com.anpfuel.app.ui.weekpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekPickerScreen(
    uiState: WeekPickerUiState,
    onRetryCatalog: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onUseLatestWeek: (() -> Unit)? = null,
    onSelectWeek: ((SurveyWeekCatalogEntry) -> Unit)? = null,
    onRetrySync: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.week_picker_title)) },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        TextButton(onClick = onNavigateBack) {
                            Text(text = stringResource(R.string.action_back))
                        }
                    }
                },
            )
        },
        bottomBar = { AnpAttributionFooter() },
    ) { innerPadding ->
        WeekPickerContent(
            uiState = uiState,
            onRetryCatalog = onRetryCatalog,
            onUseLatestWeek = onUseLatestWeek,
            onSelectWeek = onSelectWeek,
            onRetrySync = onRetrySync,
            onSkip = onSkip,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
fun WeekPickerContent(
    uiState: WeekPickerUiState,
    onRetryCatalog: () -> Unit,
    onUseLatestWeek: (() -> Unit)? = null,
    onSelectWeek: ((SurveyWeekCatalogEntry) -> Unit)? = null,
    onRetrySync: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val sections = groupSurveyWeekCatalogByYear(uiState.catalog)
    val latestWeek = uiState.catalog.firstOrNull()?.surveyWeek

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
                if (onUseLatestWeek != null && uiState.catalog.isNotEmpty()) {
                    Button(
                        onClick = onUseLatestWeek,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.week_picker_latest))
                    }
                }

                if (uiState.syncError != null && onRetrySync != null) {
                    ErrorState(
                        message = stringResource(AppErrorMapper.toStringRes(uiState.syncError)),
                        onRetry = onRetrySync,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    sections.forEach { section ->
                        item(key = "year-${section.year}") {
                            Text(
                                text = section.year.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp)
                                    .headingSemantics(),
                            )
                        }

                        items(
                            items = section.entries,
                            key = { entry -> entry.surveyWeek.endDate.toString() },
                        ) { entry ->
                            WeekPickerRow(
                                entry = entry,
                                isLatest = entry.surveyWeek == latestWeek,
                                locale = locale,
                                onClick = onSelectWeek?.let { callback -> { callback(entry) } },
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }

                if (uiState.showSkipAction && onSkip != null) {
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.onboarding_action_skip))
                    }
                }
            }
        }
    }
}

@Composable
fun WeekPickerRow(
    entry: SurveyWeekCatalogEntry,
    isLatest: Boolean,
    locale: Locale,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }

    Column(
        modifier = rowModifier.padding(vertical = 12.dp, horizontal = 8.dp),
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
                    onClick = onClick ?: {},
                    enabled = onClick != null,
                    label = { Text(text = stringResource(R.string.active_week_label)) },
                )
            }
        }

        Text(
            text = "• ${stringResource(R.string.week_picker_weekly_summary_line)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
        )

        Text(
            text = "• ${stringResource(R.string.week_picker_station_detail_line)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
        )

        entry.publishedAt?.let { publishedAt ->
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            Text(
                text = stringResource(
                    R.string.week_picker_updated_at,
                    publishedAt.format(formatter),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }

        entry.operationalNote?.let { note ->
            Text(
                text = stringResource(R.string.week_picker_operational_note, note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekPickerContentPreview() {
    val week = SurveyWeek.fromIsoDates("2026-06-07", "2026-06-13")
    val olderWeek = SurveyWeek.fromIsoDates("2025-12-28", "2026-01-03")
    AnpFuelTheme {
        WeekPickerScreen(
            uiState = WeekPickerUiState(
                catalog = listOf(
                    SurveyWeekCatalogEntry.create(
                        surveyWeek = week,
                        summaryUrl = "https://example.com/summary-latest.xlsx",
                        stationUrl = "https://example.com/station-latest.xlsx",
                        publishedAt = LocalDate.parse("2026-06-12"),
                    ),
                    SurveyWeekCatalogEntry.create(
                        surveyWeek = olderWeek,
                        summaryUrl = "https://example.com/summary-older.xlsx",
                        stationUrl = "https://example.com/station-older.xlsx",
                        publishedAt = LocalDate.parse("2026-01-02"),
                    ),
                ),
            ),
            onRetryCatalog = {},
            onNavigateBack = {},
        )
    }
}
