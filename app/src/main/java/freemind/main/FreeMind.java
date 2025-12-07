/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2000-2006 Joerg Mueller,
 * Daniel Polansky, Christian Foltin and others. See COPYING for Details
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
/* $Id: FreeMind.java,v 1.32.14.28.2.147 2011/01/09 21:03:13 christianfoltin Exp $ */

package freemind.main;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import com.inet.jortho.SpellChecker;

import freemind.controller.Controller;
import freemind.controller.LastStateStorageManagement;
import freemind.controller.MenuBar;
import freemind.controller.actions.generated.instance.MindmapLastStateStorage;
import freemind.modes.ModeController;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;

public class FreeMind extends JFrame implements FreeMindMain, ActionListener {

	public static final String J_SPLIT_PANE_SPLIT_TYPE = "JSplitPane.SPLIT_TYPE";

	public static final String VERTICAL_SPLIT_BELOW = "vertical_split_below";

	public static final String HORIZONTAL_SPLIT_RIGHT = "horizontal_split_right";

	public static final String LOG_FILE_NAME = "log";

	private static final String FREE_MIND_PROGRESS_LOAD_MAPS = "FreeMind.progress.loadMaps";

	private static final String FREE_MIND_PROGRESS_LOAD_MAPS_NAME =
			"FreeMind.progress.loadNamedMaps";

	private static final String SPLIT_PANE_POSITION = "split_pane_position";

	private static final String SPLIT_PANE_LAST_POSITION = "split_pane_last_position";

	public static final String RESOURCE_LOOKANDFEEL = "lookandfeel";

	public static final String RESOURCES_SELECTION_METHOD = "selection_method";

	public static final String RESOURCES_NODE_STYLE = "standardnodestyle";

	public static final String RESOURCES_ROOT_NODE_STYLE = "standardrootnodestyle";

	public static final String RESOURCES_NODE_TEXT_COLOR = "standardnodetextcolor";

	public static final String RESOURCES_SELECTED_NODE_COLOR = "standardselectednodecolor";

	public static final String RESOURCES_SELECTED_NODE_RECTANGLE_COLOR =
			"standardselectednoderectanglecolor";

	public static final String RESOURCE_DRAW_RECTANGLE_FOR_SELECTION =
			"standarddrawrectangleforselection";

	public static final String RESOURCES_EDGE_COLOR = "standardedgecolor";

	public static final String RESOURCES_EDGE_STYLE = "standardedgestyle";

	public static final String RESOURCES_CLOUD_COLOR = "standardcloudcolor";

	public static final String RESOURCES_LINK_COLOR = "standardlinkcolor";

	public static final String RESOURCES_BACKGROUND_COLOR = "standardbackgroundcolor";

	public static final String RESOURCE_PRINT_ON_WHITE_BACKGROUND = "printonwhitebackground";

	public static final String RESOURCES_WHEEL_VELOCITY = "wheel_velocity";

	public static final String RESOURCES_USE_TABBED_PANE = "use_tabbed_pane";

	public static final String RESOURCES_SHOW_NOTE_PANE = "use_split_pane";

	public static final String RESOURCES_SHOW_ATTRIBUTE_PANE = "show_attribute_pane";

	public static final String RESOURCES_DELETE_NODES_WITHOUT_QUESTION =
			"delete_nodes_without_question";

	public static final String RESOURCES_RELOAD_FILES_WITHOUT_QUESTION =
			"reload_files_without_question";

	private final Logger logger;

	protected static final VersionInformation VERSION = new VersionInformation("1.1.0 Beta 3");

	public static final String XML_VERSION = "1.1.0";

	public static final String RESOURCES_REMIND_USE_RICH_TEXT_IN_NEW_LONG_NODES =
			"remind_use_rich_text_in_new_long_nodes";

	public static final String RESOURCES_CONVERT_TO_CURRENT_VERSION =
			"resources_convert_to_current_version";

	public static final String RESOURCES_CUT_NODES_WITHOUT_QUESTION =
			"resources_cut_nodes_without_question";

	public static final String RESOURCES_DON_T_SHOW_NOTE_ICONS = "resources_don_t_show_note_icons";

