/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C) 2000-2007 Joerg Mueller,
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
/* $Id: StdFormatter.java,v 1.1.2.5 2008/02/03 21:50:04 dpolivaev Exp $ */

package freemind.main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;

public class StdFormatter extends Formatter {

	private static class StdOutErrLevel extends Level {
		public StdOutErrLevel(String name, int value) {
			super(name, value);
		}
	}

	/**
	 * Level for STDOUT activity.
	 */
	final static Level STDOUT = new StdOutErrLevel("STDOUT", Level.WARNING.intValue() + 53);

	/**
	 * Level for STDERR activity
	 */
	final static Level STDERR = new StdOutErrLevel("STDERR", Level.SEVERE.intValue() + 53);

	private static final DateTimeFormatter DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

	/**
	 * Custom formatter to include thread id in log messages.
	 */
	@Override
	public String format(LogRecord record) {
		// The format is <date> <time> [<thread id>] <level> - <message> <stacktrace>
		String timestamp = DATE_FORMAT.format(Instant.ofEpochMilli(record.getMillis()));
		long threadId = record.getLongThreadID();
		String level = record.getLevel().getName();
		String loggerName = record.getLoggerName();
		String message = formatMessage(record);
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp);
		sb.append(String.format(" [%2d] ", threadId));
		sb.append(String.format("%-7s", level));
		sb.append(" ");
		sb.append(loggerName != null ? loggerName : "(unknown)");
		sb.append(" - ");
		sb.append(message);
		sb.append(System.lineSeparator());
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			pw.close();
			sb.append(sw.toString());
		}
		return sb.toString();
	}
}
