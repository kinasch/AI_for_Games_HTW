package S0573132;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;
import java.awt.geom.Path2D;

import java.awt.geom.Point2D;
import java.util.HashMap;

import java.util.*;
import java.util.List;

public class Work_in_Cringness extends AI {

    float richtung;
    int score = info.getScore();
    int pathProgression = 0, pathProgression2 = 1;
    int w = 10;
    boolean airbool = true, boolbool = true, unknownbool = false;

    Point2D[] pearl = info.getScene().getPearl(); // ziele
    Point2D[] currentPearl;
    Path2D[] obstacles = info.getScene().getObstacles();
    ArrayList<Point2D> freespace = new ArrayList<>();
    Graph_WIC nodeGraph = new Graph_WIC();
    ArrayList<Node_WIC> pearlNodes = new ArrayList<>();
    ArrayList<Node_WIC> removedPearlNodes = new ArrayList<>();
    ArrayList<Node_WIC> tempTarget;

    public Work_in_Cringness(Info info) {
        super(info);
        testing();
        enlistForTournament(573132, 573689);
        dijsktrastuffStart();
        dijsktrastuffRepeat();
    }

    @Override
    public String getName() {
        return "WIC (+25)";
    }

    @Override
    public Color getColor() {
        return Color.black;
    }

