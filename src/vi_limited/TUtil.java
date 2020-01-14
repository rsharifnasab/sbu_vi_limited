package vi_limited;

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
		} catch(Exception e){
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
