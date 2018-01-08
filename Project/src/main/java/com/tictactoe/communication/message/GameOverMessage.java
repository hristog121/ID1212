package com.tictactoe.communication.message;

/**
 * This message is sent when the game is over. The message is sent to the last playing player.
 * The result of the game can be either YOU_WIN - the last playing player has won or TIED - nobody wins and there are
 * no more empty places in the grid.
 */
public class GameOverMessage extends OutboundMessage {
	public enum Result {
		YOU_WIN, TIED
	}

	private Result result;
	
	public GameOverMessage(Result r) {
		super("game_over");
		this.result = r;
	}

	public Result getResult() {
		return result;
	}
}
