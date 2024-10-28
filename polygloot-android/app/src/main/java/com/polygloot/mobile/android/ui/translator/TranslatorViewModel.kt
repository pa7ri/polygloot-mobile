package com.polygloot.mobile.android.ui.translator

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.decodeBase64Audio
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.encodeBase64Audio
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.saveAudioToInternalStorage
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import com.polygloot.mobile.polygloot.network.repository.translator.TranslatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TranslatorViewModel @Inject constructor(
    private val repository: TranslatorRepository
) : ViewModel() {

    private var recorder: MediaRecorder? = null
    private var outputFile: String? = null
    private var outputFilePath: String? = null
    private var outputFileName: String? = null

    var translatorStatus = MutableStateFlow<TranslatorStatus>(TranslatorStatusIdle)

    var sourceResult = MutableStateFlow(TranslatorResult(TranslatorUser.SOURCE, ""))
    var targetResult = MutableStateFlow(TranslatorResult(TranslatorUser.TARGET, ""))

    var sourceLanguage: Pair<String, String> = "Spanish" to "Spanish"
    var targetLanguage: Pair<String, String> = "English" to "English"

    private fun assignResult(user: TranslatorUser, result: String) {
        when (user) {
            TranslatorUser.SOURCE -> sourceResult.value = TranslatorResult(user, result)
            TranslatorUser.TARGET -> targetResult.value = TranslatorResult(user, result)
        }
    }

    fun dismissError() {
        translatorStatus.value = TranslatorStatusIdle
    }

    fun startRecording(context: Context, path: String, translatorUser: TranslatorUser) {
        outputFilePath = path
        outputFileName = "audiorecordtest-$translatorUser.mp3"
        outputFile = "$outputFilePath/$outputFileName"
        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            try {
                prepare()
            } catch (e: IOException) {
                assignResult(translatorUser, "prepare() failed - ${e.message}")
            }
            start()
        }
    }

    suspend fun stopRecording(translatorUser: TranslatorUser) {
        recorder?.apply {
            try {
                stop()
                release()
                speechToText(translatorUser)
            } catch (e: IllegalStateException) {
                assignResult(translatorUser, "Recording stop() failed - ${e.message}")
                translatorStatus.value = TranslatorStatusError(ErrorType.AUDIO_ERROR)
            } catch (e: RuntimeException) {
                assignResult(translatorUser, "Recording stop() failed - ${e.message}")
                translatorStatus.value = TranslatorStatusError(ErrorType.AUDIO_ERROR)
            } finally {
                recorder = null
            }
        }
    }

    suspend fun translateAndTextToSpeech(
        path: String,
        text: String,
        translatorUser: TranslatorUser
    ) {
        translatorStatus.value = TranslatorStatusLoading
        when (val result = withContext(Dispatchers.IO) {
            repository.translateTextAndTTS(text, sourceLanguage.first, targetLanguage.first)
        }) {
            is DomainResult.Success -> {
                if (result.body?.text.isNullOrBlank() && result.body?.audio.isNullOrBlank()) {
                    assignResult(translatorUser, "Translation failed: ${result.body}")
                    translatorStatus.value = TranslatorStatusError(ErrorType.TRANSLATION_ERROR)
                } else {
                    assignResult(translatorUser, result.body?.text.toString())
                    playAudio(saveAudio(result.body?.audio.toString(), path).absolutePath)
                    translatorStatus.value = TranslatorStatusIdle
                }
            }

            else -> {
                assignResult(translatorUser, "Translation failed: $result")
                translatorStatus.value = TranslatorStatusError(ErrorType.TRANSLATION_ERROR)
            }
        }
    }

    suspend fun speechToText(translatorUser: TranslatorUser) {
        translatorStatus.value = TranslatorStatusLoading
        when (val result =
            withContext(Dispatchers.IO) { outputFile?.let { repository.speechToText(it) } }) {
            is DomainResult.Success -> {
                assignResult(translatorUser, result.body.toString())
                translateAndTextToSpeech(
                    outputFilePath!!,
                    result.body.toString(),
                    getTargetUser(translatorUser)
                )
            }

            else -> {
                assignResult(translatorUser, "Translation failed: $result")
                translatorStatus.value = TranslatorStatusError(ErrorType.TRANSLATION_ERROR)
            }
        }
    }

    private fun getTargetUser(user: TranslatorUser): TranslatorUser {
        return when (user) {
            TranslatorUser.SOURCE -> TranslatorUser.TARGET
            TranslatorUser.TARGET -> TranslatorUser.SOURCE
        }
    }

    private fun saveAudio(audioData: String, fileDir: String): File {
        val audioBytes = decodeBase64Audio(audioData)
        return saveAudioToInternalStorage(fileDir, audioBytes)
    }

    private fun playAudio(filePath: String) {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.setOnErrorListener { mp, _, _ ->
            mp.release()
            true
        }
    }

    suspend fun translateAudioAndTTS(path: String, translatorUser: TranslatorUser) {
        when (val result = withContext(Dispatchers.IO) {
            repository.translateAudioAndTTS(
                encodeBase64Audio(outputFilePath!!, outputFileName!!),
                sourceLanguage.first,
                targetLanguage.first
            )
        }) {
            is DomainResult.Success -> {
                if (result.body?.text.isNullOrBlank() && result.body?.audio.isNullOrBlank()) {
                    assignResult(translatorUser, "Translation failed: ${result.body}")
                } else {
                    assignResult(translatorUser, result.body?.text.toString())
                    playAudio(saveAudio(result.body?.audio.toString(), path).absolutePath)
                }
            }

            else -> {
                assignResult(translatorUser, "Translation failed: $result")
            }
        }
    }

    suspend fun textToSpeech(translatorUser: TranslatorUser, text: String) {
        when (val result = withContext(Dispatchers.IO) { repository.textToSpeech(text) }) {
            is DomainResult.Success -> {
                Log.e("speechToText!", result.body.toString())
            }

            else -> {
                assignResult(translatorUser, "Translation failed: $result")
            }
        }
    }
}