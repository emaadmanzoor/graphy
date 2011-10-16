package graphy;

import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.Stack;

public class DFSVisualizer extends AlgorithmVisualizer {

    /* Inherits */
    // graph: The graph the algorithm is applied to
    // vv: The Visualisation Viewer object corresponding to the graph
    
    private Stack vertexStack; /* The stack of Vertices used for DFS */
    
    /* Constructor */
    public DFSVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        /* Algorithm initialization steps */

        if (MyVertexFactory.sourceVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to select a source vertex first; choose one by right-clicking on the vertex");
            return;
        }

        super.initialize();
        MyGraphVisualizer.informationPanel.info.append("\nInitializing the DFS algorithm:");

        for (GraphElements.MyVertex v : graph.getVertices()) {
            v.setUnvisited();   /* Set all vertices as unvisited */
        }

        MyGraphVisualizer.informationPanel.info.append("\nAll vertices set as unvisited");

        vertexStack = new Stack();  /* Create the Vertex Stack */

        MyVertexFactory.sourceVertex.setVisited();    /* Mark the source vertex as visited */
        vertexStack.push(MyVertexFactory.sourceVertex);   /* Push the source vertex onto the stack */

        MyGraphVisualizer.informationPanel.info.append("Source vertex " + MyVertexFactory.sourceVertex + " visited and pushed. Current Stack: " + vertexStack);
        
        vv.getRenderContext().getPickedVertexState().pick(MyVertexFactory.sourceVertex, true);    /* Mark the source vertex */
        running = true; /* Set DFS as running */
       
    }

    public void step() {

        if (!running) {
            initialize();
        }
        else {
            
            /* The DFS Algorithm */

            /* If the stack is not empty, then get the index of the first unvisited vertex adjacent to the top of the stack */
            if (!vertexStack.isEmpty()) {
                MyVertex adjacentUnvisitedVertex = getAdjacentUnvisitedVertex((MyVertex)vertexStack.peek());

                /* If no adjacent unvisited vertex was found, do: */
                /* Pop the stack and then check if it's empty */
                /* If the stack is empty, DFS is done. Clear the stack and reset the graph. */
                if (adjacentUnvisitedVertex == null) {

                    MyVertex poppedVertex = (MyVertex) vertexStack.pop();
                    
                    if (vertexStack.isEmpty()) {
                        MyGraphVisualizer.informationPanel.info.append("\n" + poppedVertex + " has no adjacent unvisited vertex\n\nVertex stack is empty. DFS terminated.");
                        vv.getRenderContext().getPickedVertexState().clear();
                        vv.getRenderContext().getPickedEdgeState().clear();
                        running = false;
                        terminate();
                    }
                    else {
                        MyGraphVisualizer.informationPanel.info.append(poppedVertex + " has no adjacent unvisited vertex. Popped " + poppedVertex + ". Current Stack: " + vertexStack);
                        vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge((MyVertex) vertexStack.peek(), poppedVertex), false);
                        vv.getRenderContext().getPickedVertexState().pick(poppedVertex, false);
                    }
                }

                /* If an adjacent unvisited vertex is found, do: */
                /* Set the vertex as visited, mark it and push it on to the stack */
                /* Mark the edge between the previous and the current vertex */
                else {
                   MyVertex oldVertex = (MyVertex) vertexStack.peek();
                   adjacentUnvisitedVertex.setVisited();
                   vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge((MyVertex) vertexStack.peek(), adjacentUnvisitedVertex), true);
                   vertexStack.push(adjacentUnvisitedVertex);
                   vv.getRenderContext().getPickedVertexState().pick(adjacentUnvisitedVertex, true);
                   MyGraphVisualizer.informationPanel.info.append("Visited and pushed " + oldVertex + "'s adjacent unvisited vertex " + adjacentUnvisitedVertex + ". Current Stack: " + vertexStack);
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
            running = false;
        }
        
    }

    private MyVertex getAdjacentUnvisitedVertex (GraphElements.MyVertex v) {
        for (GraphElements.MyVertex adjacentVertex : graph.getSuccessors(v)) {
            if (!adjacentVertex.isVisited())
                return adjacentVertex;
        }
        return null;
    }
}
