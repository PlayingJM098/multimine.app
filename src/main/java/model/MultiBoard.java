package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import multimine.app.multimineapp.MultiController;
import java.util.concurrent.ThreadLocalRandom;
import multimine.app.multimineapp.SettingsController;
/**
 * Represents the multiplayer game board for the MultiMine game.
 * 
 * <p>This class manages the board state, player turns, mine placement,
 * tile interactions, and win/lose conditions for a two-player game.</p>
 * 
 * <p>Features include:
 * <ul>
 *     <li>First-click safety (guaranteed safe starting area)</li>
 *     <li>Turn-based gameplay (2 clicks per turn)</li>
 *     <li>Lives and time tracking per player</li>
 *     <li>Flood-fill reveal for empty tiles</li>
 * </ul>
 * </p
 * @author JM Rahon
 */
public class MultiBoard {

    /** 2D array representing the board tiles */
    private Tile[][] board;

    /** Size of the board (NxN) */
    private int size;

    /** Images used for tile states */
    private Image hiddenTile, flagTile, bombTile, heartImage;

    /** Images for number tiles (0–8 adjacent mines) */
    private Image[] numberTiles;

    /** JavaFX GridPane used to render the board */
    private GridPane grid;

    /** Controller used to update UI elements */
    private MultiController controller;

    /** Tracks if the first click has been made */
    private boolean firstClick = true;

    /** Number of mines revealed */
    private int minesCount = 0;

    /** Total number of safe tiles */
    private int safeTilesCount;

    /** Number of revealed safe tiles */
    private int revealedSafeTiles = 0;

    /** Number of clicks made in the current turn */
    private int clicksThisTurn = 0;

    /** True if it is Player 1's turn */
    private boolean player1Turn = true;

    /** Player lives */
    private int player1Lives = 3;
    private int player2Lives = 3;

    /** Player timers (seconds) */
    private double player1Time = 10.0;
    private double player2Time = 10.0;

    /** Player names */
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";

    /** Size of each tile in pixels */
    private double tileSize = 24.0;

