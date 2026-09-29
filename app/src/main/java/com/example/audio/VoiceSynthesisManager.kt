package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceSynthesisManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentVoice = MutableStateFlow("Zephyr")
    val currentVoice: StateFlow<String> = _currentVoice.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                applyVoiceProfile("Zephyr")
                isInitialized = true
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun setVoice(voiceName: String) {
        _currentVoice.value = voiceName
        applyVoiceProfile(voiceName)
    }

    private fun applyVoiceProfile(voiceName: String) {
        when (voiceName.lowercase()) {
            "zephyr" -> {
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.02f)
            }
            "kore" -> {
                tts?.setPitch(1.2f)
                tts?.setSpeechRate(1.05f)
            }
            "puck" -> {
                tts?.setPitch(0.9f)
                tts?.setSpeechRate(1.1f)
            }
            "aoede" -> {
                tts?.setPitch(1.15f)
                tts?.setSpeechRate(0.98f)
            }
            "fenrir" -> {
                tts?.setPitch(0.75f)
                tts?.setSpeechRate(0.95f)
            }
            else -> {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) return
        stop()
        val cleanedText = text
            .replace(Regex("```[\\s\\S]*?```"), "Code block omitted.")
            .replace(Regex("[*#_`~]"), "")
            .trim()

        if (cleanedText.isNotEmpty()) {
            _isSpeaking.value = true
            tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, "AgentTurn_${System.currentTimeMillis()}")
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
