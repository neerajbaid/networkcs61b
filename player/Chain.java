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
    return pieces.contains(piece);
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
    return (Piece) pieces.front().item();
  }
}
