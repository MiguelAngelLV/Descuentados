package org.malv.descuentados.models

class Configuration(
    var codes: String = "",
    var clientSecretPath: String = "",
    var languages: Map<String, Language> = DEFAULT_LANGUAGES,
) {

    companion object {

        private const val DEFAULT_TEMPLATE = "{{discount}}$ en pedidos de {{minOrder}}$: {{code}}"
        private const val DEFAULT_START = "*Descuentos Aliexpress*"
        private const val DEFAULT_END = "*Capítulos*"
        private const val DEFAULT_LANGUAGE = "es-ES"

        private val DEFAULT_LANGUAGES = mapOf(
            DEFAULT_LANGUAGE to Language(template = DEFAULT_TEMPLATE, start = DEFAULT_START, end = DEFAULT_END, code = DEFAULT_LANGUAGE),
        )
    }
}
