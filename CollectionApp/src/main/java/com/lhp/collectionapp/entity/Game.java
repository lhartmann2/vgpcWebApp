package com.lhp.collectionapp.entity;

import java.util.Date;

public class Game {

    private int id;
    private String productName;
    private String consoleName;
    private String releaseDate;
    private boolean isNew;
    private boolean haveGame;
    private boolean haveBox;
    private boolean haveManual;
    private int totalValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getConsoleName() {
        return consoleName;
    }

    public void setConsoleName(String consoleName) {
        this.consoleName = consoleName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isHaveGame() {
        return haveGame;
    }

    public void setHaveGame(boolean haveGame) {
        this.haveGame = haveGame;
    }

    public boolean isHaveBox() {
        return haveBox;
    }

    public void setHaveBox(boolean haveBox) {
        this.haveBox = haveBox;
    }

    public boolean isHaveManual() {
        return haveManual;
    }

    public void setHaveManual(boolean haveManual) {
        this.haveManual = haveManual;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    public boolean isComplete() {
        return ((isHaveBox()) && (isHaveGame()) && (isHaveManual()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (getId() != game.getId()) return false;
        if (isNew() != game.isNew()) return false;
        if (isHaveGame() != game.isHaveGame()) return false;
        if (isHaveBox() != game.isHaveBox()) return false;
        if (isHaveManual() != game.isHaveManual()) return false;
        if (!getProductName().equals(game.getProductName())) return false;
        if (!getConsoleName().equals(game.getConsoleName())) return false;
        return getReleaseDate() != null ? getReleaseDate().equals(game.getReleaseDate()) : game.getReleaseDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getProductName().hashCode();
        result = 31 * result + getConsoleName().hashCode();
        result = 31 * result + (getReleaseDate() != null ? getReleaseDate().hashCode() : 0);
        result = 31 * result + (isNew() ? 1 : 0);
        result = 31 * result + (isHaveGame() ? 1 : 0);
        result = 31 * result + (isHaveBox() ? 1 : 0);
        result = 31 * result + (isHaveManual() ? 1 : 0);
        return result;
    }
}
