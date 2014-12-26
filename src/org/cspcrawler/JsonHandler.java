package org.cspcrawler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.cspapplier.json.*;
import org.cspcrawler.db.*;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

public class JsonHandler {
	private CSPMongoDriver dbDriver;
	private JsonAnalyzer analyzer;

	public JsonHandler(){
		try {
			dbDriver = new CSPMongoDriver();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void handle(String url, String hashKey, String pageJson){
		
		//first query if the hashKey exist in the database
		BasicDBObject query = new BasicDBObject("URLHash", hashKey);
		
		//query the pageJson collection
		DBCursor cursor = dbDriver.getCollection(0).find(query);

		//sort the query result by date, for the purpose of only compare with the latest record 
		cursor.sort(new BasicDBObject("date", -1));

		try {
			//if the hashKey does not exist in the db, insert directly
			if(cursor.count() == 0){
				System.out.println("the url does not exist, db updated");
				dbDriver.pageJson.insert(hashKey, pageJson);
				return;
			}
			
			//else, compare the new JSON with the latest record in the db, for the purpose of only compare with the latest record 
			else{
				System.out.println("the record existed, analyzing...");
				BasicDBObject record = (BasicDBObject)cursor.next();
				
				//only get the content portion for comparison
				String recordContent = record.get("content").toString();

				//reformat the the pageJson to mimic the record in the db
				BasicDBObject pageJsonFormat = (BasicDBObject)JSON.parse(pageJson);
				String pageJsonS = pageJsonFormat.toString();

				/* to see a detailed difference between the above 3 states
				System.out.println("*****************record string********************************************");
				System.out.println(recordString);
				System.out.println("*****************record content********************************************");
				System.out.println(recordContent);
				System.out.println("******************page Json**********************************************");
				System.out.println(pageJsonS);
				*/

				compare(url, hashKey, pageJsonS, recordContent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			cursor.close();
		}
	}
	
	public void compare(String url, String hashKey, String pageJson, String record) throws IOException{
		HashMapInJson newJson = JsonAnalyzer.jsonFromString(pageJson);
		HashMapInJson oldJson = JsonAnalyzer.jsonFromString(record);
		analyzer = new JsonAnalyzer(newJson, oldJson);
		
		//if found any difference
        if (!analyzer.isEmpty()) {
        	//insert the JSON into pageJson first
        	System.out.println("found differences, saving to database.");
				System.out.println();
				System.out.println("*****************record content********************************************");
				System.out.println(record);
				System.out.println();
				System.out.println("******************page Json************************************************");
				System.out.println(pageJson);
				System.out.println();

			//get the comparison result
        	ComparisonResult cssResult = analyzer.getCssComparisonResult();
        	ComparisonResult jsResult = analyzer.getJsComparisonResult();
        	HashMap<String, ArrayList<ElementInJson>> cssBlack = cssResult.getBlackList();
        	HashMap<String, DiffList> cssWarning = cssResult.getWarningList();
        	HashMap<String, ArrayList<ElementInJson>> jsBlack = jsResult.getBlackList();
        	HashMap<String, DiffList> jsWarning = jsResult.getWarningList();
        	
        	//parse it to JSON object
        	Gson gson = new Gson();
        	String cssBlackJson = gson.toJson(cssBlack);
         	String cssWarningJson = gson.toJson(cssWarning);
        	String jsBlackJson = gson.toJson(jsBlack);
			String jsWarningJson = gson.toJson(jsWarning);

 			if(Main.sampleMode) {
			//update to pageJson collection
 				System.out.println("update page json collection");
				dbDriver.pageJson.update(hashKey, cssBlackJson, cssWarningJson, jsBlackJson, jsWarningJson);
 			}
 			
 			else{
 				//update to diffURL collection
 				dbDriver.diffURL.insert(hashKey, url);

 				//save to diffLog collection 
 				dbDriver.diffLog.insert(hashKey, cssBlackJson, cssWarningJson, jsBlackJson, jsWarningJson);
 			}
        	
        }
        //else, do nothing NOTE: here don't insert the file into the pageJson collection
        else{
        	System.out.println("no differences is found, so far so good.");
        }

	}
}
