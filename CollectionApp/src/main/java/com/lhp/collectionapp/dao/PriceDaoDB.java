package com.lhp.collectionapp.dao;

import com.lhp.collectionapp.entity.Price;
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
public class PriceDaoDB implements PriceDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public void addPrice(Price price) {
        final String INSERT_PRICE = "INSERT INTO price(gameId, cibPrice, loosePrice, newPrice) " +
                "VALUES(?,?,?,?)";
        jdbc.update(INSERT_PRICE,
                price.getGameId(),
                price.getCibPrice(),
                price.getLoosePrice(),
                price.getNewPrice());
    }

    @Override
    public Price getPriceByGameId(int gameId) {
        try {
            final String GET_PRICE_BY_ID = "SELECT * FROM price WHERE gameId = ?";
            return jdbc.queryForObject(GET_PRICE_BY_ID, new PriceMapper(), gameId);
        } catch(DataAccessException ex) {
            return null;
        }
    }

    @Override
    public void updatePrice(Price price) {
        final String UPDATE_PRICE = "UPDATE price SET cibPrice = ?, loosePrice = ?, newPrice = ? WHERE gameId = ?";
        jdbc.update(UPDATE_PRICE, price.getCibPrice(), price.getLoosePrice(), price.getNewPrice(), price.getGameId());
    }

    @Override
    @Transactional
    public void deletePriceByGameId(int gameId) {
        final String DELETE_GAME = "DELETE FROM game WHERE id = ?";
        jdbc.update(DELETE_GAME, gameId);

        final String DELETE_PRICE = "DELETE FROM price WHERE gameId = ?";
        jdbc.update(DELETE_PRICE, gameId);
    }

    @Override
    public List<Price> getAllPrices() {
        final String GET_ALL_PRICES = "SELECT * FROM price";
        return jdbc.query(GET_ALL_PRICES, new PriceMapper());
    }

    public static final class PriceMapper implements RowMapper<Price> {
        @Override
        public Price mapRow(ResultSet resultSet, int i) throws SQLException {
            Price price = new Price();
            price.setGameId(resultSet.getInt("gameId"));
            price.setCibPrice(resultSet.getInt("cibPrice"));
            price.setNewPrice(resultSet.getInt("newPrice"));
            price.setLoosePrice(resultSet.getInt("loosePrice"));
            return price;
        }
    }
}
