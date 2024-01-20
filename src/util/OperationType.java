package src.util;

public enum OperationType {
    OPEN("open"),
    CLOSE("close"),
    READ("read"),
    WRITE("write");

    private final String commandName;

    OperationType(String commandName) {
        this.commandName = commandName;
    }

    public static String getCommandName(OperationType operationType) {
        return operationType.commandName;
    }
}
