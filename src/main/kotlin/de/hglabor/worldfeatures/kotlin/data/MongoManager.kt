package de.hglabor.worldfeatures.kotlin.data

import de.hglabor.worldfeatures.kotlin.features.LootableBody
import de.hglabor.worldfeatures.kotlin.gson.Config
import net.axay.blueutils.database.DatabaseLoginInformation
import net.axay.blueutils.database.mongodb.MongoDB

object MongoManager {

    val mongoDB = MongoDB(DatabaseLoginInformation(ConfigManager.dbHost, ConfigManager.dbPort, ConfigManager.dbDatabase, ConfigManager.dbUsername, ConfigManager.dbPassword))

    val BODIES = mongoDB.getCollectionOrCreate<LootableBody>("LOOTABLEBODIES_BODIES")

}