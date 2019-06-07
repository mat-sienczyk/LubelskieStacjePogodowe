package pl.sienczykm.templbn.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type


class EmptyStringTypeAdapter<T>
private constructor() : JsonDeserializer<T> {

    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, context: JsonDeserializationContext): T? {
        if (jsonElement.isJsonPrimitive) {
            val jsonPrimitive = jsonElement.asJsonPrimitive
            if (jsonPrimitive.isString && jsonPrimitive.asString.isEmpty()) {
                return null
            }
        }
        return context.deserialize<T>(jsonElement, type)
    }

}