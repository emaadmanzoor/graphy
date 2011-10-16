package graphy;

import graphy.GraphElements.MyEdge;
import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BFSVisualizer extends AlgorithmVisualizer {

    /* Inherits */
    // graph: The graph the algorithm is applied to
    // vv: The Visualisation Viewer object corresponding to the graph

    private Queue vertexQueue; /* The stack of Vertices used for BFS */

    /* Constructor */
    public BFSVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        /* Algorithm initialization steps */

        if (MyVertexFactory.sourceVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to right-click and select a source Vertex first");
            return;
        }

        super.initialize();
        MyGraphVisualizer.informationPanel.info.append("\nInitializing the BFS algorithm:");

        for (GraphElements.MyVertex v : graph.getVertices()) {
            v.setUnvisited();   /* Set all vertices as unvisited */
        }

        MyGraphVisualizer.informationPanel.info.append("All vertices set as unvisited");

        vertexQueue = new ConcurrentLinkedQueue();  /* Create the Vertex Stack */

        MyVertexFactory.sourceVertex.setVisited();    /* Mark the source vertex as visited */
        vertexQueue.offer(MyVertexFactory.sourceVertex);   /* Push the source vertex onto the stack */

        MyGraphVisualizer.informationPanel.info.append("Source vertex " + MyVertexFactory.sourceVertex + " visted and pushed. Current Queue: " + vertexQueue);

        vv.getRenderContext().getPickedVertexState().pick(MyVertexFactory.sourceVertex, true);    /* Mark the source vertex */
        running = true; /* Set BFS as running */

    }

    public void step() {

        if (!running) {
            initialize();
        }
        else {

            /* The BFS Algorithm */

            /* If the queue is not empty, then pop the queue and mark all the popped vertex's neighbours */
            if (!vertexQueue.isEmpty()) {
                
                MyVertex v = (MyVertex) vertexQueue.poll();

                if (getUnvisitedSuccessorCount(v) > 0) {
                    for (MyVertex u : graph.getSuccessors(v)) {
                        if (!(u.isVisited())) {
                            u.setVisited();
                            vv.getRenderContext().getPickedVertexState().pick(u, true);
                            vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(v, u), true);
                            vertexQueue.offer(u);
                        }
                    }
                    MyGraphVisualizer.informationPanel.info.append("Popped " + v + " and pushed its successors. Current Queue: " + vertexQueue);
                } else {
                    vv.getRenderContext().getPickedVertexState().pick(v, false);
                    for (MyEdge e : graph.getInEdges(v))
                        vv.getRenderContext().getPickedEdgeState().pick(e, false);
                    MyGraphVisualizer.informationPanel.info.append("Popped " + v + "; " + v + " has no unvisited successors. Current Queue: " + vertexQueue);
                }
            } else {
                 MyGraphVisualizer.informationPanel.info.append("\nVertex Queue is empty; BFS terminated");
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
        running = false;
    }

    private int getUnvisitedSuccessorCount(MyVertex v) {
        int unvisitedSuccessorCount = 0;
        for (MyVertex u : graph.getSuccessors(v)) {
            if (!(u.isVisited()))
                unvisitedSuccessorCount++;
        }
        return unvisitedSuccessorCount;
    }

}
