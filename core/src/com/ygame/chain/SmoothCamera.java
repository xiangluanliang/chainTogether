package com.ygame.chain;

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
public class SmoothCamera {
    private OrthographicCamera camera;
    private Vector2 targetPosition;
    private float smoothing;

    public SmoothCamera(OrthographicCamera camera, float smoothing) {
        this.camera = camera;
        this.targetPosition = new Vector2(camera.position.x, camera.position.y);
        this.smoothing = smoothing;
    }

    public void update(float deltaTime) {
        // 插值计算新的相机位置
        camera.position.x += (targetPosition.x - camera.position.x) * smoothing * deltaTime;
        camera.position.y += (targetPosition.y - camera.position.y) * smoothing * deltaTime;
        camera.update();
    }

    public void setTargetPosition(float x, float y) {
        targetPosition.set(x, y);
    }
}
