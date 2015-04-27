package com.lcsc.cs.lurkserver.game;

/**
 * Created by Jake on 4/24/2015.
 */
public enum BeingStatus {
    ALIVE("ALIVE"),
    DEAD("DEAD");

    private String _status;

    private BeingStatus(String status) {_status = status;}

    public String getStatus() {
        return _status;
    }

    public static BeingStatus fromString(String status) {
        if (status != null) {
            for (BeingStatus c : BeingStatus.values()) {
                if (status.equalsIgnoreCase(c.getStatus())) {
                    return c;
                }
            }
        }
        return null;
    }
}
