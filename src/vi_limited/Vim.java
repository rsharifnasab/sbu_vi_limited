package vi_limited;
import vi_limited.EditorMode;

import java.io.*;


/**
 TUtil as wr have in Terminal utilities
**/
class TUtil{

	/**
	 a String contains just escape
	**/
	public static final String ESCAPE = ""+(char) 27 ;

	/**
		ansi character for reset
		if we print this, all setting of colored text will be reverted
		we print this after printing colored text
	**/
	public static final String Reset = ESCAPE + "[0m";

	public static final String Red = ESCAPE + "[31m";
	public static final String Blue = ESCAPE +  "[94m";
	public static final String Green = ESCAPE +  "[32m";
	public static final String Yellow = ESCAPE +  "[93m";
	public static final String White = ESCAPE +  "[97m";

	public static final String CLEARER = "\033[H\033[2J";

	/**
		in case of pritning this, terminal will be cleaned!
	**/
	public static final String LINE_DELETER = "\u001b[K";

	/**
		print the toPrint string with the specified color
		we get color with the color enum
		and after printing, we reset terminal printing color
	**/
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

	/**
	 print an error in red and exit whole program
	**/
	public static void PError(String errorText){
		clearConsule();
		print(errorText, Color.RED);
		System.exit(0);
	}

	/**
		clear consule with prinitng the clean cunsole escape character
	**/
	public static void clearConsule() {
		System.out.print(CLEARER);
		System.out.flush();
	}

	/**
		clear cunsole with the help of cursor
		for not printing the clean screen character?
		why? becuase it this way we can overwrite prevoiusly writed datas
		not just hit enter after them
		how it is work?
		move cursor to the first
		delete current line of cursor
		go to next line ..
		after all, it set cursor to 1,1 position ( up and left )

	**/
	public static void clearConsuleC(Cursor c){
		Logger.log("clearing with cursor!");
		c.reset();
		for (int i =1; i <= c.height; i++ ) {
			TUtil.deleteThisLine(c);
			c.down();
		}
		c.reset();
	}

	/**
		we want to make terminal handy here
		what is that mean?
		set the buffer of terminal to one
		why? if uer enterred a key, we recieve it continuesly not after hittinh enter
	 	we can handle this in makefile but i wanted to do it here!
	 **/
	public static void makeTerminalHandy(){
		try{
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty -icanon min 1 </dev/tty"}).waitFor();
			TUtil.print("making terminal handy (no buffer)!" ,Color.YELLOW);
		}
		catch (Exception e){
			TUtil.PError("couldnt change terminal buffer!");
		}
	}

	/**
		we want to make terminal normal
		we should reset buffer
		read also : makeTerminalHandy
	 	we can handle this in makefile but i wanted to do it here!
	 **/
	public static void makeTerminalNormal(){
		try{
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty icanon </dev/tty"});
		}
		catch (Exception e){
			TUtil.PError("couldnt make terminal normal!");
		}
	}


	/**
		hardest method to write, probably easiest to findout
		it get a character from user like c's getch
		but is that easy? no! ofcourse not
		because java is such platform independent and we can not have low level access

		what we have here?
		we use System.in (input stream) and read byte by byte from it
		we can only read one chacter at a time with system.in.read()
		for handling problem of esc = 27 and arrow_up = 27 - 93 - 65
		we read a 3-byte array
		if the length of input is 1? it return 1 as bytesRead and return garbage value and data[1] and data[2]
		so if the bytesRead == 1 -> we get one character in data[0] such as esc or other chacters
		of the bytesRead == 3 -> we get arrow key with escape character as data[0] and other characters in data[1] and data[2]

		read also ETCUtil.arrowKeyToChar
	**/
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

	/**
		it print terminal size but does not work well
		it remained in code as deprecated , try to dont use it
		just see how it should work and read comments after each line
	**/
	@Deprecated
	public static void printTerminalSize(){
		System.out.print("\u001b[s");  // save cursor position
		System.out.print("\u001b[5000;5000H"); // move to col 5000 row 5000
		System.out.println("\u001b[6n");  // request cursor position
		System.out.print("\u001b[u"); // restore cursor position
	}

	/**
		a bit tricky code!
		it delete the current line that cursor exists
		first of all make a copy from cursor
		move it to first character of line
		and then delete the escape character for deleting line
		after all , it relocate cursor to first cursor with unchanged position
	**/
	public static void deleteThisLine(Cursor c){
		Cursor clone = c.clone();
		clone.goToX(1);
		System.out.print(LINE_DELETER);
		c.sync(); // go back
	}

}

/**
	out main class for vim editor
**/
public class Vim{

	/**
		the tricky up and down (gharardad kardim!)
		they are calculated like this:
		up = ord(esc) + ord ([) + ord(A) + 10
		up = ord(esc) + ord ([) + ord(B) + 10
		up = ord(esc) + ord ([) + ord(c) + 10
		up = ord(esc) + ord ([) + ord(AD) + 10

		check about ansi escape codes

		why +10? because set them out of 0-127 scope
		and also 193-196 are a bit meaningful
	**/
	public static final char up = (char)193;
	public static final char down = (char)194;
	public static final char right = (char)195;
	public static final char left = (char)196;

