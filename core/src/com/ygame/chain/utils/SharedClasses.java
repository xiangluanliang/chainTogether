package com.ygame.chain.utils;

import com.badlogic.gdx.math.Vector2;

import java.util.Map;

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

    public static class RegisterName {
        public static String name;
    }

    public static class UpdatePosition {
        public Vector2 vec;
    }

    public enum PlayerType {
        GREEN,
        PURPLE,
        RED
    }

    public static class PlayerState {
        public int x, y; // 示例玩家位置
        public PlayerType type;
    }

    public static class RoomJoinRequest {
        public String roomCode;
    }

    public static class RoomJoinResponse {
        public Map<String, PlayerState> playerStates;

        public RoomJoinResponse() {

        }

        public RoomJoinResponse(Map<String, PlayerState> playerStates) {
            this.playerStates = playerStates;
        }
    }
}