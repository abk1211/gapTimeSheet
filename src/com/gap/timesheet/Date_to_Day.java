package com.gap.timesheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Date_to_Day {
	public static String datecreation() {
		//Taking the current day and date
		SimpleDateFormat sdf_day = new SimpleDateFormat("EEEE");
		Date date = new Date();
		System.out.println(sdf_day.format(date));
		String day = sdf_day.format(date).toString();
		int subvalue = 0;
		int addvalue = 0;
		switch (day.toUpperCase()) {
		case "MONDAY":
			subvalue=1;
			addvalue=5;
			break;
		case "TUESDAY":
			subvalue=2;
			addvalue=4;
			break;
		case "WEDNESDAY":
			subvalue=3;
			addvalue=3;
			break;
		case "THRUSDAY":
			subvalue=4;
			addvalue=2;
			break;
		case "FRIDAY":
			subvalue=5;
			addvalue=1;
			break;
		case "SATURDAY":
			subvalue=6;
			addvalue=0;
			break;
		case "SUNDAY":
			subvalue=7;
			addvalue=-1;
			break;

		default:
			System.out.println("May be day is not present in the list");
			break;
		}
		//Taking Start date from Current date
		Calendar start_cal = Calendar.getInstance();
		start_cal.setTime(date);
		int start_Date = start_cal.get(Calendar.DATE)-subvalue;
		SimpleDateFormat sdf_startdate = new SimpleDateFormat("M/"+start_Date+"/YY");
		Date from_date = new Date();
		String strfrom_date = sdf_startdate.format(from_date).toString();
		System.out.println(strfrom_date);
		
		//Taking the End date from Current date
		Calendar to_cal = Calendar.getInstance();
		to_cal.setTime(date);
		int end_Date = to_cal.get(Calendar.DATE)+addvalue;
		SimpleDateFormat sdf_todate = new SimpleDateFormat("M/"+end_Date+"/YY");
		Date to_Date = new Date();
		String strto_date = sdf_todate.format(to_Date).toString();
		System.out.println(strto_date);
		String finaldate = strfrom_date+" - "+strto_date;
		System.out.println(finaldate);

		return finaldate;
	}
}
