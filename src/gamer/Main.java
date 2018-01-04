package gamer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Main {
	public static String basePath ="C:\\Users\\think\\Desktop\\screencap\\";
	public static String gameRoundNow = "game1";
	public static String GAMEROUNDSTR = "game-round-";
	public static void main(String[] args) {
		Main main = new Main();
		main.runGame();
//		main.test();
		
	}
	
	
	private void runGame() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		gameRoundNow = simpleDateFormat.format(new Date());
		File dir = new File(basePath + GAMEROUNDSTR+ gameRoundNow);
		if(dir.exists()) {
			System.out.println("dir " + basePath + GAMEROUNDSTR + gameRoundNow + "already exist.delete it before run this program.");
			return;
		}else {
			dir.mkdirs();
		}
		
		
		GetScreencap getScreencap = new GetScreencap();
		InputTouchEvent inputTouchEvent = new InputTouchEvent();
		PictureProcess pictureProcess = null;
		try {
			pictureProcess = new PictureProcess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		int i = 233;
		inputTouchEvent.input(750);
		Random random = new Random();
		while(i-- > 0) {
			
			try {
				
				Thread.sleep(1300 + random.nextInt(10));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			String timeStamp = new Date().getTime()+"";
			getScreencap.getScreencap(timeStamp);
			
			double len = 0;
			try {

				String currentPath = Main.basePath + Main.GAMEROUNDSTR + Main.gameRoundNow + "\\";
				len = pictureProcess.getDistance(currentPath , timeStamp);
				if(len <0) {
					System.out.println("gamer over");
					return ;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			double k =1.41;
			double timeLen = len * k;
			inputTouchEvent.input((int)timeLen);

			System.out.println("==========================================");
			
		}
		
		System.out.println("done!");

	
	}

	private void test() {
		String currentPath = Main.basePath + "testPicture\\";

		PictureProcess pictureProcess = null;
		try {
			pictureProcess = new PictureProcess();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File dir =new File(currentPath);
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File item : files) {
				if(item.isFile()) {
					String path = item.getPath();
					char c = path.charAt(path.length()-5);
					if(c>='0' && c<='9') {
						try {

							pictureProcess.getDistance(currentPath , path.substring(path.length()-17, path.length()-4));

						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				}
			}
			
		}else {
			System.out.println(currentPath + " is not a directory");
			return;
		}
		
		System.out.println("done");
		
	}
}
