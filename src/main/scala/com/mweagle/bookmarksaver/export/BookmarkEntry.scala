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

import java.sql.ResultSet

object BookmarkEntry {
  def apply(row: ResultSet): BookmarkEntry =
    {
      val id = row.getInt("id")
      val parentId = row.getString("parent")
      val title = row.getString("title")
      val url = row.getString("url")
      new BookmarkEntry(id, parentId, title, url)
    }
}

case class BookmarkEntry(id: Int, private val _parentId: String, title: String, url: String) {
  val parentId: Option[Int] = if (null == _parentId || _parentId.isEmpty()) None else Some(_parentId.toInt)
}