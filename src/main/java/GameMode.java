public interface GameMode {
    void startGame();
    void setPlayerMarks(char p1, char p2);
}

// Concrete class for 2-player mode
class TwoPlayerMode implements GameMode {
    private Board board;
    private char p1;
    private char p2;

    @Override
    public void startGame() {
        System.out.println("This is a two player game, so please take turns accordingly.");
        // Initialize the board
        board = new Board(p1, p2,3, 3);
        // Start the game
        board.start();
    }

    @Override
    public void setPlayerMarks(char p1, char p2) {
        // Not needed for 2-player mode
    }
}

// Concrete class for 1-player mode vs CPU
class SinglePlayerMode implements GameMode {
    private Board board;
    private char p1;
    private char p2;

    public SinglePlayerMode(char p1, char p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void startGame() {
        System.out.println("This is a one player match between you and the computer.");
        // Initialize the board
        board = new Board(p1, p2, 3,3);
        // Start the game
//        board.startCPU();
        // Implement logic for 1-player mode here
    }

    @Override
    public void setPlayerMarks(char p1, char p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}

// Concrete class simulated computer match
class SimulatedMatchMode implements GameMode {
    private Board board;
    private char p1;
    private char p2;

    public SimulatedMatchMode(char p1, char p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void startGame() {
        System.out.println("This is a simulated match.");
        // Initialize the board
        board = new Board(p1, p2, 3,3);
        // Start the game
        board.simulate(false);
        // Implement logic for simulated match mode here
    }

    @Override
    public void setPlayerMarks(char p1, char p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
