package S0573132;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;

import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.HashMap;

public class Crigne_V1 extends AI {

    float richtung;
    int score, reisMeter, hoffentlichindex;
    int ownScore = 0;

    boolean tempRising, tempUp, tempLeft, tempRight;

    Point[] pearl = info.getScene().getPearl(); // ziele
    int[] pearlsTemp = new int[]{pearl[0].x, pearl[1].x, pearl[2].x, pearl[3].x, pearl[4].x, pearl[5].x, pearl[6].x, pearl[7].x, pearl[8].x, pearl[9].x};
    Path2D[] obstacles = info.getScene().getObstacles();
    HashMap<Integer, Integer> sortedPearlsX = new HashMap<>();
    HashMap<Integer, Integer> sortedPearlsY = new HashMap<>();

    public Crigne_V1(Info info){
        super(info);
        //enlistForTournament(573132, 573689);
        Arrays.sort(pearlsTemp);
        getSortedPearls();
    }

    @Override
    public String getName() {
        return "Crigne_V1";
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

    @Override
    public PlayerAction update() {

        float speed = info.getMaxAcceleration(); // max speed
        score = info.getScore();

        if(tempRising){ richtung = riseAndShine();
        }else {
            for (Path2D obstacle : obstacles) {
            if (obstacle.contains(info.getX(), info.getY() - 3) ) { // obstacle unten
                tempRising = true;
                reisMeter = 0;
            }
            if (obstacle.contains(info.getX(), info.getY() + 3) ) { // obstacle oben
                tempRising = true;
                tempUp = true;
                reisMeter = 0;
            }
            if (obstacle.contains(info.getX() - 3, info.getY()) ) { // obstacle links
                tempRising = true;
                tempLeft = true;
                reisMeter = 0;
            }
            if (obstacle.contains(info.getX() + 3f, info.getY()) ) { // obstacle rechts
                tempRising = true;
                tempRight = true;
                reisMeter = 0;
            }}
            richtung = goToPearl();
        }

        if (pearlsTemp[ownScore] == info.getX() && sortedPearlsY.get(ownScore) == info.getY()) {
            ownScore = ownScore + 1;
        }

        return new DivingAction(speed,richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }

    public float goToPearl(){
        Point newDirection = new Point(pearlsTemp[ownScore] - info.getX(), sortedPearlsY.get(ownScore) - info.getY());

        richtung = (float)Math.atan2(newDirection.getY(), newDirection.getX());
        return richtung;
    }

    public void getSortedPearls(){
        for(int i = 0; i < pearl.length; i++){
            sortedPearlsX.put((int)pearl[i].getX(), i);
        }
        for(int i = 0; i < pearl.length; i++){
            sortedPearlsY.put(i, (int)pearl[sortedPearlsX.get(pearlsTemp[i])].getY());
        }
    }

    public float riseAndShine(){
        float direction = 0;
        if(reisMeter >= 30){
            tempRising = false;
            tempRight = false;
            tempLeft = false;
            tempUp = false;
        }

        if(tempRising){
            direction = (float)Math.PI / 2 - reisMeter/15f;
        }
        if(tempRight){
            direction = (float) Math.PI - reisMeter/15f;
        }
        if(tempLeft){
            direction = 0 - reisMeter/15f;
        }
        if(tempUp){
            direction = (float) -Math.PI / 2 - reisMeter/15f;
        }

        reisMeter++;

        return direction;
    }

}
