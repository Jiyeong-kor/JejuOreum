package com.jeong.jjoreum.presentation.ui.map

sealed interface MapPinUi {
    val lat: Double
    val lon: Double

    data class Single(
        override val lat: Double,
        override val lon: Double,
        val title: String,
        val oreumId: Int,
    ) : MapPinUi

    data class Cluster(
        override val lat: Double,
        override val lon: Double,
        val count: Int,
    ) : MapPinUi
}
