package com.lcsc.cs.lurkserver;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jake on 3/26/2015.
 */
public class Main {
    private static final Logger _logger      = LoggerFactory.getLogger(Main.class);
    private ServerSocket _serverSocket;

    public static void main(String[] args) {
        Settings settings = new Settings();
        JCommander jc = new JCommander(settings);

        try {
            jc.parse(args);

            if (settings.help) {
                jc.usage();
            }
            else {
                Main main = new Main();
                main.configureServer(settings);
                main.start();
            }
        } catch(ParameterException e) {
            jc.usage();
        }
    }

    public void configureServer(Settings settings) {
        try {
            _serverSocket = new ServerSocket(settings.port);
        } catch (IOException e) {
            _logger.error("Couldn't start the server", e);
        }
    }

    public void start() {
        try {
            Socket server = _serverSocket.accept();
            System.out.println("Just connected to "
                    + server.getRemoteSocketAddress());
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while (true) {
                char[] buf = new char[1000];
                in.read(buf);

                String msg = new String(buf);
                System.out.println(msg.trim());
            }
        } catch(IOException e) {
            _logger.error("Something happened when dealing with clients", e);
        }
    }

    public void stop() {
        try {
            _serverSocket.close();
        } catch (IOException e) {
            _logger.error("Couldn't close server socket", e);
        }
    }
}
