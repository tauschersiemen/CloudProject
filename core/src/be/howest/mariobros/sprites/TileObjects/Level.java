package be.howest.mariobros.sprites.TileObjects;

import be.howest.mariobros.MarioBros;
import be.howest.mariobros.Screen.PlayScreen;
import be.howest.mariobros.sprites.Mario;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;

public class Level extends InteractiveTileObject{
    public Level(PlayScreen screen, MapObject object){
        super(screen,object);
        fixture.setUserData(this);
        setCategoryFIlter(MarioBros.LEVEL_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
       Gdx.app.log("collide","yes");
    }
}
