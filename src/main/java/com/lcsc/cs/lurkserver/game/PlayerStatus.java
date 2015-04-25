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
}
