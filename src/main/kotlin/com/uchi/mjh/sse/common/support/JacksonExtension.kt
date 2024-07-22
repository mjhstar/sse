package com.uchi.mjh.sse.common.support

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.uchi.mjh.sse.common.support.JacksonExtension.mapper
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JacksonExtension {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private val mapper: ObjectMapper = createDefaultMapper()

    private fun createDefaultMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule().apply {
            registerModule(JavaTimeModule().apply {
                addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(LOCAL_DATE_TIME_FORMATTER))
                addSerializer(LocalDate::class.java, LocalDateSerializer(DATE_FORMATTER))

                addDeserializer(LocalDateTime::class.java, CustomLocalDateTimeDeserializer())
                addDeserializer(LocalDate::class.java, LocalDateDeserializer(DATE_FORMATTER))
            })
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    fun mapper(): ObjectMapper {
        return mapper
    }
}

class CustomLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    private val offsetFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val localDateTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        try {
            val text = p.text
            return when {
                isIsoOffsetDateTime(text) -> LocalDateTime.parse(text, offsetFormat)
                isLocalDateTime(text) -> LocalDateTime.parse(text, localDateTimeFormat)
                else -> LocalDateTime.parse(text)
            }
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    private fun isIsoOffsetDateTime(text: String): Boolean {
        return try {
            offsetFormat.parse(text)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isLocalDateTime(text: String): Boolean {
        return try {
            localDateTimeFormat.parse(text)
            true
        } catch (e: Exception) {
            false
        }
    }
}

inline fun <T, reified R> T.convert(): R {
    return mapper().convertValue(this, R::class.java)
}

fun <T> ByteArray?.toObject(clazz: Class<T>): T? {
    return try {
        mapper().readValue(this, clazz)
    } catch (e: Exception) {
        null
    }
}
