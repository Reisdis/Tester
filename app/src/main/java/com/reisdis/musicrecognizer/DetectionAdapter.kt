package com.reisdis.musicrecognizer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecognitionResult(
    @Json(name = "metadata") val metadata: MetadataAdapter
)

@JsonClass(generateAdapter = true)
data class MetadataAdapter(
    @Json(name = "music") val musics: List<Music>
)


@JsonClass(generateAdapter = true)
data class Music(
    // @Json(name = "external_metadata") val externalMetadata: ExternalMetadata,
    @Json(name = "title") val title: String,
    @Json(name = "album") val album: Album,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "score") val score: Int,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "artists") val artists: List<Artist>,
    @Json(name = "label") val label: String
)

data class ExternalMetadata(
    @Json(name = "musicbrainz") val musicbrainz: MusicBrainz,
    @Json(name = "deezer") val deezer: Deezer,
    @Json(name = "spotify") val spotify: Spotify,
    @Json(name = "andyou") val andyou: Andyou,
    @Json(name = "youtube") val youtube: Youtube,
    @Json(name = "syncpower") val syncpower: Syncpower
)

@JsonClass(generateAdapter = true)
data class MusicBrainz(
    @Json(name = "track") val track: Track
)

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "id") val id: String
)

@JsonClass(generateAdapter = true)
data class Deezer(
    @Json(name = "track") val track: Track,
    @Json(name = "artists") val artists: List<Artist>,
    @Json(name = "album") val album: Album
)

@JsonClass(generateAdapter = true)
data class Spotify(
    @Json(name = "track") val track: Track,
    @Json(name = "artists") val artists: List<Artist>,
    @Json(name = "album") val album: Album
)

@JsonClass(generateAdapter = true)
data class Andyou(
    @Json(name = "track") val track: Track,
    @Json(name = "artists") val artists: List<Artist>,
    @Json(name = "album") val album: Album
)

@JsonClass(generateAdapter = true)
data class Youtube(
    @Json(name = "vid") val vid: String
)

@JsonClass(generateAdapter = true)
data class Syncpower(
    @Json(name = "track") val track: Track,
    @Json(name = "artists") val artists: List<Artist>,
    @Json(name = "album") val album: Album
)

@JsonClass(generateAdapter = true)
data class Album(
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "name") val name: String
)


@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "name") val name: String
)