package com.lcsc.cs.lurkserver.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class  Room {
    private List<String>    _connections;
    private List<Monster>   _monsters;

    public Room(String roomName, Map<String, Object> roomData) {
        _connections            = new ArrayList<String>();
        Object[] connections    = (Object[])roomData.get("connections");

        for (Object connection : connections) {
            _connections.add((String)connection);
        }

        _monsters               = new ArrayList<Monster>();
        Object[] monsters       = (Object[])roomData.get("monsters");

        for (Object monsterObj : monsters) {
            Map<String, Object> monsterData = (Map<String, Object>)monsterObj;
            _monsters.add(new Monster(monsterData));
        }
    }
}