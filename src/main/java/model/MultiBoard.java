package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import multimine.app.multimineapp.MultiController;
import java.util.concurrent.ThreadLocalRandom;
import multimine.app.multimineapp.SettingsController;

/**
 * MultiBoard represents the game board for a two-player competitive Minesweeper variant.
 * Features turn-based gameplay where players alternate clicks (2 clicks per turn),
 * have limited lives (3 each), and time bonuses for safe clicks. The first click is
 * guaranteed safe with a 3x3 safe zone. Game ends when all safe tiles are revealed
 * (team win) or either player runs out of lives.
 * 
 * <p>Key mechanics:
 * <ul>
 * <li>Turn-based: 2 clicks per turn, then switch players</li>
 * <li>Lives system: Hit mine = lose 1 life (3 lives each)</li>
 * <li>Time bonuses: Safe click = +0.5s (capped at 10s)</li>
 * <li>First click safety: 3x3 area guaranteed safe + auto-flood fill</li>
 * <li>Flood fill reveal for zero-adjacent-mine tiles</li>
 * </ul>
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 */
public class MultiBoard {
    /** 2D array representing the game board tiles */
    private Tile[][] board;
    
    /** Board dimensions (square grid: size x size) */
    private int size;
    
    /** Game asset images */
    private Image hiddenTile, flagTile, bombTile, heartImage;
    private Image[] numberTiles;
    
    /** UI components */
    private GridPane grid;
    private MultiController controller;
    
    /** Game state tracking */
    private boolean firstClick = true;
    private int minesCount = 0;
    private int safeTilesCount;
    private int revealedSafeTiles = 0;
    private int clicksThisTurn = 0;
    
    /** Multiplayer state */
    private boolean player1Turn = true;
    private int player1Lives = 3;
    private int player2Lives = 3;
    private double player1Time = 10.0;
    private double player2Time = 10.0;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    
    /** Visual settings */
    private double tileSize = 24.0;

