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
import javax.swing.JOptionPane;

/**
 * A class to implement the deletion of a vertex from within a 
 * PopupVertexEdgeMenuMousePlugin.
 * @author Dr. Greg M. Bernstein
 */
public class EditVertexLabelMenuItem<V> extends JMenuItem implements VertexMenuListener<V> {
    private V vertex;
    private VisualizationViewer visComp;
    
    /** Creates a new instance of DeleteVertexMenuItem */
    public EditVertexLabelMenuItem() {
        super("Edit Vertex Label");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String label = (String) JOptionPane.showInputDialog(null, "Edit Vertex Label: ", "Graphy", 1, null, null, ((MyVertex) vertex).getName());
                ((MyVertex) vertex).setName(label);
                visComp.repaint();
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
        this.visComp = visComp;
        this.setText("Edit Vertex Label");
    }
    
}
