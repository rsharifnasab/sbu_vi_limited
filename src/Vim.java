import java.io.*;

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

	public static void saveToFile(String fileName){
		//TODO
	}

}

// TUtile as wr have in Terminal utilities
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
		print(errorText, Color.RED);
		System.exit(1);
	}

	public static void clearConsule() {
    	System.out.print(CLEARER);
    	System.out.flush();
	}

	public static void clearConsule(Cursor c){
		Cursor clone = c.clone();
		clone.reset();
		for (int i =1; i <= clone.height; i++ ) { // TODO
			TUtil.deleteThisLine(clone);
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

	public static int getChar(){
		try{
			return System.in.read();
		} catch(IOException e){
			TUtil.PError("error in getting char from user with system.in\nexiting");
		}
		return -1; // should not reach here
	}

	@Deprecated
	public static void printTerminalSize(){
		System.out.print("\u001b[s");  // save cursor position
		System.out.print("\u001b[5000;5000H"); // move to col 5000 row 5000
		System.out.println("\u001b[6n");  // request cursor position
		System.out.print("\u001b[u"); // restore cursor position
	}

	public static void deleteThisLine(Cursor c){
		Cursor clone =c.clone();
		clone.goToX(1);
		System.out.print(LINE_DELETER);
		c.sync(); // go back
	}

}

class ETCUtil{
	public static void delay(int second){
		try{
    		Thread.sleep(1000*second);
		}catch(InterruptedException ex){
    		Thread.currentThread().interrupt();
		}
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
			Runtime.getRuntime().exec(command).waitFor();
		} catch( InterruptedException | IOException e ){
			TUtil.PError("can not log!");
		}
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
		fillWithNumbers(); // TODO remove
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
			System.out.print(innerArr[c.getLine()][j]);
		}
		c.sync(); // go back
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

	public Cursor clone(){ // TODO : test
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

enum Color{	RED, GREEN, BLUE, YELLOW, WHITE }

public class Vim{

	private static String getFileNameFromArgs(String[] args){

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

	public final int height = 24;
	public final int width = 80;

	public File ourFile;
	public final Cursor cursor;
	public final Screen screen;
	public Vim(String ourFile){
		this.ourFile = (ourFile==null) ? null : new File(ourFile);

		TUtil.makeTerminalHandy();
		cursor = new Cursor(width,height);
		screen = new Screen(width,height,'~');

		greetUser();

		screen.clearAndPrintAll(cursor);

	}

	public Vim(String[] args){
		this( getFileNameFromArgs(args) );
	}

	private void greetUser(){
		TUtil.clearConsule();
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


	private void handleInput(int input){
		final int up = (int) 'w';
		final int down = (int) 's';
		final int right = (int) 'd';
		final int left = (int) 'a';
		final int deleteLine = (int) 'l';
		switch (input) {
			case up: cursor.up(); break;
			case down: cursor.down(); break;
			case right: cursor.right(); break;
			case left: cursor.left(); break;
			case deleteLine : TUtil.deleteThisLine(cursor);
		}
	}


	public void run(){

		int input;
		do {
			input = TUtil.getChar();

			screen.printLine(cursor);
			//cursor.sync();

			handleInput(input);

		} while (input != (int)'x' );

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
