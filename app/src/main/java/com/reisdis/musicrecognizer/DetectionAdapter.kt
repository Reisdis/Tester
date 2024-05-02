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
    @Json(name = "external_metadata") val externalMetadata: ExternalMetadata,
    @Json(name = "title") val title: String,
    @Json(name = "album") val album: Album,
    @Json(name = "genres") val genres: List<Genre>?,
    @Json(name = "artists") val artists: List<Artist>,
)

data class ExternalMetadata(
    @Json(name = "musicbrainz") val musicbrainz: MusicBrainz? = null,
    @Json(name = "deezer") val deezer: Deezer? = null,
    @Json(name = "spotify") val spotify: Spotify? = null,
    @Json(name = "andyou") val andyou: Andyou? = null,
    @Json(name = "youtube") val youtube: Youtube? = null,
    @Json(name = "syncpower") val syncpower: Syncpower? = null
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


val dummyMusic = Music(
    externalMetadata = ExternalMetadata(
        musicbrainz = MusicBrainz(
            track = Track(
                id = "dummy_track_id"
            )
        ),
        deezer = Deezer(
            track = Track(
                id = "dummy_track_id"
            ),
            artists = listOf(
                Artist(name = "Artist Name")
            ),
            album = Album(name = "Album Name")
        ),
        spotify = Spotify(
            track = Track(
                id = "dummy_track_id"
            ),
            artists = listOf(
                Artist(name = "Artist Name")
            ),
            album = Album(name = "Album Name")
        ),
        andyou = Andyou(
            track = Track(
                id = "dummy_track_id"
            ),
            artists = listOf(
                Artist(name = "Artist Name")
            ),
            album = Album(name = "Album Name")
        ),
        youtube = Youtube(
            vid = "dummy_video_id"
        ),
        syncpower = Syncpower(
            track = Track(
                id = "dummy_track_id"
            ),
            artists = listOf(
                Artist(name = "Artist Name")
            ),
            album = Album(name = "Album Name")
        )
    ),
    title = "Dummy Title",
    album = Album(name = "Dummy Album"),
    genres = listOf(
        Genre(name = "Genre Name")
    ),
    artists = listOf(
        Artist(name = "Artist Name")
    )
)