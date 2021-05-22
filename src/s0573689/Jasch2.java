package s0573689;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

import java.awt.*;
import java.awt.geom.Path2D;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;

import java.util.*;
import java.util.List;

public class Jasch2 extends AI {

    float richtung;
    int score;
    int ownScore = 0;

    boolean tempRising, tempUp, tempLeft, tempRight;

    Point[] pearl = info.getScene().getPearl(); // ziele
    int[] pearlsTemp = new int[]{pearl[0].x, pearl[1].x, pearl[2].x, pearl[3].x, pearl[4].x, pearl[5].x, pearl[6].x, pearl[7].x, pearl[8].x, pearl[9].x};
    Path2D[] obstacles = info.getScene().getObstacles();
    HashMap<Integer, Integer> sortedPearlsX = new HashMap<>();
    HashMap<Integer, Integer> sortedPearlsY = new HashMap<>();
    Point newDirection;
    ArrayList<Point2D> freespace = new ArrayList<>();
    Graph nodeGraph = new Graph();

    public Jasch2(Info info) {
        super(info);
        Arrays.sort(pearlsTemp);
        getSortedPearls();
        testing();
        dijsktrastuff();
    }

    @Override
    public String getName() {
        return "JaschWIP";
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

        if (pearlsTemp[ownScore] == info.getX() && sortedPearlsY.get(ownScore) == info.getY()) {
            ownScore = ownScore + 1;
        }

        return new DivingAction(speed, richtung); // Bewegung = Geschwindigkeit ∙ normalisierte Richtung
    }

    public float goToPearl() {
        newDirection = new Point(pearlsTemp[ownScore] - info.getX(), sortedPearlsY.get(ownScore) - info.getY());
        richtung = (float) Math.atan2(newDirection.getY(), newDirection.getX());

        return richtung;
    }

    public void testing() {
        for (int y = 0; y < info.getScene().getHeight(); y += 10) {
            for (int x = 0; x < info.getScene().getWidth(); x += 10) {
                if (freiBier(x, -y)) {
                    freespace.add(new Point(x + 5, -y - 5));
                }
                //System.out.print(freiBier(x, -y) ? "." : "#");
            }
            //System.out.println();
        }
    }

    public boolean freiBier(int x, int y) {
        for (Path2D obstacle : obstacles) {
            if (obstacle.intersects(x, y, 10, 10)) {
                return false;
            }
        }
        return true;
    }
//    public void Dietrying(){
//
//    }

    public void getSortedPearls() {
        for (int i = 0; i < pearl.length; i++) {
            sortedPearlsX.put((int) pearl[i].getX(), i);
        }
        for (int i = 0; i < pearl.length; i++) {
            sortedPearlsY.put(i, (int) pearl[sortedPearlsX.get(pearlsTemp[i])].getY());
        }
    }


    public void dijsktrastuff() {
        long time = System.currentTimeMillis();
        for (Point2D point : freespace) {
            Node n = new Node(point);
            nodeGraph.addNode(n);
        }

        for (Node n : nodeGraph.getNodes()) {
            for (Node neighbour : nodeGraph.getNodes()) {
                if (isBetween(neighbour.getName().getX(), n.getName().getX() - 10, n.getName().getX() + 10)
                        && isBetween(neighbour.getName().getY(), n.getName().getY() - 10, n.getName().getY() + 10)
                        && n.getName() != neighbour.getName()) {
                    int distance = neighbour.getName().getX() == n.getName().getX() || neighbour.getName().getY() == n.getName().getY() ? 10 : 14;
                    n.adjacentNodes.put(neighbour, distance);
                }
            }
        }

        for (Node nTest : nodeGraph.getNodes()) {
            nTest.adjacentNodes.keySet().forEach(node -> {
                System.out.print(node.getName() + " | ");
            });
            System.out.print("\n");
            System.out.println(nTest.adjacentNodes.values());

            nodeGraph = Dijkstra.calculateShortestPathFromSource(nodeGraph, nTest); //source: Taucher

            /* nodeGraph -> Ziel (Perle)
            *  Ziel immer zum nächsten Vorgänger
            *  immer in List speichern oder so
            *  sobald Vorgänger source ablaufen
            *
            *  wiederholen
            */

            

            break;
        }

        // Position des Tauchers, Perle nähe Node, graph?

        System.err.println(System.currentTimeMillis() - time);
    }

    public boolean isBetween(double valueToBeChecked, double lowerBound, double upperBound) {
        if (valueToBeChecked <= upperBound && valueToBeChecked >= lowerBound) {
            return true;
        }
        return false;
    }


}

// Inspired by https://www.baeldung.com/java-dijkstra
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
