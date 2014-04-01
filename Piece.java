import list.*;
import dict.*;

public class Piece
{
  public int[] coordinate;
  public int color;

  public static final int WHITE_COLOR = 0;
  public static final int BLACK_COLOR = 1;

  public Piece(int color, int x, int y) {
    this.color = color;
    coordinate = {x, y};
  }
}
