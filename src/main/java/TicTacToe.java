import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

    private char p1;
    private char p2;

    private Board board;
    private GameMode mode;

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.Game();
    }

    void Game() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Welcome to TicTacToe! Choose whether you want to play 2-player" +
                ", against a CPU (1-player), or simulate an automated match");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Type '1' for 1-player, '2' for 2-player, and 0 for simulation.");
        Scanner scanner = new Scanner(System.in);
        int userInput = 0;
        boolean validInput = false;
        while (!validInput) {
            if (scanner.hasNextInt()) {
                userInput = scanner.nextInt();
                if (userInput == 0 || userInput == 1|| userInput == 2) {
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please type 1 for 1-player or 2 for 2-player or 0 for simulation.");
                }
            } else {
                System.out.println("Invalid input. Please type a valid integer");
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
        StartGame(userInput == 1, userInput == 0);
     }

    private void SetupBoard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose your board size (e.g '3' for 3x3 board)");
        int n = 3; // default, 3x3 board
        boolean v = false;
        while (!v) {
            if (scanner.hasNextInt()) {
                n = scanner.nextInt();
                if (n > 1) { v = true; }
                else {
                    System.out.println("Board size must be a positive integer greater than 1.");
                }
            } else {
                System.out.println("Invalid input. Please type a valid integer");
                scanner.nextLine();
            }
        }
        System.out.println("How many marks in a row to win? Must be no greater than the size of the board (k <= n)");
        int k = n; // default streak = board size
        boolean v2 = false;
        while (!v2) {
            if (scanner.hasNextInt()) {
                k = scanner.nextInt();
                if (k > n){
                    System.out.println("Streak must be less than or equal to the board size. Please retype!");
                } else if (k <= 0) {
                    System.out.println("Streak must be a positive integer. Please retype!");
                } else if (k == 1) {
                    System.out.println("... yeah no. Retype. ");
                } else { v2 = true; }
            } else {
                System.out.println("Invalid input. Please type a valid integer");
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
                    System.out.println("Invalid game marker. Please retype 'X' or 'O'");
                }
            } else {
                System.out.println("Input length is not valid. Please type a single character 'X' or 'O'");
            }
        }
        p2 = (p1 == 'O') ? 'X' : 'O';
    }
    private void StartGame(boolean CPU, boolean auto) {
        if (auto) {
            board.simulate();
        } else if (CPU) {
            System.out.println("Select CPU difficulty - easy (E) or hard (H):");
            boolean valid = false;
            Scanner input = new Scanner(System.in);
            boolean difficult = false;
            while (!valid) {
                char ans = Character.toUpperCase(input.next().charAt(0));
                if (ans == 'E' || ans == 'H') {
                    difficult = ans == 'H';
                    valid = true;
                } else {
                    System.out.println("Invalid input. Please type 'E' (easy) or 'H' (hard):");
                }
            }
            board.startCPU(difficult);
        } else {
            board.start();
        }
    }

}

class Board {
    private final char[][] board; // players: 'X' and 'O'
    private int numMoves;
    private final int maxMoves;
    private boolean gameOver;
    private final int boardSize;
    private  final int streak;
    private final char p1;
    private final char p2;
    private char winner;

