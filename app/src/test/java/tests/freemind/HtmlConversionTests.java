/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2006 Christian Foltin
 * <christianfoltin@users.sourceforge.net> See COPYING for Details
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
/* $Id: HtmlConversionTests.java,v 1.1.2.16 2010/12/04 21:07:23 christianfoltin Exp $ */

package tests.freemind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.lightdev.app.shtm.SHTMLPanel;

import freemind.main.HtmlTools;
import freemind.main.HtmlTools.NodeCreator;
import freemind.main.Tools;
import freemind.main.Tools.IntHolder;
import freemind.main.XMLElement;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.actors.PasteActor;
import tests.freemind.findreplace.TestMindMapNode;

/**
 * @author foltin
 * 
 */
public class HtmlConversionTests extends FreeMindTestBase {

	/**
	 * @author foltin
	 * @date 22.12.2014
	 */
	private static final class HtmlTransfer implements Transferable {
		private DataFlavor mFlavor;
		private String mHtmlData;

		/**
		 * 
		 */
		public HtmlTransfer(String pHtmlData) {
			mHtmlData = pHtmlData;
			try {
				mFlavor = new DataFlavor("text/html; class=java.lang.String");
			} catch (ClassNotFoundException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {mFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor pFlavor) {
			return pFlavor.equals(mFlavor);
		}

		@Override
		public Object getTransferData(DataFlavor pFlavor)
				throws UnsupportedFlavorException, IOException {
			return mHtmlData;
		}
	}

	@Test
	public void testSetHtml() throws Exception {
		MindMapNodeModel node = new MindMapNodeModel(new MindMapMock("</map>"));
		node.setText("test");
		// wiped out: <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html
		// PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"
		// \"DTD/xhtml1-transitional.dtd\">\n
		assertNull(node.getXmlText(), "Should no conversion as test is not html.");
		node.setXmlText("test");

		assertEquals("test", node.getText(), "Not proper conversion");
		node.setText("<html><br>");
		assertEquals(
				"<html>\n  <head>\n    \n  </head>\n  <body>\n    <br />\n  </body>\n</html>\n",
				node.getXmlText(), "Not proper html conversion");
		// must remove the '/' in <br/>.
		node.setXmlText("<html><br/></html>");
		assertEquals("<html><br></html>", node.getText(), "Not proper html conversion");
		node.setXmlText("<html><br /></html>");
		assertEquals("<html><br ></html>", node.getText(), "Not proper html conversion");

	}

	@Test
	public void testEndContentMatcher() throws Exception {
		matchingTest("</" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">");
		matchingTest("</ " + XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">");
		matchingTest("</ " + XMLElement.XML_NODE_XHTML_CONTENT_TAG + " >");
		matchingTest("< /\n" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + " >");
	}

	/**
	 */
	private void matchingTest(String string) {
		assertTrue(string.matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
	}

	@Test
	public void testNanoXmlContent() throws Exception {
		XMLElement element = new XMLElement();
		element.parseFromReader(new StringReader("<" + XMLElement.XML_NODE_XHTML_CONTENT_TAG
				+ "><body>a<b>cd</b>e</body></" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">"));
		assertEquals("<body>a<b>cd</b>e</body>", element.getContent(),
				XMLElement.XML_NODE_XHTML_CONTENT_TAG + " tag removed");
	}

	@Test
	public void testXHtmlToHtmlConversion() throws Exception {
		assertEquals("<br >", HtmlTools.getInstance().toHtml("<br />"), "br tag conversion");
		assertEquals("<brmore/>", HtmlTools.getInstance().toHtml("<brmore/>"),
				"brmore tag not converted");
	}

	@Test
	public void testWellFormedXml() throws Exception {
		assertTrue(HtmlTools.getInstance().isWellformedXml("<a></a>"));
		// assertEquals(true,
		// HtmlTools.getInstance().isWellformedXml("<?xml version=\"1.0\"
		// encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0
		// Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">\n<a></a>"));
		assertTrue(HtmlTools.getInstance()
				.isWellformedXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a></a>"));
		assertFalse(HtmlTools.getInstance().isWellformedXml("<a><a></a>"));

	}

	@Test
	public void testBr() throws Exception {
		String input = """
				<html>
				  <head>
				   \s
				  </head>
				  <body>
				    <p>
				      asdfasdf<br />asdfasdfdasf
				    </p>
				    <p>
				      asdasdfas
				    </p>
				  </body>
				</html>""";
		String result = HtmlTools.getInstance().toHtml(input);
		assertFalse(result.matches("^.*&gt;.*$"), "Should not contain &gt; in the result.");
	}

	/**
	 * I suspected, that the toXhtml method inserts some spaces into the output, but it doesn't seem
	 * to be the case.
	 * 
	 * @throws Exception
	 */
	public void testSpaceHandling() throws Exception {
		String input = getInputStringWithManySpaces(HtmlTools.SP);
		assertEquals(input, HtmlTools.getInstance().toXhtml(input));
	}

	// public void testSpaceHandlingInShtml() throws Exception {
	// String input = getInputStringWithManySpaces(" ");
	// SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
	// panel.setCurrentDocumentContent(input);
	// assertEquals(input, panel.getDocumentText());
	// panel.setVisible(false);
	// }
	/**
	 * Set the panel to a text, read this text from the panel and set it again. Then, setting and
	 * getting this text to the panel must give the same.
	 */
	@Test
	public void testSpaceHandlingInShtmlIdempotency() throws Exception {
		if (Tools.isHeadless()) {
			return;
		}
		String input = getInputStringWithManySpaces(" ");
		SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
		panel.setCurrentDocumentContent(input);
		// set the value of the panel itself again.
		input = panel.getDocumentText();
		panel.setCurrentDocumentContent(input);
		assertEquals(input, panel.getDocumentText(),
				"Setting the input to its output should cause the same output.");
		panel.setVisible(false);
	}

	@Test
	public void testSpaceRemovalInShtml() throws Exception {
		if (Tools.isHeadless()) {
			return;
		}
		String input = getInputStringWithManySpaces(HtmlTools.SP);
		SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
		panel.setCurrentDocumentContent(input);
		// set the value of the panel itself again (twice)
		input = panel.getDocumentText();
		panel.setCurrentDocumentContent(input);
		input = panel.getDocumentText();
		panel.setCurrentDocumentContent(input);
		assertEquals(input, panel.getDocumentText(),
				"Setting the input to its output should cause the same output.");
		panel.setVisible(false);
	}

	private String getInputStringWithManySpaces(String pSpaceString) {
		String body = getHtmlBody(pSpaceString);
		return "<html>\n" + "  <head>\n" + "    \n" + "  </head>\n" + "  <body>" + body
				+ "</body>\n" + "</html>\n";
	}

	private String getHtmlBody(String pSpaceString) {
		return "\n    <p>\n" + "      Using" + pSpaceString + "Filters" + pSpaceString
				+ "the" + pSpaceString + "current" + pSpaceString + "mindmap" + pSpaceString + "can"
				+ pSpaceString + "be" + pSpaceString + "reduced" + pSpaceString + "to"
				+ pSpaceString + "nodes" + pSpaceString + "satisfying" + pSpaceString + "certain"
				+ pSpaceString + "criteria." + pSpaceString + "For" + pSpaceString + "example,"
				+ pSpaceString + "if" + pSpaceString + "you" + pSpaceString + "only" + pSpaceString
				+ "want" + pSpaceString + "to" + pSpaceString + "see" + pSpaceString + "every"
				+ pSpaceString + "node" + pSpaceString + "containing" + pSpaceString
				+ "&quot;TODO&quot;," + pSpaceString + "then" + pSpaceString + "you" + pSpaceString
				+ "have" + pSpaceString + "to" + pSpaceString + "press" + pSpaceString + "on"
				+ pSpaceString + "the" + pSpaceString + "filter" + pSpaceString + "symbol"
				+ pSpaceString + "(the" + pSpaceString + "funnel" + pSpaceString + "beside"
				+ pSpaceString + "the" + pSpaceString + "zoom" + pSpaceString + "box),"
				+ pSpaceString + "the" + pSpaceString + "filter" + pSpaceString + "toolbar"
				+ pSpaceString + "appears," + pSpaceString + "choose" + pSpaceString
				+ "&quot;edit&quot;" + pSpaceString + "and" + pSpaceString + "add" + pSpaceString
				+ "the" + pSpaceString + "condition" + pSpaceString + "that" + pSpaceString + "the"
				+ pSpaceString + "node" + pSpaceString + "content" + pSpaceString + "contains"
				+ pSpaceString + "&quot;TODO&quot;." + pSpaceString + "Then" + pSpaceString
				+ "select" + pSpaceString + "the" + pSpaceString + "filter" + pSpaceString + "in"
				+ pSpaceString + "the" + pSpaceString + "filter" + pSpaceString + "toolbar."
				+ pSpaceString + "Now," + pSpaceString + "only" + pSpaceString + "the"
				+ pSpaceString + "filtered" + pSpaceString + "nodes" + pSpaceString + "and"
				+ pSpaceString + "its" + pSpaceString + "ancestors" + pSpaceString + "are"
				+ pSpaceString + "displayed" + pSpaceString + "unless" + pSpaceString + "you"
				+ pSpaceString + "choose" + pSpaceString + "&quot;No" + pSpaceString
				+ "filtering&quot;" + pSpaceString + "in" + pSpaceString + "the" + pSpaceString
				+ "toolbar." + pSpaceString + "\n" + "    </p>\n" + "    <p>\n" + "      Using"
				+ pSpaceString + "the" + pSpaceString + "settings" + pSpaceString + "&quot;Show"
				+ pSpaceString + "ancestors&quot;" + pSpaceString + "and" + pSpaceString
				+ "&quot;Show" + pSpaceString + "descendants&quot;" + pSpaceString + "you"
				+ pSpaceString + "can" + pSpaceString + "influence" + pSpaceString + "the"
				+ pSpaceString + "apperance" + pSpaceString + "of" + pSpaceString + "the"
				+ pSpaceString + "parent" + pSpaceString + "and" + pSpaceString + "child"
				+ pSpaceString + "nodes" + pSpaceString + "that" + pSpaceString + "are"
				+ pSpaceString + "connected" + pSpaceString + "with" + pSpaceString + "the"
				+ pSpaceString + "nodes" + pSpaceString + "being" + pSpaceString + "filtered.\n"
				+ "    </p>\n" + "    <p>\n" + "      There" + pSpaceString + "are" + pSpaceString
				+ "many" + pSpaceString + "different" + pSpaceString + "criteria" + pSpaceString
				+ "filters" + pSpaceString + "can" + pSpaceString + "be" + pSpaceString + "based"
				+ pSpaceString + "on" + pSpaceString + "such" + pSpaceString + "as" + pSpaceString
				+ "a" + pSpaceString + "set" + pSpaceString + "of" + pSpaceString + "selected"
				+ pSpaceString + "nodes," + pSpaceString + "a" + pSpaceString + "specific"
				+ pSpaceString + "icon" + pSpaceString + "and" + pSpaceString + "some"
				+ pSpaceString + "attributes.\n" + "    </p>\n" + "    <p>\n" + "      "
				+ pSpaceString + "\n" + "    </p>\n  ";
	}

	@Test
	public void testUnicodeHandling() {
		String input =
				"if (myOldValue != null && myText.startsWith(myOldValue) == true) { \nmyText = myText.substring(myOldValue.length() + terminator.length());\n};\n";
		String escapedText = HtmlTools.toXMLEscapedText(input);
		String unicodeToHTMLUnicodeEntity =
				HtmlTools.unicodeToHTMLUnicodeEntity(escapedText, false);
		System.out.println(unicodeToHTMLUnicodeEntity);
		String unescapeHTMLUnicodeEntity =
				HtmlTools.unescapeHTMLUnicodeEntity(unicodeToHTMLUnicodeEntity);
		String back = HtmlTools.toXMLUnescapedText(unescapeHTMLUnicodeEntity);
		System.out.println(back);
		assertEquals(input, back, "Unicode handling failed.");
	}

	@Test
	public void testHtmlBodyExtraction() {
		String input = getInputStringWithManySpaces(" ");
		String expectedOutput = getHtmlBody(" ");
		assertTrue(HtmlTools.isHtmlNode(input));
		assertEquals(expectedOutput, HtmlTools.extractHtmlBody(input));
	}

	@Test
	public void testIllegalXmlChars() throws Exception {
		assertEquals("AB&#32;&#x20;",
				Tools.replaceUtf8AndIllegalXmlChars(
						"&#x1f;A&#0;&#31;&#x0001B;B&#x1;&#32;&#1;&#x20;"),
				"Wrong chars remaining after replacement.");
	}

	@Test
	public void testSpaceReplacements() throws Exception {
		assertEquals(" " + HtmlTools.NBSP, HtmlTools.replaceSpacesToNonbreakableSpaces("  "),
				"Space conversion failed");
		assertEquals(" " + HtmlTools.NBSP + HtmlTools.NBSP + HtmlTools.NBSP,
				HtmlTools.replaceSpacesToNonbreakableSpaces("    "),
				"Multiple space conversion failed");
		assertEquals(" " + HtmlTools.NBSP + "xy " + HtmlTools.NBSP + HtmlTools.NBSP,
				HtmlTools.replaceSpacesToNonbreakableSpaces("  xy   "),
				"Double space conversion failed");
	}

	static final String testHtml1 = "<ul><li>bla</li></ul>";
	final static String testHtml2 =
			"""
					<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
					<html>
					<head>
						<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
						<title></title>
						<meta name="generator" content="LibreOffice 4.3.3.2 (Linux)"/>
						<meta name="author" content="Christian Foltin"/>
						<meta name="created" content="2014-11-28T21:27:45.329992028"/>
						<meta name="changedby" content="Christian Foltin"/>
						<meta name="changed" content="2014-11-28T21:28:10.435205408"/>
						<style type="text/css">
							@page { margin: 2cm }
							p { margin-bottom: 0.25cm; line-height: 120% }
						</style>
					</head>
					<body lang="de-DE" dir="ltr">
					<ul>
						<li/>
					<p style="margin-bottom: 0cm; line-height: 100%">Bla</p>
						<ul>
							<li/>
					<p style="margin-bottom: 0cm; line-height: 100%">blubber</p>
							<li/>
					<p style="margin-bottom: 0cm; line-height: 100%">zweite ebene</p>
							<ul>
								<li/>
					<p style="margin-bottom: 0cm; line-height: 100%">dritte
								ebene</p>
							</ul>
						</ul>
						<li/>
					<p style="margin-bottom: 0cm; line-height: 100%">1. ebene</p>
					</ul>
					</body>
					</html>""";
	static final String testHtml3 = """
			<html>
			<head>
			<title>FreeMind Import</title>
			<link rel="important stylesheet" href="chrome://messagebody/skin/messageBody.css">
			</head>
			<body>
			<table border=0 cellspacing=0 cellpadding=0 width="100%" class="header-part1"><tr><td><b>Betreff: </b>FreeMind Import</td></tr><tr><td><b>Von: </b>xxxxxxxxx yyyyyyy &lt;xxxxxxx.yyyyy@abc.de&gt;</td></tr><tr><td><b>Datum: </b>28.11.14 08:55</td></tr></table><table border=0 cellspacing=0 cellpadding=0 width="100%" class="header-part2"><tr><td><b>An: </b>&quot;xxxxxxxxx.yyyyyy@def.de&quot; &lt;xxxxxx.yyyyy@def.de&gt;</td></tr></table><br>
			<html xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:w="urn:schemas-microsoft-com:office:word" xmlns:m="http://schemas.microsoft.com/office/2004/12/omml" xmlns="http://www.w3.org/TR/REC-html40">
			<head>
			<meta http-equiv="Content-Type" content="text/html; ">
			<meta name="Generator" content="Microsoft Word 14 (filtered medium)">
			<style><!--
			/* Font Definitions */
			@font-face
				{font-family:Wingdings;
				panose-1:5 0 0 0 0 0 0 0 0 0;}
			@font-face
				{font-family:Wingdings;
				panose-1:5 0 0 0 0 0 0 0 0 0;}
			@font-face
				{font-family:Calibri;
				panose-1:2 15 5 2 2 2 4 3 2 4;}
			/* Style Definitions */
			p.MsoNormal, li.MsoNormal, div.MsoNormal
				{margin:0cm;
				margin-bottom:.0001pt;
				font-size:11.0pt;
				font-family:"Calibri","sans-serif";
				mso-fareast-language:EN-US;}
			a:link, span.MsoHyperlink
				{mso-style-priority:99;
				color:blue;
				text-decoration:underline;}
			a:visited, span.MsoHyperlinkFollowed
				{mso-style-priority:99;
				color:purple;
				text-decoration:underline;}
			p.MsoListParagraph, li.MsoListParagraph, div.MsoListParagraph
				{mso-style-priority:34;
				margin-top:0cm;
				margin-right:0cm;
				margin-bottom:0cm;
				margin-left:36.0pt;
				margin-bottom:.0001pt;
				font-size:11.0pt;
				font-family:"Calibri","sans-serif";
				mso-fareast-language:EN-US;}
			span.E-MailFormatvorlage17
				{mso-style-type:personal-compose;
				font-family:"Arial","sans-serif";
				color:windowtext;}
			.MsoChpDefault
				{mso-style-type:export-only;
				font-family:"Calibri","sans-serif";
				mso-fareast-language:EN-US;}
			@page WordSection1
				{size:612.0pt 792.0pt;
				margin:72.0pt 72.0pt 72.0pt 72.0pt;}
			div.WordSection1
				{page:WordSection1;}
			/* List Definitions */
			@list l0
				{mso-list-id:1096172370;
				mso-list-type:hybrid;
				mso-list-template-ids:-104571736 2049105486 67567619 67567621 67567617 67567619 67567621 67567617 67567619 67567621;}
			@list l0:level1
				{mso-level-start-at:0;
				mso-level-number-format:bullet;
				mso-level-text:\\F0B7;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:Symbol;
				mso-fareast-font-family:Calibri;
				mso-bidi-font-family:"Times New Roman";}
			@list l0:level2
				{mso-level-number-format:bullet;
				mso-level-text:o;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:"Courier New";}
			@list l0:level3
				{mso-level-number-format:bullet;
				mso-level-text:\\F0A7;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:Wingdings;}
			@list l0:level4
				{mso-level-number-format:bullet;
				mso-level-text:\\F0B7;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:Symbol;}
			@list l0:level5
				{mso-level-number-format:bullet;
				mso-level-text:o;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:"Courier New";}
			@list l0:level6
				{mso-level-number-format:bullet;
				mso-level-text:\\F0A7;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:Wingdings;}
			@list l0:level7
				{mso-level-number-format:bullet;
				mso-level-text:\\F0B7;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:Symbol;}
			@list l0:level8
				{mso-level-number-format:bullet;
				mso-level-text:o;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:"Courier New";}
			@list l0:level9
				{mso-level-number-format:bullet;
				mso-level-text:\\F0A7;
				mso-level-tab-stop:none;
				mso-level-number-position:left;
				text-indent:-18.0pt;
				font-family:Wingdings;}
			ol
				{margin-bottom:0cm;}
			ul
				{margin-bottom:0cm;}
			--></style><!--[if gte mso 9]><xml>
			<o:shapedefaults v:ext="edit" spidmax="1026" />
			</xml><![endif]--><!--[if gte mso 9]><xml>
			<o:shapelayout v:ext="edit">
			<o:idmap v:ext="edit" data="1" />
			</o:shapelayout></xml><![endif]-->
			</head>
			<body lang="DE" link="blue" vlink="purple">
			<div class="WordSection1">
			<p class="MsoListParagraph" style="text-indent:-18.0pt;mso-list:l0 level1 lfo1"><![if !supportLists]><span style="font-family:Symbol"><span style="mso-list:Ignore">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</span></span></span><![endif]><span style="font-family:&quot;Arial&quot;,&quot;sans-serif&quot;">Bla<o:p></o:p></span></p>
			<p class="MsoListParagraph" style="margin-left:72.0pt;text-indent:-18.0pt;mso-list:l0 level2 lfo1">
			<![if !supportLists]><span style="font-family:&quot;Courier New&quot;"><span style="mso-list:Ignore">o<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;
			</span></span></span><![endif]><span style="font-family:&quot;Arial&quot;,&quot;sans-serif&quot;">Unterpunkt<o:p></o:p></span></p>
			<p class="MsoListParagraph" style="margin-left:72.0pt;text-indent:-18.0pt;mso-list:l0 level2 lfo1">
			<![if !supportLists]><span style="font-family:&quot;Courier New&quot;"><span style="mso-list:Ignore">o<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;
			</span></span></span><![endif]><span style="font-family:&quot;Arial&quot;,&quot;sans-serif&quot;">Zweiter Unterpunkt<o:p></o:p></span></p>
			<p class="MsoListParagraph" style="margin-left:108.0pt;text-indent:-18.0pt;mso-list:l0 level3 lfo1">
			<![if !supportLists]><span style="font-family:Wingdings"><span style="mso-list:Ignore">§<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;
			</span></span></span><![endif]><span style="font-family:&quot;Arial&quot;,&quot;sans-serif&quot;">Dritte Ebene<o:p></o:p></span></p>
			<p class="MsoListParagraph" style="text-indent:-18.0pt;mso-list:l0 level1 lfo1"><![if !supportLists]><span style="font-family:Symbol"><span style="mso-list:Ignore">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</span></span></span><![endif]><span style="font-family:&quot;Arial&quot;,&quot;sans-serif&quot;">Hauptpunkt<o:p></o:p></span></p>
			<p class="MsoNormal"><span style="font-family:&quot;Arial&quot;,&quot;sans-serif&quot;"><o:p>&nbsp;</o:p></span></p>
			<p class="MsoNormal"><span lang="EN-US" style="mso-fareast-language:DE">Viele Grüße/Best regards<o:p></o:p></span></p>
			<p class="MsoNormal"><span lang="EN-US" style="mso-fareast-language:DE"><o:p>&nbsp;</o:p></span></p>
			<p class="MsoNormal"><span lang="EN-US" style="mso-fareast-language:DE">XXXX YYYYYYY<o:p></o:p></span></p>
			<p class="MsoNormal"><span style="mso-fareast-language:DE">(-007)<o:p></o:p></span></p>
			<p class="MsoNormal"><o:p>&nbsp;</o:p></p>
			</div>
			</body>
			</html>
			
			</body>
			</html>
			""";

	@Test
	public void testListDetection() throws Exception {
		HtmlTools instance = HtmlTools.getInstance();
		final IntHolder created = new IntHolder();

		// new NodeTraversor(new NodeVisitor() {
		//
		// @Override
		// public void head(Node pNode, int pDepth) {
		// System.out.println("Node: " + pNode.getClass() + ", " + pNode);
		// }
		//
		// @Override
		// public void tail(Node pNode, int pDepth) {
		// System.out.println("/Node: " + pNode.getClass() + ", " + pNode);
		//
		// }}).traverse(Jsoup.parse(testHtml2));

		MindMapNode rootNode = new TestMindMapNode();
		rootNode.setText("myRoot");
		NodeCreator creator = new NodeCreator() {

			@Override
			public MindMapNode createChild(MindMapNode pParent) {
				created.increase();
				System.out.println("Create new node as child of: " + pParent.getText());
				TestMindMapNode newNode = new TestMindMapNode();
				pParent.insert(newNode, pParent.getChildCount());
				newNode.setParent(pParent);
				return newNode;
			}

			@Override
			public void setText(String pText, MindMapNode pNode) {
				System.out.println("Text: " + pText);
				pNode.setText(pText);
			}

			@Override
			public void setLink(String pLink, MindMapNode pNode) {}

		};
		instance.insertHtmlIntoNodes(testHtml1, rootNode, creator);

		assertEquals(1, created.getValue());
		assertEquals(1, rootNode.getChildCount(), "Only one in the first level is expected");
		created.setValue(0);
		rootNode = new TestMindMapNode();
		rootNode.setText("myRoot2");
		instance.insertHtmlIntoNodes(testHtml2, rootNode, creator);

		assertEquals(5, created.getValue());
		assertEquals(2, rootNode.getChildCount(), "Only two in the first level are expected");

		created.setValue(0);
		rootNode = new TestMindMapNode();
		rootNode.setText("myRoot3");
		instance.insertHtmlIntoNodes(testHtml3, rootNode, creator);
		assertEquals(13, created.getValue());
		assertEquals(10, rootNode.getChildCount(), "first level nodes");
	}

	@Test
	public void testDetermineNodeAmount() throws Exception {
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
		final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
		mapFeedback.setMap(mMap);
		PasteActor actor = new PasteActor(mapFeedback);
		assertEquals(1, actor.determineAmountOfNewNodes(new HtmlTransfer(testHtml1)));
		assertEquals(2, actor.determineAmountOfNewNodes(new HtmlTransfer(testHtml2)));
		// this is one less, as the determine... strips the html header and uses its
		// own.
		assertEquals(9, actor.determineAmountOfNewNodes(new HtmlTransfer(testHtml3)));
	}

}
