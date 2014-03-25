networkcs61b
============

CS 61B Project #2.

Name of student running submit:
Login of student running submit:

Second team member's name:
Second team member's login:

Third team member's name (if any):
Third team member's login:

IMPORTANT:  Once you've submitted Project 2 once, the same team member should
submit always.  If a different teammate must submit, inform cs61b@cory.eecs of
all the details.  Include a complete list of team members, and let us know
which submission you want graded.

If you've submitted your project once, or even written a substantial amount of
code together, you may not change partners without the permission of the
instructor.
===============================================================================
Does your program compile without errors?


Have you tested your program on the 61B lab machines?


Did you successfully implement game tree search?  Did you successfully
implement alpha-beta pruning?  Are there any limitations on it?  What is the
default number of search levels set by the one-parameter MachinePlayer
constructor (or is it a variable-depth search)?


Describe your board evaluation function in some detail.


Does your MachinePlayer use any special method of choosing the first few moves?


Is there anything else the graders should know to help them read your project?



Describe the classes, modules, and interfaces you designed before and while you
implemented the project.  Your description should include:
  -  A list of the classes your program uses.
  -  A list of each of the "modules" used in or by MachinePlayer, similar to
     the list in the "Teamwork" section of the README (but hopefully more
     detailed).  (If you're using a list class, that should probably count as
     a separate module.)
  -  For each module, list the class(es) the module is implemented in.
  -  For each module, say which of your team members implemented it.
  -  For each module, describe its interface--specifically, the prototype and
     behavior of each method that is available for external callers (outside
     the module) to call.  Don't include methods that are only meant to be
     called from within the module.

     For each method, provide (1) a method prototype and (2) a complete,
     unambiguous description of the behavior of the method/module.  This
     description should also appear before the method in your code's comments.

You will probably need to change some of your design decisions as you go; be
sure to modify this file to reflect these changes before you submit your
project.  Your design of classes and interfaces will be worth 10% of your
grade.

Classes:
  Player
  MachinePlayer
  Move
  Board

Modules:

1)  Determining whether a move is valid
      implemented in Board
2)  Generate list of valid moves
      implemented in Player
3)  Adjacent chip finder that finds chips adjacent to a chip
      implemented in Board
4)  Finding networks
      implemented in Board
5)  Board evaluation
      implemented in Board
6)  Minimax tree search
      implemented in MachinePlayer

1. Valid Move
class Board
// Returns whether the move is legal in the current board
public boolean isValidMove(Move move);

2. List of valid moves
class Player
// Returns an array of valid Moves in the specified board
private Moves[] validMoves(Board board);

3. Adjacent chip finder
class Board
// Find line-of-sight adjacent chips to the specified x, y position chip in the board.
private int[][][] adjacentChips(int x, int y);

4. Finding networks
class Board
// Finds all/any valid networks
// Return value is an array of hashes, outlined below.
// [{'name' => 'b', 'moves' => '[[x,y],[x,y] ... ]'}, {'name' => 'b', 'moves' => '[[x,y],[x,y] ... ]'}, ... ]
public int[][] findNetworks();

5. Board evaluation
class Board
// Returns a score for the current board, and the specified player.
public int evaluate(int player);

6. Tree search
class MachinePlayer
// Returns a new move by "this" player.  Internally records the move (updates
// the internal game board) as a move by "this" player.
// The move takes advantage of alpha-beta pruning with a tree search and references the search depth.
public Move chooseMove(); 
