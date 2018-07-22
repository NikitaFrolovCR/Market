package com.frolov.nikita.market.database.dao

import android.arch.persistence.room.*

@Dao
interface BaseDao<in I> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: I): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg objects: I)

    @Update
    fun update(obj: I)

    @Update
    fun updateAll(vararg objects: I)

    @Delete
    fun delete(obj: I)
}