package com.lcsc.cs.lurkserver.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jake on 2/26/2015.
 */
public class Command {
    private static final Logger _logger = LoggerFactory.getLogger(Command.class);

    public  final   CommandType type;
    private         ActionType  _actionType = null;
    private         String      _body = null;

    private         ExtensionType      _extension;

    public Command(ExtensionType extension, String parameter) {
        type = CommandType.EXTENSION;
        _extension          = extension;
        _body               = parameter;
    }

    public Command(CommandType ctype, ActionType atype, String body) {
        type = ctype;
        _actionType = atype;
        _body       = body;
    }

    public Command(CommandType ctype, ActionType atype) {
        type = ctype;
        _actionType = atype;
    }

    public Command(CommandType type, String body) {
        this.type = type;
        _body       = body;
    }

    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getCommandType() {
        return type;
    }

    private String buildMessage() {
        String message;

        if (type == CommandType.EXTENSION) {
            message = _extension.getExtensionHeader();
            if (_body != null) {
                message += " " + _body;
            }
        }
        else {
            message = type.getCommandHeader();

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
        return buildMessage().getBytes();
    }

    public String toString() {
        return buildMessage();
    }
}