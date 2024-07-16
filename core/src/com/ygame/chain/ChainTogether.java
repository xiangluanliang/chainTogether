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



public class ChainTogether extends Game {
    private SpriteBatch batch;
    private TextureRegion img, dog;
    private OrthographicCamera camera;
    private SmoothCamera smoothCamera;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body dogBody;
    // 在正常像素下物体重力现象不明显，需要对纹理进行缩小100++倍才有比较明显的物理效果
    private final float reduce = 100f;// 缩小100 倍易于观察到物理现象
    private boolean isJump;

    MyMapGenerator mapGenerator;


    @Override
    public void create() {

        batch = new SpriteBatch();
        // 创建一个相机， 这里缩小64倍，因为要观察的物体需要缩小100倍
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 128f, Gdx.graphics.getHeight() / 128f);
        smoothCamera = new SmoothCamera(camera,0.9f);

        // 图片一： 50*50 缩小100倍就是 0.5*0.5 在绘制时缩小的
        dog = new TextureRegion(new Texture("badlogic.jpg"), 50, 50);
        // 图片二： 256*256 ...
        img = new TextureRegion(new Texture("bochii.png"), 596, 560);

        // 创建一个世界，里面的重力加速度为 10
        world = new World(new Vector2(0, -9.8f), true);
        // 试调渲染，可以使用这个渲染观察到我们用Box2D绘制的物体图形
        debugRenderer = new Box2DDebugRenderer();

        mapGenerator = new MyMapGenerator("./chain_together_map/level-1.tmx",world);

        // 创建一个地面，其实是一个静态物体，这里我们叫它地面，玩家可以走在上面
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;// 静态的质量为0
        groundBodyDef.position.x = 0;// 位置
        groundBodyDef.position.y = -3;
        // 创建这个地面的身体，我们对这个物体
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();// 物体的形状，这样创建是矩形的
        groundBox.setAsBox(Gdx.graphics.getWidth(), 2);// 物体的宽高
        groundBody.createFixture(groundBox, 0); // 静态物体的质量应该设为0

        mapGenerator.createTerrainFromTiled("terrainObj");
        mapGenerator.createTerrainFromTiled("plats");

        // 再添加一个动态物体，可以把他看成玩家
        BodyDef dogBodyDef = new BodyDef();
        dogBodyDef.type = BodyDef.BodyType.DynamicBody;
        dogBodyDef.position.x = 10;
        dogBodyDef.position.y = 5;
        dogBody = world.createBody(dogBodyDef);
        PolygonShape dynamicBox = new PolygonShape();
        dynamicBox.setAsBox(50f / 2 / reduce, 50f / 2 / reduce);

        // 给物体添加一些属性
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = dynamicBox;// 形状
        fixtureDef.restitution = 0.1f; // 设置这个值后，物体掉落到地面就会弹起一点高度...
        dogBody.createFixture(fixtureDef).setUserData(this);//设置自定义数据可以从这个物体获取这个数据对象

        // 上面的图形要处理掉
//        groundBox.dispose();
        dynamicBox.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        mapGenerator.getMapRenderer().setView(camera);
        mapGenerator.getMapRenderer().render();

        // 获取 物体的位置
        Vector2 position = dogBody.getPosition();

        // 将相机与批处理精灵绑定
        if((position.x > 5 && position.x < 15 ) && position.y<15){
            smoothCamera.setTargetPosition(position.x, position.y*0.8f);
        }
        smoothCamera.update(Gdx.graphics.getDeltaTime());


        // 将绘制与相机投影绑定 关键 关键
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
//        batch.draw(img, 10 - 1.28f, 10, 0, 0, 256, 256, 0.01f, 0.01f, 0);
        batch.draw(dog, position.x - 50f / 2 / reduce, position.y - 50f / 2 / reduce, // 设置位置 减少 50/2/reduce 是为了和物体的形状重合
                0, 0, 50, 50, // 绘制图片的一部分，这里就是全部了
                1 / reduce, 1 / reduce, // 缩小100倍
                0 // 不旋转
        );
        batch.end();

        // 获取五星的线速度
        Vector2 linearVelocity = dogBody.getLinearVelocity();
        if (Gdx.input.isKeyPressed(Input.Keys.D) && linearVelocity.x <= 2) { // 现在最大速度为 2，不然会放飞自我
            // 施加冲动 让物体运行起来，可以看成我们推一下物体就往一边移动了
            dogBody.applyLinearImpulse(new Vector2(0.1f, 0), dogBody.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && linearVelocity.x >= -2) {
            dogBody.applyLinearImpulse(new Vector2(-0.1f, 0), dogBody.getWorldCenter(), true);
        }

        // 跳起来的逻辑，比较简单。但是时候这个演示
        if (!isJump && Gdx.input.isKeyPressed(Input.Keys.W) && linearVelocity.y <= 3) {
            dogBody.applyLinearImpulse(new Vector2(0, 3), dogBody.getWorldCenter(), true);
            isJump = true;
        }
        if (linearVelocity.y == 0) {
            isJump = false;
        }

        // 给Box2D世界里的物体绘制轮廓，让我们看得更清楚，正式游戏需要注释掉这个渲染
        debugRenderer.render(world, camera.combined);

        // 更新世界里的关系 这个要放在绘制之后，最好放最后面
        world.step(1 / 60f, 6, 2);
    }

    @Override
    public void dispose(){
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
        }
}

