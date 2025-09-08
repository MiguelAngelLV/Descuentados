package org.malv.descontados.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoLocalization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.malv.descontados.models.Language
import org.malv.descontados.models.Video
import org.malv.descontados.models.VideoResult
import org.malv.descontados.models.VideoStatus
import org.malv.utils.between
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

class YoutubeService(
    private val credential: Credential,
) {

    private val youtube = YouTube.Builder(credential.transport, credential.jsonFactory, credential)
        .setApplicationName("youtube")
        .build()

    suspend fun getVideos(): List<Video> = withContext(Dispatchers.IO) {
        clearCache()
        val channels = youtube.channels()
            .list(listOf("contentDetails"))
            .setMine(true)
            .execute()
            .items

        var next: String? = null
        val playlistIds = channels[0].contentDetails.relatedPlaylists.uploads
        sequence {
            do {
                val response = youtube.playlistItems()
                    .list(listOf("contentDetails"))
                    .setPlaylistId(playlistIds)
                    .setMaxResults(MAX_RESULTS)
                    .setPageToken(next)
                    .execute()

                next = response.nextPageToken
                val playlists = response.items
                val videos = youtube.videos()
                    .list(listOf("snippet", "localizations"))
                    .setId(playlists.map { it.contentDetails.videoId })
                    .execute()
                    .items

                videos.forEach { v ->
                    if (v.localizations == null) {
                        yield(
                            Video(
                                id = v.id,
                                title = v.snippet.title,
                                description = v.snippet.description,
                                language = "es",
                                single = true
                            )
                        )
                    } else {
                        v.localizations.forEach {
                            yield(
                                Video(
                                    id = v.id,
                                    title = it.value.title,
                                    description = it.value.description,
                                    language = it.key,
                                    single = false
                                )
                            )
                        }
                    }
                }
            } while (next != null)
        }.toList()
    }

    private suspend fun updateVideo(video: Video) = withContext(Dispatchers.IO) {
        val original = youtube.videos()
            .list(listOf("snippet", "localizations"))
            .setId(listOf(video.id))
            .execute()
            .items.firstOrNull() ?: return@withContext

        if (original.localizations == null) {
            original.localizations = mutableMapOf<String, VideoLocalization>()
        }

        original.localizations[video.language] = VideoLocalization().apply {
            title = video.title
            description = video.description
        }

        original.snippet.defaultLanguage = "es-ES"

        if (original.snippet.defaultLanguage == video.language) {
            original.snippet.title = video.title
            original.snippet.description = video.description
        }

        youtube.videos().update(listOf("localizations", "snippet"), original).execute()
    }

    suspend fun updateVideo(video: Video, languages: Map<String, Language>, codes: String): VideoResult {
        val language = languages[video.languageId] ?: let {
            return VideoResult(video.id, video.title, VideoStatus.ERROR, "Idioma no encontrado")
        }

        val newCodes = CodesService.generateCodes(codes, language.template)
        val codes = video.description.between(language.start, language.end)
            ?: return VideoResult(video.id, video.title, VideoStatus.ERROR, "Inicio/fin no encontrado.")

        if (codes == newCodes) {
            return VideoResult(video.id, video.title, VideoStatus.SKIPPED)
        }

        val newDescription = video.description.replace(codes, newCodes)
        if (newDescription.length > MAX_LENGTH) {
            return VideoResult(video.id, video.title, VideoStatus.ERROR, "Descripción demasiado larga.")
        }

        return runCatching {
            updateVideo(video.copy(description = newDescription))
        }.fold(
            onSuccess = { VideoResult(video.id, video.title, VideoStatus.UPDATED) },
            onFailure = { VideoResult(video.id, video.title, VideoStatus.ERROR, it.message) }
        )
    }

    private fun clearCache() {
        runCatching {
            val dataClass = Class.forName("com.google.api.client.util.Data")
            val nullCacheField: Field = dataClass.getDeclaredField("NULL_CACHE")
            nullCacheField.isAccessible = true
            val nullCache = nullCacheField.get(null) as ConcurrentHashMap<*, *>
            nullCache.clear()
        }
    }

    companion object {
        private const val MAX_RESULTS = 50L
        private const val MAX_LENGTH = 5000
    }
}
