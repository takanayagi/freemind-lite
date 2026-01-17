/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2000-2011 Joerg Mueller,
 * Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 * See COPYING for Details
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package tests.freemind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.print.Paper;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * @date 30.06.2011
 */
public class ToolsTests extends FreeMindTestBase {

	/**
	 * 
	 */
	private static final String UNIX_PATH_WITH_SPEACIAL_CHAR =
			TMPDIR + "/ToolsTests/downloads/Ja\u0308nstra\u00dfe 270c.pdf";
	/**
	 * 
	 */
	private static final String WINDOWS_PATH_WITH_SPECIAL_CHAR =
			"d:\\tmp\\ToolsTests\\downloads\\Ja\u0308nstra\u00dfe 270c.pdf";
	/**
	 * 
	 */
	private static final String WINDOWS_PATH_C_USERS_TMP_IM_MM = "c:\\tmp\\ToolsTests\\tmp\\im.mm";

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.freemind.FreeMindTestBase#setUp()
	 */
	@BeforeEach
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testArgsToUrlConversion() throws Exception {
		String[] args = new String[] {"/home/bla", "--quiet", "c:\\test.mm"};
		String arrayToUrls = Tools.arrayToUrls(args);
		Vector<URL> urlVector = Tools.urlStringToUrls(arrayToUrls);
		assertEquals(args.length, urlVector.size());
		for (URL urli : urlVector) {
			System.out.println(urli);
		}
	}

	@Test
	public void testRichContentConversion() throws Exception {
		String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><map version=\"0.9.0\">"
				+ "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->"
				+ "<node CREATED=\"1320424144875\" ID=\"ID_984089046\" MODIFIED=\"1320424283250\" TEXT=\"GREEK LETTERS&#x391;&#x392;&#x393;&#x394;&#x395;&#x396;&#x397;&#x398;&#x399;&#x39a;&#x39b;&#x39c;&#x39d;&#x39e;&#x39f;&#x3a0;&#x3a1;&#x3a3;&#x3a4;&#x3a5;&#x3a6;&#x3a7;&#x3a8;&#x3a9; &#x3b1;&#x3b2;&#x3b3;&#x3b4;&#x3b5;&#x3b6;&#x3b7;&#x3b8;&#x3b9;&#x3ba;&#x3bb;&#x3bc;&#x3bd;&#x3be;&#x3bf;&#x3c0;&#x3c1;&#x3c3;&#x3c4;&#x3c5;&#x3c6;&#x3c7;&#x3c8;&#x3c9; &#x3ac;&#x3ad;&#x3ae;&#x3af;&#x3cc;&#x3cd;&#x3ce;\">"
				+ "<node CREATED=\"1320424155937\" ID=\"ID_1884129484\" MODIFIED=\"1320424262562\" POSITION=\"right\">"
				+ "<richcontent TYPE=\"NODE\"><html>" + "  <head>" + "    " + "  </head>"
				+ "  <body>" + "    <p>"
				+ "      &#x391;&#x392;&#x393;&#x394;&#x395;&#x396;&#x397;&#x398;&#x399;&#x39a;&#x39b;&#x39c;&#x39d;&#x39e;&#x39f;&#x3a0;&#x3a1;&#x3a3;&#x3a4;&#x3a5;&#x3a6;&#x3a7;&#x3a8;&#x3a9;"
				+ "    </p>" + "    <p>"
				+ "      &#x3b1;&#x3b2;&#x3b3;&#x3b4;&#x3b5;&#x3b6;&#x3b7;&#x3b8;&#x3b9;&#x3ba;&#x3bb;&#x3bc;&#x3bd;&#x3be;&#x3bf;&#x3c0;&#x3c1;&#x3c3;&#x3c4;&#x3c5;&#x3c6;&#x3c7;&#x3c8;&#x3c9; &#x3ac;&#x3ad;&#x3ae;&#x3af;&#x3cc;&#x3cd;&#x3ce;"
				+ "    </p>" + "  </body>" + "</html>" + "</richcontent>" + "</node>" + "</node>"
				+ "</map>";
		Reader updateReader = Tools.getUpdateReader(new StringReader(input),
				MapAdapter.FREEMIND_VERSION_UPDATER_XSLT);
		String result = Tools.getFile(updateReader);
		result = HtmlTools.unicodeToHTMLUnicodeEntity(result, true);
		System.out.println(result);
		assertEquals(input, result.replaceAll("&#xd;$", "").trim(), "Correct conversion?");
	}