	/**
		height and width of whole editor
		they are fixed because it is not possible to find terminal size in java!
	**/

	public final int height = 24 - 4; // TODO test mode
	public final int width = 80 - 10;

	public File ourFile;
	public final Cursor cursor;
	public final Screen screen;

	/**
		TODO
	**/
	public EditorMode mode = EditorMode.COMMAND;

	/**
		the command that is entering and not fully enterred yet
		if user prees enter, we apply this command and reset this string

	**/
	public String command = "";

	/**
		a boolean to determine the app sohuld
		continue to run
		or it should exit

		exit() method will make this false;
	**/
	private Boolean running = true;

	/**
		first cunstructor of vim class
		change with caution
		it make new instance of cursor and screen class
		checkout the commands order, they are not random

		we do following things :
			set our file (if any)
			make terminal handy with decresing buffer to one in order to use terminal as editor!
			greet uesr ( totally optional )
			clean cunsole and print all of screen (that should be initailized with ' ')
	**/
	public Vim(String ourFile){
		Logger.log("starting vim app");
		this.ourFile = (ourFile==null) ? null : new File(ourFile);

		TUtil.makeTerminalHandy();

		greetUser();


		screen = new Screen(width,height,'~');
		cursor = new Cursor(width,height);

		//TUtil.clearConsuleC(cursor);
		//TUtil.clearConsule();

		screen.clearAndPrintAll(cursor);
	}

	/**
		second cunstrctor of vim class
		it give args as paramter to parse it later and extract filename
	**/
	public Vim(String[] args){
		this( ETCUtil.getFileNameFromArgs(args) );
	}

	/**
		this method do nothing except greeting user and
		waste his/her time a bit

		it also wants to show beautiness of colors!
	**/
	private void greetUser(){
		//	TUtil.clearConsule(cursor);
		TUtil.print("initializing vim!", Color.YELLOW);
		TUtil.print("input file is : " + ourFile, Color.BLUE);
		TUtil.print("the app is starting ", Color.GREEN);
		ETCUtil.delay(2);
	}

	/**
		set the command to ""
		because we aplied it earlier and we should look for new command,
		do you agree?
	**/
	private void resetCommand(){
		command = "";
	}

	/**
		its obvious!
		apply the command
		and reset it

	**/
	private void applyAndResetCommand(){
		apply();
		resetCommand();
	}

	/**
		apply the commnad in vim class
		for now it just support wq and q!
	**/
	private void apply(){
		Logger.log("command is : " + command);
		switch(command){

			case ":wq":
			case ":x":
				//save();
				exit();
				break;

			case ":q!":
				exit();
				break;

		}
	}

	/**
		handle the movement of cursor
		with the given input character
		note that up and down and right and left are not really up,down ,.. ensi chars
		because ansi use 3 char to represent arow keys
		so we used : sum of ord of 3 characters + 10
		why? check out 193 - 196 on ascii extended table
	**/
	public void handleMove(char input){

		switch (input) {
			case up:
				cursor.up();
				break;
			case down:
				cursor.down();
				break;
			case right:
			 	cursor.right();
				break;
			case left:
			 	cursor.left();
				break;
		}
	}

	/**
	append last character to input and check it we should run it or not
	**/
	private void handleCommand(char inputC){
		int input = (int) inputC;
		if( input > 127  || input == 27) // arrow keys or esc
			return;
		if(input == 10) // enter
			applyAndResetCommand();
		else
			command += inputC;
	}

	/**
	 main function and main loop
	**/
	public void run(){


		do {
			char input = TUtil.getChar();
			screen.printLine(cursor);

			handleMove(input);
			handleCommand(input);


		} while ( running );


	}

	/**
	 cleanup and get ready to exit after a loop
	**/
	public void exit(){
		cleanUpForExit();
		running = false;
	}

	/**
	do some stuff that is neccessay before quiting
	**/
	private void cleanUpForExit(){
		TUtil.makeTerminalNormal();
		TUtil.clearConsule();
	}

	/**
		our lovely main mathod!
		it just create new instance of vim and run it
	**/
	public static void main(String[] args){
		Vim app = new Vim(args);
		app.run();
	}
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






/**
	 a class for handling cursor position and movement
	 it has some tricky things
	 like clone
	 and set curser and sync

	 use goToX and gotoline with caution

**/
class Cursor{

	public final int width;
	public final int height;

	private int x;
	private int y;

	/**
		cunstructor of cursor
		it set width and hegith
		and also reset cursor
		for syncing the real place of cursor with the x ,y
		becasue at first it is not guarantee that cursor is in 1,1 place
	**/
	public Cursor(int width, int height){
		this.width = width;
		this.height = height;
		this.reset(); // set x, y
	}

