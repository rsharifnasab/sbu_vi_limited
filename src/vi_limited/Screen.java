package vi_limited;

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
