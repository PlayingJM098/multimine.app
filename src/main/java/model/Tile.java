package model;

import javafx.scene.image.ImageView;

/**
 * Represents a single tile in a minesweeper-style game grid.
 * Each tile has a position (row, column), can contain a mine, 
 * can be revealed or flagged, and is associated with a JavaFX ImageView for rendering.
 * 
 * <p><strong>Tile States:</strong></p>
 * <ul>
 *   <li><strong>Mine:</strong> Contains a mine (set via {@link #setMine(boolean)})</li>
 *   <li><strong>Revealed:</strong> Tile has been revealed to the player</li>
 *   <li><strong>Flagged:</strong> Player has marked this tile as suspected mine</li>
 * </ul>
 * 
 * @author Rapha Domingo
 * @version 1.0
 */
public class Tile {

    /** The row position of this tile in the grid */
    private int row;
    
    /** The column position of this tile in the grid */
    private int col;
    
    /** True if this tile contains a mine */
    private boolean mine;
    
    /** True if this tile has been revealed to the player */
    private boolean revealed;
    
    /** The JavaFX ImageView used to render this tile */
    private ImageView view;
    
    /** True if player has flagged this tile as a suspected mine */
    private boolean flagged;

    /**
     * Constructs a new Tile at the specified grid position with the given ImageView.
     * Initially, the tile does not contain a mine and is not revealed.
     * 
     * @param row the row index (0-based) in the game grid
     * @param col the column index (0-based) in the game grid
     * @param view the ImageView used to display this tile
     */
    public Tile(int row, int col, ImageView view) {
        this.row = row;
        this.col = col;
        this.view = view;
        this.mine = false;
        this.revealed = false;
        this.flagged = false;
    }

    /**
     * Checks if this tile has been flagged by the player as a suspected mine.
     * 
     * @return true if flagged, false otherwise
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Sets the flagged state of this tile.
     * 
     * @param flagged true to flag as mine, false to unflag
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * Checks if this tile contains a mine.
     * 
     * @return true if this tile has a mine, false otherwise
     */
    public boolean hasMine() {
        return mine;
    }

    /**
     * Sets whether this tile contains a mine.
     * 
     * @param mine true if this tile contains a mine, false otherwise
     */
    public void setMine(boolean mine) {
        this.mine = mine;
    }

    /**
     * Checks if this tile has been revealed to the player.
     * 
     * @return true if revealed, false otherwise
     */
    public boolean isRevealed() {
        return revealed;
    }

    /**
     * Reveals this tile to the player (marks as revealed).
     * This is a one-way operation - once revealed, it cannot be unrevealed.
     */
    public void reveal() {
        revealed = true;
    }

    /**
     * Returns the ImageView associated with this tile for rendering.
     * 
     * @return the ImageView for this tile
     */
    public ImageView getView() {
        return view;
    }

    /**
     * Returns the row position of this tile in the game grid.
     * 
     * @return the 0-based row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column position of this tile in the game grid.
     * 
     * @return the 0-based column index
     */
    public int getCol() {
        return col;
    }
}
