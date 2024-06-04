import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;

public final class TicTacToe {

    private char p1;
    private char p2;

    Board getBoard() {
        return board;
    }

    private Board board;
//    private GameMode mode;


    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.Game();
    }

    void Game() {
        Scanner scanner = new Scanner(System.in);

        String welcomeMessage = "\nWelcome to TicTacToe! Choose whether you want to play 2-player, "
                + "against a CPU (1-player), or simulate an automated match.";
        for (int i = 0; i < welcomeMessage.length(); i++) { System.out.print("-"); }
        System.out.println(welcomeMessage);
        for (int i = 0; i < welcomeMessage.length(); i++) { System.out.print("-"); }
        System.out.println("\nType '1' for 1-player, '2' for 2-player, and '0' for simulation.");

        int userInput = 0;
        boolean validInput = false;
        while (!validInput) {
            if (scanner.hasNextInt()) {
                userInput = scanner.nextInt();
                if (userInput == 0 || userInput == 1|| userInput == 2) {
                    validInput = true;
                } else {
                    System.err.println("Invalid input. Please type 1 for 1-player or 2 for 2-player or 0 for simulation.");
                }
            } else {
                System.err.println("Invalid input. Please type a valid integer");
                scanner.nextLine();
            }
        }

        if (userInput == 1) {
            System.out.println("This is a one player match between you and the computer.");
            System.out.println("Please choose your game marker: X or O");
            SetGameMarkers();
            System.out.println("You: " + p1 + System.lineSeparator() + "CPU: " + p2);
        } else if (userInput == 2) { // userInput == 2
            System.out.println("This is a two player game, so please take turns accordingly.");
            System.out.println("Please specify Player 1's marker: Type 'X' or 'O'");
            SetGameMarkers();
            System.out.println("Player 1: " + p1 + System.lineSeparator() + "Player 2: " + p2);
        } else { // userInput == 0;
            System.out.println("This is a simulated match.");
            System.out.println("Player 1: X\nPlayer 2: O");
            p1 = 'X';
            p2 = 'O';
        }
        SetupBoard();
        StartGame(userInput == 1, userInput == 0, scanner);

        if (EndGame(scanner)) {
            Game();
        } else {
            System.out.println("Thanks for playing!");
        }
    }

    /** Sets up the mechanics of the game board.
     * NOTE: CPU/AI takes way too long for larger inputs -- reasonable computational limit is 5x5 board
     */
    private void SetupBoard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose your board size (e.g '3' for 3x3 board)");
        int n = 3; // default, 3x3 board
        boolean valid1 = false;
        int loopCtrl = 0;
        while (!valid1 && ++loopCtrl < Board.kMaxIterations) {
            if (scanner.hasNextInt()) {
                n = scanner.nextInt();
                if (n > 1 && n <= 10) {
                    valid1 = true;
                    if (n > 5) { System.err.println("WARNING: You chose a large game board. Game may not run properly if you're playing with AI."); }
                 } else {
                    System.err.println("Board size must be a positive integer greater than 1 but no more than 10.");
                }
            } else {
                System.err.println("Invalid input. Please type a valid integer");
            }
            scanner.nextLine();
        }
        loopCtrl = 0;
        System.out.println("How many marks in a row to win? Must be no greater than the size of the board (k <= n)");
        int k = n; // default streak = board size
        boolean valid2 = false;
        while (!valid2 && ++loopCtrl < Board.kMaxIterations) {
            if (scanner.hasNextInt()) {
                k = scanner.nextInt();
                if (k > n){
                    System.err.println("Streak must be less than or equal to the board size. Please retype!");
                } else if (k <= 0) {
                    System.err.println("Streak must be a positive integer. Please retype!");
                } else if (k == 1) {
                    System.err.println("... yeah no. Retype. ");
                } else { valid2 = true; }
            } else {
                System.err.println("Invalid input. Please type a valid integer");
                scanner.next();
            }
        }

        System.out.println("You are playing a " + n + "x" + n + " game. " +
                "Players must mark " + k + " spaces in a row to win. Enjoy!");
        board = new Board(p1, p2, n, k);
    }

    private void SetGameMarkers() {
        Scanner input = new Scanner(System.in);
        boolean valid = false;
        while (!valid) {
            String s = input.next();
            if (s.length() == 1) {
                p1 = Character.toUpperCase(s.charAt(0)); // extract single character from user input

                if (p1 == 'X' || p1 == 'O') {
                    valid = true;
                } else {
                    System.err.println("Invalid game marker. Please retype 'X' or 'O'");
                }
            } else {
                System.err.println("Input length is not valid. Please type a single character 'X' or 'O'");
            }
        }
        p2 = (p1 == 'O') ? 'X' : 'O';
    }
    private void StartGame(boolean CPU, boolean auto, Scanner input) {
        if (auto) {
            board.simulate(CPUDiff(input));
        } else if (CPU) {
            board.startCPU(CPUDiff(input));
        } else {
            board.start();
        }
    }
    private CPUDifficulty CPUDiff(Scanner input) {
        System.out.println("Select CPU difficulty - easy (E), medium (M), or hard (H):");
        boolean valid = false;
        CPUDifficulty difficulty = null;
        while (!valid) {
            var capture = input.next();
            if (capture.length() == 1) {
                char ans = Character.toUpperCase(capture.charAt(0));
                if (ans == 'E' || ans == 'M' || ans == 'H') {
                    difficulty = switch (ans) { // enhanced switch
                        case 'E' -> CPUDifficulty.EASY;
                        case 'M' -> CPUDifficulty.MEDIUM;
                        case 'H' -> CPUDifficulty.HARD;
                        default -> throw new IllegalStateException("Unexpected value: " + ans);
                    };
                    valid = true;
                }
            } else {
                System.err.println("Invalid input. Please type 'E' (easy) or 'H' (hard):");
            }
        }
        return difficulty;
    }

    private boolean EndGame(Scanner input) {
        System.out.println("Would you like to play again? Press (R)estart or (Q)uit");

        char response;
        while (true) {
            var capture = input.next();
            if (capture.length() == 1) {
                response = Character.toUpperCase(capture.charAt(0));
                if (response == 'R') {
                    return true;
                } else if (response == 'Q') {
                    return false;
                }
            }
            System.err.println("Invalid response. Type R to restart or Q to quit.");
            input.nextLine();
        }
    }
}


