package model;

import javafx.scene.image.ImageView;
import java.util.concurrent.ThreadLocalRandom;

public class ZenBoard {

    private Tile[][] board;
    private int size;

    public ZenBoard(int size) {
        this.size = size;
        board = new Tile[size][size];
    }

    public void createBoard(javafx.scene.image.Image hiddenTile, javafx.scene.layout.GridPane grid) {

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                ImageView view = new ImageView(hiddenTile);
                view.setFitWidth(24);
                view.setFitHeight(24);
                view.setPickOnBounds(true);

                Tile tile = new Tile(row, col, view);
                board[row][col] = tile;

                grid.add(view, col, row);
            }
        }
    }
    public int countAdjacentMines(int row, int col) {

        int count = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {

                if (r >= 0 && r < size && c >= 0 && c < size) {

                    if (!(r == row && c == col) && board[r][c].hasMine()) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
    public void placeMines(int minesToPlace) {

        int placed = 0;

        while (placed < minesToPlace) {

            int r = ThreadLocalRandom.current().nextInt(size);
            int c = ThreadLocalRandom.current().nextInt(size);

            if (!board[r][c].hasMine()) {
                board[r][c].setMine(true);
                placed++;
            }
        }
    }

    public Tile getTile(int r, int c) {
        return board[r][c];
    }

    public int getSize() {
        return size;
    }
}
