package gamer;


public class GetScreencap {
	RunCommand runCommand = new RunCommand();
	

	public byte[] getScreencap(String timeStamp) {
		
		String currentPath = Main.basePath + Main.GAMEROUNDSTR + Main.gameRoundNow + "\\";
		
		runCommand.run("C:\\Users\\think\\AppData\\Roaming\\Tencent\\AndroidServer"
				+ "\\1.0.0.514\\tadb shell screencap -p /sdcard/Download/myscreencap/" + timeStamp + ".png");

		runCommand.run("C:\\Users\\think\\AppData\\Roaming\\Tencent\\AndroidServer"
				+ "\\1.0.0.514\\tadb pull /sdcard/Download/myscreencap/" + timeStamp + ".png "
				+ "C:\\Users\\think\\Desktop\\screencap\\" +Main.GAMEROUNDSTR + Main.gameRoundNow+"\\" + timeStamp + ".png");
//		System.out.println("get screencap : " + timeStamp + ".png");
		return null;
	}

}
