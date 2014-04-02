package player;

import list.*;
import java.util.Arrays; // DEBUGGING PURPOSES ONLY


  /**
    * Class representing a board in the game. Used for all internal state management.
    */
public class Board {
  protected static final int WHITE = 1;
  protected static final int BLACK = 0;

  private static final int DIRECTION_NONE = -1;
  private static final int DIRECTION_UP = 0;
  private static final int DIRECTION_UP_RIGHT = 1;
  private static final int DIRECTION_RIGHT = 2;
  private static final int DIRECTION_DOWN_RIGHT = 3;
  private static final int DIRECTION_DOWN = 4;
  private static final int DIRECTION_DOWN_LEFT = 5;
  private static final int DIRECTION_LEFT = 6;
  private static final int DIRECTION_UP_LEFT = 7;
  private static final int[] DIRECTIONS = { DIRECTION_UP, DIRECTION_UP_RIGHT,
      DIRECTION_RIGHT, DIRECTION_DOWN_RIGHT, DIRECTION_DOWN,
      DIRECTION_DOWN_LEFT, DIRECTION_LEFT, DIRECTION_UP_LEFT };

  private static final int MY_WIN = Integer.MAX_VALUE, OPP_WIN = Integer.MIN_VALUE;

  //Board Limits
  protected static final int MAX_PIECES = 10;
  protected static final int LENGTH = 8;
  protected static final int END_INDEX = LENGTH-1;

  private Piece[][] board;
  private DList[] colorPieces; // keeps track of Each player's pieces

  /**
    * Default constructor, initialized empty board.
    */
  public Board() {
    colorPieces = new DList[] {new DList(), new DList()};
    board = new Piece[LENGTH][LENGTH];
  }

  // CHECKING VALID MOVE

  /**
    * Switches the player color.
    * @parameter color: int representing current player
    * @return other player
    */
  protected static int flipColor(int color) {
    return 1-color;
  }

  /**
    * Checks if x,y coordinates correspond to a valid goal for the player
    * @parameter x: x coord, y: y coord, color: int representing player
    * @return boolean
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
    * Checks if x,y coordinates correspond to the opposite player's goal
    * @parameter x: x coord, y: y coord, color: int representing player
    * @return boolean representing whether coordinates are on wrong goal
    */
  private boolean isOnInvalidGoal(int x, int y, int color) {
    return isOnValidGoal(x, y, flipColor(color));
  }

  /**
    * Checks if x,y coordinates are on a corner space, returns boolean
    */
  private boolean isInCorner(int x, int y) {
    if (x == 0 || y == 0 || x == END_INDEX || y == END_INDEX) {
      int difference = Math.abs(x - y);
      return (difference == 0 || difference == END_INDEX);
    }
    return false;
  }

  /**
    * Helper method for isInCluster. Takes in x, y coordinates and the player's color
    * Checks whether the player has pieces surrounding those x,y coordinates.
    * Returns true or false.
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
    * Checks for clusters that might be formed by placing a piece of int color
    * at the x, y coordinates
    * Returns boolean
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
    * Checks if a move by the player for color
    * would be a valid add move (even if move is a step move)
    * Returns boolean
    */
  public boolean isValidAddMove(Move move, int color) {
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

    // just for cluster detection, flip color if step move
    // this is a trick for accounting for the fact that cluster detection for step moves
    // should ignore the original (source) piece
    Piece stepPiece = null;
    if (move.moveKind == move.STEP) {
      stepPiece = board[move.x2][move.y2];
      stepPiece.color = flipColor(stepPiece.color);
    }
    boolean clustered = isInCluster(move.x1, move.y1, color);
    if (stepPiece != null) {
      stepPiece.color = flipColor(stepPiece.color);
    }
    return !clustered;
  }

  /**
    * Checks if a move for the player represented by color is valid
    * Returns boolean
    */
  protected boolean isValidMove(Move move, int color){
    if (move.moveKind == Move.ADD && !hasPiecesLeft(color)) {
      return false;
    }
    return isValidAddMove(move, color);
  }

  /**
    * Checks if the player represented by color has any more pieces they can add
    * Returns boolean
    */
  protected boolean hasPiecesLeft(int color) {
    return colorPieces[color].length() < MAX_PIECES;
  }

  /**
    * Returns a DList containing all of the pieces that a player has
    * Takes in the player's color.
    */
  protected DList getPieces(int color) {
    return colorPieces[color];
  }

  // MANIPULATING BOARD

