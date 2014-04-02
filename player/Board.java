package player;

import list.*;
import dict.*;
import java.util.Arrays;

public class Board {
  private Piece[][] board;

  //White and Black
  public static final int WHITE = 0;
  public static final int BLACK = 1;

  //Directions
  public static final int DIRECTION_NONE = -1;
  public static final int DIRECTION_UP = 0;
  public static final int DIRECTION_UP_RIGHT = 1;
  public static final int DIRECTION_RIGHT = 2;
  public static final int DIRECTION_DOWN_RIGHT = 3;
  public static final int DIRECTION_DOWN = 4;
  public static final int DIRECTION_DOWN_LEFT = 5;
  public static final int DIRECTION_LEFT = 6;
  public static final int DIRECTION_UP_LEFT = 7;
  public static final int[] DIRECTIONS = { DIRECTION_UP, DIRECTION_UP_RIGHT,
      DIRECTION_RIGHT, DIRECTION_DOWN_RIGHT, DIRECTION_DOWN,
      DIRECTION_DOWN_LEFT, DIRECTION_LEFT, DIRECTION_UP_LEFT };

  //Winning
  private static final int MY_WIN = 1, OPP_WIN = -1;

  //Board Size
  public static final int LENGTH = 8;
  public static final int END_INDEX = LENGTH-1;

  public Board() {
    board = new Piece[LENGTH][LENGTH];
  }

  // CHECKING VALID MOVE

  /**
    * Switches the player color.
    * @parameter color: the integer representing the current player
    * @return           the other player
    */
  protected static int flipColor(int color) {
    return Math.abs(color - 1);
  }

  /**
    * Checks if a certain coordinate is in its goal zone.
    */
  private boolean isOnValidGoal(int x, int y, int color) {
    if (isInCorner(x, y)) {
      return false;
    }
    if (color == WHITE) {
      return x == 0 || x == END_INDEX;
    } else {
      return y == 0 || y == END_INDEX;
    }
  }

  /**
    * Checks if a certain coordinate is in the wrong goal.
    */
  private boolean isOnInvalidGoal(int x, int y, int color) {
    return isOnValidGoal(x, y, flipColor(color));
  }

  /**
    * Checks if a coordinate is in the corner of the board.
    */
  private boolean isInCorner(int x, int y) {
    if (x == 0 || y == 0 || x == END_INDEX || y == END_INDEX) {
      int difference = Math.abs(x - y);
      return (difference == 0 || difference == END_INDEX);
    }
    return false;
  }

