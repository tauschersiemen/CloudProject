package be.howest.mariobros.sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;

import be.howest.mariobros.MarioBros;
import be.howest.mariobros.Scenes.Hud;
import be.howest.mariobros.Screen.PlayScreen;
import be.howest.mariobros.sprites.Mario;


/**
 * Created by Egon on 30/10/2017.
 */

public class Brick extends InteractiveTileObject {
    private boolean hit;
    private Vector2 originalPosition;
    private Vector2 movablePosition;

    public Brick(PlayScreen screen, MapObject object){
        super(screen,object);
        fixture.setUserData(this);
        setCategoryFIlter(MarioBros.BRICK_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            Gdx.app.log("brick", "collision");
            setCategoryFIlter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();

        }
        else
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

}
