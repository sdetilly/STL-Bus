package com.tilly.steven.stlbusarrivals

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tilly.steven.stlbusarrivals.dao.DetailsDao
import com.tilly.steven.stlbusarrivals.model.Details

@Database(entities = arrayOf(Details::class), version = 3)
abstract class DetailsDatabase : RoomDatabase(){
    abstract fun detailsDao(): DetailsDao

    companion object {
        var mInstance: DetailsDatabase? = null
        @JvmStatic
        fun getInstance(): DetailsDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(MyApplication.context, DetailsDatabase::class.java, "detail")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return mInstance!!
        }
    }
}