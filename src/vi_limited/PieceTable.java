package vi_limited;

public class PieceTable{
	private List<Character> [] buffers; // original , added
	private List<Node> nodes; // list e masalan

	//@SuppressWarnings()
	@SuppressWarnings({"unchecked","rawtypes"})
	public PieceTable(){
		buffers = (List<Character>[]) new List[2];
		buffers[0] = new List<Character>(); // original
		buffers[1] = new List<Character>(); // added

		nodes = new List<Node>();
	}

	public PieceTable(java.io.File file){
	 	this();
		if(file!=null){
			String initText = FilesUtil.getFileContext(file);
			buffers[0].addAll(initText.toCharArray()); // set original text TODO
		}
	}

	public char[][] getLines(int from, int lineCount){
		return null;
	}


}
