package common;

public enum Command {
    STOP("!"),
    START_NEW_GAME("1"),
    START("start"),
    INPUT_WORD_LETTER;

    private final String representation;

    Command() {
        this.representation = "";
    }

    Command(final String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static Command getFromRepresentation(final String representation) {
        for (final Command command : Command.values()) {
            if (command.getRepresentation().equals(representation)) {
                return command;
            }
        }
        return INPUT_WORD_LETTER;
    }
}