	public static final String RESOURCES_USE_COLLABORATION_SERVER_WITH_DIFFERENT_VERSION =
			"resources_use_collaboration_server_with_different_version";

	public static final String RESOURCES_REMOVE_NOTES_WITHOUT_QUESTION =
			"resources_remove_notes_without_question";

	public static final String RESOURCES_SAVE_FOLDING_STATE = "resources_save_folding_state";

	public static final String RESOURCES_USE_DEFAULT_FONT_FOR_NOTES_TOO =
			"resources_use_default_font_for_notes_too";

	public static final String RESOURCES_USE_MARGIN_TOP_ZERO_FOR_NOTES =
			"resources_use_margin_top_zero_for_notes";

	public static final String RESOURCES_DON_T_SHOW_CLONE_ICONS =
			"resources_don_t_show_clone_icons";

	public static final String KEYSTROKE_MOVE_MAP_LEFT = "keystroke_MoveMapLeft";

	public static final String KEYSTROKE_MOVE_MAP_RIGHT = "keystroke_MoveMapRight";

	public static final String KEYSTROKE_PREVIOUS_MAP = "keystroke_previousMap";

	public static final String KEYSTROKE_NEXT_MAP = "keystroke_nextMap";

	public static final String RESOURCES_SEARCH_IN_NOTES_TOO = "resources_search_in_notes_too";

	public static final String RESOURCES_DON_T_SHOW_NOTE_TOOLTIPS =
			"resources_don_t_show_note_tooltips";

	public static final String RESOURCES_SEARCH_FOR_NODE_TEXT_WITHOUT_QUESTION =
			"resources_search_for_node_text_without_question";

	public static final String RESOURCES_COMPLETE_CLONING = "complete_cloning";

	public static final String RESOURCES_CLONE_TYPE_COMPLETE_CLONE = "COMPLETE_CLONE";

	public static final String TOOLTIP_DISPLAY_TIME = "tooltip_display_time";

	public static final String RESOURCES_DISPLAY_FOLDING_BUTTONS =
			"resources_display_folding_buttons";

	private static final int TIME_TO_DISPLAY_MESSAGES = 10000;

	public static final String ICON_BAR_COLUMN_AMOUNT = "icon_bar_column_amount";

	public static final String RESOURCES_OPTIONAL_SPLIT_DIVIDER_POSITION =
			"resources_optional_split_divider_position";

	public static final String RESOUCES_PASTE_HTML_STRUCTURE = "paste_html_structure";

	public static final String SCALING_FACTOR_PROPERTY = "scaling_factor_property";

	public static final String RESOURCES_CALENDAR_FONT_SIZE = "calendar_font_size";

	public static Properties props;

	private static Properties defProps;

	private MenuBar menuBar;

	private JLabel status;

	private Timer mStatusMessageDisplayTimer;

	// used to open different file types

	private File autoPropertiesFile;

	private File patternsFile;

	Controller controller;// the one and only controller

	private FreeMindCommon mFreeMindCommon;

	/**
	 * The main map's scroll pane.
	 */
	private JScrollPane mScrollPane = null;

	private JSplitPane mSplitPane;

	private JComponent mContentComponent = null;

	private JTabbedPane mTabbedPane = null;

	private ImageIcon mWindowIcon;

	private boolean mStartupDone = false;

	private List<StartupDoneListener> mStartupDoneListeners = new Vector<>();

	private Vector<Logger> mLoggerList = new Vector<>();

