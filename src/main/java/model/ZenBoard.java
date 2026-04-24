/**
 * Represents the Zen (single-player) game board for the MultiMine game.
 * 
 * <p>This class manages the board state, mine placement, tile interactions,
 * and win/lose conditions for Zen mode. The player has limited lives and
 * aims to reveal all safe tiles as quickly as possible.</p>
 * 
 * <p>Features include:
 * <ul>
 *     <li>Random mine placement</li>
 *     <li>Tile revealing and flagging</li>
 *     <li>Stopwatch tracking for completion time</li>
 *     <li>Win condition (all safe tiles revealed)</li>
 *     <li>Lose condition (3 mines hit)</li>
 * </ul>
 * </p>
 * 
 * @author Caleb Esteban
 * @version 1.0
 * @since 1.0
 */
public class ZenBoard {

    /** 2D array representing the board tiles */
    private Tile[][] board;

    /** Size of the board (NxN) */
    private int size;

    /** Images used for tile states */
    private Image hiddenTile;
    private Image flagTile;
    private Image bombTile;

    /** Images for numbered tiles (0–8 adjacent mines) */
    private Image[] numberTiles;

    /** JavaFX GridPane used to render the board */
    private GridPane grid;

    /** Controller responsible for updating the UI */
    private ZenController controller;

    /** Stopwatch used to track elapsed time */
    private Stopwatch stopwatch;

    /** Number of mines triggered by the player */
    private int minesCount = 0;

    /** Total number of safe tiles */
    private int safeTilesCount;

    /** Number of revealed safe tiles */
    private int revealedSafeTiles = 0;

    /** Size of each tile in pixels */
    private double tileSize;

    /**
     * Constructs a ZenBoard instance.
     *
     * @param size board size (NxN)
     * @param tileSize size of each tile in pixels
     * @param hiddenTile image for hidden tiles
     * @param flagTile image for flagged tiles
     * @param bombTile image for mines
     * @param numberTiles images for numbered tiles
     * @param grid GridPane used for rendering
     * @param controller controller handling UI updates
     * @param stopwatch stopwatch tracking elapsed time
     */
    public ZenBoard(int size, double tileSize,
                    Image hiddenTile, Image flagTile, Image bombTile,
                    Image[] numberTiles, GridPane grid,
                    ZenController controller,
                    Stopwatch stopwatch) {

        this.size = size;
        this.tileSize = tileSize;
        this.hiddenTile = hiddenTile;
        this.flagTile = flagTile;
        this.bombTile = bombTile;
        this.numberTiles = numberTiles;
        this.grid = grid;
        this.controller = controller;
        this.stopwatch = stopwatch;

        this.board = new Tile[size][size];
    }

    /**
     * Initializes the board by creating tiles and placing mines.
     *
     * @param minesToPlace number of mines to place
     */
    public void initializeBoard(int minesToPlace) {
        createBoard();
        safeTilesCount = (size * size) - minesToPlace;
        placeMines(minesToPlace);
    }

    /**
     * Creates the board UI and initializes all tiles.
     */
    private void createBoard() {
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
    }

    /**
     * Handles right-click actions (flagging/unflagging a tile).
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
     * Handles left-click actions (revealing a tile).
     *
     * @param row tile row
     * @param col tile column
     */
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
                controller.showZenSummary(tile.getView(), stopwatch.getFormattedTime(), false);
            }
        } else {
            int count = countAdjacentMines(row, col);
            tile.getView().setImage(numberTiles[count]);
            revealedSafeTiles++;

            if (revealedSafeTiles >= safeTilesCount) {
                stopwatch.stop();
                controller.showZenSummary(null, stopwatch.getFormattedTime(), true);
            }
        }

        tile.getView().setDisable(true);
    }

    /**
     * Counts the number of adjacent mines around a tile.
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

    /**
     * Randomly places mines on the board.
     *
     * @param minesToPlace number of mines to place
     */
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

    /**
     * Gets the total number of safe tiles.
     *
     * @return number of safe tiles
     */
    public int getSafeTilesCount() {
        return safeTilesCount;
    }

    /**
     * Retrieves a tile at a specific position.
     *
     * @param r row index
     * @param c column index
     * @return the Tile object
     */
    public Tile getTile(int r, int c) {
        return board[r][c];
    }

    /**
     * Gets the board size.
     *
     * @return board size
     */
    public int getSize() {
        return size;
    }
}
