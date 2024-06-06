package Module4;

public class Game2048GUI {
    private int[][] board;

    public void initGame(Game2048 game) {
        this.board = game.getBoard();
    }

    public void updateBoard(int[][] newBoard) {
        this.board = newBoard;
    }

    public int[][] getBoard() {
        return board;
    }
}
