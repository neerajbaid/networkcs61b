package player;

import list.*;
import dict.*;

public class Chain
{
  public int color;
  public DList pieces;

  public Chain(int color) {
    pieces = new DList();
    this.color = color;
  }

  public boolean contains(Piece piece)
  {
    if (pieces.front().item() == piece)
      return true;
    return pieces.contains(piece); //fast enumeration here skips first piece
  }

  public void addPiece(Piece piece)
  {
    pieces.insertBack(piece);
  }

  public DList getPieces(){
    return pieces;
  }

  public Chain copy() {
    Chain copy = new Chain(color);
    copy.pieces = pieces.copy();
    return copy;
  }

  public Piece first() {
    ListNode node = pieces.front();
    if (node.isValidNode()) {
      return (Piece) node.item();
    }
    return null;
  }

  public String toString() {
    String toReturn = pieces.toString();
    return toReturn;
  }
}
