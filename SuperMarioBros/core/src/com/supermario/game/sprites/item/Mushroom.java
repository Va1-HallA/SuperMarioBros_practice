package com.supermario.game.sprites.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.supermario.game.MarioBros;
import com.supermario.game.screens.PlayScreen;
import com.supermario.game.sprites.Mario;

public class Mushroom extends Item{
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        TextureRegion mushroomImage = new TextureRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        setRegion(mushroomImage);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.filter.categoryBits = MarioBros.ITEM_BIT;
        fdef.filter.maskBits =
                MarioBros.GROUND_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.MARIO_BIT
                | MarioBros.COIN_BIT;
        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        mario.grow();
        destroy();
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
