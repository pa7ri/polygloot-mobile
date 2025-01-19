package com.polygloot.mobile.android.ui.conversation

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.polygloot.mobile.android.ui.translator.ErrorType
import com.polygloot.mobile.android.ui.translator.TranslatorResult
import com.polygloot.mobile.android.ui.translator.TranslatorStatus
import com.polygloot.mobile.android.ui.translator.TranslatorStatusError
import com.polygloot.mobile.android.ui.translator.TranslatorStatusIdle
import com.polygloot.mobile.android.ui.translator.TranslatorStatusLoading
import com.polygloot.mobile.android.ui.translator.TranslatorUser
import com.polygloot.mobile.android.ui.translator.getTranslatorUserByLanguage
import com.polygloot.mobile.android.ui.utils.Consts.Companion.AUDIO_INPUT_FILE_NAME
import com.polygloot.mobile.android.ui.utils.Consts.Companion.AUDIO_OUTPUT_FILE_NAME
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import com.polygloot.mobile.polygloot.network.repository.translator.TranslatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.AbstractMap.SimpleEntry
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val repository: TranslatorRepository
) : ViewModel() {
    private var recorder: MediaRecorder? = null
    private var outputFile: String? = null
    private var outputFilePath: String? = null

    var translatorStatus = MutableStateFlow<TranslatorStatus>(TranslatorStatusIdle)
    var translatorResult = MutableStateFlow<MutableList<TranslatorResult>>(mutableListOf())

    var sourceLanguage = MutableStateFlow(
        SimpleEntry(Locale.getDefault().isO3Language, Locale.getDefault().displayLanguage)
    )
    var targetLanguage = MutableStateFlow(
        SimpleEntry(Locale.getDefault().isO3Language, Locale.getDefault().displayLanguage)
    )

    fun dismissError() {
        translatorStatus.value = TranslatorStatusIdle
    }

    fun startRecording(context: Context, path: String) {
        outputFilePath = path
        outputFile = "$outputFilePath/$AUDIO_INPUT_FILE_NAME"
        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            try {
                prepare()
            } catch (e: IOException) {
                translatorStatus.value = TranslatorStatusError(ErrorType.AUDIO_ERROR)
            }
            start()
        }
    }

    suspend fun stopRecording() {
        recorder?.apply {
            try {
                stop()
                release()
                speechToText()
            } catch (e: IllegalStateException) {
                translatorStatus.value = TranslatorStatusError(ErrorType.AUDIO_ERROR)
            } catch (e: RuntimeException) {
                translatorStatus.value = TranslatorStatusError(ErrorType.AUDIO_ERROR)
            } finally {
                recorder = null
            }
        }
    }

    private fun playAudio(filePath: String) {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.setOnErrorListener { mp, _, _ ->
            mp.release()
            true
        }
    }

    suspend fun speechToText() {
        translatorStatus.value = TranslatorStatusLoading
        when (val result =
            withContext(Dispatchers.IO) { outputFile?.let { repository.speechToText(it) } }) {
            is DomainResult.Success -> {
                translateText(
                    result.body.toString(),
                    sourceLanguage.value.value,
                    targetLanguage.value.value
                )
            }

            else -> {
                translatorStatus.value = TranslatorStatusError(ErrorType.STT_ERROR)
            }
        }
    }

    suspend fun textToSpeech(text: String, translatorUser: TranslatorUser) {
        val outputTranslatedFile = File(outputFilePath, AUDIO_OUTPUT_FILE_NAME)
        when (val result = withContext(Dispatchers.IO) {
            repository.textToSpeech(
                text,
                translatorUser.voice,
                outputTranslatedFile
            )
        }) {
            is DomainResult.Success -> {
                if (result.body == true) {
                    playAudio(outputTranslatedFile.absolutePath)
                    translatorStatus.value = TranslatorStatusIdle
                }
            }

            else -> {
                translatorStatus.value = TranslatorStatusError(ErrorType.TTS_ERROR)
            }
        }
    }

    private suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ) {
        when (val result = withContext(Dispatchers.IO) {
            repository.translateText(text, sourceLanguage, targetLanguage)
        }) {
            is DomainResult.Success -> {
                result.body?.let {
                    val sourceUser =
                        getTranslatorUserByLanguage(sourceLanguage, it.sourceLanguage)
                    translatorResult.value.add(TranslatorResult(sourceUser, it.text))
                    textToSpeech(it.text, sourceUser)
                }
            }

            else -> {
                translatorStatus.value = TranslatorStatusError(ErrorType.TRANSLATION_ERROR)
            }
        }
    }
}