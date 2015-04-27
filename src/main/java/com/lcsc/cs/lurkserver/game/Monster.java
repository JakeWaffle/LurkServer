package com.lcsc.cs.lurkserver.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Jake on 4/26/2015.
 */
public class Monster {
    private static final Logger _logger         = LoggerFactory.getLogger(Monster.class);
    private final   String      _name;
    private         String      _description;
    private         int         _gold;
    private         int         _attack;
    private         int         _defense;
    private         int         _regen;
    private         BeingStatus _status;
    private         int         _maxHealth;
    private         int         _health;
    private         boolean     _started;

    public Monster(Map<String, Object> monsterData) {
        _name        = (String)monsterData.get("name");
        _description = (String)monsterData.get("description");
        _gold        = ((Long)monsterData.get("gold")).intValue();
        _attack      = ((Long)monsterData.get("attack")).intValue();
        _defense     = ((Long)monsterData.get("defense")).intValue();
        _regen       = ((Long)monsterData.get("regen")).intValue();
        _maxHealth  = ((Long)monsterData.get("max_health")).intValue();
        _health      = _maxHealth;
        _status      = BeingStatus.ALIVE;
    }
}
