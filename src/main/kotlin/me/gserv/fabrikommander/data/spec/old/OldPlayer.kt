package me.gserv.fabrikommander.data.spec.old

import kotlinx.serialization.Serializable

@Serializable
data class OldPlayer(
    val name: String,

    val homes: MutableList<OldHome> = mutableListOf(),
)
