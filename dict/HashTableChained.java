/* HashTableChained.java */

package dict;

import list.*;

/**
 *  HashTableChained implements a Dictionary as a hash table with chaining.
 *  All objects used as keys must have a valid hashCode() method, which is
 *  used to determine which bucket of the hash table an entry is stored in.
 *  Each object's hashCode() is presumed to return an int between
 *  Integer.MIN_VALUE and Integer.MAX_VALUE.  The HashTableChained class
 *  implements only the compression function, which maps the hash code to
 *  a bucket in the table's range.
 *
 *  DO NOT CHANGE ANY PROTOTYPES IN THIS FILE.
 **/

public class HashTableChained implements Dictionary {

  /**
   *  Place any data fields here.
   **/
   protected int size;
   protected SList[] table;
   protected static int a;
   protected static int b;
   protected static int n;
   protected static int p;

   public int num_collisions;

  /**
   *  Construct a new empty hash table intended to hold roughly sizeEstimate
   *  entries.  (The precise number of buckets is up to you, but we recommend
   *  you use a prime number, and shoot for a load factor between 0.5 and 1.)
   **/

  public HashTableChained(int sizeEstimate) {
    n = sizeEstimate;
    table = new SList[n];
    p = n*1000;
    while (!isPrime(p))
    {
      p++;
    }
    System.out.println(p);
    a = 97;
    b = 2047;
    num_collisions = 0;
  }

  /**
   *  Construct a new empty hash table with a default size.  Say, a prime in
   *  the neighborhood of 100.
   **/

  public HashTableChained() {
    this(100);
  }

  /**
   *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
   *  to a value in the range 0...(size of hash table) - 1.
   *
   *  This function should have package protection (so we can test it), and
   *  should be used by insert, find, and remove.
   **/

  int compFunction(int code) {
    int compressed = (a*code+b)%p;
    compressed = compressed % n;
    return compressed;
  }

  /**
   *  Returns the number of entries stored in the dictionary.  Entries with
   *  the same key (or even the same key and value) each still count as
   *  a separate entry.
   *  @return number of entries in the dictionary.
   **/

  public int size() {
    // Replace the following line with your solution.
    return size;
  }

  /**
   *  Tests if the dictionary is empty.
   *
   *  @return true if the dictionary has no entries; false otherwise.
   **/

  public boolean isEmpty() {
    // Replace the following line with your solution.
    return size == 0;
  }

  /**
   *  Create a new Entry object referencing the input key and associated value,
   *  and insert the entry into the dictionary.  Return a reference to the new
   *  entry.  Multiple entries with the same key (or even the same key and
   *  value) can coexist in the dictionary.
   *
   *  This method should run in O(1) time if the number of collisions is small.
   *
   *  @param key the key by which the entry can be retrieved.
   *  @param value an arbitrary object.
   *  @return an entry containing the key and value.
   **/

  public Entry insert(Object key, Object value) {
    // Replace the following line with your solution.
    int code = key.hashCode();
    int compressed = compFunction(code);
    System.out.println(key.toString() + " " + code + " " + compressed);
    if (table[compressed] != null)
      ((SList)table[compressed]).insertBack(value);
    else
    {
      SList list = new SList();
      list.insertBack(value);
      table[compressed] = list;
      num_collisions++;
    }
    size++;
    Entry entry = new Entry();
    entry.value = value;
    entry.key = key;
    // System.out.println(table[compressed].toString());
    return entry;
  }

  /**
   *  Search for an entry with the specified key.  If such an entry is found,
   *  return it; otherwise return null.  If several entries have the specified
   *  key, choose one arbitrarily and return it.
   *
   *  This method should run in O(1) time if the number of collisions is small.
   *
   *  @param key the search key.
   *  @return an entry containing the key and an associated value, or null if
   *          no entry contains the specified key.
   **/

  public Entry find(Object key) {
    int code = key.hashCode();
    int compressed = compFunction(code);
    SList list = (SList)table[compressed];
    Object value = null;
    if (list != null)
    {
      // System.out.println(list.toString());
      try {
        value = ((SListNode)list.front()).item();
      }
      catch (InvalidNodeException e) { return null; };
      Entry entry = new Entry();
      entry.key = key;
      entry.value = value;
      return entry;
    }
    return null;
  }

  /**
   *  Remove an entry with the specified key.  If such an entry is found,
   *  remove it from the table and return it; otherwise return null.
   *  If several entries have the specified key, choose one arbitrarily, then
   *  remove and return it.
   *
   *  This method should run in O(1) time if the number of collisions is small.
   *
   *  @param key the search key.
   *  @return an entry containing the key and an associated value, or null if
   *          no entry contains the specified key.
   */

  public Entry remove(Object key) {
    // Replace the following line with your solution.
    int code = key.hashCode();
    Integer compressed = Integer.valueOf(compFunction(code));
    SList list = (SList)table[compressed];
    if (list != null)
    {
      Object value = new Object();
      try {
        value = ((SListNode)list.front()).item();
      }
      catch (InvalidNodeException e) { return null; };
      try {
        list.front().remove();
      }
      catch (InvalidNodeException e) { return null; };
      table[compressed] = list;
      Entry entry = new Entry();
      entry.key = key;
      entry.value = value;
      size--;
      return entry;
    }
    return null;
  }

  /**
   *  Remove all entries from the dictionary.
   */
  public void makeEmpty() {
    size = 0;
    table = new SList[n];
    // Your solution here.
  }

  public boolean isPrime(int n) {
    if (n%2==0) return false;
    for(int i=3;i*i<=n;i+=2) {
        if(n%i==0)
            return false;
    }
    return true;
  }

}
