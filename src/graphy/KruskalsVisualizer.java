package graphy;

import graphy.GraphElements.MyEdge;
import graphy.GraphElements.MyVertex;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections15.buffer.PriorityBuffer;

public class KruskalsVisualizer extends AlgorithmVisualizer {

    private PriorityBuffer edgeBuffer;   /* Heap for all the edges */
    private Set<MyEdge> minimumSpanningTree;    /* The set containing the edges of the minimum spanning tree */
    private Set<Set<MyVertex>> vertexSetMap;
    private int minimumWeight;
    
    /* Constructor */
    public KruskalsVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        super.initialize();

        MyGraphVisualizer.informationPanel.info.append("\nInitializing Kruskal's algorithm: ");

        vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
        vv.getRenderContext().getPickedEdgeState().clear(); /* Clear all picked edges */

        /* Initialize the minimum spanning tree edge set */
        minimumSpanningTree = new HashSet<MyEdge>(graph.getEdgeCount());
        MyGraphVisualizer.informationPanel.info.append("MST Edge Set: " + minimumSpanningTree);

        /* Create a priority buffer and add all the edges to it */
        
        edgeBuffer = new PriorityBuffer(graph.getEdgeCount(), true, new Comparator<MyEdge>() {
            public int compare (MyEdge o1, MyEdge o2) {
                    if (o1.getWeight() < o2.getWeight()) {
                            return -1;
                    }

                    if (o1.getWeight() > o2.getWeight()) {
                            return 1;
                    }
                    return 0;
            }
        });

        for (MyEdge e : graph.getEdges())
            edgeBuffer.add(e);

        MyGraphVisualizer.informationPanel.info.append("Graph Edge Set: " + edgeBuffer);

        /* Create the vertex sets and the mapping from each vertex to the set that contains it */
        vertexSetMap = new HashSet<Set<MyVertex>>(graph.getVertexCount());

        for (MyVertex v : graph.getVertices()) {
            Set<MyVertex> s = new HashSet<MyVertex>();
            s.add(v);
            vertexSetMap.add(s);
        }

        MyGraphVisualizer.informationPanel.info.append("Current Trees: " + vertexSetMap);

        minimumWeight = 0;
        running = true;
    }

    @Override
    public void step() {

        if (!running) {
            initialize();
        }
        else {
            vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
            vv.getRenderContext().getPickedEdgeState().clear(); /* Clear all picked edges */
            
            for (MyEdge e : minimumSpanningTree)
                vv.getRenderContext().getPickedEdgeState().pick(e, true);
            
            if ( !(edgeBuffer.isEmpty()) ) {
                
                MyEdge minEdge = (MyEdge)edgeBuffer.remove();
                MyGraphVisualizer.informationPanel.info.append("\nRemoved minimum edge " + minEdge + ". Edge Set is now: " + edgeBuffer);
                vv.getRenderContext().getPickedEdgeState().pick(minEdge, true);
                
                /* If the end points of the vertices are in different sets, add the edge to the MST and union the 2 sets*/
                MyVertex v1 = graph.getEndpoints(minEdge).getFirst();
                MyVertex v2 = graph.getEndpoints(minEdge).getSecond();
                Set<MyVertex> s1 = findSet(v1);
                Set<MyVertex> s2 = findSet(v2);
                vv.getRenderContext().getPickedVertexState().pick(v1, true);
                vv.getRenderContext().getPickedVertexState().pick(v2, true);
                if (!(s1.contains(v2))) {
                    MyGraphVisualizer.informationPanel.info.append(v1 + " and " + v2 + " are in different sets, so add "
                                                    + minEdge + " to the MST: " + minimumSpanningTree);
                    MyGraphVisualizer.informationPanel.info.append("Union the sets " +  s1 + " and " + s2);
                    minimumSpanningTree.add(minEdge);
                    minimumWeight = minimumWeight + minEdge.getWeight();

                    s1.addAll(s2);
                    s2.clear();
                                     
                    removeAllEmptySets();
                    
                    MyGraphVisualizer.informationPanel.info.append("Vertex sets are now: " + vertexSetMap);
                } else {
                    MyGraphVisualizer.informationPanel.info.append(v1 + " and " + v2 + " are in the same set, so we discard " + minEdge);
                }
            } else {
                MyGraphVisualizer.informationPanel.info.append("\nEdge Set is empty, algorithm terminated. Minimum spanning tree weight: " + minimumWeight);
                running = false;
                terminate();
            }
            
        }

    }

    @Override
    public void reset() {

        super.reset();

        if (running) {
            minimumSpanningTree.clear();
            vertexSetMap.clear();
            edgeBuffer.clear();
            running = false;
        }

    }

    private Set<MyVertex> findSet(MyVertex v) {
        for (Set s : vertexSetMap) {
            if (s.contains(v))
                    return s;
        }
        return null;
    }

    private void removeAllEmptySets() {
        Set<Set<MyVertex>> temp = new HashSet<Set<MyVertex>>();
        for (Set<MyVertex> s : vertexSetMap)
            if (!(s.isEmpty()))
                temp.add(s);

        vertexSetMap.clear();
        vertexSetMap.addAll(temp);
    }
}
    