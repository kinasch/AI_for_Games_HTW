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

public class Jasch extends AI {

    float richtung;
    int score = info.getScore();
    int pathProgression = 0, pathProgression2 = 1;
    int w = 10;
    boolean airbool = true, boolbool = true, unknownbool = false;

    Point2D[] pearl = info.getScene().getPearl(); // ziele
    Point2D[] currentPearl;
    Path2D[] obstacles = info.getScene().getObstacles();
    ArrayList<Point2D> freespace = new ArrayList<>();
    GraphJasch1 nodeGraph = new GraphJasch1();
    ArrayList<NodeJasch1> pearlNodes = new ArrayList<>();
    ArrayList<NodeJasch1> removedPearlNodes = new ArrayList<>();
    ArrayList<NodeJasch1> tempTarget;

    public Jasch(Info info) {
        super(info);
        testing();
        //enlistForTournament(573132, 573689);
        dijsktrastuffStart();
        dijsktrastuffRepeat();
    }

    @Override
    public String getName() {
        return "Jasch 1 (-50)";
    }

    @Override
    public Color getColor() {
        return Color.black;
    }

    @Override
    public PlayerAction update() {

        float speed = info.getMaxAcceleration(); // max speed
        if (score < info.getScore()) {
            removedPearlNodes.add(pearlNodes.remove(pearlNodes.size()-1));
            dijsktrastuffRepeat();
        }


        if (info.getAir() == info.getMaxAir() && !isBetween(info.getX(), pearlNodes.get(pearlNodes.size()-1).getName().getX() - 4, pearlNodes.get(pearlNodes.size()-1).getName().getX() + 4)) {
            richtung = pearlNodes.get(pearlNodes.size()-1).getName().getX() < info.getX() ? (float) Math.PI : 0;
            return new DivingAction(speed, richtung);
        }

        if (info.getAir() == info.getMaxAir() && isBetween(info.getX(), pearlNodes.get(pearlNodes.size()-1).getName().getX() - 4, pearlNodes.get(pearlNodes.size()-1).getName().getX() + 4)) {
            dijsktrastuffRepeat();
            if (pearlNodes.size() < 3) {
                unknownbool = true;
            }
        }

        score = info.getScore();

        Point2D notfall = null;
        for (Point2D point : pearl) {
            if (isBetween(point.getX(), pearlNodes.get(pearlNodes.size()-1).getName().getX() - 10, pearlNodes.get(pearlNodes.size()-1).getName().getX() + 10) && isBetween(point.getY(), pearlNodes.get(pearlNodes.size()-1).getName().getY() - 10, pearlNodes.get(pearlNodes.size()-1).getName().getY() + 10)) {
                notfall = point;
            }
        }
        if (notfall == null) {
            notfall = pearlNodes.get(pearlNodes.size()-1).getName();
        }

        if (airbool || unknownbool) {
            if (pathProgression < pearlNodes.get(pearlNodes.size()-1).getShortestPath().size() - 1) {
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
                }, pearlNodes.get(pearlNodes.size()-1).getShortestPath().get(pathProgression).getName());
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
            /*goToPearl(new Point2D() {
                @Override
                public double getX() {
                    return info.getX();
                }

                @Override
                public double getY() {
                    return info.getY();
                }

                @Override
                public void setLocation(double x, double y) {

                }
            }, new Point2D() {
                @Override
                public double getX() {
                    return info.getX();
                }

                @Override
                public double getY() {
                    return info.getY();
                }

                @Override
                public void setLocation(double x, double y) {

                }
            });*/
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
                for (NodeJasch1 node : this.nodeGraph.getNodes()) {
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

        if (info.getAir() < info.getMaxAir()/2) {
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
                for (NodeJasch1 node : this.nodeGraph.getNodes()) {
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

        pearlNodes = new ArrayList<NodeJasch1>();

        for (Point2D point2D : pearl) {
            Map<Double, NodeJasch1> dis = new HashMap<>();
            for (NodeJasch1 n : nodeGraph.getNodes()) {
                dis.put(Math.sqrt(Math.pow(n.getName().getX() - point2D.getX(), 2) + Math.pow(n.getName().getY() - point2D.getY(), 2)), n);
            }

            // TODO: nach distanz und höhe sortieren und abhängig davon wechseln

            ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
            Collections.sort(dListTemp);

            NodeJasch1 p = dis.get((Double) dListTemp.get(0));
            if (!removedPearlNodes.contains(p)) {
                pearlNodes.add(p);
            }
        }
    }

    // executed only once
    public void dijsktrastuffStart() {
        long time = System.currentTimeMillis();
        for (Point2D point : freespace) {
            NodeJasch1 n = new NodeJasch1(point);
            nodeGraph.addNode(n);
        }

        for (NodeJasch1 n : nodeGraph.getNodes()) {
            for (NodeJasch1 neighbour : nodeGraph.getNodes()) {
                if (isBetween(neighbour.getName().getX(), n.getName().getX() - w, n.getName().getX() + w)
                        && isBetween(neighbour.getName().getY(), n.getName().getY() - w, n.getName().getY() + w)
                        && n.getName() != neighbour.getName()) {
                    int distance = neighbour.getName().getX() == n.getName().getX() || neighbour.getName().getY() == n.getName().getY() ? w : (int) Math.floor(Math.sqrt(w * w + w * w));
                    n.adjacentNodes.put(neighbour, distance);
                }
            }
        }

        assignPearlsToNodes();
    }

    // Repeated dijsktra
    public void dijsktrastuffRepeat() {
        long time = System.currentTimeMillis();
        pathProgression = 0;
        pathProgression2 = 1;


        NodeJasch1 source = null;
        for (NodeJasch1 n : nodeGraph.getNodes()) {
            if (n.getName().getX() == (Math.floorMod(info.getX(), w)) * w + (float) w / 2 && n.getName().getY() == (Math.floorMod(
                    info.getY(), w)) * w + (float) w / 2) {
                source = n;
            }
        }
        if (source == null) {
            Map<Double, NodeJasch1> dis = new HashMap<>();
            for (NodeJasch1 n : nodeGraph.getNodes()) {
                dis.put(Math.sqrt(Math.pow(n.getName().getX() - info.getX(), 2) + Math.pow(n.getName().getY() - info.getY(), 2)), n);
            }

            ArrayList<Double> dListTemp = new ArrayList<>(dis.keySet());
            Collections.sort(dListTemp);

            source = dis.get(dListTemp.get(0));
        }

        for (NodeJasch1 n : nodeGraph.getNodes()) {
            n.setShortestPath(new LinkedList<>());
            n.setDistance(Integer.MAX_VALUE);
        }

        nodeGraph = DijkstraJasch1.calculateShortestPathFromSource(nodeGraph, source);

        assignPearlsToNodes();

        pearlNodes.sort(new Comparator<NodeJasch1>() {
            @Override
            public int compare(NodeJasch1 o1, NodeJasch1 o2) {
                return Double.compare(o1.getName().getY(), o2.getName().getY());
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
class NodeJasch1 {

    private Point2D point;

    private List<NodeJasch1> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<NodeJasch1, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(NodeJasch1 destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public NodeJasch1(Point2D point) {
        this.point = point;
    }

    // getters and setters

    public Point2D getName() {
        return point;
    }

    public void setName(Point2D point) {
        this.point = point;
    }

    public List<NodeJasch1> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<NodeJasch1> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<NodeJasch1, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<NodeJasch1, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }
}

class GraphJasch1 {

    private Set<NodeJasch1> nodes = new HashSet<>();

    public void addNode(NodeJasch1 nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
    public Set<NodeJasch1> getNodes() {
        return nodes;
    }
}

class DijkstraJasch1 {

    public static GraphJasch1 calculateShortestPathFromSource(GraphJasch1 graph, NodeJasch1 source) {
        source.setDistance(0);

        Set<NodeJasch1> settledNodes = new HashSet<>();
        Set<NodeJasch1> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            NodeJasch1 currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);

            for (Map.Entry<NodeJasch1, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                NodeJasch1 adjacentNode = adjacencyPair.getKey();
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

    private static NodeJasch1 getLowestDistanceNode(Set<NodeJasch1> unsettledNodes) {
        NodeJasch1 lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (NodeJasch1 node : unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(NodeJasch1 evaluationNode, Integer edgeWeigh, NodeJasch1 sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<NodeJasch1> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
