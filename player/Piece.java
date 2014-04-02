package player;

/**
  * Class representing a piece in the game board
  * Stores the owner of the piece (player) and own coordinates
  */
public class Piece {
  public int x;
  public int y;
  public int color;

  /**
    * Default constructor.
    * Takes in in representing piece's color, and an x and a y coordinate. 
    */
  public Piece(int color, int x, int y) {
    this.x = x;
    this.y = y;
    this.color = color;
  }

  /**
    * Returns String representation of the piece for debugging purposes
    */
  public String toString() {
    return "[" + color + ":" + x + "," + y + "]";
  }
}
