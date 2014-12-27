package org.cspcrawler;
import java.io.*;
//import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.*;
import edu.uci.ics.crawler4j.url.*;

import org.cspapplier.*;

public class CSPCrawler extends WebCrawler{
	private PageJsonGenerator jsonGen; //analyze and transform HTML to JSON, it uses the JsonAnalyzer class
	private JsonHandler jsonHandler = new JsonHandler(); // the handler for the JSON


	/*
	public CSPCrawler(){
		super();
		jsonHandler = new JsonHandler();
	}
	*/

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

/**
* You should implement this function to specify whether
* the given url should be crawled or not (based on your
* crawling logic).
*/
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches();
	}

/**
* This function is called when a page is fetched and ready 
* to be processed by your program.
*/
	@Override
	public void visit(Page page) {          
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();

			try {
				//pass the strings to page generator, to get the strings of hashURL and PageJson
				jsonGen = new PageJsonGenerator(html, url);
				String hashURL = jsonGen.getHashURL();
				String pageJson = jsonGen.getPageJson();
				
				//pass the strings to handler
				jsonHandler.handle(url, hashURL, pageJson);

				/* to output html to loca, for analysis purpose
				String fileName = Integer.toString(1);
				PrintWriter out = new PrintWriter(fileName+ ".html", "UTF-8");
				out.println(html);
				out.close();
				*/
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<WebURL> links = htmlParseData.getOutgoingUrls();
			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
			System.out.println();
		}
	}
};
