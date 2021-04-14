package s0573689;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;

public class s0573689_01 extends AI {

    private boolean tempRising = false;
    private int riseMeter = 0;
    private Point[] pearls;
    private int counter=0;


    public s0573689_01(Info info) {
        super(info);
        // one (only one) AI should enlist in the tournament at the end of the exercise
        // enlistForTournament(573689);

        // Register where the pearls are and store them in an Array.
        pearls = pearlSort();
    }

    @Override
    public String getName() {
        return "Jasch";
    }

    @Override
    public Color getColor() {
        return Color.MAGENTA;
    }

    @Override
    public PlayerAction update() {

        // steering
        float direction=0;

        if (tempRising) {
            direction = rising();
        } else {

            // Register where the obstacles are and store them in an Array.
            Path2D[] obstacles = info.getScene().getObstacles();
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
            if (obstacles[0].contains(info.getX(), info.getY()-5)) {    // Obstacle Collision detection
                direction += (float) -Math.PI/2;
            }

            /*
             *  The next part is a calculation of the angle between the downwards vector and
             *  the vector from the diver to the goal.
             */
            direction = goToPearl(pearls);
        }

        return new DivingAction(info.getMaxAcceleration(), direction);
    }

    private float rising() {
        if (riseMeter >= 50) {
            tempRising = false;
        }
        riseMeter++;
        return (float) Math.PI / 2;
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

        // Calculates the absolute value of the vector between the current Position and the Goal.
        float goalABS = (float) (Math.sqrt((Math.pow(currentPosToGoal.getX(), 2) + Math.pow(currentPosToGoal.getY(), 2))));

        // Actual calculation of the angle.
        direction = (float) ((Math.PI * 2) - (Math.acos(currentPosToGoal.getX() / goalABS)));

        // direction: goal.Y / abs(vector(goal))#
        //System.out.println(counter+" || "+pearls[1].getX()+" "+pearls[counter].getY()+" | "+goalABS+" "+direction);

        // Idea: Mark the collected pearls as complete with null.
        // TODO: Collection detection
        if (counter < info.getScore()
                /*pearls[counter].getX() >= info.getX() - 6 && pearls[counter].getX() <= info.getX() + 6
                && pearls[counter].getY() >= info.getY() - 6 && pearls[counter].getY() <= info.getY() + 6*/) {
            counter = info.getScore();
            direction = (float) Math.PI /2;
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
