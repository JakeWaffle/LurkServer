package com.lcsc.cs.lurkserver;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.lcsc.cs.lurkserver.server.Server;
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
            }
        } catch(ParameterException e) {
            jc.usage();
        }
    }


}
