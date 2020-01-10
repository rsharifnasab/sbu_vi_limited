import java.util.Scanner; // my only and lovely input getter
import java.io.*;

class ArgumentParser{
	public static final String BAD_INPUT_HINT = "bad arguments\n" +
		"usage : \'java Vim a.txt\' OR \'java Vim\'";

	private String[] args;
	public ArgumentParser(String[] args){
		this.args = args;
	}

	public Boolean check(){
		if (args == null)
			return false;
		if(args.length ==  0)
			return true;
		if(args.length > 1)
			return false;
		return true;
	}

	public String getFileName(){
		if( args.length == 1)
			return args[0];
		return null;
	}
}


enum FileStatus{
	WRITABLE, // the file is ok and ready to write
	NOT_OK, // can not open file for writing
	NOT_EXISTS, // the file doesnt exists
	IS_DIR, // requested file is directory
	NULL_YET // requested address is null
}

class FilesUtil{

	public static Boolean canCreateFile(String fileName){
		try {
			File f = new File(fileName);
			if(f.exists())
				return false;
			f.createNewFile();
			f.delete(); // check if we can create it or not
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static FileStatus getFileState(String fileName){
		if(fileName == null)
			return FileStatus.NULL_YET;

		File f = new File(fileName);

		if(f.isDirectory())
			return FileStatus.IS_DIR;
		if (f.exists() && f.canWrite())
			return FileStatus.WRITABLE;
		if(!f.exists() && canCreateFile(fileName))
			return FileStatus.NOT_EXISTS; // but it can be created
		return FileStatus.NOT_OK;
	}

	public static Boolean isWritableFile(String fileName){
		return getFileState(fileName) == FileStatus.WRITABLE;
	}

}

class PrintUtil{
	public static final String ESCAPE = ""+(char) 27 ;
	public static final String Reset = ESCAPE + "[0m";

	public static final String Red = ESCAPE + "[31m";
	public static final String Blue = ESCAPE +  "[94m";
	public static final String Green = ESCAPE +  "[32m";
	public static final String Yellow = ESCAPE +  "[93m";
	public static final String White = ESCAPE +  "[97m";

	public static final String CLEARER = "\033[H\033[2J";

	public static void print(String toPrint, Color color){
		String colorer = White;
		switch (color){
			case RED:
				colorer = Red; break;
			case BLUE:
				colorer = Blue; break;
			case GREEN:
				colorer = Green; break;
			case YELLOW:
				colorer = Yellow; break;
			default:
				colorer = White;
		}
		System.out.println( colorer + toPrint + Reset );
		System.out.flush();
	}

	public static void PError(String errorText){
		print(errorText, Color.RED);
	}

	public static void clearScreen() {
    	System.out.print(CLEARER);
    	System.out.flush();
	}

	public static void makeTerminalHandy(){
		try{
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty -icanon min 1 </dev/tty"}).waitFor();
			//PrintUtil.print("making terminal handy!" ,Color.YELLOW);
		}
		catch (Exception e){
			PrintUtil.PError("couldnt make terminal handy!");
			System.exit(1);
		}
	}


	public static void makeTerminalNormal(){
		try{
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty icanon </dev/tty"});
		}
		catch (Exception e){
			PrintUtil.PError("couldnt make terminal normal!");
			System.exit(1);
		}
	}

}

enum Color{	RED, GREEN, BLUE, YELLOW, WHITE }

public class Vim{
	private static String getFileNameFromArgs(String[] args){

		ArgumentParser argParse = new ArgumentParser(args);
		if (!argParse.check()){ // bad input
			PrintUtil.PError(ArgumentParser.BAD_INPUT_HINT);
			System.exit(1);
		}

		String fileName = argParse.getFileName(); // null if : usage : "vim"
		FileStatus status = FilesUtil.getFileState( argParse.getFileName());
		switch (status){
			case WRITABLE:
			case NOT_EXISTS:
			case NULL_YET:
				return fileName;

			case IS_DIR:
				PrintUtil.PError("error: target is directory: "+fileName);
				System.exit(1);

			case NOT_OK:
				PrintUtil.PError("error: cant open input file: "+fileName);
				System.exit(1);

			default:
				throw new RuntimeException("should not reach here!, probalby bad FILESTATUS enum");
		}
	}



	public String ourFile;
	private Scanner ourScanner;
	public Vim(String ourFile, Scanner scanner){
		this.ourFile = ourFile;
		this.ourScanner = scanner;

		PrintUtil.clearScreen();

		PrintUtil.print("initializing vim!", Color.GREEN);
		PrintUtil.makeTerminalHandy();
		PrintUtil.print("input file is : " + ourFile, Color.BLUE);
		PrintUtil.print("our scanner is: " + ourScanner.getClass().getName(), Color.BLUE);

	}

	public Vim(String[] args, Scanner scanner){
		this( getFileNameFromArgs(args) , scanner);
	}

	private Boolean appShouldExit(String input){
		if (
		"exit".equalsIgnoreCase(input) ||
		"quit".equalsIgnoreCase(input) ||
		":q!".equalsIgnoreCase(input)
		)
			return true;

		return false;
	}


	public void run() throws IOException{
		PrintUtil.print("the app started succesfully", Color.GREEN);

		int input;
        while ((input = System.in.read()) != -1){
			// work with input
			System.out.println( (char) input );
		}


	//	System.out.println("save cursor pos : \u001b[s \n ");
	//	System.out.println("request cursor pos : \u001b[6n \n" );
	//	System.out.println("change pos : \u001b[u \n" );
/*
		"\u001b[s"             // save cursor position
		"\u001b[5000;5000H"    // move to col 5000 row 5000
		"\u001b[6n"            // request cursor position
		"\u001b[u"
*/

		PrintUtil.makeTerminalNormal();

	}



	public static void main(String[] args){

		Scanner scanner = new Scanner(System.in);

		Vim app = new Vim(args,scanner);

		try{
			app.run();
		}catch(IOException e){
			PrintUtil.PError("error in getting character");
		}


		scanner.close();
	}
}
