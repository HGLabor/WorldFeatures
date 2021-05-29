package de.hglabor.worldfeatures.kotlin.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.axay.blueutils.database.DatabaseLoginInformation
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Reason: gson configs got removed from kspigot
 * @author bluefireoly
 **/

inline fun <reified T : Any> jsonConfig(
    file: File,
    saveAfterLoad: Boolean = false,
    noinline default: (() -> T)? = null,
) = ConfigDelegate(T::class, file, saveAfterLoad, default)

class ConfigDelegate<T : Any>(
    private val configClass: KClass<T>,
    private val file: File,
    private val saveAfterLoad: Boolean,
    private val defaultCallback: (() -> T)?
) {

    private var internalConfig: T = loadIt()

    var data: T
        get() = internalConfig
        set(value) {
            internalConfig = value
        }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = internalConfig

    operator fun setValue(thisRef: Any?, property: KProperty<*>, config: T): Boolean {
        internalConfig = config
        return true
    }

    /**
     * Saves the config object in its current state to disk.
     */
    fun save() = saveIt(internalConfig)

    /**
     * Loads the current state of the config on disk to the config object.
     */
    fun reload() {
        loadIt()
    }

    private fun saveIt(toSave: T) {
        GsonConfigManager.saveConfig(file, toSave, true)
        internalConfig = toSave
    }

    private fun loadIt(): T {

        val loaded = if (defaultCallback == null)
            GsonConfigManager.loadConfig(file, configClass)
        else
            GsonConfigManager.loadOrCreateDefault(file, configClass, true, defaultCallback)


        if (saveAfterLoad)
            saveIt(loaded)

        return loaded

    }

}

internal object GsonConfigManager {

    fun <T : Any> loadConfig(file: File, configClass: KClass<T>): T =
        FileReader(file).use { reader -> return getGson().fromJson(reader, configClass.java) }

    fun <T : Any> saveConfig(file: File, config: T, pretty: Boolean = true) {
        file.createIfNotExists()
        FileWriter(file).use { writer ->
            getGson(pretty).toJson(config, writer)
        }
    }

    fun <T : Any> loadOrCreateDefault(
        file: File,
        configClass: KClass<T>,
        pretty: Boolean = true,
        default: () -> T
    ): T {
        try {
            return loadConfig(file, configClass)
        } catch (exc: Exception) {
            default.invoke().let {
                saveConfig(file, it, pretty)
                return it
            }
        }
    }

}

internal fun File.createIfNotExists(): Boolean {
    return if (!exists()) {
        if (!parentFile.exists())
            parentFile.mkdirs()
        createNewFile()
    } else true
}

private val gsonBuilder by lazy {
    GsonBuilder()
}

private val gson: Gson by lazy { gsonBuilder.create() }
private val gsonPretty: Gson by lazy { gsonBuilder.setPrettyPrinting().create() }

fun getGson(pretty: Boolean = false) = if (pretty) gsonPretty else gson

object Config {

    val databaseInfo
            by jsonConfig(File("./db.json")) {
                DatabaseLoginInformation.NOTSET_DEFAULT
            }

}