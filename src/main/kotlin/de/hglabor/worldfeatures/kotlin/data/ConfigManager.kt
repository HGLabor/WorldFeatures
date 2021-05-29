package de.hglabor.worldfeatures.kotlin.data

import de.hglabor.worldfeatures.kotlin.gson.jsonConfig
import net.axay.blueutils.database.DatabaseLoginInformation
import java.io.File

object ConfigManager {

    val databaseLoginInformation
            by jsonConfig(File("./db.json")) {
                DatabaseLoginInformation.NOTSET_DEFAULT
            }

}