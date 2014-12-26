package org.cspcrawler.db;
import java.net.UnknownHostException;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.mongodb.*;

public class CSPMongoDriver {
	protected DB db;
	public PageJsonColl pageJson;
	public DiffLogColl diffLog; 
	public DiffURLColl diffURL;

	public CSPMongoDriver() throws UnknownHostException{
        MongoClient mongoClient = new MongoClient( "localhost", 27017);
        db = mongoClient.getDB("cspdb");

        //#1 collection, store page json
        pageJson = new PageJsonColl(db);

        //#2 collection, store difference list for each url
        diffLog = new DiffLogColl(db);

        //#3 collection, store difference url and the differnce count
        diffURL = new DiffURLColl(db);
	}

	public DBCollection getCollection(int dbIndex){
		if(dbIndex == 0)
			return pageJson.getCollection();
		else if(dbIndex == 1)
			return diffLog.getCollection();
		else if(dbIndex == 2)
			return diffURL.getCollection();
		else
			return null;
	}

	public static String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String output = dateFormat.format(cal.getTime()); //2014/08/06 16:00:22
		return output;
	}

}//end of class
