package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import multimine.app.multimineapp.ZenController;
import java.util.concurrent.ThreadLocalRandom;

public class ZenBoard {

    private Tile[][] board;
    private int size;
    private Image hiddenTile;
    private Image flagTile;
    private Image bombTile;
    private Image[] numberTiles;
    private GridPane grid;
    private ZenController controller;
    private Stopwatch stopwatch;
    private int minesCount = 0;

    public ZenBoard(int size, Image hiddenTile, Image flagTile, Image bombTile, 
                   Image[] numberTiles, GridPane grid, 
                   ZenController controller,
                   Stopwatch stopwatch) {
        this.size = size;
        this.hiddenTile = hiddenTile;
        this.flagTile = flagTile;
        this.bombTile = bombTile;
        this.numberTiles = numberTiles;
        this.grid = grid;
        this.controller = controller;
        this.stopwatch = stopwatch;
        this.board = new Tile[size][size];
    }

    public void initializeBoard(int minesToPlace) {
        createBoard();
        placeMines(minesToPlace);
    }

    private void createBoard() {
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

    public void handleRightClick(int row, int col) {
        Tile tile = getTile(row, col);
        if (tile.isRevealed()) return;

        if (!tile.isFlagged()) {
            tile.setFlagged(true);
            tile.getView().setImage(flagTile);
        } else {
            tile.setFlagged(false);
            tile.getView().setImage(hiddenTile);
        }
    }

    public void handleClick(int row, int col) {
        Tile tile = getTile(row, col);
        if (tile.isRevealed() || tile.isFlagged()) return;

        tile.reveal();

        if (tile.hasMine()) {
            tile.getView().setImage(bombTile);
            minesCount++;
            controller.updateHearts(minesCount);

            if (minesCount >= 3) {
                stopwatch.stop();
                controller.showZenSummary(tile.getView(), stopwatch.getFormattedTime());
            }
        } else {
            int count = countAdjacentMines(row, col);
            tile.getView().setImage(numberTiles[count]);
        }

        tile.getView().setDisable(true);
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

    private void placeMines(int minesToPlace) {
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
