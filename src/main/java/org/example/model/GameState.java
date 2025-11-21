package org.example.model;

import org.example.service.BoardService;

public class GameState {
    private GamePhase gamePhase = GamePhase.EDIT;
    private BoardService boardService;
    private boolean humanPlaysX = false;
    private boolean loadedAGame = false;
    private boolean isXTurn = false;
    private boolean exitRequested = false;

    public GameState() {}

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }

    public BoardService getBoardService() {
        return boardService;
    }

    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }

    public boolean isHumanPlaysX() {
        return humanPlaysX;
    }

    public void setHumanPlaysX(boolean humanPlaysX) {
        this.humanPlaysX = humanPlaysX;
    }

    public boolean isLoadedAGame() {
        return loadedAGame;
    }

    public void setLoadedAGame(boolean loadedAGame) {
        this.loadedAGame = loadedAGame;
    }

    public boolean isXTurn() {
        return isXTurn;
    }

    public void setXTurn(boolean XTurn) {
        isXTurn = XTurn;
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    public void setExitRequested(boolean exitRequested) {
        this.exitRequested = exitRequested;
    }
}
