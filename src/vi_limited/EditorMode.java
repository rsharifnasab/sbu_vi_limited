package vi_limited;

/**
	an enum to handle 4 mode of editor
	more detail in commnets
**/
public enum EditorMode{
	STATISTICS, // to show some statics!
	INSERT, // for editiing text
	ONE_KEY_COMMAND, // for handling one keycommand such as exit
	LONG_COMMAND // for handling long command such as :wq (start with : )
}
