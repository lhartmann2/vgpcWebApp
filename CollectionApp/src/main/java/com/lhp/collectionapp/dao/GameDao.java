package com.lhp.collectionapp.dao;

import com.lhp.collectionapp.entity.Game;

import java.util.List;

public interface GameDao {

    void addGame(Game game);
    Game getGameById(int id);
    Game getGameByTitle(String productName);
    void updateGame(Game game);
    void deleteGameById(int id);
    void deleteGameByTitle(String productName);

    List<Game> getAllGames();
    List<Game> getGamesByConsole(String consoleName);
}
