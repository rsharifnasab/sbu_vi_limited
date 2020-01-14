package vi_limited;

/**
	an enum to determine status of existance
	and writability of a file ( or even its not file)
**/
enum FileStatus{
	WRITABLE, // the file is ok and ready to write
	NOT_OK, // can not open file for writing
	NOT_EXISTS, // the file doesnt exists
	IS_DIR, // requested file is directory
	NULL_YET // requested address is null
}