class Board {
    public static final int kMaxIterations = 10000;
    private final char[][] board; // players: 'X' and 'O'
    private int numMoves; // current number of successful moves made in-game

    private String suffix = "";
    private final int maxMoves; // max number of possible moves on the game board
    private boolean gameOver; // draw or winner
    private final int boardSize;
    private  final int streak; // consecutive game marks required to win
    private final int MAX_DEPTH; // maximum depth for minimax algorithm
    private int numRecursions; // debugging purposes, tracks # of recursive calls to minimax
    private final char p1;
    private final char p2;
    private char winner;
    private final HashMap<String, Integer> memo; // memoize/save moves to optimize minimax algorithm

    private static final int WIN_SCORE = 10; // score for evaluation function
    Board(char player1, char player2, int size, int streak) {
        board = new char[size][size];
        for (char[] row : board) {
            Arrays.fill(row, '\0');
        }
        boardSize = size;
        this.streak = streak;
        numMoves = 0;
        maxMoves = boardSize * boardSize;
        gameOver = false;
        MAX_DEPTH = calculateMaxDepth(boardSize, streak);
        p1 = player1;
        p2 = player2;
        winner = 0; // default to draw
        memo = new HashMap<>();
    }

    public void start() {
        Scanner input = new Scanner(System.in);
        boolean flag = true; // true == P1's turn
        while (numMoves < maxMoves && !gameOver) { // n x n board spaces, game control variable
            char currentPlayer = flag ? p1 : p2;
            String player = flag ? "P1" : "P2";
            display(); // display current board before each move

            System.out.println(player + ", it's your turn. Place your \"" + currentPlayer + "\" into a row and column seperated by a space (e.g., 1 1):");
            try {
                int row = input.nextInt();
                int col = input.nextInt();

                if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) { // valid range
                    if (makeMove(row, col, currentPlayer)) {
                        flag = !flag; // only switch player if move was successful
                        numMoves++;
                    }
                } else {
                    System.err.println("Invalid move. Please enter a valid row and column space between 0-2");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. You must enter two consecutive integers between 0-2");
                input.nextLine();
            }
            updateSuffix();
            System.out.println(numMoves + suffix + " turn.");
            gameOver = checkGameStatus(currentPlayer);
        }
        display(); // display the final board after the match is over
        if (winner == 0) {
            System.out.println("Match ended in a draw.");
        } else if (winner == p1) {
            System.out.println("Winner of the match is Player 1. Congrats!");
        } else if (winner == p2) {
            System.out.println("Winner of the match is Player 2. Congrats!");
        }
        // END OF GAME
    }

