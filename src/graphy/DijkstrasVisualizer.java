package graphy;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import graphy.GraphElements.MyEdge;
import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.buffer.PriorityBuffer;

public class DijkstrasVisualizer extends AlgorithmVisualizer {

    /* Inherits */
    // graph: The graph the algorithm is applied to
    // vv: The Visualisation Viewer object corresponding to the graph

    private PriorityBuffer Q;   /* Used to get the vertex at minimum distance */
    private Map<MyVertex, MyVertex> parentMap; /* Used to set the parents of vertices */
    private Set<MyEdge> shortestPath;
    
    /* Constructor */
    public DijkstrasVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        /* Algorithm initialization steps */

        if (MyVertexFactory.sourceVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to right-click and select a source Vertex first");
            return;
        }

        if (MyVertexFactory.sinkVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to right-click and select a sink Vertex first");
            return;
        }

        super.initialize();

        MyGraphVisualizer.informationPanel.info.append("\nInitializing Dijkstra's Algorithm:");

        vv.getRenderContext().setVertexLabelTransformer(new Transformer<MyVertex, String>() {
            public String transform(MyVertex e) {
                String distance = (e.getDistance() == Integer.MAX_VALUE) ? "inf" : Integer.toString(e.getDistance());
                String parent = (String) ((parentMap.get(e) == null) ? "null" : parentMap.get(e).toString());
                return (e.toString() + " " + "P:" + parent + " D:" + distance);
            }
        });

        /* Initialize a heap to retrieve the vertices with minimum distance */
        Q = new PriorityBuffer(graph.getVertexCount(), true, new Comparator<MyVertex>() {	/* Used as a heap in Dijkstra's algorithm */
            public int compare (MyVertex o1, MyVertex o2) {
                    if (o1.getDistance() < o2.getDistance()) {
                            return -1;
                    }

                    if (o1.getDistance() > o2.getDistance()) {
                            return 1;
                    }

                    return 0;
            }
        });

        parentMap = new HashMap<MyVertex, MyVertex>(graph.getVertexCount());

        MyVertexFactory.sourceVertex.setDistance(0);    /* Set d(source) = 0 */
        parentMap.put(MyVertexFactory.sourceVertex, null);
        Q.add(MyVertexFactory.sourceVertex);   /* Push the source vertex onto the priority queue */
        
        /* Set the parents of all vertices as null */
        for (GraphElements.MyVertex v : graph.getVertices()) {
            if (!(v.equals(MyVertexFactory.sourceVertex))) {
                v.setDistance(Integer.MAX_VALUE);
                parentMap.put(v, null);
                Q.add(v);
            }
        }

        MyGraphVisualizer.informationPanel.info.append("Added all vertices to Q: " + Q);
        MyGraphVisualizer.informationPanel.info.append("Set Parent(v) for all vertices as NULL and D(v) as infinity");
        
        MyGraphVisualizer.informationPanel.info.append("Set Distance(" + MyVertexFactory.sourceVertex + ") = " + MyVertexFactory.sourceVertex.getDistance()
                                                        + "; Parent("  + MyVertexFactory.sourceVertex + ") = null");

        vv.repaint();
        running = true; /* Set Dijkstras as running */
    }

    public void step() {

        if (!running) {
            initialize();
        }
        else {

            /* The Dijkstras Algorithm */

            /* If the priority queue is not empty, then pop the queue and update its neighbours*/
            vv.getRenderContext().getPickedVertexState().clear();
            vv.getRenderContext().getPickedEdgeState().clear();
            
            if (!Q.isEmpty()) {

                MyVertex u = (MyVertex) Q.get();
                vv.getRenderContext().getPickedVertexState().pick(u, true);
                
                if (u.getDistance() == Integer.MAX_VALUE) {
                    MyGraphVisualizer.informationPanel.info.append("\nVertices are unreachable");
                    running = false;
                    terminate();
                }
                
                u = (MyVertex) Q.remove();
                MyGraphVisualizer.informationPanel.info.append("\nPicked vertex with minimum distance: " + u);

                if (u == MyVertexFactory.sinkVertex) {
                    MyGraphVisualizer.informationPanel.info.append("Reached target vertex " + u);
                    markShortestPath();
                    MyGraphVisualizer.informationPanel.info.append("\nShortest path marked using the parents of the vertices\nShortest distance = " + MyVertexFactory.sinkVertex.getDistance()
                                                                    + "\nAlgorithm Terminated");
                    running = false;
                    terminate();
                    return;
                }

                MyGraphVisualizer.informationPanel.info.append("Looking at neighbours of " + u + " that are in Q: " + Q);
                for (MyVertex v : graph.getSuccessors(u)) {
                    if (Q.contains(v)) {
                        int alt = u.getDistance() + graph.findEdge(u, v).getWeight();
                        if (alt < v.getDistance()) {
                            v.setDistance(alt);
                            parentMap.put(v, u);
                            MyGraphVisualizer.informationPanel.info.append("Updated vertex " + v + "; D(" + v + ") = " + alt + ", P(" + v + ") = " + u);
                        }
                        vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(u, v), true);
                        vv.getRenderContext().getPickedVertexState().pick(v, true);
                    }
                }
                
                refreshQ();
                
            } else {
                 MyGraphVisualizer.informationPanel.info.append("\nDijkstras terminated without finding a path to the target vertex");
                 vv.getRenderContext().getPickedVertexState().clear();
                 vv.getRenderContext().getPickedEdgeState().clear();
                 running = false;
                 terminate();
            }
        }

    }

    /* Termination doesn't involve anything extra, taken care of by the superclass */

    @Override
    public void reset() {

        super.reset();

        if (running) {

            for (GraphElements.MyVertex v : graph.getVertices()) {
                v.setDistance(0);
                parentMap.put(v, null); /* Set all vertices as unvisited */
            }

            /* Clear the stack */
            Q.clear();
            running = false;
        }
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.repaint();

    }

    private void markShortestPath() {
        vv.getRenderContext().getPickedVertexState().clear();
        vv.getRenderContext().getPickedEdgeState().clear();

        MyVertex v;
        MyVertex u = MyVertexFactory.sinkVertex;
        vv.getRenderContext().getPickedVertexState().pick(u, true);

        do {
            v = u;
            u = parentMap.get(u);
            vv.getRenderContext().getPickedVertexState().pick(u, true);
            vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(u, v), running);
        } while (parentMap.get(u) != null);
    }

    private void refreshQ() {
        PriorityBuffer temp = new PriorityBuffer(graph.getVertexCount(), true, new Comparator<MyVertex>() {
            public int compare (MyVertex o1, MyVertex o2) {
                        if (o1.getDistance() < o2.getDistance()) {
                                return -1;
                        }

                        if (o1.getIndex() > o2.getIndex()) {
                                return 1;
                        }

                        return 0;
                }
        });

        while (Q.size() > 0) {
                temp.add(Q.remove());
        }

        Q = temp;
    }

}
