package akme.core.util;

/**
 * Hashtable indexed by primitive ints that maintains the sequence of events.
 *
 *  A map of objects whose mapping entries are sequenced based on the order in
 *  which they were added.  This data structure has fast <I>O(1)</I> search
 *  time, deletion time, and insertion time.
 *
 *  <P>Although this map is sequenced, it cannot implement {@link
 *  java.util.List} because of incompatible interface definitions.  The remove
 *  methods in List and Map have different return values (see: {@link
 *  java.util.List#remove(Object)} and {@link java.util.Map#remove(Object)}).
 *
 *  <P>This class is not thread safe.  When a thread safe implementation is
 *  required, use {@link Collections#synchronizedMap(Map)} as it is documented,
 *  or use explicit synchronization controls.
 *
 * TODO: write and execute tests
 *
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</A>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 *
 * @author Copyright(c) 2004 AKME Solutions
 * @author keith.mashinter
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/08 23:25:57 $
 * $NoKeywords: $
 */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;

public class IntSequencedHashMap implements Serializable, Cloneable {

	private static final long serialVersionUID =  192873674895873662L;

	/** The hash table data. */
	private Entry[] table;

	/** The total number of entries in the hash table. */
	private int count;

	/** Rehashes the table when count exceeds this threshold. */
	private int threshold;

	/** The load factor for the hashtable. */
	private float loadFactor;

	/** Sentinel used to hold the head and tail of the list of entries. */
	private Entry sentinel;

	/**
	 *  Holds the number of modifications that have occurred to the map,
	 *  excluding modifications made through a collection view's iterator
	 *  (e.g. entrySet().iterator().remove()).  This is used to create a
	 *  fail-fast behavior with the iterators.
	 **/
	private transient long modCount = 0;

	/**
	 * Construct a new, empty hashtable with the specified initial
	 * capacity and the specified load factor.
	 * @param initialCapacity the initial number of buckets
	 * @param loadFactor a number between 0.0 and 1.0, it defines
	 *		the threshold for rehashing the hashtable into
	 *		a bigger one.
	 * @exception IllegalArgumentException If the initial capacity
	 * is less than or equal to zero.
	 * @exception IllegalArgumentException If the load factor is
	 * less than or equal to zero.
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public IntSequencedHashMap( int initialCapacity, float loadFactor ) {
		if ( initialCapacity <= 0 || loadFactor <= 0.0 )
			throw new IllegalArgumentException();
		this.loadFactor = loadFactor;
		this.sentinel = createSentinel();
		table = new Entry[initialCapacity];
		threshold = (int) ( initialCapacity * loadFactor );
	}

	/// Constructs a new, empty hashtable with the specified initial
	// capacity.
	// @param initialCapacity the initial number of buckets
	public IntSequencedHashMap( int initialCapacity ) {
		this( initialCapacity, 0.75f );
	}

	/// Constructs a new, empty hashtable. A default capacity and load factor
	// is used. Note that the hashtable will automatically grow when it gets
	// full.
	public IntSequencedHashMap() {
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
		IntSequencedHashMap result = (IntSequencedHashMap) super.clone();
		result.loadFactor = loadFactor;
		result.threshold = threshold;
		result.sentinel = createSentinel();
		result.table = new Entry[table.length];
		result.putAll(this);
		return result;
	}

	/**
	 * Returns the number of elements contained in the hashtable.
	 */
	public int size() {
		return count;
	}

	/**
	 * Returns true if the hashtable contains no elements.
	 */
	public boolean isEmpty() {
		return sentinel.next == sentinel;
	}

