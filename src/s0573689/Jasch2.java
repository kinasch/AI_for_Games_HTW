package s0573689;

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

public class Jasch2 extends AI {

    float richtung;
    int score = info.getScore();
    int pathProgression = 0;

    Point2D[] pearl = info.getScene().getPearl(); // ziele
    Point2D[] currentPearl;
    Path2D[] obstacles = info.getScene().getObstacles();
    ArrayList<Point2D> freespace = new ArrayList<>();
    Graph nodeGraph = new Graph();
    ArrayList<Node> pearlNodes = new ArrayList<>();
    ArrayList<Node> removedPearlNodes = new ArrayList<>();

    int w = 10;

    public Jasch2(Info info) {
        super(info);
        testing();
        dijsktrastuffStart();
        dijsktrastuffRepeat();
    }

    @Override
    public String getName() {
        return "JaschWIP";
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

        score = info.getScore();

        Point2D notfall=null;
        for(Point2D point : pearl){
            if(isBetween(point.getX(),pearlNodes.get(0).getName().getX()-10,pearlNodes.get(0).getName().getX()+10) && isBetween(point.getY(),pearlNodes.get(0).getName().getY()-10,pearlNodes.get(0).getName().getY()+10)){
                notfall = point;
            }
        }
        if(notfall == null){
            notfall = pearlNodes.get(0).getName();
        }

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

        return new DivingAction(speed, richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }


    public void goToPearl(Point2D start, Point2D target) {
        Point newDirection = new Point((int) (target.getX() - start.getX()), (int) (target.getY() - start.getY()));
        if(!target.equals(start)) {
            richtung = (float) Math.atan2(newDirection.getY(), newDirection.getX());
        }

        int bound = 1;
        if (isBetween(info.getX(), target.getX() - bound, target.getX() + bound) && isBetween(info.getY(), target.getY() - bound,
                target.getY() + bound)) {
            pathProgression++;
        }

    }

    public void testing() {
        for (int y = 0; y < info.getScene().getHeight(); y += w) {
            for (int x = 0; x < info.getScene().getWidth(); x += w) {
                if (freiBier(x, -y-w)) {
                    freespace.add(new Point(x+(int)((float)w/2), -y-(int)((float)w/2)));
                }
//                System.out.print(freiBier(x, -y) ? "." +" ": "#" + " ");
            }
//            System.out.println();
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

    /*public void getSortedPearls() {
        for (int i = 0; i < pearl.length; i++) {
            sortedPearlsX.put((int) pearl[i].getX(), i);
        }
        for (int i = 0; i < pearl.length; i++) {
            sortedPearlsY.put(i, (int) pearl[sortedPearlsX.get(pearlsTemp[i])].getY());
        }
    }*/

    public void assignPearlsToNodes() {

        pearlNodes = new ArrayList<Node>();

        for (Point2D point2D : pearl) {
            Map<Double, Node> dis = new HashMap<>();
            for (Node n : nodeGraph.getNodes()) {
                dis.put(Math.sqrt(
                        Math.pow(n.getName().getX() - point2D.getX(), 2) + Math.pow(n.getName().getY() - point2D.getY(),
                                2)), n);
            }

            ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
            Collections.sort(dListTemp);

            Node p = dis.get((Double) dListTemp.get(0));
            if(!removedPearlNodes.contains(p)){
                pearlNodes.add(p);
            }
        }

    }

    // executed only once
    public void dijsktrastuffStart() {
        long time = System.currentTimeMillis();
        for (Point2D point : freespace) {
            Node n = new Node(point);
            nodeGraph.addNode(n);
        }

        for (Node n : nodeGraph.getNodes()) {
            for (Node neighbour : nodeGraph.getNodes()) {
                if (isBetween(neighbour.getName().getX(), n.getName().getX() - w, n.getName().getX() + w)
                        && isBetween(neighbour.getName().getY(), n.getName().getY() - w, n.getName().getY() + w)
                        && n.getName() != neighbour.getName()) {
                    int distance = neighbour.getName().getX() == n.getName().getX() || neighbour.getName().getY() == n.getName().getY() ? w : (int)Math.floor(Math.sqrt(w*w+w*w));
                    n.adjacentNodes.put(neighbour, distance);
                }
            }
        }

        // Debugging the distances
        /*for (Node nTest : nodeGraph.getNodes()) {
            nTest.adjacentNodes.keySet().forEach(node -> {
                System.out.print(node.getName() + " | ");
            });
            System.out.print("\n");
            System.out.println(nTest.adjacentNodes.values());

            break;
        }*/

        assignPearlsToNodes();

        System.err.println("Dijsktrastart in ms:" + (System.currentTimeMillis() - time));
    }

    // Repeated dijsktra
    public void dijsktrastuffRepeat() {
        long time = System.currentTimeMillis();
        pathProgression = 0;


        Node source = null;
        for (Node n : nodeGraph.getNodes()) {
            if (n.getName().getX() == (Math.floorMod(info.getX(), w)) * w + (float)w/2 && n.getName().getY() == (Math.floorMod(
                    info.getY(), w)) * w + (float)w/2) {
                source = n;
            }
        }
        if (source == null) {
            Map<Double, Node> dis = new HashMap<>();
            for (Node n : nodeGraph.getNodes()) {
                dis.put(Math.sqrt(
                        Math.pow(n.getName().getX() - info.getX(), 2) + Math.pow(n.getName().getY() - info.getY(), 2)),
                        n);
            }

            ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
            Collections.sort(dListTemp);

            source = dis.get(dListTemp.get(0));
        }

        for(Node n: nodeGraph.getNodes()){
            n.setShortestPath(new LinkedList<>());
            n.setDistance(Integer.MAX_VALUE);
        }

        nodeGraph = Dijkstra.calculateShortestPathFromSource(nodeGraph, source);

        assignPearlsToNodes();

        pearlNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Integer.compare(o1.getDistance(), o2.getDistance());
            }
        });

        System.out.println("Source: " + source.getName());
        pearlNodes.get(0).getShortestPath().forEach(node -> {
            System.out.print(node.getName() + " | ");
        });
        System.out.println();
        //  //source: Taucher

        /* nodeGraph -> Ziel (Perle)
         *  Ziel immer zum nächsten Vorgänger
         *  immer in List speichern oder so
         *  sobald Vorgänger source, ablaufen
         *
         *  wiederholen
         */


        // Position des Tauchers, Perle nähe Node, graph?

        System.err.println("DijsktraRepeated in ms:" + (System.currentTimeMillis() - time));
    }

    public boolean isBetween(double valueToBeChecked, double lowerBound, double upperBound) {
        if (valueToBeChecked <= upperBound && valueToBeChecked >= lowerBound) {
            return true;
        }
        return false;
    }


}

// Code taken from: https://www.baeldung.com/java-dijkstra
class Node {

    private Point2D point;

    private List<Node> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(Point2D point) {
        this.point = point;
    }

    // getters and setters

    public Point2D getName() {
        return point;
    }

    public void setName(Point2D point) {
        this.point = point;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<Node, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }
}

class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
    public Set<Node> getNodes() {
        return nodes;
    }
}

class Dijkstra {

    public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Integer> adjacencyPair :
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
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

    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node : unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
