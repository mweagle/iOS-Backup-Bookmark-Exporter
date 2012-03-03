iOS Backup Bookmark Exporter
================================

While migrating a faulty iPad to a new replacement, I accidentally "misplaced" some data.  Specifically some 700+ bookmarks that had been carefully curated over more than a year.  I had assumed iCloud would fix this problem, but you know the old story about making assumptions...

Solution
------------------------------- 
Create a tool that exports the iOS [Sqlite](http://www.sqlite.org/) bookmark.db file that is part of an iOS backup to the [Netscape Bookmark Format](http://msdn.microsoft.com/en-us/library/ie/aa753582\(v=vs.85\).aspx).  

It's not pretty, and gave me a new appreciation for anyone who's able to make a serviceable Java Swing UI, but it does work.

![](http://github.com/mweagle/iOS-Backup-Bookmark-Exporter/blob/master/docs/iOS Bookmark Exporter.png)

Building
------------------------------- 
1. Install Maven
2. In a terminal window:
	mvn package
3. In  _target/appassembler/bin_, there will be platform-specific shell scripts to run the UI version
4. Everything is also zipped up into _target/com.mweagle.iosBookmarkSaver-1.0-SNAPSHOT-dist.zip_


Usage
------------------------------- 
1. Download and Install the [iPhone/iPod Touch Backup Extractor](http://supercrazyawesome.com/) 
2. Find the iOS backup file you're interested in, and expand the archive
3. Launch the iOS Bookmark Exporter

### Command line ###
Via the command line, by providing a list of _bookmark.db_ files to export: 

    java -jar com.mweagle.iosBookmarkSaver-1.0-SNAPSHOT.jar /path/to/my/bookmarks.db

An HTML file that uses the original path plus ".html" will be created.  For instance _/path/to/my/bookmarks.db.html_

### GUI ###
Run the platform-appropriate _bookmarkSaver_ shell script to bring up the (admittedly ugly) UI.

