package com.myJDBC.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MyDateAndTimestampConversion {
	public static void main(String[] args) {
		System.out.println(getTimestamp("TIME.NOW"));
		System.out.println(getTimestamp("2019-12-13 12:12:23"));
		System.out.println(getDate("CURRENT_DATE"));
		System.out.println(getDate("2019-12-23"));
	}
	
	private static Timestamp getTimestamp(String dataValue) {
		if(dataValue.equals("TIME.NOW"))
		{
			System.out.println("current timestamp is: " + new Timestamp((new java.util.Date()).getTime()));
			return Timestamp.valueOf(LocalDateTime.now());
		}
		else
		{
			return Timestamp.valueOf(dataValue);
		}
	}

	private static Date getDate(String dataValue) {
		if(dataValue.equals("CURRENT_DATE"))
		{
			return new Date((new java.util.Date().getTime()));
		}
		else
		{
			return Date.valueOf(dataValue);
		}
	}

}