    /**
     * Constructs a MultiBoard instance.
     *
     * @param size board size (NxN)
     * @param hiddenTile image for hidden tiles
     * @param flagTile image for flagged tiles
     * @param bombTile image for mines
     * @param heartImage image representing lives
     * @param numberTiles images for numbered tiles
     * @param grid GridPane used for rendering
     * @param controller controller handling UI updates
     */
    public MultiBoard(int size, Image hiddenTile, Image flagTile, Image bombTile, Image heartImage,
                      Image[] numberTiles, GridPane grid, MultiController controller) {
        this.player1Name = SettingsController.getPlayer1Name();
        this.player2Name = SettingsController.getPlayer2Name();
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

    /**
     * Initializes the board and resets counters.
     *
     * @param minesToPlace number of mines to place
     */
    public void initializeBoard(int minesToPlace) {
        createBoard();
        safeTilesCount = (size * size) - minesToPlace;
        revealedSafeTiles = 0;
        minesCount = 0;
        firstClick = true;
    }

    /**
     * Resets the entire game state including players, timers, and turn.
     */
    public void resetGameState() {
        revealedSafeTiles = 0;
        minesCount = 0;
        clicksThisTurn = 0;
        player1Turn = true;
        player1Lives = 3;
        player2Lives = 3;
        player1Time = 10.0;
        player2Time = 10.0;
        firstClick = true;
        controller.updateUI();
    }

    /**
     * Creates the board UI and initializes all tiles.
     */
    private void createBoard() {
        grid.getChildren().clear();
        grid.setPrefSize(tileSize * size, tileSize * size);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                ImageView view = new ImageView(hiddenTile);
                view.setFitWidth(tileSize);
                view.setFitHeight(tileSize);
                view.setPickOnBounds(true);

                Tile tile = new Tile(row, col, view);
                board[row][col] = tile;
                grid.add(view, col, row);
            }
        }
        setupClickHandlers();
    }

    /**
     * Assigns mouse click handlers to each tile.
     */
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

    /**
     * Handles right-click (flagging/unflagging).
     *
     * @param row tile row
     * @param col tile column
     */
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

    /**
     * Handles left-click actions including revealing tiles and mine detection.
     *
     * @param row tile row
     * @param col tile column
     */
    public void handleClick(int row, int col) {
        Tile tile = getTile(row, col);
        if (tile.isRevealed() || tile.isFlagged()) return;

        if (firstClick) {
            firstClick = false;
            ensureFirstClickSafe(row, col);
            floodFillReveal(row, col);
            revealedSafeTiles++;
            playerSafeClick();
        } else if (tile.hasMine()) {
            tile.reveal();
            tile.getView().setImage(bombTile);
            tile.getView().setDisable(true);
            minesCount++;
            playerHitMine();

            if (isGameOver()) {
                endGame(false);
            }
        } else {
            floodFillReveal(row, col);
            playerSafeClick();
        }

        registerClick();
    }

    /**
     * Ensures the first click and surrounding tiles are safe.
     */
    private void ensureFirstClickSafe(int clickRow, int clickCol) {
        int minesToPlace = (size * size) - safeTilesCount;

        for (int r = Math.max(0, clickRow - 1); r <= Math.min(size - 1, clickRow + 1); r++) {
            for (int c = Math.max(0, clickCol - 1); c <= Math.min(size - 1, clickCol + 1); c++) {
                board[r][c].setMine(false);
            }
        }

        int placed = 0;
        while (placed < minesToPlace) {
            int r = ThreadLocalRandom.current().nextInt(size);
            int c = ThreadLocalRandom.current().nextInt(size);

            if (Math.abs(r - clickRow) <= 1 && Math.abs(c - clickCol) <= 1) continue;

            if (!board[r][c].hasMine()) {
                board[r][c].setMine(true);
                placed++;
            }
        }
    }

    /**
     * Recursively reveals safe tiles using flood-fill.
     */
    private void floodFillReveal(int row, int col) {
        Tile tile = getTile(row, col);

        if (tile.isRevealed() || tile.hasMine() || tile.isFlagged()) return;

        tile.reveal();
        revealedSafeTiles++;

        int adjacentMines = countAdjacentMines(row, col);
        tile.getView().setImage(numberTiles[adjacentMines]);
        tile.getView().setDisable(true);

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

    /** Reduces life of the current player when hitting a mine */
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

    /** Rewards player with time when clicking a safe tile */
    private void playerSafeClick() {
        if (player1Turn) {
            player1Time = Math.min(10.0, player1Time + 0.5);
        } else {
            player2Time = Math.min(10.0, player2Time + 0.5);
        }
    }

    /** Registers clicks and switches turn after 2 clicks */
    private void registerClick() {
        clicksThisTurn++;
        if (clicksThisTurn >= 2) {
            clicksThisTurn = 0;
            switchTurn();
        }
    }

    /** Switches the current player's turn */
    private void switchTurn() {
        player1Turn = !player1Turn;
        controller.updateUI();
    }

    /**
     * Checks if the game is over.
     *
     * @return true if any player has no lives left
     */
    public boolean isGameOver() {
        return player1Lives <= 0 || player2Lives <= 0;
    }

    /**
     * Ends the game and notifies controller.
     *
     * @param teamWin true if players win, false if lose
     */
    public void endGame(boolean teamWin) {
        controller.endGame(player1Name, player1Time, player2Name, player2Time, teamWin);
    }

    /**
     * Counts adjacent mines around a tile.
     *
     * @param row tile row
     * @param col tile column
     * @return number of adjacent mines
     */
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

    /** Getter and setter methods */

    public boolean isPlayer1Turn() { return player1Turn; }
    public String getCurrentPlayerName() { return player1Turn ? player1Name : player2Name; }
    public double getPlayer1Time() { return player1Time; }
    public double getPlayer2Time() { return player2Time; }
    public Tile getTile(int r, int c) { return board[r][c]; }
    public int getSize() { return size; }

    public void setTileSize(double size) { this.tileSize = size; }

    public void setBoardSize(int size) {
        this.size = size;
        this.board = new Tile[size][size];
    }

    public void setPlayer1Name(String name) {
        this.player1Name = name.isEmpty() ? "Player 1" : name;
    }

    public void setPlayer2Name(String name) {
        this.player2Name = name.isEmpty() ? "Player 2" : name;
    }

    public void setPlayer1Time(double time) {
        this.player1Time = Math.max(0, time);
    }

    public void setPlayer2Time(double time) {
        this.player2Time = Math.max(0, time);
    }
}
