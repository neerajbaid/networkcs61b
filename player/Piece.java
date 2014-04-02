package player;

public class Piece
{
  public int x;
  public int y;
  public int color;
  private Board board;

  public Piece(int color, int x, int y) {
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public String toString() {
    return "[" + color + ":" + x + "," + y + "]";
  }
}
