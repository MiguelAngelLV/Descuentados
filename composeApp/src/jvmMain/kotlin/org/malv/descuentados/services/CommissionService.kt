package org.malv.descuentados.services

import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.getRowOrNull
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.malv.descuentados.models.Commission
import java.util.Locale

class CommissionService {

    fun getCommissions(file: String): List<Commission> {
        return DataFrame.readCsv(file, parserOptions = ParserOptions(locale = Locale.US))
            .rename(
                "CompletedPaymentsTime" to "date",
                "CompletedPaymentsAmount" to "amount",
                "EstimatedPaymentsCommission" to "commission",
                "OrderStatus" to "status",
                "Order Platform" to "platform",
                "OrderID" to "order"
            ).convertTo<Row>()
            .filter { it["status"] != INVALID }
            .filter { it["platform"] == INFLUENCER_PLATFORM }
            .groupBy("order")
            .aggregate {
                min("date") into "date"
                sum("commission") into "commission"
                count() into "items"
            }
            .add("month") { it.date.monthNumber }
            .add("year") { it.date.year }
            .sortBy("year", "month")
            .groupBy("year", "month")
            .aggregate {
                sum("commission") into "commission"
                sum("items") into "items"
                count() into "orders"
            }
            .add("diff") { row ->
                val previousRow = getRowOrNull(index() - 1) ?: return@add 0.0
                (100.0 * (row.commission - previousRow.commission)) / previousRow.commission
            }.convertTo<Commission>().toList()
    }

    @DataSchema
    data class Row(
        val date: LocalDate,
        val amount: Double,
        val commission: Double = 0.0,
        val platform: String,
        val order: String,
    )

    companion object {
        val instance = CommissionService()
        private const val INFLUENCER_PLATFORM = "influencer platform"
        private const val INVALID = "Invalid"
    }
}
