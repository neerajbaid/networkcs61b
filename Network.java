import list.*;
import dict.*;

public class Network
{
  public int color;
  public DList pieces;

  public Network(int color) {
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

  public Network copy() {
    Network copy = new Network(color);
    copy.pieces = pieces.copy();
    return copy;
  }
}
