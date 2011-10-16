/*
 * MyMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package graphy;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * A collection of classes used to assemble popup mouse menus for the custom
 * edges and vertices developed in this example.
 * @author Dr. Greg M. Bernstein
 */
public class MyMouseMenus {
    
    public static class EdgeMenu extends JPopupMenu {        
        public EdgeMenu(final JFrame frame) {
            super("Edge Menu");
            this.add(new EdgePropItem(frame));
            this.addSeparator();
            this.add(new DeleteEdgeMenuItem<GraphElements.MyEdge>());
        }   
    }
    
    public static class EdgePropItem extends JMenuItem implements EdgeMenuListener<GraphElements.MyEdge>, MenuPointListener {
        GraphElements.MyEdge edge;
        VisualizationViewer visComp;
        Point2D point;
        
        public void setEdgeAndView(GraphElements.MyEdge edge, VisualizationViewer visComp) {
            this.edge = edge;
            this.visComp = visComp;
        }

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public  EdgePropItem(final JFrame frame) {            
            super("Edit Edge Properties");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EdgePropertyDialog dialog = new EdgePropertyDialog(frame, edge);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }   
            });
        }       
    }
    
    public static class VertexMenu extends JPopupMenu {
        public VertexMenu() {
            super("Vertex Menu");
            this.add(new SourceVertexMenuItem<GraphElements.MyVertex>());
            this.add(new SinkVertexMenuItem<GraphElements.MyVertex>());
            this.add(new EditVertexLabelMenuItem<GraphElements.MyVertex>());
            this.add(new DeleteVertexMenuItem<GraphElements.MyVertex>());
        }
    }  
}
