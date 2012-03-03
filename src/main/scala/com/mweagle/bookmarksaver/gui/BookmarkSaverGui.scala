/*
   Copyright 2012 Matt Weagle (mweagle@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.mweagle.bookmarksaver.gui

import java.io.File
import scala.swing.event.ButtonClicked
import scala.swing.event.ValueChanged
import scala.swing.Action
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.FileChooser
import scala.swing.FlowPanel
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.SimpleSwingApplication
import scala.swing.Swing
import scala.swing.TextField
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.Icon
import javax.swing.ImageIcon
import java.awt.GridBagLayout
import scala.swing.GridBagPanel


class BookmarkSaverGui extends SimpleSwingApplication {
  // ROW 1
  val lblChooser = new Label {
    text = "<html><b>Select bookmarks.db file: </b></html>"
  }
  val txtPathField = new TextField(32) {

  }
  val btnFileChoose = new Button {
    text = "..."
  }
  val btnExportBookmarks = new Button {
    text = "Export"
    enabled = false
  }
  
  // ROW 2
  val lblOutputFilepath = new TextField {
    editable = false
    background = null
    border = null
    text = " "
  }
  
  val btnOpenFile = new Button {
    text = " View "
    visible = false
  }
  
  // ROW 3
  val lblProgress = new Label {
    text = ""
    visible = true
  }

  def top = new MainFrame {
    // Global settings
    title = "iOS Bookmark Exporter"
    centerOnScreen
    resizable = false
    contents = new BoxPanel(Orientation.Vertical) {
      // Row 1
      contents += new FlowPanel {
        contents += lblChooser += txtPathField += btnFileChoose += btnExportBookmarks
      }
      // Row 2
      contents += new FlowPanel {
        contents += lblOutputFilepath += btnOpenFile
      }
      // Row 3
      contents += new FlowPanel {
        contents += lblProgress
      }
      // Border
      border = Swing.EmptyBorder(10, 10, 30, 10)
    }
  }
  // Register event listeners
  listenTo(btnFileChoose, btnExportBookmarks, txtPathField)
  reactions += {
    case ValueChanged(this.txtPathField) =>
      {
        btnExportBookmarks.enabled = try {
          try {
            val inputFile = new File(this.txtPathField.text)
            inputFile.exists() && !inputFile.isDirectory()
          } catch {
            case _ => false
          }
        }
      }
    case ButtonClicked(this.btnFileChoose) =>
      val currentDir = new File(".");
      val fileChooser = new FileChooser(currentDir);
      val filter = new FileNameExtensionFilter("Bookmarks Database", "db");
      fileChooser.fileFilter = filter;

      val retVal = fileChooser.showOpenDialog(btnFileChoose)
      if (retVal == FileChooser.Result.Approve) {
        this.txtPathField.text = fileChooser.selectedFile.getCanonicalPath()
      }
    case ButtonClicked(this.btnExportBookmarks) =>
      {
        // Export the data in a custom task...
        btnOpenFile.visible = false
        lblOutputFilepath.text = ""
        lblProgress.text = ""
        btnOpenFile.action = Action.NoAction
        val guiWorker = new BookmarkSaverGuiWorker(this.txtPathField.text, lblProgress, lblOutputFilepath, btnOpenFile)
        guiWorker.execute()
      }
  }
}