    @Override
    public PlayerAction update() {

        float speed = info.getMaxAcceleration(); // max speed
        if (score < info.getScore()) {
            removedPearlNodes.add(pearlNodes.remove(0));
            dijsktrastuffRepeat();
        }


        if (info.getAir() == info.getMaxAir() && !isBetween(info.getX(), pearlNodes.get(0).getName().getX() - 4, pearlNodes.get(0).getName().getX() + 4)) {
            richtung = pearlNodes.get(0).getName().getX() < info.getX() ? (float) Math.PI : 0;
            return new DivingAction(speed, richtung);
        }

        if (info.getAir() == info.getMaxAir() && isBetween(info.getX(), pearlNodes.get(0).getName().getX() - 4, pearlNodes.get(0).getName().getX() + 4)) {
            dijsktrastuffRepeat();
            if (pearlNodes.size() < 3) {
                unknownbool = true;
            }
        }

        score = info.getScore();

        Point2D notfall = null;
        for (Point2D point : pearl) {
            if (isBetween(point.getX(), pearlNodes.get(0).getName().getX() - 10, pearlNodes.get(0).getName().getX() + 10) && isBetween(point.getY(), pearlNodes.get(0).getName().getY() - 10, pearlNodes.get(0).getName().getY() + 10)) {
                notfall = point;
            }
        }
        if (notfall == null) {
            notfall = pearlNodes.get(0).getName();
        }

        if (airbool || unknownbool) {
            if (pathProgression < pearlNodes.get(0).getShortestPath().size() - 1) {
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
        } else {
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
                for (Node_WIC node : this.nodeGraph.getNodes()) {
                    if (isBetween(node.getName().getX(), info.getX() - 4, info.getX() + 4) && node.getName().getY() > -10) {
                        tempTarget = new ArrayList<>(node.getShortestPath());
                        tempTarget.add(node);
                    }
                }
            }
        }

        return new DivingAction(speed, richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }


    // Vielleicht können wir den Taucher an der Oberfläche schwimmen lassen
    // bis er über der Perle ist und dann Wegesuche betreiben.

    public void goToPearl(Point2D start, Point2D target) {
        if (info.getAir() == info.getMaxAir()/*info.getY() == 0 && score !=0*/) {
            airbool = true;
            boolbool = true;
        }

        if (info.getAir() < -info.getY() + 25) {
            airbool = false;
            if (boolbool) {
                dijsktrastuffRepeat(); //zum nach oben schwimmen
            }
            boolbool = false;
        }

        if (airbool || unknownbool) {
            tempTarget = null;
            Point newDirection = new Point((int) (target.getX() - start.getX()), (int) (target.getY() - start.getY()));
            if (!target.equals(start)) {
                richtung = (float) Math.atan2(newDirection.getY(), newDirection.getX());
            }
            int bound = 1;
            if (isBetween(info.getX(), target.getX() - bound, target.getX() + bound) && isBetween(info.getY(), target.getY() - bound,
                    target.getY() + bound)) {
                pathProgression++;
            }
        } else { // setze neue ziel oberfläche
            if (tempTarget == null) {
                for (Node_WIC node : this.nodeGraph.getNodes()) {
                    if (isBetween(node.getName().getX(), info.getX() - 4, info.getX() + 4) && node.getName().getY() > -10) {
                        tempTarget = new ArrayList<>(node.getShortestPath());
                        tempTarget.add(node);
                    }
                }
            }
            int bound = 1;
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
        for (int y = 0; y < info.getScene().getHeight(); y += w) {
            for (int x = 0; x < info.getScene().getWidth(); x += w) {
                if (freiBier(x, -y - w)) {
                    freespace.add(new Point(x + (int) ((float) w / 2), -y - (int) ((float) w / 2)));
                }
            }
        }
    }

    public boolean freiBier(int x, int y) {
        for (Path2D obstacle : obstacles) {
            if (obstacle.intersects(x, y, w, w)) {
                return false;
            }
        }
        return true;
    }

    public void assignPearlsToNodes() {

        pearlNodes = new ArrayList<Node_WIC>();

        for (Point2D point2D : pearl) {
            Map<Double, Node_WIC> dis = new HashMap<>();
            for (Node_WIC n : nodeGraph.getNodes()) {
                dis.put(Math.sqrt(Math.pow(n.getName().getX() - point2D.getX(), 2) + Math.pow(n.getName().getY() - point2D.getY(), 2)), n);
            }

            ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
            Collections.sort(dListTemp);

            Node_WIC p = dis.get((Double) dListTemp.get(0));
            if (!removedPearlNodes.contains(p)) {
                pearlNodes.add(p);
            }
        }

    }

    // executed only once
    public void dijsktrastuffStart() {
        long time = System.currentTimeMillis();
        for (Point2D point : freespace) {
            Node_WIC n = new Node_WIC(point);
            nodeGraph.addNode(n);
        }

        for (Node_WIC n : nodeGraph.getNodes()) {
            for (Node_WIC neighbour : nodeGraph.getNodes()) {
                if (isBetween(neighbour.getName().getX(), n.getName().getX() - w, n.getName().getX() + w)
                        && isBetween(neighbour.getName().getY(), n.getName().getY() - w, n.getName().getY() + w)
                        && n.getName() != neighbour.getName()) {
                    int distance = neighbour.getName().getX() == n.getName().getX() || neighbour.getName().getY() == n.getName().getY() ? w : (int) Math.floor(Math.sqrt(w * w + w * w));
                    n.adjacentNodes.put(neighbour, distance);
                }
            }
        }

        assignPearlsToNodes();
        ;
    }

    // Repeated dijsktra
    public void dijsktrastuffRepeat() {
        long time = System.currentTimeMillis();
        pathProgression = 0;
        pathProgression2 = 1;


        Node_WIC source = null;
        for (Node_WIC n : nodeGraph.getNodes()) {
            if (n.getName().getX() == (Math.floorMod(info.getX(), w)) * w + (float) w / 2 && n.getName().getY() == (Math.floorMod(
                    info.getY(), w)) * w + (float) w / 2) {
                source = n;
            }
        }
        if (source == null) {
            Map<Double, Node_WIC> dis = new HashMap<>();
            for (Node_WIC n : nodeGraph.getNodes()) {
                dis.put(Math.sqrt(Math.pow(n.getName().getX() - info.getX(), 2) + Math.pow(n.getName().getY() - info.getY(), 2)), n);
            }

            ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
            Collections.sort(dListTemp);

            source = dis.get(dListTemp.get(0));
        }

        for (Node_WIC n : nodeGraph.getNodes()) {
            n.setShortestPath(new LinkedList<>());
            n.setDistance(Integer.MAX_VALUE);
        }

        nodeGraph = Dijkstra_WIC.calculateShortestPathFromSource(nodeGraph, source);

        assignPearlsToNodes();

        pearlNodes.sort(new Comparator<Node_WIC>() {
            @Override
            public int compare(Node_WIC o1, Node_WIC o2) {
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
class Node_WIC {

    private Point2D point;

    private List<Node_WIC> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<Node_WIC, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node_WIC destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node_WIC(Point2D point) {
        this.point = point;
    }

    // getters and setters

    public Point2D getName() {
        return point;
    }

    public void setName(Point2D point) {
        this.point = point;
    }

    public List<Node_WIC> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<Node_WIC> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<Node_WIC, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<Node_WIC, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }
}

class Graph_WIC {

    private Set<Node_WIC> nodes = new HashSet<>();

    public void addNode(Node_WIC nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
    public Set<Node_WIC> getNodes() {
        return nodes;
    }
}

class Dijkstra_WIC {

    public static Graph_WIC calculateShortestPathFromSource(Graph_WIC graph, Node_WIC source) {
        source.setDistance(0);

        Set<Node_WIC> settledNodes = new HashSet<>();
        Set<Node_WIC> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node_WIC currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);

            for (Map.Entry<Node_WIC, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node_WIC adjacentNode = adjacencyPair.getKey();
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

    private static Node_WIC getLowestDistanceNode(Set<Node_WIC> unsettledNodes) {
        Node_WIC lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node_WIC node : unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node_WIC evaluationNode, Integer edgeWeigh, Node_WIC sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node_WIC> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
