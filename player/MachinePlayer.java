/* MachinePlayer.java */

package player;

import list.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  private Board board;
  private int color;
  private int searchDepth;
  private static final int DEFAULT_DEPTH = 2;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new Board();
    searchDepth = DEFAULT_DEPTH;
    this.color = color;
  }

  // Creates a machine player with the given color and search depth.  Color is   
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    board = new Board();
    this.searchDepth = searchDepth;
    this.color = color;
  }
  
  private Move[] validMoves(int color){
    DList addMoves = new DList();

    for(int i = 0; i < board.LENGTH; i++){
      for(int j = 0; j < board.LENGTH; j++){
        Move move = new Move(i, j);
        if(board.isValidMove(move, color)){
          addMoves.insertFront(move);
        }
      }
    }
    DList validMoves;
    DList myPieces = board.getPieces(color);
    int size = 0;
    if (board.hasPiecesLeft(color)) {
      validMoves = addMoves;
      size = addMoves.length();
    }
    else {
      validMoves = new DList();
    }
    size += size * myPieces.length();

    Move[] arr = new Move[size];
    int i = 0;
    ListNode current = validMoves.front();
    while(current.isValidNode()) {
      arr[i] = (Move) current.item();
      i++;
      current = current.next();
    }
    current = myPieces.front();
    while (current.isValidNode()) {
      Piece piece = (Piece) current.item();
      ListNode nested = addMoves.front();
      while(nested.isValidNode()) {
        Move addMove = (Move) nested.item();
        arr[i] = new Move(addMove.x1, addMove.y1, piece.x, piece.y);
        i++;
        nested = nested.next();
      }
      current = current.next();
    }
    
    return arr;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    Move m = chooseMoveHelper(color, -Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
    board.performValidMove(m, color);
    return m;
  }

  private Move chooseMoveHelper(int side, int alpha, int beta, int depth) {
    Move myBest, replyBest;
    int myBestScore, replyBestScore;
    if (side == color) {
      myBestScore = alpha;
      replyBestScore = beta;
    } else {
      myBestScore = beta;
      replyBestScore = alpha;
    }
    Move[] validMoves = validMoves(side);

    if (depth == searchDepth) {
      myBest = validMoves[0];
      for (Move move : validMoves) {
        board.performValidMove(move, side);
        int score = board.evaluate(side);
        board.undoMove(move);
        if (color == Board.WHITE && score > myBestScore) {
          myBestScore = score;
          myBest = move;
        }
        if (color == Board.BLACK && score < myBestScore) {
          myBestScore = score;
          myBest = move;
        }
      }
      return myBest;
    }

    // if ("this" Grid is full or has a win) {
      // return a Best with the Grid’s score, no move;
      // return new Move();
    // }

    myBest = validMoves[0];
    for (Move move : validMoves) {
      board.performValidMove(move, side);
      replyBest = chooseMoveHelper(Board.flipColor(side), alpha, beta, depth+1);
      board.undoMove(move);
      if (side == Board.WHITE && replyBestScore > myBestScore) {
        myBest = move;
        myBestScore = replyBestScore;
        alpha = replyBestScore;
      } else if (side == Board.BLACK && replyBestScore < myBestScore) {
        myBest = move;
        myBestScore = replyBestScore;
        beta = replyBestScore;
      }
      if (alpha >= beta) {
        return myBest;
      }
    }
    return myBest;
  }

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    if (!board.isValidMove(m, Board.flipColor(color))) {
      return false;
    }
    board.performValidMove(m, Board.flipColor(color));
    return true;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if (!board.isValidMove(m, color)) {
      return false;
    }
    board.performValidMove(m, color);
    return true;
  }

  private static void expect(Object expect, Object o) {
    System.out.println("Expect " + expect + ": " + o);
  }

  private static void print(Object o) {
    System.out.println(o);
  }
  public static void main(String[] args) {
    Move m;
    int depth = 1;
    MachinePlayer p = new MachinePlayer(Board.WHITE, depth);
    MachinePlayer o = new MachinePlayer(Board.BLACK, depth);

    m = p.chooseMove();
    print("me: " + m);
    expect(false, p.forceMove(m));
    expect(true, o.opponentMove(m));

    m = o.chooseMove();
    print("opponent: " + m);
    expect(true, p.opponentMove(m));
    expect(false, o.forceMove(m));

    m = p.chooseMove();
    print("me: " + m);
    expect(false, p.forceMove(m));
    expect(true, o.opponentMove(m));

    m = o.chooseMove();
    print("opponent: " + m);
    expect(true, p.opponentMove(m));
    print(p.board);

    m = p.chooseMove();
    print("me: " + m);
    expect(true, o.opponentMove(m));

    m = o.chooseMove();
    print("opponent: " + m);
    expect(true, p.opponentMove(m));

    m = p.chooseMove();
    print("me: " + m);
    expect(true, o.opponentMove(m));

    m = o.chooseMove();
    print("opponent: " + m);
    expect(true, p.opponentMove(m));
    print(p.board);
  }

}
