package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseHeader;

/**
 * Created by Jake on 4/24/2015.
 */
public enum ResponseMessageType {
    REPRISING_PLAYER(ResponseHeader.ACCEPTED, "Reprising Player"),
    NEW_PLAYER(ResponseHeader.ACCEPTED, "New Player"),
    NAME_TAKEN(ResponseHeader.REJECTED, "Name Already Taken"),
    INCORRECT_STATE(ResponseHeader.REJECTED, "Incorrect State"),
    FINE(ResponseHeader.ACCEPTED, "Fine"),
    STATS_TOO_HIGH(ResponseHeader.REJECTED, "Stats Too High"),
    NOT_READY(ResponseHeader.REJECTED, "Not Ready");

    private ResponseHeader _type;
    private String          _message;

    private ResponseMessageType(ResponseHeader type, String message) {
        _type       = type;
        _message    = message;
    }

    public Response getResponse() {
        return new Response(_type, _message);
    }
}
