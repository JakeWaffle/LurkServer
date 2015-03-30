package com.lcsc.cs.lurkserver.Protocol;

/**
 * Created by Jake on 3/3/2015.
 */
public enum CommandType {
    CONNECT("CNNCT"),
    SET_ATTACK_STAT("ATTCK"),
    SET_DEFENSE_STAT("DEFNS"),
    SET_REGEN_STAT("REGEN"),
    SET_PLAYER_DESC("DESCR"),
    LEAVE("LEAVE"),
    QUERY("QUERY"),
    START("START"),
    ACTION("ACTON"),
    EXTENSION("EXT");

    private final String commandName;

    private CommandType(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandHeader() {
        return this.commandName;
    }

    public static CommandType fromString(String commandName) {
        if (commandName != null) {
            for (CommandType c : CommandType.values()) {
                if (commandName.equalsIgnoreCase(c.getCommandHeader())) {
                    return c;
                }
            }
        }
        return null;
    }
};