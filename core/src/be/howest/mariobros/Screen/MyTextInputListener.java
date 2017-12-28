package be.howest.mariobros.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import be.howest.mariobros.Scenes.Hud;

/**
 * Created by Egon on 28/12/2017.
 */

public class MyTextInputListener implements Input.TextInputListener {
    private Hud hud;
    public MyTextInputListener(Hud hud){
        this.hud = hud;
    }
    @Override
    public void input (String text) {
        Gdx.app.log("Your name", text);

        Gdx.app.log("Your score",  hud.getScore() + "");
    }

    @Override
    public void canceled () {
    }
}
