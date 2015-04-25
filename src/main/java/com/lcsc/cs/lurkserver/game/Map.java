package com.lcsc.cs.lurkserver.game
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import org.eclipse.jetty.util.ajax.JSON;
import java.util.HashMap;
import java.util.Map;

public class Map {
    private static final Logger _logger = LoggerFactory.getLogger(Map.class);
    
    private Map<String, Room> _rooms;
    private String            _startingRoom;
    
    public Map() {
        _rooms         = new HashMap<String, Room>();
        _startingRoom  = "Purgatory";
    }

    /**
     * This will load a map for the game from a json file.
     * @param gameDir This is the directory (relative to the data
     *                directory) that contains the game description
     *                along with the map.
     */
    public void loadMap(String gameDir) {
        String projRoot = new File("").absolutePath();
        FileReader reader;
        
        try {
            File gameDir = new File(projRoot, "data/"+gameDir+"/map.mdef");
            reader       = new FileReader(gameDir);
            
            Map<String, Object> map = (Map<String, Object>)JSON.parse(reader);
            
            _startingRoom = (String)map.get("starting_room);
            
            //Each item maps a room name to some map of room data.
            Map<String, Object> rooms = (Map<String, Object>)map.get("rooms");
            
            for (Map.Entry<String, Object> room : rooms.entrySet()) {
                Room newRoom = new Room(room.key(), (Map<String, Object>)room.value());
                _rooms.put(room.key(), newRoom);
            }
        } catch (FileNotFoundException e) {
            _logger.error("Problem loading the player data file", e);
        } catch (IOException e) {
            _logger.error("Problem loading the player data file", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {}
        }
    }
}

