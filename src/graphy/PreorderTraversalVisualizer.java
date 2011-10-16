package graphy;

import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.Stack;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PreorderTraversalVisualizer extends AlgorithmVisualizer {

    /* Inherits */
    // graph: The graph the algorithm is applied to
    // vv: The Visualisation Viewer object corresponding to the graph

    private Stack vertexStack; /* The stack of Vertices used for DFS */
    private Queue<MyVertex> traversalSequence;
    
    /* Constructor */
    public PreorderTraversalVisualizer(MyGraphVisualizer graphy) {
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
        MyGraphVisualizer.informationPanel.info.append("\nInitializing the Preorder Traversal Visualizer (ROOT-LEFT-RIGHT):");

        for (GraphElements.MyVertex v : graph.getVertices()) {
            v.setUnvisited();   /* Set all vertices as unvisited */
        }

        vertexStack = new Stack();  /* Create the Vertex Stack */
        traversalSequence = new ConcurrentLinkedQueue<MyVertex>();
        
        MyVertexFactory.sourceVertex.setVisited();    /* Mark the source vertex as visited */
        vertexStack.push(MyVertexFactory.sourceVertex);   /* Push the source vertex onto the stack */
        traversalSequence.add(MyVertexFactory.sourceVertex);
        
        MyGraphVisualizer.informationPanel.info.append("Root traversed: " + MyVertexFactory.sourceVertex);
        MyGraphVisualizer.informationPanel.info.append("Preorder Traversal Sequence: " + traversalSequence);
        
        vv.getRenderContext().getPickedVertexState().pick(MyVertexFactory.sourceVertex, true);    /* Mark the source vertex */
        running = true;

    }

    public void step() {

        if (!running) {
            initialize();
        }
        else {

            if (!vertexStack.isEmpty()) {
                MyVertex leftChild = getLeftChild((MyVertex)vertexStack.peek());

                if (leftChild == null) {

                    MyVertex poppedVertex = (MyVertex) vertexStack.pop();
                    
                    if (vertexStack.isEmpty()) {
                        MyGraphVisualizer.informationPanel.info.append("\nPreorder Traversal Terminated.");
                        vv.getRenderContext().getPickedVertexState().clear();
                        vv.getRenderContext().getPickedEdgeState().clear();
                        running = false;
                        terminate();
                    } else {

                        MyVertex rightChild = getLeftChild((MyVertex)vertexStack.peek());

                        if (rightChild == null) {
                            if (vertexStack.isEmpty()) {
                                MyGraphVisualizer.informationPanel.info.append("\nPreorder Traversal Terminated.");
                                vv.getRenderContext().getPickedVertexState().clear();
                                vv.getRenderContext().getPickedEdgeState().clear();
                                running = false;
                                terminate();
                            } else {
                                vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge((MyVertex) vertexStack.peek(), poppedVertex), false);
                                vv.getRenderContext().getPickedVertexState().pick(poppedVertex, false);
                            }
                        } else {
                            rightChild.setVisited();
                            MyVertex parentVertex = (MyVertex) vertexStack.peek();
                            vertexStack.push(rightChild);
                            traversalSequence.add(rightChild);
                            vv.getRenderContext().getPickedVertexState().pick(rightChild, true);

                            vv.getRenderContext().getPickedEdgeState().clear();
                            vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(parentVertex, rightChild), true);

                            MyGraphVisualizer.informationPanel.info.append("Right Child: " + rightChild);
                            MyGraphVisualizer.informationPanel.info.append("Preorder Traversal Sequence: " + traversalSequence);

                            vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge((MyVertex) vertexStack.peek(), poppedVertex), false);
                            vv.getRenderContext().getPickedVertexState().pick(poppedVertex, false);
                        }
                    }
                    
                } else {

                   leftChild.setVisited();
                   MyVertex parentVertex = (MyVertex) vertexStack.peek();
                   vertexStack.push(leftChild);
                   traversalSequence.add(leftChild);
                   vv.getRenderContext().getPickedVertexState().pick(leftChild, true);
                   
                   vv.getRenderContext().getPickedEdgeState().clear();
                   vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(parentVertex, leftChild), true);
                                     
                   MyGraphVisualizer.informationPanel.info.append("Left Child: " + leftChild);
                   MyGraphVisualizer.informationPanel.info.append("Preorder Traversal Sequence: " + traversalSequence);
                   
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
        
        if (graph.getNeighborCount(myVertex) > 3) {
             MyGraphVisualizer.informationPanel.info.append(myVertex + " has more than 2 children, cannot traverse this graph.");
             return null;
        }

        for (MyVertex v : graph.getNeighbors(myVertex)) {
            v.setX(graphy.layout.getX(v));
            if (((int) v.getX() < minimumX) && !(v.isVisited())) {         
                minimumX = (int) v.getX();
                leftChild = v;
            }
        }

        return leftChild;
    }

}