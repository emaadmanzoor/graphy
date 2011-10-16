/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphy;

import graphy.GraphElements.MyEdge;
import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author pingu
 */
public class EdmondsKarpVisualizer extends AlgorithmVisualizer {

    private ConcurrentLinkedQueue vertexQueue;    /* Queue need for the breadth first search algorithm */
    private HashMap <MyVertex, MyVertex> PredecessorMap;

    int maximumFlow;
    int increment;  /* Infinity */
    private boolean BFSDone = false;
    
    /* Constructor */
    public EdmondsKarpVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        if (MyVertexFactory.sourceVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to select a source vertex first; choose one by right-clicking on the vertex");
            return;
        }

        if (MyVertexFactory.sinkVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to select a sink vertex first; choose one by right-clicking on the vertex");
            return;
        }

        super.initialize();

        maximumFlow = 0;
        increment = Integer.MAX_VALUE;

        for (GraphElements.MyEdge e : graph.getEdges()) {
            e.setWeight(0);
        }

        MyGraphVisualizer.informationPanel.info.append("\nRunning Edmonds-Karp algorithm with source: " + MyVertexFactory.sourceVertex + ", sink: " + MyVertexFactory.sinkVertex);

        vertexQueue = new ConcurrentLinkedQueue(); /* Create the Vertex Queue */
        PredecessorMap = new HashMap<MyVertex, MyVertex>();
        running = true;
        
    }

    @Override
    public void step() {
        
        if (!running) {
            initialize();
        }

        else {
            
            if (!BFSDone) {

                boolean foundAugmentingPath = BreadthFirstSearch(MyVertexFactory.sourceVertex, MyVertexFactory.sinkVertex);
            
                if (foundAugmentingPath) {
                    MyGraphVisualizer.informationPanel.info.append("\n");
                    MyGraphVisualizer.informationPanel.info.append("BFS found an augmenting path. We now need to find the minimum of\n"
                                                                    + "the flows in the augmented path, and add it to the forward edges\n"
                                                                    + "of the augmented path and subtract it from the reverse edges.\n");
                    increment = Integer.MAX_VALUE;
                    
                    /* Find the minimum of the flows along the augmented path and mark the augmented path */
                    /* Add the minimum of the flows to each forward edge and subtract it from each backward edge */
                    
                    for (MyVertex u = MyVertexFactory.sinkVertex; PredecessorMap.get(u).getIndex() != -1; u = PredecessorMap.get(u)) {

                        MyEdge e;
                        
                        if (graph.findEdge(PredecessorMap.get(u), u) != null) {
                            e = graph.findEdge(PredecessorMap.get(u), u);
                            vv.getRenderContext().getPickedEdgeState().pick(e, true);
                            increment = (int) Math.min(increment, e.getCapacity() - e.getWeight());
                        }

                        if (graph.findEdge(u, PredecessorMap.get(u)) != null) {
                            e = graph.findEdge(u, PredecessorMap.get(u));
                            vv.getRenderContext().getPickedEdgeState().pick(e, true);
                            increment = (int) Math.min(increment, 0 - (-e.getWeight()));
                        }

                        vv.getRenderContext().getPickedVertexState().pick(u, true);
                        vv.getRenderContext().getPickedVertexState().pick(PredecessorMap.get(u), true);
                        
                    }

                    BFSDone = true;

                    vv.repaint();
                    
                }
                else {
                    MyGraphVisualizer.informationPanel.info.append("\nNo augmenting path found. The algorithm has terminated.\n"
                                                                + "Maximum Flow = "+ maximumFlow);

                    running = false;
                    BFSDone = false;

                    terminate();
                    
                    return;
                }
            }
            else if (BFSDone) {

                    /* Add the minimum flow to each edge in the augmented path */
                    MyGraphVisualizer.informationPanel.info.append("Augmented the minimum flow = " + increment + " to each edge in the augmented path");
                    for (MyVertex u = MyVertexFactory.sinkVertex; PredecessorMap.get(u).getIndex() != -1; u = PredecessorMap.get(u)) {

                        MyEdge e = graph.findEdge(PredecessorMap.get(u), u);
                        MyEdge e_reverse = graph.findEdge(u, PredecessorMap.get(u));

                        if (e != null)
                            e.setWeight(e.getWeight() + increment);

                        if (e_reverse != null)
                            e_reverse.setWeight(e_reverse.getWeight() - increment);
                        
                    }

                    maximumFlow = maximumFlow + increment;

                    BFSDone = false;

                    vv.repaint();
            }

        }
    }

    private boolean BreadthFirstSearch (MyVertex source, MyVertex sink) {

        for (GraphElements.MyVertex v : graph.getVertices()) {
            v.setUnvisited();   /* Set all vertices as unvisited */
        }
        
        vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
        vv.getRenderContext().getPickedEdgeState().clear(); /* Clear all picked edges */
        
        MyGraphVisualizer.informationPanel.info.append("\nRunning Breadth First Search to find an augmenting path between " + source + " and " + sink + "\n");

        vertexQueue.add(source);   /* Append the root vertex to the queue */
        source.setVisited();
        PredecessorMap.put(source, new MyVertex("NULL", -1));

        while (!vertexQueue.isEmpty()) {

            MyVertex u = (MyVertex) vertexQueue.poll();
            
            MyGraphVisualizer.informationPanel.info.append("Popped " + u);
            
            if (graph.getNeighborCount(u) != 0) {
                for (MyVertex v : graph.getNeighbors(u)) {

                    MyGraphVisualizer.informationPanel.info.append("Looking at neighbour " + v + " of " + u);
                    MyGraphVisualizer.informationPanel.info.append("Has " + v + " been visited? " + v.isVisited());

                    if (graph.findEdge(u, v) != null) {
                        if ( (!v.isVisited()) && (graph.findEdge(u, v).getCapacity() - graph.findEdge(u, v).getWeight() > 0) ) {
                            vertexQueue.add(v);
                            v.setVisited();
                            PredecessorMap.put(v, u);
                            MyGraphVisualizer.informationPanel.info.append("\nFlow(" + u + ", " + v + ") = " + graph.findEdge(u, v).getWeight()
                                                                            + " <= " + "Capacity(" + u + ", " + v + ") = " + graph.findEdge(u, v).getCapacity());
                            MyGraphVisualizer.informationPanel.info.append("Found an edge for the augmenting path, pushed " + v + "\n");
                        }

                    }

                    if (graph.findEdge(v, u) != null) {

                        if ( (!v.isVisited()) && (graph.findEdge(v, u).getWeight() > 0) ) {
                            vertexQueue.add(v);
                            v.setVisited();
                            PredecessorMap.put(v, u);
                            MyGraphVisualizer.informationPanel.info.append("\nFlow(" + v + ", " + u + ") = " + graph.findEdge(v, u).getWeight() + " > " + "0");
                            MyGraphVisualizer.informationPanel.info.append("Found a reverse edge for the augmenting path, pushed " + v + "\n");
                        }

                    }
                        
                }
            }
            else {
                MyGraphVisualizer.informationPanel.info.append(u + " has no neighbours");
            }

        }
        
        return sink.isVisited();

    }

    @Override
    public void reset() {

        super.reset();

        if (running) {

            vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
            vv.getRenderContext().getPickedEdgeState().clear();     /* Clear all picked edges */

            for (GraphElements.MyEdge e : graph.getEdges()) {
                e.setWeight(0);
            }
            graphy.vv.repaint();
            
            maximumFlow = 0;
            increment = Integer.MAX_VALUE;

            vertexQueue.clear();
            PredecessorMap.clear();
            
            running = false;
            BFSDone = false;
        }

    }

}
