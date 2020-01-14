import java.io.*;

enum EditorMode{
	STATISTICS, INSERT, COMMAND
}

enum Key{
		UP, DOWN, LEFT, RIGHT, ESC, UNK
}

/*

	public Key get() {
			if (ans==null) return Key.ESC;
			Logger.log("in get, ans = " + ans);
			switch(ans){

				case "[A": return Key.UP;
				case "[B": return Key.DOWN;
				case "[C": return Key.RIGHT;
				case "[D": return Key.LEFT;

				default:
					return Key.UNK;
			}
*/

// TUtil as wr have in Terminal utilities
class TUtil{

	public static final String ESCAPE = ""+(char) 27 ;
	public static final String Reset = ESCAPE + "[0m";

	public static final String Red = ESCAPE + "[31m";
	public static final String Blue = ESCAPE +  "[94m";
	public static final String Green = ESCAPE +  "[32m";
	public static final String Yellow = ESCAPE +  "[93m";
	public static final String White = ESCAPE +  "[97m";

	public static final String CLEARER = "\033[H\033[2J";

	public static final String LINE_DELETER = "\u001b[K";

	// print the toPrint string with the specified color
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

	// print an error in red and exit whole program
	public static void PError(String errorText){
		clearConsule();
		print(errorText, Color.RED);
		System.exit(0);
	}

	public static void clearConsule() {
    	System.out.print(CLEARER);
    	System.out.flush();
	}

	public static void clearConsuleC(Cursor c){
		Logger.log("clearing with cursor!");
		c.reset();
		for (int i =1; i <= c.height; i++ ) {
			TUtil.deleteThisLine(c);
			c.down();
		}
		c.reset();
	}

	// we can handle that in makefile
	public static void makeTerminalHandy(){
		try{
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty -icanon min 1 </dev/tty"}).waitFor();
			TUtil.print("making terminal handy (no buffer)!" ,Color.YELLOW);
		}
		catch (Exception e){
			TUtil.PError("couldnt change terminal buffer!");
		}
	}

	// we can handle that in makefile
	public static void makeTerminalNormal(){
		try{
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty icanon </dev/tty"});
		}
		catch (Exception e){
			TUtil.PError("couldnt make terminal normal!");
		}
	}

	public static char getChar(){ // tricky sensetive code
		try{
			byte[] data  = new byte[3];
			int bytesRead = System.in.read(data);
			if (bytesRead == 3)// arrow key found
				return ETCUtil.arrowKeyToChar(data);  // 3 char for arrow keys
			else  // otherwise : one character
				return (char) (data[0]);
		} catch(IOException e){
			TUtil.PError("error in getting char from user with system.in\nexiting");
		}
		return ' '; // should not reach here
	}

	@Deprecated
	public static void printTerminalSize(){
		System.out.print("\u001b[s");  // save cursor position
		System.out.print("\u001b[5000;5000H"); // move to col 5000 row 5000
		System.out.println("\u001b[6n");  // request cursor position
		System.out.print("\u001b[u"); // restore cursor position
	}

	public static void deleteThisLine(Cursor c){
		Cursor clone = c.clone();
		clone.goToX(1);
		System.out.print(LINE_DELETER);
		c.sync(); // go back
	}

}

class InputHandler{

	public final static  char up = (char)193;
	public final static  char down = (char)194;
	public final static  char right = (char)195;
	public final static  char left = (char)196;

	final Cursor cursor;
	public InputHandler(Cursor c){
		this.cursor = c;
	}

	public void handle(char input){
			switch (input) {
				case up: cursor.up(); break;
				case down: cursor.down(); break;
				case right: cursor.right(); break;
				case left: cursor.left(); break;
			}
	}
}

public class Vim{

	public final int height = 24 - 12; // TODO test mode
	public final int width = 80 - 40;

	public File ourFile;
	public final Cursor cursor;
	public final Screen screen;
	public final InputHandler handler;

	public EditorMode mode;
	public Vim(String ourFile){
		Logger.log("starting vim app");
		this.ourFile = (ourFile==null) ? null : new File(ourFile);

		TUtil.makeTerminalHandy();

		greetUser();


		screen = new Screen(width,height,'~');
		cursor = new Cursor(width,height);
		handler = new InputHandler(cursor);



	//TUtil.clearConsuleC(cursor);
	TUtil.clearConsule();


		screen.clearAndPrintAll(cursor);

		mode = EditorMode.COMMAND;
	}

	public Vim(String[] args){
		this( ETCUtil.getFileNameFromArgs(args) );
	}

	private void greetUser(){
		//	TUtil.clearConsule(cursor);
		TUtil.print("initializing vim!", Color.YELLOW);
		TUtil.print("input file is : " + ourFile, Color.BLUE);
		TUtil.print("the app is starting ", Color.GREEN);
		ETCUtil.delay(1);
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

	/*

		command mode :
			:wq
			:w
			:q
			:q!
			h j k l

		editor mode :
		esacpe + [A  [B [C [D
		up,down, right, left


	*/



	/*
	public void test(){
		try{

		}catch( Exception e){
			TUtil.clearConsule();
			e.printStackTrace();
			System.exit(0);
		}
	}
*/

	public void run(){
		String command = "";

		do {

			char input = TUtil.getChar();
			Logger.log("input is : " + input);
			screen.printLine(cursor);

			handler.handle(input);

		} while ( ! appShouldExit(command) );

		cleanUpForExit();
	}

	private void cleanUpForExit(){
		TUtil.makeTerminalNormal();
		TUtil.clearConsule();
	}

	public static void main(String[] args){
		Vim app = new Vim(args);
		app.run();
	}
}














class Cursor{
	public final int width;
	public final int height;
	private int x;
	private int y;
	public Cursor(int width, int height){
		this.width = width;
		this.height = height;
		this.reset(); // set x, y
	}

