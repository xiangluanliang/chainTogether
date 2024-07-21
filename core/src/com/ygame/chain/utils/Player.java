package com.ygame.chain.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.ygame.chain.utils.ConstPool.PPM;

/**
 * ProjectName: chain_together
 * ClassName: Player
 * Package : com.chain.together
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/8 9:06
 * @Version 1.0
 */

public class Player {
    private Texture img;
    private final TextureRegion role;
    private final Body roleBody;

    private final int Height;
    private final int Width;

    private Sprite sprite;
    private boolean isJump;


    public Player(String path, World world, float bornPositionX, float bornPositionY) {
        img = new Texture(path);
        Width = img.getWidth();
        Height = img.getHeight();
        role = new TextureRegion(img, Width, Height);

        BodyDef roleBodyDef = new BodyDef();
        roleBodyDef.type = BodyDef.BodyType.DynamicBody;
        roleBodyDef.position.x = bornPositionX;
        roleBodyDef.position.y = bornPositionY;
        roleBody = world.createBody(roleBodyDef);
        CircleShape dynamicBox = new CircleShape();
        dynamicBox.setRadius(Width / 2f / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = dynamicBox;
        fixtureDef.density = 1.0f;// 密度
        fixtureDef.restitution = 0.3f; // 弹性
        fixtureDef.friction = 0.2f; // 摩擦
        roleBody.createFixture(fixtureDef).setUserData(this); //教程说从物体获取数据对象，不太懂，-mark->一下先

        dynamicBox.dispose();
    }

    public TextureRegion getRole() {
        return role;
    }

    public Body getBody() {
        return roleBody;
    }

    public Vector2 getPosition() {
        return roleBody.getPosition();
    }

    public void setPosition(float x, float y) {
        roleBody.getPosition().x = x;
        roleBody.getPosition().y = y;
    }

    public float getHeight() {
        return Height;
    }

    public float getWidth() {
        return Width;
    }

    public void move(float x, float y) {
        if (Math.abs(roleBody.getLinearVelocity().x) <= 5) {
            roleBody.applyLinearImpulse(new Vector2(x, y), roleBody.getWorldCenter(), true);
        }
    }

    public void jump(float x, float y) {
        if (roleBody.getLinearVelocity().y == 0) {
            isJump = false;
        }
        if (!isJump && roleBody.getLinearVelocity().y <= 3) {
            roleBody.applyLinearImpulse(new Vector2(x, y), roleBody.getWorldCenter(), true);
            isJump = true;
        }
    }

    public void setTexture(String texturePath) {
        img = new Texture(texturePath);
    }

    public void render(SpriteBatch batch) {
        sprite = new Sprite(role);

        // 这方法无敌了吧？发现了能不用也是神人了？你们有这样的方法吗
        // 所以到底是怎么做到的？是因为缩放带来的误差吗？兄弟没道理的
        // 它这个明显是在最开始就设定好中心，感觉像是给Texture坐标问题打的一个补丁？
        sprite.setOriginBasedPosition(roleBody.getPosition().x, roleBody.getPosition().y);

        sprite.setRotation(roleBody.getAngle() * MathUtils.radiansToDegrees);
        sprite.setScale(1 / PPM);
        sprite.draw(batch);
    }
}

