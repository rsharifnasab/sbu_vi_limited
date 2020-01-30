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
	IT IS ONLY FILLED In long command mode
	in other modes, itis supported to be emtpy
	**/
	public String command;

	/**
	a boolean to determine the app sohuld
	continue to run
	or it should exit

	exit() method will make this false;
	**/
	private Boolean running = true;

	/**
		out string to handle the typed text but not appedded to text yet
	**/
	String tempText = "";

	/**
		an String to hadnle clipboard
	**/
	String clipBoard = "";

	/**
		OUT PIECE TABLE THAT KEEP ALL TEXT
	**/
	PieceTable context;

	/**
		TODO
	**/
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
	clean cunsole and print all of screen (that should be initailized with the file or ' ')
	**/
	public Vim(String ourFile){
		Logger.log("starting vim app");
		TUtil.makeTerminalHandy();
		this.ourFile = (ourFile==null) ? null : new java.io.File(ourFile);


		greetUser();

		context = new PieceTable(ourFile);
		iter = new PTIter(context);
		screen = new Screen(width,height,context);
		cursor = new Cursor(width,height);

		//TUtil.clearConsuleC(cursor);
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

	/**
		copy current line of screen to the cliboard
	**/
	private void copyOneLine(){
		Character[] line = screen.getLine(cursor);
		clipBoard = ETCUtil.characterArrToString(line);
	}

	/**
		paste the copied line to the iterator position
		it set the tempText so first, check it temptext is ok to add or not
		it do not empty the clipboard, maybe we want to paste that text again
	**/
	private void pasteOneLine(){
		addText(); // add the remaining temp text to piece table
		tempText = clipBoard;
		addText(); // add the new clipboard text to the piece table
		tempText = "";
	}

	/**
		handle going to line x
		if x is bigger that total lines, the command is ignored
		it will bring the xth line to the top of screen and reset the cursor for eas eof implement
	**/
	private void goToLine(int x){
		if(x > context.linesCount() ) return;
		if(x<1) x = 1;
		//iter. TODO
		screen.goToLine(x);
		TUtil.clearAndPrintScreen(screen,cursor);
		cursor.reset();
	}


	private void forwardWord(int n){
		Logger.log("forward");
		//TODO
		TUtil.PError("forward not implemented");
	}

	private void backWord(int n){
		Logger.log("back word");
		//TODO
		TUtil.PError("backward not implemented");
	}


	/**
		we user want to go to end  of line, this method will be called
		it move cursor and iterator to end of the  current line
	**/
	private void vimGotoLastOfLine(){
		//iter.gotoLastOfLine();
		cursor.gotoLastOfLine();
		addText();
	}

	/**
		we user want to go to first of line, this method will be called
		it move cursor and iterator to beginning of the line
	**/
	private void vimGotoFirstOfLine(){
		//iter.gotoFirstOfLine();
		cursor.gotoFirstOfLine();
		addText();
	}




	/**
		handle ,oving cursor to one line up
	**/
	private void vimUp(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.up();
		cursor.up();
		addText();
		if(cursor.screenUpNeed()){
			screen.up();
			TUtil.clearAndPrintScreen(screen,cursor);

		}
	}

	/**
		handle moving cursor to one line down
	**/
	private void vimDown(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.down();
		cursor.down();
		addText();
		if(cursor.screenDownNeed()){
			screen.down();
			TUtil.clearAndPrintScreen(screen,cursor);
		}

	}

	/**
		handle movement of cursor the left
	**/
	private void vimLeft(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.left();
		cursor.left();
		addText();
	}

	/**
		handle moveing cursor to right
	**/
	private void vimRight(){
		if(mode == EditorMode.STATISTICS)
			return;
		//iter.right();
		cursor.right();
		addText();
	}


	/**
		handle adding text (in tempText string) to the piece table
		it will update screen and reprint it
		TODO
	**/
	private void addText(){
		if(tempText.length() == 0) return;
		iter.add(tempText);
		screen.updateScreenContent();
		TUtil.clearAndPrintScreen(screen,cursor);

		tempText = "";
		Logger.log("context:"+context);
		Logger.log("- - - - - - ");
		Logger.log(context.getAllText());

	}

	/**
		TODO
	**/
	private void addCharToScreen(char inputC){
		Character[] line = screen.getLine(cursor.getLine());
		int x = cursor.getX();
		int len = line.length;
		for(int i = len-1; i > x; i--){
			line[i] = line[i-1];
		}
		line[x] = inputC;
		TUtil.printLine(screen,cursor);

	}



	/**
		if user press enter after :command
		this method will be called
		we have a swtich case to fetermine commands
		if command is none of the cases, we think that it is gotoline x command
	**/
	private void applyLongCommand(){
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

			case "yy":
				copyOneLine();
				break;

			case "p":
				pasteOneLine();
				break;

			default:
				etcHandle();
		}
	}



	/**
		handle keys in one key mode
		here we can go to any other mode with diffrent keys
		also we can go up and down and .. with hjkl
		also copying and goi0ng to fist and end of line is handled here
		note that search will start from here (/)
	**/
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
			case '/':
				gotoSearchMode();
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

			case 'Y':
				copyOneLine();
				break;

			case 'w':
				forwardWord(1);
				break;

			case 'b':
				backWord(1);
				break;

		}

	}


	private void deleteOneChar(){
		TUtil.PError("backspace handle didnt implemented yet");
	}

	/**
		handle new character in insert mode
		it add it to tempText
		and handle esc key go back to one key mode
	**/
	private void handleInsertMode(char inputC){
		int input = (int) inputC;
		if( input > 127) // arrow keys
			return;
		if (input == 27){ // esc presseed
			goToOneKeyCommandMode();
			return;
		}
		if (input == 127){ // DEL presseed
			deleteOneChar();
			return;
		}

		tempText += inputC;
		addCharToScreen(inputC);
		cursor.right();
		//iter.right(); // TODO bia

		if(inputC == '\n' || inputC == ' ')
			addText();
	}

	/**
		handle input char in statistics mode
		in this mode we ignoe all cursor movement to prevent bugs
		if esc key pressed, we go back to one key mode
	**/
	private void handleStatisticsMode(char inputC){
		int input = (int) inputC;
		if( input > 127) // arrow keys
			return;
		if (input == 27) // esc presseed
			goToOneKeyCommandMode();
	}

	/**
		handle new input character in long commnad mod e
		it append it to the command string
		and handle going back to one key mode or when to appy
	**/
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

	private void handleSearchMode(char inputC){
		int input = (int) inputC;
		if( input > 127) // arrow keys or esc
			return;

		else if(input == 27) //escape
			goToOneKeyCommandMode();

		else if(input == 10) // enter
			search();
		else
			command += inputC;
	}


	/**
		hide mess of cursor at next line (if you move cursor and end of the line
		it re print current line and next line )
	**/
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
			iter.reset();
			char input = TUtil.getChar();
			hideCursorMess(input);

			handleCursorMove(input);

			switch(mode){

				case ONE_KEY_COMMAND:
					handleOneCharCommand(input);
					break;

				case LONG_COMMAND:
					handleLongCommand(input);
					break;

				case INSERT:
					handleInsertMode(input);
					break;

				case STATISTICS:
					handleStatisticsMode(input);
					break;

				case SEARCH:
					handleSearchMode(input);
					break;

				default:
					throw new RuntimeException("should not reach here, probably bad EditorMode enum");

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
		ETCUtil.delay(0.8);
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
	public void handleCursorMove(char input){
		switch (input) {
			case up:
				vimUp();
				break;
			case down:
				vimDown();
				break;
			case right:
				vimRight();
				break;
			case left:
				vimLeft();
				break;
		}
	}

	/**
		clean screen after exiting srtatistics mode
	**/
	private void cleanStatistics(){
		screen.updateScreenContent();
		TUtil.clearAndPrintScreen(screen,cursor);
	}

	/**
		foing to insert mode from one key mode ( i pressed)
		it reset commdn string
	**/
	private void goToInsertMode(){
		Logger.log("enterring insert mode");
		mode = EditorMode.INSERT;

	}

	/**
		foing to insert mode from one key mode ( i pressed)
		it reset commdn string
	**/
	private void gotoSearchMode(){
		Logger.log("enterring search mode");
		mode = EditorMode.SEARCH;
		resetCommand();
	}

	/**
		got to one key mode from any mode
		it clean screen when we came fro mstatistics mode
	**/
	private void goToOneKeyCommandMode(){
		if(mode == EditorMode.STATISTICS)
			cleanStatistics();
		if(mode == EditorMode.INSERT)
			addText();
		Logger.log("enterring one key mode");
		resetCommand();
		mode = EditorMode.ONE_KEY_COMMAND;
	}

	/**
		got to long command mode from any mode
	**/
	private void goToOneLongCommandMode(){
		Logger.log("enterring long command mode");
		resetCommand();
		mode = EditorMode.LONG_COMMAND;
	}


	/**
		go to statistics mode (because its much similiar to search mdoe)
		get searched text from piece table search method
		and show it in screen
	**/
	private void search(){
		Logger.log("searching");
		String toSearch = command;// hame be joz oun /
		mode = EditorMode.STATISTICS;

		screen.updateScreenContent(context.search(toSearch));
		TUtil.clearAndPrintScreen(screen,cursor);
	}



	/**
		if command is just numberical
		we go to that line of file
	**/
	private void etcHandle(){
		if(command.length() == 0) return; // empty command

		try{
			if (!command.matches("\\d+") ) return; // not numberical
			int x = Integer.parseInt(command);
			goToLine(x);

		} catch(Exception e){
			//ignore any bad command
		}
	}



	/**
		handle going to statistics mode
		it get static text from piece table
		and  fill screen with it
		and also reprint the screen with the new text
	**/
	private void goToStatisticsMode(){
		Logger.log("enterring statistics mode");
		resetCommand();

		mode = EditorMode.STATISTICS;

		screen.updateScreenContent(context.getStatistics());
		TUtil.clearAndPrintScreen(screen,cursor);
	}

	/**
		handle going to last line of file
		note that this will bring ;last line to the top of screen
		and whole screen will be empty , but no problem
	**/
	private void vimGoToEndOfFile(){
		goToLine( context.linesCount() );
	}

	/**
		handle going to fist line of file
	**/
	private void vimGoToFirstOfFile(){
		goToLine(1);
	}

	/**
		handle saving all file to the file
		if the file is null, program will exit with an error
		it get all of piece table text with getAllTExt method
	**/
	public void save(){
		Logger.log("saved");
		FilesUtil.writeToFile(context.getAllText(),ourFile);
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
