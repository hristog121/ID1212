package com.tictactoe.controller;

import com.tictactoe.communication.TextWebSocketFrameWriter;
import com.tictactoe.communication.message.*;
import com.tictactoe.model.Game;
import com.tictactoe.model.Player;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Map;

import static com.tictactoe.communication.message.GameOverMessage.Result.TIED;
import static com.tictactoe.communication.message.GameOverMessage.Result.YOU_WIN;
import static com.tictactoe.communication.message.TurnMessage.Turn.WAITING;
import static com.tictactoe.communication.message.TurnMessage.Turn.YOUR_TURN;

/**
 * A controller that will handle user requests:
 * new game initialization or adding a player to an already existing game
 * closing a game
 * handling user's move (selected field in the grid)
 */
public class GameController {
    public static final AttributeKey<Integer> GAME_ID = AttributeKey.valueOf("gameid");
    public static final AttributeKey<Game.PlayerLetter> PLAYER_LETTER = AttributeKey.valueOf("playerletter");

    private final Map<Integer, Game> games = new HashMap();

    /**
     * Initializes a controller. Finds an open controller for a player (if another player is already waiting) or creates a new controller.
     *
     * @param textWebSocketFrameWriter
     */
    public Player initGame(TextWebSocketFrameWriter textWebSocketFrameWriter) {
        // Try to find a controller waiting for a player. If one doesn't exist, create a new one.
        Game game = findGame();

        // Create a new instance of player and assign their channel for WebSocket communications.
        Player player = new Player(textWebSocketFrameWriter, game.getId());

        // Add the player to the controller.
        Game.PlayerLetter letter = game.addPlayer(player);

        // Add the controller to the collection of games.
        games.put(game.getId(), game);

        // Send confirmation message to player with controller ID and their assigned letter (X or O)
        player.getMessageWriter().write(new HandshakeMessage(game.getId(), letter.toString()).toJson());

        // If the controller has begun we need to inform the players. Send them a "turn" message (either "waiting" or "your_turn")
        System.out.println("Game status " + game.getStatus() + " " + games.size());
        if (game.getStatus() == Game.Status.IN_PROGRESS) {
            game.getPlayer(Game.PlayerLetter.X).getMessageWriter().write(new TurnMessage(YOUR_TURN).toJson());
            game.getPlayer(Game.PlayerLetter.O).getMessageWriter().write(new TurnMessage(WAITING).toJson());
        }

        return player;
    }

    public void closeGame(int gameId, Game.PlayerLetter playerLetter) {
        Game game = findGameById(gameId);

        if (game == null) {
            return;
        }

        if (game.getStatus() == Game.Status.IN_PROGRESS) {
            game.getOpponent(playerLetter.name()).getMessageWriter().write(new OpponentResignedMessage().toJson());
        }

        games.remove(gameId);
    }

    public void handleMove(SelectedPositionMessage message){
        // Find the controller by its id.
        Game game = findGameById(message.getGameId());

        if (game == null) {
            return;
        }

        // Get payers.
        Player opponent = game.getOpponent(message.getPlayer());
        Player player = game.getPlayer(Game.PlayerLetter.valueOf(message.getPlayer()));

        // Mark the cell the player selected.
        game.markCell(message.getGridIdAsInt(), player.getLetter());

        // Get the status for the current controller.
        boolean winner = game.isPlayerWinner(player.getLetter());
        boolean tied = game.isTied();

        // Respond to the opponent in order to update their screen.
        String responseToOpponent = new CurrentStatusMessage(player.getLetter().toString(), message.getGridId(), winner, tied).toJson();
        opponent.getMessageWriter().write(responseToOpponent);

        // Respond to the player to let them know they won.
        if (winner) {
            player.getMessageWriter().write(new GameOverMessage(YOU_WIN).toJson());
        } else if (tied) {
            player.getMessageWriter().write(new GameOverMessage(TIED).toJson());
        }
    }

    /**
     * Finds an open controller for a player (if another player is waiting) or creates a new controller.
     *
     * @return Game
     */
    private Game findGame() {
        // Find an existing controller and return it
        for (Game g : games.values()) {
            if (g.getStatus().equals(Game.Status.WAITING)) {
                return g;
            }
        }
        // Or return a new controller
        return new Game();
    }

    private Game findGameById(int gameId) {
        // Find an existing game by id and return it
        return games.get(gameId);
    }
}
