import list.*;
import dict.*;

public class Network
{
  private int color;
  private SList pieces;

  public boolean contains(Piece piece)   
  {
    return (pieces.contain(piece));
  }

  public void addPiece(Piece piece)
  {
    pieces.insertBack(piece);
  }
  
  public SList getPieces(){
	  return this.pieces;
  }
}
