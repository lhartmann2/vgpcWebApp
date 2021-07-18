package com.lhp.collectionapp.entity;

import java.text.DecimalFormat;

public class Price {

    private int gameId;
    private float cibPrice;
    private float newPrice;
    private float loosePrice;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public float getCibPrice() {
        return cibPrice;
    }

    public void setCibPrice(float cibPrice) {
        this.cibPrice = cibPrice;
    }

    public float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(float newPrice) {
        this.newPrice = newPrice;
    }

    public float getLoosePrice() {
        return loosePrice;
    }

    public void setLoosePrice(float loosePrice) {
        this.loosePrice = loosePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price = (Price) o;

        if (getGameId() != price.getGameId()) return false;
        if (Float.compare(price.getCibPrice(), getCibPrice()) != 0) return false;
        if (Float.compare(price.getNewPrice(), getNewPrice()) != 0) return false;
        return Float.compare(price.getLoosePrice(), getLoosePrice()) == 0;
    }

    @Override
    public int hashCode() {
        int result = getGameId();
        result = 31 * result + (getCibPrice() != +0.0f ? Float.floatToIntBits(getCibPrice()) : 0);
        result = 31 * result + (getNewPrice() != +0.0f ? Float.floatToIntBits(getNewPrice()) : 0);
        result = 31 * result + (getLoosePrice() != +0.0f ? Float.floatToIntBits(getLoosePrice()) : 0);
        return result;
    }
}
