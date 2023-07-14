package com.juniori.puzzle.app.util


import com.juniori.puzzle.data.datasource.firebasedatasource.response.BooleanFieldFilter
import com.juniori.puzzle.data.datasource.firebasedatasource.response.BooleanValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.CompositeFilter
import com.juniori.puzzle.data.datasource.firebasedatasource.response.FieldReference
import com.juniori.puzzle.data.datasource.firebasedatasource.response.Filter
import com.juniori.puzzle.data.datasource.firebasedatasource.response.IntegerFieldFilter
import com.juniori.puzzle.data.datasource.firebasedatasource.response.IntegerValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.Order
import com.juniori.puzzle.data.datasource.firebasedatasource.response.StringFieldFilter
import com.juniori.puzzle.data.datasource.firebasedatasource.response.StringValue
import com.juniori.puzzle.data.datasource.firebasedatasource.response.StructuredQuery
import com.juniori.puzzle.data.datasource.firebasedatasource.response.Where

object QueryUtil {
    fun getMyVideoQuery(uid: String, offset: Int?, limit: Int?) = StructuredQuery(
        where = Filter(
            fieldFilter = StringFieldFilter(
                field = FieldReference("owner_uid"),
                op = "EQUAL",
                value = StringValue(uid)
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference("update_time"),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )

    fun getMyVideoWithKeywordQuery(
        uid: String,
        toSearch: String,
        keyword: String,
        offset: Int?,
        limit: Int?
    ) = StructuredQuery(
        where = Where(
            CompositeFilter(
                op = "AND",
                filters = listOf(
                    Filter(
                        StringFieldFilter(
                            field = FieldReference("owner_uid"),
                            op = "EQUAL",
                            value = StringValue(uid)
                        )
                    ),
                    Filter(
                        StringFieldFilter(
                            field = FieldReference(toSearch),
                            op = "ARRAY_CONTAINS",
                            value = StringValue(keyword)
                        )
                    )
                )
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference("update_time"),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )


    fun getPublicVideoQuery(
        orderBy: SortType,
        offset: Int?,
        limit: Int?,
        latestData: Long?,
    ) = StructuredQuery(
        where = Where(
            CompositeFilter(
                op = "AND",
                filters = listOf(
                    Filter(
                        fieldFilter = BooleanFieldFilter(
                            field = FieldReference("is_private"),
                            op = "EQUAL",
                            value = BooleanValue(false)
                        )
                    ),
                    Filter(
                        fieldFilter = IntegerFieldFilter(
                            field = FieldReference(orderBy.value),
                            op = "LESS_THAN_OR_EQUAL",
                            value = IntegerValue(latestData?:Long.MAX_VALUE)
                        )
                    )
                )
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference(orderBy.value),
                direction = "DESCENDING"
            ),
            Order(
                field = FieldReference(when(orderBy){
                    SortType.NEW -> SortType.LIKE.value
                    SortType.LIKE -> SortType.NEW.value
                }),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )

    fun getPublicVideoWithKeywordQuery(
        orderBy: SortType,
        toSearch: String,
        keyword: String,
        latestData: Long?,
        offset: Int?,
        limit: Int?
    ) = StructuredQuery(
        where = Where(
            CompositeFilter(
                op = "AND",
                filters = listOf(
                    Filter(
                        fieldFilter = BooleanFieldFilter(
                            field = FieldReference("is_private"),
                            op = "EQUAL",
                            value = BooleanValue(false)
                        )
                    ),
                    Filter(
                        StringFieldFilter(
                            field = FieldReference(toSearch),
                            op = "ARRAY_CONTAINS",
                            value = StringValue(keyword)
                        )
                    ),
                    Filter(
                        IntegerFieldFilter(
                            field = FieldReference(orderBy.value),
                            op = "LESS_THAN_OR_EQUAL",
                            value = latestData?.let { IntegerValue(it) }
                                ?: IntegerValue(Long.MAX_VALUE)
                        )
                    )
                )
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference(orderBy.value),
                direction = "DESCENDING"
            ),
            Order(
                field = FieldReference(when(orderBy){
                    SortType.NEW -> SortType.LIKE.value
                    SortType.LIKE -> SortType.NEW.value
                }),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )
}