	@Test
	public void testUrlConversion() throws Exception {
		File input = new File(UNIX_PATH_WITH_SPEACIAL_CHAR);
		System.out.println("input file " + input);
		URL url = Tools.fileToUrl(input);
		String externalForm = HtmlTools.unicodeToHTMLUnicodeEntity(url.toExternalForm(), false);
		System.out.println("External form: " + externalForm);
		// convert back:
		String unescapeHTMLUnicodeEntity = HtmlTools.unescapeHTMLUnicodeEntity(externalForm);
		File urlToFile = new File(new URI(unescapeHTMLUnicodeEntity));
		assertEquals(input.getAbsolutePath(), urlToFile.getAbsolutePath(),
				"Forth and back should give the same");

	}

	/**
	 * To be tested under windows
	 */
	@Test
	public void testRelativeUrlsWindows() throws Exception {
		if (Tools.isWindows()) {
			String pathname = WINDOWS_PATH_WITH_SPECIAL_CHAR;
			File input = new File(pathname);
			String expected =
					"../../../../d:/tmp/ToolsTests/downloads/Ja\u0308nstra\u00dfe%20270c.pdf";
			File mapFile = new File(WINDOWS_PATH_C_USERS_TMP_IM_MM);
			testCorrectRelativism(input, expected, mapFile);
		}
	}

	@Test
	public void testGetPrefix() throws Exception {
		if (Tools.isWindows()) {
			assertEquals("c:\\", Tools.getPrefix(WINDOWS_PATH_C_USERS_TMP_IM_MM));
		} else {
			assertEquals("/", Tools.getPrefix(UNIX_PATH_WITH_SPEACIAL_CHAR));

		}
	}

