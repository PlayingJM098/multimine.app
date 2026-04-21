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
    
    private int minesCount = 0;
    private int safeTilesCount;
    private int revealedSafeTiles = 0;
    private int clicksThisTurn = 0;
    private boolean player1Turn = true;
    private int player1Lives = 3;
    private int player2Lives = 3;
    private double player1Time = 10.0;
    private double player2Time = 10.0;
    private double player1CumulativeTime = 0.0;
    private double player2CumulativeTime = 0.0;
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
        placeMines(minesToPlace);
        resetGameState();
    }

    private void resetGameState() {
        revealedSafeTiles = 0;
        minesCount = 0;
        clicksThisTurn = 0;
        player1Turn = true;
        player1Lives = 3;
        player2Lives = 3;
        player1Time = 10.0;
        player2Time = 10.0;
        player1CumulativeTime = 0.0;
        player2CumulativeTime = 0.0;
        controller.updateUI();
        controller.startPlayerTimer();
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

        tile.reveal();

        if (tile.hasMine()) {
            tile.getView().setImage(bombTile);
            minesCount++;
            playerHitMine();
            
            if (isGameOver()) {
                endGame(false);
            }
        } else {
            int count = countAdjacentMines(row, col);
            tile.getView().setImage(numberTiles[count]);
            revealedSafeTiles++;
            playerSafeClick();

            if (revealedSafeTiles >= safeTilesCount) {
                endGame(true);
            }
        }

        tile.getView().setDisable(true);
        registerClick();
    }

    private void playerHitMine() {
        if (player1Turn) {
            player1CumulativeTime += (10.0 - player1Time);
            player1Lives--;
            controller.updatePlayer1Hearts(player1Lives);
        } else {
            player2CumulativeTime += (10.0 - player2Time);
            player2Lives--;
            controller.updatePlayer2Hearts(player2Lives);
        }
        controller.updateUI();
    }

    private void playerSafeClick() {
        if (player1Turn) {
            player1Time += 0.5;
        } else {
            player2Time += 0.5;
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
        controller.endGame(player1Name, player1CumulativeTime, player2Name, player2CumulativeTime, teamWin);
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

    public boolean isPlayer1Turn() { return player1Turn; }
    public String getCurrentPlayerName() { return player1Turn ? player1Name : player2Name; }
    public double getPlayer1Time() { return player1Time; }
    public double getPlayer2Time() { return player2Time; }
    public Tile getTile(int r, int c) { return board[r][c]; }
    public int getSize() { return size; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public double getPlayer1CumulativeTime() { return player1CumulativeTime; }
    public double getPlayer2CumulativeTime() { return player2CumulativeTime; }
    public void setPlayer1Time(double time) { this.player1Time = time; }
    public void setPlayer2Time(double time) { this.player2Time = time; }
    public void setPlayer1CumulativeTime(double time) { this.player1CumulativeTime = time; }
    public void setPlayer2CumulativeTime(double time) { this.player2CumulativeTime = time; }
}
