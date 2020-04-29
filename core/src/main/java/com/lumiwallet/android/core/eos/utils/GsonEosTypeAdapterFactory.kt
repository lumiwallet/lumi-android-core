package com.lumiwallet.android.core.eos.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.lumiwallet.android.core.eos.models.types.TypeAccountName
import com.lumiwallet.android.core.eos.models.types.TypeActionName
import com.lumiwallet.android.core.eos.models.types.TypeName
import com.lumiwallet.android.core.eos.models.types.TypePermissionName
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*

class GsonEosTypeAdapterFactory : TypeAdapterFactory {

    private val adapters = LinkedHashMap<Class<*>, TypeAdapter<*>>()

    init {
        adapters[TypeName::class.java] = TypeNameAdapter(TypeName::class.java)
        adapters[TypeAccountName::class.java] = TypeNameAdapter(TypeAccountName::class.java)
        adapters[TypeActionName::class.java] = TypeNameAdapter(TypeActionName::class.java)
        adapters[TypePermissionName::class.java] = TypeNameAdapter(TypePermissionName::class.java)
    }

    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        var typeAdapter: TypeAdapter<T>? = null
        var currentType: Class<*> = Any::class.java
        for (type in adapters.keys) {
            if (type.isAssignableFrom(typeToken.rawType)) {
                if (currentType.isAssignableFrom(type)) {
                    currentType = type
                    typeAdapter = adapters[type] as TypeAdapter<T>?
                }
            }
        }
        return typeAdapter
    }

    class TypeNameAdapter<C>(private val clazz: Class<C>) : TypeAdapter<C>() {

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): C? {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }

            try {
                val constructor = clazz.getConstructor(String::class.java)
                return constructor.newInstance(`in`.nextString())
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            return null
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: C?) {
            if (value == null) {
                out.nullValue()
                return
            }

            out.value(value.toString())
        }
    }
}
