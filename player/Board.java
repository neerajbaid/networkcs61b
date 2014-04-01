package player;

import list.*;
import dict.*;

public class Board
{
  private Piece[][] board;

  public static final int WHITE_COLOR = 0;
  public static final int BLACK_COLOR = 1;

  public static final int DIRECTION_NONE = -1;
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
  
  private static final int WHITE_WIN = 0, BLACK_WIN = 1;

  public static final int LENGTH = 8;
  public static final int END_INDEX = 7;

  public Board() {
    board = new Piece[LENGTH][LENGTH];
  }
  

  // CHECKING VALID MOVE

  private boolean isOnGoal(Piece piece) {
    return piece.coordinate[0] == 0 && piece.coordinate[1] != 0 || piece.coordinate[1] == 0 && piece.coordinate[0] != 0;   
  }

  public boolean isOnInvalidGoal(int x, int y, int color) {
    if (color == WHITE_COLOR) {
      return y == 0 || y == END_INDEX;
    }
    else {
      return x == 0 || x == END_INDEX;
    }
  }

  public boolean isInCorner(int x, int y) {
    if (x == 0 || y == 0 || x == END_INDEX || y == END_INDEX) {
      int difference = Math.abs(x-y);
      return (difference == 0 || difference == END_INDEX);
    }
    return false;
  }

