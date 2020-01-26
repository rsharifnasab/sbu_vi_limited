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



	public void add(T elem){
		ensureCapacity(NOE+1);
		innerArray[NOE++] = elem;
	}

	@SuppressWarnings("unchecked")
	public T get(int index){
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

}
