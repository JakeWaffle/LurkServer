package com.lcsc.cs.lurkserver;

import com.beust.jcommander.Parameter;

/**
 * Created by Jake on 3/26/2015.
 * This uses JCommander and a commandline interface to be populated.
 * This defines the information needed for the server.
 */
public class Settings {
    @Parameter(names = "-help", help = true)
    public boolean help;

    @Parameter(names = "-port", required = true, description = "Port the server listens on.")
    public Integer port = 1;
    
    @Parameter(names = "-game_dir", required = true, description = "This is the game's data directory relative to the data directory. This allows separate games to exist and be selected.")
    public String gameDir;
}
