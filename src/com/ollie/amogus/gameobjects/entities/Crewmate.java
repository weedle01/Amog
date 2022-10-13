package com.ollie.amogus.gameobjects.entities;

import com.ollie.amogus.gameobjects.GameObject;
import com.ollie.amogus.gameobjects.Wall;
import com.ollie.amogus.imagehandling.MirrorHandler;
import com.ollie.amogus.imagehandling.SpriteSheetLoader;
import com.ollie.amogus.main.Frame;
import com.ollie.amogus.networking.MovePacket;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Crewmate extends GameObject {

    private boolean isImposter;
    private boolean moving;

    private String userName;

    private int displayNum;
    private int modifier;

    private Directions directions;

    public Crewmate(float x, float y, String userName) {
        super(x, y, 4, new Rectangle((int) x, (int) y, 48, 48));

        this.userName = userName;

        displayNum = 0;
        modifier = 0;

        isImposter = false;
        moving = false;

        directions = Directions.RIGHT;

        BufferedImage[] sprites = new BufferedImage[8];
        //Load the sprites from the sprite sheet, (x, y);
        sprites[0] = SpriteSheetLoader.getSprite(0, 0);
        sprites[1] = SpriteSheetLoader.getSprite(1, 0);
        sprites[2] = SpriteSheetLoader.getSprite(2, 0);
        sprites[3] = SpriteSheetLoader.getSprite(3, 0);

        //Mirror the sprites to be able to get the correct direction for going the opposite direction
        sprites[4] = MirrorHandler.mirrorer(sprites[0]);
        sprites[5] = MirrorHandler.mirrorer(sprites[1]);
        sprites[6] = MirrorHandler.mirrorer(sprites[2]);
        sprites[7] = MirrorHandler.mirrorer(sprites[3]);

        super.addSprites(sprites);

    }

    @Override
    public synchronized void drawImage(Graphics g) {

        if(directions == Directions.LEFT) modifier = 4;
        else if (directions == Directions.RIGHT) modifier = 0;

         g.drawImage((Image) super.getSprites()[displayNum + modifier], (int) getX(), (int) getY(), super.getSprites()[displayNum + modifier].getWidth()*3,super.getSprites()[displayNum + modifier].getHeight()*3, null);
         g.drawRect(getCollisionDetector().getBounds().x, getCollisionDetector().getBounds().y, getCollisionDetector().getBounds().width, getCollisionDetector().getBounds().height);
    }

    public synchronized boolean checkCollisions(int x, int y){

        for(Wall wall : Frame.getG().getWalls()){

            Rectangle newDetector = new Rectangle((int) getX(), (int) getY(), (int) getCollisionDetector().getBounds().getWidth(), (int) getCollisionDetector().getBounds().getHeight());
            //System.out.println(newDetector.getX() + " " + newDetector.getY() + " " + ((Rectangle) w.getCollisionDetector()).getX() + ((Rectangle) w.getCollisionDetector()).getY());
            if(newDetector.intersects((Rectangle) wall.getCollisionDetector())){
                return true;
            }

        }

        return false;
    }

    public synchronized void changeDirection(Directions direction){

        directions = direction;

    }

    //Change which part of the animation the sprite is in
    public synchronized void addSpriteNum(){

        if(moving) {
            if ((displayNum + 1) < 4) displayNum++;
            else displayNum = 0;
        }
    }

    public synchronized void updatePos(float x, float y) {

        //if(com.ollie.amogus.main.Frame.getG().getCrew().checkCollisions((int) x, (int) y)) return;

        setX(x);
        setY(y);

        MovePacket mp = new MovePacket(getUserName(), getX(), getY(), true, directions.ordinal());
        mp.writeData(Frame.getG().getClient());
    }

    public void setMoving(boolean moving){

        if(!moving)
            displayNum = 0;

        this.moving = moving;

    }

    public String getUserName() {
        return userName;
    }
}
