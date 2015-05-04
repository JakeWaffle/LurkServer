package com.lcsc.cs.lurkserver.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jake on 3/7/2015.
 * This class will take in messages from the client and will deliver them to the Concierge.
 */
public class MailBox extends Thread {
    private static final Logger                         _logger = LoggerFactory.getLogger(MailBox.class);

    private              boolean                        _done;
    private              BlockingQueue<List<Command>>   _commandQueue;
    private              BufferedReader                 _reader;

    public MailBox(BlockingQueue<List<Command>> commandQueue, BufferedReader reader) {
        _done = false;
        _commandQueue = commandQueue;
        _reader = reader;
    }

    public void run() {
        String incompleteMsg = "";
        while (!_done) {
            char[] msg = new char[1048576];
            try {
               _reader.read(msg);
            } catch (IOException e) {
                _logger.error("MailBox was interrupted probably so it could join its thread.");
                break;
            }

            String message = new String(msg).replaceAll("\0", "");

            //If we have an incomplete message laying around we need to use it!
            if (incompleteMsg.length() > 0) {
                message         = incompleteMsg + message;
                incompleteMsg   = "";
            }

            List<Command> commands = new ArrayList<Command>();

            String headerPattern = CommandType.getCommandTypePattern() + "|" + ExtensionType.getExtensionTypePattern();

            //This just searched for the different Response headers that are possible.
            Pattern pattern = Pattern.compile(headerPattern);
            Matcher matcher = pattern.matcher(message);

            //We need to match the first item to get an idea of where we're starting and which response type we're
            // starting with.
            if (matcher.find()) {
                String header           = matcher.group();
                CommandType type        = CommandType.fromString(header);
                ExtensionType extType   = null;
                if (type == null)
                    extType             = ExtensionType.fromString(header);

                int start           = matcher.end();
                int end             = -1;
                if (matcher.find()) {
                    do {
                        end             = matcher.start();

                        if (message.length() > start+1 && (start+1) >= 0 &&
                                message.length() > end && (end) >= 0 &&
                                start+1 < end) {
                            Command newCmd = constructCommand(type, extType, message.substring(start + 1, end));
                            if (newCmd != null)
                                commands.add(newCmd);
                        }
                        else {
                            Command newCmd = constructCommand(type, extType, "");
                            if (newCmd != null)
                                commands.add(newCmd);
                        }
                        header          = matcher.group();
                        type            = CommandType.fromString(header);
                        extType   = null;
                        if (type == null)
                            extType             = ExtensionType.fromString(header);

                        start           = matcher.end();
                    } while (matcher.find());
                }
                if (message.length() > start+1 && (start+1) >= 0){
                    Command newCmd = constructCommand(type, extType, message.substring(start + 1));
                    if (newCmd != null)
                        commands.add(newCmd);
                }
                //In this case only a header was sent from the user.
                else {
                    Command newCmd = constructCommand(type, extType, "");
                    if (newCmd != null)
                        commands.add(newCmd);
                }
            }
            else if (message.length() == 0) {
                commands.add(constructCommand(CommandType.LEAVE, null, ""));
            }
            else {
                _logger.error("Message doesn't have any valid headers for some reason: "+message);
            }

            if (commands.size() > 0)
                _commandQueue.add(commands);
        }
    }

    /**
     * This will take in the information sent by the user and will construct a Command object that
     * will make the rest of the program easier to deal with.
     * @param type This is the first header that was given by the user.
     * @param extType In cases when an extension is sent, the type will be null and
     *                extType will equal the extension that was used.
     * @param body This is the body following the header (this may be an empty string.)
     * @return A Command object that was constructed or null if the user's input was invalid.
     */
    private Command constructCommand(CommandType type, ExtensionType extType, String body) {
        Command cmd     = null;
        body            = body.trim();

        if (type == CommandType.LEAVE) {
            cmd     = new Command(type);
            _done   = true;
        }
        else if (type == CommandType.ACTION) {
            ActionType aType;
            if (body.length() > 5) {
                aType = ActionType.fromString(body.substring(0, 5));
                if (body.length() > 6)
                    body = body.substring(6);
                else
                    body = "";
            }
            else
                aType = ActionType.fromString(body.substring(0));
            if (aType == ActionType.CHANGE_ROOM ||
                    aType == ActionType.MESSAGE) {
                cmd             = new Command(type, aType, body);
            }
            else if (aType == ActionType.FIGHT)
                cmd             = new Command(type, aType);
        }
        else if (type == CommandType.CONNECT ||
                type == CommandType.SET_ATTACK_STAT ||
                type == CommandType.SET_DEFENSE_STAT ||
                type == CommandType.SET_PLAYER_DESC ||
                type == CommandType.SET_REGEN_STAT) {
            cmd     = new Command(type, body);
        }
        else if (type == CommandType.QUERY ||
                type == CommandType.START) {
            cmd     = new Command(type);
        }
        else if (extType == ExtensionType.PICK_UP ||
                extType == ExtensionType.UNLOCK)
            cmd     = new Command(extType, body);



        if (cmd != null)
            _logger.info("Received Command:\n" + cmd.toString());
        else if (type != null)
            _logger.error("Received Command is null: Type: "+type.getCommandHeader()+" Body: "+body);
        else if (extType != null)
            _logger.error("Received Extension is null: Type: "+ extType.extensionHeader+" Param: "+body);
        else
            _logger.error("Neither a CommandType or ExtensionType was found.");

        return cmd;
    }

    public synchronized void stopReceiving() {
        _done = true;
    }
}
