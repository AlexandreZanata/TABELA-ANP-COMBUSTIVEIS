package com.anpfuel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anpfuel.data.local.entity.AveragePriceEntity

@Dao
interface AveragePriceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<AveragePriceEntity>)

    @Query(
        """
        SELECT * FROM average_price
        WHERE survey_week_id = :surveyWeekId
          AND state = :state
          AND municipality = :municipality
        """,
    )
    suspend fun findByLocation(
        surveyWeekId: String,
        state: String,
        municipality: String,
    ): List<AveragePriceEntity>

    @Query("SELECT COUNT(*) FROM average_price")
    suspend fun count(): Int
}
