package vi_limited;

/**
	 a class for handling cursor position and movement
	 it. has some tricky things
	 like clone
	 and set curser and sync

	 use goToX and gotoline with caution

**/
public class Cursor{

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


	public void gotoFirstOfLine(){
		goToX(1);
	}

	public void gotoLastOfLine(){
		goToX(width);
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
