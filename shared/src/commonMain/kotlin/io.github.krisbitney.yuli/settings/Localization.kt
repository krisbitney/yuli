package io.github.krisbitney.yuli.settings

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@OptIn(ExperimentalResourceApi::class)
object Localization {

    var current: Language = Language.default()

    private var strings: MutableMap<String, Map<String, String>> = HashMap()
    private val resourcePath: (Language) -> String = { "strings/${it.value}/strings.xml" }

    fun stringResource(key: String, language: Language = current): String {
        load(language)
        return strings[language.value]!![key]!!
    }

    private fun load(language: Language) {
        if (strings.containsKey(language.value)) return
        val xmlContent = readXmlToString(language)
        val parsed = parseXmlContent(xmlContent)
        strings[language.value] = parsed
    }

    private fun readXmlToString(language: Language): String {
        val byteArray = runBlocking {
            resource(resourcePath(language)).readBytes()
        }
        return byteArray.decodeToString()
    }
    
    private fun parseXmlContent(xmlContent: String): Map<String, String> {
        val map = HashMap<String, String>()
        val regex = "<string name=\"(.*?)\">(.*?)</string>".toRegex()
        regex.findAll(xmlContent).forEach { matchResult ->
            val key = matchResult.groups[1]?.value ?: ""
            val value = matchResult.groups[2]?.value ?: ""
            map[key] = value
        }
        return map
    }
}