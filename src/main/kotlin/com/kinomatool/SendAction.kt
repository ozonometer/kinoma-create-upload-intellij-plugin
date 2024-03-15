package com.kinomatool

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.Messages

class SendAction: AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val fileChoosersDescriptor = FileChooserDescriptor (false, true, false,false,false,false)
        fileChoosersDescriptor.title = "Choose Kinoma Project Folder"
        fileChoosersDescriptor.description = "Chose Kinoma project src folder"
        FileChooser.chooseFile(fileChoosersDescriptor, actionEvent.project, null, {
            Messages.showMessageDialog(actionEvent.project, it.path, "Path", Messages.getInformationIcon() )
        })
    }

}