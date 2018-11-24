package com.tilly.steven.stlbusarrivals.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.tilly.steven.stlbusarrivals.model.Details
import java.sql.SQLException

/**
 * Created by Steven on 2016-01-29.
 */
class DatabaseHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var detailsDao: Dao<Details, Int>? = null
    private var detailsRuntimeDao: RuntimeExceptionDao<Details, Int>? = null

    fun getDao(): Dao<Details, Int>? {
        if (detailsDao == null) {
            detailsDao = getDao(Details::class.java)
        }
        return detailsDao
    }

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTable(connectionSource, Details::class.java)
        } catch (e: SQLException) {
            Log.e(DatabaseHelper::class.java.canonicalName, "Can't create database", e)
            throw RuntimeException(e)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, connectionSource: ConnectionSource,
                           oldVersion: Int, newVersion: Int) {
        try {
            TableUtils.dropTable<Details, Any>(connectionSource, Details::class.java, true)
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource)

        } catch (e: SQLException) {
            Log.e(DatabaseHelper::class.java.name, "Can't drop databases", e)
            throw RuntimeException(e)
        }

    }

    fun getDetailsDao(): RuntimeExceptionDao<Details, Int>? {
        if (detailsRuntimeDao == null) {
            detailsRuntimeDao = getRuntimeExceptionDao(Details::class.java)
        }
        return detailsRuntimeDao
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    override fun close() {
        super.close()
        detailsDao = null
        detailsRuntimeDao = null
    }

    companion object {
        val DATABASE_NAME = "Details.db"
        private val DATABASE_VERSION = 3
    }

}
