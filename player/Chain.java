package player;

import list.*;
import dict.*;

public class Chain
{
  //The player to whom this network belongs.
  public int color;

  //All the pieces in this network.
  public DList pieces;

  public Chain(int color) {
    pieces = new DList();
    this.color = color;
  }

  /**
    * Checks if this network contains piece.
    */
  public boolean contains(Piece piece)
  {
    return pieces.contains(piece);
  }

  /**
    * Adds piece to this network.
    */
  public void addPiece(Piece piece)
  {
    pieces.insertBack(piece);
  }

  /**
    * Returns the number of pieces in this network.
    */
  public int numPieces()
  {
    return pieces.length();
  }

  /**
    * Returns all the pieces in this network.
    */
  public DList getPieces(){
    return pieces;
  }

  /**
    * Returns a copy of this network.
    */
  public Chain copy() {
    Chain copy = new Chain(color);
    copy.pieces = pieces.copy();
    return copy;
  }

  /**
    * Returns the first piece of this network.
    */
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
