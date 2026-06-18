package com.anpfuel.data.di

import com.anpfuel.application.usecase.location.SearchMunicipalityUseCase
import com.anpfuel.application.usecase.location.SelectLocationUseCase
import com.anpfuel.application.usecase.onboarding.CompleteOnboardingUseCase
import com.anpfuel.application.usecase.price.GetMunicipalityPricesUseCase
import com.anpfuel.application.usecase.price.GetPriceHistoryUseCase
import com.anpfuel.application.usecase.price.GetStationPricesUseCase
import com.anpfuel.application.usecase.settings.ApplyStationDetailRetentionUseCase
import com.anpfuel.application.usecase.settings.ClearCacheUseCase
import com.anpfuel.application.usecase.settings.GetSettingsUseCase
import com.anpfuel.application.usecase.settings.UpdatePreferencesUseCase
import com.anpfuel.application.usecase.sync.DownloadStationDetailUseCase
import com.anpfuel.application.usecase.sync.SyncPriceTablesUseCase
import com.anpfuel.domain.repository.AveragePriceRepository
import com.anpfuel.domain.repository.CacheRepository
import com.anpfuel.domain.repository.DomainEventPublisher
import com.anpfuel.domain.repository.MunicipalitySearchRepository
import com.anpfuel.domain.repository.PriceTableRepository
import com.anpfuel.domain.repository.PriceTableSyncGateway
import com.anpfuel.domain.repository.StationPriceRepository
import com.anpfuel.domain.repository.SyncJobRepository
import com.anpfuel.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideApplyStationDetailRetentionUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        stationPriceRepository: StationPriceRepository,
    ): ApplyStationDetailRetentionUseCase = ApplyStationDetailRetentionUseCase(
        userPreferencesRepository = userPreferencesRepository,
        stationPriceRepository = stationPriceRepository,
    )

    @Provides
    @Singleton
    fun provideSyncPriceTablesUseCase(
        syncJobRepository: SyncJobRepository,
        priceTableRepository: PriceTableRepository,
        priceTableSyncGateway: PriceTableSyncGateway,
        userPreferencesRepository: UserPreferencesRepository,
        eventPublisher: DomainEventPublisher,
        applyStationDetailRetentionUseCase: ApplyStationDetailRetentionUseCase,
    ): SyncPriceTablesUseCase = SyncPriceTablesUseCase(
        syncJobRepository = syncJobRepository,
        priceTableRepository = priceTableRepository,
        priceTableSyncGateway = priceTableSyncGateway,
        userPreferencesRepository = userPreferencesRepository,
        eventPublisher = eventPublisher,
        applyStationDetailRetentionUseCase = applyStationDetailRetentionUseCase,
    )

    @Provides
    @Singleton
    fun provideDownloadStationDetailUseCase(
        syncJobRepository: SyncJobRepository,
        priceTableRepository: PriceTableRepository,
        priceTableSyncGateway: PriceTableSyncGateway,
        averagePriceRepository: AveragePriceRepository,
        eventPublisher: DomainEventPublisher,
    ): DownloadStationDetailUseCase = DownloadStationDetailUseCase(
        syncJobRepository = syncJobRepository,
        priceTableRepository = priceTableRepository,
        priceTableSyncGateway = priceTableSyncGateway,
        averagePriceRepository = averagePriceRepository,
        eventPublisher = eventPublisher,
    )

    @Provides
    @Singleton
    fun provideCompleteOnboardingUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        priceTableRepository: PriceTableRepository,
    ): CompleteOnboardingUseCase = CompleteOnboardingUseCase(
        userPreferencesRepository = userPreferencesRepository,
        priceTableRepository = priceTableRepository,
    )

    @Provides
    @Singleton
    fun provideSearchMunicipalityUseCase(
        municipalitySearchRepository: MunicipalitySearchRepository,
        priceTableRepository: PriceTableRepository,
    ): SearchMunicipalityUseCase = SearchMunicipalityUseCase(
        municipalitySearchRepository = municipalitySearchRepository,
        priceTableRepository = priceTableRepository,
    )

    @Provides
    @Singleton
    fun provideSelectLocationUseCase(
        averagePriceRepository: AveragePriceRepository,
        priceTableRepository: PriceTableRepository,
        userPreferencesRepository: UserPreferencesRepository,
        eventPublisher: DomainEventPublisher,
    ): SelectLocationUseCase = SelectLocationUseCase(
        averagePriceRepository = averagePriceRepository,
        priceTableRepository = priceTableRepository,
        userPreferencesRepository = userPreferencesRepository,
        eventPublisher = eventPublisher,
    )

    @Provides
    @Singleton
    fun provideGetMunicipalityPricesUseCase(
        averagePriceRepository: AveragePriceRepository,
        priceTableRepository: PriceTableRepository,
        userPreferencesRepository: UserPreferencesRepository,
    ): GetMunicipalityPricesUseCase = GetMunicipalityPricesUseCase(
        averagePriceRepository = averagePriceRepository,
        priceTableRepository = priceTableRepository,
        userPreferencesRepository = userPreferencesRepository,
    )

    @Provides
    @Singleton
    fun provideGetPriceHistoryUseCase(
        averagePriceRepository: AveragePriceRepository,
        priceTableRepository: PriceTableRepository,
        userPreferencesRepository: UserPreferencesRepository,
    ): GetPriceHistoryUseCase = GetPriceHistoryUseCase(
        averagePriceRepository = averagePriceRepository,
        priceTableRepository = priceTableRepository,
        userPreferencesRepository = userPreferencesRepository,
    )

    @Provides
    @Singleton
    fun provideGetStationPricesUseCase(
        stationPriceRepository: StationPriceRepository,
        averagePriceRepository: AveragePriceRepository,
        priceTableRepository: PriceTableRepository,
        userPreferencesRepository: UserPreferencesRepository,
    ): GetStationPricesUseCase = GetStationPricesUseCase(
        stationPriceRepository = stationPriceRepository,
        averagePriceRepository = averagePriceRepository,
        priceTableRepository = priceTableRepository,
        userPreferencesRepository = userPreferencesRepository,
    )

    @Provides
    @Singleton
    fun provideGetSettingsUseCase(
        userPreferencesRepository: UserPreferencesRepository,
    ): GetSettingsUseCase = GetSettingsUseCase(
        userPreferencesRepository = userPreferencesRepository,
    )

    @Provides
    @Singleton
    fun provideUpdatePreferencesUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        eventPublisher: DomainEventPublisher,
    ): UpdatePreferencesUseCase = UpdatePreferencesUseCase(
        userPreferencesRepository = userPreferencesRepository,
        eventPublisher = eventPublisher,
    )

    @Provides
    @Singleton
    fun provideClearCacheUseCase(
        cacheRepository: CacheRepository,
        stationPriceRepository: StationPriceRepository,
        userPreferencesRepository: UserPreferencesRepository,
        eventPublisher: DomainEventPublisher,
    ): ClearCacheUseCase = ClearCacheUseCase(
        cacheRepository = cacheRepository,
        stationPriceRepository = stationPriceRepository,
        userPreferencesRepository = userPreferencesRepository,
        eventPublisher = eventPublisher,
    )
}
