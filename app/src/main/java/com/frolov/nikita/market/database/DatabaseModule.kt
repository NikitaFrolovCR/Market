package com.frolov.nikita.market.database

import com.frolov.nikita.market.database.repositories.GoodsRepository
import com.frolov.nikita.market.database.repositories.GoodsRepositoryImpl


interface DatabaseModule {

    fun getGoodsRepository(): GoodsRepository

}

object DatabaseModuleImpl : DatabaseModule {

    override fun getGoodsRepository() = GoodsRepositoryImpl()

}