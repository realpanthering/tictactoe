import static org.junit.jupiter.api.Assertions.*;

class TicTacToeTest {
    TicTacToe game = new TicTacToe();

    void testInitialBoardState() {
        var board = game.getBoard();

        // REFACTOR PROJECT STRUCTURE TO SEPARATE GAME LOGIC FROM USER INPUT PROMPTS AND AI, ETC
        // OTHERWISE UNIT TESTING IS NOT REALLY GOING TO WORK HERE
    }
}