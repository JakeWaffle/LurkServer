package com.lcsc.cs.lurkserver.Protocol;

/**
 * Created by Jake on 3/3/2015.
 */
public enum ResponseHeader {
    ACCEPTED("ACEPT"),
    REJECTED("REJEC"),
    RESULT("RESLT"),
    INFORM("INFOM"),
    QUERY_INFORM("QINFO"),
    ROOM_INFORM("RINFO"),
    PLAYER_INFORM("PINFO"),
    MONSTER_INFORM("MINFO"),
    MESSAGE("MESSG"),
    NOTIFY("NOTIF"),
    INVALID("INVLD");

    private final String type;

    private ResponseHeader(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    /**
     * This will return a regex pattern that will find the response headers that the server supports.
     * @return A regex pattern to search for response headers with.
     */
    public static String getResponseTypePattern() {
        return "ACEPT|REJEC|RESLT|INFOM|MESSG|NOTIF";
    }

    public static ResponseHeader fromString(String type) {
        if (type != null) {
            for (ResponseHeader r : ResponseHeader.values()) {
                if (type.equalsIgnoreCase(r.getType())) {
                    return r;
                }
            }
        }
        return null;
    }
};