	public Cursor clone(){
		Cursor clone =  new Cursor(width,height);
		clone.setCursor(this.x, this.y);
		return clone;
	}

	private void setCursor(int x,int y){
		this.x = x;
		this.y = y;
		System.out.print("\u001b[" + y + ";" + x + "H");
	}

	public void goToLine(int line){
		setCursor(this.x, line);
	}

	public void goToX(int newX){
		setCursor(newX, this.y);
	}

	public int getX(){
		return this.x;
	}

	public int getLine(){
		return this.y;
	}


	public void sync(){
		setCursor(this.x, this.y);
	}

	public void reset(){
		setCursor(1, 1);
	}

	public void up(){
		y = (y>1)? y-1 : 1;
		sync();
	}
	public void down(){
		y = (y<height)? y+1 : height;
		sync();
	}
	public void left(){
		x = (x>1)? x-1 : 1;
		sync();
	}
	public void right(){
		x = (x<width)? x+1 : width;
		sync();
	}

}

class Screen{
	Character[][] innerArr;
	public final int height;
	public final int width;
	public Screen(int width,int height,char c){
		this.width = width;
		this.height = height;
		innerArr = new Character[this.height+1][this.width+1];
		fillScreen(c);
		//fillWithNumbers(); // for testing
	}
	public Screen(int height,int width){
		this(height,width,' ');
	}

	public void fillWithNumbers(){
		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				Integer t = i;
				innerArr[i][j] = t.toString().charAt(0);
			}
		}
	} // end func

	public void fillScreen(char c){
		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				innerArr[i][j] = c;
			}
		}
	} // end func

	public void clearAndPrintAll(Cursor c){
		//TUtil.clearConsule(c);
		c.reset();
		for (int i=1; i<=height; i++ ) {
				printLine(c);
				c.down();
		}
		c.reset();
	}

	public Character[] getLine(int n){
		return innerArr[n];
	}

	public void printLine(Cursor c){ // sensetive code
		Cursor clone = c.clone();
		clone.goToX(1);
		TUtil.deleteThisLine(clone);
		for(int j=1; j<=width; j++){
			Character toPrint = innerArr[c.getLine()][j];
			if(toPrint == null || toPrint == '\n') break;
			System.out.print(toPrint);
		}
		c.sync(); // go back
	}

}

class Logger{
	private static String[] command = new String[3];
	private static final String LOG_FILE = "./log1.txt";
	static{
		command[0] = "/bin/sh";
		command[1] = "-c";
	}

	public static void log(String toWrite){
		command[2] = "echo " + "\"" + toWrite + "\"" + " >> " + LOG_FILE;
		try{
			Runtime.getRuntime().exec(command)
			.waitFor();
		} catch( InterruptedException | IOException e ){
			TUtil.clearConsule();
			e.printStackTrace();
			TUtil.PError("can not write to log file!");
		}
	}

}

class ETCUtil{

	public static void delay(int second){
		try{
    		Thread.sleep(1000*second);
		}catch(InterruptedException ex){
				TUtil.PError("error : interrupt happened in delay!");
		}
	}

	public static int sumByte(byte[] bytes){
		return bytes[0] + bytes[1] + bytes[2];
	}

	public static char arrowKeyToChar(byte[] charCodes){
		if(charCodes.length == 3){
				int sum = sumByte(charCodes);
				switch(sum){
					case 183 : // up
						return (char)193; // gharar dad baraye bala

					case 184 : // down
						return (char)194; // gharar dad baraye payeen

					case 185 : // right
							return (char)195; // gharar dad baraye rast

					case 186 : // left
							return (char)196; // gharar dad baraye chap

					default:
						return ' ' ; // should not reach here
				}
		}
		else TUtil.PError("not 3 byte array in arrowKeyToChar");
		return ' '; // wont reach here
	}

	public static String getFileNameFromArgs(String[] args){

		ArgumentParser argParse = new ArgumentParser(args);
		if (!argParse.check()){ // bad input
			TUtil.PError("bad arguments\n" +
			 "usage : \'java Vim a.txt\' OR \'java Vim\'");
		}

		String fileName = argParse.getFileName(); // null if : usage : "vim"
		FileStatus status = FilesUtil.getFileState( argParse.getFileName());
		switch (status){
			case WRITABLE:
			case NOT_EXISTS:
			case NULL_YET:
				return fileName;

			case IS_DIR:
				TUtil.PError("error: target is directory: "+fileName);
				break;

			case NOT_OK:
				TUtil.PError("error: cant open input file: "+fileName);
				break;
		}
		throw new RuntimeException("should not reach here!, probalby bad FileStatus enum");

	}

}

class ArgumentParser{
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

	@Deprecated // untested
	public static String toAbsoloutePath(String fileName){
		if(fileName == null)
			return null;
		File f = new File(fileName);
		if(f.canRead() && f.isFile() && f.exists())
			return f.getAbsolutePath();
		return null;
	}

	public static Boolean canCreateFile(String fileName){
		if(fileName == null)
			return false;
		try {
			File f = new File(fileName);
			if(f.exists())
				return false;
			f.createNewFile();
			f.delete(); // check if we can create it or not
			return true;
		} catch(IOException e) {
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

enum Color{	RED, GREEN, BLUE, YELLOW, WHITE }
