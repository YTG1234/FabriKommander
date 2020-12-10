@file:UseSerializers(IdentifierSerializer::class)

package me.gserv.fabrikommander.data.spec

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.gserv.fabrikommander.utils.IdentifierSerializer
import net.minecraft.util.Identifier

@Serializable
data class Pos(
    val x: Double,
    val y: Double,
    val z: Double,

    val yaw: Float,
    val pitch: Float,

    val world: Identifier,
)
