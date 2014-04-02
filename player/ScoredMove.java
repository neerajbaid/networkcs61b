package player;

  /**
    * Convenience class for representing the combination of an integer score
    * and a Move object.
    */
public class ScoredMove {
  public int score;
  public Move move;

  /**
    * Empty default constructor
    */
  public ScoredMove() {}

  /**
    * Convenience constructor for ScoreMove.
    * Constructs a ScoredMove that represents the combination of an integer score
    * and a Move object.
    */
  public ScoredMove(int score, Move move) {
    this.score = score;
    this.move = move;
  }

  /**
    * Returns string representation of ScoredMove (for testing)
    */
  public String toString() {
    return move.toString() + " -> " + score;
  }
}