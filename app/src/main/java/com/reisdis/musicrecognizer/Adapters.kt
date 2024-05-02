package com.reisdis.musicrecognizer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Humming(
    @Json(name = "duration_ms") val durationMs: Int,
    @Json(name = "external_metadata") val externalMetadata: Map<String, Any>,
    val title: String,
    val album: Album,
    val acrid: String,
    @Json(name = "release_date") val releaseDate: String,
    val artists: List<Artist>,
    @Json(name = "play_offset_ms") val playOffsetMs: Int,
    @Json(name = "result_from") val resultFrom: Int,
    val score: Double,
    val label: String
)

@JsonClass(generateAdapter = true)
data class Album(
    val name: String
)

@JsonClass(generateAdapter = true)
data class Artist(
    val name: String,
    val roles: List<String>? = null,
    val langs: List<Lang>? = null,
    val isni: String? = null
)

@JsonClass(generateAdapter = true)
data class Lang(
    val code: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class RecognitionResult(
    @Json(name = "cost_time") val costTime: Double,
    val status: Status,
    val metadata: Metadata
)

@JsonClass(generateAdapter = true)
data class Status(
    val code: Int,
    val msg: String,
    val version: String
)

@JsonClass(generateAdapter = true)
data class Metadata(
    val humming: List<Humming>
)