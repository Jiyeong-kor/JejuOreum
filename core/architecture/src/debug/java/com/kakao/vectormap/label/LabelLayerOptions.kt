package com.kakao.vectormap.label

class LabelLayerOptions private constructor() {
    fun setOrderingType(@Suppress("UNUSED_PARAMETER") type: OrderingType): LabelLayerOptions = this
    fun setCompetitionUnit(@Suppress("UNUSED_PARAMETER") unit: CompetitionUnit): LabelLayerOptions = this
    fun setCompetitionType(@Suppress("UNUSED_PARAMETER") type: CompetitionType): LabelLayerOptions = this

    companion object {
        fun from(@Suppress("UNUSED_PARAMETER") layerId: String): LabelLayerOptions = LabelLayerOptions()
    }
}

enum class OrderingType {
    Rank,
}

enum class CompetitionUnit {
    IconAndText,
}

enum class CompetitionType {
    All,
}
