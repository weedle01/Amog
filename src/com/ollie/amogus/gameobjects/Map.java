package com.ollie.amogus.gameobjects;

import com.ollie.amogus.gameobjects.entities.Crewmate;
import com.ollie.amogus.gameobjects.entities.Directions;
import com.ollie.amogus.gameobjects.entities.MPCrewMate;
import com.ollie.amogus.main.Frame;
import com.ollie.amogus.networking.MovePacket;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Objects;

public class Map {

    private ArrayList<Crewmate> crewmates = new ArrayList<>();
    private BufferedImage backImage;
    int x, y;

    public Map(int x, int y) throws IOException {

        this.x = x;
        this.y = y;
        //Find the map resource in the file given, done like this to allow for jar compilation
        backImage = ImageIO.read(Objects.requireNonNull(Map.class.getResource("/resoures/map.png")));
    }

    public void drawImage(Graphics g) {

        g.drawImage(backImage, getX(), getY(), null);

    }

    /*
        When moving we move the background instead of the crewmate to allow for easier and smoother movement.
     */

    //Adds a new crewmate to the map
    public synchronized void addCrewMate(Crewmate c){
        crewmates.add(c);
    }

    //Renders all crewmates on the map, used for rendering everything connected to the server
    public void render(Graphics g, Crewmate currentClientCrew){
        for(Crewmate crewmate : crewmates){

            if(!crewmate.getUserName().equals(currentClientCrew.getUserName())) {
                crewmate.drawImage(g);
            }
        }
    }

    private int getCrewmateIndex(String username){

        int i = 0;
        for(Crewmate crewmate : crewmates){
            if(crewmate instanceof MPCrewMate && crewmate.getUserName().equals(username))
                break;

            i++;
        }

        return i;

    }

    //Move a MPCrewmate on a specific client
    public synchronized void moveCrewmates(String username, float x, float y, Directions movingDir){

        int i = getCrewmateIndex(username);
        MPCrewMate crewmate = (MPCrewMate) crewmates.get(i);

        crewmate.setNewX(x);
        crewmate.setNewY(y);
        crewmate.changeDirection(movingDir);

    }

    public void setX(int x) {
        this.x += x;
    }

    public void setY(int y) {
        this.y += y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<Crewmate> getCrewmates() {
        return crewmates;
    }
}
