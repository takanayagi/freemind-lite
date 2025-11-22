/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2000-2007 Christian Foltin,
 * Dimitry Polivaev and others.
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
 * Created on 31.07.2007
 */
/* $Id: DontShowAgainDialogTests.java,v 1.1.2.1 2007/08/07 20:09:24 christianfoltin Exp $ */

package tests.freemind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.junit.jupiter.api.Test;
import freemind.common.OptionalDontShowMeAgainDialog;

/**
 * @author foltin
 * 
 */
public class DontShowAgainDialogTests extends FreeMindTestBase {
	@Test
	public void testDialog() throws Exception {
		int showResult;
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		startRobotThread(frame.getLocationOnScreen());
		showResult = new OptionalDontShowMeAgainDialog(frame, frame, "message", "title",
				pKey -> "?" + pKey + "?", new OptionalDontShowMeAgainDialog.DontShowPropertyHandler() {

					public String getProperty() {
						return "";
					}

					public void setProperty(String pValue) {
						System.out.println("Property Result: '" + pValue + "'");
					}
				}, OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED).show().getResult();
		assertEquals(JOptionPane.CANCEL_OPTION, showResult, "Expected CANCEL result, but got: " + showResult);
		showResult = new OptionalDontShowMeAgainDialog(frame, frame, "message NO SHOW", "title",
				pKey -> "?" + pKey + "?", new OptionalDontShowMeAgainDialog.DontShowPropertyHandler() {

					public String getProperty() {
						return "true";
					}

					public void setProperty(String pValue) {
						System.out.println("Property Result: '" + pValue + "'");
					}
				}, OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED).show().getResult();
		assertEquals(JOptionPane.OK_OPTION, showResult, "Expected OK result, but got: " + showResult);
		showResult = new OptionalDontShowMeAgainDialog(frame, frame, "message NO SHOW", "title",
				pKey -> "?" + pKey + "?", new OptionalDontShowMeAgainDialog.DontShowPropertyHandler() {

					public String getProperty() {
						return "false";
					}

					public void setProperty(String pValue) {
						System.out.println("Property Result: '" + pValue + "'");
					}
				}, OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED).show().getResult();
		startRobotThread(frame.getLocationOnScreen());
		showResult = new OptionalDontShowMeAgainDialog(frame, frame, "message remind", "title",
				pKey -> "?" + pKey + "?", new OptionalDontShowMeAgainDialog.DontShowPropertyHandler() {

					public String getProperty() {
						return "";
					}

					public void setProperty(String pValue) {
						System.out.println("Property Result: '" + pValue + "'");
					}
				}, OptionalDontShowMeAgainDialog.BOTH_OK_AND_CANCEL_OPTIONS_ARE_STORED).show()
						.getResult();
		assertEquals(JOptionPane.CANCEL_OPTION, showResult, "Expected CANCEL result, but got: " + showResult);
	}

	// Create a robot in another thread, so that the dialog can be shown
	// without blocking the test.
	private void startRobotThread(Point location) {
		new Thread(() -> {
			try {
				Robot robot = new Robot();
				robot.mouseMove(location.x, location.y + 20);
				// wait for the dialog to be shown.
				Thread.sleep(2000);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

}
