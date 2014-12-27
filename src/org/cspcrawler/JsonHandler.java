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
				BasicDBObject recordC = (BasicDBObject) record.get("content");

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

				compare(url, hashKey, pageJsonS, recordC);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			cursor.close();
		}
	}
	
	public void compare(String url, String hashKey, String pageJson, BasicDBObject recordC) throws IOException{
		HashMapInJson newJson = JsonAnalyzer.jsonFromString(pageJson);
		HashMapInJson oldJson = JsonAnalyzer.jsonFromString(recordC.toString());
		analyzer = new JsonAnalyzer(newJson, oldJson);
		
		//if found any blacklist 
		boolean jsListIsEmpty = analyzer.getJsComparisonResult().getBlackList().isEmpty();
		boolean cssListIsEmpty = analyzer.getCssComparisonResult().getBlackList().isEmpty();
        if (!jsListIsEmpty || !cssListIsEmpty) {
        	//insert the JSON into pageJson first
        	System.out.println("found black list, saving to database.");
        	/*
				System.out.println("*****************record content********************************************");
				System.out.println(recordC.toString());
				System.out.println("******************page Json************************************************");
				System.out.println(pageJson);
			*/

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
        	//System.out.println(cssBlackJson);

         	String cssWarningJson = gson.toJson(cssWarning);
        	//System.out.println(cssWarningJson);

        	String jsBlackJson = gson.toJson(jsBlack);
        	//System.out.println(jsBlackJson);

			String jsWarningJson = gson.toJson(jsWarning);
        	//System.out.println(jsWarningJson);

 			if(Main.sampleMode) {
			//update to pageJson collection
 				System.out.println("update page json collection...");
				dbDriver.pageJson.update(recordC, hashKey, cssBlackJson, cssWarningJson, jsBlackJson, jsWarningJson);
 				dbDriver.diffURL.insert(hashKey, url);
 				//dbDriver.diffLog.insert(hashKey, cssBlackJson, cssWarningJson, jsBlackJson, jsWarningJson);
 			}
 			
 			else{
 				//update to diffURL collection, increase count for the black url
 				dbDriver.diffURL.insert(hashKey, url);

 				//save to diffLog collection 
 				dbDriver.diffLog.insert(hashKey, cssBlackJson, cssWarningJson, jsBlackJson, jsWarningJson);
 			}
        	
        }
        //else, do nothing NOTE: here don't insert the file into the pageJson collection
        else{
        	System.out.println("no blacklist is found, so far so good. The warning list is ignored");
        }

	}
}
