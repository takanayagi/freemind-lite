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
 * Created on 18.08.2006
 */
/* $Id: HookDescriptorBase.java,v 1.1.2.7 2008/07/09 20:01:00 christianfoltin Exp $ */
package freemind.extensions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginClasspath;
import freemind.main.Resources;
import freemind.main.Tools;

/**
 * @author foltin
 * 
 */
public class HookDescriptorBase {

	// Logging:
	protected static Logger logger = null;

	protected final Plugin pluginBase;

	protected final String mXmlPluginFile;

	/**
	 * @param pluginBase
	 * @param xmlPluginFile
	 */
	public HookDescriptorBase(final Plugin pluginBase, final String xmlPluginFile) {
		super();
		this.pluginBase = pluginBase;
		mXmlPluginFile = xmlPluginFile;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(this.getClass().getName());
		}
	}

	/**
	 */
	protected String getFromResourceIfNecessary(String string) {
		if (string == null) {
			return null;
		}
		if (string.startsWith("%")) {
			return Resources.getInstance().getResourceString(string.substring(1));
		}
		return string;
	}

	protected String getFromPropertiesIfNecessary(String string) {
		if (string == null) {
			return null;
		}
		if (string.startsWith("%")) {
			return Resources.getInstance().getProperty(string.substring(1));
		}
		return string;
	}

	/**
	 * @return the relative/absolute(?) position of the plugin xml file.
	 */
	private String getPluginDirectory() {
		return Resources.getInstance().getFreemindBaseDir() + "/"
				+ new File(mXmlPluginFile).getParent();
	}

	public Plugin getPluginBase() {
		return pluginBase;
	}

	public List<PluginClasspath> getPluginClasspath() {
		Vector<PluginClasspath> returnValue = new Vector<>();
		for (Object obj : pluginBase.getListChoiceList()) {
			if (obj instanceof PluginClasspath pluginClasspath) {
				returnValue.add(pluginClasspath);
			}
		}
		return returnValue;
	}

	public ClassLoader getPluginClassLoader() {
		// construct class loader:
		List<PluginClasspath> pluginClasspathList = getPluginClasspath();
		return getClassLoader(pluginClasspathList);
	}

	private static HashMap<String, ClassLoader> classLoaderCache = new HashMap<>();

	/**
	 * This string is used to identify known classloaders as they are cached.
	 * 
	 */
	private String createPluginClasspathString(List<PluginClasspath> pluginClasspathList) {
		StringBuilder result = new StringBuilder();
		for (PluginClasspath type : pluginClasspathList) {
			result.append(type.getJar()).append(',');
		}
		return result.toString();
	}

	/**
	 *
	 */
	private ClassLoader getClassLoader(List<PluginClasspath> pluginClasspathList) {
		String key = createPluginClasspathString(pluginClasspathList);
		if (classLoaderCache.containsKey(key))
			return classLoaderCache.get(key);
		try {
			URL[] urls = new URL[pluginClasspathList.size()];
			int j = 0;
			for (PluginClasspath classPath : pluginClasspathList) {
				String jarString = classPath.getJar();
				// if(jarString.startsWith(FREEMIND_BASE_DIR_STRING)){
				// jarString = frame.getFreemindBaseDir() +
				// jarString.substring(FREEMIND_BASE_DIR_STRING.length());
				// }
				// new version of classpath resolution suggested by ewl under
				// patch [ 1154510 ] Be able to give absolute classpath entries
				// in plugin.xml
				File file = new File(jarString);
				if (!file.isAbsolute()) {
					file = new File(getPluginDirectory(), jarString);
				}
				// end new version by ewl.
				logger.info("file " + Tools.fileToUrl(file) + " exists = " + file.exists());
				urls[j++] = Tools.fileToUrl(file);
			}
			ClassLoader loader =
					new URLClassLoader(urls, Resources.getInstance().getFreeMindClassLoader());
			classLoaderCache.put(key, loader);
			return loader;
		} catch (MalformedURLException e) {
			freemind.main.Resources.getInstance().logException(e);
			return this.getClass().getClassLoader();
		}
	}
}
