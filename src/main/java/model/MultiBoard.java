package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import multimine.app.multimineapp.MultiController;
import java.util.concurrent.ThreadLocalRandom;

public class MultiBoard {
    private Tile[][] board;
    private int size;
    private Image hiddenTile, flagTile, bombTile, heartImage;
    private Image[] numberTiles;
    private GridPane grid;
    private MultiController controller;
    private boolean firstClick = true; // Track first click
    private int minesCount = 0;
    private int safeTilesCount;
    private int revealedSafeTiles = 0;
    private int clicksThisTurn = 0;
    private boolean player1Turn = true;
    private int player1Lives = 3;
    private int player2Lives = 3;
    private double player1Time = 10.0;
    private double player2Time = 10.0;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";

    public MultiBoard(int size, Image hiddenTile, Image flagTile, Image bombTile, Image heartImage,
                     Image[] numberTiles, GridPane grid, MultiController controller) {
        this.size = size;
        this.hiddenTile = hiddenTile;
        this.flagTile = flagTile;
        this.bombTile = bombTile;
        this.heartImage = heartImage;
        this.numberTiles = numberTiles;
        this.grid = grid;
        this.controller = controller;
        this.board = new Tile[size][size];
    }

    public void initializeBoard(int minesToPlace) {
        createBoard();
        safeTilesCount = (size * size) - minesToPlace;
        revealedSafeTiles = 0;
        minesCount = 0;
        firstClick = true; // Reset first click flag
    }
    
    public void resetGameState() {
        revealedSafeTiles = 0;
        minesCount = 0;
        clicksThisTurn = 0;
        player1Turn = true;
        player1Lives = 3;
        player2Lives = 3;
        player1Time = 10.0;
        player2Time = 10.0;
        firstClick = true; // ADD THIS LINE
        controller.updateUI();
    }

    private void createBoard() {
        grid.getChildren().clear();
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
        setupClickHandlers();
    }

    private void setupClickHandlers() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int r = row, c = col;
                Tile tile = board[row][col];
                tile.getView().setOnMouseClicked(e -> {
                    if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                        handleRightClick(r, c);
                    } else {
                        handleClick(r, c);
                    }
                });
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

        // FIRST CLICK SAFETY
        if (firstClick) {
            firstClick = false;
            ensureFirstClickSafe(row, col);
            floodFillReveal(row, col); // Auto-reveal large safe area
            revealedSafeTiles++; 
            playerSafeClick();
        } else if (tile.hasMine()) {
            // Regular mine hit (after first click)
            tile.reveal();
            tile.getView().setImage(bombTile);
            tile.getView().setDisable(true);
            minesCount++;
            playerHitMine();

            if (isGameOver()) {
                endGame(false);
            }
        } else {
            // Regular safe click (after first click)
            floodFillReveal(row, col);
            playerSafeClick();
        }

        registerClick();
    }
    private void ensureFirstClickSafe(int clickRow, int clickCol) {
        int minesToPlace = (size * size) - safeTilesCount; // Original mine count

        // Mark clicked tile + 8 neighbors as safe (3x3 area = 9 tiles)
        for (int r = Math.max(0, clickRow - 1); r <= Math.min(size - 1, clickRow + 1); r++) {
            for (int c = Math.max(0, clickCol - 1); c <= Math.min(size - 1, clickCol + 1); c++) {
                board[r][c].setMine(false); // Force safe
            }
        }

        // Place mines everywhere else
        int placed = 0;
        while (placed < minesToPlace) {
            int r = ThreadLocalRandom.current().nextInt(size);
            int c = ThreadLocalRandom.current().nextInt(size);

            // Skip the 3x3 safe zone
            if (Math.abs(r - clickRow) <= 1 && Math.abs(c - clickCol) <= 1) {
                continue;
            }

            if (!board[r][c].hasMine()) {
                board[r][c].setMine(true);
                placed++;
            }
        }
    }
    private void floodFillReveal(int row, int col) {
        Tile tile = getTile(row, col);

        if (tile.isRevealed() || tile.hasMine() || tile.isFlagged()) return;

        tile.reveal();
        revealedSafeTiles++; // count here

        int adjacentMines = countAdjacentMines(row, col);
        tile.getView().setImage(numberTiles[adjacentMines]);
        tile.getView().setDisable(true);

        // ✅ CHECK WIN HERE (every reveal)
        if (revealedSafeTiles == safeTilesCount) {
            endGame(true);
            return;
        }

        if (adjacentMines == 0) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (r >= 0 && r < size && c >= 0 && c < size) {
                        floodFillReveal(r, c);
                    }
                }
            }
        }
    }

    private void playerHitMine() {
        if (player1Turn) {
            player1Lives--;
            controller.updatePlayer1Hearts(player1Lives);
        } else {
            player2Lives--;
            controller.updatePlayer2Hearts(player2Lives);
        }
        controller.updateUI();
    }

    private void playerSafeClick() {
    if (player1Turn) {
        player1Time = Math.min(10.0, player1Time + 0.5); // Cap at 10s
    } else {
        player2Time = Math.min(10.0, player2Time + 0.5); // Cap at 10s
    }
}

    private void registerClick() {
        clicksThisTurn++;
        if (clicksThisTurn >= 2) {
            clicksThisTurn = 0;
            switchTurn();
        }
    }

    private void switchTurn() {
        player1Turn = !player1Turn;
        controller.updateUI();
    }

    public boolean isGameOver() {
        return player1Lives <= 0 || player2Lives <= 0;
    }

    public void endGame(boolean teamWin) {
        controller.endGame(player1Name, player1Time, player2Name, player2Time, teamWin);
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

    public boolean isPlayer1Turn() { return player1Turn; }
    public String getCurrentPlayerName() { return player1Turn ? player1Name : player2Name; }
    public double getPlayer1Time() { return player1Time; }
    public double getPlayer2Time() { return player2Time; }
    public Tile getTile(int r, int c) { return board[r][c]; }
    public int getSize() { return size; }
    public void setPlayer1Time(double time) { 
        this.player1Time = Math.max(0, time); // Prevent negative time
    }
    public void setPlayer2Time(double time) { 
        this.player2Time = Math.max(0, time); // Prevent negative time
    }
    
}
