/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  public static final int WHITE_COLOR = 0;
  public static final int BLACK_COLOR = 1;
  private Board board;
  private int color;
  private int searchDepth;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new Board();
    searchDepth = 3; // default
    this.color = color;
  }

  // Creates a machine player with the given color and search depth.  Color is   
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    board = new Board();
    this.searchDepth searchDepth;
    this.color = color;
  }
  
  public Moves[] validMoves(Board board){
	  SList moves = new SList();
	  
	  for(int i = 0; i < board.getWidth(); i++){
		  for(int j = 0; j < board.getHeight(); j++){   
			  Move move = new Move(i, j);
			  if(board.isValidMove(move)){
				  moves.push(move);
			  }
		  }
	  }
	  
	  Moves[] arr = new Moves[moves.length()];   
	  SListNode current = moves.front();
	  for(int i = 0; i < arr.length; i++){
		  arr[i] = current.item();
		  current = current.next();
	  }
	  
	  return arr;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {

    Moves[] validMoves = this.validMoves(board);
    return new Move();
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    return false;
  }

  private void undoMove(Move move) {
    
  }


  public Move chooseMove(boolean side, int alpha, int beta) {
    Move myBest, replyBest;
    int myBestScore, replyBestScore;
    if ("this" Grid is full or has a win) {
      // return a Best with the Grid’s score, no move;
      return new Move();
    }
    if (side == WHITE_COLOR) { //WHITE_COLOR = COMPUTER
      myBestScore = alpha;
    } else {
      myBestScore = beta;
    }
    for (each legal move m) {
      perform move m; // Modifies "this" Grid
      replyBest = chooseMove(!side, alpha, beta);
      undo move m; // Restores "this" Grid
      if (side == WHITE_COLOR && replyBestScore > myBestScore) {
        myBest = m;
        myBestScore = replyBestScore;
        alpha = replyBestScore;
      } else if (side == BLACK_COLOR && replyBestScore < myBestScore) {
        myBest = m;
        myBestScore = replyBestScore;
        beta = replyBestScore;
      }
      if (alpha >= beta) {
        return myBest;
      }
    }
    return myBest;
  }


}
