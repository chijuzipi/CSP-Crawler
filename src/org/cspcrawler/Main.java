package org.cspcrawler;

public class Main {
	static boolean sampleMode = true;
	public static void main(String[] args) {
		try {
			if(args.length != 4){
				System.out.println("Usage: java Main [crawlerNum] [depth] [maxPageNum] [String seed]");
				CSPCrawlController launcher = new CSPCrawlController();
				launcher.run();
			}
			
			else{

			int crawlerNum = Integer.parseInt(args[0]);
			int depth = Integer.parseInt(args[1]);
			int maxPageNum = Integer.parseInt(args[2]);
			String seed = args[3];

			CSPCrawlController launcher = new CSPCrawlController(crawlerNum, depth, maxPageNum, seed);
			launcher.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
