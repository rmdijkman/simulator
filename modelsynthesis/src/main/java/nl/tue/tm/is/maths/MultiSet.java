package nl.tue.tm.is.maths;

import java.util.Collection;
import java.util.Vector;

@SuppressWarnings("serial")
public class MultiSet<T> extends Vector<T>{
	public MultiSet(){
		super();
	}
	
	public MultiSet(Collection<? extends T> c){
		super(c);
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o){
		if (o instanceof MultiSet){
			MultiSet ms = (MultiSet) o; 
			return (ms.containsAll(this) && this.containsAll(ms));
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")	
	public boolean containsAll(Collection<?> c){
		if (c instanceof MultiSet){
			MultiSet ms = new MultiSet(c); 
			for (Object o: this){
				ms.remove(o);
				if (ms.isEmpty()){
					return true;
				}
			}
			return ms.isEmpty();
		}else{
			return false;
		}
	}
	
}
