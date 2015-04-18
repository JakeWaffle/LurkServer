package com.lcsc.cs.lurkserver.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jake on 2/26/2015.
 */
public class Command {
    private static final Logger _logger = LoggerFactory.getLogger(Command.class);

    public  final   CommandType type;
    public  final   ActionType  actionType;
    public  final   String parameter;

    private         ExtensionType      _extension;

    public Command(ExtensionType extension, String parameter) {
        type = CommandType.EXTENSION;
        _extension          = extension;
        this.parameter      = parameter;
        actionType          = null;
    }

    public Command(CommandType ctype, ActionType atype, String parameter) {
        type            = ctype;
        actionType      = atype;
        this.parameter  = parameter;
    }

    public Command(CommandType ctype, ActionType atype) {
        type        = ctype;
        actionType  = atype;
        parameter   = null;
    }

    public Command(CommandType type, String parameter) {
        this.type       = type;
        this.parameter  = parameter;
        actionType      = null;
    }

    public Command(CommandType type) {
        this.type   = type;
        parameter   = null;
        actionType  = null;
    }

    private String buildMessage() {
        String message;

        if (type == CommandType.EXTENSION) {
            message = _extension.getExtensionHeader();
            if (parameter != null) {
                message += " " + parameter;
            }
        }
        else {
            message = type.getCommandHeader();

            if (actionType != null) {
                message += " " + actionType.getActionName();
            }

            if (parameter != null) {
                message += " " + parameter;
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