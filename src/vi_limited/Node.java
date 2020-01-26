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
}
