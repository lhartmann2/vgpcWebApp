package com.lhp.collectionapp.dao;

import com.lhp.collectionapp.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GameDaoDB implements GameDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public void addGame(Game game) {
        final String INSERT_GAME = "INSERT INTO game(id, productName, consoleName, releaseDate, isNew, " +
                "haveGame, haveBox, haveManual, totalValue) VALUES(?,?,?,?,?,?,?,?,?)";
        jdbc.update(INSERT_GAME,
                game.getId(),
                game.getProductName(),
                game.getConsoleName(),
                game.getReleaseDate(),
                game.isNew(),
                game.isHaveGame(),
                game.isHaveBox(),
                game.isHaveManual(),
                game.getTotalValue());
    }

    @Override
    public Game getGameById(int id) {
        try {
            final String GET_GAME_BY_ID = "SELECT * FROM game WHERE id = ?";
            return jdbc.queryForObject(GET_GAME_BY_ID, new GameMapper(), id);
        } catch(DataAccessException ex) {
            return null;
        }
    }

    @Override
    public Game getGameByTitle(String productName) {
        try {
            final String GET_GAME_BY_TITLE = "SELECT * FROM game WHERE productName LIKE ?";
            return jdbc.queryForObject(GET_GAME_BY_TITLE, new GameMapper(), productName);
        } catch(DataAccessException ex) {
            return null;
        }
    }

    @Override
    public void updateGame(Game game) {
        final String UPDATE_GAME = "UPDATE game SET productName = ?, consoleName = ?, releaseDate = ?, " +
                "isNew = ?, haveGame = ?, haveBox = ?, haveManual = ?, totalValue = ? WHERE id = ?";
        jdbc.update(UPDATE_GAME,
                game.getProductName(),
                game.getConsoleName(),
                game.getReleaseDate(),
                game.isNew(),
                game.isHaveGame(),
                game.isHaveBox(),
                game.isHaveManual(),
                game.getTotalValue(),
                game.getId());
    }

    @Override
    @Transactional
    public void deleteGameById(int id) {
        final String DELETE_PRICE = "DELETE FROM price WHERE gameId = ?";
        jdbc.update(DELETE_PRICE, id);

        final String DELETE_GAME = "DELETE FROM game WHERE id = ?";
        jdbc.update(DELETE_GAME, id);
    }

    @Override
    @Transactional
    public void deleteGameByTitle(String productName) {
        Game game = getGameByTitle(productName);
        if(game != null) {
            int id = game.getId();

            final String DELETE_PRICE = "DELETE FROM price where gameId = ?";
            jdbc.update(DELETE_PRICE, id);

            final String DELETE_GAME = "DELETE FROM game WHERE id = ?";
            jdbc.update(DELETE_GAME, id);
        }
    }

    @Override
    public List<Game> getAllGames() {
        final String SELECT_ALL_GAMES = "SELECT * FROM game";
        return jdbc.query(SELECT_ALL_GAMES, new GameMapper());
    }

    @Override
    public List<Game> getGamesByConsole(String consoleName) {
        final String SELECT_GAMES_BY_CONSOLE = "SELECT * FROM game WHERE consoleName LIKE ?";
        return jdbc.query(SELECT_GAMES_BY_CONSOLE, new GameMapper());
    }

    public static final class GameMapper implements RowMapper<Game> {
        @Override
        public Game mapRow(ResultSet rs, int i) throws SQLException {
            Game game = new Game();
            game.setId(rs.getInt("id"));
            game.setProductName(rs.getString("productName"));
            game.setConsoleName(rs.getString("consoleName"));
            game.setReleaseDate(rs.getString("releaseDate"));
            game.setNew(rs.getBoolean("isNew"));
            game.setHaveGame(rs.getBoolean("haveGame"));
            game.setHaveBox(rs.getBoolean("haveBox"));
            game.setHaveManual(rs.getBoolean("haveManual"));
            game.setTotalValue(rs.getInt("totalValue"));
            return game;
        }
    }
}
