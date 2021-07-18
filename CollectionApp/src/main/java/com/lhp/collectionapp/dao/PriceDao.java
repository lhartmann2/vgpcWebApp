package com.lhp.collectionapp.dao;

import com.lhp.collectionapp.entity.Price;

import java.util.List;

public interface PriceDao {

    void addPrice(Price price);
    Price getPriceByGameId(int gameId);
    void updatePrice(Price price);
    void deletePriceByGameId(int gameId);

    List<Price> getAllPrices();
}
