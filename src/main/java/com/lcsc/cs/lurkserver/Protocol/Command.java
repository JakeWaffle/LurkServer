package com.lcsc.cs.lurkserver.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jake on 2/26/2015.
 */
public class Command {
    private static final Logger logger = LoggerFactory.getLogger(Command.class);

    private final   CommandType _type;
    private         ActionType  _actionType = null;
    private         String      _body = null;

    private         String      _extensionHeader;

    public Command(String extensionHeader, String parameter) {
        _type               = CommandType.EXTENSION;
        _extensionHeader    = extensionHeader;
        _body               = parameter;
    }

    public Command(CommandType ctype, ActionType atype, String body) {
        _type       = ctype;
        _actionType = atype;
        _body       = body;
    }

    public Command(CommandType ctype, ActionType atype) {
        _type       = ctype;
        _actionType = atype;
    }

    public Command(CommandType type, String body) {
        _type       = type;
        _body       = body;
    }

    public Command(CommandType type) {
        _type = type;
    }

    public CommandType getCommandType() {
        return _type;
    }

    private String buildMessage() {
        String message;

        if (_type == CommandType.EXTENSION) {
            message = _extensionHeader;
            if (_body != null) {
                message += " " + _body;
            }
        }
        else {
            message = _type.getCommandHeader();

            if (_actionType != null) {
                message += " " + _actionType.getActionName();
            }

            if (_body != null) {
                message += " " + _body;
            }
        }

        return message;
    }

    public byte[] toBytes() {
        return this.buildMessage().getBytes();
    }
    public String toString() {
        return this.buildMessage();
    }
}