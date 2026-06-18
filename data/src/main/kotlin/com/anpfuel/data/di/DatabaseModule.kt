package com.anpfuel.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anpfuel.data.local.AnpFuelDatabase
import com.anpfuel.data.local.dao.AveragePriceDao
import com.anpfuel.data.local.dao.ImportAuditLogDao
import com.anpfuel.data.local.dao.StationPriceDao
import com.anpfuel.data.local.dao.SurveyWeekDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "anp_fuel.db"

    @Provides
    @Singleton
    fun provideAnpFuelDatabase(
        @ApplicationContext context: Context,
    ): AnpFuelDatabase =
        Room.databaseBuilder(context, AnpFuelDatabase::class.java, DATABASE_NAME)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()

    @Provides
    fun provideSurveyWeekDao(database: AnpFuelDatabase): SurveyWeekDao =
        database.surveyWeekDao()

    @Provides
    fun provideAveragePriceDao(database: AnpFuelDatabase): AveragePriceDao =
        database.averagePriceDao()

    @Provides
    fun provideStationPriceDao(database: AnpFuelDatabase): StationPriceDao =
        database.stationPriceDao()

    @Provides
    fun provideImportAuditLogDao(database: AnpFuelDatabase): ImportAuditLogDao =
        database.importAuditLogDao()
}