    /**
     * @precondition - will not be called if game is already over
     * @param row - nonnegative integer 0-2
     * @param col - nonnegative integer 0-2
     * @param player - character 'X' or 'O' only
     * @return whether the requested move was successful
     */
    private boolean makeMove(int row, int col, char player) {
        if (board[row][col] == 'X' || board[row][col] == 'O') {
            System.err.println("Space (" + row + "," + col + ") is already occupied.");
            return false; // unsuccessful move
        }
        board[row][col] = player;
        System.out.println("Your move: (" + row + "," + col + ")");
        return true; // successful move
    }

    private boolean checkGameStatus(char currentPlayer) {
        if (checkWinner(currentPlayer)) {
            winner = currentPlayer;
            return true; // Game over due to win
        } else {
            return isBoardFull(); // Game over due to draw, or game continues
        }
    }
    private boolean isBoardFull() {
        // Check if all cells on the board are filled
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == '\0') {
                    return false; // Board is not full
                }
            }
        }
//        System.out.println("Board is full.");
        return true; // Board is full
    }

    private boolean checkWinner(char currentPlayer) {
        return HorizontalWin(currentPlayer) || VerticalWin(currentPlayer) || DiagonalWin(currentPlayer);
    }
    private boolean DiagonalWin(char currentPlayer) {
        int mainCount = 0; // Check main diagonal
        for (int i = 0; i < boardSize; i++) {
            if (board[i][i] == currentPlayer) {
                mainCount++;
                if (mainCount >= streak) {
//                    System.out.println("Win detected across main diagonal");
                    return true; // detect win when streak is reached
                }
            } else {
                mainCount = 0; // reset counter of streak is broken
            }
        }
        int antiCount = 0; // Check anti diagonal
        for (int i = 0; i < boardSize; i++) {
            if (board[i][boardSize - i - 1] == currentPlayer) {
                antiCount++;
                if (antiCount >= streak) {
//                    System.out.println("Win detected across anti-diagonal");
                    return true; // detect win when streak is reached
                }
            } else {
                antiCount = 0; // reset counter of streak is broken
            }
        }

        // Check other diagonals (not corner to corner) for shorter streaks later
        return false; // no diagonal win detected
    }

    private boolean VerticalWin(char currentPlayer) {
        for (int col = 0; col < boardSize; col++) {
            int counter = 0;
            for (int k = 0; k < boardSize; k++) {
                if (board[k][col] == currentPlayer) {
                    counter++;
                    if (counter >= streak) {
//                        System.out.println("Vertical Win detected across column " + col);
                        return true;
                    }
                } else {
                    counter = 0; // reset counter when streak is broken
                }
            }
        }

        return false;
    }

    private boolean HorizontalWin(char currentPlayer) {
        for (int row = 0; row < boardSize; row++) {
            int counter = 0;
            for (int k = 0; k < boardSize; k++) {
                if (board[row][k] == currentPlayer) {
                    counter++;
                    if (counter >= streak) {
//                        System.out.println("Horizontal Win detected across row " + row);
                        return true;
                    }
                } else {
                    counter = 0; // reset counter when streak is broken
                }
            }
        }

        return false;
    }

    public void display() {
        for (int n = 0; n < boardSize; n++) { System.out.print("----"); } // Top border
        System.out.println("-"); // additional borders
        for (int i = 0; i < board.length; i++) {
            System.out.print("| "); // Left border for each row
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] == '\0' ? " " : board[i][j]);
                System.out.print(" | ");
            }
            System.out.println(); // move to next line after each row
            for (int k = 0; k < boardSize; k++) { System.out.print("----"); } // Row separator
            System.out.println("-"); // additional borders
        }
    }

    private boolean isValidMove(int x, int y) {
        return (x >= 0 && x < boardSize && y >= 0 && y < boardSize && board[x][y] == '\0');
    }

    private boolean makePlayerMove(char currentPlayer, Scanner input) {
        // Player's Turn
        System.out.println("It's your turn. Place your \"" + currentPlayer + "\" into a row and column seperated by a space (e.g., 1 1):");
        try {
            int row = input.nextInt();
            int col = input.nextInt();

            if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) { // valid range
                if (makeMove(row, col, currentPlayer)) {
                    return true;
                }
            } else {
                System.err.println("Invalid move. Please enter a valid row and column space between 0-2");
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. You must enter two consecutive integers between 0-2");
            input.nextLine();
        }
        return false;
    }

    public void startCPU(CPUDifficulty difficulty) {
        Scanner input = new Scanner(System.in);
        boolean flag = true; // true == P1's turn
        while (numMoves < maxMoves && !gameOver) { // n x n board spaces, game control variable
            char currentPlayer = flag ? p1 : p2;
            display(); // display current board before each move

            // Player's Turn
            if (flag) {
                flag = !makePlayerMove(currentPlayer, input);
                if (!flag) {
                    numMoves++;
                    updateSuffix();
                    System.out.println(numMoves + suffix + " move:");
                }
            } else {
                makeCPUMove(difficulty, currentPlayer);
                flag = true;
                numMoves++;
                updateSuffix();
                System.out.println(numMoves + suffix + " move:");
            }
            gameOver = checkGameStatus(currentPlayer);
        }
        if (difficulty == CPUDifficulty.HARD) {
            System.out.println(numRecursions + " total recursive calls to minimax");
        }


        display(); // display the final board after the match is over
        if (winner == 0) {
            System.out.println("Match ended in a draw.");
        } else if (winner == p1) {
            System.out.println("You win! Congrats!");
        } else if (winner == p2) {
            System.out.println("You lost. Better luck next time!");
        }
        // END OF GAME
    }

    void makeCPUMove(CPUDifficulty diff, char currentPlayer) {
        Move bestMove = null;
        int iter = 0;
        while (bestMove == null && ++iter < kMaxIterations) {
            bestMove = switch (diff) {
                case CPUDifficulty.EASY -> // dumb (random) CPU move
                        getRandomMove();
                case CPUDifficulty.MEDIUM -> // priority-based CPU move
                        makePriorityMove(currentPlayer);
                case CPUDifficulty.HARD -> // smart (minimax strategy) CPU move
                        smartMove(currentPlayer);
            };
        }
        assert bestMove != null;
        int x = bestMove.row;
        int y = bestMove.col;
        assert x >= 0 && y >= 0 : "Error with CPU move. Should not trigger this assertion";
        board[x][y] = currentPlayer;
        System.out.println("It is the CPU's turn (" + currentPlayer + "). It's move: (" + x + ", " + y + ")");
    }

    private Move getRandomMove() {
        int x;
        int y;
        do {
            Random random = new Random();
            x = random.nextInt(boardSize);
            y = random.nextInt(boardSize);
        } while (!isValidMove(x,y));
        return new Move(x,y);
    }

    public void simulate(CPUDifficulty diff) {
        boolean turn = false;
        while (numMoves < maxMoves && !gameOver) {
            char current = turn ? p1 : p2;
            display();
            makeCPUMove(diff, current);
            numMoves++;
            updateSuffix();
            turn = !turn;
            gameOver = checkGameStatus(current);
            System.out.println(numMoves + suffix + " move:");
        }
        display();
        if (winner == 0) {
            System.out.println("Match ended in a draw.");
        } else {
            System.out.println("CPU " + (winner == p1 ? "1" : "2")
                    + " (marker " + winner + ") wins in " + numMoves + " rounds!");
        }
        if (diff == CPUDifficulty.HARD) {
            System.out.println(numRecursions + " total recursive calls to minimax");
        }
        // END OF GAME

    }

    /**
     * Evaluates the current board state from the perspective of the given player.
     *
     * @param player The current player used for evaluation. Must be either 'X' or 'O'.
     * @return +WIN_SCORE if the specified player has won, -WIN_SCORE if the opponent has won, or 0 if there is no winner yet.
     * This method checks for a win condition for both the current player and the opponent.
     * It returns a positive score if the current player is winning, a negative score if the opponent is winning,
     * and zero if there is no winner detected.
     */
    private int evaluate(char player) {
        char otherPlayer = player == p1 ? p2 : p1;
        // Check rows for X/O victory
        if (checkWinner(player)) {
            return WIN_SCORE;
        } else if (checkWinner(otherPlayer)) {
            return -WIN_SCORE;
        } else {
            return 0;
        }
    }
    /**
     * Minimax algorithm with alpha-beta pruning.
     *
     * @param depth              The current depth in the search tree.
     * @param isMaximizingPlayer Indicates whether the current player is maximizing or minimizing.
     * @param player             The current player.
     * @param alpha              best already explored option along path to the root for the maximizer
     * @param beta               best already explorer option along path to the root for the minimizer
     * @return                   The best score achievable by the current player at the given depth.
     */
    private int minimax(int depth, boolean isMaximizingPlayer, char player, int alpha, int beta) {
        numRecursions++;
        // (1) consider cached results (2) then terminal states (3) finally heuristic results
        // Generate a unique key for the current state to use in memoization
        String key = generateKey();
        if (memo.containsKey(key)) {    // Check if the current state is already memoized
            return memo.get(key);
        } // Note: memoization check comes first to skip unnecessary computation

        // Check if the current board state represents a win or loss for the player
        int score = evaluate(player);
        if (score == WIN_SCORE) {
            return score - depth; // adjust by depth to prioritize faster wins
        } else if (score == -WIN_SCORE) {
            return score + depth; // adjust by depth to prioritize slower losses
        }

        // If the maximum depth is reached or the game is over, evaluate the state
        if (depth >= MAX_DEPTH || gameOver) {
            int eval = evaluate(player); // Evaluate the state using a heuristic function
            memo.put(key, eval); // Memoize the evaluated value
            return eval; // Return the evaluated value
        }

        if (isBoardFull()) { // if current board state is terminating state
            return 0; // return value of board (it is a tie)?
        }

        int best;
        if (isMaximizingPlayer) {
            best = Integer.MIN_VALUE;
            // for each move in board
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (isValidMove(i, j)) {
                        board[i][j] = player;
                        // call minmax recursively (until base case is hit) and choose the maximum value
                        best = Math.max(best, minimax(depth + 1, false, player, alpha, beta));
                        board[i][j] = '\0'; // undo the move
                        alpha = Math.max(alpha, best);
                        if (alpha > beta) { // prune this branch
                            return best; // maximizer will never choose to enter this branch since it has a better option
                        }
                    }
                }
            }
        } else { // Minimizing Player
            best = Integer.MAX_VALUE;
            // for each move in board
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (isValidMove(i, j)) {
                        board[i][j] = player;
                        // call minmax recursively (until base case is hit) and choose the maximum value
                        best = Math.min(best, minimax(depth + 1, true, player, alpha, beta));
                        board[i][j] = '\0'; // undo the move
                        beta = Math.min(beta, best);
                        if (beta <= alpha) { // prune this branch
                            return best; // minimizer will never choose this path since it has a better option
                        }
                    }
                }
            }
        }
        return best;
    }

    private Move smartMove(char player) {
        // Minimax algorithm - NOTE: not very smart right now, the medium difficulty mode is actually smarter, fix l8r

        int bestVal = Integer.MIN_VALUE;
        Move bestMove = new Move();
        bestMove.row = -1;
        bestMove.col = -1;

        // for each move in board
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == '\0') { // if empty space
                    board[i][j] = player; // test moving current player to this space
                    // call minimax with alpha beta pruning (alpha = -∞, beta = +∞)
                    int moveVal = minimax(0, false, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j] = '\0'; // reset board space
                    if (moveVal > bestVal) { // if current move is better than bestMove
                        bestMove.row = i;
                        bestMove.col = j;
                        bestVal = moveVal; // update
                    }
                }

            }
        }
