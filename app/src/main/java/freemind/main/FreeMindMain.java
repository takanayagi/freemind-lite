/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2000-2001 Joerg Mueller
 * <joergmueller@bigfoot.com> See COPYING for Details
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
/* $Id: FreeMindMain.java,v 1.12.14.5.2.12 2008/07/17 19:16:33 christianfoltin Exp $ */

package freemind.main;

import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import freemind.controller.Controller;
import freemind.controller.MenuBar;
import freemind.view.mindmapview.MapView;

public interface FreeMindMain {
	interface StartupDoneListener {
		void startupDone();
	}

	JFrame getJFrame();

	boolean isApplet();

	MapView getView();

	void setView(MapView view);

	Controller getController();

	void setWaitingCursor(boolean waiting);

	File getPatternsFile();

	MenuBar getFreeMindMenuBar();

	/** Returns the ResourceBundle with the current language */
	ResourceBundle getResources();

	String getResourceString(String key);

	String getResourceString(String key, String defaultResource);

	Container getContentPane();

	void out(String msg);

	void err(String msg);

	/**
	 * Open url in WWW browser. This method hides some differences between operating systems.
	 */
	void openDocument(URL location) throws Exception;

	/** remove this! */
	void repaint();

	URL getResource(String name);

	int getIntProperty(String key, int defaultValue);

	/** @return returns the list of all properties. */
	Properties getProperties();

	/**
	 * Properties are stored in freemind.properties (internally) and ~/.freemind/auto.properties for
	 * user changed values. This method returns the user value (if changed) or the original.
	 * 
	 * @param key The property key as specified in freemind.properties
	 * @return the value of the property or null, if not found.
	 */
	String getProperty(String key);

	void setProperty(String key, String value);

	void saveProperties(boolean pIsShutdown);

	/**
	 * Returns the path to the directory the freemind auto properties are in, or null, if not
	 * present.
	 */
	String getFreemindDirectory();

	JLayeredPane getLayeredPane();

	void setTitle(String title);

	// to keep last win size (PN)
	int getWinHeight();

	int getWinWidth();

	int getWinState();

	int getWinX();

	int getWinY();

	String ENABLE_NODE_MOVEMENT = "enable_node_movement";

	class VersionInformation {
		enum VersionType {
			Alpha,
			Beta,
			RC,
			RELEASE
		}

		private final int mMaj;
		private final int mMid;
		private final int mMin;
		private final VersionType mType;
		private final int mNum;

		/**
		 * Sets the version number from a string.
		 * 
		 * @param pString : The version number coding. Example "0.9.0 Beta 1" Keywords are "Alpha",
		 *        "Beta", "RC". Separation by " " or by ".".
		 */
		public VersionInformation(String pString) {
			String[] info = pString.split("[. ]+");
			if (info.length != 3 && info.length != 5)
				throw new IllegalArgumentException(
						"Wrong number of tokens for version information: " + pString);
			mMaj = Integer.parseInt(info[0]);
			mMid = Integer.parseInt(info[1]);
			mMin = Integer.parseInt(info[2]);
			if (info.length == 3) {
				// release.
				mType = VersionType.RELEASE;
				mNum = 0;
				return;
			}
			// here,we have info.length == 5!
			mType = VersionType.valueOf(info[3]);
			mNum = Integer.parseInt(info[4]);
		}

		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append(mMaj);
			buf.append('.');
			buf.append(mMid);
			buf.append('.');
			buf.append(mMin);
			if (mType != VersionType.RELEASE) {
				buf.append(' ').append(mType.name()).append(' ').append(mNum);
			}
			return buf.toString();
		}
	}

	/** version info: */
	VersionInformation getFreemindVersion();

	/** To obtain a logging element, ask here. */
	Logger getLogger(String forClass);

	/**
	 * Inserts a (south) component into the split pane. If the screen isn't split yet, a split pane
	 * should be created on the fly.
	 * 
	 * @param pMindMapComponent
	 * 
	 * @return the split pane in order to move the dividers.
	 */
	JSplitPane insertComponentIntoSplitPane(JComponent pMindMapComponent);

	/**
	 * Indicates that the south panel should be made invisible.
	 */
	void removeSplitPane();

	/**
	 * @return a ClassLoader derived from the standard, with freeminds base dir included.
	 */
	ClassLoader getFreeMindClassLoader();

	/**
	 * @return default ".", but on different os this differs.
	 */
	String getFreemindBaseDir();

	/**
	 * Makes it possible to have a property different for different localizations. Common example is
	 * to put keystrokes to different keys as some are better reachable than others depending on the
	 * locale.
	 */
	String getAdjustableProperty(String label);

	void setDefaultProperty(String key, String value);

	JComponent getContentComponent();

	JScrollPane getScrollPane();

	void registerStartupDoneListener(StartupDoneListener pStartupDoneListener);

	/**
	 * @return a list of all loggers. Used for example for the log file viewer.
	 */
	List<Logger> getLoggerList();
}
