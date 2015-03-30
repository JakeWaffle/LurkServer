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
            char[] msg = new char[4096];
            try {
               _reader.read(msg);
            } catch (IOException e) {
                _logger.error("MailBox was interrupted probably so it could join its thread.");
                break;
            }

            String message = new String(msg).replaceAll("\0", "");

            _logger.debug("New message: "+message);
        }
    }

    public synchronized void stopReceiving() {
        _done = true;
    }
}
