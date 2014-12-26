package org.cspcrawler.db;

import com.mongodb.*;
import com.mongodb.util.JSON;

public class PageJsonColl extends CSPCollection{

	protected DBCollection pageJson;

	public PageJsonColl(DB db){
		super();
		//NOTE the collection name format
		pageJson = db.getCollection("Page_Json");
	}
	
	//insert to pageJson collection
	public void insert(String key, String json){
		BasicDBObject doc = (BasicDBObject)JSON.parse(json);

		String date = CSPMongoDriver.getDate();

		//wrap the document with date and hashKey;
		BasicDBObject docInsert = new BasicDBObject("URLHash", key).
										append("content", doc).
										append("date", date);
		pageJson.insert(docInsert);

	}
	
	public DBCollection getCollection(){
		return pageJson;
	}
	
	public void query(){
		
	}
	
	public void update(){
		
	}
}

