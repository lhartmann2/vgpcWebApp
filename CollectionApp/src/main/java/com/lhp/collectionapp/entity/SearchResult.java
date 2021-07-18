package com.lhp.collectionapp.entity;

public class SearchResult {

    private int gameId;
    private String gameName;
    private String consoleName;
    private String releaseDate;

    public SearchResult(int gameId, String gameName, String consoleName, String releaseDate) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.consoleName = consoleName;
        this.releaseDate = releaseDate;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
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
}
