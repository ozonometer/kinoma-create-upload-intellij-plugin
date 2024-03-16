package com.kinomatool

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.uiDesigner.core.AbstractLayout
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.konomatool.java.SendHttpRequest
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class UserDialog : DialogWrapper(true) {

    val panel = JPanel(GridBagLayout())
    val appName = JTextField()
    val ip = JTextField()
    val srcPath = JTextField()
    val launch = JCheckBox()
    init {
        init()
        title = "Select Target Device and Project Path"
        val state = KinomaConfigs.getInstanse().state
        if (state != null) {
            appName.text = state.appName
            ip.text = state.ip
            srcPath.text = state.srcPath
            launch.isSelected = state.launch
        }
    }

    override fun createCenterPanel(): JComponent? {
        val gb = GridBag()
            .setDefaultInsets(JBUI.insets(0, 0, AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP))
            .setDefaultWeightX(1.0)
            .setDefaultFill(GridBagConstraints.HORIZONTAL)

        panel.preferredSize = Dimension(400, 200)

        panel.add(label("App Name"), gb.nextLine().next().weightx(0.2))
        panel.add(appName, gb.next().weightx(0.8))
        panel.add(label("Kinoma Create IP"), gb.nextLine().next().weightx(0.2))
        panel.add(ip, gb.next().weightx(0.8))
        panel.add(label("Path to project src"), gb.nextLine().next().weightx(0.2))
        panel.add(srcPath, gb.next().weightx(0.8))
        panel.add(label("Launch on target"), gb.nextLine().next().weightx(0.2))
        panel.add(launch, gb.next().weightx(0.8))

        return panel
    }

    private fun label(text: String) : JComponent {
        val label = JBLabel(text)
        label.componentStyle = UIUtil.ComponentStyle.SMALL
        label.fontColor = UIUtil.FontColor.BRIGHTER
        label.border = JBUI.Borders.empty(0, 5, 2, 0)
        return label
    }

    override fun doOKAction() {
        val appName = appName.text
        val ip = ip.text
        val srcPath = srcPath.text
        val launch = launch.isSelected
        val state = KinomaConfigs.getInstanse().state
        state?.appName = appName
        state?.ip = ip
        state?.srcPath = srcPath
        state?.launch = launch

        var code = SendHttpRequest.sendRequest("OPTIONS", "http://$ip:10000/disconnect",
            "application/javascript", null, null)
        if (code == 200) {
            code = SendHttpRequest.sendRequest("POST", "http://$ip:10000/disconnect",
                "application/javascript", null, null)
            if (code == 200) {
                code = SendHttpRequest.sendRequest("OPTIONS", "http://$ip:10000/upload?path=applications" +
                        "/$appName/application.xml&temporary=false", "application/javascript", null, null)
                if (code == 200) {
                    var body = "<?xml version=\"1.0\" encoding=\"utf-8\"?><application " +
                            "xmlns=\"http://www.kinoma.com/kpr/application/1\" id=\"$appName\" " +
                            "program=\"src/main\" title=\"$appName\"></application>"
                    code = SendHttpRequest.sendRequest("PUT", "http://$ip:10000/upload?path=" +
                            "applications/$appName/application.xml&temporary=false", "application/javascript", null, body)
                    if (code == 200) {
                        val directory = File(srcPath)
                        // Check if the given path is a directory
                        if (!directory.isDirectory) {
                            println("Error: Not a directory!")
                        }

                        // Get all files in the directory
                        val files = directory.listFiles()
                        files?.forEach { file ->
                            if (file.isFile) {
                                val fileName = file.name
                                val filePath = srcPath + "\\$fileName"
                                code = SendHttpRequest.sendRequest("OPTIONS", "http://$ip:10000/" +
                                        "upload?path=applications/$appName/src/$fileName&temporary=false",
                                    "application/javascript", null, null)
                                if (code == 200) {
                                    if (filePath.endsWith(".js")) {
                                        code = SendHttpRequest.sendRequest("PUT", "http://$ip:10000/" +
                                                "upload?path=applications/$appName/src/$fileName&temporary=false",
                                            "application/javascript", filePath, null)
                                    } else {
                                        /*val fileExtention: String = filePath.substringAfterLast(".")
                                            //.replace(".", "")
                                        code = SendHttpRequest.sendRequest("PUT", "http://$ip:10000/" +
                                                "upload?path=applications/$appName/src/$fileName&temporary=false",
                                            "image/$fileExtention", filePath, null)*/
                                    }
                                }
                            }
                        }
                        /*val assetsDirectory = File("$srcPath\\assets")
                        // Check if the given path is a directory
                        if (!assetsDirectory.isDirectory) {
                            println("Error: assetsDirectory Not a directory!")
                        }

                        val assetsFiles = assetsDirectory.listFiles()
                        assetsFiles?.forEach { file ->
                            if (file.isFile) {
                                val assetFileName = file.name
                                val assetFilePath =  "$srcPath\\$assetFileName"
                                code = SendHttpRequest.sendRequest("OPTIONS", "http://$ip:10000/" +
                                        "upload?path=applications/$appName/src/assets/$assetFileName&temporary=false",
                                    null, null, null)
                                if (code == 200) {
                                    code = SendHttpRequest.sendRequest("PUT", "http://$ip:10000/" +
                                            "upload?path=applications/$appName/src/assets/$assetFileName&temporary=false",
                                        null, null, assetFilePath)
                                }
                            }
                        }*/
                        if (launch) {
                            code = SendHttpRequest.sendRequest("OPTIONS", "http://$ip:10000/launch?" +
                                    "id=$appName&file=main.js", "application/javascript", null, null)
                            if (code == 200) {
                                val launchBody = "{\n" +
                                        "    \"debug\": false,\n" +
                                        "    \"breakOnExceptions\": false,\n" +
                                        "    \"temporary\": false,\n" +
                                        "    \"application\": {\n" +
                                        "        \"id\": \"$appName\",\n" +
                                        "        \"app\": \"applications/$appName\"\n" +
                                        "    }\n" +
                                        "}"
                                SendHttpRequest.sendRequest("POST", "http://$ip:10000/launch?" +
                                        "id=$appName&file=main.js", "application/javascript", null, launchBody)
                            }
                        }
                    }
                }
            }
        }

        if (this.okAction.isEnabled) {
            this.applyFields()
            this.close(0)
        }
    }
}