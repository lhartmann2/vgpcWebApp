DROP DATABASE IF EXISTS vgpc;
CREATE DATABASE vgpc;

USE vgpc;


CREATE TABLE game(
	id INT PRIMARY KEY,
    productName VARCHAR(128) NOT NULL,
    consoleName VARCHAR(128) NOT NULL,
    releaseDate VARCHAR(10) DEFAULT NULL,
    isNew BOOLEAN NOT NULL,
    haveGame BOOLEAN NOT NULL,
    haveBox BOOLEAN NOT NULL,
    haveManual BOOLEAN NOT NULL,
    totalValue INT DEFAULT NULL
);

CREATE TABLE price(
	gameId INT NOT NULL,
    cibPrice INT NULL,
    loosePrice INT NULL,
    newPrice INT NULL,	
    PRIMARY KEY(gameId),
    FOREIGN KEY (gameId) REFERENCES game(id)
);
