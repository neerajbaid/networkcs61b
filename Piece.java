import list.*;
import dict.*;

public class Piece
{
  private int[] coordinate;
  private int color;

  public static final int WHITE_COLOR = 0;
  public static final int BLACK_COLOR = 1;

  public int getColor()
  {
    return color;
  }

  public int[] getCoordinate()
  {
    return coordinate;
  }
}
