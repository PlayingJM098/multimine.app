package model;

import javafx.scene.image.ImageView;

public class Tile {

    private int row;
    private int col;
    private boolean mine;
    private boolean revealed;
    private ImageView view;

    public Tile(int row, int col, ImageView view) {
        this.row = row;
        this.col = col;
        this.view = view;
        this.mine = false;
        this.revealed = false;
    }

    public boolean hasMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void reveal() {
        revealed = true;
    }

    public ImageView getView() {
        return view;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
