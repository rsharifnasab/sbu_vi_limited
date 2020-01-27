package vi_limited;

/**
	the screen class to handle
	thing are currently on terminal

**/
public class Screen{

	/**
		note that inner array is one row and one culomn bigger
		than it should be
		for comfortable syncing indexes with cursor position
		we do not use 0 row and 0 column at all
		they are always null
	**/
	Character[][] innerArr;


	PieceTable context;
	Cursor cursor;

	public final int height;
	public final int width;

	private int posInFile=1;

	private PTIter iter;

	/**
		cunstructor with the given char to fill array
	**/
	public Screen(int width,int height,PieceTable context,Cursor c){
		this.width = width;
		this.height = height;
		this.context = context;
		this.cursor = c;

		this.iter = new PTIter(context);
		innerArr = new Character[this.height+1][this.width+1];
		updateScreenContent();
		//fillWithNumbers(); // for testing
	}

	public int getPos(){
		return posInFile;
	}

	public void goToFirstOfFile(){
		posInFile = 1;
		iter.goToLine(posInFile);
		updateScreenContent();
	}

	public void up(){
		posInFile = (posInFile>1)? posInFile-1 : 1;
		iter.goToLine(posInFile);
		updateScreenContent();
		//clearAndPrintAll();
	}

	public void down(){

		//posInFile = (posInFile>1)? posInFile-1 : 1;
		posInFile++; // maybe TODO
		iter.goToLine(posInFile);
		updateScreenContent();
		//clearAndPrintAll();
	}




	public void updateScreenContent(){ // very coslt operation

		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				innerArr[i][j] = iter.next();
				if(innerArr[i][j]=='\n'){
					break;
				}

			} // end inner for
		} // end outer for
		clearAndPrintAll();
	} // end funtion



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
	public void clearAndPrintAll(){
		Cursor clone = cursor.clone();
		clone.reset();
		for (int i=1; i<=height; i++ ) {
			printLine(clone);
			clone.justDown();
		}
		cursor.sync();
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
