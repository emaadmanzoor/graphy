/*
 * GraphElements.java
 *
 * Created on March 21, 2007, 9:57 AM
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package graphy;

import org.apache.commons.collections15.Factory;

/**
 *
 * @author Dr. Greg M. Bernstein
 */
public class GraphElements {
    
    /** Creates a new instance of GraphElements */
    public GraphElements() {
    }
    
    public static class MyVertex  {
        private String name;
        private int vIndex;
        private boolean visited = false;
        private int distance = 0;
        
        private double x;
        private double y;
        
        public MyVertex(String name, int vIndex) {
            this.name = name;
            this.vIndex = vIndex;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getX() {
            return x;
        }
        
        public void setY(double y) {
            this.y = y;
        }

        public double getY() {
            return y;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return vIndex;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited() {
            this.visited = true;
        }

        public void setUnvisited() {
            this.visited = false;
        }

        public int getDistance() {
            return this.distance;
        }

        public void setDistance (int distance) {
            this.distance = distance;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public static class MyEdge {
        private int capacity;
        private int weight;
        private String name;
        private int eIndex;

        public MyEdge(String name, int eIndex) {
            this.name = name;
            this.eIndex = eIndex;
        }
        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return eIndex;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    // Single factory for creating Vertices...
    public static class MyVertexFactory implements Factory<MyVertex> {
        private static int nodeCount = 0;
        private static MyVertexFactory instance = new MyVertexFactory();

        public static MyVertex sourceVertex;
        public static MyVertex sinkVertex;
        
        static void setNodeCount(int maximumVertexIndex) {
            nodeCount = maximumVertexIndex;
        }
        
        private MyVertexFactory() {            
        }
        
        public static MyVertexFactory getInstance() {
            return instance;
        }
        
        public GraphElements.MyVertex create() {
            int vIndex = nodeCount;
            String name = "v" + nodeCount;
            nodeCount++;
            MyVertex v = new MyVertex(name, vIndex);
            return v;
        }

        public static void resetNodecount() {
            nodeCount = 0;
        }

    }
    
    // Singleton factory for creating Edges...
    public static class MyEdgeFactory implements Factory<MyEdge> {
        private static int linkCount = 0;
        private static int defaultWeight;
        private static int defaultCapacity;
        
        public static boolean weighted;

        private static MyEdgeFactory instance = new MyEdgeFactory();

        static void setLinkCount(int maximumEdgeIndex) {
            linkCount = maximumEdgeIndex;
        }
        
        private MyEdgeFactory() {            
        }
        
        public static MyEdgeFactory getInstance() {
            return instance;
        }
        
        public GraphElements.MyEdge create() {
            String name = "E" + linkCount;
            MyEdge link = new MyEdge(name, linkCount);
            linkCount++;
            link.setWeight(defaultWeight);
            link.setCapacity(defaultCapacity);
            return link;
        }    

        public static int getDefaultWeight() {
            return defaultWeight;
        }

        public static void setDefaultWeight(int aDefaultWeight) {
            defaultWeight = aDefaultWeight;
        }

        public static int getDefaultCapacity() {
            return defaultCapacity;
        }

        public static void setDefaultCapacity(int aDefaultCapacity) {
            defaultCapacity = aDefaultCapacity;
        }

        public static void resetLinkcount() {
            linkCount = 0;
        }
        
    }

}
