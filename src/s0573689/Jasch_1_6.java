package s0573689;

import lenz.htw.ai4g.ai.*;

import java.awt.*;
import java.awt.geom.Path2D;

import java.awt.geom.Point2D;
import java.util.HashMap;

import java.util.*;
import java.util.List;

public class Jasch_1_6 extends AI {

    float richtung;
    int score = info.getScore();
    int fortune = info.getFortune();
    int pathProgression = 0, pathProgression2 = 1;
    int nodeSize = 10;
    boolean airbool = true, boolbool = true, unknownbool = false, updated = false;

    Point2D[] pearl = info.getScene().getPearl(); // ziele
    Point2D[] fortunes = info.getScene().getRecyclingProducts(); // flaschen
    Path2D[] obstacles = info.getScene().getObstacles();
    ArrayList<Point2D> freespace = new ArrayList<>();
    GraphV6 nodeGraph = new GraphV6();
    ArrayList<NodeV6> pearlNodes = new ArrayList<>();
    ArrayList<NodeV6> tempTarget;
    ArrayList<NodeV6> fortuneNodes = new ArrayList<>();


    public Jasch_1_6(Info info) {
        super(info);
        testing();
        //enlistForTournament(573132, 573689);
        dijsktrastuffStart();
        assignfortuneToNodes();
        dijsktrastuffRepeat();

    }

    @Override
    public String getName() {
        return "cut";
    }

    @Override
    public Color getColor() {
        return Color.black;
    }

