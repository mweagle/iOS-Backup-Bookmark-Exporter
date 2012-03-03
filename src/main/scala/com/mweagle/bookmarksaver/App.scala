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
package com.mweagle.bookmarksaver

import com.mweagle.bookmarksaver.console.BookmarkSaverTerminal
import com.mweagle.bookmarksaver.gui.BookmarkSaverGui

import javax.swing.UIManager

object App {

  def main(args: Array[String]) {
    if (args.isEmpty)
    {
	    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    val gui = new BookmarkSaverGui();
	    gui.main(args);      
    }
    else
    {
      val logger = 
      args foreach {
        (dbFile:String) => 
           new BookmarkSaverTerminal(dbFile).saveBookmarks()
        }
    }
  }
}
