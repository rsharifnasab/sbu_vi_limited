package vi_limited;

class Node{
	public final int start;
	public final int length;
 	public final int type; // zero for orginal and one for added text
	public final int lineCount;

	public static final Node empty = new Node(0,0,0,0);


	@SuppressWarnings("unchecked")
	public boolean mosavi(Object o){
		if(o==null) return false;
		if(! (o instanceof Node )) return false;
		Node other = (Node) o;
		return other.start == this.start &&
			other.length == this.length &&
			 other.type == this.type;
	}

	public Node(int start,int length,int type,int lineCount){
		this.start = start;
		this.length = length;
		this.type = type;
		this.lineCount = lineCount;
	}


	public static Node getInitialNode(int length){
		return new Node(0,length,0,0); // contains all of original
	}

	@Override
	public String toString(){
			return "Node: {start : " + start + ", length : " + length + ", type : " + type + ", wc -l : " + lineCount + "}";
	}

	public static Node getAlaki(){
		return new Node(0,0,0,0);
	}

	public Node[] split(int splitPlace,int indexInBuffer, int newTextLen, List<Character> [] buffers, int toAddLines){

		 List<Character> chArr = buffers[this.type];

		int part1lines = 0;
		int part2lines = 0;

		for (int i = this.start; i < this.length+this.start ; i++ ) {
			Character c = chArr.get(i);
			if(c==null) continue;
			if (c.equals('\n')){
				if (i < splitPlace)
					part1lines++;
				else
					part2lines++;
			}
		}

		int part1len =  splitPlace - this.start;
		int part2len = this.length - part1len;
		Logger.log("length of toSplit : " + this.length);

		Node[] ans = new Node[3];
		ans[0]= new Node(this.start,part1len, this.type,part1lines);
		ans[1]= new Node(indexInBuffer,newTextLen,1,toAddLines); //added text
		ans[2]= new Node(splitPlace,part2len, this.type,part2lines);

		return ans;
	}

	public static void deleteEmpty(List<Node> nodes){
		for(int i = nodes.noe()-1; i>0; i--){
			if( empty.mosavi(nodes.get(i)) ) nodes.delete(i);
		}
	}
}
