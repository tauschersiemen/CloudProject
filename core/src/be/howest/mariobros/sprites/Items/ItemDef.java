package be.howest.mariobros.sprites.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Egon on 02/11/2017.
 */

public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
