package vi_limited;

/**
out main class for vim editor
**/
public class Vim{

	/**
	the tricky up and down (gharardad kardim!)
	they are calculated like this:
	up = ord(esc) + ord ([) + ord(A) + 10
	down = ord(esc) + ord ([) + ord(B) + 10
	right = ord(esc) + ord ([) + ord(c) + 10
	left = ord(esc) + ord ([) + ord(AD) + 10

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

	public final int height = 24;// - 4;
	public final int width = 80;// - 10;

	public java.io.File ourFile;
	public final Cursor cursor;
	public final Screen screen;

	/**
	this will determine current editor mode
	command mode for command
	insert mode for edit
	and etc..
	**/
	public EditorMode mode;

	/**
	the command that is entering and not fully enterred yet
	if user prees enter, we apply this command and reset this string

	**/
	public String command;

	/**
	a boolean to determine the app sohuld
	continue to run
	or it should exit

	exit() method will make this false;
	**/
	private Boolean running = true;


	String tempText = "";
	boolean moved = false;

	PieceTable context;

	PTIter iter;


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
		this.ourFile = (ourFile==null) ? null : new java.io.File(ourFile);

		TUtil.makeTerminalHandy();

		greetUser();


		context = new PieceTable(ourFile);
		iter = new PTIter(context);
		screen = new Screen(width,height,context);
		cursor = new Cursor(width,height);

		//TUtil.clearConsuleC(cursor);
		//TUtil.clearConsule();

		TUtil.clearAndPrintScreen(screen,cursor);

