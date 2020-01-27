package vi_limited;

public class List<T>{
	private Object[] innerArray;
	private int NOE;
	public final int INITIAL_SIZE = 10;

	public List(){
		innerArray = new Object[INITIAL_SIZE];
		NOE = 0;
	}

	public List(List<T> toAdd){
		this();
		addAll(toAdd);
	}

	@SuppressWarnings("unchecked")
	public void addAll(List<T> toAdd){
		for(Object  elem : toAdd.innerArray ){
			this.add( (T) elem);
		}
	}

	@SuppressWarnings("unchecked")
	public void addAll(T[] toAdd){
		for(Object  elem : toAdd ){
			this.add( (T) elem);
		}
	}

	@SuppressWarnings("unchecked")
	public void addAll(char[] toAdd){
		for(char  c : toAdd ){
			Object elem =  (Character) (c);
			this.add( (T) elem);
		}
	}







	public void add(T elem){
		if(elem == null)
			throw new NullPointerException("null elem cant be in list");
		ensureCapacity(NOE+1);
		innerArray[NOE++] = elem;
	}

	@SuppressWarnings("unchecked")
	public T get(int index){
		if(index < 0 || index >= NOE)
			throw new RuntimeException("get index of list, out of range,\n index : " + index + " but noe : " + NOE);
		return (T) innerArray[index];
	}

	@SuppressWarnings("unchecked")
	public T[] getAsArray(T alaki){ // sensetive code
		T[] clone =  (T[]) java.lang.reflect.Array.newInstance(alaki.getClass(), NOE);
		copy(innerArray,clone,NOE);
		return clone;
	}

	public T getLast(){
		return get(NOE-1);
	}

	public int noe(){
		return NOE;
	}

	private void resize(int newSize){
		if(newSize < NOE) return;
		Object[] newAllocated = new Object[newSize];
		copy(innerArray,newAllocated,NOE);
		innerArray = newAllocated; // let the GC to delte old arr
	}

	public void ensureCapacity(int requestedSize){
		if (requestedSize < innerArray.length) return;
		else if(requestedSize < innerArray.length + 10) resize((innerArray.length+10)*3/2);
		else resize(requestedSize+1);
	}

	private void copy(Object[] source,Object[] dist, int tedad){
		copy(source,0,dist,0,tedad);
	}

	private void copy(Object[] source,int sfrom,Object[] dist, int dfrom, int tedad){
		for (int i =0; i<tedad ; i++) {
			dist[dfrom+i] = source[sfrom+i];
		}
	}

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

	public void replaceOneWithThree(int index, T[] elems){
		shiftTwoRight(index+1);
		set3(index,elems);
	}




}
