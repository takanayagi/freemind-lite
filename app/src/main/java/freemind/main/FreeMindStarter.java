/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2000-2006 Joerg Mueller,
 * Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 *
 * Created on 06.07.2006
 */
/* $Id: FreeMindStarter.java,v 1.1.2.11 2009/03/29 19:37:23 christianfoltin Exp $ */
package freemind.main;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * 
 * @author foltin
 * 
 */
public class FreeMindStarter {

	public static void main(String[] args) {
		FreeMindStarter starter = new FreeMindStarter();
		Properties defaultPreferences = starter.readDefaultPreferences();
		starter.createUserDirectory(defaultPreferences);
		Properties userPreferences = starter.readUsersPreferences(defaultPreferences);
		starter.setDefaultLocale(userPreferences);

		// Christopher Robin Elmersson: set
		Toolkit.getDefaultToolkit();

		try {
			FreeMind.main(args, defaultPreferences, userPreferences,
					starter.getUserPreferencesFile(defaultPreferences));

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					null, "freemind.main.FreeMind can't be started: " + e.getLocalizedMessage()
							+ "\n" + Tools.getStacktrace(e),
					"Startup problem", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void createUserDirectory(Properties pDefaultProperties) {
		File userPropertiesFolder = new File(getFreeMindDirectory(pDefaultProperties));
		try {
			// create user directory:
			if (!userPropertiesFolder.exists()) {
				userPropertiesFolder.mkdir();
			}
		} catch (Exception e) {
			// exception is logged to console as we don't have a logger
			e.printStackTrace();
			System.err.println("Cannot create folder for user properties and logging: '"
					+ userPropertiesFolder.getAbsolutePath() + "'");

		}
	}

	/**
	 * @param pProperties
	 */
	private void setDefaultLocale(Properties pProperties) {
		String lang = pProperties.getProperty(FreeMindCommon.RESOURCE_LANGUAGE);
		if (lang == null) {
			return;
		}
		Locale localeDef = switch (lang.length()) {
			case 2 -> new Locale(lang);
			case 5 -> new Locale(lang.substring(0, 1), lang.substring(3, 4));
			default -> null;
		};
		if (localeDef == null) {
			return;
		}
		Locale.setDefault(localeDef);
	}

	private Properties readUsersPreferences(Properties defaultPreferences) {
		Properties auto = new Properties(defaultPreferences);
		try (InputStream in = new FileInputStream(getUserPreferencesFile(defaultPreferences))) {
			auto.load(in);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Panic! Error while loading user preferences.");
		}
		return auto;
	}

	private File getUserPreferencesFile(Properties defaultPreferences) {
		if (defaultPreferences == null) {
			System.err.println("Panic! Error while loading default properties.");
			System.exit(1);
		}
		String freemindDirectory = getFreeMindDirectory(defaultPreferences);
		File userPropertiesFolder = new File(freemindDirectory);
		return new File(userPropertiesFolder, defaultPreferences.getProperty("autoproperties"));
	}

	private String getFreeMindDirectory(Properties defaultPreferences) {
		return System.getProperty("user.home") + File.separator
				+ defaultPreferences.getProperty("properties_folder");
	}

	public Properties readDefaultPreferences() {
		String propsLoc = "freemind.properties";
		URL defaultPropsURL = this.getClass().getClassLoader().getResource(propsLoc);
		Properties props = new Properties();
		try (InputStream in = defaultPropsURL.openStream()) {
			props.load(in);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Panic! Error while loading default properties.");
		}
		return props;
	}
}
