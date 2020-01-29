package vi_limited;

/**
	some utilities that dont have a suitable place
**/
public class ETCUtil{

	/**
		delay program in seconds
	**/
	public static void delay(int second){
		try{
			Thread.sleep(1000*second);
		} catch(InterruptedException ex){
			TUtil.PError("error : interrupt happened in delay!");
		}
	}

	/**
		calculate sum of 3 byte in an array
	**/
	public static int sumByte(byte[] bytes){
		return bytes[0] + bytes[1] + bytes[2];
	}

	/**
		some tricky code to convert array of ascii codes
		of arrow keys (3 characters) to just one
		193 : up
		194 : down
		195 : right
		196 : left
	**/
	public static char arrowKeyToChar(byte[] charCodes){
		if(charCodes.length == 3){
			int sum = sumByte(charCodes);
			return (char) (sum + 10); // gharar dad baraye arrowkey ha
		}
		else
			TUtil.PError("not 3 byte array in arrowKeyToChar");
		return ' '; // never reach here
	}

	/**
		get arguamets of main method and extract filename
		it use ArgumentParser and FilesUtil
	**/
	public static String getFileNameFromArgs(String[] args){

		ArgumentParser argParse = new ArgumentParser(args);
		if (!argParse.check()){ // bad input
			TUtil.PError("bad arguments\n" +
			"usage : \'java Vim a.txt\' OR \'java Vim\' OR \'java Vim vim a.txt\' ");
		}

		String fileName = argParse.getFileName(); // null if : usage : "vim"
		FileStatus status = FilesUtil.getFileState( argParse.getFileName());
		switch (status){
			case WRITABLE:
			case NOT_EXISTS:
			case NULL_YET:
				return fileName;

			case IS_DIR:
				TUtil.PError("error: target is directory: "+fileName);
				break;

			case NOT_OK:
				TUtil.PError("error: cant open input file: "+fileName);
				break;
		}
		throw new RuntimeException("should not reach here!, probalby bad FileStatus enum");

	}


	public static String characterArrToString(Character[] arr){
		char[] ans = new char[arr.length];
		for(int i =0; i < arr.length; i++)
			ans[i] = arr[i]==null?' ':arr[i];
		return new String(ans);
	}

}
