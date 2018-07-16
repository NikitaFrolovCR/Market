package com.frolov.nikita.market.models

interface Model<T> : BaseParcelable {
    var id: T?
}