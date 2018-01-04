package gamer;

import java.util.Random;

public class InputTouchEvent {
	int defaultx = 540;
	int defaulty = 1000;
	int randomRadius = 100;
	Random random = new Random();
	RunCommand runCommand = new RunCommand();

	public void input(int timeLen) {
		input(defaultx + random.nextInt(randomRadius), defaulty + random.nextInt(randomRadius), timeLen);
	}

	private void input(int x, int y, int timeLen) {
		runCommand.run("C:\\Users\\think\\AppData\\Roaming\\Tencent\\AndroidServer\\1.0.0.514\\tadb shell input swipe " + x
				+ " " + y + " " + x + " " + y + " " + timeLen);
//		System.out.println("touch screen x = " + x + " y = " + y + " " + "timelen = " + timeLen);
	}

}
