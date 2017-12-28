package be.howest.mariobros.sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.utils.Array;

import be.howest.mariobros.MarioBros;
import be.howest.mariobros.Screen.PlayScreen;
import be.howest.mariobros.sprites.Enemies.Enemy;
import be.howest.mariobros.sprites.Enemies.Turtle;
import be.howest.mariobros.sprites.Other.FireBall;


/**
 * Created by Egon on 30/10/2017.
 */

public class Mario extends Sprite {
    public enum State {Falling, Jumping, Standing, Running, Growing, Dead}
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;
    private Animation<TextureRegion> firing;

    private float stateTimer;
    private boolean runningRight;
    private boolean timeToRedefineMario;
    public boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean marioIsDead;
    private PlayScreen screen;

    private Array<FireBall> fireBalls;

    public Mario (PlayScreen screen){
        this.screen = screen;
        this.world = screen.getWorld();

        currentState = State.Standing;
        previousState = State.Standing;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i *16, 0, 16,16));
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i *16, 0, 16,32));
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);

        frames.clear();

        //get animation mario grow
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        //mario jump
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80 ,0 , 16 , 32);

        //mario stand
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0 ,0 , 16 , 32);
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);

        //mario dead
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        //define mario
        defineMario();

        //set initial values for mario location and bounds
        setBounds(0,0,16 / MarioBros.PPM,16 / MarioBros.PPM);
        setRegion(marioStand);

        fireBalls = new Array<FireBall>();
    }

    public void update(float dt){

        if(screen.getHud().isTimeUp() && isDead()){
            die();
        }
        //for updating the sprite
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() /2 - 6 / MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() /2);
        // update the sprite with the correct region
        setRegion(getFrame(dt));
        if(timeToDefineBigMario){
            defineBigMario();
        }
        if(timeToRedefineMario)
            redefineMario();

        for(FireBall ball : fireBalls){
            ball.update(dt);
            if(ball.isDestroyed()){
                fireBalls.removeValue(ball,true);
            }
        }
        //Mario falls
        if(b2body.getPosition().y<0 && !marioIsDead){
            b2body.setLinearVelocity(0,0);
            b2body.getPosition().y = 0;
            hit(null);
        }
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case Dead:
                region = marioDead;
                break;
            case Growing:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case Jumping:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case Running:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case Falling:
            case Standing:
                default:
                    region = marioIsBig ? bigMarioStand: marioStand;
                    break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        }else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(marioIsDead)
            return State.Dead;
        else if(runGrowAnimation)
            return State.Growing;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.Jumping ) ||(b2body.getLinearVelocity().y < 0 && previousState == State.Jumping))
            return State.Jumping;
        else if(b2body.getLinearVelocity().y < 0 )
            return State.Falling;
        else if(b2body.getLinearVelocity().x != 0)
            return State.Running;
        else
            return State.Standing;
    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        //because mario is now bigger we need to add a certain y height
        bdef.position.set(currentPosition.add(0,10/ MarioBros.PPM));

        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT |MarioBros.ITEM_BIT |MarioBros.LEVEL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(200 / MarioBros.PPM,32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
            | MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT |MarioBros.ITEM_BIT |MarioBros.LEVEL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

    }



    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireBalls){
            ball.draw(batch);
        }
    }

    public void grow(){
        if(!isBig()){
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);


        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT |MarioBros.ITEM_BIT |MarioBros.LEVEL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.STANDING_SHELL)
            ((Turtle) enemy).kick(enemy.b2body.getPosition().x > b2body.getPosition().x ? Turtle.KICK_RIGHT : Turtle.KICK_LEFT);
        else {
            if(marioIsBig){
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() /2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            }else
                die();
        }
    }

    public void die(){
        if(!isDead()){
            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
             MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }


    public void jump(){
        if(currentState != State.Jumping){
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.Jumping;
        }
    }

    public void fire(){
        if(fireBalls.size == 0)
            fireBalls.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }
}
