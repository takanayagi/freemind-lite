/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2005   Christian Foltin.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 12.06.2005
 */

To build FreeMind from the sources, JDK must be installed.
The version of JDK to be used for build is specified in build.gradle,
so please change the following line, which currently specifies JDK-17, to match your environment.

build.gradle:L20
            languageVersion = JavaLanguageVersion.of(17)


Use the gradle wrapper script to run gradle. (No need to install gradle manually)

./gradlew clean build
	- Compile the source, run the test, and make an archive for distribution.
	  (this will also download dependencies if needed)

./gradlew :app:run
	- to start FreeMind from the sources.


To import into Eclipse, run `./gradlew :app:compileXsd eclipse` first,
then select 'Existing Gradle Project' in the import wizard,
and specify the JDK-17 installation location for 'Java home'
in the 'Import Options' window.
