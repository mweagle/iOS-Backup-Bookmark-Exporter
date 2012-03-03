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
package com.mweagle.bookmarksaver.export

import java.io.File
import java.io.OutputStreamWriter
import java.io.Writer
import java.sql.Connection
import java.sql.DriverManager
import scala.collection.mutable.Set
import scala.collection.mutable.HashMap
import javax.swing.SwingWorker
import java.text.SimpleDateFormat
import java.util.Date

trait BookmarkSaver {
  def saveBookmarks(): String

  protected def save(dbFilepath: String, listener: BookmarkProgressListener): String =
    {
      Class.forName("org.sqlite.JDBC");
      var dbConnection: Option[Connection] = None
      try {
        listener.info("------------------------------------------------------------------------")
        listener.info("Exporting bookmarks for file: " + dbFilepath)

        dbConnection = Some(DriverManager.getConnection("jdbc:sqlite:%s".format(dbFilepath)))
        val rootBookmark = findBookmarkRootItem(dbConnection.get)

        // We don't ever write the root object...
        val outputPath = new File(dbFilepath + ".html")
        val writer = createBookmarkOutputFile(outputPath)
        writeBookmarkExportHeader(writer, dbFilepath + ".html")

        var totalCount = 0;
        for {
          dbConn <- dbConnection;
          root <- rootBookmark
        } {
          val exportContext = ExportContext(dbConn, root, writer, 1, listener)
          totalCount = writeBookmarks(exportContext)
        }
        writeBookmarkExportFooter(writer)
        writer.close()
        listener.info("Completed export to: " + outputPath)
        listener.info("Number of exported bookmarks: " + totalCount)
        outputPath.getCanonicalPath()
      } finally {
        dbConnection foreach (_ close)
      }
    }

  private def writeBookmarks(context: ExportContext): Int =
    {
      var exportCount = 0

      // Do the work...
      context.listener.info("Exporting: " + context.bookmark.title)
      context.writeHeader("<DT><H3 FOLDED>%s</H3>".format(context.bookmark.title))
      context.writeHeader("<DL><p>")
      /**
       * All node children...
       */
      val statement = context.dbConn.createStatement()
      val leafChildrenQuery = "SELECT * FROM bookmarks WHERE type==0 AND parent=%d".format(context.bookmark.id)
      val bookmarks = statement.executeQuery(leafChildrenQuery)
      while (bookmarks.next()) {
        context.writeBookmarkEntry(BookmarkEntry(bookmarks))
        exportCount += 1
      }
      statement.close()
      bookmarks.close()
      /**
       * And the leafs...
       */
      val childStatement = context.dbConn.createStatement()
      val nodeChildrenQuery = "SELECT * FROM bookmarks WHERE type==1 AND parent=%d".format(context.bookmark.id)
      val childBookmarks = childStatement.executeQuery(nodeChildrenQuery)
      while (childBookmarks.next()) {
        val childFolder = BookmarkEntry(childBookmarks)
        exportCount += writeBookmarks(ExportContext.descend(context, childFolder))
      }
      childBookmarks.close()
      childStatement.close()
      context.writeHeader("</DL><p>")

      exportCount
    }

  private def findBookmarkRootItem(dbConn: Connection): Option[BookmarkEntry] =
    {
      // Select all the folders from the bookmarks table
      var rootBookmarkFolder: Option[BookmarkEntry] = None

      val statement = dbConn.createStatement()
      val resultSet = statement.executeQuery("SELECT * FROM bookmarks WHERE type==1")
      while (resultSet.next()) {
        val bookmarkFolder = BookmarkEntry(resultSet)
        if (!bookmarkFolder.parentId.isDefined) {
          require(!rootBookmarkFolder.isDefined)
          rootBookmarkFolder = Some(bookmarkFolder)
        }
      }
      resultSet.close()
      rootBookmarkFolder
    }

  private def createBookmarkOutputFile(outputFilepath: File): Writer =
    {
      val outputStream = new java.io.FileOutputStream(outputFilepath)
      val outputWriter = new OutputStreamWriter(outputStream, "UTF-8")
      outputWriter
    }

  private def writeBookmarkExportHeader(writer: Writer, dbFilepath:String) =
    {
    val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    val currentTime = dateFormat.format(new Date())
    
      writer.write("""<!DOCTYPE NETSCAPE-Bookmark-file-1>
     <!-- 
          iOS saved bookmarks from BookmarkSaver (http://github.com/mweagle)
          Generated: %s
          Source: %s
     -->
     <HTML>
    		  <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
    		  <Title>Exported bookmarks for: %s</Title>
    		  <H1>Bookmarks</H1>
""".format(currentTime, dbFilepath, dbFilepath))
    }
  private def writeBookmarkExportFooter(writer: Writer) =
    {
      writer.write("""</HTML>""")
    }
}
