package org.malv.descuentados.services

object CodesService {
    private val REGEX_CODE = Regex("(\\d+)([.,]\\d{2})?\\D.*?\\D(\\d+)([.,]\\d{2})?.*?【\\s*(\\w+)\\s*】")
    private val REGEX_CURRENCY = Regex("[$€]")
    data class Code(val code: String, val minOrder: Int, val discount: Int, val currency: String)

    fun extractCodes(codes: String): List<Code> {
        val currency = REGEX_CURRENCY.find(codes)?.value ?: "$"
        return codes.lines().mapNotNull { REGEX_CODE.find(it) }.map {
            val (discount, _, minOrder, _, code) = it.destructured
            Code(code, minOrder.toInt(), discount.toInt(), currency)
        }
    }

    fun applyTemplate(codes: List<Code>, template: String): String {
        return template
            .replace("{{discount}}", codes.first().discount.toString())
            .replace("{{minOrder}}", codes.first().minOrder.toString())
            .replace("{{code}}", codes.joinToString(", ") { it.code })
            .replace("{{currency}}", codes.first().currency)
            .replace("{{percentage}}", "${(codes.first().discount * 100) / codes.first().minOrder}%")
    }

    fun generateCodes(codes: String, template: String, start: String = "", end: String = ""): String {
        val codes = extractCodes(codes)
            .sortedBy { it.minOrder }
            .groupBy { Pair(it.minOrder, it.discount) }
            .map { applyTemplate(it.value, template) }

        return "$start\n${codes.joinToString("\n")}\n\n$end"
    }
}
