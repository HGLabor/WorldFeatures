package de.hglabor.worldfeatures.kotlin.data

import de.hglabor.worldfeatures.kotlin.gson.jsonConfig
import net.axay.blueutils.database.DatabaseLoginInformation
import net.axay.kspigot.config.PluginFile
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ConfigManager {

    private val databaseConfig = YamlConfiguration.loadConfiguration(PluginFile("db.yml"))

    val dbHost
    get() = databaseConfig.getString("host")!!

    val dbDatabase
        get() = databaseConfig.getString("database")!!

    val dbUsername
        get() = databaseConfig.getString("username")!!

    val dbPort
        get() = databaseConfig.getInt("port")

    val dbPassword
        get() = databaseConfig.getString("password")!!

}