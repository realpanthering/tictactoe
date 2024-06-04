
enum CPUDifficulty {
    EASY, MEDIUM, HARD
}

class Move { // Encapsulates move logic
    int row;
    int col;

    Move(int x, int y) {
        row = x;
        col =y;
    }

    public Move() {
    }
}

