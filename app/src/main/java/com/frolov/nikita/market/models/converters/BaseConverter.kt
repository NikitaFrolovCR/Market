package com.frolov.nikita.market.models.converters

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer

abstract class BaseConverter<IN, OUT> : Converter<IN, OUT> {

    override fun convertInToOut(inObject: IN): OUT = processConvertInToOut(inObject)

    override fun convertOutToIn(outObject: OUT): IN = processConvertOutToIn(outObject)

    override fun convertListInToOut(inObjects: List<IN>?): List<OUT> =
            inObjects?.let {
                arrayListOf<OUT>().apply {
                    it.forEach {
                        convertInToOut(it)?.let {
                            add(it)
                        }
                    }
                }
            } ?: listOf()

    override fun convertListOutToIn(outObjects: List<OUT>?): List<IN> =
            outObjects?.let {
                arrayListOf<IN>().apply {
                    it.forEach {
                        add(convertOutToIn(it))
                    }
                }
            } ?: listOf()

    override fun convertOutToInRx(outObject: OUT): Flowable<IN?> =
            Flowable.just(convertOutToIn(outObject))

    override fun convertInToOutRx(inObject: IN): Flowable<OUT> =
            Flowable.just(convertInToOut(inObject))

    override fun convertListInToOutRx(inObjects: List<IN>): Flowable<List<OUT>> =
            Flowable.just(convertListInToOut(inObjects))

    override fun convertListOutToInRx(outObjects: List<OUT>): Flowable<List<IN>> =
            Flowable.just(convertListOutToIn(outObjects))

    override fun singleINtoOUT() = FlowableTransformer<OUT, IN> { it.map { convertOutToIn(it) } }

    override fun singleOUTtoIN() = FlowableTransformer<IN?, OUT> { it.map { convertInToOut(it) } }

    override fun listINtoOUT() = FlowableTransformer<List<OUT>, List<IN>> {
        it.map { convertListOutToIn(it) }
    }

    override fun listOUTtoIN() = FlowableTransformer<List<IN>, List<OUT>> {
        it.map { convertListInToOut(it) }
    }

    protected abstract fun processConvertInToOut(inObject: IN): OUT

    protected abstract fun processConvertOutToIn(outObject: OUT): IN
}