    /**
     * Constructs a new MultiBoard with the specified configuration.
     * 
     * @param size board size (square grid: size x size)
     * @param hiddenTile image for unrevealed tiles
     * @param flagTile image for flagged tiles
     * @param bombTile image for revealed mines
     * @param heartImage image for life indicators
     * @param numberTiles array of number images [0-8] for adjacent mine counts
     * @param grid GridPane to render the board
     * @param controller controller for UI updates and game flow
     */
    public MultiBoard(int size, Image hiddenTile, Image flagTile, Image bombTile, Image heartImage,
                     Image[] numberTiles, GridPane grid, MultiController controller) {
        // In MultiBoard constructor/initialize:
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
     * Initializes a new game board with the specified number of mines.
     * Resets all game state and creates fresh tiles.
     * 
     * @param minesToPlace total number of mines to place on the board
     */
    public void initializeBoard(int minesToPlace) {
        createBoard();
        safeTilesCount = (size * size) - minesToPlace;
        revealedSafeTiles = 0;
        minesCount = 0;
        firstClick = true; // Reset first click flag
    }
    
    /**
     * Resets game state for a new round while preserving board configuration.
     * Restores lives, time, turns, and other gameplay variables.
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
        firstClick = true; // ADD THIS LINE
        controller.updateUI();
    }

    /**
     * Handles right-click (flag/unflag) on a tile.
     * 
     * @param row tile row (0 to size-1)
     * @param col tile column (0 to size-1)
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
     * Handles left-click (reveal) on a tile. Implements first-click safety,
     * mine detection, flood-fill reveal, and turn management.
     * 
     * @param row tile row (0 to size-1)
     * @param col tile column (0 to size-1)
     */
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

    /**
     * Ensures the first click lands in a safe 3x3 area by forcing those tiles
     * safe and placing all mines elsewhere. Called only on first click.
     * 
     * @param clickRow row of first click
     * @param clickCol column of first click
     */
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

    /**
     * Recursively reveals safe tiles using flood-fill algorithm.
     * Reveals adjacent zero-mine tiles automatically. Checks win condition
     * after each reveal.
     * 
     * @param row tile row to reveal
     * @param col tile column to reveal
     */
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

    /**
     * Deducts a life from the current player when they hit a mine.
     * Updates UI with new heart count.
     */
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

    /**
     * Awards time bonus (+0.5s, capped at 10s) to current player for safe click.
     */
    private void playerSafeClick() {
        if (player1Turn) {
            player1Time = Math.min(10.0, player1Time + 0.5); // Cap at 10s
        } else {
            player2Time = Math.min(10.0, player2Time + 0.5); // Cap at 10s
        }
    }

    /**
     * Sets Player 1's name. Defaults to "Player 1" if empty string provided.
     * 
     * @param name Player 1's display name
     */
    public void setPlayer1Name(String name) { 
        this.player1Name = name.isEmpty() ? "Player 1" : name; 
    }

    /**
     * Sets Player 2's name. Defaults to "Player 2" if empty string provided.
     * 
     * @param name Player 2's display name
     */
    public void setPlayer2Name(String name) { 
        this.player2Name = name.isEmpty() ? "Player 2" : name; 
    }

    /**
     * Registers a player click and manages turn switching (2 clicks per turn).
     */
    private void registerClick() {
        clicksThisTurn++;
        if (clicksThisTurn >= 2) {
            clicksThisTurn = 0;
            switchTurn();
        }
    }

    /**
     * Switches turn between Player 1 and Player 2. Updates UI.
     */
    private void switchTurn() {
        player1Turn = !player1Turn;
        controller.updateUI();
    }

    /**
     * Checks if game is over (either player out of lives).
     * 
     * @return true if game over, false otherwise
     */
    public boolean isGameOver() {
        return player1Lives <= 0 || player2Lives <= 0;
    }

    /**
     * Ends the game and notifies controller with final scores.
     * 
     * @param teamWin true if all safe tiles revealed (win), false if lives depleted (loss)
     */
    public void endGame(boolean teamWin) {
        controller.endGame(player1Name, player1Time, player2Name, player2Time, teamWin);
    }

    /**
     * Counts adjacent mines for a given tile (used for numbering).
     * 
     * @param row tile row
     * @param col tile column
     * @return number of adjacent mines (0-8)
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

    /**
     * Sets the visual size of tiles.
     * 
     * @param size tile size in pixels
     */
    public void setTileSize(double size) {
        this.tileSize = size;
    }

    /**
     * Sets the board dimensions and reallocates the tile array.
     * 
     * @param size new board size (square grid)
     */
    public void setBoardSize(int size) {
        this.size = size;
        this.board = new Tile[size][size];
    }
    
    /** @return true if it's Player 1's turn */
    public boolean isPlayer1Turn() { return player1Turn; }
    
    /** @return current player's name */
    public String getCurrentPlayerName() { return player1Turn ? player1Name : player2Name; }
    
    /** @return Player 1's current time */
    public double getPlayer1Time() { return player1Time; }
    
    /** @return Player 2's current time */
    public double getPlayer2Time() { return player2Time; }
    
    /**
     * Gets tile at specified coordinates.
     * 
     * @param r row (0 to size-1)
     * @param c column (0 to size-1)
     * @return Tile at position (r,c)
     */
    public Tile getTile(int r, int c) { return board[r][c]; }
    
    /** @return board size */
    public int getSize() { return size; }
    
    /**
     * Sets Player 1's time (clamped to >= 0).
     * 
     * @param time new time value
     */
    public void setPlayer1Time(double time) { 
        this.player1Time = Math.max(0, time); // Prevent negative time
    }
    
    /**
     * Sets Player 2's time (clamped to >= 0).
     * 
     * @param time new time value
     */
    public void setPlayer2Time(double time) { 
        this.player2Time = Math.max(0, time); // Prevent negative time
    }
}
