package com.anpfuel.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anpfuel.data.local.dao.AveragePriceDao
import com.anpfuel.data.local.dao.ImportAuditLogDao
import com.anpfuel.data.local.dao.StationPriceDao
import com.anpfuel.data.local.dao.SurveyWeekDao
import com.anpfuel.data.local.entity.AveragePriceEntity
import com.anpfuel.data.local.entity.ImportAuditLogEntity
import com.anpfuel.data.local.entity.StationPriceEntity
import com.anpfuel.data.local.entity.SurveyWeekEntity

@Database(
    entities = [
        SurveyWeekEntity::class,
        AveragePriceEntity::class,
        StationPriceEntity::class,
        ImportAuditLogEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AnpFuelDatabase : RoomDatabase() {

    abstract fun surveyWeekDao(): SurveyWeekDao

    abstract fun averagePriceDao(): AveragePriceDao

    abstract fun stationPriceDao(): StationPriceDao

    abstract fun importAuditLogDao(): ImportAuditLogDao
}
