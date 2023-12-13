package com.rebalance.backend.api.dto.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.rebalance.backend.api.dto.response.ApiNotificationType
import java.lang.reflect.Type

class ApiNotificationTypeAdapter : JsonDeserializer<ApiNotificationType> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ApiNotificationType {
        json?.let {
            val typeInt = it.asInt
            return ApiNotificationType.fromInt(typeInt)
                ?: throw IllegalArgumentException("Unknown enum type: $typeInt")
        } ?: throw NullPointerException("JsonElement is null")
    }
}
