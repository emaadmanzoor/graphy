package graphy;

import graphy.GraphElements.MyVertex;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections15.buffer.PriorityBuffer;

public class PruferVisualizer extends AlgorithmVisualizer {

    private PriorityBuffer vertexBuffer;
    private List pruferCode;
    private List sisterCode;
    private int verticesDone;
    private boolean stepOne;
    
    /* Constructor */
    public PruferVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        super.initialize();

        MyGraphVisualizer.informationPanel.info.append("\nInitialized the Prufer Code generation algorithm");

        vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
        vv.getRenderContext().getPickedEdgeState().clear(); /* Clear all picked edges */

        /* Create a priority buffer to pull the minimum label out */
        vertexBuffer = new PriorityBuffer(graph.getVertexCount(), true, new Comparator<MyVertex>() {
            public int compare (MyVertex o1, MyVertex o2) {
                    if (o1.getIndex() < o2.getIndex()) {
                            return -1;
                    }

                    if (o1.getIndex() > o2.getIndex()) {
                            return 1;
                    }
                    return 0;
            }
        });

        pruferCode = new ArrayList<Integer>(graph.getVertexCount() - 1);
        sisterCode = new ArrayList<Integer>(graph.getVertexCount() - 1);

        verticesDone = 0;
        stepOne = true;
        running = true;
    }

    @Override
    public void step() {

        if (!running) {
            initialize();
        }

        else {

            vv.getRenderContext().getPickedVertexState().clear();
            vv.getRenderContext().getPickedEdgeState().clear();

            if (graph.getVertexCount() > 2) {
                
                /* First find all vertices of degree one and mark them */
                if (stepOne) {
                    /* The vertex buffer will contain all the vertices of degree 1 for every iteration */
                    vertexBuffer.clear();

                    for (MyVertex v : graph.getVertices()) {
                        if (graph.degree(v) == 1) {
                            vertexBuffer.add(v);
                            vv.getRenderContext().getPickedVertexState().pick(v, true);
                        }
                    }

                    if (vertexBuffer.isEmpty()) {
                        MyGraphVisualizer.informationPanel.info.append("\nNo vertices of degree one found; algorithm terminated");
                        MyGraphVisualizer.informationPanel.info.append("Prufer Code: " + pruferCode);
                        MyGraphVisualizer.informationPanel.info.append("Sister Code " + sisterCode);
                        terminate();
                        return;
                    }
                    
                    MyGraphVisualizer.informationPanel.info.append("\nFound vertices of degree 1: " + vertexBuffer);
                    stepOne = false;
                } else {
                    /* Get the vertex of degree 1 with minimum label and remove it */
                    MyVertex minVertex = (MyVertex) vertexBuffer.get();
                    MyVertex oppositeVertex = graph.getNeighbors(minVertex).iterator().next();
                    sisterCode.add(minVertex.getIndex());
                    pruferCode.add(oppositeVertex.getIndex());
                    verticesDone++;

                    graph.removeVertex(minVertex);
                    vv.repaint();

                    MyGraphVisualizer.informationPanel.info.append("Removed vertex with minimum label: " + minVertex + " (to be added to the sister code)");
                    MyGraphVisualizer.informationPanel.info.append("Opposite vertex: " + oppositeVertex + " (to be added to the Prufer code)");
                    MyGraphVisualizer.informationPanel.info.append("Current Prufer Code: " + pruferCode);
                    MyGraphVisualizer.informationPanel.info.append("Current Sister Code " + sisterCode);

                    stepOne = true;
                }
            } else {
                MyGraphVisualizer.informationPanel.info.append("\nOnly 2 vertices left; algorithm terminated");
                MyGraphVisualizer.informationPanel.info.append("Prufer Code: " + pruferCode);
                MyGraphVisualizer.informationPanel.info.append("Sister Code " + sisterCode);
                terminate();
            }
         }

    }

    @Override
    public void reset() {

        super.reset();

        if (running) {
            vertexBuffer.clear();
            pruferCode.clear();
            sisterCode.clear();
            running = false;
        }
    }

}
    