package vi_limited;

class Node{
	public final int start;
	public final int length;
 	public final int type; // zero for orginal and one for added text

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

		Node[] ans = new Node[3];
		ans[0]= new Node(this.start,part1len, this.type);
		ans[1]= new Node(indexInBuffer,newTextLen,1); //added text
		ans[2]= new Node(splitPlace,part2len, this.type);

		return ans;
	}
}
