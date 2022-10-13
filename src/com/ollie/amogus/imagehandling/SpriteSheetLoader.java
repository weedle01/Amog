package com.ollie.amogus.imagehandling;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SpriteSheetLoader {

    private static BufferedImage spritesheet;

    private static final int TILE_SIZE = 16;

    public static BufferedImage loadImage(){

        BufferedImage sprite = null;

        try{
            sprite = ImageIO.read(Objects.requireNonNull(SpriteSheetLoader.class.getResource("/resoures/spriteSheet.png")));
        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return sprite;

    }

    public static BufferedImage getSprite(int xGrid, int yGrid){
        if(spritesheet == null){
            spritesheet = loadImage();
        }

        return spritesheet.getSubimage(xGrid*TILE_SIZE, yGrid*TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

}