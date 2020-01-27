package vi_limited;

/**
	a class to parse and extract info from args
	it also check is it parable or not
**/
public class ArgumentParser{
	private String[] args;

	public ArgumentParser(String[] args){
		this.args = args;
	}

	/**
		get argument and check if its valid or not
	**/
	public Boolean check(){
		if (args == null)
			return false;
		if(args.length ==  0)
			return true;
		if(args.length == 1 || args.length == 2)
			return true;
		return false;
	}

	/**
		actually return the filename
		and null if lenght of argument is 0
	**/
	public String getFileName(){
		if( args.length == 1)
			return args[0];
		if( args.length == 2)
			return args[1];
		return null;
	}
}
