import java.io.File;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class TicTacToe {
    private boolean gameOver = false;
    private char p1;
    private char p2;

    private Board board;
    private GameMode mode;

    public static void main(String[] args) {

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

        switch (userInput) {
            case 0:
                mode = new SimulatedMatchMode(p1, p2);
            case 1:
                mode = new SinglePlayerMode(p1, p2);
            case 2:
                mode = new TwoPlayerMode();
        }
//
//        if (userInput == 1) {
//            System.out.println("This is a one player match between you and the computer.");
//            System.out.println("Please choose your game mark: X or O");
//            SetGameMarks();
//            System.out.println("You: " + p1 + System.lineSeparator() + "CPU: " + p2);
//        } else if (userInput == 2) { // userInput == 2
//            System.out.println("This is a two player game, so please take turns accordingly.");
//            System.out.println("Please specify Player 1's mark: Type 'X' or 'O'");
//            SetGameMarks();
//            System.out.println("Player 1: " + p1 + System.lineSeparator() + "Player 2: " + p2);
//        } else { // userInput == 0;
//            System.out.println("This is a simulated match.");
//            System.out.println("Player 1: X\nPlyaer 2: O");
//        }
        System.out.println("Enjoy!");
//        board = new Board(p1, p2); // create board
//        board.display();
//        StartGame(userInput == 1, userInput == 0);
        mode.startGame();
     }

    private void StartGame(boolean CPU, boolean auto) {
        if (auto) {
            board.simulate();
        } else if (CPU) {
            board.startCPU();
        } else {
            board.start();
        }
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

}

class Board {
    private final char[][] board = new char[3][3]; // players: 'X' and 'O'
    private int numMoves;
    private boolean gameOver;

    private char p1;
    private char p2;
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
        } else {
            board[row][col] = player;
            numMoves++;
            return true; // successful move
        }
    }

    void start() {
        Scanner input = new Scanner(System.in);
        boolean flag = true; // true == P1's turn
        while (numMoves < 9 && !gameOver) {
            char currentPlayer = flag ? p1 : p2;
            String player = flag ? "P1" : "P2";
            display();

            System.out.println(player + ", it's your turn. Enter row and column seperated by a space (e.g., 1 1):");
            try {
                int row = input.nextInt();
                int col = input.nextInt();

                if (row >= 0 && row < 3 && col >= 0 && col < 3) { // valid range
                    if (makeMove(row, col, currentPlayer)) {
                        flag = !flag; // only switch player if move was successful
                        gameOver = checkWinner(currentPlayer, row, col);
                    }
                } else {
                    System.out.println("Invalid move. Please enter a valid row and column space between 0-2");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. You must enter two consecutive integers between 0-2");
                input.nextLine();
            }
        }
        display();
        if (winner == 0) {
            System.out.println("Match ended in a draw.");
        } else if (winner == p1) {
            System.out.println("Winner of the match is Player 1. Congrats!");
        } else if (winner == p2) {
            System.out.println("Winner of the match is Player 2. Congrats!");
        }

    }

    private boolean checkWinner(char currentPlayer, int row, int col) {
        // Implement logic to check for winning conditions at the current cell

        // WIN CONS: (1) Horizontal Win, (2) Vertical Win, (3) Diagonal Win
        if (HorizontalWin(currentPlayer, row)) {
            System.out.println("Horizontal Win at row " + row);
            winner = currentPlayer;
            return true;
        }
        if (VerticalWin(currentPlayer, col)) {
            System.out.println("Vertical Win at row " + row);
            winner = currentPlayer;
            return true;
        }
        if (DiagonalWin(currentPlayer, row, col)) {
            System.out.println("Diagonal Win at row " + row);
            winner = currentPlayer;
            return true;
        }

        return false;
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

    public void startCPU() {
    }

    void simulate() {}

}


