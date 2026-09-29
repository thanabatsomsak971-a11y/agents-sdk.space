package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ToolDefinitionEntity
import com.example.data.model.AgentSdkConfig
import com.example.data.model.ToolDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AgentConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val toolDao = db.toolDao()

    private val _config = MutableStateFlow(AgentSdkConfig())
    val config: StateFlow<AgentSdkConfig> = _config.asStateFlow()

    val availableTools: StateFlow<List<ToolDefinition>> = toolDao.getAllTools().map { list ->
        list.map {
            ToolDefinition(
                name = it.name,
                description = it.description,
                parametersJson = it.parametersJson,
                isEnabled = it.isEnabled,
                isBuiltIn = it.isBuiltIn
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateModel(modelName: String) {
        _config.value = _config.value.copy(modelName = modelName)
    }

    fun updateVoice(voiceName: String) {
        _config.value = _config.value.copy(voiceName = voiceName)
    }

    fun updateThinkingLevel(level: String) {
        _config.value = _config.value.copy(thinkingLevel = level)
    }

    fun updateContextCompression(trigger: String, window: String) {
        _config.value = _config.value.copy(
            triggerTokens = trigger,
            slidingWindowTokens = window
        )
    }

    fun updateSystemInstruction(prompt: String) {
        _config.value = _config.value.copy(systemInstruction = prompt)
    }

    fun toggleGoogleSearch(enabled: Boolean) {
        _config.value = _config.value.copy(isGoogleSearchEnabled = enabled)
    }

    fun toggleTool(toolName: String, currentEnabled: Boolean) {
        viewModelScope.launch {
            val tool = availableTools.value.find { it.name == toolName } ?: return@launch
            toolDao.updateTool(
                ToolDefinitionEntity(
                    name = tool.name,
                    description = tool.description,
                    parametersJson = tool.parametersJson,
                    isEnabled = !currentEnabled,
                    isBuiltIn = tool.isBuiltIn
                )
            )
        }
    }

    fun addCustomTool(name: String, description: String, parametersJson: String) {
        viewModelScope.launch {
            toolDao.insertTool(
                ToolDefinitionEntity(
                    name = name,
                    description = description,
                    parametersJson = parametersJson,
                    isEnabled = true,
                    isBuiltIn = false
                )
            )
        }
    }
}