  public boolean hasNeighbor(int[] coordinate, int color) {
    int x = coordinate[0];
    int y = coordinate[1];
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        try{
          Piece piece = board[x + i][y + j];
          if (piece == null || (i == 0 && j == 0 )) {
            continue;
          }
          if (piece.color == color) {
            return true;
          }
        } catch(Exception e){}
      }
    }
    return false;
  }

  public boolean isInCluster(int x, int y, int color) {
    //count how many pieces in vacinity
    int counter = 0;
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        try{
          Piece piece = board[x + i][y + j];
          if (piece == null || (i == 0 && j == 0 )) {
            continue;
          }
          if (piece.color == color) {
            counter += 1 + piece.neighbors;
          }
        } catch(Exception e){}
      }
    }
    return counter > 2;
  }

  public boolean isValidMove(Move move, int color){
    int x = move.x1;
    int y = move.y1;
    if (isInCorner(x,y)) {
      return false;
    }
    // piece already there:
    if (board[x][y] != null) {
      return false;
    }
    if (isOnInvalidGoal(x,y,color)) {
      return false;
    }
    return isInCluster(move.x1, move.y1, color);
  }

  // MANIPULATING BOARD

  // Must be a valid move. If not valid, will break
  public void performValidMove(Move move, int color) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    Piece piece;
    if (move.moveKind == move.STEP) {
      piece = board[move.x2][move.y2];
      board[move.x2][move.y2] = null;
    }
    else {
      piece = new Piece(color, move.x1, move.y1);
    }
    board[move.x1][move.y1] = piece;
    piece.coordinate = new int[] {move.x1, move.y1};
    piece.neighbors = (hasNeighbor(piece.coordinate, color) ? 1 : 0);
  }

  public void undoMove(Move move, int color) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    if (move.moveKind == move.STEP) {
      Piece piece = board[move.x1][move.y1];
      board[move.x2][move.y2] = piece;
      piece.coordinate = new int[] {move.x2, move.y2};
      piece.neighbors = hasNeighbor(piece.coordinate, color) ? 1 : 0;
    }
    board[move.x1][move.y1] = null;
  }


  // # pragma mark - Network Finding #iOSProgrammers #ye

  public DList findAllNetworks(int color)
  {
    DList beginningZonePieces = beginningZonePieces(color);
    DList networks = new DList();
    for (ListNode pieceNode : beginningZonePieces)
    {
      Piece piece = (Piece) pieceNode.item();
      Chain network = findNetwork(piece, new Chain(color), DIRECTION_NONE);
      if (network != null)
        networks.insertFront(network);
    }
    if (networks.length() == 0)
      return null;
    else
      return networks;
  }

  public Chain findNetwork(Piece piece, Chain currentNetwork, int prevDirection)
  {
    // DList endZonePieces = endZonePieces(color);
    if (pieceIsInTargetEndZone(piece, currentNetwork))
      return currentNetwork;
    for (int direction : DIRECTIONS)
    {
      if (direction == prevDirection) {
        continue;
      }
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (nextPiece == null)
        continue;
      else if (currentNetwork.contains(nextPiece)) // very bad performance here
        continue;
      else if (nextPiece.color != currentNetwork.color)
        continue;
      else
      {
        currentNetwork = currentNetwork.copy();
        currentNetwork.addPiece(nextPiece);
        return findNetwork(nextPiece, currentNetwork, direction);
      }
    }
    return null;
  }

  public boolean pieceIsInTargetEndZone(Piece piece, Chain network) {
    int x = piece.coordinate[0];
    int y = piece.coordinate[1];
    int color = network.color;
    if (isOnInvalidGoal(x,y,color)) {
      return false;
    }
    int startX = network.first().coordinate[0];
    int startY = network.first().coordinate[1];
    if (color == WHITE_COLOR) {
      return startX != piece.coordinate[0];
    }
    // black:
    return startY != piece.coordinate[1];
  }

  public Piece findNextPieceInDirection(Piece piece, int direction)
  {
    int[] coordinate = piece.coordinate;
    while (pieceAtCoordinate(coordinate) == null)
    {
      if (!containsCoordinate(coordinate))
        return null;
      else
        coordinate = incrementCoordinateInDirection(coordinate, direction);
    }
    return pieceAtCoordinate(coordinate);
  }

  public int[] incrementCoordinateInDirection(int[] coordinate, int direction)
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
    return (coordinate[0] < LENGTH && coordinate[0] > 0
            && coordinate[1] < LENGTH && coordinate[1] > 0);
  }

  public Piece pieceAtCoordinate(int[] coordinate)
  {
    return board[coordinate[0]][coordinate[1]];
  }

  public DList beginningZonePieces(int color)
  {
    DList pieces = new DList();
    if (color == WHITE_COLOR)
    {
      for (int i = 1; i < board[0].length-1; i++)
      {
        int[] coordinate = {i,0};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else if (color == BLACK_COLOR)
    {
      for (int i = 1; i < board[0].length-1; i++)
      {
        int[] coordinate = {0,i};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else
      System.out.println("color error");
    return null;
  }

  public DList endZonePieces(int color)
  {
    DList pieces = new DList();
    if (color == WHITE_COLOR)
    {
      for (int i = 1; i < board[0].length-1; i++)
      {
        int[] coordinate = {i,board[0].length-1};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else if (color == BLACK_COLOR)
    {
      for (int i = 1; i < board[0].length-1; i++)
      {
        int[] coordinate = {board[0].length-1, i};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else {
      System.out.println("color error");
    }
    return null;
  }

  public int evaluate(int playerIn) {
    int player = playerIn;
    
    DList networks = this.findAllNetworks(player);
    boolean reachesGoal = false;
    ListNode current = networks.front();
    while(current != null) {
      Chain network = (Chain) current.item();
      
      DList pieces = network.getPieces();
      Piece front = (Piece) pieces.front().item();
      Piece back = (Piece) pieces.back().item();
      
      if(this.isOnGoal(front) && this.isOnGoal(back)) {
        return 1;
      }
      
      current = current.next();
    }

    //switch players
    player = 1 - player;
    networks = this.findAllNetworks(player);
    reachesGoal = false;
    current = networks.front();
    while(current != null){
      Chain network = (Chain) current.item();
      
      DList pieces = network.getPieces();   
      Piece front = (Piece) pieces.front().item();
      Piece back = (Piece) pieces.back().item();

      if(this.isOnGoal(front) && this.isOnGoal(back)){
        return -1;
      }
      
      current = current.next();
    }
    
    return 0;
  }
}