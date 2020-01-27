package vi_limited;

public class PTIter{
	PieceTable context;
	Node currentNode;
	int currentNodeIndex = 0;
	int indexInNode = 0;

	public PTIter(PieceTable context){
		this.context = context;
		currentNode = context.nodes.get(currentNodeIndex);
	}


	public void up(){

	}

	public void down(){

	}
	public void left(){

	}
	public void right(){

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

	private void goToNextNode(){
		currentNodeIndex++;
		currentNode = context.nodes.get(currentNodeIndex);
		indexInNode = 0;
	}

	public void goToLine(int line){
		for(int i =1; i < line && hasNext(); ){
			char t = this.next();
			if(t=='\n') i++;
		}
	}
      */

}
