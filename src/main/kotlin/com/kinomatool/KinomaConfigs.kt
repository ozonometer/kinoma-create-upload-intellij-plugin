package com.kinomatool

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State (name = "State to save configurations", storages = [Storage("kinoma-tool-storage.xml")])
class KinomaConfigs : PersistentStateComponent<SettingsState>  {

    private var configState = SettingsState()
    override fun getState(): SettingsState? {
        return configState
    }

    override fun loadState(p0: SettingsState) {
        configState = p0
    }

    companion object {
        @JvmStatic
        fun getInstanse(): PersistentStateComponent<SettingsState> {
            return ServiceManager.getService(KinomaConfigs::class.java)
        }
    }
}