package vi_limited;


/**
	some utilities for working with files
**/
public class FilesUtil{
	/**
		check if the filename could be created
		and its not exists or not
	**/
	public static Boolean canCreateFile(String fileName){
		if(fileName == null)
			return false;
		try {
			java.io.File f = new java.io.File(fileName);
			if(f.exists())
				return false;
			f.createNewFile();
			f.delete(); // check if we can create it or not
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	/**
		get states of a file
		in an enum
		more info in FileStatus
	**/
	public static FileStatus getFileState(String fileName){
		if(fileName == null)
			return FileStatus.NULL_YET;

		java.io.File f = new java.io.File(fileName);

		if(f.isDirectory())
			return FileStatus.IS_DIR;
		if (f.exists() && f.canWrite())
			return FileStatus.WRITABLE;
		if(!f.exists() && canCreateFile(fileName))
			return FileStatus.NOT_EXISTS; // but it can be created
		return FileStatus.NOT_OK;
	}

	/**
		check if a file is writeble (and we can save it )
		or no
	**/
	public static Boolean isWritableFile(String fileName){
		return getFileState(fileName) == FileStatus.WRITABLE;
	}

	/**
		get all context of file as string with given java.io.File
	**/
	public static String getFileContext(java.io.File file){
		if(file == null) return null;

		FileStatus state = getFileState( file.getAbsolutePath() );
		if (state == FileStatus.WRITABLE){
			try{
				java.util.Scanner sc = new java.util.Scanner(file);
	    			// we just need to use \\Z as delimiter
	   			sc.useDelimiter("\\Z");
				return sc.next(); // read whole file!
			}catch (Exception e) {
				e.printStackTrace();
				TUtil.PError("cannot read input file");
				return null;
			}
		}
		else return null;
	}

	/**
		read all data from the file with string address
	**/
	public static String getAddressContext(String address){
		if (address == null) return getFileContext(null);
		return getFileContext( new java.io.File(address) );
	}

	/**
		wirte specified String to the file with java.io.File input
	**/
	public static void writeToFile(String text,java.io.File address){
		try {
			java.io.PrintWriter writer = new java.io.PrintWriter(address, "UTF-8");
			writer.println(text);
			writer.close();
		} catch(Exception e) {
			TUtil.PError("cant save to file");
		}
	}

}
