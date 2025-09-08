package org.malv.descontados.services

object CodesService {
    private val REGEX_CODE = Regex("(\\d+)\\D.*\\D(\\d{1,3}).*【(\\w+)】")
    data class Code(val code: String, val minOrder: Int, val discount: Int)

    fun extractCodes(codes: String): List<Code> {
        return codes.lines().mapNotNull { REGEX_CODE.find(it) }.map {
            val (discount, minOrder, code) = it.destructured
            Code(code, minOrder.toInt(), discount.toInt())
        }
    }

    fun applyTemplate(codes: List<Code>, template: String): String {
        return template
            .replace("{{discount}}", codes.first().discount.toString())
            .replace("{{minOrder}}", codes.first().minOrder.toString())
            .replace("{{code}}", codes.joinToString(", ") { it.code })
    }

    fun generateCodes(codes: String, template: String, start: String = "", end: String = ""): String {
        val codes = extractCodes(codes)
            .sortedBy { it.minOrder }
            .groupBy { Pair(it.minOrder, it.discount) }
            .map { applyTemplate(it.value, template) }

        return "$start\n${codes.joinToString("\n")}\n\n$end"
    }
}
