package gamer;

import java.io.IOException;

public class RunCommand {
	public void run(String commad) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(commad);
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
