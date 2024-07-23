package com.ygame.chain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ygame.chain.utils.*;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: Level1
 * Package : com.ygame.chain.screens
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/19 10:20
 * @Version 1.0
 */
public class Level1 implements Screen {
    private SpriteBatch batch;
    private Player redBall;
    private Player greenBall;
    private Player purpleBall;
    private SmoothCamera smoothCamera;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    GameMapGenerator mapGenerator;
    private Stage stage;
    private Array<Bullet> bullets;
    private float bulletTimer;

    private boolean playerHit;

    private Texture bulletTexture;

    public Level1(){
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        bulletTexture = new Texture("drop.png");

        // 创建相机
        float forceLength = 100f;// 相机焦距（缩小倍率） -mark-> 后期考虑要不要把相机封装起来（感觉没必要？

        smoothCamera = new SmoothCamera(0.9f);
        smoothCamera.setToOrtho(false,
                Gdx.graphics.getWidth() / forceLength,
                Gdx.graphics.getHeight() / forceLength);

        // 载入地图（我是不是有选择困难证？先加载地图还是先加载人物都要想半天。。没差啦，按照自然顺序先加载地图吧

        // 创建世界
        world = new World(new Vector2(0, -9.8f), true);
        // 试调渲染
        debugRenderer = new Box2DDebugRenderer();

        // 兜底大地面，以免卡出无限掉落
        BodyDef groundBodyDef = new BodyDef(); //定义
        groundBodyDef.type = BodyDef.BodyType.StaticBody;// -mark-> 这里先设成静态，等加了刺就编到刺类里，触碰重开
        groundBodyDef.position.x = 0;
        groundBodyDef.position.y = -3;
        Body groundBody = world.createBody(groundBodyDef); //实体化
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(Gdx.graphics.getWidth(), 1);
        groundBody.createFixture(groundBox, 0);

        // 加载所有碰撞箱
        mapGenerator = new GameMapGenerator("./chain_together_map/level-1.tmx", world);
        mapGenerator.createTerrainFromTiled("terrainObj");


        bullets = new Array<>();
        bulletTimer = 0;
        playerHit = false;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                if ((fixtureA.getUserData() != null && fixtureA.getUserData().equals("bullet")) ||
                        (fixtureB.getUserData() != null && fixtureB.getUserData().equals("bullet"))) {
                    if ((fixtureA.getUserData() != null && fixtureA.getUserData().equals("player")) ||
                            (fixtureB.getUserData() != null && fixtureB.getUserData().equals("player"))) {
                        playerHit = true;
                        System.out.println("Player Hit! Game Over.");
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
        // 创建角色
        // 有且仅有三个
        redBall = new Player("./ball/smallRedBall.png", world, 5, 5);
        greenBall = new Player("./ball/smallGreenBall.png", world, 5.1f, 5.1f);
        purpleBall = new Player("./ball/smallPurpleBall.png", world, 5.2f, 5.2f);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapGenerator.getMapRenderer().setView(smoothCamera);
        mapGenerator.getMapRenderer().render();

        updateBullet(delta);
//        smoothCamera.update(redBall);

        // 将绘制与相机投影绑定
        batch.setProjectionMatrix(smoothCamera.combined);
        batch.begin();
        if (!playerHit) {
            redBall.render(batch);
            greenBall.render(batch);
            purpleBall.render(batch);
        }
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
        batch.end();

        handleInput();

        // 给Box2D世界里的物体绘制轮廓，正式游戏需要注释掉这个渲染
        debugRenderer.render(world, smoothCamera.combined);

        // 更新世界里的关系 这个要放在绘制之后，最好放最后面
        world.step(1 / 60f, 6, 2);
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            redBall.move(0.1f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            redBall.move(-0.1f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            redBall.jump(0, 6);
    }

    public void updateBullet(float deltaTime) {

        bulletTimer += deltaTime;
        if (bulletTimer >= 0.5f) {
            bulletTimer = 0;
            bullets.add(new Bullet(bulletTexture, Gdx.graphics.getWidth() / ConstPool.PPM, 1, world)); // 从右下角发射
        }
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime);
            // 移除离开屏幕的子弹
            if (bullet.isOffScreen()) {
                bullets.removeIndex(i);
                world.destroyBody(bullet.getBody());
            }
        }

    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
