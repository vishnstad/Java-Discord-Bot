package Module4;

import java.util.Random;

public class Game2048 {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static final int SIZE = 4;
    private int[][] board;
    private Random random;

    public Game2048() {
        board = new int[SIZE][SIZE];
        random = new Random();
        addRandomTile();
        addRandomTile();
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean move(Direction direction) {
        boolean moved = false;
        switch (direction) {
            case UP:
                moved = moveUp();
                break;
            case DOWN:
                moved = moveDown();
                break;
            case LEFT:
                moved = moveLeft();
                break;
            case RIGHT:
                moved = moveRight();
                break;
        }

        if (moved) {
            addRandomTile();
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < SIZE; col++) {
            int[] colArray = new int[SIZE];
            for (int row = 0; row < SIZE; row++) {
                colArray[row] = board[row][col];
            }
            int[] newCol = merge(colArray);
            for (int row = 0; row < SIZE; row++) {
                if (board[row][col] != newCol[row]) {
                    board[row][col] = newCol[row];
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < SIZE; col++) {
            int[] colArray = new int[SIZE];
            for (int row = 0; row < SIZE; row++) {
                colArray[row] = board[SIZE - 1 - row][col];
            }
            int[] newCol = merge(colArray);
            for (int row = 0; row < SIZE; row++) {
                if (board[SIZE - 1 - row][col] != newCol[row]) {
                    board[SIZE - 1 - row][col] = newCol[row];
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < SIZE; row++) {
            int[] newRow = merge(board[row]);
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != newRow[col]) {
                    board[row][col] = newRow[col];
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < SIZE; row++) {
            int[] rowArray = new int[SIZE];
            for (int col = 0; col < SIZE; col++) {
                rowArray[col] = board[row][SIZE - 1 - col];
            }
            int[] newRow = merge(rowArray);
            for (int col = 0; col < SIZE; col++) {
                if (board[row][SIZE - 1 - col] != newRow[col]) {
                    board[row][SIZE - 1 - col] = newRow[col];
                    moved = true;
                }
            }
        }
        return moved;
    }

    private int[] merge(int[] array) {
        int[] newArray = new int[SIZE];
        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            if (array[i] != 0) {
                if (index > 0 && newArray[index - 1] == array[i]) {
                    newArray[index - 1] *= 2;
                } else {
                    newArray[index++] = array[i];
                }
            }
        }
        return newArray;
    }

    private void addRandomTile() {
        int row, col;
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (board[row][col] != 0);

        board[row][col] = random.nextInt(10) == 0 ? 4 : 2;
    }
}