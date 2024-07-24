package com.ygame.chain.utils;

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
//        return String.valueOf(number);
        return "4210";
    }

    public static String getServerAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
