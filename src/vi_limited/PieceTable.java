package vi_limited;


public class PieceTable{
	public List<Character> [] buffers; // original , added
	public List<Node> nodes; // list e masalan

	@SuppressWarnings({"unchecked","rawtypes"})
	public PieceTable(){
		buffers = (List<Character>[]) new List[2];
		buffers[0] = new List<Character>(); // original
		buffers[1] = new List<Character>(); // added
		nodes = new List<Node>();
		nodes.add(Node.getInitialNode(0));
	}

	public PieceTable(java.io.File file){
	 	this();
		if(file!=null){
			String initText = FilesUtil.getFileContext(file);
			buffers[0].addAll(initText.toCharArray()); // set original text TODO
		}
		nodes.delete(0);
		nodes.add(
			Node.getInitialNode(
				buffers[0].noe()
			)
		);


	}

/*
	public char[][] getLines(int from,int width, int lineCount){
		char[][] ans = new char[lineCount+1][];
		for (int i =1; i<=lineCount;i++ ) {
			ans[i] = new char[width+1];
			for (int j =1; j<=width;j++ ) {
				ans[i][j] = '-';
			}
		}
		return ans;
	}
*/

}
