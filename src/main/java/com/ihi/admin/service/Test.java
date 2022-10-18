package com.ihi.admin.service;

import java.time.LocalDate;
import java.util.Calendar;

public class Test {

	public static void main(String[] args) {
		
//		Calendar calendar = Calendar.getInstance();
//		int yearpart = 2010;
//		int monthPart = 01;
//		int dateDay =1;
//		calendar.set(yearpart, monthPart, dateDay);
//		int numOfDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//		System.out.println("Number of Days: " + numOfDaysInMonth);
//		System.out.println("First Day of month: " + calendar.getTime());
//		calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth-1);
//		System.out.println("Last Day of month: " + calendar.getTime());
		
		
//		 Calendar gc = new GregorianCalendar();
////	        gc.set(Calendar.MONTH, month);
////	        gc.set(Calendar.DAY_OF_MONTH, 1);
//	        gc.set(yearpart, monthPart,dateDay);
//	        Date monthStart = gc.getTime();
//	        gc.add(Calendar.MONTH, 1);
//	        gc.add(Calendar.DAY_OF_MONTH, -1);
//	        Date monthEnd = gc.getTime();
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//
//	        System.out.println("Calculated month start date : " + format.format(monthStart));
//	        System.out.println("Calculated month end date : " + format.format(monthEnd));
		
		LocalDate initial = LocalDate.of(2014,1,1);
		LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.getMonth().length(initial.isLeapYear()));
		System.out.println(start.toString());
		System.out.println(end.toString());
		System.out.println(org.joda.time.LocalDate.now());
		
	}
}
