package com.tictactoe.communication.message;

/**
 * A message that will be sent by the client when the user selects a field in the grid
 */
public class SelectedPositionMessage {
    private int gameId;
    private String player;
    private String gridId;

    public int getGameId() {
        return gameId;
    }

    public String getPlayer() {
        return player;
    }

    public String getGridId() {
        return gridId;
    }

    public int getGridIdAsInt() {
        return Integer.valueOf(gridId.substring(gridId.length() - 1));
    }
}
