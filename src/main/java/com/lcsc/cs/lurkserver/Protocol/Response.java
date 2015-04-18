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
        if (type == ResponseType.INFORM) {
            //TODO Add in byte length for INFOM.
        }
        this.type       = type;
        this.message = message.trim();
    }

    public String getType() {
        return type.toString();
    }

    public String getResponse() {
        return message;
    }

    public String buildMessage() {
        String message = type.getType();
        message += " "+this.message;
        return message;
    }

    public String toString(){
        return String.format(String.format("Response Type: %s\nResponse:%s", type.toString(), message));
    }

    public byte[] toBytes() {
        return buildMessage().getBytes();
    }
}