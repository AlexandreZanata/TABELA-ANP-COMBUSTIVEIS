package com.anpfuel.application.usecase.location

import com.anpfuel.domain.event.CitySelected
import com.anpfuel.domain.exception.DomainException
import com.anpfuel.domain.model.UserPreferences
import com.anpfuel.domain.repository.AveragePriceRepository
import com.anpfuel.domain.repository.DomainEventPublisher
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.repository.UserPreferencesRepository
import com.anpfuel.domain.rule.EmptyMunicipalityResultRule
import com.anpfuel.domain.rule.SearchRequiresImportedDataRule
import com.anpfuel.domain.valueobject.BrazilianState
import com.anpfuel.domain.valueobject.DomainId
import com.anpfuel.domain.valueobject.SurveyWeek

data class LocationStatesResult(
    val surveyWeek: SurveyWeek,
    val states: List<BrazilianState>,
)

data class LocationMunicipalitiesResult(
    val surveyWeek: SurveyWeek,
    val state: BrazilianState,
    val municipalities: List<String>,
) {
    val isEmpty: Boolean
        get() = EmptyMunicipalityResultRule.shouldReturnEmptyList(municipalities.size)
}

data class PreferredLocation(
    val state: BrazilianState,
    val municipality: String,
)

data class SelectLocationResult(
    val event: CitySelected,
    val preferences: UserPreferences,
)

class SelectLocationUseCase(
    private val averagePriceRepository: AveragePriceRepository,
    private val priceTableRepository: PriceTableRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val eventPublisher: DomainEventPublisher,
) {

    suspend fun getStatesWithData(): LocationStatesResult {
        requireImportedData()
        val surveyWeek = requireLatestSurveyWeek()
        val states = averagePriceRepository.getStatesWithData(surveyWeek)

        return LocationStatesResult(
            surveyWeek = surveyWeek,
            states = states,
        )
    }

    suspend fun getMunicipalities(state: BrazilianState): LocationMunicipalitiesResult {
        requireImportedData()
        val surveyWeek = requireLatestSurveyWeek()
        val municipalities = averagePriceRepository.getMunicipalitiesWithData(
            state = state,
            surveyWeek = surveyWeek,
        )

        return LocationMunicipalitiesResult(
            surveyWeek = surveyWeek,
            state = state,
            municipalities = municipalities,
        )
    }

    suspend fun getPreferredLocation(): PreferredLocation? {
        val preferences = userPreferencesRepository.getPreferences()
        val state = preferences.preferredState ?: return null
        val municipality = preferences.preferredMunicipality ?: return null

        return PreferredLocation(
            state = state,
            municipality = municipality,
        )
    }

    suspend fun selectLocation(
        state: BrazilianState,
        municipality: String,
    ): SelectLocationResult {
        requireImportedData()
        val surveyWeek = requireLatestSurveyWeek()
        validateMunicipalitySelection(
            state = state,
            municipality = municipality,
            surveyWeek = surveyWeek,
        )

        val currentPreferences = userPreferencesRepository.getPreferences()
        val updatedPreferences = currentPreferences.copy(
            preferredState = state,
            preferredMunicipality = municipality.trim(),
        )
        userPreferencesRepository.savePreferences(updatedPreferences)

        val event = CitySelected.create(
            payload = CitySelected.Payload(
                municipality = municipality.trim(),
                state = state,
                surveyWeekId = DomainId.forSurveyWeek(surveyWeek),
            ),
        )
        eventPublisher.publish(event)

        return SelectLocationResult(
            event = event,
            preferences = updatedPreferences,
        )
    }

    private suspend fun requireImportedData() {
        SearchRequiresImportedDataRule.validate(
            priceTableRepository.countImportedSurveyWeeks(),
        )
    }

    private suspend fun requireLatestSurveyWeek(): SurveyWeek =
        averagePriceRepository.getLatestImportedSurveyWeek()
            ?: throw DomainException("BR-006: No successfully imported SurveyWeek is available")

    private suspend fun validateMunicipalitySelection(
        state: BrazilianState,
        municipality: String,
        surveyWeek: SurveyWeek,
    ) {
        val normalizedMunicipality = municipality.trim()
        if (normalizedMunicipality.isBlank()) {
            throw DomainException("Municipality must not be blank")
        }

        val availableMunicipalities = averagePriceRepository.getMunicipalitiesWithData(
            state = state,
            surveyWeek = surveyWeek,
        )
        if (normalizedMunicipality !in availableMunicipalities) {
            throw DomainException(
                "Selected municipality is not available for ${state.name} in survey week ${surveyWeek.endDate}",
            )
        }
    }
}