	/**
	make a copy from out cursor with exactly same properties
	it use to temporialy change cursor
	then we can sync the main cursor to go back ro first place
	we SHOUD NOT HAVE more than one cursor for long time
	**/
	public Cursor clone(){
		Cursor clone =  new Cursor(width,height);
		clone.setCursor(this.x, this.y);
		return clone;
	}

	/**
		really move cursor to a position with printing to stdout
		and save its position to x, y
	**/
	private void setCursor(int x,int y){
		this.x = x;
		this.y = y;
		System.out.print("\u001b[" + y + ";" + x + "H");
	}

	/**
		set y!
	**/
	public void goToLine(int line){
		setCursor(this.x, line);
	}

	/**
		set x
	**/
	public void goToX(int newX){
		setCursor(newX, this.y);
	}

	/**
		get x
	**/
	public int getX(){
		return this.x;
	}

	/**
		get y
	**/
	public int getLine(){
		return this.y;
	}

	/**
		sync the real position of cursor with the x , y
		it is suitable for syncing back physical cursor after setting x,y

	**/
	public void sync(){
		setCursor(this.x, this.y);
	}

	/**
		move back cursor to up and left
	**/
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


/**
	the screen class to handle
	thing are currently on terminal

**/
class Screen{

	/**
		note that inner array is one row and one culomn bigger
		than it should be
		for comfortable syncing indexes with cursor position
		we do not use 0 row and 0 column at all
		they are always null
	**/
	Character[][] innerArr;

	public final int height;
	public final int width;

	/**
		cunstructor with the given char to fill array
	**/
	public Screen(int width,int height,char c){
		this.width = width;
		this.height = height;
		innerArr = new Character[this.height+1][this.width+1];
		fillScreen(c);
		//fillWithNumbers(); // for testing
	}

	/**
		cunstructor without character to fill
	**/
	public Screen(int height,int width){
		this(height,width,' ');
	}


	/**
		fill the inner array with some numbers
		from 2 to 9 for debugging
	**/
	public void fillWithNumbers(){
		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				Integer t = i;
				innerArr[i][j] = t.toString().charAt(0);
			}
		}
	}

	/**
		fill (and initialize)
		the inner array of scrren with the given character
	**/
	public void fillScreen(char c){
		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				innerArr[i][j] = c;
			}
		}
	}

	/**
		clear screen and print all of screen
		it will be used first time
	**/
	public void clearAndPrintAll(Cursor c){
		//TUtil.clearConsuleC(c);
		c.reset();
		for (int i=1; i<=height; i++ ) {
			printLine(c);
			c.down();
		}
		c.reset();
	}

	/**
		get one line of screen
	**/
	public Character[] getLine(int n){
		return innerArr[n];
	}

	/**
		get a cursor and print that line of screen array
		to the line that cursor exists
	**/
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

/**
	some utilities that dont have a suitable place
**/
class ETCUtil{

	/**
		delay program in seconds
	**/
	public static void delay(int second){
		try{
			Thread.sleep(1000*second);
		} catch(InterruptedException ex){
			TUtil.PError("error : interrupt happened in delay!");
		}
	}

	/**
		calculate sum of 3 byte in an array
	**/
	public static int sumByte(byte[] bytes){
		return bytes[0] + bytes[1] + bytes[2];
	}

	/**
		some tricky code to convert array of ascii codes
		of arrow keys (3 characters) to just one
		193 : up
		194 : down
		195 : right
		196 : left
	**/
	public static char arrowKeyToChar(byte[] charCodes){
		if(charCodes.length == 3){
			int sum = sumByte(charCodes);
			return (char) (sum + 10); // gharar dad baraye arrowkey ha
		}
		else
			TUtil.PError("not 3 byte array in arrowKeyToChar");
		return ' '; // never reach here
	}

	/**
		get arguamets of main method and extract filename
		it use ArgumentParser and FilesUtil
	**/
	public static String getFileNameFromArgs(String[] args){

		ArgumentParser argParse = new ArgumentParser(args);
		if (!argParse.check()){ // bad input
			TUtil.PError("bad arguments\n" +
			"usage : \'java Vim a.txt\' OR \'java Vim\' OR \'java Vim vim a.txt\' ");
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

	/**
		get argument and check if its valid or not
	**/
	public Boolean check(){
		if (args == null)
			return false;
		if(args.length ==  0)
			return true;
		if(args.length == 1 || args.length == 2)
			return true;
		return false;
	}

	/**
		actually return the filename
		and null if lenght of argument is 0
	**/
	public String getFileName(){
		if( args.length == 1)
			return args[0];
		if( args.length == 2)
			return args[1];
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

/**
	some utilities for working with files
**/
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

	/**
		check if the filename could be created
		and its not exists or not
	**/
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

	/**
		get states of a file
		in an enum
		more info in FileStatus
	**/
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

	/**
		check if a file is writeble (and we can save it )
		or no
	**/
	public static Boolean isWritableFile(String fileName){
		return getFileState(fileName) == FileStatus.WRITABLE;
	}

}

enum Color{	RED, GREEN, BLUE, YELLOW, WHITE }
