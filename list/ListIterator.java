package list;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListIterator implements Iterator<ListNode> {
  ListNode current;

  public ListIterator(List list) {
    current = list.front();
  }

  public boolean hasNext() {
    return current.isValidNode() && current.next().isValidNode();
  }

  public ListNode next() {
    if (!hasNext() || !current.isValidNode()) {
      return null;
    }
    current = current.next();
    return current;
  }

  public void remove() {
    if (!current.isValidNode()) {
      return;
    }
    current.remove();
  }
}