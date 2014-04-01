package player;

public class Piece
{
  public int x;
  public int y;
  public int color;
  public int neighbors;
  private Board board;

  public static final int WHITE_COLOR = 0;
  public static final int BLACK_COLOR = 1;

  public Piece(int color, int x, int y) {
    neighbors = 0;
    this.x = x;
    this.y = y;
    this.color = color;
  }
}
