package vi_limited;

import java.io.File;

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