	public FreeMind(Properties pDefaultPreferences, Properties pUserPreferences,
			File pAutoPropertiesFile) {
		super("FreeMind");
		defProps = pDefaultPreferences;
		props = pUserPreferences;
		autoPropertiesFile = pAutoPropertiesFile;
		initializeLogger();
		logger = getLogger(FreeMind.class.getName());
		StringBuilder info = new StringBuilder();
		info.append("freemind_version = ");
		info.append(VERSION);
		info.append("; freemind_xml_version = ");
		info.append(XML_VERSION);
		String propsLoc = "version.properties";
		URL versionUrl = this.getClass().getClassLoader().getResource(propsLoc);
		Properties buildNumberPros = new Properties();
		try (InputStream stream = versionUrl.openStream()) {
			buildNumberPros.load(stream);
			info.append("; Build: ").append(buildNumberPros.getProperty("build.number")).append("\n");
		} catch (Exception e) {
			info.append("Problems reading build number file: ").append(e);
		}
		info.append("java_version = ");
		info.append(System.getProperty("java.version"));
		info.append("; os_name = ");
		info.append(System.getProperty("os.name"));
		info.append("; os_version = ");
		info.append(System.getProperty("os.version"));
		logger.info(info.toString());
		try {
			StringBuilder b = new StringBuilder();
			b.append('\n');
			// print all java/sun properties
			Properties properties = System.getProperties();
			List<String> list = new ArrayList<>();
			for (Object key : properties.keySet()) {
				list.add((String) key);
			}
			Collections.sort(list);
			for (String key : list) {
				if (key.startsWith("java") || key.startsWith("sun")) {
					b.append("Environment key ").append(key).append(" = ").append(properties.getProperty(key)).append("\n");
				}
			}
			logger.info(b.toString());
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		mFreeMindCommon = new FreeMindCommon(this);
		Resources.createInstance(this);
	}

	private void initializeLogger() {
		try {
			LogManager.getLogManager().readConfiguration(FreeMind.class.getResourceAsStream("/logging.properties"));

			Logger logger = Logger.getLogger(StdFormatter.STDOUT.getName());
			LoggingOutputStream los = new LoggingOutputStream(logger, StdFormatter.STDOUT);
			System.setOut(new PrintStream(los, true));

			// commented out to avoid double logging to stderr:
			// logger = Logger.getLogger(StdFormatter.STDERR.getName());
			// los = new LoggingOutputStream(logger, StdFormatter.STDERR);
			// System.setErr(new PrintStream(los, true));
		} catch (SecurityException | IOException e) {
			System.err.println("Error initializing logging system");
			e.printStackTrace();
		}
	}

	void init(FeedBack feedback) {
		/* This is only for apple but does not harm for the others. */
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		patternsFile = new File(getFreemindDirectory(), getDefaultProperty("patternsfile"));

		feedback.increase("FreeMind.progress.updateLookAndFeel", null);

		updateLookAndFeel();
		feedback.increase("FreeMind.progress.createController", null);

		setIconImage(mWindowIcon.getImage());
		// Layout everything
		getContentPane().setLayout(new BorderLayout());

		controller = new Controller(this);
		controller.init();
		feedback.increase("FreeMind.progress.settingPreferences", null);
		// add a listener for the controller, resource bundle:
		Controller.addPropertyChangeListener((propertyName, newValue, oldValue) -> {
			if (propertyName.equals(FreeMindCommon.RESOURCE_LANGUAGE)) {
				// re-read resources:
				mFreeMindCommon.clearLanguageResources();
				getResources();
			}
		});
		// fc, disabled with purpose (see java look and feel styleguides).
		// http://java.sun.com/products/jlf/ed2/book/index.html
		// // add a listener for the controller, look and feel:
		// Controller.addPropertyChangeListener(new FreemindPropertyListener() {
		//
		// public void propertyChanged(String propertyName, String newValue,
		// String oldValue) {
		// if (propertyName.equals(RESOURCE_LOOKANDFEEL)) {
		// updateLookAndFeel();
		// }
		// }
		// });

		controller.optionAntialiasAction
				.changeAntialias(getProperty(FreeMindCommon.RESOURCE_ANTIALIAS));

		setupSpellChecking();
		feedback.increase("FreeMind.progress.propageteLookAndFeel", null);
		SwingUtilities.updateComponentTreeUI(this); // Propagate LookAndFeel to

		feedback.increase("FreeMind.progress.buildScreen", null);
		setScreenBounds();

		// JComponents

		feedback.increase("FreeMind.progress.createInitialMode", null);
		controller.createNewMode(getProperty("initial_mode"));
		// EventQueue eventQueue = Toolkit.getDefaultToolkit()
		// .getSystemEventQueue();
		// eventQueue.push(new MyEventQueue());
	}// Constructor

	/**
	 * 
	 */
	private void updateLookAndFeel() {
		// set Look&Feel
		try {
			String lookAndFeel = props.getProperty(RESOURCE_LOOKANDFEEL);
			switch (lookAndFeel) {
				case "windows" -> UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				case "motif" -> UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				// Only available on macOS
				case "mac" -> UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
				case "metal" -> UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				case "gtk" ->  UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				case "nothing" -> {}
				default -> {
					if (lookAndFeel.indexOf('.') != -1) {
						// string contains a dot, so we assume class name
						UIManager.setLookAndFeel(lookAndFeel);
					} else {
						// default.
						logger.info("Default (System) Look & Feel: "
								+ UIManager.getSystemLookAndFeelClassName());
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("Unable to set Look & Feel.");
		}
		mFreeMindCommon.loadUIProperties(defProps);
	}

	@Override
	public boolean isApplet() {
		return false;
	}

	@Override
	public File getPatternsFile() {
		return patternsFile;
	}

	@Override
	public VersionInformation getFreemindVersion() {
		return VERSION;
	}

	// maintain this methods to keep the last state/size of the window (PN)
	@Override
	public int getWinHeight() {
		return getHeight();
	}

	@Override
	public int getWinWidth() {
		return getWidth();
	}

	@Override
	public int getWinX() {
		return getX();
	}

	@Override
	public int getWinY() {
		return getY();
	}

	@Override
	public int getWinState() {
		return getExtendedState();
	}

	@Override
	public URL getResource(String name) {
		return this.getClass().getClassLoader().getResource(name);
	}

	@Override
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	@Override
	public int getIntProperty(String key, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public String getDefaultProperty(String key) {
		return defProps.getProperty(key);
	}

	@Override
	public void setDefaultProperty(String key, String value) {
		defProps.setProperty(key, value);
	}

	@Override
	public String getFreemindDirectory() {
		return System.getProperty("user.home") + File.separator + getProperty("properties_folder");
	}

	@Override
	public void saveProperties(boolean pIsShutdown) {
		try (OutputStream out = new FileOutputStream(autoPropertiesFile)) {
			final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.ISO_8859_1);
			outputStreamWriter.write("#FreeMind ");
			outputStreamWriter.write(VERSION.toString());
			outputStreamWriter.write('\n');
			outputStreamWriter.flush();
			// to save as few props as possible.
			Properties toBeStored = Tools.copyChangedProperties(props, defProps);
			toBeStored.store(out, null);
		} catch (Exception ex) {
			Resources.getInstance().logException(ex);
		}
		getController().getFilterController().saveConditions();
	}

	@Override
	public MapView getView() {
		return controller.getView();
	}

	@Override
	public Controller getController() {
		return controller;
	}

	@Override
	public void setView(MapView view) {
		mScrollPane.setViewportView(view);
	}

	@Override
	public MenuBar getFreeMindMenuBar() {
		return menuBar;
	}

	@Override
	public void out(String msg) {
		if (status != null) {
			status.setText(msg);
			// Automatically remove old messages after a certain time.
			mStatusMessageDisplayTimer.restart();
			// logger.info(msg);
		}
	}

	@Override
	public void err(String msg) {
		out(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent pE) {
		out("");
		mStatusMessageDisplayTimer.stop();
	}

	/**
	 * Open URL in system browser
	 * <p>
	 * Opens the specified URL in the default browser for the operating system.
	 * 
	 * @param url a url pointing to where the browser should open
	 * @see URL
	 */
	@Override
	public void openDocument(URL url) throws Exception {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				// fix for
				// https://sourceforge.net/p/freemind/discussion/22102/thread/cf032151/?limit=25#c631
				URI uri = new URI(url.toString().replaceAll("^file:////", "file://"));
				desktop.browse(uri);
			} catch (Exception e) {
				logger.severe("Caught: " + e);
			}
		}
	}

	@Override
	public void setWaitingCursor(boolean waiting) {
		if (waiting) {
			getRootPane().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getRootPane().getGlassPane().setVisible(true);
		} else {
			getRootPane().getGlassPane()
					.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			getRootPane().getGlassPane().setVisible(false);
		}
	}

	/** Returns the ResourceBundle with the current language */
	@Override
	public ResourceBundle getResources() {
		return mFreeMindCommon.getResources();
	}

	@Override
	public String getResourceString(String resource) {
		return mFreeMindCommon.getResourceString(resource);
	}

	@Override
	public String getResourceString(String key, String pDefault) {
		return mFreeMindCommon.getResourceString(key, pDefault);
	}

	@Override
	public Logger getLogger(String forClass) {
		Logger loggerForClass = Logger.getLogger(forClass);
		mLoggerList.add(loggerForClass);
		return loggerForClass;
	}

	public static void main(final String[] args, Properties pDefaultPreferences,
			Properties pUserPreferences, File pAutoPropertiesFile) {
		try {

			final FreeMind frame =
					new FreeMind(pDefaultPreferences, pUserPreferences, pAutoPropertiesFile);
			int scale = frame.getIntProperty(SCALING_FACTOR_PROPERTY, 100);
			if (scale != 100) {
				Tools.scaleAllFonts(scale / 100f);
				Font SEGOE_UI_PLAIN_12 = new Font("Segoe UI", Font.PLAIN, 12 * scale / 100);
				UIManager.put("MenuItem.acceleratorFont", SEGOE_UI_PLAIN_12);
				UIManager.put("Menu.acceleratorFont", SEGOE_UI_PLAIN_12);
				UIManager.put("CheckBoxMenuItem.acceleratorFont", SEGOE_UI_PLAIN_12);
				UIManager.put("RadioButtonMenuItem.acceleratorFont", SEGOE_UI_PLAIN_12);
			}
			IFreeMindSplash splash = new FreeMindSplashModern(frame);
			splash.setVisible(true);
			final FeedBack feedBack = splash.getFeedBack();
			frame.mWindowIcon = splash.getWindowIcon();

			feedBack.setMaximumValue(10 + frame.getMaximumNumberOfMapsToLoad(args));
			frame.init(feedBack);

			feedBack.increase("FreeMind.progress.startCreateController", null);

			feedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS, null);

			frame.loadMaps(args, feedBack);

			Tools.waitForEventQueue();
			feedBack.increase("FreeMind.progress.endStartup", null);
			// focus fix after startup.
			frame.addWindowFocusListener(new WindowFocusListener() {

				public void windowLostFocus(WindowEvent e) {}

				public void windowGainedFocus(WindowEvent e) {
					frame.getController().obtainFocusForSelected();
					frame.removeWindowFocusListener(this);
				}
			});
			frame.setVisible(true);
			splash.setVisible(false);
			frame.fireStartupDone();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(null,
							"FreeMind can't be started: " + e.getLocalizedMessage() + "\n"
									+ Tools.getStacktrace(e),
							"Startup problem", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void setupSpellChecking() {
		boolean checkSpelling =
				// Resources.getInstance().getBoolProperty(FreeMindCommon.CHECK_SPELLING);
				Tools.safeEquals("true", props.getProperty(FreeMindCommon.CHECK_SPELLING));
		if (checkSpelling) {
			try {
				// TODO filter languages in dictionaries.properties like this:
				// String[] languages = "en,de,es,fr,it,nl,pl,ru,ar".split(",");
				// for (int i = 0; i < languages.length; i++) {
				// System.out.println(new File("dictionary_" + languages[i] +
				// ".ortho").exists());
				// }
				String decodedPath = Tools.getFreeMindBasePath();
				URL url = null;
				if (new File(decodedPath).exists()) {
					url = new URL("file", null, decodedPath);
				}
				SpellChecker.registerDictionaries(url, Locale.getDefault().getLanguage());
			} catch (MalformedURLException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}

	private void fireStartupDone() {
		mStartupDone = true;
		for (StartupDoneListener listener : mStartupDoneListeners) {
			listener.startupDone();
		}
	}

	private void setScreenBounds() {
		// Create the MenuBar
		menuBar = new MenuBar(controller);
		setJMenuBar(menuBar);

		// Create the scroll pane
		mScrollPane = new MapView.ScrollPane();
		if (Resources.getInstance().getBoolProperty("no_scrollbar")) {
			mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		} else {
			mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		}
		status = new JLabel("!");
		status.setPreferredSize(status.getPreferredSize());
		status.setText("");
		mStatusMessageDisplayTimer = new Timer(TIME_TO_DISPLAY_MESSAGES, this);
		mContentComponent = mScrollPane;

		boolean shouldUseTabbedPane =
				Resources.getInstance().getBoolProperty(RESOURCES_USE_TABBED_PANE);

		if (shouldUseTabbedPane) {
			// tabbed panes eat control up. This is corrected here.
			InputMap map;
			map = (InputMap) UIManager.get("TabbedPane.ancestorInputMap");
			KeyStroke keyStrokeCtrlUp =
					KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK);
			map.remove(keyStrokeCtrlUp);
			mTabbedPane = new JTabbedPane();
			mTabbedPane.setFocusable(false);
			controller.addTabbedPane(mTabbedPane);
			getContentPane().add(mTabbedPane, BorderLayout.CENTER);
		} else {
			// don't use tabbed panes.
			getContentPane().add(mContentComponent, BorderLayout.CENTER);
		}
		getContentPane().add(status, BorderLayout.SOUTH);

		// Disable the default close button, instead use windowListener
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				controller.quit.actionPerformed(new ActionEvent(this, 0, "quit"));
			}

			/*
			 * fc, 14.3.2008: Completely removed, as it damaged the focus if for example the note
			 * window was active.
			 */
			// public void windowActivated(WindowEvent e) {
			// // This doesn't work the first time, it's called too early to
			// // get Focus
			// logger.info("windowActivated");
			// if ((getView() != null) && (getView().getSelected() != null)) {
			// getView().getSelected().requestFocus();
			// }
			// }
		});

		if (Tools.safeEquals(getProperty("toolbarVisible"), "false")) {
			controller.setToolbarVisible(false);
		}

		if (Tools.safeEquals(getProperty("leftToolbarVisible"), "false")) {
			controller.setLeftToolbarVisible(false);
		}

		// first define the final layout of the screen:
		setFocusTraversalKeysEnabled(false);
		// and now, determine size, position and state.
		pack();
		// set the default size (PN)
		int win_width = getIntProperty("appwindow_width", 0);
		int win_height = getIntProperty("appwindow_height", 0);
		int win_x = getIntProperty("appwindow_x", 0);
		int win_y = getIntProperty("appwindow_y", 0);
		win_width = (win_width > 0) ? win_width : 640;
		win_height = (win_height > 0) ? win_height : 440;
		final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		final Insets screenInsets = defaultToolkit.getScreenInsets(getGraphicsConfiguration());
		Dimension screenSize = defaultToolkit.getScreenSize();
		final int screenWidth = screenSize.width - screenInsets.left - screenInsets.right;
		win_width = Math.min(win_width, screenWidth);
		final int screenHeight = screenSize.height - screenInsets.top - screenInsets.bottom;
		win_height = Math.min(win_height, screenHeight);
		win_x = Math.max(screenInsets.left, win_x);
		win_x = Math.min(screenWidth + screenInsets.left - win_width, win_x);
		win_y = Math.max(screenInsets.top, win_y);
		win_y = Math.min(screenWidth + screenInsets.top - win_height, win_y);
		setBounds(win_x, win_y, win_width, win_height);
		// set the default state (normal/maximized) (PN)
		// (note: this must be done later when partucular
		// initalizations of the windows are ready,
		// perhaps after setVisible is it enough... :-?
		int win_state = Integer.parseInt(FreeMind.props.getProperty("appwindow_state", "0"));
		win_state = ((win_state & ICONIFIED) != 0) ? NORMAL : win_state;
		setExtendedState(win_state);
	}

	private int getMaximumNumberOfMapsToLoad(String[] args) {
		LastStateStorageManagement management = getLastStateStorageManagement();
		return Math.max(args.length + management.getLastOpenList().size(), 1);
	}

	private void loadMaps(final String[] args, FeedBack pFeedBack) {
		boolean fileLoaded = false;
		if (Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAPS_AND_LAYOUT))) {
			int index = 0;
			MapModule mapToFocus = null;
			LastStateStorageManagement management = getLastStateStorageManagement();
			for (MindmapLastStateStorage store : management.getLastOpenList()) {
				String restorable = store.getRestorableName();
				pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS_NAME,
						new Object[] {restorable.replaceAll(".*/", "")});
				try {
					if (controller.getLastOpenedList().open(restorable)) {
						if (index == management.getLastFocussedTab()) {
							mapToFocus = controller.getMapModule();
						}
					}
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
				index++;
			}
			if (mapToFocus != null) {
				controller.getMapModuleManager().changeToMapModule(mapToFocus.getDisplayName());
			}
		}
		ModeController modeController = controller.getModeController();
		for (String arg : args) {
			String fileArgument = arg;
			pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS_NAME,
					new Object[]{fileArgument.replaceAll(".*/", "")});
			if (fileArgument.toLowerCase()
					.endsWith(FreeMindCommon.FREEMIND_FILE_EXTENSION)) {

				if (!Tools.isAbsolutePath(fileArgument)) {
					fileArgument = System.getProperty("user.dir")
							+ FileSystems.getDefault().getSeparator() + fileArgument;
				}
				try {
					modeController.load(new File(fileArgument));
					fileLoaded = true;
					// logger.info("Attempting to load: " +
					// args[i]);
				} catch (Exception ex) {
					System.err.println("File " + fileArgument + " not found error");
				}
			}
		}
		if (!fileLoaded) {
			fileLoaded = processLoadEventFromStartupPhase();
		}
		if (!fileLoaded) {
			String restoreable = getProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED);
			if (Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAP))
					&& restoreable != null && !restoreable.isEmpty()) {
				pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS_NAME,
						new Object[] {restoreable.replaceAll(".*/", "")});
				try {
					controller.getLastOpenedList().open(restoreable);
					controller.getModeController().getView().moveToRoot();
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
					out("An error occured on opening the file: " + restoreable + ".");
				}
			}
		}
		if (!fileLoaded && Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_NEW_MAP))) {
			/*
			 * nothing loaded so far. Perhaps, we should display a new map... According to Summary:
			 * On first start FreeMind should show new map to newbies
			 * https://sourceforge.net/tracker/?func=detail&atid=107118 &aid=1752516&group_id=7118
			 */
			modeController.newMap();
			pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS, null);
		}
	}

	private LastStateStorageManagement getLastStateStorageManagement() {
		String lastStateMapXml = getProperty(FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE);
		return new LastStateStorageManagement(lastStateMapXml);
	}

	/**
	 * Iterates over the load events from the startup phase
	 * <p>
	 * More than one file can be opened during startup. The filenames are stored in numbered
	 * properties, i.e.
	 * <ul>
	 * loadEventDuringStartup0=/Users/alex/Desktop/test1.mm
	 * loadEventDuringStartup1=/Users/alex/Desktop/test2.mm
	 * </ul>
	 * 
	 * @return true if at least one file has been loaded
	 */
	private boolean processLoadEventFromStartupPhase() {
		boolean atLeastOneFileHasBeenLoaded = false;
		int count = 0;
		while (true) {
			String propertyKey = FreeMindCommon.LOAD_EVENT_DURING_STARTUP + count;
			if (getProperty(propertyKey) == null) {
				break;
			} else {
				if (processLoadEventFromStartupPhase(propertyKey))
					atLeastOneFileHasBeenLoaded = true;
				++count;
			}
		}
		return atLeastOneFileHasBeenLoaded;
	}

	private boolean processLoadEventFromStartupPhase(String propertyKey) {
		String filename = getProperty(propertyKey);
		try {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading " + filename);
			}
			controller.getModeController().load(Tools.fileToUrl(new File(filename)));
			// remove temporary property because we do not want to store in a
			// file and survive restart
			getProperties().remove(propertyKey);
			return true;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			out("An error occured on opening the file: " + filename + ".");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.main.FreeMindMain#getJFrame()
	 */
	@Override
	public JFrame getJFrame() {
		return this;
	}

	@Override
	public ClassLoader getFreeMindClassLoader() {
		return mFreeMindCommon.getFreeMindClassLoader();
	}

	@Override
	public String getFreemindBaseDir() {
		return mFreeMindCommon.getFreemindBaseDir();
	}

	@Override
	public String getAdjustableProperty(String label) {
		return mFreeMindCommon.getAdjustableProperty(label);
	}

	@Override
	public JSplitPane insertComponentIntoSplitPane(JComponent pMindMapComponent) {
		if (mSplitPane != null) {
			// already present:
			return mSplitPane;
		}
		removeContentComponent();
		int splitType = JSplitPane.VERTICAL_SPLIT;
		String splitProperty = getProperty(J_SPLIT_PANE_SPLIT_TYPE);
		if (Tools.safeEquals(splitProperty, HORIZONTAL_SPLIT_RIGHT)) {
			splitType = JSplitPane.HORIZONTAL_SPLIT;
		} else if (Tools.safeEquals(splitProperty, VERTICAL_SPLIT_BELOW)) {
			// default
		} else {
			logger.warning("Split type not known: " + splitProperty);
		}
		mSplitPane = new JSplitPane(splitType, mScrollPane, pMindMapComponent);
		mSplitPane.setContinuousLayout(true);
		mSplitPane.setOneTouchExpandable(false);
		/*
		 * This means that the mind map area gets all the space that results from resizing the
		 * window.
		 */
		mSplitPane.setResizeWeight(1.0d);
		// split panes eat F8 and F6. This is corrected here.
		Tools.correctJSplitPaneKeyMap();
		mContentComponent = mSplitPane;
		setContentComponent();
		// set divider position:
		setSplitLocation();
		// after making this window visible, the size is adjusted. To get the right
		// split location, we postpone this.
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent pE) {
				setSplitLocation();
				removeComponentListener(this);
			}
		});
		return mSplitPane;
	}

	private void setSplitLocation() {
		int splitPanePosition = getIntProperty(SPLIT_PANE_POSITION, -1);
		int lastSplitPanePosition = getIntProperty(SPLIT_PANE_LAST_POSITION, -1);
		if (mSplitPane != null && splitPanePosition != -1 && lastSplitPanePosition != -1) {
			mSplitPane.setDividerLocation(splitPanePosition);
			mSplitPane.setLastDividerLocation(lastSplitPanePosition);
		}
	}

	@Override
	public void removeSplitPane() {
		if (mSplitPane != null) {
			setProperty(SPLIT_PANE_POSITION, "" + mSplitPane.getDividerLocation());
			setProperty(SPLIT_PANE_LAST_POSITION, "" + mSplitPane.getLastDividerLocation());
			removeContentComponent();
			mContentComponent = mScrollPane;
			setContentComponent();
			mSplitPane = null;
		}
	}

	private void removeContentComponent() {
		if (mTabbedPane != null) {
			if (mTabbedPane.getSelectedIndex() >= 0) {
				mTabbedPane.setComponentAt(mTabbedPane.getSelectedIndex(), new JPanel());
			}
		} else {
			getContentPane().remove(mContentComponent);
			getRootPane().revalidate();
		}

	}

	private void setContentComponent() {
		if (mTabbedPane != null) {
			if (mTabbedPane.getSelectedIndex() >= 0) {
				mTabbedPane.setComponentAt(mTabbedPane.getSelectedIndex(), mContentComponent);
			}
		} else {
			getContentPane().add(mContentComponent, BorderLayout.CENTER);
			getRootPane().revalidate();
		}
	}

	@Override
	public JScrollPane getScrollPane() {
		return mScrollPane;
	}

	@Override
	public JComponent getContentComponent() {
		return mContentComponent;
	}

	@Override
	public void registerStartupDoneListener(StartupDoneListener pStartupDoneListener) {
		if (!mStartupDone) {
			mStartupDoneListeners.add(pStartupDoneListener);
		}
	}

	@Override
	public List<Logger> getLoggerList() {
		return Collections.unmodifiableList(mLoggerList);
	}

}
