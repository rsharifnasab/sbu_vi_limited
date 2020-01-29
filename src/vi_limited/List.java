package vi_limited;

/**
	implemented a list
	why? because we need it and we cant use the java's list
	its array list BTW
**/
public class List<T>{
	/**
		out private inner array to keep elemts
	**/
	private Object[] innerArray;

	/**
		number of leemnts current in inner array
	**/
	private int NOE;

	/**
		initial size of array,
		it ridiclous to have Arraywith size less than 10 no?
	**/
	public final int INITIAL_SIZE = 10;

	/**
		constructor of list!
	**/
	public List(){
		innerArray = new Object[INITIAL_SIZE];
		NOE = 0;
	}

	/**
		add add of elems in toAdd list
		to the main list
	**/
	@SuppressWarnings("unchecked")
	public void addAll(List<T> toAdd){
		for(int i =0; i < toAdd.NOE; i++){
			this.add( (T) toAdd.innerArray[i] );
		}
	}

	/**
		add all with input of T[]
	**/
	@SuppressWarnings("unchecked")
	public void addAll(T[] toAdd){
		for(Object  elem : toAdd ){
			this.add( (T) elem);
		}
	}

	/**
		addAll with input of char[]
		it only work with List<Character>
		otherwise? your own risk
	**/
	@SuppressWarnings("unchecked")
	public void addAll(char[] toAdd){
		for(char  c : toAdd ){
			Object elem =  (Character) (c);
			this.add( (T) elem);
		}
	}

	public void addAll(String s){
		addAll(s.toCharArray());
	}

	/**
		add elem to the list!
	**/
	public void add(T elem){
		if(elem == null)
			throw new NullPointerException("null elem cant be in list");
		ensureCapacity(NOE+1);
		innerArray[NOE++] = elem;
	}

	/**
		get ( retrieve ) with the given index
	**/
	@SuppressWarnings("unchecked")
	public T get(int index){
		if(index < 0 || index >= NOE)
			throw new RuntimeException("get index of list, out of range,\n index : " + index + " but noe : " + NOE);
		return (T) innerArray[index];
	}

	/**
		get whole list as the array of T[]
	**/
	@SuppressWarnings("unchecked")
	public T[] getAsArray(T alaki){ // sensetive code
		T[] clone =  (T[]) java.lang.reflect.Array.newInstance(alaki.getClass(), NOE);
		copy(innerArray,clone,NOE);
		return clone;
	}


	/**
		get whole list as the array of char
		it only work with List<character>
	**/
	@SuppressWarnings("unchecked")
	public char[] toCharArray(){ // sensetive code,
		char[] ans =  new char[NOE];
		for (int i =0;i<NOE;i++ ) {
			ans[i] = (Character) innerArray[i];
		}
		return ans;
	}


	/**
		get lest elem of list
	**/
	public T getLast(){
		return get(NOE-1);
	}

	/**
		return size of list
		number of elems currnetly in list
	**/
	public int noe(){
		return NOE;
	}

	/**
		allocate new array of given size
		and move old items and .. .
	**/
	private void resize(int newSize){
		if(newSize < NOE) return;
		Object[] newAllocated = new Object[newSize];
		copy(innerArray,newAllocated,NOE);
		innerArray = newAllocated; // let the GC to delte old arr
	}

	/**
		make sure list inner array have that capacity
		if not? resize
	**/
	public void ensureCapacity(int requestedSize){
		if (requestedSize < innerArray.length) return;
		else if(requestedSize < innerArray.length + 10) resize((innerArray.length+10)*3/2);
		else resize(requestedSize+1);
	}

	/**
		copy a part of source array to a part of des array
	**/
	private void copy(Object[] source,Object[] dist, int tedad){
		copy(source,0,dist,0,tedad);
	}

	/**
		more spscific copy!
	**/
	private void copy(Object[] source,int sfrom,Object[] dist, int dfrom, int tedad){
		for (int i =0; i<tedad ; i++) {
			dist[dfrom+i] = source[sfrom+i];
		}
	}

	/**
		delete elem at specified index
	**/
	public void delete(int index){
		for (int i=index;i+1<NOE ;i++ ) {
			innerArray[i] = innerArray[i+1];
		}
		innerArray[NOE-1] = null;
		NOE--;
	}




	private void shiftRight(int from, int tedad){ // 123 -> 12XX3
		this.ensureCapacity(NOE+tedad);
		for (int i = NOE-1;i >=from ;i-- ) {
			innerArray[i+tedad] = innerArray[i];
			innerArray[i] = null;
		}
		NOE+=tedad;
		Logger.log("after shift : " + this);
	}

	private void shiftTwoRight(int from){ // 123 -> 12XX3
		shiftRight(from,2);
	}

	private void set(int index,T elem){
		if(elem == null)
			throw new NullPointerException("null elem cant be in list");
		innerArray[index] = elem;
	}

	private void set3(int index,T[] elems){
		if(elems == null)
			throw new NullPointerException("null array of elem");
		set(index,elems[0]);
		set(index+1,elems[1]);
		set(index+2,elems[2]);
	}

	/**
		give an array of size three
		shift all elems after index (2)
		replce the index and next 2 with elems of array
		why? for split pieces in piece table
	**/
	public void replaceOneWithThree(int index, T[] elems){
		shiftTwoRight(index+1);
		set3(index,elems);

	}

	/**
		override of tostring
		it prints each elem of the list
		it counts on the tostring of elements
	**/
	@Override
	public String toString(){
		String ans = "---list :----\n";
		for(int i =0; i<NOE; i++){
			ans += innerArray[i] + "\n";
		}
		ans+="--------";
		return ans;
	}

	@SuppressWarnings({"unchecked"})
	public List<T> subList(int from, int to, T alaki){
		T[] clone =  (T[]) java.lang.reflect.Array.newInstance(alaki.getClass(), to-from);
		int le = 0;
		for (int i =from;i < to;i++ ) {
			clone[le++] = (T) innerArray[i];
		}
		List<T> ans = new List<T>();
		ans.addAll(clone);
		return ans;
	}

	public List<T> subList(int from, T alaki){
		return subList(from,NOE, alaki);
	}



}
