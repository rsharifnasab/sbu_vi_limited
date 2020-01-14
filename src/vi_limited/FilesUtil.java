package vi_limited;

import java.io.File;

/**
	some utilities for working with files
**/
public class FilesUtil{

	@Deprecated // untested
	public static String toAbsoloutePath(String fileName){
		if(fileName == null)
			return null;
		File f = new File(fileName);
		if(f.canRead() && f.isFile() && f.exists())
			return f.getAbsolutePath();
		return null;
	}

	/**
		check if the filename could be created
		and its not exists or not
	**/
	public static Boolean canCreateFile(String fileName){
		if(fileName == null)
			return false;
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

	/**
		get states of a file
		in an enum
		more info in FileStatus
	**/
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

	/**
		check if a file is writeble (and we can save it )
		or no
	**/
	public static Boolean isWritableFile(String fileName){
		return getFileState(fileName) == FileStatus.WRITABLE;
	}

}
