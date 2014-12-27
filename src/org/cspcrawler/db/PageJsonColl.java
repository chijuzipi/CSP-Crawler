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

	public void update(BasicDBObject recordC, String key, String cssBlack, String cssWarn, String jsBlack, String jsWarn){	
		DBObject doc1 = (BasicDBObject)JSON.parse(cssBlack);
		//DBObject doc2 = (BasicDBObject)JSON.parse(cssWarn);
		DBObject doc3 = (BasicDBObject)JSON.parse(jsBlack);
		//DBObject doc4 = (BasicDBObject)JSON.parse(jsWarn);

		
		//combine the existing record with the css black list
		//REF: http://stackoverflow.com/questions/7713753/mongodb-merging-two-dbobjects
		DBObject newContentCss = (BasicDBObject) (recordC.get("css"));
		newContentCss.putAll(doc1);

		//combine the existing record with the js black list
		DBObject newContentJs = (BasicDBObject) (recordC.get("js"));
		newContentJs.putAll(doc3);


		BasicDBObject newContent = new BasicDBObject().append("js", newContentJs).append("css", newContentCss);

		BasicDBObject newDoc = new BasicDBObject();
		newDoc.append("$set", new BasicDBObject().append("content", newContent));
			
		//construct the query
		BasicDBObject searchQuery = new BasicDBObject().append("URLHash", key);

		//update
		pageJson.update(searchQuery, newDoc);

		//BasicDBObject origJs = (BasicDBObject) recordC.get("js");
		//BasicDBObject origCss = (BasicDBObject) recordC.get("css");
		//String date = CSPMongoDriver.getDate();

		/*
		//update the document 
		//Ref: http://www.mkyong.com/mongodb/java-mongodb-update-document/
		newDoc.append("$set", new BasicDBObject().append("content", 110));
		*/
	}
}

