package akme.core.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * An efficient Hashtable for primitive int keys and values.
 * This is based on the Acme IntHashtable ( see <a href="http://www.acme.com/">http://www.acme.com/</a> ).
 *
 * TODO: write and execute tests
 *
 * @see java.util.Hashtable
 *
 * @author Copyright(c) 2004 AKME Solutions open-sourced to Frameworks
 * @author keith.mashinter
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/08 23:25:57 $
 * $NoKeywords: $
 */
public class IntHashMap implements Cloneable, Serializable {

	private static final long serialVersionUID = 4475233906697722541L;

	/// The hash table data.
	private Entry[] table;

	/// The total number of entries in the hash table.
	private int count;

	/// Rehashes the table when count exceeds this threshold.
	private int threshold;

	/// The load factor for the hashtable.
	private float loadFactor;

	/// Constructs a new, empty hashtable with the specified initial
	// capacity and the specified load factor.
	// @param initialCapacity the initial number of buckets
	// @param loadFactor a number between 0.0 and 1.0, it defines
	//		the threshold for rehashing the hashtable into
	//		a bigger one.
	// @exception IllegalArgumentException If the initial capacity
	// is less than or equal to zero.
	// @exception IllegalArgumentException If the load factor is
	// less than or equal to zero.
	public IntHashMap( int initialCapacity, float loadFactor ) {
		if ( initialCapacity <= 0 || loadFactor <= 0.0 )
			throw new IllegalArgumentException();
		this.loadFactor = loadFactor;
		table = new Entry[initialCapacity];
		threshold = (int) ( initialCapacity * loadFactor );
	}

	/// Constructs a new, empty hashtable with the specified initial
	// capacity.
	// @param initialCapacity the initial number of buckets
	public IntHashMap( int initialCapacity ) {
		this( initialCapacity, 0.75f );
	}

	/// Constructs a new, empty hashtable. A default capacity and load factor
	// is used. Note that the hashtable will automatically grow when it gets
	// full.
	public IntHashMap() {
		this( 101, 0.75f );
	}

	/**
	 * Creates a shallow copy of this object, preserving the internal structure
	 * by copying only references.  The keys and values themselves are not
	 * <code>clone()</code>'d.  The cloned object maintains the same sequence.
	 *
	 * @return A clone of this instance.
	 *
	 * @exception CloneNotSupportedException if clone is not supported by a
	 * subclass.
	 */
	public Object clone() throws CloneNotSupportedException {
		// see http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html for super.clone();
		IntHashMap result = (IntHashMap) super.clone();
		result.table = new Entry[table.length];
		System.arraycopy(table, 0, result.table, 0, count);
		return result;
	}
	
	/// Returns the number of elements contained in the hashtable.
	public int size() {
		return count;
	}

	/// Returns true if the hashtable contains no elements.
	public boolean isEmpty() {
		return count == 0;
	}

	/// Returns true if the specified object is an element of the hashtable.
	// This operation is more expensive than the containsKey() method.
	// @param value the value that we are looking for
	// @exception NullPointerException If the value being searched
	// for is equal to null.
	// @see IntHashtable#containsKey
	public boolean contains( int value ) {
		Entry tab[] = table;
		for ( int i = tab.length ; i-- > 0 ; ) {
			for ( Entry e = tab[i] ; e != null ; e = e.next ) {
				if ( e.value == value )
					return true;
			}
		}
		return false;
	}

	/// Returns true if the collection contains an element for the key.
	// @param key the key that we are looking for
	// @see IntHashtable#contains
	public boolean containsKey( int key ) {
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index] ; e != null ; e = e.next ) {
			if ( e.hash == hash && e.key == key )
				return true;
		}
		return false;
	}

	/// Gets the object associated with the specified key in the
	// hashtable.
	// @param key the specified key
	// @returns the element for the key or null if the key
	// 		is not defined in the hash table.
	// @see IntHashtable#put
	public int get( int key ) {
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index] ; e != null ; e = e.next ) {
			if ( e.hash == hash && e.key == key )
				return e.value;
		}
		return 0;
	}

	/// Rehashes the content of the table into a bigger table.
	// This method is called automatically when the hashtable's
	// size exceeds the threshold.
	protected void rehash() {
		int oldCapacity = table.length;
		Entry oldTable[] = table;

		int newCapacity = oldCapacity * 2 + 1;
		Entry newTable[] = new Entry[newCapacity];

		threshold = (int) ( newCapacity * loadFactor );
		table = newTable;

		for ( int i = oldCapacity ; i-- > 0 ; ) {
			for ( Entry old = oldTable[i] ; old != null ; ) {
				Entry e = old;
				old = old.next;

				int index = ( e.hash & 0x7FFFFFFF ) % newCapacity;
				e.next = newTable[index];
				newTable[index] = e;
			}
		}
	}

	/**
	 * Puts the specified element into the hashtable, using the specified
	 * key.  The element may be retrieved by doing a get() with the same key.
	 * The key and the element cannot be null.
	 * @param key the specified key in the hashtable
	 * @param value the specified element
	 * @exception NullPointerException If the value of the element
	 * is equal to null.
	 * @see IntHashtable#get
	 * @return the old value of the key, or null if it did not have one.
	*/
	public int put( int key, int value ) {
		// Makes sure the key is not already in the hashtable.
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index] ; e != null ; e = e.next ) {
			if ( e.hash == hash && e.key == key ) {
				int old = e.value;
				e.value = value;
				return old;
			}
		}

		if ( count >= threshold ) {
			// Rehash the table if the threshold is exceeded.
			rehash();
			return put( key, value );
		}

		// Creates the new entry.
		Entry e = new Entry();
		e.hash = hash;
		e.key = key;
		e.value = value;
		e.next = tab[index];
		tab[index] = e;
		++count;
		return 0;
	}

	/// Removes the element corresponding to the key. Does nothing if the
	// key is not present.
	// @param key the key that needs to be removed
	// @return the value of key, or null if the key was not found.
	public int remove( int key ) {
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index], prev = null ; e != null ; prev = e, e = e.next ) {
			if ( e.hash == hash && e.key == key ) {
				if ( prev != null )
					prev.next = e.next;
				else
					tab[index] = e.next;
				--count;
				return e.value;
			}
		}
		return 0;
	}

	/// Clears the hash table so that it has no more elements in it.
	public void clear() {
		Entry tab[] = table;
		for ( int index = tab.length; --index >= 0; )
			tab[index] = null;
		count = 0;
	}

	public int[] keysOrdered() {
		int res[] = keys();
		Arrays.sort(res);
		return res;
	}

	public int[] keys() {
		int res[] = new int[count];
		int ptr = 0;
		int index = table.length;
		Entry entry = null;
		while (true) {
			if (entry == null)
				while ((index-- > 0) && ((entry = table[index]) == null));
			if (entry == null)
				break;
			Entry e = entry;
			entry = e.next;
			res[ptr++] = e.key;
		}
		return res;
	}

	public static class Entry implements Serializable {
		private static final long serialVersionUID = -1791555381072735664L;

		int hash;
		int key;
		int value;
		Entry next;
	}
}
