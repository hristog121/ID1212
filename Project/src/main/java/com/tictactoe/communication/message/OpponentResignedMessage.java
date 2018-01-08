package com.tictactoe.communication.message;

/**
 * A message that we send when the other opponent exits the game and closes the connection
 */
public class OpponentResignedMessage extends OutboundMessage {
    public OpponentResignedMessage() {
        super("opponent_resigned");
    }
}
