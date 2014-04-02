package player;

import list.*;
import dict.*;
import java.util.Arrays;

public class Board
{
  private Piece[][] board;

  public static final int WHITE = 0;
  public static final int BLACK = 1;

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

  private static final int MY_WIN = 1, OPP_WIN = -1;

  public static final int LENGTH = 8;
  public static final int END_INDEX = 7;

  public static DList current_networks;

  public Board() {
    board = new Piece[LENGTH][LENGTH];
    current_networks = new DList();
  }


  // CHECKING VALID MOVE
  protected static int flipColor(int color) {
    return Math.abs(color - 1);
  }

  private boolean isOnValidGoal(int x, int y, int color) {
    if (isInCorner(x,y)) {
      return false;
    }
    if (color == WHITE) {
      return x == 0 || x == END_INDEX;
    }
    else {
      return y == 0 || y == END_INDEX;
    }
  }

  private boolean isOnInvalidGoal(int x, int y, int color) {
    return isOnValidGoal(x,y,flipColor(color));
  }

  private boolean isInCorner(int x, int y) {
    if (x == 0 || y == 0 || x == END_INDEX || y == END_INDEX) {
      int difference = Math.abs(x-y);
      return (difference == 0 || difference == END_INDEX);
    }
    return false;
  }

  private boolean isInChainedCluster(int x, int y, int color) {
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

  private boolean isInCluster(int x, int y, int color) {
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
            counter += 1;
            if (isInChainedCluster(x + i, y + j, color)) {
              return true;
            }
          }
        } catch(Exception e){}
      }
    }
    return counter >= 2;
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
    return !isInCluster(move.x1, move.y1, color);
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
    piece.x = move.x1;
    piece.y = move.y1;
  }

  public void undoMove(Move move) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    if (move.moveKind == move.STEP) {
      Piece piece = board[move.x1][move.y1];
      board[move.x2][move.y2] = piece;
      piece.x = move.x2;
      piece.y = move.y2;
    }
    board[move.x1][move.y1] = null;
  }


  // # pragma mark - Network Finding #iOSProgrammers #ye

  public DList findAllNetworks(int color)
  {
    current_networks = new DList();
    DList beginningZonePieces = beginningZonePieces(color);
    System.out.println("beginning zone: " + beginningZonePieces.toString());
    System.out.println("first beginning piece: " + beginningZonePieces.front().item().toString());
    DList networks = new DList();
    int length = beginningZonePieces.length();
    ListNode pieceNode = beginningZonePieces.front();

    while (length > 0)
    {
      Piece piece = (Piece) pieceNode.item();
      System.out.println("starting piece: " + piece);
      Chain beginning = new Chain(color);
      beginning.addPiece(piece);
      System.out.println("starting chain: " + beginning.toString());
      Chain network = findNetwork(piece, beginning, DIRECTION_NONE);
      if (network == null)
        System.out.println("returned completed network, network is null");
      else
        System.out.println("returned completed network, network is not null");
      if (network != null)
        networks.insertFront(network);
      length--;
      pieceNode = pieceNode.next();
    }

    return networks;
  }

  public Chain findNetwork(Piece piece, Chain currentNetwork, int prevDirection)
  {
    if (pieceIsInTargetEndZone(piece, currentNetwork))
    {
      System.out.println("piece is in target end zone");
      Chain completed = currentNetwork.copy();
      System.out.println("complete network one: " + currentNetwork.toString());
      System.out.println("complete network two: " + completed.toString());
      if (completed.numPieces() >= 6)
      {
        current_networks.insertFront(completed);
        return completed;
      }
      else
        return null;
    }
    for (int direction : DIRECTIONS)
    {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (direction == prevDirection)
        continue;
      if (nextPiece == null)
      {
        System.out.println("next piece is null");
        continue;
      }
      else if (currentNetwork.contains(nextPiece))
      {
        System.out.println("network contains piece");
        continue;
      }
      else if (nextPiece.color != currentNetwork.color)
      {
        System.out.println("piece of opposite color blocking");
        continue;
      }
      else
      {
        System.out.println("current network before copy: " + currentNetwork.toString());
        Chain next = currentNetwork.copy();
        System.out.println("current network before adding: " + next.toString());
        System.out.println("next piece: " + nextPiece.toString());
        next.addPiece(nextPiece);
        System.out.println("current network after adding: " + next.toString());
        findNetwork(nextPiece, next, direction);
      }
    }
    System.out.println("return null");
    return null;
  }

  public boolean pieceIsInTargetEndZone(Piece piece, Chain network) {
    int x = piece.x;
    int y = piece.y;
    int color = network.color;
    if (!isOnValidGoal(x,y,color)) {
      return false;
    }
    Piece start = network.first();
    if (start == null) {
      return false;
    }
    int startX = start.x;
    int startY = start.y;
    if (color == WHITE) {
      return startX != piece.x;
    }
    // black:
    return startY != piece.y;
  }

  public Piece findNextPieceInDirection(Piece piece, int direction)
  {
    int[] pieceCoordinate = new int[] {piece.x, piece.y};
    System.out.println("initial coordinate: " + pieceCoordinate[0] + " " + pieceCoordinate[1] + " direction: " + direction);
    int[] coordinate = incrementCoordinateInDirection(pieceCoordinate, direction);
    System.out.println("incremented coordinate: " + coordinate[0] + " " + coordinate[1]);
    while (containsCoordinate(coordinate))
    {
      Piece next = pieceAtCoordinate(coordinate);
      if (next != null) {
        return next;
      }
      coordinate = incrementCoordinateInDirection(coordinate, direction);
      System.out.println("incremented coordinate: " + coordinate[0] + " " + coordinate[1]);
    }
    return null;
  }

  public int[] incrementCoordinateInDirection(int[] coordinate, int direction)
  {
    int x = coordinate[0];
    int y = coordinate[1];
    if (direction == DIRECTION_UP)
      y--;
    else if (direction == DIRECTION_UP_RIGHT)
    {
      x++;
      y--;
    }
    else if (direction == DIRECTION_RIGHT)
      x++;
    else if (direction == DIRECTION_DOWN_RIGHT)
    {
      x++;
      y++;
    }
    else if (direction == DIRECTION_DOWN)
      y++;
    else if (direction == DIRECTION_DOWN_LEFT)
    {
      x--;
      y++;
    }
    else if (direction == DIRECTION_LEFT)
      x--;
    else if (direction == DIRECTION_UP_LEFT)
    {
      x--;
      y--;
    }
    coordinate[0] = x;
    coordinate[1] = y;
    return coordinate;
  }

  // # pragma mark - Utility Methods #iOSProgrammers

  public boolean containsCoordinate(int[] coordinate)
  {
    int x = coordinate[0];
    int y = coordinate[1];
    return (x < LENGTH && x >= 0
            && y < LENGTH && y >= 0);
  }

  public Piece pieceAtCoordinate(int[] coordinate)
  {
    int x = coordinate[0];
    int y = coordinate[1];
    return board[x][y];
  }

  public DList beginningZonePieces(int color)
  {
    DList pieces = new DList();
    if (color == WHITE)
    {
      for (int i = 1; i < END_INDEX; i++)
      {
        int[] coordinate = {0,i};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else if (color == BLACK)
    {
      for (int i = 1; i < END_INDEX; i++)
      {
        int[] coordinate = {i,0};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    return pieces;
  }

  public DList endZonePieces(int color)
  {
    DList pieces = new DList();
    if (color == WHITE)
    {
      for (int i = 1; i < END_INDEX; i++)
      {
        int[] coordinate = {END_INDEX, i};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    else if (color == BLACK)
    {
      for (int i = 1; i < END_INDEX; i++)
      {
        int[] coordinate = {i,END_INDEX};
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    return pieces;
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

      if(this.isOnValidGoal(front.x, front.y, playerIn) && this.isOnValidGoal(back.x, back.y, playerIn)) {
        return MY_WIN;
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

      if(this.isOnValidGoal(front.x, front.y, playerIn) && this.isOnValidGoal(back.x, back.y, playerIn)) {
        return OPP_WIN;
      }

      current = current.next();
    }

    return 0;
  }


  // TESTING CODE:

  public String toString() {
    String result = "";
    for (int y = 0; y < LENGTH; y ++ ) {
      int x = 0;
      for (Piece[] pieces : board) {
        Piece piece = pieces[y];
        if (piece == null) {
          result += String.format("%1$-" + 8 + "s","(" + x + "" + y + ")");
        }
        else {
          result += String.format("%1$-" + 8 + "s", pieces[y] + " ");
        }
        x++;
      }
      result += "\n";
    }
    return result;
  }
  private static void expect(Object expect, Object o) {
    System.out.println("Expect " + expect + ": " + o);
  }
  private static void print(Object o) {
    System.out.println(o);
  }
  public static void main(String[] args) {
    Board b = new Board();

    // isOnValidGoal
    print("isOnValidGoal");
    expect(false, !b.isOnValidGoal(1,0,BLACK));
    expect(false, !b.isOnValidGoal(6,0,BLACK));
    expect(true, !b.isOnValidGoal(0,1,BLACK));
    expect(true, !b.isOnValidGoal(7,4,BLACK));
    expect(true, !b.isOnValidGoal(1,0,WHITE));
    expect(true, !b.isOnValidGoal(6,0,WHITE));
    expect(false, !b.isOnValidGoal(0,1,WHITE));
    expect(false, !b.isOnValidGoal(7,4,WHITE));

    // isInCorner
    print("Corner");
    expect(true, b.isInCorner(0,0));
    expect(true, b.isInCorner(7,0));
    expect(true, b.isInCorner(7,7));
    expect(false, b.isInCorner(7,4));
    expect(false, b.isInCorner(5,5));
    expect(false, b.isInCorner(0,1));

    // performValidMove and undoMove
    print("Perform and undo");
    Move m = new Move(1,5);
    b.performValidMove(m, BLACK);
    print(b);

    m = new Move(3,6,1,5);
    b.performValidMove(m, BLACK);
    print(b);

    b.undoMove(m);
    print(b);

    m = new Move(7,2);
    b.performValidMove(m, WHITE);
    print(b);
    b.undoMove(m);
    print(b);

    // isInCluster
    print("inCluster");
    b.performValidMove(m, WHITE);
    m = new Move(6,3);
    b.performValidMove(m, WHITE);
    print(b);
    expect(true, b.isInCluster(7,3,WHITE));
    expect(true, b.isInCluster(7,4,WHITE));
    expect(false, b.isInCluster(7,4,BLACK));
    expect(true, b.isInCluster(7,2,WHITE));

    // isValidMove
    print("isValidMove");
    m = new Move(7,4);
    expect(false, b.isValidMove(m, WHITE));
    expect(false, b.isValidMove(m, BLACK));
    m = new Move(6,2);
    expect(true, b.isValidMove(m, BLACK));
    m = new Move(2,7);
    expect(false, b.isValidMove(m, WHITE));
    expect(true, b.isValidMove(m, BLACK));
    m = new Move(1,5);
    expect(false, b.isValidMove(m, WHITE));
    m = new Move(7,7);
    expect(false, b.isValidMove(m, BLACK));

    // FINDING NETWORKS
    print("");
    print("FINDING NETWORKS");

    //beginning zone pieces
    expect(0,b.beginningZonePieces(WHITE).length());
    m = new Move(0,2);
    b.performValidMove(m, WHITE);
    expect(1,b.beginningZonePieces(WHITE).length());
    m = new Move(2,0);
    b.performValidMove(m, BLACK);
    expect(1,b.beginningZonePieces(BLACK).length());

    //increment direction
    int[] c;
    c = b.incrementCoordinateInDirection(new int[]{0,0},DIRECTION_UP_LEFT);
    expect("[-1, -1]",Arrays.toString(c));

    //findNextPieceInDirection
    Piece p = b.pieceAtCoordinate(new int[] {1,5});
    Piece p1 = b.findNextPieceInDirection(p,DIRECTION_RIGHT);
    expect(null, p1);
    m = new Move(4,2);
    b.performValidMove(m, BLACK);
    p1 = b.findNextPieceInDirection(p, DIRECTION_UP_RIGHT);
    expect("[1:4,2]", p1);

    // isintargetEndZone
    m = new Move(0,4);
    b.performValidMove(m, WHITE);
    print(b);
    Chain ch = new Chain(WHITE);
    ch.addPiece(b.board[0][2]);
    expect(true, b.pieceIsInTargetEndZone(b.board[7][2], ch));
    expect(false, b.pieceIsInTargetEndZone(b.board[0][4], ch));

    //Find Network:
    m = new Move(4,3);
    b.performValidMove(m, WHITE);
    m = new Move(2,3);
    b.performValidMove(m, WHITE);
    m = new Move(0,3);
    b.performValidMove(m, WHITE);
    print(b);
    b.findAllNetworks(WHITE);
    System.out.println("all networks: " + current_networks.toString());
    // expect(0, b.findAllNetworks(WHITE).length());
    expect(0, b.current_networks.length());
    print(b);

    b.board[0][2] = null;
    b.board[0][4] = null;
    m = new Move(3,4);
    b.performValidMove(m, WHITE);
    b.findAllNetworks(WHITE);
    print(b);
    System.out.println("all networks: " + current_networks.toString());
    expect(1, b.current_networks.length());

    b.board[2][3] = null;
    b.board[3][4] = null;
    b.findAllNetworks(WHITE);
    print(b);
    System.out.println("all networks: " + current_networks.toString());
    expect(0, b.current_networks.length());

  }
}