  /**
    *
    */
  private boolean isInChainedCluster(int x, int y, int color) {
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        try {
          Piece piece = board[x + i][y + j];
          if (piece == null || (i == 0 && j == 0)) {
            continue;
          }
          if (piece.color == color) {
            return true;
          }
        } catch (Exception e) {
        }
      }
    }
    return false;
  }

  /**
    *
    */
  private boolean isInCluster(int x, int y, int color) {
    //count how many pieces in vicinity
    int counter = 0;
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        try {
          Piece piece = board[x + i][y + j];
          if (piece == null || (i == 0 && j == 0)) {
            continue;
          }
          if (piece.color == color) {
            counter += 1;
            if (isInChainedCluster(x + i, y + j, color)) {
              return true;
            }
          }
        } catch (Exception e) {
        }
      }
    }
    return counter >= 2;
  }

  /**
    * Checks if a certain move by a specific player is valid on the Board. Adheres
    *   to all the rules outlined in the readme.
    */
  public boolean isValidMove(Move move, int color){
    int x = move.x1;
    int y = move.y1;
    if (isInCorner(x, y)) {
      return false;
    }
    // piece already there:
    if (board[x][y] != null) {
      return false;
    }
    if (isOnInvalidGoal(x, y, color)) {
      return false;
    }
    return !isInCluster(move.x1, move.y1, color);
  }

  // MANIPULATING BOARD

  /**
    * Performs a move for a specific player.
    * @parameter move:  Must be a valid move.
    */
  public void performValidMove(Move move, int color) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    Piece piece;
    if (move.moveKind == move.STEP) {
      piece = board[move.x2][move.y2];
      board[move.x2][move.y2] = null;
    } else {
      piece = new Piece(color, move.x1, move.y1);
    }
    board[move.x1][move.y1] = piece;
    piece.x = move.x1;
    piece.y = move.y1;
  }

  /**
    * Reverses a move.
    * @parameter move:  The Move that is to be reversed.
    */
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
  /**
    * Finds all the networks currently on the board of a certain player.
    * @return:  Returns all the networks that have been found.
    */
  public DList findAllNetworks(int color)
  {
    DList beginningZonePieces = beginningZonePieces(color);
    // System.out.println("beginning zone: " + beginningZonePieces.toString());
    // System.out.println("first beginning piece: " + beginningZonePieces.front().item().toString());
    DList networks = new DList();
    int length = beginningZonePieces.length();
    ListNode pieceNode = beginningZonePieces.front();

    while (length > 0) {
      Piece piece = (Piece) pieceNode.item();
      // System.out.println("starting piece: " + piece);
      Chain beginning = new Chain(color);
      beginning.addPiece(piece);
      // System.out.println("starting chain: " + beginning.toString());
      findNetwork(piece, beginning, DIRECTION_NONE, networks);
      // if (network == null)
        // System.out.println("returned completed network, network is null");
      // else
        // System.out.println("returned completed network, network is not null");
      length--;
      pieceNode = pieceNode.next();
    }
    return networks;
  }

  /**
    * Finds a network based on a starting piece. Each time it finds another piece,
    *   it adds it to the network it is currently constructing.
    * @parameter piece:          The Piece from where findNetwork should begin its search.
    * @parameter currentNetwork: The Chain currently being constructed.
    * @parameter prevDirection:  The direction from which the last piece was gained.
    *                              This is so that we cannot have more than 2 pieces
    *                              in a line, as specified in the readme.
    * @parameter networks:       A DList of all the networks that have already been found.
    */
  public void findNetwork(Piece piece, Chain currentNetwork, int prevDirection, DList networks)
  {
    if (pieceIsInTargetEndZone(piece, currentNetwork))
    {
      // System.out.println("piece is in target end zone");
      // System.out.println("complete network one: " + currentNetwork.toString());
      // System.out.println("complete network two: " + completed.toString());
      if (currentNetwork.numPieces() >= 6)
      {
        // current_networks.insertFront(completed);
        networks.insertBack(currentNetwork.copy());
        return; // check this for networks that run thru end zone and loop back
      }
    }

    for (int direction : DIRECTIONS)
    {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (direction == prevDirection) {
        continue;
      }
      if (nextPiece == null)
      {
        // System.out.println("next piece is null");
        continue;
      }
      else if (currentNetwork.contains(nextPiece))
      {
        // System.out.println("network contains piece");
        continue;
      }
      else if (nextPiece.color != currentNetwork.color)
      {
        // System.out.println("piece of opposite color blocking");
        continue;
      }
      else
      {
        // System.out.println("current network before copy: " + currentNetwork.toString());
        Chain next = currentNetwork.copy();
        // System.out.println("current network before adding: " + next.toString());
        // System.out.println("next piece: " + nextPiece.toString());
        next.addPiece(nextPiece);
        // System.out.println("current network after adding: " + next.toString());
        findNetwork(nextPiece, next, direction, networks);
      }
    }
  }

  /**
    * Checks if a piece is in its end zone.
    */
  public boolean pieceIsInTargetEndZone(Piece piece, Chain network) {
    int x = piece.x;
    int y = piece.y;
    int color = network.color;
    if (!isOnValidGoal(x, y, color)) {
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

  /**
    * Finds the next Piece of the same color in direction.
    */
  public Piece findNextPieceInDirection(Piece piece, int direction)
  {
    int[] pieceCoordinate = new int[] {piece.x, piece.y};
    // System.out.println("initial coordinate: " + pieceCoordinate[0] + " " + pieceCoordinate[1] + " direction: " + direction);
    int[] coordinate = incrementCoordinateInDirection(pieceCoordinate, direction);
    // System.out.println("incremented coordinate: " + coordinate[0] + " " + coordinate[1]);
    while (containsCoordinate(coordinate))
    {
      Piece next = pieceAtCoordinate(coordinate);
      if (next != null) {
        return next;
      }
      coordinate = incrementCoordinateInDirection(coordinate, direction);
      // System.out.println("incremented coordinate: " + coordinate[0] + " " + coordinate[1]);
    }
    return null;
  }

  /**
    *  Returns the next coordinate in direction.
    */
  public int[] incrementCoordinateInDirection(int[] coordinate, int direction) {
    int x = coordinate[0];
    int y = coordinate[1];
    if (direction == DIRECTION_UP)
      y--;
    else if (direction == DIRECTION_UP_RIGHT) {
      x++;
      y--;
    } else if (direction == DIRECTION_RIGHT)
      x++;
    else if (direction == DIRECTION_DOWN_RIGHT) {
      x++;
      y++;
    } else if (direction == DIRECTION_DOWN)
      y++;
    else if (direction == DIRECTION_DOWN_LEFT) {
      x--;
      y++;
    } else if (direction == DIRECTION_LEFT)
      x--;
    else if (direction == DIRECTION_UP_LEFT) {
      x--;
      y--;
    }
    coordinate[0] = x;
    coordinate[1] = y;
    return coordinate;
  }

  // # pragma mark - Utility Methods #iOSProgrammers

  /**
    *  Checks if coordinate is on the board.
    */
  public boolean containsCoordinate(int[] coordinate) {
    int x = coordinate[0];
    int y = coordinate[1];
    return (x < LENGTH && x >= 0 && y < LENGTH && y >= 0);
  }

  /**
    *  Returns the piece at coordinate.
    */
  public Piece pieceAtCoordinate(int[] coordinate) {
    int x = coordinate[0];
    int y = coordinate[1];
    return board[x][y];
  }

  /**
    *  Gets all the pieces in a player's beginning zone. One of the zones has been
    *    indicated beginning and one end simply for the purposes of network finding
    *    since networks are direction agnostic.
    */
  public DList beginningZonePieces(int color) {
    DList pieces = new DList();
    if (color == WHITE) {
      for (int i = 1; i < END_INDEX; i++) {
        int[] coordinate = { 0, i };
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    } else if (color == BLACK) {
      for (int i = 1; i < END_INDEX; i++) {
        int[] coordinate = { i, 0 };
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    return pieces;
  }

  /**
    *  Gets all the pieces in a player's end zone. One of the zones has been
    *    indicated beginning and one end simply for the purposes of network finding
    *    since networks are direction agnostic.
    */
  public DList endZonePieces(int color) {
    DList pieces = new DList();
    if (color == WHITE) {
      for (int i = 1; i < END_INDEX; i++) {
        int[] coordinate = { END_INDEX, i };
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    } else if (color == BLACK) {
      for (int i = 1; i < END_INDEX; i++) {
        int[] coordinate = { i, END_INDEX };
        Piece piece = pieceAtCoordinate(coordinate);
        if (piece != null)
          pieces.insertBack(piece);
      }
    }
    return pieces;
  }

  /**
    * Calculates the intermediate value of the board based on how many pairs are
    *  formed by the player and the opponent.
    */
  private int calcInter(int player) {
    DList playerPieces = piecesOfPlayer(player);
    DList opponentPieces = piecesOfPlayer(1 - player);
    DListNode currentPiece = (DListNode) playerPieces.front();
    int length = playerPieces.length();
    int yourScore = 0;

    while (length > 0) {
      yourScore += numPairsPieceCanForm((Piece)currentPiece.item());
      currentPiece = (DListNode) currentPiece.next();
      length--;
    }

    currentPiece = (DListNode) opponentPieces.front();
    length = opponentPieces.length();
    int otherScore = 0;
    while (length > 0) {
      otherScore += numPairsPieceCanForm((Piece)currentPiece.item());
      currentPiece = (DListNode) currentPiece.next();
      length--;
    }

    return yourScore - otherScore;
  }

  /**
    * Evaluates the board based on the intermediate score and whether or not
    *  there is a network completed.
    */
  public int evaluate(int playerIn) {
    int player = playerIn;

    findAllNetworks(player);
    DList networks = findAllNetworks(player);
    boolean reachesGoal = false;
    ListNode current = networks.front();
    int networkLength = networks.length();
    int counter = 0;

    while (counter < networkLength) {
      Chain network = (Chain) current.item();

      DList pieces = network.getPieces();
      Piece front = (Piece) pieces.front().item();
      Piece back = (Piece) pieces.back().item();

      if (this.isOnValidGoal(front.x, front.y, playerIn)
          && this.isOnValidGoal(back.x, back.y, playerIn)) {
        return Integer.MAX_VALUE;
      }

      current = current.next();
      counter++;
    }
    player = flipColor(player);
    networks = findAllNetworks(player);
    reachesGoal = false;
    current = networks.front();
    networkLength = networks.length();
    counter = 0;
    while (counter < networkLength) {
      Chain network = (Chain) current.item();
      DList pieces = network.getPieces();
      Piece front = (Piece) pieces.front().item();
      Piece back = (Piece) pieces.back().item();

      if (this.isOnValidGoal(front.x, front.y, playerIn)
          && this.isOnValidGoal(back.x, back.y, playerIn)) {
        return Integer.MIN_VALUE;
      }

      current = current.next();
      counter++;
    }

    return calcInter(playerIn);
  }

  /**
    * Returns a DList of all the pieces of a specified player.
    */
  public DList piecesOfPlayer(int player) {
    DList pieces = new DList();
    for (int x = 0; x < LENGTH; x++) {
      for (int y = 0; y < LENGTH; y++) {
    	  Piece piece = pieceAtCoordinate(new int[] { x, y });
        if (piece != null && piece.color == player) {
          pieces.insertBack(pieceAtCoordinate((new int[] { x, y })));

        }
      }
    }
    return pieces;
  }

  /**
    * Returns the number of pairs a Piece can form with the pieces around it.
    */
  public int numPairsPieceCanForm(Piece piece) {
    int num = 0;
    for (int direction : DIRECTIONS) {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (nextPiece != null) {
        num++;
      }
    }
    return num;
  }

  // TESTING CODE:

  public String toString() {
    String result = "";
    for (int y = 0; y < LENGTH; y++) {
      int x = 0;
      for (Piece[] pieces : board) {
        Piece piece = pieces[y];
        if (piece == null) {
          result += String.format("%1$-" + 8 + "s", "(" + x + "" + y
              + ")");
        } else {
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
    expect(false, !b.isOnValidGoal(1, 0, BLACK));
    expect(false, !b.isOnValidGoal(6, 0, BLACK));
    expect(true, !b.isOnValidGoal(0, 1, BLACK));
    expect(true, !b.isOnValidGoal(7, 4, BLACK));
    expect(true, !b.isOnValidGoal(1, 0, WHITE));
    expect(true, !b.isOnValidGoal(6, 0, WHITE));
    expect(false, !b.isOnValidGoal(0, 1, WHITE));
    expect(false, !b.isOnValidGoal(7, 4, WHITE));

    // isInCorner
    print("Corner");
    expect(true, b.isInCorner(0, 0));
    expect(true, b.isInCorner(7, 0));
    expect(true, b.isInCorner(7, 7));
    expect(false, b.isInCorner(7, 4));
    expect(false, b.isInCorner(5, 5));
    expect(false, b.isInCorner(0, 1));

    // performValidMove and undoMove
    print("Perform and undo");
    Move m = new Move(1, 5);
    b.performValidMove(m, BLACK);
    print(b);

    m = new Move(3, 6, 1, 5);
    b.performValidMove(m, BLACK);
    print(b);

    b.undoMove(m);
    print(b);

    m = new Move(7, 2);
    b.performValidMove(m, WHITE);
    print(b);
    b.undoMove(m);
    print(b);

    // isInCluster
    print("inCluster");
    b.performValidMove(m, WHITE);
    m = new Move(6, 3);
    b.performValidMove(m, WHITE);
    print(b);
    expect(true, b.isInCluster(7, 3, WHITE));
    expect(true, b.isInCluster(7, 4, WHITE));
    expect(false, b.isInCluster(7, 4, BLACK));
    expect(true, b.isInCluster(7, 2, WHITE));

    // isValidMove
    print("isValidMove");
    m = new Move(7, 4);
    expect(false, b.isValidMove(m, WHITE));
    expect(false, b.isValidMove(m, BLACK));
    m = new Move(6, 2);
    expect(true, b.isValidMove(m, BLACK));
    m = new Move(2, 7);
    expect(false, b.isValidMove(m, WHITE));
    expect(true, b.isValidMove(m, BLACK));
    m = new Move(1, 5);
    expect(false, b.isValidMove(m, WHITE));
    m = new Move(7, 7);
    expect(false, b.isValidMove(m, BLACK));

    // FINDING NETWORKS
    print("");
    print("FINDING NETWORKS");

    // beginning zone pieces
    expect(0, b.beginningZonePieces(WHITE).length());
    m = new Move(0, 2);
    b.performValidMove(m, WHITE);
    expect(1, b.beginningZonePieces(WHITE).length());
    m = new Move(2, 0);
    b.performValidMove(m, BLACK);
    expect(1, b.beginningZonePieces(BLACK).length());

    // increment direction
    int[] c;
    c = b.incrementCoordinateInDirection(new int[] { 0, 0 },
        DIRECTION_UP_LEFT);
    expect("[-1, -1]", Arrays.toString(c));

    // findNextPieceInDirection
    Piece p = b.pieceAtCoordinate(new int[] { 1, 5 });
    Piece p1 = b.findNextPieceInDirection(p, DIRECTION_RIGHT);
    expect(null, p1);
    m = new Move(4, 2);
    b.performValidMove(m, BLACK);
    p1 = b.findNextPieceInDirection(p, DIRECTION_UP_RIGHT);
    expect("[1:4,2]", p1);

    // isintargetEndZone
    m = new Move(0, 4);
    b.performValidMove(m, WHITE);
    print(b);
    Chain ch = new Chain(WHITE);
    ch.addPiece(b.board[0][2]);
    expect(true, b.pieceIsInTargetEndZone(b.board[7][2], ch));
    expect(false, b.pieceIsInTargetEndZone(b.board[0][4], ch));

    b = new Board();

    // Find Network:
    b.performValidMove(new Move(0, 3), WHITE);
    b.performValidMove(new Move(2, 3), WHITE);
    b.performValidMove(new Move(3, 2), WHITE);
    b.performValidMove(new Move(4, 3), WHITE);
    b.performValidMove(new Move(5, 3), WHITE);
    b.performValidMove(new Move(7, 5), WHITE);
    b.performValidMove(new Move(1, 3), BLACK);
    print(b);
    print("eval " + b.evaluate(0));
  }
}
