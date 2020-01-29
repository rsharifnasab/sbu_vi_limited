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

	public final int height;
	public final int width;

	private int posInFile=1;


	/**
		cunstructor with the given char to fill array
	**/
	public Screen(int width,int height,PieceTable context){
		this.width = width;
		this.height = height;
		this.context = context;

		innerArr = new Character[this.height+1][this.width+1];
		updateScreenContent();
		//fillWithNumbers(); // for testing
	}


	public void goToLine(int x){
		int contextLines = context.linesCount();
		posInFile = x<=contextLines ? x : contextLines;
		updateScreenContent();
	}

	public int getPos(){
		return posInFile;
	}


	public void up(){
		posInFile = (posInFile>1)? posInFile-1 : 1;
		updateScreenContent();
	}

	public void down(){
		int lines = context.linesCount();
		posInFile = (posInFile>lines-1)? lines : posInFile+1;
		Logger.log("in scren down, poseinfile:" + posInFile);
		updateScreenContent();
	}

	public void updateScreenContent(){
		updateScreenContent(
			context.getText(posInFile,height)
		);
	}


	public void updateScreenContent(String stText){ // very coslt operation
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
