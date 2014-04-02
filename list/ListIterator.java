package list;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
  *  Iterator for DList. Used for for-in loops.
  *  Return ListNode objects
  */

public class ListIterator implements Iterator<ListNode> {
  ListNode current;

/**
  *  Constructs iterator instance for list
  */
  public ListIterator(List list) {
    current = list.front();
  }
/**
  *  Returns whether there is another node ahead.
  */
  public boolean hasNext() {
    return current.isValidNode();
  }

/**
  *  Returns next ListNode
  */
  public ListNode next() {
    if (!current.isValidNode()) {
      return null;
    }
    ListNode temp = current;
    current = current.next();
    return temp;
  }

/**
  *  Remove ListNode (unused)
  */
  public void remove() {
    if (!current.isValidNode()) {
      return;
    }
    current.remove();
  }
}
