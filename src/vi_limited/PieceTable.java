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

	public PieceTable(String fileAdd){
	 	this();
		if(fileAdd!=null){
			String initText = FilesUtil.getAddressContext(fileAdd);
			buffers[0].addAll(initText.toCharArray()); // set original text TODO
		}
		nodes.delete(0);
		nodes.add(
			Node.getInitialNode(
				buffers[0].noe()
			)
		);

	}

	@Override
	public String toString(){
		String ans = "nodes : \n";
		for(Object n : nodes.getAsArray(Node.getAlaki()) ){
			ans += n.toString() + "\n";
		}
		ans += "\nbuffer[0] :";
		for(Object n : buffers[0].getAsArray('a')){
			ans += n.toString();
		}
		ans += "\nbuffer[1] :";
		for(Object n : buffers[1].getAsArray('a')){
			ans += n.toString();
		}

		return ans;
	}

	public void add(String toAdd,PTIter iter){
		buffers[1].addAll(toAdd.toCharArray());

		int newTextLen = toAdd.length();
		int splitIndex = iter.currentNode.start + iter.indexInNode;

		Node toSplit = iter.currentNode;
		nodes.replaceOneWithThree(
			iter.currentNodeIndex,
			toSplit.split(splitIndex,newTextLen)
		);


	}


}
