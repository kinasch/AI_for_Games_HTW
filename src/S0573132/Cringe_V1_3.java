package S0573132;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;

import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.HashMap;

public class Cringe_V1_3 extends AI {

    float richtung;
    float tempOrientation;
    int score, reisMeter, hoffentlichindex, tempX, tempY;
    int ownScore = 0;

    boolean tempRising;

    Point[] pearl = info.getScene().getPearl(); // ziele
    int[] pearlsTemp = new int[]{pearl[0].x, pearl[1].x, pearl[2].x, pearl[3].x, pearl[4].x, pearl[5].x, pearl[6].x, pearl[7].x, pearl[8].x, pearl[9].x};
    Path2D[] obstacles = info.getScene().getObstacles();
    HashMap<Integer, Integer> sortedPearlsX = new HashMap<>();
    HashMap<Integer, Integer> sortedPearlsY = new HashMap<>();
    Point newDirection;

    public Cringe_V1_3(Info info) {
        super(info);
//        enlistForTournament(573132, 573689);
        Arrays.sort(pearlsTemp);
        getSortedPearls();
    }

    @Override
    public String getName() {
        return "Cringe_V1.3";
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public PlayerAction update() {

        float speed = info.getMaxAcceleration(); // max speed
        score = info.getScore();

        if (tempRising) {
            richtung = riseAndShine();
        } else {
            richtung = goToPearl();

            if (System.currentTimeMillis() % 10 == 0 && tempX == info.getX() && tempY == info.getY()) {
                tempOrientation = info.getOrientation();
                tempRising = true;
                reisMeter = 0;
            }
        }


        if (pearlsTemp[ownScore] == info.getX() && sortedPearlsY.get(ownScore) == info.getY()) {
            ownScore = ownScore + 1;
        }

        tempX = info.getX();
        tempY = info.getY();

        return new DivingAction(speed, richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }

    public float goToPearl() {

        newDirection = new Point(pearlsTemp[ownScore] - info.getX(), sortedPearlsY.get(ownScore) - info.getY());
        richtung = (float) Math.atan2(newDirection.getY(), newDirection.getX());


        return richtung;
    }

    public void getSortedPearls() {
        for (int i = 0; i < pearl.length; i++) {
            sortedPearlsX.put((int) pearl[i].getX(), i);
        }
        for (int i = 0; i < pearl.length; i++) {
            sortedPearlsY.put(i, (int) pearl[sortedPearlsX.get(pearlsTemp[i])].getY());
        }
    }

    public float riseAndShine() {
        float direction = info.getOrientation();

        if (reisMeter >= 40) {
            tempRising = false;
        }

        if (tempRising) {
            direction = tempOrientation + (float) Math.PI / 2 - reisMeter / 40f;
        }
        reisMeter++;

        return direction;
    }

}