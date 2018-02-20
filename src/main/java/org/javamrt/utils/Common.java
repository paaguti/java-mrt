// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.utils;

// import java.awt.GraphicsEnvironment;

import org.javamrt.progs.route_btoa;

import java.util.Calendar;
import java.util.TimeZone;

public class Common {

	/**
	 * Time conversion utilities <br>
	 * <br>
	 * Synchronize at midnight and the beginning of a month taking as reference
	 * <b>UTC</b>,<br>
	 * which is how the timestamps are managed in the RIPE-RR.
	 */
	private static Calendar cal = Calendar.getInstance(TimeZone
			.getTimeZone("UTC"));

	/**
	 * Calculate the timestamp for the beginning of month before <i>time</i> <br>
	 * if <i>time</i> is the beginning of month, return it.
	 *
	 * @param time
	 * @return timestamp of beginning of the month before <i>time</i>
	 */
	public static long beginMonth(long time) {
		cal.setTimeInMillis(time * 1000L);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis() / 1000L;
	}

	/**
	 * Calculate the timestamp of the midnight before <i>time</i> <br>
	 * If <i>time</i> is the midnight, return it.
	 *
	 * @param time
	 * @return timestamp of the midnight before <i>time</i>
	 */
	public static long midNight(long time) {
		cal.setTimeInMillis(time * 1000L);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis() / 1000L;
	}

	/**
	 * Calculate the timestamp for the beginning of hour
	 *
	 * @param time
	 * @return
	 */
	public static long startHour(long time) {
		cal.setTimeInMillis(time * 1000L);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis() / 1000L;
	}

	/**
	 *
	 * @param time
	 * @return the seconds since the start of hour; <br>
	 *         for hh:00:00, return 0
	 */
	public static long secondsOfHour(long time) {
		return time - startHour(time);
	}

	/**
	 *
	 * @param time
	 * @return the seconds since midnight of the day;<br>
	 *         if time is midnight, return 0
	 */
	public static long secondsOfDay(long time) {
		return time - midNight(time);
	}

	/**
	 *
	 * @param time
	 * @return the seconds since midnight of the day;<br>
	 *         if time is midnight, return 0
	 */
	public static long secondsOfMonth(long time) {
		return time - beginMonth(time);
	}

	/**
	 * returns a string with the UTC time (hour,min,day,month,year)
	 * @param utcTime
	 * @return HH:MM DD-MM-YYYY
	 */
	public static String UTCTime(long utcTime) {
		cal.setTimeInMillis(utcTime*1000L);
		return String.format("%02d:%02dUTC %2d-%02d-%4d",
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE),
				cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.MONTH)+1,
				cal.get(Calendar.YEAR));
	}

	public static int days(int month, int year) {
		if (month == 2)
			return 28 + ((year % 4 == 0) ? 1 : 0) - ((year % 4 == 100) ? 1 : 0);
		if (month < 8)
			return 30 + (month % 2);
		return 31 - (month % 2);
	}

	public static int minutes(int seconds) {
		return seconds * 60;
	}

	public static void main(String args[]) {
		int year = 2009;
		int month = Calendar.AUGUST;
		int date  = 17;
		try {
			date  = Integer.parseInt(args[0]);
			month = Integer.parseInt(args[1])-1;
			year  = Integer.parseInt(args[2]);
		} catch (Exception e) {
			year = 2009;
			month = Calendar.AUGUST;
			date  = 17;
		}
		cal.set(Calendar.DATE,  date);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR,  year);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);

		printCal(cal,"UTC");
		route_btoa.System_err_println(String.format(" son %d segundos de la epoca", cal.getTimeInMillis() / 1000L));
	}

	private static void printCal(Calendar cal2, String zone) {
		route_btoa.System_err_println(String.format("%s: %2d/%02d/%04d %2d:%02d:%02d", zone, cal2
                .get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.MONTH) + 1, cal2
                .get(Calendar.YEAR), cal2.get(Calendar.HOUR_OF_DAY), cal2
                .get(Calendar.MINUTE), cal2.get(Calendar.SECOND)));
	}

}
