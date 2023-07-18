package com.juniori.puzzle.data.datasource.firebasedatasource.response

import com.google.gson.annotations.SerializedName

data class RunQueryRequestDTO(
    val structuredQuery: StructuredQuery
)

data class RunQueryResponseDTO(
    @SerializedName("document") val videoItem: VideoItem?,
    @SerializedName("readTime") val readTime: String
)