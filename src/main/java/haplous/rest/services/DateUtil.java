package haplous.rest.services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import haplous.rest.init.XMLSuite;

public class DateUtil {
	/*
	 * =========================================================================
	 * =================================== Method to get current date AUTHOR :
	 * Chandan Shanbhag
	 * 
	 * =========================================================================
	 * ====================================
	 */
	public String dateformat = null;
	public String timeformat = null;

	public DateUtil() throws IOException {
		super();
		String dateformat = XMLSuite.dateformat;
		String timeformat = XMLSuite.timeformat;
		this.dateformat = dateformat;
		this.timeformat = timeformat;
	}

	public String getDateAndTime() {
		String date = null;
		SimpleDateFormat fr;
		fr = new SimpleDateFormat(dateformat + " " + timeformat);
		date = fr.format(new Date());
		return date;
	}

	public String futureDate() {
		Date today = new Date();
		Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
		String date = null;
		SimpleDateFormat fr;
		fr = new SimpleDateFormat(dateformat + " " + timeformat);
		date = fr.format(tomorrow);
		return date;

	}

	public String futureDate(int days) {
		Date today = new Date();
		Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24 * days));
		String date = null;
		SimpleDateFormat fr;
		fr = new SimpleDateFormat(dateformat + " " + timeformat);
		date = fr.format(tomorrow);
		return date;

	}

	public String getCurrentTime(int hour, int min, int sec) {
		Calendar calendar = null;
		DateFormat timeFormat = null;
		String time = null;
		timeFormat = new SimpleDateFormat(timeformat);
		// get calendar
		calendar = addTime(hour, min, sec);
		// Convert Date to String.
		time = timeFormat.format(calendar.getTime());
		System.out.println("Current time in 12 hour format : " + time);
		return time;
	}

	public Calendar addTime(int hour, int min, int sec) {
		Calendar calendar = new GregorianCalendar();
		System.out.println("Current Date::");
		// Subtract 4 months,5 days,12 hours and 24 minutes
		calendar.add(Calendar.HOUR, hour);
		calendar.add(Calendar.MINUTE, min);
		calendar.add(Calendar.SECOND, sec);
		return calendar;
	}

	public String getDate() {
		String date = null;
		SimpleDateFormat fr;
		fr = new SimpleDateFormat(dateformat);
		date = fr.format(new Date());
		return date;
	}
	
	public String getTime() {
		String time = null;
		SimpleDateFormat fr;
		fr = new SimpleDateFormat(timeformat);
		time = fr.format(new Date());
		return time;
	}


	public int currentYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); // use java.util.Date object as arguement
		// get the value of all the calendar date fields.
		System.out.println("Calendar's Year: " + cal.get(Calendar.YEAR));
		return cal.get(Calendar.YEAR);
	}

	public int currentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); // use java.util.Date object as arguement
		// get the value of all the calendar date fields.
		System.out.println("Calendar's Month: " + cal.get(Calendar.MONTH));
		return cal.get(Calendar.MONTH);
	}

	public int currentDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); // use java.util.Date object as arguement
		// get the value of all the calendar date fields.
		System.out.println("Calendar's Day: " + cal.get(Calendar.DAY_OF_MONTH));
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public String customDateTime(String customformat)	{
		String date = null;
		SimpleDateFormat fr;
		fr = new SimpleDateFormat(customformat);
		date = fr.format(new Date());
		return date;
	}
	

}
