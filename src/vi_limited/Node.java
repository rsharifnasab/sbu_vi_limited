package vi_limited;

class Node{
	public final int start;
	public final int length;
 	public final int type; // zero for orginal and one for added text

	public static final Node empty = new Node(0,0,0);


	@SuppressWarnings("unchecked")
	public boolean mosavi(Object o){
		if(o==null) return false;
		if(! (o instanceof Node )) return false;
		Node other = (Node) o;
		return other.start == this.start &&
			other.length == this.length &&
			 other.type == this.type;
	}

	public Node(int start,int length,int type){
		this.start = start;
		this.length = length;
		this.type = type;
	}


	public static Node getInitialNode(int length){
		return new Node(0,length,0); // contains all of original
	}

	@Override
	public String toString(){
			return "Node! {start : " + start + ", length : " + length + ", type : " + type + "}";
	}

	public static Node getAlaki(){
		return new Node(1,1,1);
	}

	public Node[] split(int splitPlace,int indexInBuffer, int newTextLen){
		int part1len =  splitPlace - this.start;
		int part2len = this.length - part1len;
		Logger.log("length of toSplit : " + this.length);

		Node[] ans = new Node[3];
		ans[0]= new Node(this.start,part1len, this.type);
		ans[1]= new Node(indexInBuffer,newTextLen,1); //added text
		ans[2]= new Node(splitPlace,part2len, this.type);

		return ans;
	}

	public static void deleteEmpty(List<Node> nodes){
		for(int i = nodes.noe()-1; i>0; i--){
			if( empty.mosavi(nodes.get(i)) ) nodes.delete(i);
		}
	}
}
