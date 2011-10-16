package graphy;

import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.Stack;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PostorderTraversalVisualizer extends AlgorithmVisualizer {

    /* Inherits */
    // graph: The graph the algorithm is applied to
    // vv: The Visualisation Viewer object corresponding to the graph

    private Stack vertexStack; /* The stack of Vertices used for DFS */
    private Queue<MyVertex> traversalSequence;
    private MyVertex currentVertex;

    /* Constructor */
    public PostorderTraversalVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        /* Algorithm initialization steps */

        if (MyVertexFactory.sourceVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to select a root vertex first; choose one by right-clicking on the vertex");
            return;
        }

        super.initialize();
        MyGraphVisualizer.informationPanel.info.append("\nInitialized the Postorder Traversal Visualizer (LEFT-RIGHT-ROOT):");

        for (GraphElements.MyVertex v : graph.getVertices()) {
            v.setUnvisited();   /* Set all vertices as unvisited */
        }

        vertexStack = new Stack();  /* Create the Vertex Stack */
        traversalSequence = new ConcurrentLinkedQueue<MyVertex>();

        vertexStack.push(MyVertexFactory.sourceVertex);
        running = true;

    }

    public void step() {

        if (!running) {
            initialize();
        }
        else {

            if (vertexStack.isEmpty()) {
                MyGraphVisualizer.informationPanel.info.append("\nPostorder Traversal Terminated.");
                vv.getRenderContext().getPickedVertexState().clear();
                vv.getRenderContext().getPickedEdgeState().clear();
                running = false;
                terminate();
                return;
            }

            currentVertex = (MyVertex) vertexStack.peek();
            MyVertex currentLeft = getLeftChild(currentVertex);
            MyVertex currentRight = getRightChild(currentVertex);
            vv.getRenderContext().getPickedEdgeState().clear();
            vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(currentLeft, currentVertex), true);
            vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(currentRight, currentVertex), true);
            MyGraphVisualizer.informationPanel.info.append("Currently at " + currentVertex + ", left child: " + currentLeft + ", right child: " + currentRight);
            
            if ((currentLeft != null) && !(currentLeft.isVisited())) {
                vertexStack.push(currentLeft);
            } else {
                if ((currentRight != null) && !(currentRight.isVisited())) {
                    vertexStack.push(currentRight);
                } else {
                    vv.getRenderContext().getPickedVertexState().pick(currentVertex, true);
                    traversalSequence.add(currentVertex);
                    currentVertex.setVisited();
                    MyGraphVisualizer.informationPanel.info.append("\nTraversal Sequence: " + traversalSequence);
                    vertexStack.pop();
                }
            }
        }

    }

    /* Termination doesn't involve anything extra, taken care of by the superclass */

    @Override
    public void reset() {

        super.reset();

        if (running) {

            for (GraphElements.MyVertex v : graph.getVertices()) {
                v.setUnvisited();   /* Set all vertices as unvisited */
            }

            /* Clear the stack */
            vertexStack.clear();
            traversalSequence.clear();
            running = false;
        }

    }

    private MyVertex getLeftChild(MyVertex myVertex) {

        int minimumX = 1000;
        MyVertex leftChild = null;
        myVertex.setY(graphy.layout.getY(myVertex));
        myVertex.setX(graphy.layout.getX(myVertex));

        if (graph.getNeighborCount(myVertex) > 3) {
             MyGraphVisualizer.informationPanel.info.append(myVertex + " has more than 2 children, cannot traverse this graph.");
             return null;
        }

        for (MyVertex v : graph.getNeighbors(myVertex)) {

            v.setX(graphy.layout.getX(v));
            v.setY(graphy.layout.getY(v));

            if (v.getY() < myVertex.getY()) {
                continue;
            }

            if (v.getX() < myVertex.getX()) {
                leftChild = v;
            }
        }
        return leftChild;
    }

    private MyVertex getRightChild(MyVertex myVertex) {

        int maximumX = -1000;
        MyVertex rightChild = null;
        myVertex.setY(graphy.layout.getY(myVertex));
        myVertex.setX(graphy.layout.getX(myVertex));

        if (graph.getNeighborCount(myVertex) > 3) {
             MyGraphVisualizer.informationPanel.info.append(myVertex + " has more than 2 children, cannot traverse this graph.");
             return null;
        }

        for (MyVertex v : graph.getNeighbors(myVertex)) {

            v.setX(graphy.layout.getX(v));
            v.setY(graphy.layout.getY(v));

            if (v.getY() < myVertex.getY()) {
                continue;
            }

            if (v.getX() > myVertex.getX()) {
                rightChild = v;
            }
        }
        return rightChild;
    }

}