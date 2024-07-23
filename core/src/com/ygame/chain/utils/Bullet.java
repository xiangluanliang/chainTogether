package com.ygame.chain.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.ygame.chain.utils.ConstPool.PPM;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: Bullet
 * Package : com.ygame.chain.utils
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/22 23:21
 * @Version 1.0
 */
public class Bullet {
    private Texture texture;
    private Body body;
    private Rectangle bounds;

    public Bullet(Texture texture, float startX, float startY, World world) {
        this.texture = texture;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);
        body = world.createBody(bodyDef);
        body.setGravityScale(0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(texture.getWidth() / 2f / PPM, texture.getHeight() / 2f / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // 设置为传感器，不受力
        fixtureDef.density = 0;
        body.createFixture(fixtureDef).setUserData("bullet");


        shape.dispose();

        body.setLinearVelocity(-5, 0); // 向左下方的速度

        bounds = new Rectangle(startX / PPM, startY / PPM, texture.getWidth() / PPM, texture.getHeight() / PPM);
    }

    public void update(float deltaTime) {
        Vector2 position = body.getPosition();
        bounds.setPosition(position.x - texture.getWidth() / 2f / PPM, position.y - texture.getHeight() / 2f / PPM);
    }

    public void render(SpriteBatch batch) {
        Sprite sprite = new Sprite(texture);
        sprite.setScale(1 / PPM);
        sprite.draw(batch);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isOffScreen() {
        return bounds.x + bounds.width < 0 || bounds.y + bounds.height < 0;
    }

    public Body getBody() {
        return body;
    }
}
