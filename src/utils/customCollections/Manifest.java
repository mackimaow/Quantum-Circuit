package utils.customCollections;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

public class Manifest <T> implements Serializable {
	private static final long serialVersionUID = -5048176347320317395L;
	
	private final Hashtable<T, ManifestElementHandle> elements;
	
	public Manifest() {
		this.elements = new Hashtable<>();
	}
	
	private Manifest(Hashtable<T, ManifestElementHandle> elements) {
		this.elements = elements;
	}
	
	public ManifestElementHandle add(T element) {
		ManifestElementHandle mo = elements.get(element);
		if(mo == null) {
			mo = new ManifestElementHandle(element);
			elements.put(element, mo);
		} else {
			mo.ocurrances++;
		}
		return mo;
	}
	
	public void replace(T oldValue, T newValue) {
		ManifestElementHandle mo = elements.remove(oldValue);
		
		if(mo != null) {
			mo.element = newValue;
			elements.put(newValue, mo);
		}
	}
	
	public void remove(T element) {
		ManifestElementHandle mo = elements.get(element);
		
		if(mo != null) {
			if(mo.ocurrances == 0)
				elements.remove(element);
			else
				mo.ocurrances--;
		}
	}
	
	public int getOccurrences(T element) {
		ManifestElementHandle mo = elements.get(element);
		if(mo != null)
			return mo.ocurrances + 1;
		return 0;
	}
	
	public boolean contains (T element) {
		return elements.containsKey(element);
	}
	
	public Set<T> getElements () {
		return elements.keySet();
	}
	
	public Manifest<T> deepCopy() {
		Hashtable<T, ManifestElementHandle> temp = new Hashtable<>();
		
		for(T key : elements.keySet()) {
			ManifestElementHandle mo = elements.get(key);
			temp.put(key, mo.clone());
		}
		
		return new Manifest<T>(temp);
	}
	
	public class ManifestElementHandle implements Serializable {
		private static final long serialVersionUID = -4119245424558455962L;
		
		private T element;
		private int ocurrances;
		
		private ManifestElementHandle (T element) {
			this(element, 0);
		}
		
		private ManifestElementHandle(T element, int occurances) {
			this.element = element;
			this.ocurrances = occurances;
		}
		
		public T getElement () {
			return element;
		}
		
		public ManifestElementHandle clone() {
			return new ManifestElementHandle(element, ocurrances);
		}
	}
}
