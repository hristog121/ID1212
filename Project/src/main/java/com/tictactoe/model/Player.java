package com.tictactoe.model;

import com.tictactoe.communication.MessageWriter;
import com.tictactoe.model.Game.PlayerLetter;
import com.tictactoe.communication.TextWebSocketFrameWriter;

/**
 * Represents a player for a controller of Tic Tac Toe.
 */
public class Player {
	
	// The player's message writer. Used for communications.
	private MessageWriter messageWriter;
	
	// The player's currently assigned letter.
	private PlayerLetter letter;

	// The player's game.
	private int gameId;

	public Player(MessageWriter messageWriter, int gameId) {
		this.messageWriter = messageWriter;
		this.gameId = gameId;
	}

	public MessageWriter getMessageWriter() {
		return messageWriter;
	}

	public PlayerLetter getLetter() {
		return letter;
	}

	public int getGameId() {
		return gameId;
	}

	public void setLetter(PlayerLetter letter) {
		this.letter = letter;
	}
}
