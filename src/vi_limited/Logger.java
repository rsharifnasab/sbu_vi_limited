package vi_limited;
import java.io.IOException;
/**
	our logger class to write on an external file
**/
class Logger{
	private static String[] command = new String[3];
	private static final String LOG_FILE = "./log1.txt";
	static{
		command[0] = "/bin/sh";
		command[1] = "-c";
	}
	/**
		log the toWrite String to : LOG_FILE
	**/
	public static void log(String toWrite){
		command[2] = "echo " + "\"" + toWrite + "\"" + " >> " + LOG_FILE;
		try{
			Runtime.getRuntime().exec(command).waitFor();
		} catch( InterruptedException | IOException e ){
			TUtil.clearConsule();
			e.printStackTrace();
			TUtil.PError("can not write to log file!");
		}
	}

}
