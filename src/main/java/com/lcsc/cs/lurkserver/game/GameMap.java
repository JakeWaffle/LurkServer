package com.lcsc.cs.lurkserver.game;
import com.lcsc.cs.lurkserver.Protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import org.eclipse.jetty.util.ajax.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    private static final Logger _logger = LoggerFactory.getLogger(Map.class);

    private Map<String, Room> _rooms;
    private String            _startingRoom;
    
    public GameMap() {
        _rooms         = new HashMap<String, Room>();
        //This is the default room for when the map file didn't get loaded correctly.
        _startingRoom  = "Purgatory";
    }

    /**
     * This will make sure that each room in the map is updated.
     * @param secondsPassed This is how many seconds has passed since the last update.
     */
    public synchronized void update(int secondsPassed) {
        for (Room room : _rooms.values()) {
            room.update(secondsPassed);
        }
    }

    public synchronized String getStartingRoom() {
        return _startingRoom;
    }

    /**
     * This will put a new player into the starting room.
     * @param newPlayer This is a player that has just started the game.
     */
    public synchronized void spawnPlayer(Player newPlayer) {
        _rooms.get(_startingRoom).addPlayer(newPlayer);
    }

    /**
     * This will move a player from one room to another
     * @param player This is the player who is leaving one room to go to another.
     * @param newRoom This is the room that the player is going to.
     * @return This is the amount of gold that the player has collected.
     */
    public synchronized int changeRoom(Player player, String newRoom) {
        _rooms.get(player.currentRoom()).removePlayer(player.name);
        return _rooms.get(newRoom).addPlayer(player);
    }

    /**
     * Checks to see if the given rooms are connected.
     * @param roomA A room's name
     * @param roomB Another room's name
     * @return A boolean informing if the rooms are connected or not.
     */
    public synchronized boolean areRoomsConnected(String roomA, String roomB) {
        Room room = _rooms.get(roomA);
        return room.isConnected(roomB);
    }

    /**
     * This is meant to get a list of information about the room that will be sent back to the client. In addition
     * to the room's info it will include players and monsters in the room.
     * @param room This is the room that the info will be about.
     * @param excludedPlayerName This player will be excluded from the list of players in the room.
     * @return A list of strings that are meant to be sent to the client in separate INFOM messages.
     */
    public synchronized List<String> getRoomInfo(String room, String excludedPlayerName) {
        return _rooms.get(room).getRoomInfo(excludedPlayerName);
    }

    /**
     * This triggers monsters and players in a room to fight!
     * @param room All players and monsters will fight in the current room.
     */
    public synchronized void fightMonsters(String room) {
        _rooms.get(room).fight();
    }

    /**
     * This is called when a user uses the UNLCK extension to unlock something.
     * @param player This is the player that may have the key.
     * @param roomName This is the room that is being unlocked.
     * @return A response for the user saying if it was successful.
     */
    public synchronized Response unlockDoor(Player player, String roomName) {
        Room curRoom = _rooms.get(player.currentRoom());
        return curRoom.unlockDoor(player, roomName);
    }

    /**
     * Checks to see if a room is unlocked.
     * @param curRoomName The room the player is in.
     * @param otherRoomName The room the player is going to.
     * @return The key name that will unlock the door or null.
     */
    public synchronized String isDoorUnlocked(String curRoomName, String otherRoomName) {
        Room curRoom = _rooms.get(curRoomName);
        return curRoom.isDoorUnlocked(otherRoomName);
    }

    /**
     * This will have a player pickup a key that is in the room.
     * @param player The player picking up the key.
     * @param keyName The name of the key (found in the description of the room.)
     * @return A response for the user saying if it was successful.
     */
    public synchronized Response pickupKey(Player player, String keyName) {
        Room curRoom = _rooms.get(player.currentRoom());
        return curRoom.pickUpKey(player, keyName);
    }

    /**
     * This takes in a map that came from a json file. This map defines the game.
     * @param gameDef This map defines the game that has been loaded.
     */
    public void loadMap(Map<String, Object> gameDef) {
        if (gameDef.containsKey("starting_room") && gameDef.containsKey("rooms")) {
            _startingRoom = (String) gameDef.get("starting_room");

            //Each item maps a room name to some map of room data.
            Map<String, Object> rooms = (Map<String, Object>) gameDef.get("rooms");

            for (Map.Entry<String, Object> room : rooms.entrySet()) {
                Room newRoom = new Room(room.getKey(), (Map<String, Object>) room.getValue());
                _rooms.put(room.getKey(), newRoom);
            }
        }
        else {
            _logger.error("The 'starting_room' and/or the 'rooms' needs to be defined properly in the game definition file!");
            System.exit(1);
        }
    }
}

