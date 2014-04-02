package player;

public class ScoredMove {
  public int score;
  public Move move;

  public ScoredMove() {
  }

  public ScoredMove(int score, Move move) {
    this.score = score;
    this.move = move;
  }
}