package com.lcsc.cs.lurkserver.server;

/**
 * Created by Jake on 3/29/2015.
 * This state of the client says what they're doing and expecting.
 */
public enum ClientState {
    NOT_CONNECTED(),
    NOT_STARTED(),
    STARTED(),
    QUIT();

    private ClientState() {}
}
