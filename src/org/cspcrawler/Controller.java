package org.cspcrawler;
import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;
import edu.uci.ics.crawler4j.crawler.*;

public class Controller {
	private CrawlController controller;
	private CrawlConfig config;
	private int numberOfCrawlers;

	public Controller(int numOfCrawler) throws Exception{
		    String crawlStorageFolder = "/data/crawl/root";
		    numberOfCrawlers = numOfCrawler;
		    
		    
		    config = new CrawlConfig();
		    config.setCrawlStorageFolder(crawlStorageFolder);
		    config.setMaxDepthOfCrawling(2);

		    config.setMaxPagesToFetch(200);

		    System.out.println(config.toString());

		    PageFetcher pageFetcher = new PageFetcher(config);
		    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		    controller = new CrawlController(config, pageFetcher, robotstxtServer);

		    //controller.addSeed("http://www.wikipedia.org");
		    //controller.addSeed("http://www.cnn.com");
		    //controller.addSeed("http://www.google.com");
		    controller.addSeed("http://www.amazon.com");
		    //controller.addSeed("http://www.northwestern.edu/");
	  }
	
	public void start(CSPCrawler craw){
		controller.start(craw.getClass(), numberOfCrawlers);    
	}
		
}
