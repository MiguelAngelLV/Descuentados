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
import org.slf4j.LoggerFactory
import java.util.Locale

class CommissionService {
    private val logger = LoggerFactory.getLogger(CommissionService::class.java)

    fun getCommissions(file: String): List<Commission> {
        logger.info("Cargando comisiones desde archivo: $file")

        val dataFrame = try {
            DataFrame.readCsv(file, parserOptions = ParserOptions(locale = Locale.US))
        } catch (e: Exception) {
            logger.error("Error al leer archivo CSV: ${e.message}", e)
            throw e
        }

        logger.debug("DataFrame cargado con ${dataFrame.rowsCount()} filas")

        val commissions = dataFrame
            .rename(
                "CompletedPaymentsTime" to "date",
                "CompletedPaymentsAmount" to "amount",
                "EstimatedPaymentsCommission" to "commission",
                "OrderStatus" to "status",
                "Order Platform" to "platform",
                "OrderID" to "order"
            ).convertTo<Row>()
            .filter { row ->
                val isValid = row["status"] != INVALID
                if (!isValid) {
                    logger.debug("Filtrando orden con estado inválido: {}", row["order"])
                }
                isValid
            }
            .filter { row ->
                val isInfluencer = row["platform"] == INFLUENCER_PLATFORM
                if (!isInfluencer) {
                    logger.debug("Filtrando orden de plataforma no influencer: {}", row["platform"])
                }
                isInfluencer
            }
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

        logger.info("Comisiones procesadas: ${commissions.size} meses de datos")
        commissions.forEach { commission ->
            logger.debug(
                "Mes ${commission.month}/${commission.year}: ${commission.orders} órdenes, " +
                    "${commission.items} items, comisión: ${commission.commission}"
            )
        }

        return commissions
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