    private static final int WIN_SCORE = 10;
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
        p1 = player1;
        p2 = player2;
        winner = 0; // default to draw
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
                    }
                } else {
                    System.out.println("Invalid move. Please enter a valid row and column space between 0-2");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. You must enter two consecutive integers between 0-2");
                input.nextLine();
            }
            System.out.println(numMoves + "th turn.");
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
            System.out.println("Space (" + row + "," + col + ") is already occupied.");
            return false; // unsuccessful move
        }
        board[row][col] = player;
        numMoves++;
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

    private boolean checkWinner(char currentPlayer) {
        return HorizontalWin(currentPlayer) || VerticalWin(currentPlayer) || DiagonalWin(currentPlayer);
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
        System.out.println("Board is full.");
        return true; // Board is full
    }

    private boolean DiagonalWin(char currentPlayer) {
        int mainCount = 0; // Check main diagonal
        for (int i = 0; i < boardSize; i++) {
            if (board[i][i] == currentPlayer) {
                mainCount++;
                if (mainCount >= streak) {
                    System.out.println("Win detected across main diagonal");
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
                    System.out.println("Win detected across anti-diagonal");
                    return true; // detect win when streak is reached
                }
            } else {
                antiCount = 0; // reset counter of streak is broken
            }
        }
        return false; // no diagonal win detected
    }

    private boolean VerticalWin(char currentPlayer) {
        for (int col = 0; col < boardSize; col++) {
            int counter = 0;
            for (int k = 0; k < boardSize; k++) {
                if (board[k][col] == currentPlayer) {
                    counter++;
                    if (counter >= streak) {
                        System.out.println("Vertical Win detected across column " + col);
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
                        System.out.println("Horizontal Win detected across row " + row);
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

    private boolean makePlayerMove(char currentPlayer, @NotNull Scanner input) {
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
                System.out.println("Invalid move. Please enter a valid row and column space between 0-2");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. You must enter two consecutive integers between 0-2");
            input.nextLine();
        }
        return false;
    }

    public void startCPU(boolean difficulty) {
        Scanner input = new Scanner(System.in);
        boolean flag = true; // true == P1's turn
        while (numMoves < maxMoves && !gameOver) { // n x n board spaces, game control variable
            char currentPlayer = flag ? p1 : p2;
            display(); // display current board before each move

            // Player's Turn
            if (flag) {
                flag = !makePlayerMove(currentPlayer, input);
            } else {
                makeCPUMove(difficulty, currentPlayer);
                flag = true;
            }
            System.out.println(numMoves + "th move:");
            gameOver = checkGameStatus(currentPlayer);
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

    void makeCPUMove(boolean diff, char currentPlayer) {
        int x = -1;
        int y = -1;
        while (!isValidMove(x, y)) {
            if (diff) { // smart (minimax strategy) CPU move
                var bestMove = smartMove(currentPlayer);
                x = bestMove.row;
                y = bestMove.col;
            } else { // dumb (random) CPU move
                Random random = new Random();
                x = random.nextInt(boardSize);
                y = random.nextInt(boardSize);
            }
        }
        assert x >= 0 && y >= 0 : "";
        board[x][y] = currentPlayer;
        numMoves++;
        System.out.println("It is the CPU's turn (" + currentPlayer + "). It's move: (" + x + ", " + y + ")");
    }

    public void simulate() {
        boolean turn = false;
        while (numMoves < maxMoves && !gameOver) {
            char current = turn ? p1 : p2;
            display();
            makeCPUMove(false, current);
            turn = !turn;
            gameOver = checkGameStatus(current);
            System.out.println(numMoves + "th move");
        }
        display();
        if (winner == 0) {
            System.out.println("Match ended in a draw.");
        } else {
            System.out.println("CPU " + (winner == p1 ? "1" : "2")
                + " (marker " + winner + ") wins in " + numMoves + " rounds!");
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

    private int minimax(int depth, boolean isMaximizingPlayer, char player) {
        int score = evaluate(player);
        if (score == WIN_SCORE) {
            return score - depth;
        } else if (score < -WIN_SCORE) {
            return score + depth;
        }

        if (isBoardFull()) { // if current board state is terminating state
            return 0; // return value of board (it is a tie)?
        }

        int best;// set to very large number (+∞)
        if (isMaximizingPlayer) {
            best = Integer.MIN_VALUE;
            // for each move in board
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (isValidMove(i, j)) {
                        board[i][j] = player;
                        // call minmax recursively (until base case is hit) and choose the maximum value
                        best = Math.max(best, minimax(depth + 1, false, player));
                        board[i][j] = '\0'; // undo the move
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
                        best = Math.min(best, minimax(depth + 1, true, player));
                        board[i][j] = '\0'; // undo the move
                    }
                }
            }
        }
        return best;
    }
    private Move smartMove(char player) {
        // Minimax algorithm
        int bestVal = -1000;
        Move bestMove = new Move();
        bestMove.row = -1;
        bestMove.col = -1;

        // for each move in board
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = player;
                    int moveVal = minimax(0, false, player);
                    board[i][j] = '\0';
                    if (moveVal > bestVal) { // if current move is better than bestMove
                        bestMove.row = i;
                        bestMove.col = j;
                        bestVal = moveVal;
                    }
                }

            }
        }
        System.out.printf("The value of the best Move " +
                "is : %d\n\n", bestVal);

        return bestMove;
    }


    private static class Move {
        int row;
        int col;
    }
}

