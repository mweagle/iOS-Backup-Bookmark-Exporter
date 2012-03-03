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

import java.awt.Desktop
import java.io.File

import scala.swing.Action
import scala.swing.Button
import scala.swing.Label
import scala.swing.TextComponent

import com.mweagle.bookmarksaver.export.BookmarkProgressListener
import com.mweagle.bookmarksaver.export.BookmarkSaver

import javax.swing.SwingWorker

class BookmarkSaverGuiWorker(dbPath: String, progressLabel: Label, resultLabel: TextComponent, openResultButton:Button)
  extends SwingWorker[String, String] with BookmarkSaver with BookmarkProgressListener {

  override def doInBackground(): String =
    {
      saveBookmarks()
    }

  override def process(messages: java.util.List[String]): Unit =
    {
      if (!messages.isEmpty()) {
        progressLabel.text = messages.get(messages.size - 1);
      }
    }
  override def done(): Unit =
    {
      try {
        val outputPath = get();
        val file = new File(outputPath)
        val filePath = file.getCanonicalPath()
        if (Desktop.isDesktopSupported()) {
          openResultButton.visible = true

          openResultButton.action = new Action("View")
          {
            override def apply():Unit = 
            {
              Desktop.getDesktop().browse(file.toURI())
            }
          }
        } 
        val link = "Bookmarks exported to: %s".format(filePath)
        resultLabel.text = link
      } catch {
        case t: Throwable =>
          {
        	  	resultLabel.text = "ERROR: " + t.getMessage()
          }
      }
    }
  override def saveBookmarks(): String =
    {
      this.save(dbPath, this)
    }

  def info(message: String): Unit =
    {
      publish(message)
    }
  def error(message: String, t: Option[Throwable]): Unit =
    {
      val progressMessage = "ERROR: " + message
      publish(progressMessage)
    }
}
