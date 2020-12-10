package me.gserv.fabrikommander.data.spec

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val name: String,

    val pos: Pos,
)
