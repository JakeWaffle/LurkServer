package com.lcsc.cs.lurkserver.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Jake on 2/26/2015.
 * This mail man handles the sending of messages to the client and also the receiving of commands from the client.
 */
public class MailMan extends Thread {
    private static final    Logger                          _logger     = LoggerFactory.getLogger(MailMan.class);
    private                 boolean                         _done;
    private                 Socket                          _socket     = null;
    private                 OutputStream                    _out        = null;
    private                 BufferedReader                  _in         = null;
    private                 List<CommandListener>           _listeners  = new ArrayList<CommandListener>();
    private                 BlockingQueue<List<Command>>    _commandQueue;
    private                 MailBox                         _mailBox;

    public MailMan(Socket socket) {
        _done           = false;
        _commandQueue   = new ArrayBlockingQueue<List<Command>>(50);
        _socket         = socket;
        try {
            _out = new DataOutputStream(_socket.getOutputStream());
            _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            _mailBox = new MailBox(_commandQueue, _in);
            _mailBox.start();
        } catch (IOException e) {
            _logger.error("OutputStream/InputStream failed to initialize", e);
        }
    }

    //This method will be waiting for input from the server essentially.
    //Then it will send those messages back to all of the _listeners that are registered.
    public void run() {
        while (!_done) {
            while (_commandQueue.size() > 0) {
                try {
                    List<Command> commands = _commandQueue.take();
                    for (CommandListener listener : _listeners) {
                        listener.notify(commands);
                    }
                } catch(InterruptedException e) {
                    _logger.error("Interrupted while removing from the command queue.", e);
                }
            }

            try {
                Thread.sleep(500);
            } catch(InterruptedException e) {
                _logger.error("Interrupted while sleeping! I'm really mad!", e);
            }
        }
    }

    public synchronized void disconnect() {
        _done = true;
        if (_socket != null && _out != null && _in != null) {
            try {
                _mailBox.stopReceiving();
                _logger.debug("Intentionally closing the socket so the MailBox isn't blocking on a read anymore!");
                _socket.close();
                _out.close();
                _in.close();
                //The _mailBox won't join until it's _done with its read operation (which is why the socket and reader
                // are closed!)
                _mailBox.join();
                _logger.debug("Joined MailBox thread!");
            } catch (IOException e) {
                _logger.error("Couldn't close socket to server!", e);
            } catch(InterruptedException e) {
                _logger.error("Couldn't join the MailBox thread.", e);
            }
        }
    }

    public synchronized void registerListener(CommandListener listener) {
        _listeners.add(listener);
    }

    public synchronized void clearListeners() {
        _listeners.clear();
    }

    //This will send a message to the server
    public synchronized void sendMessage(Response response) {
        try {
            _logger.info("Sending Message to Client:\n" + response.buildMessage());
            _out.write(response.toBytes());
            Thread.sleep(100);
        } catch(IOException e) {
            _logger.error("Couldn't write to client", e);
        } catch(InterruptedException e) {
            _logger.error("Interrupted while sleeping! I'm really mad!", e);
        }
    }
}