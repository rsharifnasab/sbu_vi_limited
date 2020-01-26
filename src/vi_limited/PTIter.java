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

	public boolean hasNext(){
		if(indexInNode<currentNode.length)
			return true;
		return context.nodes.noe() > (currentNodeIndex+1);
	}

	public char next(){
		if(! hasNext() ) return '~';

		if(indexInNode>=currentNode.length){
			goToNextNode();
		}
		return context.buffers[currentNode.type].get(indexInNode++);
	}

	private void goToNextNode(){
		currentNodeIndex++;
		currentNode = context.nodes.get(currentNodeIndex);
		indexInNode = 0;
	}

	public void goToLine(int line){
		for(int i =1; i < line; ){
			char t = this.next();
			if(t=='\n') i++;
		}
	}

}
