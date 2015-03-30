package com.lcsc.cs.lurkserver.server;

import com.lcsc.cs.lurkserver.Protocol.MailMan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Jake on 3/29/2015.
 * This class will listen to a client's messages and will respond to it with the respective information.
 * So this class will need to have access to the game's map, player list, monsters and pretty much every part of the
 * game.
 */
public class Concierge extends Thread {
    private static final Logger     _logger = LoggerFactory.getLogger(Concierge.class);
    private              MailMan    _mailMan;
    private              boolean    _done;

    public Concierge(Socket socket) {
        _logger.debug("Just connected to " + socket.getRemoteSocketAddress());
        _done       = false;
        _mailMan    = new MailMan(socket);
        _mailMan.start();
    }

    @Override
    public void run() {
        while(!_done) {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                _logger.error("Interrupted while sleeping! I'm really mad!", e);
            }
        }
    }

    public synchronized void dropClient() {
        _done = true;
        _logger.debug("Dropping client!");
        _mailMan.disconnect();
        try {
            this._mailMan.join();
            _logger.debug("Joined MailMan thread!");
        } catch (InterruptedException e) {
            _logger.error("Interrupted when joining the MailMan's thread", e);
        }
    }
}
