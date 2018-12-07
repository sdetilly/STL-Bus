package com.tilly.steven.stlbusarrivals.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tilly.steven.stlbusarrivals.model.Details

@Dao
interface DetailsDao {

    @Query("SELECT * FROM detail")
    fun loadDetails(): LiveData<List<Details>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(detail: Details)

    @Delete
    fun deleteDetail(detail: Details)
}