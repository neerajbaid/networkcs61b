import list.*;
import dict.*;
import player.*;

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
  
  private static final int WHITE_WIN = 0, BLACK_WIN = 1;

  private static final int BOARD_LENGTH = 8;

  public Board() {
    board = new Piece[BOARD_LENGTH][BOARD_LENGTH];
  }

  
  public int evaluate(int playerIn) {
    int player = playerIn;
    
    DList networks = this.findAllNetworks(player);
    boolean reachesGoal = false;
    ListNode current = networks.front();
    while(current != null) {
      Network network = (Network) current.item();
      
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
      Network network = (Network) current.item();
      
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
  
  private boolean isOnGoal(Piece piece) {
    return piece.coordinate[0] == 0 && piece.coordinate[1] != 0 || piece.coordinate[1] == 0 && piece.coordinate[0] != 0;   
  }

  public boolean isValidMove(Move move){
    int difference = move.x1 - move.y1;
    boolean notInCorner = difference != 0 && difference != BOARD_LENGTH;
    boolean pieceThere = this.board[move.x1][move.y1] != null;
    
    //count how many pieces in vacinity
    int counter = 0;
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        try{
          counter += board[move.x1 + i][move.y1 + j] != null ? 1 : 0;
        } catch(Exception e){}
      }
    }
    
    boolean clusterAround = counter > 2;
    
    //check if placed in opposite color's goal
    
    return notInCorner && !pieceThere && !clusterAround;
    
  }

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
  }

  public void undoMove(Move move) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    if (move.moveKind == move.STEP) {
      board[move.x2][move.y2] = board[move.x1][move.y1];
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
      Network network = findNetwork(piece, new Network(), color);
      if (network != null)
        networks.insertFront(network);
    }
    if (networks.length() == 0)
      return null;
    else
      return networks;
  }

  public Network findNetwork(Piece piece, Network currentNetwork, int color)
  {
    DList endZonePieces = endZonePieces(color);
    if (pieceIsInEndZone(piece))
      return currentNetwork;
    for (int direction : DIRECTIONS)
    {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (nextPiece == null)
        continue;
      else if (currentNetwork.contains(piece))
        continue;
      else if (nextPiece.color != piece.color)
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
    return (coordinate[0] < BOARD_LENGTH && coordinate[0] > 0
            && coordinate[1] < BOARD_LENGTH && coordinate[1] > 0);
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
  }
}
