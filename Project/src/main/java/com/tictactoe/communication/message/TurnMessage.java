package com.tictactoe.communication.message;

/**
 * A message that we will send to notify the client whether it is this user's turn or not:
 * WAITING - it is other user's turn
 * YOUR_TURN - it is this user's turn
 */
public class TurnMessage extends OutboundMessage {
	
	public enum Turn {
		WAITING, YOUR_TURN
	}

	private Turn turn;

	public TurnMessage(Turn t) {
		super("turn");
		turn = t;
	}

	public Turn getTurn() {
		return turn;
	}
}
