package com.supermario.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.supermario.game.MarioBros;
import com.supermario.game.screens.PlayScreen;

import org.w3c.dom.Text;

public class FireBall extends Sprite {
    private PlayScreen screen;
    private World world;
    private Body body;
    private MarioBros game;
    private Mario mario;
    private boolean fireRight;
    private boolean setToDestroy;
    private boolean destroyed;
    private Animation animation;
    private float stateTimer;

    public FireBall(PlayScreen screen, float x, float y, boolean fireRight){
        this.screen = screen;
        this.fireRight = fireRight;
        this.world = screen.getWorld();
        this.game = screen.getGame();
        this.mario = screen.mario;
        this.stateTimer = 0;
        this.setToDestroy = false;
        this.destroyed = false;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i*8, 0, 8, 8));
        }
        this.animation = new Animation(0.2f, frames);

        setRegion((TextureRegion) animation.getKeyFrame(0));
        setBounds(x, y, 8 / MarioBros.PPM, 8 / MarioBros.PPM);
        defineFireBall();
    }

    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        bdef.position.set(fireRight ? getX() + 12 / MarioBros.PPM : getX() - 12 / MarioBros.PPM, getY());
        bdef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bdef);
        shape.setRadius(3 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.FIREBALL_BIT;
        fdef.filter.maskBits =
                MarioBros.GROUND_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT;
        fdef.shape = shape;
        fdef.restitution = 1;
        fdef.friction = 0;
        body.createFixture(fdef).setUserData(this);
//        body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 2.5f));
        body.applyLinearImpulse(new Vector2(fireRight ? 2 : -2, 1.5f), body.getWorldCenter(), true);
    }

    public void update(float deltaTime){
        stateTimer += deltaTime;
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion((TextureRegion)animation.getKeyFrame(stateTimer, true));
//        if(body.getLinearVelocity().y > 2f)
//            body.setLinearVelocity(body.getLinearVelocity().x, 2f);

        if(!destroyed && (stateTimer > 3 || setToDestroy)){
            world.destroyBody(body);
            screen.fireBalls.removeValue(this, true);
            destroyed = true;
        }
    }

    public void hit(){
        this.setToDestroy = true;
    }
}
