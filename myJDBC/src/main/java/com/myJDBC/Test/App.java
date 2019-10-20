package com.myJDBC.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App {
	static List<String> insertScriptWithColumnNames = new ArrayList<String>();

	public static void main(String[] args) throws IOException, NumberFormatException, SQLException {
		InsertData();
	}

	private static void InsertData() throws IOException, NumberFormatException, SQLException {
		String databaseName = "";
		String schemaName = "";
		String tableName = "";
		String apiName = "";
		Map<String, String> columnNames = new LinkedHashMap<>(); // Column name,
																	// data type
		String[] tempArray = new String[3];
		String columnName = "";
		String dataType = "";

		BufferedReader br = new BufferedReader(new FileReader(
				System.getProperty("user.dir") + "/Mapping.txt"));
		String line = br.readLine();
		while (line != null) {
			if (line.contains("#")) {
				String temp = line
						.substring(line.indexOf('#') + 1, line.indexOf('='))
						.toString().trim();
				System.out.println(temp);
				tempArray = temp.split("\\.");
				System.out.println(tempArray.length);
				databaseName = tempArray[0];
				schemaName = tempArray[1];
				tableName = tempArray[2];

				apiName = line.substring(line.indexOf('=') + 1).trim();

				// System.out.println("database: " + databaseName +
				// " schemaName: " + schemaName + " tableName: " + tableName);
				// System.out.println("API name is: " + apiName);
			} else {
				if (!line.isEmpty()) {
					columnName = line.substring(0, line.indexOf('(')).trim();
					dataType = line.substring(line.indexOf('(') + 1,
							line.lastIndexOf(')')).trim();

					columnNames.put(columnName, dataType);
				}
			}
			line = br.readLine();
			if (line == null || line.contains("#")) {
				// System.out.println("database: " + databaseName +
				// " schemaName: " + schemaName + " : tableName: " + tableName);
				// System.out.println("API name is: " + apiName);

				Iterator colIterator = columnNames.entrySet().iterator();
				columnName = "Insert into " + databaseName + "." + schemaName
						+ "." + tableName + "(";
				while (colIterator.hasNext()) {
					Entry e = (Entry) colIterator.next();
					// System.out.println(e.getKey() + ": " + e.getValue() +
					// ": " + targetIterator.next().toString());
					columnName = columnName + e.getKey();

					if (colIterator.hasNext()) {
						columnName = columnName + ", ";
					} else {
						columnName = columnName + ") values ( ";
						int j = 0;
						int i = columnNames.size();
						while (j < i - 1) {
							columnName = columnName + "?, ";
							j++;
						}
						columnName = columnName + "? )";

					}
				}

				insertScriptWithColumnNames.add(columnName);

				insertDataInTables(insertScriptWithColumnNames, columnNames, apiName);

				// System.out.println(columnName);
				insertScriptWithColumnNames.clear();
				columnName = "";
				columnNames.clear();
			}
		}

	}

	public static void insertDataInTables(List<String> insertScriptWithColumnNames, Map<String, String> columnNames, String apiName) throws NumberFormatException, SQLException
	{
		Iterator<Entry<String, String>> colIterator = columnNames.entrySet().iterator();
		List<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		int i = 0, j = 0;
		System.out.println(App.insertScriptWithColumnNames.get(0));
		while(i<3)
		{
			ArrayList<String> l = new ArrayList<String>();
			while(j<columnNames.size())
			{
				l.add(j++, String.valueOf((new Random()).nextInt()));
			}

			
			dataList.add(i++, l);
		}
		
		i = 0;
		
		PreparedStatement pstmt = getConnection();
		
		while(colIterator.hasNext())
		{
			Entry e = (Entry) colIterator.next();
			System.out.println(e.getValue() + " : " + dataList.get(0).get(i++));
			String dataValue = dataList.get(0).get(i);
			
			if(e.getValue().toString().contains("Integer") )
			{
				pstmt.setInt(i, Integer.parseInt(dataValue));
			}
			else if(e.getValue().toString().contains("BigInteger"))
			{
				pstmt.setLong(i, Long.parseLong(dataValue));
			}
			else if(e.getValue().toString().contains("boolean"))
			{
				pstmt.setBoolean(i, Boolean.parseBoolean(dataValue));
			}
			else if(e.getValue().toString().contains("Varying") || e.getValue().toString().contains("text"))
			{
				pstmt.setString(i, dataValue);
			}
			else if(e.getValue().toString().contains("Float"))
			{
				pstmt.setFloat(i, Float.parseFloat(dataValue));
			}
			else if(e.getValue().toString().contains("Double"))
			{
				pstmt.setDouble(i, Double.parseDouble(dataValue));
			}
			else if(e.getValue().toString().contains("Timestamp"))
			{
				pstmt.setTimestamp(i, getTimestamp(dataValue));
			}
			else if(e.getValue().toString().contains("Date"))
			{
				pstmt.setDate(i, getDate(dataValue));
			}
			else if(e.getValue().toString().contains("NULL"))
			{
				pstmt.setNull(i, java.sql.Types.NULL);
			}
			else if(e.getValue().toString().contains("Json"))
			{
//				PGobject jsonObject = new PGobject();
//				 jsonObject.setType("json");
//				 jsonObject.setValue(jsonStr);
//				 statement.setObject(1, jsonObject);
				pstmt.setString(i, dataValue);
			}
				
				// else if(e.getValue().toString().contains(""))
		i++;
		}
		
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

	public static PreparedStatement getConnection() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager.getConnection("", "", "");
			pstmt = conn.prepareStatement("",
					Statement.RETURN_GENERATED_KEYS);
			return pstmt;
		} catch (Exception e) {
			return pstmt;
		}

	}
}
