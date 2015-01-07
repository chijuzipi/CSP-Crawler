package org.cspcrawler;
import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;
import edu.uci.ics.crawler4j.crawler.*;

import java.util.TimerTask;
import java.util.Date;

//import org.eclipse.wst.jsdt.core.dom.ASTParser;

public class CSPCrawlController extends TimerTask{
	private CrawlController controller;
	private CrawlConfig config;
	private int numberOfCrawlers;
	Date now;

	public CSPCrawlController(int numOfCrawler, int depth, int maxPageNum, String seed) throws Exception{
		    String crawlStorageFolder = "/data/crawl/root";
		    numberOfCrawlers = numOfCrawler;
		    
		    
		    config = new CrawlConfig();
		    config.setCrawlStorageFolder(crawlStorageFolder);
		    config.setMaxDepthOfCrawling(depth);

		    config.setMaxPagesToFetch(maxPageNum);
		    config.setIncludeHttpsPages(true);

		    System.out.println(config.toString());

		    PageFetcher pageFetcher = new PageFetcher(config);
		    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		    controller = new CrawlController(config, pageFetcher, robotstxtServer);

		    controller.addSeed(seed);

	  }

	public CSPCrawlController() throws Exception{
		    String crawlStorageFolder = "/data/crawl/root";
		    numberOfCrawlers = 5;
		    
		    
		    config = new CrawlConfig();
		    config.setCrawlStorageFolder(crawlStorageFolder);
		    config.setMaxDepthOfCrawling(1);

		    config.setMaxPagesToFetch(3000);
		    config.setIncludeHttpsPages(true);

		    System.out.println(config.toString());

		    PageFetcher pageFetcher = new PageFetcher(config);
		    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		    controller = new CrawlController(config, pageFetcher, robotstxtServer);

		    controller.addSeed("http://www.yahoo.com");
		    controller.addSeed("http://www.amazon.com");
		    controller.addSeed("http://www.google.com");
		    controller.addSeed("http://www.wikipedia.org");
		    controller.addSeed("http://www.cnn.com");
		    controller.addSeed("http://www.northwestern.edu/");
	}

	public void run(){
		now = new Date();
		System.out.println("Time is :" + now);
		controller.start(CSPCrawler.class, numberOfCrawlers);    
	}
		
}
