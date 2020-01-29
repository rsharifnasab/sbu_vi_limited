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
		nodes.add(Node.getInitialNode(0,""));
	}

	public PieceTable(String fileAdd){
	 	this();
		if(fileAdd==null) return;

		String initText = FilesUtil.getAddressContext(fileAdd);
		buffers[0].addAll(initText.toCharArray());

		nodes.delete(0);
		nodes.add(
			Node.getInitialNode(
				buffers[0].noe(),initText
			)
		);

	}

	@Override
	public String toString(){
		String ans = "nodes : \n";
		for(Object n : nodes.getAsArray(Node.getAlaki()) ){
			ans += n.toString() + "\n";
		}
		ans += "\nbuffer[0] :\n";
		for(Object n : buffers[0].getAsArray('a')){
			ans += n.toString();
		}
		ans += "\nbuffer[1] :\n";
		for(Object n : buffers[1].getAsArray('a')){
			ans += n.toString();
		}

		return ans;
	}

	public int getTextLen(){
		int len = 0;
		for (Node n : nodes.getAsArray(Node.getAlaki())) {
			len+= n.length;
		}
		return len;
	}

	public String getAllText(){
		PTIter iter = new PTIter(this);
		int len = getTextLen();
		char[] ansCHA = new char[len];
		int le = 0;
		for(int i =0;i < len; i++){
			ansCHA[le++] = iter.get();
			iter.right();
		}

		return new String(ansCHA);
	}

	public List<Character> getNodeText(Node node){
		List<Character> ans = new List<Character>();
		for (int i =node.start;i<node.start+node.length;i++ ) {
			ans.add(
				buffers[node.type].get(i)
			);
		}
		return ans;
	}

	public String getText(int fromLine,int height){
		int nodeInd = 0;
		while(nodes.get(nodeInd).lineCount < fromLine){
			fromLine -= nodes.get(nodeInd).lineCount;
			nodeInd++;
			if(nodeInd >= nodes.noe()) break;
		}

		List<Character> ans = new List<Character>();

		for(int i = nodeInd; i<nodes.noe(); i++){
			Node currNode = nodes.get(i);
			ans.addAll(
				getNodeText(currNode)
			);
			height -= currNode.lineCount;
			if(height <=0) break;
		}

		return new String(ans.toCharArray());

	}

	public void add(String toAdd,PTIter iter){

		int toAddLines = 0;
		for(char c : toAdd.toCharArray() ){
			if(c=='\n') toAddLines++;
		}

		int beginInBuffer = buffers[1].noe();
		buffers[1].addAll(toAdd.toCharArray());

		int newTextLen = toAdd.length();
		int splitIndex = iter.currentNode.start + iter.indexInNode;
		Logger.log("split index(should be 0) : " + splitIndex);

		Node toSplit = iter.currentNode;
		nodes.replaceOneWithThree(
			iter.currentNodeIndex,
			toSplit.split(splitIndex,beginInBuffer ,newTextLen,buffers, toAddLines)
		);

		Node.deleteEmpty(nodes);


	}


	public int wordsCount(){
		return 0; //TODO
	}

	public int linesCount(){
		int ans = 0;
		for(Node n : nodes.getAsArray(Node.getAlaki()))
			ans += n.lineCount;
		return ans;
	}

	public String[] tenWords(){
		String [] ans = new String[20];
			//TODO
		return ans;
	}

	public String getStatistics(){
		List<Character> ans = new List<Character>();
		ans.addAll(
			"number of words:" + wordsCount() + "\n"
		);

		ans.addAll(
			"number of lines:" + linesCount() + "\n"
		);

		//TODO : ten words

		return new String(ans.toCharArray());

	}


}
