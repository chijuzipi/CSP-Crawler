package org.cspcrawler;

public class Main {
	static boolean sampleMode = true;
	public static void main(String[] args) {
		try {
			//MongoDriver dbDriver = new MongoDriver();
			CSPCrawler craw = new CSPCrawler();
			Controller launcher = new Controller(10);
			launcher.start(craw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
