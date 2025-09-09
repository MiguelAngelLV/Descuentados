package org.malv.descuentados.models

data class VideoResult(
    val videoId: String,
    val title: String,
    val status: VideoStatus,
    val error: String? = null,
)
