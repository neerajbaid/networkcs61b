/* HashTableChained.java */
package dict;

import list.DList;
import list.ListNode;

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
  protected DList[] buckets;
  protected int size;
  protected static final int COMP_A = 3;
  protected static final int COMP_B = 5;


  private static boolean isPrime(int n) {
    if (n % 2 == 0) {
      return false;
    }
    for (int divisor = 3; divisor < Math.sqrt(n); divisor += 2) {
      if (n % divisor == 0) {
        return false;
      }
    }
    return true;
  }
  private static int primeAfter(int n) {
    if (n%2 == 0) {
      n--;
    }
    while (!isPrime(n)) {
      n += 2;
    }
    return n;
  }


  /** 
   *  Construct a new empty hash table intended to hold roughly sizeEstimate
   *  entries.  (The precise number of buckets is up to you, but we recommend
   *  you use a prime number, and shoot for a load factor between 0.5 and 1.)
   **/

  public HashTableChained(int sizeEstimate) {
    // Your solution here.
    // netween sizeEstimate and 2 * sizeEstimate buckets
    sizeEstimate = Math.max(1, sizeEstimate);
    size = 0;
    int prime = primeAfter(sizeEstimate);
    buckets = new DList[prime];
    for(int i=0; i < prime; i++) {
      buckets[i] = new DList();
    }
  }

  /** 
   *  Construct a new empty hash table with a default size.  Say, a prime in
   *  the neighborhood of 100.
   **/

  public HashTableChained() {
    // Your solution here.
    size = 0;
    buckets = new DList[101];
    for(int i=0; i < 101; i++) {
      buckets[i] = new DList();
    }
  }

  /**
   *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
   *  to a value in the range 0...(size of hash table) - 1.
   *
   *  This function should have package protection (so we can test it), and
   *  should be used by insert, find, and remove.
   **/

  private static int mod(int a, int b) {
    a = a % b;
    if (a < 0) {
      a += b;
    }
    return a;
  }

  int compFunction(int code) {
    // Replace the following line with your solution.
    int n = buckets.length;
    int p = primeAfter(n * 2);
    return mod(mod(COMP_A * code + COMP_B, p), n);
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
    return size() == 0;
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
    Entry entry = new Entry();
    entry.key = key;
    entry.value = value;
    int n = compFunction(key.hashCode());
    DList list = buckets[n];
    list.insertBack(entry);
    size++;
    return entry;
  }

  private static ListNode findNode(DList list, Object key) {
    ListNode node = list.front();
    while(node.isValidNode()) {
      Entry entry = (Entry) node.item();
      if (key.equals(entry.key())) {
        return node;
      }
      node = node.next();
    }
    return null;
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
    // Replace the following line with your solution.
    int n = compFunction(key.hashCode());
    DList list = buckets[n];
    ListNode node = findNode(list, key);
    if (node != null) {
      return (Entry) node.item();
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
    int n = compFunction(key.hashCode());
    DList list = buckets[n];
    ListNode node = findNode(list, key);
    if (node != null) {
      Entry entry = (Entry) node.item();
      node.remove();
      size--;
      return entry;
    }
    return null;
  }

  /**
   *  Remove all entries from the dictionary.
   */
  public void makeEmpty() {
    // Your solution here.
    size = 0;
    for(int i=0; i < buckets.length; i++) {
      buckets[i] = new DList();
    }
  }



  // DEBUGGING CODE:

  public float loadFactor() {
    int n = 0;
    for(int i=0; i < buckets.length; i++) {
      DList bucket = buckets[i];
      if (bucket.length() > 0) {
        n += 1;
      }
    }
    return n / (float) buckets.length;
  }
  public int bucketLength() {
    return buckets.length;
  }
  public int collisions() {
    int n = 0;
    for(int i=0; i < buckets.length; i++) {
      DList bucket = buckets[i];
      if (bucket.length() > 1) {
        n += bucket.length() - 1;
      }
    }
    return n;
  }
  public void printBucketDistribution() {
    for(int i=0; i < buckets.length; i++) {
      DList bucket = buckets[i];
      String bar = "Bucket " + i + ": ";
      for (int j = 0; j < bucket.length(); j++) {
        bar += "X";
      }
      System.out.println(bar);
    }
  }

  public static void main(String[] args) {
    int[] testValues = {0,1,64, 1200};
    for (int num : testValues) {
      HashTableChained t;
      if (num == 0) {
        t = new HashTableChained();
      }
      else {
       t = new HashTableChained(num);
      }
      System.out.println(t.bucketLength());
      Entry a = t.insert("hi", 4);
      System.out.println(t.size()); // 1
      System.out.println(t.find("hi").value()); // 4
      t.insert("hi", "gah");
      System.out.println(t.size()); // 2
      System.out.println(t.find("hi").value()); // 4
      t.insert("blue", "sky");
      System.out.println(t.size()); // 3
      System.out.println(t.find("blue").value()); // sky
      a = t.remove("hi");
      System.out.println(t.size()); // 2
      System.out.println(a.value()); //4
      a = t.remove("hi");
      System.out.println(t.size()); // 1
      System.out.println(a.value()); //gah
      System.out.println(t.find("hi")); // null
      t.insert(2, "well");
      System.out.println(t.find(2).value()); // well
      t.makeEmpty();
      System.out.println(t.find(2)); // null
      System.out.println(t.remove(2)); // null
      System.out.println(t.size()); // 0
      System.out.println();
    }
  }
}
