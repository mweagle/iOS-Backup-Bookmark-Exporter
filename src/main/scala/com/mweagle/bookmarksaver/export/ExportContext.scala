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

import java.io.Writer
import java.sql.Connection

case object ExportContext {
  def descend(parent: ExportContext, bookmarkEntry: BookmarkEntry): ExportContext =
    {
      ExportContext(parent.dbConn, bookmarkEntry, parent.writer, parent.depth + 1, parent.listener)
    }
}

case class ExportContext(dbConn: Connection, bookmark: BookmarkEntry, writer: Writer, depth: Int, listener:BookmarkProgressListener) {
  private val headerIndent =
    {
      var indent = ""
      for (i <- 1 to depth) {
        indent += "\t"
      }
      indent
    }
  private val bookmarkIndent = headerIndent + "\t"

  def writeHeader(line: String): Unit =
    {
      writer.write("%s%s\n".format(headerIndent, line))
    }
  def writeBookmarkEntry(bookmarkChild: BookmarkEntry): Unit =
    {
      writer.write(bookmarkIndent)
      writer.write("<DT><A HREF=\"%s\">%s</A>\n".format(bookmarkChild.url, bookmarkChild.title))
    }
}