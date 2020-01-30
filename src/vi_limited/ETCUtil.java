package vi_limited;

/**
	some utilities that dont have a suitable place
**/
public class ETCUtil{

	/**
		delay program in seconds
	**/
	public static void delay(double second){
		try{
			int delayInMS = (int) (second * 1000);
			Thread.sleep( delayInMS );
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

	/**
		java dont have built in method to convert Character[] to String
		it only accept char[]
		so what should i do?
		i wrote this funtion to convert all Character s to chars
		and then i cretead new String with them
		in the parallel world, this method doesnt exist and java handles it, itslef
	**/
	public static String characterArrToString(Character[] arr){
		char[] ans = new char[arr.length];
		for(int i =0; i < arr.length; i++)
			ans[i] = arr[i]==null?' ':arr[i];
		return new String(ans);
	}

	/**
		very nive and good function!
		it get a sTring as first argument and few characters as second argument ( as varars)
		it count happenning of all characters in toFind array in the main String
		it is used to count number of words (by counting "\n" and ' ' s)
	**/
	public static int charCounter(String s,char...toFind){
		int count = 1;
		for(char c : s.toCharArray()){
			for(char toFindi : toFind)
			if(c==toFindi) count++;
		}
		return count;
	}

	/**
		this is private method so this javadoc isnt important right?
		it shift all elemts after ind of 3 arrays to the right
		what if they have more elemets? ignore them
	**/
	private static void shiftRight(int ind,String[] a,int[] b, int[] c){
		for (int i =9;i>i+1;i--){
			a[i] = a[i-1];
			b[i] = b[i-1];
			c[i] = c[i-1];
		}
		a[ind] = null;
		b[ind] = -1;
		c[ind] = 0;
	}

	/**
		check if the string s exists in the array or not
		it is used to unique the 10 short words and 10 long words
	**/
	public static boolean exists(String s,String[] strings){
		for(String toCheck : strings){
			if (s.equals(toCheck)) return true;
		}
		return false;
	}

	/**
		this method give an String[] as the text which is splited in white spaces
		it should find 10 shortest words (word is sth between whitespaces)
		it is sensetive code, take care of it
	**/
	public static String[] tenShortWords(String[] allText){
		String [] ans = new String[10];
		int[] indexes = new int[10];
		int[] toolHa = new int[10];

		for (int i =0;i<10;i++ ) { // initialize 3 arrays
			ans[i] = null;
			indexes[i] = -1;
			toolHa[i] = Integer.MAX_VALUE;
		}

		for (int i =0;i<allText.length;i++) {
			String s = allText[i];
			int len = s.length();
			if(len==0) continue;
			if(s.matches("\\s+")) continue;
			for (int j =0;j<10;j++ ) {
				if(len < toolHa[j]){
					if(exists(s,ans)) break;
					shiftRight(j,ans,indexes,toolHa);
					ans[j] = s;
					indexes[j] = i;
					toolHa[j] = len;
					break;
				}
			}
		}

		return ans;
	}

	/**
		this method give an String[] as the text which is splited in white spaces
		it should find 10 longest words (word is sth between whitespaces)
		it is sensetive code, take care of it
	**/
	public static String[] tenLongWords(String[] allText){
		String [] ans = new String[10];
		int[] indexes = new int[10];
		int[] toolHa = new int[10];

		for (int i =0;i<10;i++ ) { // initialize 3 arrays
			ans[i] = null;
			indexes[i] = -1;
			toolHa[i] = 0;
		}

		for (int i =0;i<allText.length;i++) {
			String s = allText[i];
			int len = s.length();
			if(len==0) continue;
			if(s.matches("\\s+")) continue;
			for (int j =0;j<10;j++ ) {
				if(len > toolHa[j]){
					if(exists(s,ans)) break;
					shiftRight(j,ans,indexes,toolHa);
					ans[j] = s;
					indexes[j] = i;
					toolHa[j] = len;
					break;
				}
			}
		}

		return ans;
	}

	/**
		calcualte words of input String
	**/
	public static int wordsCount(String allText){
		return ETCUtil.charCounter(allText,' ','\n');
	}



}
