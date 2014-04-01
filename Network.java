import list.*;
import dict.*;

public class Network
{
  private int color;
  private DList pieces;

  public boolean contains(Piece piece)   
  {
    return (pieces.contain(piece));
  }

  public void addPiece(Piece piece)
  {
    pieces.insertBack(piece);
  }
  
  public DList getPieces(){
	  return this.pieces;
  }
}
