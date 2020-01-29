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

	public static int charCounter(String s,char...toFind){
		int count = 1;
		for(char c : s.toCharArray()){
			for(char toFindi : toFind)
			if(c==toFindi) count++;
		}
		return count;
	}

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

	public static String[] tenShortWords(String[] allText){
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
			if(s.matches("\\s*")) continue;
			for (int j =0;j<10;j++ ) {
				if(len > toolHa[j]){
					shiftRight(j,ans,indexes,toolHa);
					ans[j] = s;
					indexes[j] = i;
					toolHa[j] = len;
					continue;
				}
			}
		}

		return ans;
	}

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
			if(s.matches("\\s*")) continue;
			for (int j =0;j<10;j++ ) {
				if(len < toolHa[j]){
					shiftRight(j,ans,indexes,toolHa);
					ans[j] = s;
					indexes[j] = i;
					toolHa[j] = len;
				}
			}
		}

		return ans;
	}



}
