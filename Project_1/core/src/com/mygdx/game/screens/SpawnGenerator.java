package com.mygdx.game.screens;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.entities.Enemy;

import java.util.ArrayList;

public class SpawnGenerator {
    public static ArrayList<Enemy> spawnEnemy(World world, TiledMap map){
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        MapObjects objects  = map.getLayers().get("spawnPoints").getObjects();

        for(MapObject object : objects){

            if (object.getProperties().get("gid").equals(4917)){
                Enemy enemy = new Enemy(world,object.getProperties().get("x",float.class),object.getProperties().get("y", float.class));
                enemies.add(enemy);
            }
        }
        return enemies;
    }
}
