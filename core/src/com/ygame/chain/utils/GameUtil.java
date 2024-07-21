package com.ygame.chain.utils;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: GameUtil
 * Package : com.ygame.chain.utils
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/21 11:04
 * @Version 1.0
 */
public class GameUtil {
    public static String generateRoomNumber() {
        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        return String.valueOf(number);
    }

    public static String getServerAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static class KryoHelper {
        public static void registerClasses(Kryo kryo) {
            kryo.register(SharedClasses.JoinRequest.class);
            kryo.register(SharedClasses.JoinResponse.class);
            kryo.register(SharedClasses.PlayerMove.class);
            kryo.register(Vector2.class);
        }
    }
}
