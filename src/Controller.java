import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;
import edu.uci.ics.crawler4j.crawler.*;

public class Controller {
	  public static void main(String[] args) throws Exception {
		    String crawlStorageFolder = "/data/crawl/root";
		    int numberOfCrawlers = 7;

		    CrawlConfig config = new CrawlConfig();
		    config.setCrawlStorageFolder(crawlStorageFolder);
		    config.setMaxDepthOfCrawling(1);
		    System.out.println(config.toString());

		    PageFetcher pageFetcher = new PageFetcher(config);
		    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		    //controller.addSeed("http://www.wikipedia.org");
		    //controller.addSeed("http://www.cnn.com");
		    //controller.addSeed("http://www.google.com");
		    controller.addSeed("http://www.northwestern.edu/");
		    controller.start(MyCrawler.class, numberOfCrawlers);    
	  }
}
