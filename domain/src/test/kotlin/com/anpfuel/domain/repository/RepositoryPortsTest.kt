package com.anpfuel.domain.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RepositoryPortsTest {

    @Test
    fun allRepositoryPortsAreDeclared() {
        val portNames = listOf(
            PriceTableRepository::class.java,
            AveragePriceRepository::class.java,
            StationPriceRepository::class.java,
            MunicipalitySearchRepository::class.java,
            UserPreferencesRepository::class.java,
            SyncJobRepository::class.java,
            PriceTableSyncGateway::class.java,
            DomainEventPublisher::class.java,
            CacheRepository::class.java,
        ).map { it.simpleName }

        assertEquals(
            setOf(
                "PriceTableRepository",
                "AveragePriceRepository",
                "StationPriceRepository",
                "MunicipalitySearchRepository",
                "UserPreferencesRepository",
                "SyncJobRepository",
                "PriceTableSyncGateway",
                "DomainEventPublisher",
                "CacheRepository",
            ),
            portNames.toSet(),
        )

        assertTrue(
            listOf(
                PriceTableRepository::class.java,
                AveragePriceRepository::class.java,
                StationPriceRepository::class.java,
                MunicipalitySearchRepository::class.java,
                UserPreferencesRepository::class.java,
                SyncJobRepository::class.java,
                PriceTableSyncGateway::class.java,
                DomainEventPublisher::class.java,
                CacheRepository::class.java,
            ).all { it.isInterface },
        )
    }
}
