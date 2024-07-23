package com.ygame.chain.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.ygame.chain.utils.ConstPool.PPM;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: LevelKey
 * Package : com.ygame.chain.utils
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/24 1:41
 * @Version 1.0
 */
public class LevelKey {

    private final TextureRegion role;
    private Sprite sprite;
    private Body starBody;

    public LevelKey(World world) {
        Texture img = new Texture("./resourse/space-shooter/star-1.png");
        role = new TextureRegion(img, img.getWidth(), img.getHeight());

        BodyDef starBodyDef = new BodyDef(); //定义
        starBodyDef.type = BodyDef.BodyType.StaticBody;// -mark-> 这里先设成静态，等加了刺就编到刺类里，触碰重开
        starBodyDef.position.x = 17;
        starBodyDef.position.y = 2;
        starBody = world.createBody(starBodyDef); //实体化
        PolygonShape starBox = new PolygonShape();
        starBox.setAsBox(img.getWidth() / 2f / PPM - 2 / PPM, img.getHeight() / 2f / PPM - 2 / PPM);
        starBody.createFixture(starBox, 0).setUserData(this);
    }

    public void render(SpriteBatch batch) {
        sprite = new Sprite(role);
        sprite.setOriginBasedPosition(starBody.getPosition().x, starBody.getPosition().y);
        sprite.setScale(2 / PPM);
        sprite.draw(batch);
    }
}
