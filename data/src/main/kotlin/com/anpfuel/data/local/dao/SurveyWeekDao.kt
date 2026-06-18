package com.anpfuel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anpfuel.data.local.entity.SurveyWeekEntity

@Dao
interface SurveyWeekDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: SurveyWeekEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<SurveyWeekEntity>)

    @Query("SELECT * FROM survey_week WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): SurveyWeekEntity?

    @Query("SELECT COUNT(*) FROM survey_week")
    suspend fun count(): Int
}
