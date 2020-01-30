package vi_limited;

public class PTIter{
	PieceTable context;
	Node currentNode;
	int currentNodeIndex;
	int indexInNode;
	int currentLine;

	@Override
	public String toString(){
			return "node of iter: " + currentNode
				+ "\ncurrentNodeIndex: " + currentNodeIndex
				+ "\nindexInNode: " + indexInNode;
	}

	public PTIter(PieceTable context){
		this.context = context;
		this.reset();
	}

	public void add(String toAdd){
		context.add(toAdd,this);
		//TUtil.PError("add text not implemented yet");
	}

	public void reset(){
		currentNodeIndex = 0;
		indexInNode = 0;
		currentLine = 0;
		currentNode = context.nodes.get(currentNodeIndex);
	}

	public void gotoFirstOfLine(){
		up();
		right();
	}

	public void gotoLastOfLine(){
		down();
		left();
	}



	public void xLineUp(int x){
		x = x > currentLine ? currentLine : x;
		for(int i = 0;i<x; i++){
			up();
		}
	}

	public void xLineDown(int x){
		for(int i = 0;i<x; i++){
			down();
		}
	}

	public void up(){
		while( get() != '\n')
			left();
	}

	private char get(Node node,int index){
		return context.buffers[node.type].get(node.start+index);
	}

	public char get(){
		return get(currentNode,indexInNode);
	}

	public void down(){
		while( get() != '\n')
			right	();
	}

	public void left(){
		if(indexInNode == 0 && currentNodeIndex == 0) return; // avval e aval

		while(indexInNode<=0){
			goToPreNode();
		}
		indexInNode--;
		indexInNode = 0 > indexInNode ? 0 : indexInNode;
	}

	public void right(){
		while(
			indexInNode>=currentNode.length ){
			goToNextNode();
		}
		indexInNode++;
		indexInNode = currentNode.length > indexInNode ? indexInNode : currentNode.length-1;
	}



	private void goToNextNode(){
		currentNodeIndex++;
		currentNode = context.nodes.get(currentNodeIndex);
		indexInNode = 0;
	}

	private void goToPreNode(){
		currentNodeIndex--;
		currentNode = context.nodes.get(currentNodeIndex);
		indexInNode = currentNode.length-1;
	}
/*
	public boolean hasNext(){
		if(indexInNode+1<currentNode.length)
			return true;
		return context.nodes.noe() > (currentNodeIndex+2);
	}

	public char next(){
		if(! hasNext() ) return ' ';

		if(indexInNode>=currentNode.length){
			goToNextNode();
		}
		if(! hasNext() ) return ' ';

		return context.buffers[currentNode.type].get(indexInNode++);
	}



	public void goToLine(int line){
		for(int i =1; i < line && hasNext(); ){
			char t = this.next();
			if(t=='\n') i++;
		}
	}
      */

}
