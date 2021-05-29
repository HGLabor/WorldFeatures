package de.hglabor.worldfeatures.kotlin.data

import de.hglabor.worldfeatures.kotlin.features.LootableBody
import de.hglabor.worldfeatures.kotlin.gson.Config
import net.axay.blueutils.database.mongodb.MongoDB

object MongoManager {

    val mongoDB = MongoDB(Config.databaseInfo, spigot = true)

    val BODIES = mongoDB.getCollectionOrCreate<LootableBody>("LOOTABLEBODIES_BODIES")

}