    @Override
    public PlayerAction update() {

        if (info.getFortune() >= 2 && info.getX() == info.getScene().getShopPosition() && info.getY() == 0 && !updated) {
            updated = true;
            return new ShoppingAction(ShoppingItem.CORNER_CUTTER);
        }


        float speed = info.getMaxAcceleration(); // max speed

        //zählt eingesammelte perlen
        if (score < info.getScore()) {
            pearlNodes.remove(0);
            dijsktrastuffRepeat();
        }
        if(fortune < info.getFortune()&& !updated){
            fortuneNodes.remove(0);
            dijsktrastuffRepeat();
        }
        score = info.getScore(); //updates score
        fortune = info.getFortune(); //updates fortune

        if(info.getAir() == info.getMaxAir() && updated){
            if(isBetween(info.getX(), pearlNodes.get(0).getName().getX() - 4, pearlNodes.get(0).getName().getX() + 4)){ //luft ignorieren bei letzen 2 perlen
                dijsktrastuffRepeat();
                if (pearlNodes.size() < 3) {
                    unknownbool = true;
                }
            }else{ //an der oberfläche schwimmen
                richtung = pearlNodes.get(0).getName().getX() < info.getX() ? (float) Math.PI : 0;
                return new DivingAction(speed, richtung);
            }
        }

        Point2D notfall = createEmergencyPoint(pearlNodes, pearl);
        Point2D notfall2 = createEmergencyPoint(fortuneNodes, fortunes);

        if(fortune >= 2 && !updated){
            swimStraightUp();
            if(info.getAir() == info.getMaxAir()){ //an der oberfläche schwimmen
                richtung = info.getScene().getShopPosition() < info.getX() ? (float) Math.PI : 0;
                return new DivingAction(speed, richtung);
            }
        } else if (airbool || unknownbool) { //schwimmt zur perle
            if(info.getFortune() < 2 && pathProgression < fortuneNodes.get(0).getShortestPath().size() -1 && !updated){
                goToPearl(new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return info.getY();
                    }

                    @Override
                    public void setLocation(double a, double b) {
                    }
                }, fortuneNodes.get(0).getShortestPath().get(pathProgression).getName());
            }else if(info.getFortune() < 2 && pathProgression >= fortuneNodes.get(0).getShortestPath().size() -1 && !updated){
                goToPearl(new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return info.getY();
                    }

                    @Override
                    public void setLocation(double a, double b) {
                    }
                }, notfall2);
            } else if (pathProgression < pearlNodes.get(0).getShortestPath().size() - 1) {
                goToPearl(new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return info.getY();
                    }

                    @Override
                    public void setLocation(double a, double b) {
                    }
                }, pearlNodes.get(0).getShortestPath().get(pathProgression).getName());
            } else {
                goToPearl(new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return info.getY();
                    }

                    @Override
                    public void setLocation(double a, double b) {
                    }
                }, notfall);
            }
        } else { //schwimmt zur oberfläche über dem taucher
            swimStraightUp();
        }
        return new DivingAction(speed, richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }

    private void swimStraightUp() {
        airbool = false;
        if (tempTarget != null) {
            if (pathProgression2 < tempTarget.size() - 1) {
                goToPearl(new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return info.getY();
                    }

                    @Override
                    public void setLocation(double a, double b) {
                    }
                }, tempTarget.get(pathProgression2).getName());
            } else {
                goToPearl(new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return info.getY();
                    }

                    @Override
                    public void setLocation(double a, double b) {
                    }
                }, new Point2D() {
                    @Override
                    public double getX() {
                        return info.getX();
                    }

                    @Override
                    public double getY() {
                        return 0;
                    }

                    @Override
                    public void setLocation(double x, double y) {

                    }
                });
            }
        } else {
            for (NodeV6 node : this.nodeGraph.getNodes()) {
                if (isBetween(node.getName().getX(), info.getX() - 4, info.getX() + 4) && node.getName().getY() > -10) {
                    tempTarget = new ArrayList<>(node.getShortestPath());
                    tempTarget.add(node);
                }
            }
        }
    }
    private Point2D createEmergencyPoint(ArrayList<NodeV6> listName, Point2D[] array) {
        Point2D notfall = null;
        for (Point2D point : array) {
            if (isBetween(point.getX(), listName.get(0).getName().getX() - 10, listName.get(0).getName().getX() + 10) && isBetween(point.getY(), listName.get(0).getName().getY() - 10, listName.get(0).getName().getY() + 10)) {
                notfall = point;
            }
        }
        if (notfall == null) {
            notfall = listName.get(0).getName();
        }
        return notfall;
    }


    public void goToPearl(Point2D start, Point2D target) {
        if (info.getAir() == info.getMaxAir()/*info.getY() == 0 && score !=0*/) {
            airbool = true;
            boolbool = true;
        }

        if(!updated){
            if (info.getAir() < info.getMaxAir()/2 + 25 && info.getAir() < -(info.getY() +15)) {
                airbool = false;
                if (boolbool) {
                    dijsktrastuffRepeat(); //zum nach oben schwimmen
                }
                boolbool = false;
            }
        }else{
            if (info.getAir() < info.getMaxAir()/4 + 25 && info.getAir() < -(info.getY() +15)) {
                airbool = false;
                if (boolbool) {
                    dijsktrastuffRepeat(); //zum nach oben schwimmen
                }
                boolbool = false;
            }
        }

        if (airbool || unknownbool) {
            tempTarget = null;
            Point newDirection = new Point((int) (target.getX() - start.getX()), (int) (target.getY() - start.getY()));
            if (!target.equals(start)) {
                richtung = (float) Math.atan2(newDirection.getY(), newDirection.getX());
            }
            int bound = 2;
            if (isBetween(info.getX(), target.getX() - bound, target.getX() + bound) && isBetween(info.getY(), target.getY() - bound,
                    target.getY() + bound)) {
                pathProgression++;
            }
        } else { // setze neue ziel oberfläche
            int bound = 2;
            if (isBetween(info.getX(), target.getX() - bound, target.getX() + bound) && isBetween(info.getY(), target.getY() - bound,
                    target.getY() + bound)) {
                pathProgression2++;
            }
            Point newDirection = new Point((int) (target.getX() - start.getX()), (int) (target.getY() - start.getY()));
            if (!target.equals(start)) {
                richtung = (float) Math.atan2(newDirection.getY(), newDirection.getX());
            }
        }

    }

    public void testing() {
        for (int y = 0; y < info.getScene().getHeight(); y += nodeSize) {
            for (int x = 0; x < info.getScene().getWidth(); x += nodeSize) {
                if (freiBier(x, -y - nodeSize)) {
                    freespace.add(new Point(x + (int) ((float) nodeSize / 2), -y - (int) ((float) nodeSize / 2)));
                }
            }
        }
    }

    public boolean freiBier(int x, int y) {
        for (Path2D obstacle : obstacles) {
            if (obstacle.intersects(x, y, nodeSize, nodeSize)) {
                return false;
            }
        }
        return true;
    }

    public void assignPearlsToNodes() {
        pearlNodes = new ArrayList<NodeV6>();
        for (Point2D point2D : pearl) {
            pearlNodes.add(nearestNode(point2D.getX(), point2D.getY()));
        }
    }
    public void assignfortuneToNodes() {
        fortuneNodes = new ArrayList<NodeV6>();
        for (Point2D point2D : fortunes) {
            fortuneNodes.add(nearestNode(point2D.getX(), point2D.getY()));
        }
    }

    public NodeV6 nearestNode(double x, double y){
        Map<Double, NodeV6> dis = new HashMap<>();
        for (NodeV6 n : nodeGraph.getNodes()) {
            dis.put(Math.sqrt(Math.pow(n.getName().getX() - x, 2) + Math.pow(n.getName().getY() - y, 2)), n);
        }

        ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
        Collections.sort(dListTemp);

        return dis.get(dListTemp.get(0));
    }
    // executed only once
    public void dijsktrastuffStart() {
        for (Point2D point : freespace) {
            NodeV6 n = new NodeV6(point);
            nodeGraph.addNode(n);
        }

        for (NodeV6 n : nodeGraph.getNodes()) {
            for (NodeV6 neighbour : nodeGraph.getNodes()) {
                if (isBetween(neighbour.getName().getX(), n.getName().getX() - nodeSize, n.getName().getX() + nodeSize) && isBetween(neighbour.getName().getY(), n.getName().getY() - nodeSize, n.getName().getY() + nodeSize) && n.getName() != neighbour.getName()) {
                    int distance = neighbour.getName().getX() == n.getName().getX() || neighbour.getName().getY() == n.getName().getY() ? nodeSize : (int) Math.floor(Math.sqrt(nodeSize * nodeSize + nodeSize * nodeSize)); //dont ask
                    n.adjacentNodes.put(neighbour, distance);
                }
            }
        }
        assignPearlsToNodes();
    }

    // Repeated dijsktra
    public void dijsktrastuffRepeat() {
        pathProgression = 0;
        pathProgression2 = 1;

        NodeV6 source = null;
        for (NodeV6 node : nodeGraph.getNodes()) {
            if (node.getName().getX() == (Math.floorMod(info.getX(), nodeSize)) * nodeSize + (float) nodeSize / 2 && node.getName().getY() == (Math.floorMod(info.getY(), nodeSize)) * nodeSize + (float) nodeSize / 2) {
                source = node;
                break;
            }
        }

        if (source == null) {
            source = nearestNode(info.getX(), info.getY());
        }

        for (NodeV6 n : nodeGraph.getNodes()) {
            n.setShortestPath(new LinkedList<>());
            n.setDistance(Integer.MAX_VALUE);
        }

        nodeGraph = DijkstraV6.calculateShortestPathFromSource(nodeGraph, source);

        pearlNodes.sort(new Comparator<NodeV6>() {
            @Override
            public int compare(NodeV6 o1, NodeV6 o2) {
                return Integer.compare(o1.getDistance(), o2.getDistance());
            }
        });
        fortuneNodes.sort(new Comparator<NodeV6>() {
            @Override
            public int compare(NodeV6 o1, NodeV6 o2) {
                return Integer.compare(o1.getDistance(), o2.getDistance());
            }
        });
    }

    public boolean isBetween(double valueToBeChecked, double lowerBound, double upperBound) {
        if (valueToBeChecked <= upperBound && valueToBeChecked >= lowerBound) {
            return true;
        }
        return false;
    }
}

// Code taken from: https://www.baeldung.com/java-dijkstra
class NodeV6 {

    private Point2D point;

    private List<NodeV6> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<NodeV6, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(NodeV6 destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public NodeV6(Point2D point) {
        this.point = point;
    }

    // getters and setters

    public Point2D getName() {
        return point;
    }

    public void setName(Point2D point) {
        this.point = point;
    }

    public List<NodeV6> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<NodeV6> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<NodeV6, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<NodeV6, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }
}

class GraphV6 {

    private Set<NodeV6> nodes = new HashSet<>();

    public void addNode(NodeV6 nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
    public Set<NodeV6> getNodes() {
        return nodes;
    }
}

class DijkstraV6 {

    public static GraphV6 calculateShortestPathFromSource(GraphV6 graph, NodeV6 source) {
        source.setDistance(0);

        Set<NodeV6> settledNodes = new HashSet<>();
        Set<NodeV6> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            NodeV6 currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);

            for (Map.Entry<NodeV6, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                NodeV6 adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();

                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private static NodeV6 getLowestDistanceNode(Set<NodeV6> unsettledNodes) {
        NodeV6 lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (NodeV6 node : unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(NodeV6 evaluationNode, Integer edgeWeigh, NodeV6 sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<NodeV6> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
