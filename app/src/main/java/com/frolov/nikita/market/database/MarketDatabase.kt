package com.frolov.nikita.market.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.frolov.nikita.DB_VERSION
import com.frolov.nikita.market.database.converter.EnumTypeConverters
import com.frolov.nikita.market.database.dao.GoodsDao
import com.frolov.nikita.market.database.tables.GoodsDb

@Database(entities = [GoodsDb::class], version = DB_VERSION, exportSchema = false)
@TypeConverters(EnumTypeConverters::class)
abstract class MarketDatabase : RoomDatabase() {

    abstract fun goodsDao(): GoodsDao

}