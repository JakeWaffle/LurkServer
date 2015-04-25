package com.lcsc.cs.lurkserver;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.lcsc.cs.lurkserver.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jake on 3/26/2015.
 */
public class Main {
    private static final Logger _logger      = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Settings settings = new Settings();
        JCommander jc = new JCommander(settings);

        try {
            jc.parse(args);

            if (settings.help) {
                jc.usage();
            }
            else {
                Server server = new Server();
                server.configureServer(settings);
                server.start();
                _logger.info(String.format("Server is listening on port %d!", settings.port));

                boolean done = false;
                BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in) );
                while (!done) {
                    System.out.println("Enter 'quit' to shutdown server:");
                    String input = dataIn.readLine();
                    if (input.trim().equals("quit"))
                        done = true;
                }
                server.stopServer();
                try {
                
                    server.join();
                    _logger.debug("Joined Server thread!");
                } catch (InterruptedException e) {
                    _logger.error("Interrupted when joining the Server's thread", e);
                }
            }
        } catch(ParameterException e) {
            jc.usage();
        } catch(IOException e) {
            _logger.error("Couldn't get input from the user.", e);
        }
    }
}
