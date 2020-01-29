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

	/**
		rerence of out piece table
		it is used to get new text and fill scren when its needed
	**/
	PieceTable context;

	public final int height;
	public final int width;

	/**
		position of first line of screen in file
		in scoll (and some toher command) we change this and re genrate screen contentents from piece table
	**/
	private int posInFile=1;


	/**
		cunstructor with the piece table to fill screen
	**/
	public Screen(int width,int height,PieceTable context){
		this.width = width;
		this.height = height;
		this.context = context;

		innerArr = new Character[this.height+1][this.width+1];
		updateScreenContent();
		//fillWithNumbers(); // for testing
	}


	/**
		same as set position
		it give a position (in line)
		and move first line of screen to that line
	**/
	public void goToLine(int x){
		int contextLines = context.linesCount();
		posInFile = x<=contextLines ? x : contextLines;
		updateScreenContent();
	}

	/**
		get current line of top of screen
	**/
	public int getPos(){
		return posInFile;
	}

	/**
		move screen one line up (if its available)
	**/
	public void up(){
		posInFile = (posInFile>1)? posInFile-1 : 1;
		updateScreenContent();
	}

	/**
	move screen one line down (if its available)
	it come down untill just first line of scrren is filled with text
	other lines are emtpy
	**/
	public void down(){
		int lines = context.linesCount();
		posInFile = (posInFile>lines-1)? lines : posInFile+1;
		Logger.log("in scren down, poseinfile:" + posInFile);
		updateScreenContent();
	}

	/**
		update context of scrren with the help of piece table
		ig get few line (as much as screen height)
		from "posInfile" to "posInFile+height"

		note that it is possible that getText return longer text, we ignore it!
	**/
	public void updateScreenContent(){
		updateScreenContent(
			context.getText(posInFile,height)
		);
	}


	/**
		get as String from input ans set it to the screen charArray character by character
		if input text is longer, we ignore it
		it it is shorter that size of screeen, we print ' ' instead of it
		the tamum boolean show that we reached end od the input text or not
		we do not add anything after '\n' characger
	**/
	public void updateScreenContent(String stText){
		char[] text = stText.toCharArray();
		int ind = 0;
		int len = text.length;
		boolean tamum = false;
		outer:
		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				if(ind == len)
					tamum = true;
				if(tamum){
					innerArr[i][j] = ' ';
					continue;
				}
				innerArr[i][j] = text[ind++];
				if(innerArr[i][j]=='\n'){
					break;
				}

			} // end inner for
		} // end outer for
	} // end funtion



	/**
		fill the inner array with some numbers
		from 2 to 9 for debugging
	**/
	@Deprecated
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
	@Deprecated
	public void fillScreen(char c){
		for (int i=1; i<=height; i++ ) {
			for(int j=1; j<=width; j++){
				innerArr[i][j] = c;
			}
		}
	}

	/**
		get one line of screen
	**/
	public Character[] getLine(int n){
		return innerArr[n];
	}

	/**
		get one line of screen by Cursor
	**/
	public Character[] getLine(Cursor c){
		return innerArr[c.getLine()];
	}


}
