package vi_limited;

/**
	the class which handle keeping al text in memory
	main part of project!
**/
public class PieceTable{

	/**
		2 list for buffers
		one for original buffer
		and one for add buffers
	**/
	public List<Character> [] buffers; // original , added

	/**
		the nodes list
		it hand;e adding node and splitting and etc
	**/
	public List<Node> nodes; // list e masalan


	/**
		one instacne of TrieTree to do search stuff
		it is null, if text is modified and tree should me re created
	**/
	public TrieTree searchTree;

	/**
		the main constructor
		it make buffers and first noe!
	**/
	@SuppressWarnings({"unchecked","rawtypes"})
	public PieceTable(){
		buffers = (List<Character>[]) new List[2];
		buffers[0] = new List<Character>(); // original
		buffers[1] = new List<Character>(); // added

		nodes = new List<Node>();
		nodes.add(Node.getInitialNode(0,""));

		makeSearchTree();
	}

	/**
		the constructor with fileaddress
		it read all file content and add to original buffer
	**/
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

		updateSearchTree(initText, 0);
	}

	/**
		make a debug friendly string for debugging
		not for prpintg or saving
	**/
	@Override
	@SuppressWarnings("unchecked")
	public String toString(){
		List<Character> ans = new List<Character>();

		ans.addAll("\n---this is pieceTable toString-------------\nnodes : \n");

		for(Object n : nodes.getAsArray(Node.getAlaki()) ){
			ans.addAll( "\n" + n.toString() + "\n nodeText : \n" );
			ans.addAll( getNodeText( (Node) n) );
			ans.addAll( "\n\n" );
		}
		ans.addAll("\n----nodes complete------\n");
		ans.addAll("\nbuffer[0] :\n");
		for(Object n : buffers[0].getAsArray('a')){
			ans.addAll(n.toString() );
		}
		ans.addAll("\nbuffer[1] :\n");
		for(Object n : buffers[1].getAsArray('a')){
			ans.addAll( n.toString() );
		}
		ans.addAll ("\n-----------=======------------------\n");

		return ETCUtil.characterArrToString(ans.getAsArray(' '));
	}

	/**
		calacualte count of \n  of all text
		by adding linecount pof every node
	**/
	public int getTextLen(){
		int len = 0;
		for (Node n : nodes.getAsArray(Node.getAlaki())) {
			len+= n.length;
		}
		return len;
	}

	/**
		get all of text of piece table as an String
		is is costly
		it only called when saving ot searching
		or statistics
	**/
	public String getAllText(){
		int len = getTextLen();
		List<Character> ans = new List<Character>();
		ans.ensureCapacity(len);
		for(Node n : nodes.getAsArray(Node.getAlaki())){
			ans.addAll(
				getNodeText(n)
			);
		}
		return ETCUtil.characterArrToString(ans.getAsArray(' '));
	}

	/**
		convert a node to text which is pointing to
	**/
	public List<Character> getNodeText(Node node){
		List<Character> ans = new List<Character>();
		ans.ensureCapacity(node.length);
		for (int i =node.start;i<node.start+node.length;i++ ) {
			ans.add(
				buffers[node.type].get(i)
			);
		}
		return ans;
	}

	/**
		find location (index) of nth line
		in the node, to use in sublist
	**/
	private int nThLinePostion(Node node, int n){
		n--;
		if (n==0) return 0;
		for(int i=node.start;i<node.start+node.length; i++){
			if(n==0) return i;
			if (buffers[node.type].get(i) == '\n' ) n--;
		}
		return node.start+node.length; // not found
	}

	/**
		get a part of text
		from the line : fromLine
		to line : fromLine + height
		it is used for updating screen
		it is not explicit to be height line
		because screen class can ignore longer text
	**/
	public String getText(int fromLine,int height){ // TODO some bugs
		if(fromLine < 0) fromLine = 0;
		int nodeInd = 0;
		while(nodes.get(nodeInd).lineCount < fromLine && fromLine > 0){
			fromLine -= nodes.get(nodeInd).lineCount;
			nodeInd++;
			if(nodeInd >= nodes.noe()) break;
		}

		List<Character> ans = new List<Character>();

		for(int i = nodeInd; i<nodes.noe(); i++){
			Node currNode = nodes.get(i);
			ans.addAll(
				getNodeText(currNode).subList(
					nThLinePostion(currNode,fromLine)
				,' ')
			);
			height -= currNode.lineCount;
			if(height <=0) break;
		}

		return new String(ans.toCharArray());

	}

	/**
		addting a String tp the text in the location of iterator
	**/
	public void add(String toAdd,PTIter iter){
		updateSearchTree(
			toAdd,
			iter.indexInNode + iter.currentNode.start // starting index
		);

		int toAddLines = ETCUtil.charCounter(toAdd,'\n');

		int beginInBuffer = buffers[1].noe();
		buffers[1].addAll(toAdd.toCharArray());

		int newTextLen = toAdd.length();
		int splitIndex = iter.currentNode.start + iter.indexInNode;
		Logger.log("split index(should be 0) : " + splitIndex);

		Node toSplit = iter.currentNode;
		Node [] threeNewNodes = toSplit.split(splitIndex,beginInBuffer ,newTextLen,buffers, toAddLines);
		nodes.replaceOneWithThree(
			iter.currentNodeIndex,
			threeNewNodes
		);

		iter.currentNode = threeNewNodes[2]; /// bad az matn e insert shode
		iter.currentNodeIndex = iter.currentNodeIndex + 2; // 2
		iter.indexInNode = 0; // aval e oun node
		iter.currentLine = countOfLinesBeforeNode(iter.currentNodeIndex);

		//Logger.log("\niter is :\n"+iter);

	}

	private int countOfLinesBeforeNode(int index){
		int sum = 0;
		for(int i =0; i < index; i++){
			sum += nodes.get(i).lineCount;
		}
		return sum;
	}


	/**
		calcualte number of lines based on linecount in every node
	**/
	public int linesCount(){
		int ans = 1;
		for(Node n : nodes.getAsArray(Node.getAlaki()))
			ans += n.lineCount;
		return ans;
	}

	/**
		it returns a text in string
		less than 10 lines
		which contains statisticsabout text
		it is effiecient because it calcalte all test just one time
		it also split text just one time
		finding 10 longest and 10 shortest is O(n)
		it implemented in ETCUtils
	**/
	public String getStatistics(){
		String allText = getAllText();

		List<Character> ans = new List<Character>();
		ans.addAll(
			"number of words:" + ETCUtil.wordsCount(allText) + " and "
		);

		ans.addAll(
			"number of lines:" + linesCount() + "\n"
		);

		String[] allTextSplited = allText.split("\\s+");

		ans.addAll("ten shortest words: \n");
		String[] smallWords = ETCUtil.tenShortWords(allTextSplited);
		for(int i =0; i < 10; i++){
			String word = smallWords[i];
			if(word!=null) ans.addAll(word+"\n");
		}

		ans.addAll("ten longest words: \n");
		String[] longWords = ETCUtil.tenLongWords(allTextSplited);
		for(int i =0; i < 10; i++){
			String word = longWords[i];
			if(word!=null) ans.addAll(word+"\n");
		}

		return new String(ans.toCharArray());
	}

	private void updateSearchTree(String newText,int startIndex){
		if(searchTree == null) return;
		if(newText.length() > 1000){
			searchTree = null;
			return; // avoid stack over flow
		}

		for (int i = 0; i < newText.length(); i++){
			searchTree.insert(newText.substring(i), i+startIndex);
		}
	}

	private void makeSearchTree(){
		searchTree = new TrieTree();
	}

	public List<Integer> searchwithTrie(String toSearch){
		if(searchTree == null)
			return null; // long text, we cant search
	 	return searchTree.search(toSearch);
	}


