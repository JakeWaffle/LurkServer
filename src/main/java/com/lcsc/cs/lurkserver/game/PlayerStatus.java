package com.lcsc.cs.lurkserver.game;

/**
 * Created by Jake on 4/24/2015.
 */
public enum PlayerStatus {
    ALIVE("ALIVE"),
    DEAD("DEAD");

    private String _status;

    private PlayerStatus(String status) {_status = status;}

    public String getStatus() {
        return _status;
    }

    public static PlayerStatus fromString(String status) {
        if (status != null) {
            for (PlayerStatus c : PlayerStatus.values()) {
                if (status.equalsIgnoreCase(c.getStatus())) {
                    return c;
                }
            }
        }
        return null;
    }
}
