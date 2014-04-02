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
  private int oppColor;
  private int searchDepth;
  private int variableSearchDepth;
  private static final int DEFAULT_DEPTH = 5;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new Board();
    searchDepth = DEFAULT_DEPTH;
    this.variableSearchDepth = searchDepth;
    this.color = color;
    this.oppColor = Board.flipColor(color);
  }

  // Creates a machine player with the given color and search depth.  Color is   
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    board = new Board();
    this.searchDepth = searchDepth;
    variableSearchDepth = searchDepth;
    this.color = color;
    this.oppColor = Board.flipColor(color);
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
    // find add moves:
    if (board.hasPiecesLeft(color)) {
      return validMovesHelper(color, null);
    }

    // now find step moves:
    DList myPieces = board.getPieces(color);
    DList validMoves = new DList();
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
    // lower the depth for step pieces
    if (!board.hasPiecesLeft(color)) {
      variableSearchDepth = searchDepth - 3;
    }
    ScoredMove scoredMove = chooseMoveHelper(color, -Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
    Move move = scoredMove.move;
    board.performValidMove(move, color);
    return move;
  }

  private ScoredMove chooseMoveHelper(int side, int alpha, int beta, int depth) {
    DList validMoves = validMoves(side);

    if (depth >= variableSearchDepth) {
      // System.out.println("depth hit");
      int max = 0;
      Move best = null;
      for (ListNode node : validMoves) {
        Move move = (Move) node.item();
        board.performValidMove(move, side);
        int score = board.evaluate(color);
        board.undoMove(move);
        if (best == null) {
          best = move;
          max = score;
        }
        if (side == color && score > max) {
          max = score;
          best = move;
        }
        else if (side == oppColor && score < max) {
          max = score;
          best = move;
        }
      }
      return new ScoredMove(max, best);
    }

    ScoredMove replyBest;
    ScoredMove myBest = new ScoredMove();
    if (side == color) {
      myBest.score = alpha;
    } else {
      myBest.score = beta;
    }
    myBest.move = (Move) validMoves.front().item();
    for (ListNode node : validMoves) {
      // System.out.println("hi");
      Move move = (Move) node.item();
      board.performValidMove(move, side);
      replyBest = chooseMoveHelper(Board.flipColor(side), alpha, beta, depth + 1);
      board.undoMove(move);
      if (side == color && replyBest.score > myBest.score) {
        myBest.move = move;
        myBest.score = replyBest.score;
        alpha = replyBest.score;
      } else if (side == oppColor && replyBest.score < myBest.score) {
        myBest.move = move;
        myBest.score = replyBest.score;
        beta = replyBest.score;
      }
      if (alpha >= beta) {
        // System.out.println("alpha");
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
    if (!board.isValidMove(m, oppColor)) {
      return false;
    }
    board.performValidMove(m, oppColor);
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
    int depth = DEFAULT_DEPTH;
    MachinePlayer p = new MachinePlayer(Board.WHITE, depth);
    MachinePlayer o = new MachinePlayer(Board.BLACK, depth);

    // Test validMoves
    // print(p.board);
    DList validMoves = p.validMoves(Board.WHITE);
    expect(8*6, validMoves.length()); // 6*8 = 48 possible add moves on empty board
    print(validMoves);
    expect(8*6, o.validMoves(Board.BLACK).length());

    m = new Move(6,6);
    p.forceMove(m);

    // Test validMoves
    print(p.board);
    validMoves = p.validMoves(Board.WHITE);
    expect(8*6-1, validMoves.length()); // 47 add moves
    expect(8*6-1, p.validMoves(Board.BLACK).length()); // 47 possible add moves

    p.forceMove(new Move(7,6));
    p.forceMove(new Move(7,4));
    p.forceMove(new Move(6,4));
    p.forceMove(new Move(7,2));
    p.forceMove(new Move(6,2));
    p.forceMove(new Move(4,6));
    p.forceMove(new Move(3,6));
    p.forceMove(new Move(4,4));
    p.forceMove(new Move(3,4));

    // Now only step moves are allowed
    print(p.board);
    validMoves = p.validMoves(Board.WHITE);
    expect(198, validMoves.length());
    print(validMoves);
    expect(41, p.validMoves(Board.BLACK).length()); // 41 possible add moves
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
