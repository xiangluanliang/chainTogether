package com.ygame.chain;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.ygame.chain.ConstPool.PPM;


public class ChainTogether extends Game {
    private SpriteBatch batch;
    private Player redBall;
    private Player greenBall;
    private Player purpleBall;
    private OrthographicCamera camera;
    private SmoothCamera smoothCamera;

    private World world;
    private Box2DDebugRenderer debugRenderer;


    MyMapGenerator mapGenerator;


    @Override
    public void create() {

        batch = new SpriteBatch();

        // 创建相机
        float forceLength = 128f;// 相机焦距（缩小倍率） -mark-> 后期考虑要不要把相机封装起来（感觉没必要？
        camera = new OrthographicCamera();
        camera.setToOrtho(false,
                Gdx.graphics.getWidth() / forceLength,
                Gdx.graphics.getHeight() / forceLength);
        smoothCamera = new SmoothCamera(camera, 0.9f);

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
        mapGenerator = new MyMapGenerator("./chain_together_map/level-1.tmx", world);
        mapGenerator.createTerrainFromTiled("terrainObj");

        // 创建角色
        // 有且仅有三个
        redBall = new Player("./ball/smallRedBall.png", world, 5, 5);
        greenBall = new Player("./ball/smallGreenBall.png", world, 5.1f, 5.1f);
        purpleBall = new Player("./ball/smallPurpleBall.png", world, 5.2f, 5.2f);

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapGenerator.getMapRenderer().setView(camera);
        mapGenerator.getMapRenderer().render();

        // 获取 物体的位置
        Vector2 position = redBall.getPosition();

        // 将相机与批处理精灵绑定
        if ((position.x > 0 && position.x < 15) && position.y < 30) {
            smoothCamera.setTargetPosition(position.x, position.y * 0.8f + 2);
        }
        smoothCamera.update(Gdx.graphics.getDeltaTime());


        // 将绘制与相机投影绑定
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        redBall.render(batch);
        greenBall.render(batch);
        purpleBall.render(batch);
        batch.end();

        handleInput();

        // 给Box2D世界里的物体绘制轮廓，正式游戏需要注释掉这个渲染
        debugRenderer.render(world, camera.combined);

        // 更新世界里的关系 这个要放在绘制之后，最好放最后面
        world.step(1 / 60f, 6, 2);
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            redBall.move(0.1f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            redBall.move(-0.1f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            redBall.jump(0, 3);
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}

