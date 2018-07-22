package com.frolov.nikita.market.database.repositories

import com.frolov.nikita.market.database.dao.BaseDao
import com.frolov.nikita.market.models.Model
import com.frolov.nikita.market.models.converters.Converter

abstract class BaseRepository<M : Model<T>, DBModel, T> : Repository<M, T> {

    protected abstract val converter: Converter<M, DBModel>

    protected abstract val dao: BaseDao<DBModel>

}