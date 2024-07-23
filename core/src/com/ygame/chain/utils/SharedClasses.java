package com.ygame.chain.utils;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.HashMap;

public class SharedClasses {

    public static HashMap<String, PlayerState> playerMap;
    public static PlayerState red;
    public static PlayerState green;
    public static PlayerState purple;
    public static boolean gameStart;

    public SharedClasses() {
        playerMap = new HashMap<>();
        red = new PlayerState("red");
        green = new PlayerState("green");
        purple = new PlayerState("purple");
        playerMap.put("red", red);
        playerMap.put("green", green);
        playerMap.put("purple", purple);
        gameStart = false;
    }

    public static class PlayerState implements Serializable {
        public String userID;
        public float x, y;
        public Vector2 linearImpulse;
        public float angle;


        public PlayerState(String userID) {
            this.userID = userID;
            x = 5.1f;
            y = 5.1f;
            linearImpulse = new Vector2(0, 0);
            this.angle = 0;
        }

        public void update(float x, float y, float angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }
}


