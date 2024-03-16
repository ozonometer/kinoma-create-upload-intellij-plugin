package com.kinomatool

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SendAction: AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val userInputDialog = UserDialog()
        if (userInputDialog.showAndGet()) {

        }
    }
}