  /**
    * Performs a move for a specific player represented by color
    * This method does not and should not check if move is valid.
    */
  protected void performValidMove(Move move, int color) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    Piece piece;
    if (move.moveKind == move.STEP) {
      piece = board[move.x2][move.y2];
      board[move.x2][move.y2] = null;
    }
    // ADD move:
    else {
      piece = new Piece(color, move.x1, move.y1);
      colorPieces[color].insertFront(piece);
    }
    board[move.x1][move.y1] = piece;
    piece.x = move.x1;
    piece.y = move.y1;
  }

  /**
    * Reverses a move.
    * Takes in the Move that is to be reversed.
    */
  protected void undoMove(Move move) {
    if (move.moveKind == move.QUIT) {
      return;
    }
    Piece piece = board[move.x1][move.y1];
    if (move.moveKind == move.STEP) {
      board[move.x2][move.y2] = piece;
      piece.x = move.x2;
      piece.y = move.y2;
    }
    // ADD move:
    else {
      colorPieces[piece.color].remove(piece);
    }
    board[move.x1][move.y1] = null;
  }

  /**
    * Used to remove a piece from the board.
    * tempRemove is named such to indicate that it is intended
    * to be used only for temporarily removing a piece that will be added back
    * later
    */
  void tempRemove (Piece piece) {
    board[piece.x][piece.y] = null;
  }
  /**
    * Used to set a piece on the board
    * tempRestore is named such to indicate that it is intended
    * to be used only for adding back a piece that was removed 
    * with tempRemove
    */
  void tempRestore (Piece piece) {
    board[piece.x][piece.y] = piece;
  }


  /**
    * Finds all the networks currently on the board of a certain player.
    * player is represented by color.
    * Returns a DList of all the networks for that player.
    */
  public DList findAllNetworks(int color)
  {
    DList beginningZonePieces = beginningZonePieces(color);
    DList networks = new DList();

    for (ListNode pieceNode : beginningZonePieces) {
      Piece piece = (Piece) pieceNode.item();
      Chain beginning = new Chain(color);
      beginning.addPiece(piece);
      findNetwork(piece, beginning, DIRECTION_NONE, networks);
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
    *                              in a line.
    * @parameter networks:       A DList of all the networks that have already been found.
    * Any completed networks will be added to the DList passed in the networks parameter.
    */
  private void findNetwork(Piece piece, Chain currentNetwork, int prevDirection, DList networks) {
    if (pieceIsInTargetEndZone(piece, currentNetwork)) {
      if (currentNetwork.numPieces() >= 6) {
        networks.insertBack(currentNetwork.copy());
      }
      return;
    }

    for (int direction : DIRECTIONS) {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (direction == prevDirection) {
        continue;
      }
      if (nextPiece == null) {
        continue;
      }
      if (currentNetwork.contains(nextPiece)) {
        continue;
      }
      if (nextPiece.color != currentNetwork.color) {
        continue;
      }
      Chain next = currentNetwork.copy();
      next.addPiece(nextPiece);
      findNetwork(nextPiece, next, direction, networks);
    }
  }

  /**
    * Checks if a piece is in its end zone.
    * Takes in a Piece piece and a Chain network
    * The network is used to store which player this is for, and to
    * check that the network doesn't end in the same endzone where it started.
    * Returns a boolean
    */
  private boolean pieceIsInTargetEndZone(Piece piece, Chain network) {
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
    * Finds the next Piece piece of the same color in specified direction.
    * Returns a Piece
    */
  private Piece findNextPieceInDirection(Piece piece, int direction) {
    int[] pieceCoordinate = new int[] {piece.x, piece.y};
    int[] coordinate = incrementCoordinateInDirection(pieceCoordinate, direction);
    while (containsCoordinate(coordinate)) {
      Piece next = pieceAtCoordinate(coordinate);
      if (next != null) {
        return next;
      }
      coordinate = incrementCoordinateInDirection(coordinate, direction);
    }
    return null;
  }

  /**
    *  Returns the next coordinate in direction.
    * Takes in an array of two ints, which represents an [x,y] coordinate pair
    * Takes in a direction.
    * Returns an array of two ints, which represents an [x,y] coordinate pair
    */
  private int[] incrementCoordinateInDirection(int[] coordinate, int direction) {
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


  /**
    *  Checks if coordinate is on the board.
    *  Coordinate is an int[] representing an [x,y] coordinate pair
    *  Returns a boolean
    */
  private boolean containsCoordinate(int[] coordinate) {
    int x = coordinate[0];
    int y = coordinate[1];
    return (x < LENGTH && x >= 0 && y < LENGTH && y >= 0);
  }

  /**
    *  Returns the Piece at an int[] coordinate that represents an [x,y] coordinate pair
    */
  private Piece pieceAtCoordinate(int[] coordinate) {
    int x = coordinate[0];
    int y = coordinate[1];
    return board[x][y];
  }

  /**
    *  Gets all the pieces in a player's beginning zone. One of the zones has been
    *   arbitrarily designated as beginning and the other zone as the end
    *   simply for the purposes of network finding
    *   since networks are direction agnostic.
    * Returns a DList of the pieces.
    */
  private DList beginningZonePieces(int color) {
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
    *    indicated beginning and one as end simply for the purposes of network finding
    *    since networks are direction agnostic.
    * Takes in the player's color.
    * Returns a DList of the pieces.
    */
  private DList endZonePieces(int color) {
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
    *  Helper function for evaluate that calculates an intermediate score for a board.
    * Takes in a player's color to determine who to score the board for.
    * Returns an integer ranging from Integer.MIN_VALUE to Integer.MAX_VALUE
    */
  private int intermediate (int player) {
    DList playerPieces = getPieces(player);
    DList opponentPieces = getPieces(flipColor(player));
    
    //calculate a score based on how many pieces each piece can see (doesn't matter if pieces double counted)
    int yourScore = 0;
    for (ListNode currentPiece : playerPieces) {
      yourScore += numPairsPieceCanForm((Piece)currentPiece.item());   
    }

    //do same for opponent
    int otherScore = 0;
    for (ListNode currentPiece : opponentPieces) {
      otherScore += numPairsPieceCanForm((Piece)currentPiece.item());
    }

    //difference between number of pieces each piece can see from my side and opponent's side
    return yourScore - otherScore;
  }

  /**
    * Returns an evaluation score for the current board.
    * Takes in a player's color and a boolean runIntermediate.
    * runIntermediate determines whether or not the functon tries to arrive at an intermediate score 
    * if no wins are found. 
    * Returns an integer ranging from Integer.MIN_VALUE to Integer.MAX_VALUE
    */
  public int evaluate(int player, boolean runIntermediate) {

    //find if any network reaches goal, if so then return my win
    DList networks = findAllNetworks(player);
    
    //look at all networks and determine whether pieces are at opposite goals
    for (ListNode current : networks) {
      Chain network = (Chain) current.item();

      DList pieces = network.getPieces();
      Piece front = (Piece) pieces.front().item();
      Piece back = (Piece) pieces.back().item();
      
      if (this.isOnValidGoal(front.x, front.y, player)
          && this.isOnValidGoal(back.x, back.y, player)) {
        return MY_WIN;
      }
    }

    player = flipColor(player);
    networks = findAllNetworks(player);
    int otherPlacedPieces = 0;

    //look at all networks and determine whether pieces are at opposite goals 
    for (ListNode current : networks) {
      Chain network = (Chain) current.item();
      DList pieces = network.getPieces();
      Piece front = (Piece) pieces.front().item();
      Piece back = (Piece) pieces.back().item();
      otherPlacedPieces += pieces.length();

      if (this.isOnValidGoal(front.x, front.y, player)
          && this.isOnValidGoal(back.x, back.y, player)) {
        return OPP_WIN;
      }
    }
    //if neither opponent has a sure win, then calculate an intermediate score and sum it with a score based on if I can add or set
    if (runIntermediate) {
      return intermediate(flipColor(player));
    }
    return 0;
  }

  /**
    * Returns the number of pairs a Piece can form with the pieces around it.
    */
  private int numPairsPieceCanForm(Piece piece) {
    int num = 0;
    for (int direction : DIRECTIONS) {
      Piece nextPiece = findNextPieceInDirection(piece, direction);
      if (nextPiece != null) {
        num++;
      }
    }
    return num;
  }



  // ***** TESTING CODE ******
  // READERS DO NOT NEED TO READ THE CODE BELOW

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
    expect(1, b.getPieces(BLACK).length());
    print(b);

    b.undoMove(m);
    print(b);

    m = new Move(7, 2);
    b.performValidMove(m, WHITE);
    expect(1, b.getPieces(WHITE).length());
    print(b);
    b.undoMove(m);
    expect(0, b.getPieces(WHITE).length());
    print(b);

    // isInCluster
    print("inCluster");
    b.performValidMove(m, WHITE);
    m = new Move(6, 3);
    b.performValidMove(m, WHITE);
    expect(2, b.getPieces(WHITE).length());
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
    print("eval " + b.evaluate(0, true));
  }
}
