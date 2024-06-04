TicTacToe by David Okafo.

Simple Tic-Tac-Toe game with customizable gameplay options.
(1) Option for 2-player mode with 2 real people on the same device, each player taking turns
(2) Option for 1-player mode versus CPU
(3) Option to simulate a match between two CPU/AI

- CPU has easy, medium, and hard difficulty modes
  - easy mode simply generates random numbers
  - hard mode uses effective heuristics for shorter board sizes (currently more effective than 'hard' mode)
  - difficult mode uses a variation for the minimax algorithm that also attempts to incorporate alpha-beta pruning (needs serious optimization and doesn't work fully as intended)
  

- This game constantly prompts the user for its move and gives feedback on success, as well as displaying the current board state for each turn.
- Provides the option to restart or quit the game at the end


Notes:
- Work on difficult mode (minimax algorithm)
- Work on implementing GUI for a visual representation of the game
- Separate game logic, user input prompts, and other components to refactor program
