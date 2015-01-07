package org.cspcrawler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.cspcrawler.diff_match_patch.Diff;
import org.cspcrawler.diff_match_patch.Operation;
import org.cspcrawler.db.*;

import com.google.gson.Gson;
import com.mongodb.AggregationOptions;
//import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DynamScriptAnalyzer {
	private CSPMongoDriver dbDriver;
	private DBCollection coll;
	//static int statC;// limit how many url write to the file
	PrintWriter writer;
	public DynamScriptAnalyzer() throws FileNotFoundException, UnsupportedEncodingException{
		writer = new PrintWriter("diff_result.txt", "UTF-8");
		try {
			dbDriver = new CSPMongoDriver();
			coll = dbDriver.diffLog.getCollection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void find(){

		// create our pipeline operations, first with the $match
		DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$URLHash").append("total",
						 new BasicDBObject("$sum", 1)));

		AggregationOptions aggregationOptions = AggregationOptions.builder()
		        .batchSize(100)
		        .outputMode(AggregationOptions.OutputMode.CURSOR)
		        .allowDiskUse(true)
		        .build();

		List<DBObject> pipeline = Arrays.asList(group);

		Cursor cursor = coll.aggregate(pipeline, aggregationOptions);
		while (cursor.hasNext()) {
			BasicDBObject curr = (BasicDBObject)cursor.next();
			if(curr.get("total").equals(2)){
				//statC++;
				String URLHash = curr.getString("_id");
				String currUrl = curr.getString("url");
				BasicDBObject query = new BasicDBObject("URLHash", URLHash);

				Cursor cursor2 = coll.find(query);

				ArrayList<ArrayList<String>> cssMainList = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> jsMainList = new ArrayList<ArrayList<String>>();
				try {
				   while(cursor2.hasNext()) {
					   ArrayList<String> cssList = new ArrayList<String>();
					   ArrayList<String> jsList = new ArrayList<String>();
					   DBObject now = cursor2.next();
					   currUrl = now.get("url").toString();

						   DBObject innerJs = ((DBObject) now.get("jsBlackList"));
						   DBObject innerCss = ((DBObject) now.get("cssBlackList"));
						   if(innerJs != null){
						   for(String k : innerJs.keySet()){
							   String diffJS = innerJs.get(k).toString();
							   jsList.add(diffJS);
						   }
						   	jsMainList.add(jsList);
						   }

						   if(innerCss != null){
						   for(String k : innerCss.keySet()){
							   String diffCSS = innerCss.get(k).toString();
							   cssList.add(diffCSS);
						   }
						   	cssMainList.add(cssList);
						   }

				   }
				} finally {
				   cursor2.close();
				}
				try {
					diff(currUrl, cssMainList, jsMainList);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				if(statC > 50)
				break;
				*/
			}		
		}

		writer.close();
	}
	
	public void diff(String url, ArrayList<ArrayList<String>> css, ArrayList<ArrayList<String>> js) throws FileNotFoundException, UnsupportedEncodingException{
		//diff_match_patch diff = new diff_match_patch();
		writer.println();
		writer.println();
		writer.println("the URL ---------------> " + url);
		writer.println();
		writer.println("******************* js *******************");
		if(js.size() > 1){
			ArrayList<String> js1 = js.get(0);
			ArrayList<String> js2 = js.get(1);
			for(int i = 0; i < js1.size(); i++){
				for(int j = 0; j < js2.size(); j++){
					String string1 = js1.get(i);
					String string2 = js2.get(j);
					List<String> smallList1 = Arrays.asList(string1.split(":"));
					List<String> smallList2 = Arrays.asList(string2.split(":"));
					//get the xpath, only printout the strings with xpath equal
					if(smallList1.get(6).equals(smallList2.get(6))){
						writer.println("#1------>" + string1);
						writer.println("#2------>" + string2);
					}
				}
				/* using diff is too diffucult to see for human
				LinkedList<Diff> result = diff.diff_main(string1, string2);
				diff.diff_cleanupSemantic(result);
				*/
			writer.println();
			}
		}
		writer.println("******************* css *******************");
		if(css.size() > 1){
			ArrayList<String> css1 = css.get(0);
			ArrayList<String> css2 = css.get(1);
			for(int i = 0; i < css1.size(); i++){
				for(int j = 0; j < css2.size(); j++){
					String string1 = css1.get(i);
					String string2 = css2.get(j);
					List<String> smallList1 = Arrays.asList(string1.split(":"));
					List<String> smallList2 = Arrays.asList(string2.split(":"));

					if(smallList1.get(6).equals(smallList2.get(6))){
						writer.println("------>" + string1);
						writer.println("------>" + string2);
					}
				}
			writer.println();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		DynamScriptAnalyzer a = new DynamScriptAnalyzer();
		a.find();
	}
}
