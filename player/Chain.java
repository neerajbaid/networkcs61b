package player;

import list.*;

/**
  * Class representing a network in the game.
  */
public class Chain {
  //The player to whom this network belongs.
  public int color;
  private DList pieces;

  /**
    * Initializes default Chain with color representing the player whose pieces are in the chain
    */
  public Chain(int color) {
    pieces = new DList();
    this.color = color;
  }

  /**
    * Checks if this network contains a piece.
    */
  public boolean contains(Piece piece) {
    return pieces.contains(piece);
  }

  /**
    * Add a piece to this network.
    */
  public void addPiece(Piece piece) {
    pieces.insertBack(piece);
  }

  /**
    * Returns the number of pieces in this network.
    */
  public int numPieces() {
    return pieces.length();
  }

  /**
    * Returns all the pieces in this network.
    * Returns a DList of the pieces
    */
  public DList getPieces() {
    return pieces;
  }

  /**
    * Returns a shallow copy of this network.
    * Returns a Chain object
    */
  public Chain copy() {
    Chain copy = new Chain(color);
    copy.pieces = pieces.copy();
    return copy;
  }

  /**
    * Returns the first Piece in this network.
    */
  public Piece first() {
    ListNode node = pieces.front();
    if (node.isValidNode()) {
      return (Piece) node.item();
    }
    return null;
  }

  // Returns String representation of instance
  public String toString() {
    return pieces.toString();
  }
}
