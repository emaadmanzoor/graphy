package graphy;

import graphy.GraphElements.MyVertex;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.collections15.buffer.PriorityBuffer;

public class MaximumDegreeFirstColoringVisualizer extends AlgorithmVisualizer {

    /* Inherits */
    // graph: The graph the algorithm is applied to
    // vv: The Visualisation Viewer object corresponding to the graph

    private PriorityBuffer<MyVertex> Q;   /* Used to store vertices in order of degree */
    private Set<MyVertex> colored;
    int colorsUsed; /* Store number of currently used colors */
    
    /* Constructor */
    public MaximumDegreeFirstColoringVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        super.initialize();
        MyGraphVisualizer.informationPanel.info.append("\nInitializing the Maximum Degree First Colouring Visualizer");

        for (MyVertex v : graph.getVertices())
            v.setName("v" + v.getIndex());

        colorsUsed = 0;
        
        /* Initialize a heap to retrieve the vertices with minimum degree */
        Q = new PriorityBuffer<MyVertex>(graph.getVertexCount(), false, new Comparator<MyVertex>() {
            public int compare (MyVertex o1, MyVertex o2) {
                    if (graph.getNeighborCount(o1) < graph.getNeighborCount(o2)) {
                            return -1;
                    }

                    if (graph.getNeighborCount(o1) > graph.getNeighborCount(o2)) {
                            return 1;
                    }

                    return 0;
            }
        });

        for (MyVertex v : graph.getVertices()) {
            Q.add(v);
        }
        
        colored = new HashSet<MyVertex>(graph.getVertexCount());
        
        MyGraphVisualizer.informationPanel.info.append("Finished ordering vertices by degree");
        running = true;
    }

    public void step() {

        if (!running) {
            initialize();
        } else {

            if (Q.isEmpty()) {
                MyGraphVisualizer.informationPanel.info.append("\nColoring terminated.");
                vv.getRenderContext().getPickedVertexState().clear();
                vv.getRenderContext().getPickedEdgeState().clear();
                colored.clear();
                running = false;
                terminate();
            } else {

                MyVertex maximumDegree = (MyVertex) Q.remove();
                maximumDegree.setName(maximumDegree.getName() + " C" + colorsUsed);
                colored.add(maximumDegree);
                vv.getRenderContext().getPickedVertexState().pick(maximumDegree, true);
                MyGraphVisualizer.informationPanel.info.append("\n" + "v" + maximumDegree.getIndex() + " is uncolored and has maximum degree. Colored with C" + colorsUsed);

                Set<MyVertex> temp = new HashSet<MyVertex>();
                temp.addAll(Q);
                
                for (MyVertex v : temp) {
                    boolean isAdjacent = false;

                    for (MyVertex u : colored) {
                        if (graph.getNeighbors(u).contains(v)) {
                            isAdjacent = true;
                        }
                    }
                    
                    if (!isAdjacent) {
                        v.setName(v.getName() + " C" + colorsUsed);
                        vv.getRenderContext().getPickedVertexState().pick(v, true);
                        MyGraphVisualizer.informationPanel.info.append("v" + v.getIndex() + " is uncolored and is not adjacent to any of the colored vertices. Colored with C" + colorsUsed);
                        colored.add(v);
                        Q.remove(v);
                    }
                }

                colorsUsed++;
            }
        }

    }

    /* Termination doesn't involve anything extra, taken care of by the superclass */

    @Override
    public void reset() {

        for (MyVertex v : graph.getVertices())
            v.setName("v" + v.getIndex());

        super.reset();

        if (running) {

            for (GraphElements.MyVertex v : graph.getVertices()) {
                v.setUnvisited();   /* Set all vertices as unvisited */
            }

            Q.clear();
            colored.clear();
            colorsUsed = 0;
            
            running = false;
        }

    }

}