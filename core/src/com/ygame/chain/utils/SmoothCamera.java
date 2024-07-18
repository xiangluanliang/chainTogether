package com.ygame.chain.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: SmoothCamera
 * Package : com.ygame.chain
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/16 14:23
 * @Version 1.0
 */
public class SmoothCamera extends OrthographicCamera {
    private Vector2 targetPosition;
    private float smoothing;

    public SmoothCamera(float smoothing) {
        this.targetPosition = new Vector2(position.x, position.y);
        this.smoothing = smoothing;
    }

    public void update(Player player) {
        Vector2 playerPosition = player.getPosition();
        // 将相机与批处理精灵绑定
        if ((playerPosition.x > 0 && playerPosition.x < 15) && playerPosition.y < 30) {
            targetPosition.set(playerPosition.x, playerPosition.y * 0.8f + 2);
        }
        position.x += (targetPosition.x - position.x) * smoothing * Gdx.graphics.getDeltaTime();
        position.y += (targetPosition.y - position.y) * smoothing * Gdx.graphics.getDeltaTime();
        update();
    }

}
