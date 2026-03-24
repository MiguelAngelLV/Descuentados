package org.malv.descuentados.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoLocalization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.malv.descuentados.models.Language
import org.malv.descuentados.models.Video
import org.malv.descuentados.models.VideoResult
import org.malv.descuentados.models.VideoStatus
import org.malv.utils.between
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

class YoutubeService(
    private val credential: Credential,
) {
    private val logger = LoggerFactory.getLogger(YoutubeService::class.java)

    private val youtube = YouTube.Builder(credential.transport, credential.jsonFactory, credential)
        .setApplicationName("youtube")
        .build()

    suspend fun getVideos(): List<Video> = withContext(Dispatchers.IO) {
        logger.info("Obteniendo lista de vídeos de YouTube")
        clearCache()
        val channels = youtube.channels()
            .list(listOf("contentDetails,snippet"))
            .setMine(true)
            .execute()
            .items

        logger.info("Canales encontrados: {}", channels.joinToString { it.snippet.title })

        var next: String? = null
        val videos = channels.map { it.contentDetails.relatedPlaylists.uploads }.flatMap { playlistId ->
            logger.debug("Procesando playlist: $playlistId")
            sequence {
                do {
                    val response = youtube.playlistItems()
                        .list(listOf("contentDetails"))
                        .setPlaylistId(playlistId)
                        .setMaxResults(MAX_RESULTS)
                        .setPageToken(next)
                        .execute()

                    next = response.nextPageToken
                    val playlists = response.items
                    logger.debug("Obtenidos ${playlists.size} items de la playlist")

                    val videos = youtube.videos()
                        .list(listOf("snippet", "localizations"))
                        .setId(playlists.map { it.contentDetails.videoId })
                        .execute()
                        .items

                    videos.forEach { v ->
                        if (v.localizations == null) {
                            logger.debug("Vídeo ${v.id} sin localizaciones")
                            yield(
                                Video(
                                    id = v.id,
                                    title = v.snippet.title,
                                    description = v.snippet.description,
                                    language = v.snippet.defaultLanguage,
                                    single = true
                                )
                            )
                        } else {
                            logger.debug("Vídeo ${v.id} con ${v.localizations.size} localizaciones")
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

        logger.info("Total de vídeos obtenidos: {}", videos.size)
        videos
    }

    private suspend fun updateVideo(video: Video) = withContext(Dispatchers.IO) {
        logger.info("Actualizando vídeo ${video.id} (${video.language})")
        logger.debug("Título: ${video.title}")

        val original = youtube.videos()
            .list(listOf("snippet", "localizations"))
            .setId(listOf(video.id))
            .execute()
            .items.firstOrNull()

        if (original == null) {
            logger.error("No se encontró el vídeo ${video.id} en YouTube")
            return@withContext
        }

        if (original.localizations == null) {
            logger.debug("Creando mapa de localizaciones para vídeo ${video.id}")
            original.localizations = mutableMapOf<String, VideoLocalization>()
        }

        original.localizations[video.language] = VideoLocalization().apply {
            title = video.title
            description = video.description
        }

        if (original.snippet.defaultLanguage == video.language) {
            logger.debug("Actualizando idioma por defecto")
            original.snippet.title = video.title
            original.snippet.description = video.description
        }

        youtube.videos().update(listOf("localizations", "snippet"), original).execute()
        logger.info("Vídeo ${video.id} actualizado exitosamente")
    }

    suspend fun updateVideo(video: Video, languages: Map<String, Language>, codes: String): VideoResult {
        logger.debug("Procesando actualización de vídeo ${video.id} - Idioma: ${video.languageId}")

        val language = languages[video.languageId]
        if (language == null) {
            logger.error("Idioma ${video.languageId} no encontrado en la configuración")
            return VideoResult(video.id, video.title, VideoStatus.ERROR, "Idioma ${video.languageId} no encontrado.")
        }

        val newCodes = CodesService.generateCodes(codes, language.template)
        val codes = video.description.between(language.start, language.end)

        if (codes == null) {
            logger.error("No se encontraron marcadores de inicio/fin en vídeo ${video.id}")
            logger.debug("Inicio: '${language.start}', Fin: '${language.end}'")
            return VideoResult(video.id, video.title, VideoStatus.ERROR, "Inicio/fin no encontrado.")
        }

        if (codes == newCodes) {
            logger.info("Vídeo ${video.id} ya tiene los códigos actualizados, omitiendo")
            return VideoResult(video.id, video.title, VideoStatus.SKIPPED)
        }

        val newDescription = video.description.replace("${language.start}$codes${language.end}", "${language.start}${newCodes}${language.end}")
        if (newDescription.length > MAX_LENGTH) {
            logger.error("Descripción demasiado larga para vídeo ${video.id}: ${video.description.length} -> ${newDescription.length}")
            return VideoResult(video.id, video.title, VideoStatus.ERROR, "Descripción demasiado larga: ${video.description.length} -> ${newDescription.length}")
        }

        return runCatching {
            updateVideo(video.copy(description = newDescription))
        }.fold(
            onSuccess = {
                logger.info("Vídeo ${video.id} actualizado correctamente")
                VideoResult(video.id, video.title, VideoStatus.UPDATED)
            },
            onFailure = {
                logger.error("Error al actualizar vídeo ${video.id}: ${it.message}", it)
                VideoResult(video.id, video.title, VideoStatus.ERROR, it.message)
            }
        )
    }

    private fun clearCache() {
        logger.debug("Limpiando caché de Google API")
        runCatching {
            val dataClass = Class.forName("com.google.api.client.util.Data")
            val nullCacheField: Field = dataClass.getDeclaredField("NULL_CACHE")
            nullCacheField.isAccessible = true
            val nullCache = nullCacheField.get(null) as ConcurrentHashMap<*, *>
            nullCache.clear()
            logger.debug("Caché limpiada exitosamente")
        }.onFailure {
            logger.warn("No se pudo limpiar la caché de Google API: ${it.message}")
        }
    }

    companion object {
        private const val MAX_RESULTS = 50L
        private const val MAX_LENGTH = 5000
    }
}
