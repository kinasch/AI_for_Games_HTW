package S0573132;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Work_in_Cringness extends AI {

    float richtung;
    int score, reisMeter, hoffentlichindex;
    int ownScore = 0;

    boolean tempRising, tempUp, tempLeft, tempRight;

    Point[] pearl = info.getScene().getPearl(); // ziele
    int[] pearlsTemp = new int[]{pearl[0].x, pearl[1].x, pearl[2].x, pearl[3].x, pearl[4].x, pearl[5].x, pearl[6].x, pearl[7].x, pearl[8].x, pearl[9].x};
    Path2D[] obstacles = info.getScene().getObstacles();
    HashMap<Integer, Integer> sortedPearlsX = new HashMap<>();
    HashMap<Integer, Integer> sortedPearlsY = new HashMap<>();
    Point newDirection;
    ArrayList<Point2D> freespace = new ArrayList<>();

    public Work_in_Cringness(Info info){
        super(info);
        Arrays.sort(pearlsTemp);
        getSortedPearls();
        testing();
    }

    @Override
    public String getName() {
        return "Work_in_Crigne";
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }

    @Override
    public PlayerAction update() {

        float speed = info.getMaxAcceleration(); // max speed
        score = info.getScore();
        Point[] fishies = info.getScene().getFish(); // Fische

        if(tempRising){ richtung = riseAndShine();
        }else {
            for (Path2D obstacle : obstacles) {
                if (obstacle.contains(info.getX(), info.getY() - 3)) { // obstacle unten
                    tempRising = true;
                    reisMeter = 0;
                }
                if (obstacle.contains(info.getX(), info.getY() + 3)) { // obstacle oben
                    tempRising = true;
                    tempUp = true;
                    reisMeter = 0;
                }
                if (obstacle.contains(info.getX() - 3, info.getY())) { // obstacle links
                    tempRising = true;
                    tempLeft = true;
                    reisMeter = 0;
                }
                if (obstacle.contains(info.getX() + 3f, info.getY())) { // obstacle rechts
                    tempRising = true;
                    tempRight = true;
                    reisMeter = 0;
                }
            }
            richtung = goToPearl();
        }

        if(pearlsTemp[ownScore] == info.getX() && sortedPearlsY.get(ownScore) == info.getY()){
            ownScore = ownScore+1;
        }

        return new DivingAction(speed,richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }

    public float goToPearl(){
        newDirection = new Point(pearlsTemp[ownScore] - info.getX() ,sortedPearlsY.get(ownScore) - info.getY() );
        richtung = (float)Math.atan2(newDirection.getY(), newDirection.getX());

        return richtung;
    }

    public float goToPearl2(){
        newDirection = new Point(pearlsTemp[ownScore] - info.getX() ,sortedPearlsY.get(ownScore) - info.getY() );
        richtung = (float)Math.atan2(newDirection.getY(), newDirection.getX());

        return richtung;
    }

    public void testing(){
        for(int y = 0; y < info.getScene().getHeight(); y+=10){
            for(int x = 0; x < info.getScene().getWidth(); x+=10){
                if (freiBier(x, -y)) {
                   freespace.add(new Point(x+5, -y-5));
                }
                System.out.print(freiBier(x,-y) ? "." : "#");
            }
            System.out.println();
        }
    }
    public boolean freiBier(int x, int y){
        for (Path2D obstacle : obstacles) {
            if (obstacle.intersects(x, y, 10,10)){
                return false;
            }
        }
        return true;
    }
//    public void Dietrying(){
//
//    }

    public void getSortedPearls(){
        for(int i = 0; i < pearl.length; i++){
            sortedPearlsX.put((int)pearl[i].getX(), i);
        }
        for(int i = 0; i < pearl.length; i++){
            sortedPearlsY.put(i, (int)pearl[sortedPearlsX.get(pearlsTemp[i])].getY());
        }
    }

    public float riseAndShine(){
        float direction = info.getOrientation();

        if(reisMeter >= 25){
            tempRising = false;
            tempRight = false;
            tempLeft = false;
            tempUp = false;
        }

        if(tempRising){ // nach oben
            direction = (float)Math.PI/2 - reisMeter/25f;
        }
        if(tempRight){ // nach links
            direction = (float)Math.PI - reisMeter/12.5f;
        }
        if(tempLeft){ // nach rechts
            direction = 0 - reisMeter/12.5f;
        }
        if(tempUp){ // nach unten
            direction = -(float)Math.PI/2 - reisMeter/12.5f;
        }
        reisMeter++;
        return direction;
    }
}



//class Node{
//
//    ArrayList<Node> nodes = new ArrayList<>();
//    Point2D p;
//
//    public Node(Point2D p){
//        this.p = p;
//    }
//
//    public Point2D getPoint(){
//        return this.p;
//    }
//
////    public Point2D setNeightbour(){
////
////    }
//}