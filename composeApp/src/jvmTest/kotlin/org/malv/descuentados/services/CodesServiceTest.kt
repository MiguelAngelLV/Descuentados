package org.malv.descuentados.services

import kotlin.test.Test
import kotlin.test.assertEquals

class CodesServiceTest {

    @Test
    fun `extract dollar codes from string`() {
        val codes = """
            $3 de descuento en pedidos superiores a $29:【IFP7EYYH】
            $6 de descuento en pedidos superiores a $59:【IFPNJ8PO】
            $9 de descuento en pedidos superiores a $89:【IFP3UH6B】
            $16 de descuento en pedidos superiores a $149:【IFPPJBN7】
            $23 de descuento en pedidos superiores a $199:【IFPXWMCI】
            $30 de descuento en pedidos superiores a $269:【IFPI1AXA】
            $40 de descuento en pedidos superiores a $369:【IFPXBK2A】
            $50 de descuento en pedidos superiores a $469:【IFPXHFYP】
            $60 de descuento en pedidos superiores a $599:【IFPYSGRC】
            $70 de descuento en pedidos superiores a $699:【IFPFM3VI】
            """

        val result = CodesService.extractCodes(codes)
        assertEquals(10, result.size)
        assertEquals("IFP7EYYH", result[0].code)
        assertEquals(29, result[0].minOrder)
        assertEquals(3, result[0].discount)
    }

    @Test
    fun `extract euro codes from string`() {
        val codes = """
            3,00€ de descuento en pedidos superiores a 15,00€:【 IFPCPGI7 】
            5,00€ de descuento en pedidos superiores a 30,00€:【 IFPVKEH4 】
            7,00€ de descuento en pedidos superiores a 49,00€:【 IFPVPPVR 】
            11,00€ de descuento en pedidos superiores a 79,00€:【 IFP2TIDB 】
            20,00€ de descuento en pedidos superiores a 139,00€:【 IFPVPJVR 】
            30,00€ de descuento en pedidos superiores a 209,00€:【 IFPXZ9LM 】
            45,00€ de descuento en pedidos superiores a 319,00€:【 IFP4U3EA 】
            60,00€ de descuento en pedidos superiores a 429,00€:【 IFPPNPOJ 】
            70,00€ de descuento en pedidos superiores a 509,00€:【 IFPK0NOE 】
            2026/03/16 00:00:00 PST - 2026/03/25 23:59:59 PST 
        """

        val result = CodesService.extractCodes(codes)
        assertEquals(9, result.size)
        assertEquals("IFPCPGI7", result[0].code)
        assertEquals(15, result[0].minOrder)
        assertEquals(3, result[0].discount)
    }
}
