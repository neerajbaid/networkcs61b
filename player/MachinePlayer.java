/* MachinePlayer.java */

package player;

import list.*;
import java.util.Arrays; // DEBUGGING PURPOSES ONLY

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
  
  private DList validMovesHelper (int color, Piece stepPiece) {
    DList moves = new DList();
    for(int x = 0; x < board.LENGTH; x++){
      for(int y = 0; y < board.LENGTH; y++){
        Move move = new Move(x, y);
        if(board.isValidAddMove(move, color)){
          if (stepPiece != null) {
            // cannot step back onto where the piece originally was
            if (stepPiece.x == x && stepPiece.y == y) {
              continue;
            }
            move.moveKind = move.STEP;
            move.x2 = stepPiece.x;
            move.y2 = stepPiece.y;
          }
          moves.insertFront(move);
        }
      }
    }
    return moves;
  }

  public DList validMoves(int color){
    DList validMoves;
    DList myPieces = board.getPieces(color);
    // find add moves:
    if (board.hasPiecesLeft(color)) {
      validMoves = validMovesHelper(color, null);
    }
    else {
      validMoves = new DList();
    }

    // now find step moves:
    ListNode current = myPieces.front();
    while(current.isValidNode()) {
      Piece piece = (Piece) current.item();
      board.tempRemove(piece);
      DList newMoves = validMovesHelper(color, piece);
      validMoves.extend(newMoves);
      board.tempRestore(piece);
      current = current.next();
    }
    
    return validMoves;
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
    DList validMoves = validMoves(side);

    if (depth == searchDepth) {
      myBest = (Move) validMoves.front().item();
      ListNode current = validMoves.front();
      while(current.isValidNode()) {
        Move move = (Move) current.item();
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
        current = current.next();
      }
      return myBest;
    }

    // if ("this" Grid is full or has a win) {
      // return a Best with the Grid’s score, no move;
      // return new Move();
    // }

    ListNode current = validMoves.front();
    myBest = (Move) current.item();
    while(current.isValidNode()) {
      Move move = (Move) current.item();
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
      current = current.next();
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




  // ***** TESTING CODE ******
  // READERS DO NOT NEED TO READ THE CODE BELOW

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

    // Test validMoves
    DList validMoves = p.validMoves(Board.WHITE);
    expect(8*6, validMoves.length()); // 6*8 = 48 possible add moves on empty board
    print(validMoves);
    expect(8*6, o.validMoves(Board.BLACK).length());

    m = new Move(6,6);
    p.forceMove(m);

    // Test validMoves
    validMoves = p.validMoves(Board.WHITE);
    expect(2* (8*6 - 1) , validMoves.length()); // 6*8 - 1 = 47 possible add AND step moves.
    expect(8*6-1, p.validMoves(Board.BLACK).length()); // 47 possible add moves

    m = new Move(5,6);
    p.forceMove(m);

    // Test validMoves
    validMoves = p.validMoves(Board.WHITE);
    expect(6*8-8  +  46 * 2, validMoves.length()); // 6*8 - 8 = 40 possible add moves. 46 * 2 posssible step moves.
    print(validMoves);
    expect(8*6-2, p.validMoves(Board.BLACK).length()); // 46 possible add moves
    print(p.board);

    // Test chooseMove and operators
    p = new MachinePlayer(Board.WHITE, depth);
    o = new MachinePlayer(Board.BLACK, depth);

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
