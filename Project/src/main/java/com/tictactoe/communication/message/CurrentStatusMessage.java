package com.tictactoe.communication.message;

/**
 * A message that we will send to the user when an opponent makes a move. The message properties are the opponent letter,
 * the id of the grid box that the user has clicked in, whether the opponent has won and whether the game is tied
 */
public class CurrentStatusMessage extends OutboundMessage {
	private String opponent;
	private String gridId;
	private boolean winner;
	private boolean tied;
	
	public CurrentStatusMessage(String opponent, String grid, boolean winner, boolean tied) {
		super("response");
		this.opponent = opponent;
		this.gridId = grid;
		this.winner = winner;
		this.tied = tied;
	}

	public String getOpponent() {
		return opponent;
	}

	public String getGridId() {
		return gridId;
	}

	public boolean isWinner() {
		return winner;
	}

	public boolean isTied() {
		return tied;
	}
}
