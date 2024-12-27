package sk.sfabian.myeliquid

import java.util.Properties

object Configuration {
    private val properties: Properties = Properties()

    init {
        val inputStream = Configuration::class.java.classLoader?.getResourceAsStream("config.properties")
        if (inputStream != null) {
            properties.load(inputStream)
        } else {
            throw RuntimeException("Config file not found!")
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return properties.getProperty(key)?.toBoolean() ?: defaultValue
    }
}