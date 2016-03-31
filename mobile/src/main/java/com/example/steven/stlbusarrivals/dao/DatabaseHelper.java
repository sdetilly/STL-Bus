package com.example.steven.stlbusarrivals.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.steven.stlbusarrivals.model.Details;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Steven on 2016-01-29.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "Details.db";
    private static final int DATABASE_VERSION = 3;
    private Dao<Details, Integer> detailsDao;
    private RuntimeExceptionDao<Details, Integer> detailsRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Details.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getCanonicalName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Details.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);

        } catch (SQLException e){
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Details, Integer> getDao()throws SQLException{
        if(detailsDao == null){
            detailsDao = getDao(Details.class);
        }
        return detailsDao;
    }

    public RuntimeExceptionDao<Details, Integer> getDetailsDao(){
        if(detailsRuntimeDao == null){
            detailsRuntimeDao = getRuntimeExceptionDao(Details.class);
        }
        return detailsRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    public void close(){
        super.close();
        detailsDao = null;
        detailsRuntimeDao = null;
    }

    public ArrayList<Details> getAllOrderedDetails() {
        // Construct the data source
        // get our query builder from the DAO
        QueryBuilder<Details, Integer> queryBuilder = getDetailsDao().queryBuilder();
        // the 'password' field must be equal to "qwerty"
        // prepare our sql statement
        PreparedQuery<Details> preparedQuery = null;
        try {
            preparedQuery = queryBuilder.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (ArrayList) getDetailsDao().query(preparedQuery);
    }

}