		goToOneKeyCommandMode();
	}

	/**
	second cunstrctor of vim class
	it give args as paramter to parse it later and extract filename
	**/
	public Vim(String[] args){
		this( ETCUtil.getFileNameFromArgs(args) );
	}


	//////////////////////////////////////////////////

	private void goToStatisticsMode(){
		Logger.log("enterring statistics mode");
		resetCommand();

		mode = EditorMode.STATISTICS;

		screen.updateScreenContent(context.getStatistics());
		TUtil.clearAndPrintScreen(screen,cursor);

		//TUtil.PError("khat e akhar : " + ETCUtil.characterArrToString(screen.getLine(20) ) );
	}

	private void vimGoToEndOfFile(){
		//TODO
		TUtil.PError("not implemented yet");
	}

	private void vimGoToFirstOfFile(){ // maybe TODO
		iter.reset();
		cursor.reset();
		screen.goToFirstOfFile();
		screen.updateScreenContent();
		//screen.clearAndPrintAll();
	}

	public void save(){
		Logger.log("saved");
		FilesUtil.writeToFile(context.getAllText(),ourFile);
		Logger.log("--------\nsaved : \n"+ (context.getAllText().substring(0,200)) + "\n-------\n" );
	}

	private void applyLongCommand(){
		Logger.log("comamnd: " + command);
		switch(command){

			case "w":
				save();
				break;

			case "wq":
			case "x":
				save();
				exit();
				break;

			case "q":
			case "q!":
				exit();
				break;

			case "0":
				vimGoToFirstOfFile();
				break;

			case "$":
				vimGoToEndOfFile();
				break;
		}
	}


	private void vimUp(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.up();
		cursor.up();
		if(cursor.screenUpNeed()){
			screen.up();
			TUtil.clearAndPrintScreen(screen,cursor);

		}
	}

	private void vimDown(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.down();
		cursor.down();
		if(cursor.screenDownNeed()){
			Logger.log("down needed");
			screen.down();
			TUtil.clearAndPrintScreen(screen,cursor);
		}

	}

	private void vimLeft(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.left();
		cursor.left();
	}


	private void vimRight(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.right();
		cursor.right();
	}

	private void vimGotoLastOfLine(){
		//iter.gotoLastOfLine();
		cursor.gotoLastOfLine();
	}

	private void vimGotoFirstOfLine(){
		//iter.gotoFirstOfLine();
		cursor.gotoFirstOfLine();
	}




	private void handleOneCharCommand(char inputC){
		int input = (int) inputC;
		if( input > 127  || input == 27) // arrow keys or esc
			return;

		switch (inputC) {
			case 'i':
				goToInsertMode();
				break;
			case ':':
				goToOneLongCommandMode();
				break;
			case 'v':
				goToStatisticsMode();
				break;

			case 'h':
				vimLeft();
				break;
			case 'l':
				vimRight();
				break;

			case 'j':
				vimDown();
				break;
			case 'k':
				vimUp();
				break;


			case '0':
				vimGotoFirstOfLine();
				break;
			case '$':
				vimGotoLastOfLine();
				break;

		}

	}

	private void handleInsertMode(char inputC,PTIter iter){
		int input = (int) inputC;
		if( input > 127) // arrow keys
			return;
		if (input == 27) // esc presseed
			goToOneKeyCommandMode();
		tempText += inputC;
		//iter.right(); // TODO bia

		handleAddText(inputC,iter);
	}

	private void handleStatisticsMode(char inputC){
		int input = (int) inputC;
		if( input > 127) // arrow keys
			return;
		if (input == 27) // esc presseed
			goToOneKeyCommandMode();
	}

	private void handleLongCommand(char inputC){
		int input = (int) inputC;
		if( input > 127) // arrow keys or esc
			return;

		else if(input == 27) //escape
			goToOneKeyCommandMode();


		else if(input == 10) // enter
			applyAndResetLongCommand();
		else
			command += inputC;
	}



	private void addText(PTIter iter){
		context.add(tempText,iter);
		screen.updateScreenContent();
		tempText = "";
		Logger.log("context:"+context);
		Logger.log("- - - - - - ");
		Logger.log(context.getAllText());
		//TUtil.PError("context:"+context);

	}

	private void addCharToScreen(char inputC){
		Character[] line = screen.getLine(cursor.getLine());
		int x = cursor.getX();
		int len = line.length;
		for(int i = len-1; i > x; i--){
			line[i] = line[i-1];
		}
		line[x] = inputC;
	}

	private void handleAddText(char inputC,PTIter iter){
		if(tempText.length() == 0) return;
		addCharToScreen(inputC);
		if(inputC == '\n' || inputC == ' ' || moved)
			addText(iter);
	}

	private void hideCursorMess(char inputC){
		TUtil.printLine(screen,cursor); // hamoun khat ro dobare chap kon
		if(
			cursor.getX() >  cursor.width-4
	 	 ){
			Cursor clone = cursor.clone();
			clone.cloneDown();
			TUtil.printLine(screen,clone);
			cursor.sync();
		}

	}

	/**
	main function and main loop
	**/
	public void run(){
		while(running){
			PTIter iter = new PTIter(context);
			//iter.goToLine(3);
			char input = TUtil.getChar();
			hideCursorMess(input);

			moved = handleCursorMove(input);

			switch(mode){

				case ONE_KEY_COMMAND:
					handleOneCharCommand(input);
					break;

				case LONG_COMMAND:
					handleLongCommand(input);
					break;

				case INSERT:
					handleInsertMode(input,iter);
					break;

				case STATISTICS:
					handleStatisticsMode(input);

			} // end switch


		} // end while

	} // end function run





	/**
	this method do nothing except greeting user and
	waste his/her time a bit

	it also wants to show beautiness of colors!
	**/
	private void greetUser(){
		TUtil.print("initializing vim!", Color.YELLOW);
		TUtil.print("input file is : " + ourFile, Color.BLUE);
		TUtil.print("the app is starting ", Color.GREEN);
		ETCUtil.delay(1);
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
	and go to one key mode!
	**/
	private void applyAndResetLongCommand(){
		applyLongCommand();
		resetCommand();
		goToOneKeyCommandMode();
	}




	/**
	handle the movement of cursor
	with the given input character
	note that up and down and right and left are not really up,down ,.. ensi chars
	because ansi use 3 char to represent arow keys
	so we used : sum of ord of 3 characters + 10
	why? check out 193 - 196 on ascii extended table
	return true if we move it, false toherwise
	**/
	public boolean handleCursorMove(char input){
		switch (input) {
			case up:
				vimUp();
				return true;
			case down:
				vimDown();
				return true;
			case right:
				vimRight();
				return true;
			case left:
				vimLeft();
				return true;
		}
		return false;
	}


	private void cleanStatistics(){
		screen.updateScreenContent();
		TUtil.clearAndPrintScreen(screen,cursor);
	}

	private void goToInsertMode(){
		if(mode == EditorMode.STATISTICS)
			cleanStatistics();
		Logger.log("enterring insert mode");
		resetCommand();
		mode = EditorMode.INSERT;

	}

	private void goToOneKeyCommandMode(){
		if(mode == EditorMode.STATISTICS)
			cleanStatistics();
		Logger.log("enterring one key mode");
		resetCommand();
		mode = EditorMode.ONE_KEY_COMMAND;
	}

	private void goToOneLongCommandMode(){
		if(mode == EditorMode.STATISTICS)
			cleanStatistics();
		Logger.log("enterring long command mode");
		resetCommand();
		mode = EditorMode.LONG_COMMAND;
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
public void test(){
try{

}catch( Exception e){
TUtil.clearConsule();
e.printStackTrace();
System.exit(0);
}
}
*/
