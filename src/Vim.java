import java.util.Scanner; // my only and lovely input getter
import java.io.*;

class ArgumentParser{
	private String[] args;
	public ArgumentParser(String[] args){
		this.args = args;
	}

	public Boolean check(){
		if (args == null)
			return false;
		if(args.length ==  0)
			return true;
		if(args.length > 1)
			return false;
		return true;
	}

	public String getFileName(){
		if( args.length == 1)
			return args[0];
		return null;
	}
}


enum FileStatus{
	WRITABLE, // the file is ok and ready to write
	NOT_OK, // can not open file for writing
	NOT_EXISTS, // the file doesnt exists
	IS_DIR, // requested file is directory
	NULL_YET // requested address is null
}

class FilesUtil{

	public static Boolean canCreateFile(String fileName){
		try {
			File f = new File(fileName);
			if(f.exists())
				return false;
			f.createNewFile();
			f.delete(); // check if we can create it or not
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static FileStatus getFileState(String fileName){
		if(fileName == null)
			return FileStatus.NULL_YET;

		File f = new File(fileName);

		if(f.isDirectory())
			return FileStatus.IS_DIR;
		if (f.exists() && f.canWrite())
			return FileStatus.WRITABLE;
		if(!f.exists() && canCreateFile(fileName))
			return FileStatus.NOT_EXISTS; // but it can be created
		return FileStatus.NOT_OK;
	}

	public static Boolean isWritableFile(String fileName){
		return getFileState(fileName) == FileStatus.WRITABLE;
	}

}

public class Vim{

	public static final String BAD_INPUT_HINT = "bad arguments\n" +
		"usage : \'java Vim a.txt\' OR \'java Vim\'";


	private static String getFileNameFromArgs(String[] args){

		ArgumentParser argParse = new ArgumentParser(args);
		if (!argParse.check()){ // bad input
			System.out.println(BAD_INPUT_HINT);
			System.out.flush();
			System.exit(1);
		}

		String fileName = argParse.getFileName(); // null if : usage : "vim"
		FileStatus status = FilesUtil.getFileState( argParse.getFileName());
		switch (status){
			case WRITABLE:
			case NOT_EXISTS:
			case NULL_YET:
				return fileName;

			case IS_DIR:
				System.out.println("error: target is directory: "+fileName );
				System.out.flush();
				System.exit(1);

			case NOT_OK:
				System.out.println("error: cant open input file: "+fileName );
				System.out.flush();
				System.exit(1);

			default:
				throw new RuntimeException("should not reach here!"); // should not reach here
		}
	}

	public static String ourFile;

	public static void main(String[] args){
		ourFile = getFileNameFromArgs(args);

		Scanner sc = new Scanner(System.in);

		System.out.println("input file is " + ourFile);

		sc.close();

	}
}
