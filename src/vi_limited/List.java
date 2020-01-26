package vi_limited;

public class List<T>{
	private Object[] innerArray;
	private int NOE;


	public List(){
		innerArray = new Object[0];
		NOE = 0;
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
	public T[] getAsArray(){
		T[] clone = (T[]) new Object[NOE];
		copy(innerArray,clone,NOE);
		return clone;
	}

	public void ensureCapacity(int n){
		if (n < innerArray.length) return;
		Object[] newAllocated = new Object[n];
		copy(innerArray,newAllocated,NOE);
		innerArray = newAllocated; // let the GC to delte old arr
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
