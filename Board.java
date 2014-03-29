import list.*;
import dict.*;

public class Board
{
  private Piece[][] board;

  public static final int WHITE_COLOR = 0;
  public static final int BLACK_COLOR = 1;

  public static final int DIRECTION_UP = 0;
  public static final int DIRECTION_UP_RIGHT = 1;
  public static final int DIRECTION_RIGHT = 2;
  public static final int DIRECTION_DOWN_RIGHT = 3;
  public static final int DIRECTION_DOWN = 4;
  public static final int DIRECTION_DOWN_LEFT = 5;
  public static final int DIRECTION_LEFT = 6;
  public static final int DIRECTION_UP_LEFT = 7;
  public static final int[] DIRECTIONS = {DIRECTION_UP, DIRECTION_UP_RIGHT,
    DIRECTION_RIGHT, DIRECTION_DOWN_RIGHT, DIRECTION_DOWN,
    DIRECTION_DOWN_LEFT, DIRECTION_LEFT, DIRECTION_UP_LEFT};

  public Board() {
    board = new Piece[8][8];
  }

  // # pragma mark - Network Finding #iOSProgrammers

  public SList findAllNetworks(int color)
  {
    SList beginningZonePieces = beginningZonePieces(color);
    SList networks = new SList();
    for (Piece piece in beginningZonePieces)
    {
      Network network = findNetwork(piece, new Network())
      if (network != null)
        networks.insertFront(network);
    }
    if (networks.size() == 0)
      return null;
    else
      return networks;
  }

  public Network findNetwork(Piece piece, Network currentNetwork)
  {
    SList endZonePieces = endZonePieces(color);
    if (pieceIsInEndZone(piece))
      return currentNetwork;
    for (int direction in DIRECTIONS)
    {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (nextPiece == null)
        continue;
      else if (currentNetwork.contains(piece))
        continue;
      else if (nextPiece.getColor() != piece.getColor())
        continue;
      else
      {
        currentNetwork.addPiece(nextPiece);
        findAllNetworks(nextPiece, currentNetwork, direction);
      }
    }
  }

  public Piece findNextPieceInDirection(Piece piece, int direction)
  {
    int[] coordinate = piece.getCoordinate();
    while (pieceAtCoordinate(coordinate) == null)
    {
      if (!containsCoordinate(coordinate))
        return null;
      else
        coordinate = incrementCoordinateInDirection(coordinate, direction);
    }
    return pieceAtCoordinate(coordinate);
  }

  public int[] incrementCoordinateInDirection(coordinate, direction)
  {
    if (direction == DIRECTION_UP)
      coordinate[1]--;
    else if (direction == DIRECTION_UP_RIGHT)
    {
      coordinate[0]++;
      coordinate[1]--;
    }
    else if (direction == DIRECTION_RIGHT)
      coordinate[0]++;
    else if (direction == DIRECTION_DOWN_RIGHT)
    {
      coordinate[0]++;
      coordinate[1]++;
    }
    else if (direction == DIRECTION_DOWN)
      coordinate[1]++;
    else if (direction == DIRECTION_DOWN_LEFT)
    {
      coordinate[0]--;
      coordinate[1]++;
    }
    else if (direction == DIRECTION_LEFT)
      coordinate[0]--;
    else if (direction == DIRECTION_UP_LEFT)
    {
      coordinate[0]--;
      coordinate[1]--;
    }
    else
      System.out.println("direction error");
    return coordinate;
  }

  // # pragma mark - Utility Methods #iOSProgrammers

  public boolean containsCoordinate(int[] coordinate)
  {
    return (coordinate[0] < board.length() && coordinate[0] > 0
            && coordinate[1] < board.length() && coordinate[1] > 0);
  }

  public Piece pieceAtCoordinate(int[] coordinate)
  {
    return board[coordinate[0]][coordinate[1]];
  }

  public SList beginningZonePieces(int color)
  {
    SList pieces = new SList();
    if (color == WHITE_COLOR)
    {
      for (int i = 1; i < board[0].length-1)
      {
        int[] coordinate = [i,0];
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else if (color == BLACK_COLOR)
    {
      for (int i = 1; i < board[0].length-1)
      {
        int[] coordinate = [0,i];
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else
      System.out.println("color error");
  }

  public SList endZonePieces(int color)
  {
    SList pieces = new SList();
    if (color == WHITE_COLOR)
    {
      for (int i = 1; i < board[0].length-1)
      {
        int[] coordinate = [i,board[0].length-1];
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else if (color == BLACK_COLOR)
    {
      for (int i = 1; i < board[0].length-1)
      {
        int[] coordinate = [board[0].length-1,i];
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else
      System.out.println("color error");
  }
}
