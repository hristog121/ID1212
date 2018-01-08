package com.tictactoe.communication.message;

/**
 * A message sent when the handshake is done. Properties are the game id and the player letter.
 */
public class HandshakeMessage extends OutboundMessage {
	private int gameId;
	private String player;

	public HandshakeMessage(int gameId, String player) {
		super("handshake");
		this.gameId = gameId;
		this.player = player;
	}

	public int getGameId() {
		return gameId;
	}

	public String getPlayer() {
		return player;
	}
}
