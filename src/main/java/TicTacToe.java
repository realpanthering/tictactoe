import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
    private boolean gameOver = false;
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
            }
        }

//        switch (userInput) {
//            case 0:
//                mode = new SimulatedMatchMode(p1, p2);
//            case 1:
//                mode = new SinglePlayerMode(p1, p2);
//            case 2:
//                mode = new TwoPlayerMode();
//        }
//         mode.startGame();

        if (userInput == 1) {
            System.out.println("This is a one player match between you and the computer.");
            System.out.println("Please choose your game mark: X or O");
            SetGameMarks();
            System.out.println("You: " + p1 + System.lineSeparator() + "CPU: " + p2);
        } else if (userInput == 2) { // userInput == 2
            System.out.println("This is a two player game, so please take turns accordingly.");
            System.out.println("Please specify Player 1's mark: Type 'X' or 'O'");
            SetGameMarks();
            System.out.println("Player 1: " + p1 + System.lineSeparator() + "Player 2: " + p2);
        } else { // userInput == 0;
            System.out.println("This is a simulated match.");
            System.out.println("Player 1: X\nPlyaer 2: O");
        }
        System.out.println("Enjoy!");
        board = new Board(p1, p2); // create board
        StartGame(userInput == 1, userInput == 0);
     }

    private void SetGameMarks() {
        Scanner input = new Scanner(System.in);
        boolean valid = false;
        while (!valid) {
            String s = input.next();
            if (s.length() == 1) {
                p1 = Character.toUpperCase(s.charAt(0)); // extract single character from user input

                if (p1 == 'X' || p1 == 'O') {
                    valid = true;
                } else {
                    System.out.println("Invalid game mark. Please retype 'X' or 'O'");
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
    private final char[][] board = new char[3][3]; // players: 'X' and 'O'
    private int numMoves;
    private boolean gameOver;

    private final char p1;
    private final char p2;
    private char winner;
    Board(char player1, char player2) {
        for (char[] row : board) {
            Arrays.fill(row, '\0');
        }
        numMoves = 0;
        gameOver = false;
        p1 = player1;
        p2 = player2;
        winner = 0; // default to draw
    }
    /**
     * @precondition - will not be called if game is already over
     * @param row - nonnegative integer 0-2
     * @param col - nonnegative integer 0-2
     * @param player - character 'X' or 'O' only
     * @return whether the requested move was successful
     */
    boolean makeMove(int row, int col, char player) {
        if (board[row][col] == 'X' || board[row][col] == 'O') {
            System.out.println("Space (" + row + "," + col + ") is already occupied.");
            return false; // unsuccessful move
        }
        board[row][col] = player;
        numMoves++;
        System.out.println("Your move: (" + row + "," + col + ")");
        return true; // successful move
    }

    void start() {
        Scanner input = new Scanner(System.in);
        boolean flag = true; // true == P1's turn
        while (numMoves < 9 && !gameOver) { // 9 board spaces, game control variable
            char currentPlayer = flag ? p1 : p2;
            String player = flag ? "P1" : "P2";
            display(); // display current board before each move

            System.out.println(player + ", it's your turn. Place your \"" + currentPlayer + "\" into a row and column seperated by a space (e.g., 1 1):");
            try {
                int row = input.nextInt();
                int col = input.nextInt();

                if (row >= 0 && row < 3 && col >= 0 && col < 3) { // valid range
                    if (makeMove(row, col, currentPlayer)) {
                        gameOver = checkWinner(currentPlayer); // check for a win after each successful move
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

    private boolean checkGameStatus(char currentPlayer) {
        if (checkWinner(currentPlayer)) {
            winner = currentPlayer;
            return true; // Game over due to win
        } else if (isBoardFull()) {
            return true; // Game over due to draw
        }
        return false; // Game continues
    }

    private boolean checkWinner(char currentPlayer) {
        // Iterate through the board to check for winning conditions
        for (int i = 0; i < board.length; i++) {
            if (HorizontalWin(currentPlayer, i) || VerticalWin(currentPlayer, i) || DiagonalWin(currentPlayer, i, i)) {
                return true; // Player has won
            }
        }
        // No win yet
        return false;
    }
    private boolean isBoardFull() {
        // Check if all cells on the board are filled
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    return false; // Board is not full
                }
            }
        }
        return true; // Board is full
    }

    private boolean DiagonalWin(char currentPlayer, int row, int col) {
        boolean main = true, anti = true;
        for (int i = 0; i < board.length; i++) {
           if (board[i][i] != currentPlayer) { // check main diagonal
               main = false;
           }
           if (board[i][board.length - i - 1] != currentPlayer) { // check anti diagonal
               anti = false;
           }
        }

        return main || anti;
    }

    private boolean VerticalWin(char currentPlayer, int col) {
        for (int k = 0; k < board.length; k++) {
            if (board[k][col] != currentPlayer) {
                return false;
            }
        }
        return true;
    }

    private boolean HorizontalWin(char currentPlayer, int row) {
        for (int k = 0; k < board[row].length; k++) {
            if (board[row][k] != currentPlayer) {
                return false;
            }
        }
        return true;
    }

    void display() {
        System.out.println("-------------"); // Top border
        for (int i = 0; i < board.length; i++) {
            System.out.print("| "); // Left border for each row
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] == '\0' ? " " : board[i][j]);
                System.out.print(" | ");
            }
            System.out.println(); // move to next line after each row
            System.out.println("-------------"); // Row separator
        }
    }

    private boolean playerTurn(char currentPlayer, Scanner input) {
        // Player's Turn
        System.out.println("It's your turn. Place your \"" + currentPlayer + "\" into a row and column seperated by a space (e.g., 1 1):");
        try {
            int row = input.nextInt();
            int col = input.nextInt();

            if (row >= 0 && row < 3 && col >= 0 && col < 3) { // valid range
                if (makeMove(row, col, currentPlayer)) {
                    gameOver = checkWinner(currentPlayer); // check for a win after each successful move
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
        while (numMoves < 9 && !gameOver) { // 9 board spaces, game control variable
            char currentPlayer = flag ? p1 : p2;
            String player = flag ? "Player" : "CPU";
            display(); // display current board before each move

            // Player's Turn
            if (flag) {
                flag = !playerTurn(currentPlayer, input);
            } else {
                makeCPUMove(difficulty, currentPlayer);
                flag = true;
            }
            System.out.println(numMoves + "th move");
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
            if (diff) {
                int[] moves = smartMove(x, y);
                x = moves[0];
                y = moves[1];
            } else {
                Random random = new Random();
                x = random.nextInt(3);
                y = random.nextInt(3);
            }
        }
        assert x >= 0 && y >= 0 : "";
        board[x][y] = currentPlayer;
        System.out.println("It is the CPU's turn. It's move: (" + x + ", " + y + ")");
        gameOver = checkWinner(currentPlayer);
    }

    private int[] smartMove(int x, int y) {
        // Minimax algorithm
        int[] moves = new int[2];
        moves[0] = 1;
        moves[1] = 1;
        return moves;
    }

    private boolean isValidMove(int x, int y) {
        return (x >= 0 && x <= 2 && y >= 0 && y <= 2 && board[x][y] == '\0');
    }


    void simulate() {}

}


