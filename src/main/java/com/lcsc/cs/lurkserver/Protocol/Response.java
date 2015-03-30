package com.lcsc.cs.lurkserver.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jake on 3/3/2015.
 * This response will be what is sent back to the client. This structure just makes the response easier to
 * use until they are sent to the client.
 */
public class Response {
    private static final Logger     _logger = LoggerFactory.getLogger(Response.class);
    public  final ResponseType      type;
    public  final String            message;

    public Response(ResponseType type, String message) {
        if (type == ResponseType.INFORM){
            //The unneeded message length after the header needs to be replaced since this is
            //an INFOM message.
            message = message.replaceFirst("[0-9]+", "");

            if (message.contains("GameDescription:"))
                this.type = ResponseType.QUERY_INFORM;
            else if (message.contains("Location:"))
                this.type = ResponseType.PLAYER_INFORM;
            else if (message.contains("Connection:") || message.contains("Monster:"))
                this.type = ResponseType.ROOM_INFORM;
            else if (message.contains("Name:"))
                this.type = ResponseType.MONSTER_INFORM;
            else
                this.type = ResponseType.INVALID;
        }
        else
            this.type       = type;

        this.message = message.trim();
    }

    public String getType() {
        return type.toString();
    }

    public String getResponse() {
        return message;
    }

    private String buildMessage() {
        String message = "adsfadsf";
        return message;
    }

    public String toString(){
        return String.format(String.format("Response Type: %s\nResponse:%s", type.toString(), message));
    }

    public byte[] toBytes() {
        return buildMessage().getBytes();
    }
}