package com.lcsc.cs.lurkserver;

import com.beust.jcommander.Parameter;
import com.lcsc.cs.lurkserver.game.Room;
import org.eclipse.jetty.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 3/26/2015.
 * This uses JCommander and a commandline interface to be populated.
 * This defines the information needed for the server.
 */
public class Settings {
    private static final Logger _logger = LoggerFactory.getLogger(Settings.class);

    @Parameter(names = "-help", help = true)
    public boolean help;

    @Parameter(names = "-port", required = true, description = "Port the server listens on.")
    public Integer port = 1;
    
    @Parameter(names = "-game_file", required = true,
            description = "This is the game's file relative to the ./data/games/ directory. " +
                    "It defines the game that will be loaded. It's assumed that the game file" +
                    " will follow a particular json format.")
    public String gameFile;

    /**
     * This will load the game file for use later. It a
     * @return A Map<String, Object> that defines the game. Or null will be returned if there was a problem.
     */
    public Map<String, Object> loadGameFile() {
        String projRoot = new File("").getAbsolutePath();
        FileReader reader = null;

        Map<String, Object> gameDef = null;

        try {
            File gameDataDir        = new File(projRoot, "data/games/"+gameFile);
            reader                  = new FileReader(gameDataDir);

            gameDef = (Map<String, Object>) JSON.parse(reader);
        } catch (FileNotFoundException e) {
            _logger.error("Problem loading the game definition file", e);
            System.exit(1);
        } catch (IOException e) {
            _logger.error("Problem loading the game definition file", e);
            System.exit(1);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {}
        }
        return gameDef;
    }
}