	@Test
	public void testRelativeUrls() throws Exception {
		File input = new File(UNIX_PATH_WITH_SPEACIAL_CHAR);
		String expected = "../downloads/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File(TMPDIR + "/ToolsTests/tmp/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	@Test
	public void testRelativeUrls2() throws Exception {
		File input =
				new File(TMPDIR + "/ToolsTests/downloads/subdir1/subdir2/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "../downloads/subdir1/subdir2/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File(TMPDIR + "/ToolsTests/tmp/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	@Test
	public void testRelativeUrls3() throws Exception {
		File input = new File(UNIX_PATH_WITH_SPEACIAL_CHAR);
		String expected = "../../../downloads/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File(TMPDIR + "/ToolsTests/tmp/subdir1/subdir2/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	@Test
	public void testRelativeUrls4() throws Exception {
		File input = new File(UNIX_PATH_WITH_SPEACIAL_CHAR);
		String expected = "Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File(TMPDIR + "/ToolsTests/downloads/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	@Test
	public void testRelativeUrlsSpaces() throws Exception {
		File input = new File(
				TMPDIR + "/ToolsTests/downloads/subd ir1/subdi r2/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "../downloads/subd%20ir1/subdi%20r2/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File(TMPDIR + "/ToolsTests/tmp/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	protected void testCorrectRelativism(File input, String expected, File mapFile)
			throws URISyntaxException, MalformedURLException {
		String relative = Tools.fileToRelativeUrlString(input, mapFile);
		assertEquals(expected, relative, "Correct relative result?");
		URL u = Tools.fileToUrl(mapFile).toURI().resolve(relative).toURL();
		URL e = Tools.fileToUrl(input);
		assertEquals(e.toExternalForm(), u.toExternalForm(), "Correct absolute  result?");
	}

	@Test
	public void testOccurrences() throws Exception {
		assertEquals(5, Tools.countOccurrences("abababaa", "a"), "Correct amount");
		assertEquals(3, Tools.countOccurrences("abababaa", "ab"), "Correct amount");
	}

	@Test
	public void testUpdate() throws IOException {
		doUpdate();
	}

	protected void doUpdate() throws IOException {
		String input = "<map version=\"0.9.0\">"
				+ "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->"
				+ "<node CREATED=\"1337970913625\" ID=\"ID_1753131052\" MODIFIED=\"1337970913625\" TEXT=\"Neue Mindmap\"/>"
				+ "</map>";
		Reader updateReader = Tools.getUpdateReader(new StringReader(input),
				MapAdapter.FREEMIND_VERSION_UPDATER_XSLT);
		String output = Tools.getFile(updateReader);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + input, output.trim(),
				"Correct output?");
	}

	@Test
	public void testPageFormatStorage() throws Exception {
		Paper paper = new Paper();
		paper.setImageableArea(1d, 2d, 3d, 4d);
		paper.setSize(5d, 6d);
		String pageFormatAsString = Tools.getPageFormatAsString(paper);
		Paper paper2 = new Paper();
		Tools.setPageFormatFromString(paper2, pageFormatAsString);
		assertEquals(paper.getHeight(), paper2.getHeight(), 0d);
		assertEquals(paper.getWidth(), paper2.getWidth(), 0d);
		assertEquals(paper.getImageableHeight(), paper2.getImageableHeight(), 0d);
		assertEquals(paper.getImageableWidth(), paper2.getImageableWidth(), 0d);
		assertEquals(paper.getImageableX(), paper2.getImageableX(), 0d);
		assertEquals(paper.getImageableY(), paper2.getImageableY(), 0d);
	}

	private static class A {

	}

	private class B extends A {

	}

	private boolean visit(A pA) {
		return true;
	}

	private boolean visit(B pA) {
		return false;
	}

	/**
	 * 
	 */
	@Test
	public void testVisitor() {
		A a = new A();
		B b = new B();
		A castedB = b;
		assertTrue(visit(a));
		assertFalse(visit(b));
		/*
		 * I don't understand this: no polymorphism is applied to the call of visit in this case, as
		 * the visit(a) method is called, although, a is of class B! If anybody can explain this to
		 * me, please write an email to me (chris).
		 */
		assertTrue(visit(castedB));
	}

	@Test
	public void testKeyDocumentationPathConversion() throws Exception {
		String file =
				"c:\\home\\java\\freemind\\0_9_0\\bin\\dist\\doc/FM_Key_Mappings_Quick_Guide.pdf";
		System.out.println(new File(Tools.fileToUrl(new File(file)).toURI()));
	}

	@Test
	public void testChangedProperties() throws Exception {
		Properties def = new Properties();
		Properties changed = new Properties();
		String key = "blabla";
		String key2 = "notexistent";
		String key3 = "notpresentindef";
		String key4 = "equal";
		def.put(key, "A");
		changed.put(key, "B");
		def.put(key4, "A");
		changed.put(key4, "A");
		def.put(key2, "default");
		changed.put(key3, "new value");
		Properties copy = Tools.copyChangedProperties(changed, def);
		assertEquals(2, copy.size());
	}

	@Test
	public void testNumberRegexp() throws Exception {
		assertTrue("1,2345".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertFalse("a1,2345".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertFalse("1-,2345".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertFalse("1,23+45".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertTrue("+1,2345".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertTrue("-1,2345".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertFalse("+".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));
		assertFalse("-".matches(MindMapController.REGEXP_FOR_NUMBERS_IN_STRINGS));

	}

	@Test
	public void testMakeFileHidden() throws Exception {
		File tempFile = File.createTempFile("hidden", "now");
		Tools.makeFileHidden(tempFile, true);
	}

	@Test
	public void testHiddenNonExistingFile() throws Exception {
		File tempFile = File.createTempFile("hidden", "now");
		File nonExisting = new File(tempFile.getAbsolutePath() + ".notexisting");
		Tools.makeFileHidden(nonExisting, true);

	}
}
