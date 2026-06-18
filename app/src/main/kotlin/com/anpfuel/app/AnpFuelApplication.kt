package com.anpfuel.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.anpfuel.app.locale.AppLocaleHolder
import com.anpfuel.data.worker.SyncWorkScheduler
import com.anpfuel.domain.repository.UserPreferencesRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class AnpFuelApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncWorkScheduler: SyncWorkScheduler

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        runBlocking(Dispatchers.IO) {
            AppLocaleHolder.localeTag = userPreferencesRepository.getPreferences().localeTag
        }
        applicationScope.launch {
            syncWorkScheduler.schedulePeriodicSync()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
