package com.tictactoe.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a controller of Tic Tac Toe. Contains players and a controller board.
 * 
 */
public class Game {
	
	private static int GAME_COUNT = 0;
	
	public enum Status {
	    WAITING, IN_PROGRESS, WON, TIED
	}
	
	public enum PlayerLetter { 
		X, O 
	}
	
	// The controller ID. The server increments this count with each new controller initiated.
	private final int id;
	
	// Status of the current controller (WAITING, IN_PROGRESS, FINISHED)
	private Status status;
	
	private final GameBoard board;	
	private Map<PlayerLetter, Player> players;
	private PlayerLetter winner;

	public Game() {
		this.id = GAME_COUNT++;
		this.board = new GameBoard();
		status = Status.WAITING;
		players = new EnumMap<PlayerLetter, Player>(PlayerLetter.class);
	}
	
	/**
	 * Adds a player to this controller. Changes status of controller from WAITING to IN_PROGRESS if the controller fills up.
	 * 
	 * @param p
	 * @return
	 * @throws RuntimeException if there are already 2 or more players assigned to this controller.
	 */
	public PlayerLetter addPlayer(Player p) {
		if (players.size() >= 2) {
			throw new RuntimeException("Too many players. Cannot add more than 1 player to a controller.");
		}
		
		PlayerLetter playerLetter = (players.containsKey(PlayerLetter.X)) ? PlayerLetter.O : PlayerLetter.X;
		p.setLetter(playerLetter);
		players.put(playerLetter, p);
		
		if (players.size() == 2) {
			status = Status.IN_PROGRESS;
		}		
		return playerLetter;
	}
	
	/**
	 * Marks the selected cell of the user and updates the controller's status.
	 * 
	 * @param gridId
	 * @param playerLetter
	 */
	public void markCell(int gridId, PlayerLetter playerLetter) {
		board.markCell(gridId, playerLetter);
		setStatus(playerLetter);
	}
	
	/**
	 * Updates the status of the controller. Invoked after each player's turn.
	 * 
	 * @param playerLetter
	 */
	private void setStatus(PlayerLetter playerLetter) {		
		// Checks first to see if the board has a winner.
		if (board.isWinner(playerLetter)) {
			status = Status.WON;
			
			if (playerLetter == PlayerLetter.X) {
				winner = PlayerLetter.X;
			} else {
				winner = PlayerLetter.O;
			}
		// Next check to see if the controller has been tied.
		} else if (board.isTied()) {
			status = Status.TIED;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Collection<Player> getPlayers() {
		return players.values();
	}
	
	public Player getPlayer(PlayerLetter playerLetter) {
		return players.get(playerLetter);
	}
	
	/**
	 * Returns the opponent given a player letter.
	 */
	public Player getOpponent(String currentPlayer) {
		PlayerLetter currentPlayerLetter = PlayerLetter.valueOf(currentPlayer);
		PlayerLetter opponentPlayerLetter = currentPlayerLetter.equals(PlayerLetter.X) ? PlayerLetter.O : PlayerLetter.X;
		return players.get(opponentPlayerLetter);
	}	
	
	public GameBoard getBoard() {
		return board;
	}
	
	public Status getStatus() {
		return status;
	}	
	
	public PlayerLetter getWinner() {
		return winner;
	}
	
	/**
	 * Convenience method to determine if a specific player is the winner.
	 */
	public boolean isPlayerWinner(PlayerLetter playerLetter) {
		return status == Status.WON && winner == playerLetter;
	}

	/**
	 * Convenience method to determine if the controller has been tied.
	 */	
	public boolean isTied() {
		return status == Status.TIED;
	}
}
