package com.lcsc.cs.lurkserver.Protocol;

/**
 * Created by Jake on 3/3/2015.
 */
public enum ActionType {
    CHANGE_ROOM("CHROM"),
    FIGHT("FIGHT"),
    MESSAGE("MESSG");

    private final String actionName;

    private ActionType(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return this.actionName;
    }
};