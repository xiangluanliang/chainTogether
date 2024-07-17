package com.ygame.chain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import static com.ygame.chain.ConstPool.PPM;

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
    private TextureRegion role;
    private Body roleBody;

    private int Height;
    private int Width;


    private Sprite sprite;
    private Rectangle boundingBox;
    float moveSpeed;
    private float velocityX = 0;
    boolean isMoving = false;
    private final static float FRICTION = -199;
    private final static float GRAVITY = -980;
    float jumpSpeed;
    private float velocityY = 0;
    boolean isJumping = false;
    int leftRightFlag;
    private int deadCount = 0;
    private float[] flagPosition = new float[2];

    public Player(String path, World world, float bornPositionX, float bornPositionY) {
        Texture img = new Texture(path);
        Width = img.getWidth();
        Height = img.getHeight();
        role = new TextureRegion(img, img.getWidth(), img.getHeight());

        BodyDef roleBodyDef = new BodyDef();
        roleBodyDef.type = BodyDef.BodyType.DynamicBody;
        roleBodyDef.position.x = bornPositionX;
        roleBodyDef.position.y = bornPositionY;
        roleBody = world.createBody(roleBodyDef);
        CircleShape dynamicBox = new CircleShape();
        dynamicBox.setRadius(Width / 2f / PPM);
//
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

    public float getHeight() {
        return Height;
    }

    public float getWidth() {
        return Width;
    }

    public Player(float x, float y, float scale, String path) {
        Texture role = new Texture(Gdx.files.internal(path));
        moveSpeed = 200;
        jumpSpeed = 500;
        sprite = new Sprite(role);
        sprite.setSize(role.getWidth() * scale, role.getHeight() * scale);
        sprite.setPosition(x, y);
        setFlag(x, y);
        boundingBox = new Rectangle(x, y, (role.getWidth() - 200) * scale, (role.getHeight() - 200) * scale);
    }

    public void updateBox(Body body, float PPM) {
        sprite.setPosition(
                body.getPosition().x * PPM - sprite.getWidth() / 2,
                body.getPosition().y * PPM - sprite.getHeight() / 2
        );
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    private void setFlag(float[] flag) {
        flagPosition[0] = flag[0];
        flagPosition[1] = flag[1];
    }

    private void setFlag(float x, float y) {
        flagPosition[0] = x;
        flagPosition[1] = y;
    }

    public void move(int flag) {
        leftRightFlag = flag;
        velocityX = flag * moveSpeed;
        isMoving = true;
    }

    public void moveUpdate(float deltaTime, Array<Rectangle> collidableRects) {
        if (isMoving) {
            velocityX += leftRightFlag * FRICTION * deltaTime;
            boundingBox.setPosition(sprite.getX() + velocityX * deltaTime, sprite.getY());

            for (Rectangle rect : collidableRects) {
                if (boundingBox.overlaps(rect)) {
                    isMoving = false;
                    return;
                }
            }
            sprite.translateX(velocityX * deltaTime);

            if (velocityX * leftRightFlag <= 180) {
                velocityX = 0;
                isMoving = false;
            }
        }
    }

    public void platUpdate(Array<MapObject> plats) {
        for (MapObject plat : plats) {
            if ((boolean) plat.getProperties().get("isTouched")) {
                plat.setVisible(false);
            }

        }

    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpSpeed;
            isJumping = true;
        }
    }

    public void jumpUpdate(float deltaTime, Array<Rectangle> collidableRects) {
        if (isJumping) {
            velocityY += GRAVITY * deltaTime;
            boundingBox.setPosition(sprite.getX(), sprite.getY() + velocityY * deltaTime);

            for (Rectangle rect : collidableRects) {
                if (boundingBox.overlaps(rect)) {

                    if (velocityY < 0 && sprite.getY() > rect.getY() + rect.getHeight()) {
                        isJumping = false;
                        velocityY = 0;
                        sprite.setY(rect.getY() + rect.getHeight());

                    } else if (velocityY > 0 && sprite.getY() < rect.getY()) {
                        velocityY = 0;
                        sprite.setY(rect.getY() - sprite.getHeight());
                    }
                    return;
                }
            }

            sprite.translateY(velocityY * deltaTime);

//            if (sprite.getY() <= 0) {
//                sprite.setY(0);
//                velocityY = 0;
//                isJumping = false;
//            }
        }
    }

    public void applyGravity(float deltaTime, Array<Rectangle> collidableRects) {
        if (!isJumping) {
            velocityY += GRAVITY * deltaTime;
            boundingBox.setPosition(sprite.getX(), sprite.getY() + velocityY * deltaTime);

            for (Rectangle rect : collidableRects) {
                if (boundingBox.overlaps(rect)) {
                    if (sprite.getY() > rect.getY() + rect.getHeight()) {
                        velocityY = 0;
                        sprite.setY(rect.getY() + rect.getHeight());
                    }
                    return;
                }
            }

            sprite.translateY(velocityY * deltaTime);

//            if (sprite.getY() <= 0) {
//                sprite.setY(0);
//                velocityY = 0;
//            }
        }
    }

    public void deadUpdate() {
        if (sprite.getY() <= 0) {
            sprite.setPosition(flagPosition[0], flagPosition[1]);
            boundingBox.setPosition(flagPosition[0], flagPosition[1]);
            deadCount++;
            velocityY = 0;
            velocityX = 0;
            isJumping = false;
            isMoving = false;
        }
    }

    //    public void platUpdate(float deltaTime, Array<Rectangle> plats){
//
//    }
    public int getDeadCount() {
        return deadCount;
    }

    public void render(SpriteBatch batch) {
        batch.draw(role,
                roleBody.getPosition().x,
                roleBody.getPosition().y,
                0, 0,
                Width, Height,
                1 / PPM, 1 / PPM,
                roleBody.getAngle() * MathUtils.radiansToDegrees// 旋转
        );
    }
}

