
package graphy;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;

/**
 *
 * @author pingu
 */

public abstract class AlgorithmVisualizer {

    /* Private fields */
    protected Graph<GraphElements.MyVertex, GraphElements.MyEdge> graph;    /* The graph to run the algorithm on */
    protected boolean running = false;    /* Is the algorithm currently running? */
    protected VisualizationViewer<GraphElements.MyVertex,GraphElements.MyEdge> vv;
    protected MyGraphVisualizer graphy;
    
    /* Abstract Methods */
    public abstract void step();

    private EditingModalGraphMouse oldGraphMouse;
    
    /* Constructor */
    public AlgorithmVisualizer(MyGraphVisualizer graphy) {
        this.graphy = graphy;
        this.vv = graphy.vv;
        this.graph = this.vv.getGraphLayout().getGraph();
    }

    protected void initialize() {

        MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setEnabled(false);
        MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.disableButtonPanel();
        
        vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
        vv.getRenderContext().getPickedEdgeState().clear(); /* Clear all picked edges */
        
        this.oldGraphMouse = (EditingModalGraphMouse) vv.getGraphMouse();
        vv.setGraphMouse(null);
        
    };

    protected void terminate() {
        MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setEnabled(true);
        MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.enableButtonPanel();
        vv.setGraphMouse(oldGraphMouse);
        vv.requestFocusInWindow();
    };

    public void reset() {
        vv.getRenderContext().getPickedVertexState().clear();   /* Clear all picked vertices */
        vv.getRenderContext().getPickedEdgeState().clear(); /* Clear all picked edges */

        MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setEnabled(true);
        MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.enableButtonPanel();
        
        vv.setGraphMouse(oldGraphMouse);
        vv.requestFocusInWindow();
    }


}
