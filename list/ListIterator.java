package list;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListIterator implements Iterator<ListNode> {
  ListNode current;

  public ListIterator(List list) {
    current = list.front();
  }

  public boolean hasNext() {
    return current.isValidNode();
  }

  public ListNode next() {
    if (!current.isValidNode()) {
      return null;
    }
    ListNode temp = current;
    current = current.next();
    return temp;
  }

  public void remove() {
    if (!current.isValidNode()) {
      return;
    }
    current.remove();
  }
}
