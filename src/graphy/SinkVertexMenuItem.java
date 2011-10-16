/*
 * DeleteVertexMenuItem.java
 *
 * Created on March 21, 2007, 2:03 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package graphy;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * A class to implement the deletion of a vertex from within a
 * PopupVertexEdgeMenuMousePlugin.
 * @author Dr. Greg M. Bernstein
 */
public class SinkVertexMenuItem<V> extends JMenuItem implements VertexMenuListener<V> {
    private V vertex;

    /** Creates a new instance of DeleteVertexMenuItem */
    public SinkVertexMenuItem() {
        super("Set Sink Vertex");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                MyVertexFactory.sinkVertex = (MyVertex) vertex;
                MyGraphVisualizer.informationPanel.info.append("Set " + vertex + " as sink vertex");
            }
        });
    }

    /**
     * Implements the VertexMenuListener interface.
     * @param v
     * @param visComp
     */
    public void setVertexAndView(V v, VisualizationViewer visComp) {
        this.vertex = v;
        this.setText("Set " + v.toString() + " As Sink");
    }

}
