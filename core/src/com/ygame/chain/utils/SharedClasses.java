package com.ygame.chain.utils;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: SharedClass
 * Package : com.ygame.chain.utils
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/20 23:35
 * @Version 1.0
 */


public class SharedClasses {
    public static class JoinRequest implements Serializable {
        public String userID;

        public JoinRequest() {
        }

        public JoinRequest(String userID) {
            this.userID = userID;
        }
    }

    public static class JoinResponse implements Serializable {
        public int playerOrder;
        public String texturePath;

        public JoinResponse() {
        }

        public JoinResponse(int playerOrder, String texturePath) {
            this.playerOrder = playerOrder;
            this.texturePath = texturePath;
        }
    }

    public static class PlayerMove implements Serializable {
        public String userID;
        public Vector2 position;

        public PlayerMove() {
        }

        public PlayerMove(String userID, Vector2 position) {
            this.userID = userID;
            this.position = position;
        }
    }

//    public static class RoomCode{
//        public String roomCode;
//
//        public RoomCode(){
//
//        }
//
//        public RoomCode(String roomCode){
//            this.roomCode = roomCode;
//        }
//
//    }
}