	/**
	 * Returns true if the specified object is an element of the hashtable.
	 * This operation is more expensive than the containsKey() method.
	 * @param value the value that we are looking for
	 * @exception NullPointerException If the value being searched
	 * for is equal to null.
	 * @see IntHashtable#containsKey
	 */
	public boolean contains( int value ) {
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			if ( e.value == value )
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the collection contains an element for the key.
	 * @param key the key that we are looking for
	 * @see IntHashtable#contains
	 */
	public boolean containsKey( int key ) {
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index] ; e != null ; e = e.hashNext ) {
			if ( e.hash == hash && e.key == key )
				return true;
		}
		return false;
	}

	/** Gets the object associated with the specified key in the
	 * hashtable.
	 * @param key the specified key
	 * @returns the element for the key or null if the key
	 * 		is not defined in the hash table.
	 * @see IntHashtable#put
	 */
	public int get( int key ) {
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index] ; e != null ; e = e.hashNext ) {
			if ( e.hash == hash && e.key == key )
				return e.value;
		}
		return 0;
	}

	/**
	 *  Return the entry for the "oldest" mapping.  That is, return the Entry
	 *  for the key-value pair that was first put into the map when compared to
	 *  all the other pairings in the map.  This behavior is equivalent to using
	 *  <code>entrySet().iterator().next()</code>, but this method provides an
	 *  optimized implementation.
	 *
	 *  @return The first entry in the sequence, or <code>null</code> if the
	 *  map is empty.
	 **/
	public Entry getFirst() {
	  	return (isEmpty()) ? null : sentinel.next;
	}

	/**
	 *  Return the key for the "oldest" mapping.  That is, return the key for the
	 *  mapping that was first put into the map when compared to all the other
	 *  objects in the map.  This behavior is equivalent to using
	 *  <code>getFirst().getKey()</code>, but this method provides a slightly
	 *  optimized implementation.
	 *
	 *  @return The first key in the sequence.
	 *  @throws IllegalStateException if the map is empty.
	 */
	public int getFirstKey() {
	  	if (isEmpty()) throw new IllegalStateException("Map is empty.");
	  	return sentinel.next.key;
	}

	/**
	 *  Return the value for the "oldest" mapping.  That is, return the value for
	 *  the mapping that was first put into the map when compared to all the
	 *  other objects in the map.  This behavior is equivalent to using
	 *  <code>getFirst().value</code>, but this method provides a slightly
	 *  optimized implementation.
	 *
	 *  @return The first value in the sequence.
	 *  @throws IllegalStateException if the map is empty.
	 */
	public int getFirstValue() {
	 	if (isEmpty()) throw new IllegalStateException("Map is empty.");
	  	return sentinel.next.value;
	}

	/**
	 *  Return the entry for the "newest" mapping.  That is, return the Map.Entry
	 *  for the key-value pair that was first put into the map when compared to
	 *  all the other pairings in the map.  The behavior is equivalent to:
	 *
	 *  <pre>
	 *    Object obj = null;
	 *    Iterator iter = entrySet().iterator();
	 *    while(iter.hasNext()) {
	 *      obj = iter.next();
	 *    }
	 *    return (Map.Entry)obj;
	 *  </pre>
	 *
	 *  However, the implementation of this method ensures an O(1) lookup of the
	 *  last key rather than O(n).
	 *
	 *  @return The last entry in the sequence, or <code>null</code> if the map
	 *  is empty.
	 **/
	public Entry getLast() {
	  	return (isEmpty()) ? null : sentinel.prev;
	}

	/**
	 *  Return the key for the "newest" mapping.  That is, return the key for the
	 *  mapping that was last put into the map when compared to all the other
	 *  objects in the map.  This behavior is equivalent to using
	 *  <code>getLast().key</code>, but this method provides a slightly
	 *  optimized implementation.
	 *
	 *  @return The last key in the sequence.
	 *  @throws IllegalStatemException if the map is empty.
	 **/
	public int getLastKey() {
		if (isEmpty()) throw new IllegalStateException("Map is empty.");
	  	return sentinel.prev.key;
	}

	/**
	 *  Return the value for the "newest" mapping.  That is, return the value for
	 *  the mapping that was last put into the map when compared to all the other
	 *  objects in the map.  This behavior is equivalent to using
	 *  <code>getLast().getValue()</code>, but this method provides a slightly
	 *  optimized implementation.
	 *
	 *  @return The last value in the sequence.
	 *  @throws IllegalStatemException if the map is empty.
	 **/
	public int getLastValue() {
		if (isEmpty()) throw new IllegalStateException("Map is empty.");
		return sentinel.prev.value;
	}

	/**
	 * Rehashes the content of the table into a bigger table.
	 * This method is called automatically when the hashtable's
	 * size exceeds the threshold.
	 */
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
				old = old.hashNext;

				int index = ( e.hash & 0x7FFFFFFF ) % newCapacity;
				e.hashNext = newTable[index];
				newTable[index] = e;
			}
		}
	}

	/**
	 * Puts the specified element into the hashtable, using the specified
	 * key.  The element may be retrieved by doing a get() with the same key.
	 * The entry is inserted or updated to be returned as the getLast() entry.
     *
	 * @param key the specified key in the hashtable
	 * @param value the specified element
	 * @exception NullPointerException If the value of the element
	 * is equal to null.
	 * @see IntHashtable#get
	 * @return the old value of the key, or 0 if it did not have one.
	 */
	public int put( int key, int value ) {
		// Makes sure the key is not already in the hashtable.
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index] ; e != null ; e = e.hashNext ) {
			if ( e.hash == hash && e.key == key ) {
				int old = e.value;
				e.value = value;
				removeEntry(e);
				insertEntry(e);
				return old;
			}
		}

		modCount++;
		if ( count >= threshold ) {
			// Rehash the table if the threshold is exceeded.
			rehash();
			return put(key,value);
		}

		// Creates the new entry.
		Entry e = new Entry();
		e.hash = hash;
		e.key = key;
		e.value = value;
		e.hashNext = tab[index];
		tab[index] = e;
		removeEntry(e);
		insertEntry(e);
		++count;
		return 0;
	}

	/**
	 *  Adds all the mappings in the specified map to this map, replacing any
	 *  mappings that already exist (as per {@link Map#putAll(Map)}).  The order
	 *  in which the entries are added is determined by the iterator returned
	 *  from {@link Map#entrySet()} for the specified map.
	 *
	 *  @param t the mappings that should be added to this map.
	 *
	 *  @exception NullPointerException if <code>t</code> is <code>null</code>
	 */
	public void putAll(IntSequencedHashMap t) {
		Entry[] others = t.entries();
		for (int i=0; i<others.length; i++) {
			Entry other = others[i];
			put(other.key,other.value);
		}
	}

	/**
	 * Removes the element corresponding to the key. Does nothing if the
	 * key is not present.
	 * @param key the key that needs to be removed
	 * @return the value of key or 0 if not found
	 */
	public int remove( int key ) {
		Entry tab[] = table;
		int hash = key;
		int index = ( hash & 0x7FFFFFFF ) % tab.length;
		for ( Entry e = tab[index], prev = null ; e != null ; prev = e, e = e.hashNext ) {
			if ( e.hash == hash && e.key == key ) {
				if ( prev != null )
					prev.hashNext = e.hashNext;
				else
					tab[index] = e.hashNext;
				removeEntry(e);
				--count;
				++modCount;
				return e.value;
			}
		}
		return 0;
	}

	public int remove() {
		Entry e = sentinel.prev;
		return remove(e.key);
	}

	/// Clears the hash table so that it has no more elements in it.
	public void clear() {
		Entry tab[] = table;
		for ( int index = tab.length; --index >= 0; )
			tab[index] = null;
		this.sentinel.next = sentinel;
		this.sentinel.prev = sentinel;
		count = 0;
		modCount = 0;
	}

	/**
	 *  Returns the Map.Entry at the specified index
	 *
	 *  @exception ArrayIndexOutOfBoundsException if the specified index is
	 *  <code>&lt; 0</code> or <code>&gt;</code> the size of the map.
	 */
	public Entry getIndexedEntry(int idx) {
		Entry pos = sentinel;
		if(idx < 0) throw new ArrayIndexOutOfBoundsException(idx + " < 0");
		// loop to one before the position
		int i = -1;
		while(i < (idx-1) && pos.next != sentinel) {
		  i++;
		  pos = pos.next;
		}
		// pos.next is the requested position
		// if sentinel is next, past end of list
		if (pos.next == sentinel) throw new ArrayIndexOutOfBoundsException(idx + " >= " + (i + 1));
		return pos.next;
	}

	public int getIndexedKey(int idx) {
		return getIndexedEntry(idx).key;
	}

	public int getIndexedValue(int idx) {
		return getIndexedEntry(idx).value;
	}

	/**
	 * Find the index position of the first occurance of the given key.
	 * Since keys are unique this is the same as <code>indexOf(key)</code>.
	 *
	 * @param key Key whose index should be found.
	 * @return Index of the key or -1 if not found.
	 */
	public int indexOf(int key) {
		int idx = -1;
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			idx++;
			if (e.key == key) return idx;
		}
		return idx;
	}

	/**
	 * Find the index position of the last occurance of the given key.
	 * Since keys are unique this is the same as <code>indexOf(key)</code>.
	 *
	 * @param key Key whose index should be found.
	 * @return Index of the key or -1 if not found.
	 */
	public int lastIndexOf(int key) {
		return indexOf(key);
	}

	public Entry[] entries() {
		Entry[] res = new Entry[count];
		int i = 0;
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			res[i++] = e;
		}
		return res;
	}

	public int[] keys() {
		int res[] = new int[count];
		int i = 0;
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			res[i++] = e.key;
		}
		return res;
	}

	public int[] keysOrdered() {
		int res[] = keys();
		Arrays.sort(res);
		return res;
	}

	public int[] values() {
		int res[] = new int[count];
		int i = 0;
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			res[i++] = e.value;
		}
		return res;
	}

	/**
	 *  Implements {@link Map#equals(Object)}.
	 */
	public boolean equals(Object obj) {
	  	if(obj == null) return false;
	  	if(obj == this) return true;

	  	if(!(obj instanceof IntSequencedHashMap)) return false;

		// Use a more efficient implementation than getting entries().
	  	//return Arrays.equals(entries(),((IntSequencedHashMap)obj).entries());
	  	Entry osent = ((IntSequencedHashMap)obj).sentinel;
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			Entry oent = osent.next;
			if (oent == osent || !(e.equals(oent))) return false;
		}
	  	return true;
	}

	/**
	 *  Implements {@link Map#hashCode()}.
	 */
	public int hashCode() {
		int result = 0;
		for (Entry e = sentinel; e.next != sentinel; e = e.next) {
			result ^= e.key;
			result ^= e.value;
		}
		return result;
	}

	/**
	 *  Provides a string representation of the entries within the map.  The
	 *  format of the returned string may change with different releases, so this
	 *  method is suitable for debugging purposes only.  If a specific format is
	 *  required, use {@link #entrySet()}.{@link Set#iterator() iterator()} and
	 *  iterate over the entries in the map formatting them as appropriate.
	 **/
	public String toString() {
	  StringBuffer buf = new StringBuffer();
	  buf.append('[');
	  for (Entry pos = sentinel.next; pos != sentinel; pos = pos.next) {
		buf.append(pos.key);
		buf.append('=');
		buf.append(pos.value);
		if (pos.next != sentinel) {
		  buf.append(',');
		}
	  }
	  buf.append(']');

	  return buf.toString();
	}

	/**
	 *  Deserializes this map from the given stream.
	 *
	 *  @param in the stream to deserialize from
	 *  @throws IOException if the stream raises it
	 *  @throws ClassNotFoundException if the stream raises it
	 */
	public void readExternal( ObjectInput in )
	  throws IOException, ClassNotFoundException
	{
	  int size = in.readInt();
	  for(int i = 0; i < size; i++)  {
		int key = in.readInt();
		int value = in.readInt();
		put(key, value);
	  }
	}

	/**
	 *  Serializes this map to the given stream.
	 *
	 *  @param out  the stream to serialize to
	 *  @throws IOException  if the stream raises it
	 */
	public void writeExternal( ObjectOutput out ) throws IOException {
	  out.writeInt(size());
	  for(Entry pos = sentinel.next; pos != sentinel; pos = pos.next) {
		out.writeInt(pos.key);
		out.writeInt(pos.value);
	  }
	}

	/**
	 *  Construct an empty sentinel used to hold the head (sentinel.next) and the
	 *  tail (sentinel.prev) of the list.  The sentinal has a <code>null</code>
	 *  key and value.
	 **/
	private static final Entry createSentinel() {
	  Entry s = new Entry();
	  s.prev = s;
	  s.next = s;
	  return s;
	}

	/**
	 *  Removes an internal entry from the linked list.  This does not remove
	 *  it from the underlying map.
	 **/
	private void removeEntry(Entry entry) {
	  entry.next.prev = entry.prev;
	  entry.prev.next = entry.next;
	}

	/**
	 *  Inserts a new internal entry to the tail of the linked list.  This does
	 *  not add the entry to the underlying map.
	 **/
	private void insertEntry(Entry entry) {
	  entry.next = sentinel;
	  entry.prev = sentinel.prev;
	  sentinel.prev.next = entry;
	  sentinel.prev = entry;
	}

	public static class Entry implements Serializable {
		private static final long serialVersionUID = 2695247369261564169L;
		
		int hash;
		int key;
		int value;
		Entry hashNext;
		Entry next;
		Entry prev;

		public int hashCode() {
		  // implemented per api docs for Map.Entry.hashCode()
		  return (key) ^ (value);
		}

		public boolean equals(Object obj) {
		  if(obj == null) return false;
		  if(obj == this) return true;
		  if(!(obj instanceof Entry)) return false;

		  Entry other = (Entry)obj;

		  // implemented per api docs for Map.Entry.equals(Object)
		  return (key == other.key && value == other.value);
		}

		public String toString() {
		  return "{" + key + "=" + value + "}";
		}
	}
	
}