//        System.out.printf("The value of the best Move " +
//                "is : %d\n\n", bestVal);

        return bestMove;
    }

    /** Calculates maximum depth to limit recursive call overhead
     * This method is very arbitrary, as the values used are not based on any formula yet.
     * @return maximum depth level allowed to travel given the board size and streak
     */
    private int calculateMaxDepth(int boardSize, int streak) {
        int calc = switch (boardSize) {
            case 2 -> 4;
            case 3 -> 9;
            case 4 -> 7; // Adjust as needed
            case 5 -> 5; // Adjust as needed
            default -> 4; // Larger boards
        };
        return (calc / streak * 2); // arbitrary, fix later
    }

    private String generateKey() { // for memoization process
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == '\0') {
                    key.append('.'); // Use '.' to represent empty cells
                } else {
                    key.append(board[i][j]);
                }
            }
        }
        return key.toString();
    }

    private void updateSuffix() {
        if (numMoves == 11 || numMoves == 12 || numMoves == 13) {
            suffix = "th";
            return;
        }
        switch (numMoves % 10) {
            case 1:
                suffix = "st";
                break;
            case 2:
                suffix = "nd";
                break;
            case 3:
                suffix = "rd";
                break;
            default:
                suffix = "th";
                break;
        }
    }

    /**
     * Priorities for any position are:
     * Check for a winning move and make it
     * Check for opponent win on next turn and block it
     * Take the center
     * Take an open corner
     * Take an open side
     * @param player current player
     * @return Move object for optimal CPU move
     */
    private Move makePriorityMove(char player) {
        Move move;
        if ((move = makeWinningMove(player)) != null) { // check for winning move
            return move;
        }
        if ((move = blockOpponentWin(player)) != null) { // block opp winning move
            return move;
        }
        if (boardSize % 2 != 0) { // center move
            int center = boardSize / 2;
            if (isValidMove(center, center)) {
                return new Move(center, center);
            }
        }
        // Make corner move
        if ((move = moveCorners()) != null) {
            return move;
        }

        // All else fails, make random move
        return getRandomMove();
    }

    private Move makeWinningMove(char player) {
        return winningMove(player);
    }

    @Nullable
    private Move winningMove(char player) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == '\0') {
                    board[row][col] = player;
                    if (checkWinner(player)) {
                        return new Move(row, col); // return winning move
                    } else {
                        board[row][col] = '\0'; // undo move if not a winning move
                    }
                }
            }
        }
        return null; // no winning move found
    }

    private Move blockOpponentWin(char player) {
        char opponent = (player == 'X') ? 'O' : 'X';
        return winningMove(opponent);
    }
    private Move moveCorners() {
        if (isValidMove(0,0)) { // top-left
            return new Move(0,0);
        }
        if (isValidMove(0,boardSize - 1)) { // top-right
            return new Move(0, boardSize - 1);
        }
        if (isValidMove(boardSize - 1,boardSize - 1)) { // bottom-right
            return new Move(boardSize - 1, boardSize -1);
        }
        if (isValidMove(boardSize - 1,0)) { // bottom-right
            return new Move(boardSize - 1, 0);
        }
        return null; // no corner move available
    }

    // Unit Testing Methods

}
