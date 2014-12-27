package org.cspcrawler;

//import java.util.Timer;

public class Main {
	static boolean sampleMode = true;
	public static void main(String[] args) {
		try {
			if(args.length != 4){
				System.out.println("Usage: java Main [crawlerNum] [depth] [maxPageNum] [String seed]");
				return;
			}

			int crawlerNum = Integer.parseInt(args[0]);
			int depth = Integer.parseInt(args[1]);
			int maxPageNum = Integer.parseInt(args[2]);
			String seed = args[3];

			/*
			set up timer for peoridically execute the program 
			REF: http://www.mkyong.com/java/how-to-run-a-task-periodically-in-java/
			Timer time = new Timer();
			Controller launcher = new Controller(1);
			time.schedule(launcher, 0, 100000);
			*/

			Controller launcher = new Controller(crawlerNum, depth, maxPageNum, seed);
			launcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
