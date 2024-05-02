package com.reisdis.musicrecognizer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackResponse(
    val album: SpotifyAlbum,
    val artists: List<SpotifyArtist>,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val preview_url: String,
    val type: String,
    val uri: String
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbum(
    val images: List<Image>,
    val name: String,
    val release_date: String
)

@JsonClass(generateAdapter = true)
data class SpotifyArtist(
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

@JsonClass(generateAdapter = true)
data class ExternalUrls(
    val spotify: String
)

@JsonClass(generateAdapter = true)
data class Image(
    val height: Int,
    val url: String,
    val width: Int
)