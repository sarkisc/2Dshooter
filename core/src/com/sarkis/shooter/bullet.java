package com.sarkis.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class bullet {
    private Rectangle BulletRect;
    private Texture BulletImage;
    private Texture BImage = new Texture(Gdx.files.internal("bullet.png"));
    private Texture PImage = new Texture(Gdx.files.internal("particle.png"));

    public final String type; // can be either "friend" or "enemy"

    static final float acceleration = 10000;

    private float velocity;
    private double angle;

    // it is called in a(n Enemy) Player’s fire() function
    bullet(float x, float y, float touchPosX, float touchPosY, String Type) {
        if( Type == "bullet") {
            this.BulletImage = BImage;
        }
        else
            this.BulletImage = PImage;

        this.type = Type;
        this.BulletRect = new Rectangle(x, y, BulletImage.getWidth(), BulletImage.getHeight());
        this.angle = Math.atan2((touchPosY-y), (touchPosX-x));
        this.velocity = 100;
    }

    public Texture getBulletImage() {
        return BulletImage;
    }

    public final Rectangle getRect() {
        return new Rectangle(BulletRect);
    }

    // this updates the bullet’s rectangle’s coordinates
    public void update(float deltaTime) {
        // get angle
        BulletRect.setX(BulletRect.getX() + (float) (velocity * deltaTime * Math.cos(angle)) + (float)(0.5 * acceleration * Math.pow(deltaTime,2)));
        BulletRect.setY(BulletRect.getY() + (float) (velocity * deltaTime * Math.sin(angle))+ (float)(0.5 * acceleration * Math.pow(deltaTime,2)));
        velocity += acceleration*deltaTime;

    }

}