/*
	private List<Integer> searchByRegex(String toSearch){
		final java.util.regex.Matcher subMatcher = java.util.regex.Pattern.compile(toSearch).matcher(getAllText());
		List<Integer> result = new List<Integer>();

		while( subMatcher.find() ){
			result.add(
				subMatcher.start()
			);
		}

		return result;
	}
*/
	public String search(String toSearch){

		List<Integer> result = searchwithTrie(toSearch);
		if(result == null) return "nothing found dont worry!\n";

		List<Character> ans = new List<Character>();
		ans.addAll("searching for " + toSearch + "\n");
		ans.addAll("number of finds: " + result.noe() + "\n");
		int len = result.noe();
		for( int i = 0; i < len; i++){
			ans.addAll(
				"found that on line: " +
				charIndToLine(result.get(i))
				+ "\n"
			);
		}

		return new String(ans.toCharArray());
	}

	private int charIndToLine(int index){
		int index_bpk = index;
		int line = 1;

		for(Node n : nodes.getAsArray(Node.getAlaki())){
			if(n.length <= index){
				index -= n.length;
				line += n.lineCount;
			}
			else{
				for(int i=0; i<= index_bpk; i++){
					if( buffers[n.type].get(i) == '\n' ) line++;
				}
				break;
			}

		}

		return line;
	}


}
