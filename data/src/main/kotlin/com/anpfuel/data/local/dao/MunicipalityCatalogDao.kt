package com.anpfuel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anpfuel.data.local.entity.MunicipalityCatalogEntity

@Dao
interface MunicipalityCatalogDao {

    @Query("SELECT COUNT(*) FROM municipality_catalog")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<MunicipalityCatalogEntity>)

    @Query(
        """
        SELECT * FROM municipality_catalog
        WHERE state = :state AND normalized_name = :normalizedName
        LIMIT 1
        """,
    )
    suspend fun findByStateAndNormalizedName(
        state: String,
        normalizedName: String,
    ): MunicipalityCatalogEntity?

    @Query("UPDATE municipality_catalog SET anp_alias = :alias WHERE id = :id")
    suspend fun updateAnpAlias(id: String, alias: String)

    @Query("SELECT * FROM municipality_catalog")
    suspend fun findAll(): List<MunicipalityCatalogEntity>
}
