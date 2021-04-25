package s0573689;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;
import java.awt.geom.Path2D;

import java.util.Arrays;
import java.util.HashMap;

import java.util.*;
import java.util.List;

public class s0573689_01_2 extends AI {

    private boolean tempRising = false;
    private boolean obsRight,obsLeft,obsUp,obsDown;
    private float risingDirection;

    private int riseMeter = 0;
    private Point[] pearls;
    private Path2D[] obstacles;
    private int counter=0;


    public s0573689_01_2(Info info) {
        super(info);
        // one (only one) AI should enlist in the tournament at the end of the exercise
        enlistForTournament(573689);
        // Register where the pearls are and store them in an Array.
        pearls = pearlSort();

        obstacles = info.getScene().getObstacles();
    }

    @Override
    public String getName() {
        return "Jasch2";
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    @Override
    public PlayerAction update() {

        // steering
        float direction=0;

        if (tempRising) {
            direction = rising(risingDirection);
        } else {

            // Register where the obstacles are and store them in an Array.

            /*
                TODO: register obstacles
            *   Idea: Go for the pearls, if there's an obstacle in the way, change direction vector to
            *   orthographic of vector from diver to nearest point of the obstacle.
            *   Whether this direction vector has a positive or negative y component is up to the
            *   direction it was headed to before.
            *   If the diver is closer to a shell than to the obstacle, go for the shell.
            *
            *   Alt: Avoid obstacle until d.x == p.x and d.y > p.y
            */


            /*
             *  The next part is a calculation of the angle between the downwards vector and
             *  the vector from the diver to the goal.
             */
            for (Path2D obstacle : obstacles) {
                // Down
                if (obstacle.contains(info.getX(), info.getY() - 2)) {
                    tempRising = true;
                    risingDirection = (float) Math.PI / 2;
                    obsDown = true;
                    riseMeter = 0;
                }
                // Right
                if (obstacle.contains(info.getX() + 2, info.getY())) {
                    tempRising = true;
                    risingDirection = (float) Math.PI;
                    obsRight = true;
                    riseMeter = 0;
                }
                // Left
                if (obstacle.contains(info.getX() - 2, info.getY())) {
                    tempRising = true;
                    risingDirection = (float) 0;
                    obsLeft = true;
                    riseMeter = 0;
                }
                // Up
                if (obstacle.contains(info.getX(), info.getY() + 3)) {
                    tempRising = true;
                    Random rdm = new Random();
                    //int rdmInt = rdm.nextInt(100);
                    // risingDirection = rdmInt % 2 == 0 ? (float) (-Math.PI) : (float) 0;
                    risingDirection = (float)(-Math.PI/2);
                    obsUp = true;
                    riseMeter = 0;
                }
            }

            direction =  goToPearl(pearls);

            // direction += fleeWeight* fleeFromObstacle(obstacles);
        }

        return new DivingAction(info.getMaxAcceleration(), direction);
    }

    private float rising(float direction) {

        // TODO: flee from here on?

        // TODO: maybe use point between two pearls as flee point?

        // TODO: FLEE - SEEK

        if (riseMeter>=10 && obsLeft) {
            tempRising = false; //
        }
        if (riseMeter>=10 && obsRight) {
            risingDirection = (float) Math.PI / 2; //
            obsRight = false;
        }
        if (riseMeter>=10 && obsUp) {
            risingDirection = (float)Math.PI*3/4;
            obsUp = false;
        }
        if (riseMeter>=15 && obsDown) {
            risingDirection = 0; //

        }
        /*if (riseMeter>=15 && sideways){
            risingDirection = (float)Math.PI/2;
        }
        if (riseMeter>=20 && sideways){
            risingDirection = 0;
        }*/

        if(riseMeter > 20 && !obsDown){
            tempRising = false;
        }
        if(riseMeter > 35 && obsDown){
            obsDown = false;
            tempRising = false;
        }
        riseMeter++;
        return direction;
    }


    /**
     * This method calculates the angle at which the diver has to swim for the (theoretically)
     * shortest way from his position to a pearl.
     *
     * @param pearls:  array of Points which stores the location of the pearls in the scene.
     *
     * @return float direction: angle at which the diver moves relational to the x-axis.
     */
    public float goToPearl(Point[] pearls) {
        float direction = 0;

        // Calculates the vector from the current position of the diver to the goal.
        Point currentPosToGoal = new Point((int) (pearls[counter].getX() - info.getX()), (int) (pearls[counter].getY() - info.getY()));

        /*// Calculates the absolute value of the vector between the current Position and the Goal.
        float goalABS = (float) (Math.sqrt((Math.pow(currentPosToGoal.getX(), 2) + Math.pow(currentPosToGoal.getY(), 2))));

        // Actual calculation of the angle.
        direction = (float) ((Math.PI * 2) - (Math.acos(currentPosToGoal.getX() / goalABS)));
*/
        direction = (float)Math.atan2(currentPosToGoal.getY(), currentPosToGoal.getX());

        // direction: goal.Y / abs(vector(goal))#
        //System.out.println(counter+" || "+pearls[1].getX()+" "+pearls[counter].getY()+" | "+goalABS+" "+direction);

        // Idea: Mark the collected pearls as complete with null.
        // TODO: Collection detection
        if (counter < info.getScore()
                /*pearls[counter].getX() >= info.getX() - 6 && pearls[counter].getX() <= info.getX() + 6
                && pearls[counter].getY() >= info.getY() - 6 && pearls[counter].getY() <= info.getY() + 6*/) {
            counter = info.getScore();
            direction = (float) Math.PI /2;
            risingDirection = (float) Math.PI /2;
            obsDown = true;
            tempRising = true;
            riseMeter = 0;

        }
        return direction;
    }

    private Point[] pearlSort(){
        Point[] pearls = info.getScene().getPearl();
        // Map(Key: x value of pearl, Value: index of pearl)
        HashMap<Integer,Integer> pMap = new HashMap<>();
        for(int i=0;i<pearls.length;i++){
            pMap.put((int)pearls[i].getX(),i);
        }
        // List to sort the keys
        List<Integer> pearlCoords = new ArrayList<>(pMap.keySet());
        Collections.sort(pearlCoords);
        Point[] pearlsTemp = pearls.clone();
        for(int i=0;i<pearls.length;i++){
            pearlsTemp[i] = pearls[pMap.get(pearlCoords.get(i))];
        }
        pearls = pearlsTemp;
        return pearls;
    }

}
