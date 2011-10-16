/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphy;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.algorithms.util.SelfLoopEdgePredicate;

//import edu.uci.ics.jung.io.MatrixFile;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import graphy.GraphElements.MyEdge;
import graphy.GraphElements.MyEdgeFactory;
import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.NotPredicate;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

/**
 *
 * @author rijul
 */
public class FileMenuListener implements ActionListener {

    private JFrame mainFrame;
    private MyGraphVisualizer graphy;
    VisualizationViewer<MyVertex, MyEdge> vv;
    Graph<MyVertex, MyEdge> sgv;

    private static FileMenuListener instance = new FileMenuListener(null, null);
    
    public FileMenuListener(JFrame f, MyGraphVisualizer graphy){
        this.mainFrame = f;
        this.graphy = graphy;
    }

    public static FileMenuListener getInstance() {
        return instance;
    }
    
    public void actionPerformed (ActionEvent a)
    {
        Component c = (Component) a.getSource();
        String text = a.getActionCommand();

        if (text.endsWith("Exit")) {
            System.exit(0);
        }

        /* Menu items to create unweighted graphs */
        else if(text.equalsIgnoreCase("Directed Unweighted Graph")) {
             sgv = new DirectedSparseGraph<MyVertex, MyEdge>();
             MyEdgeFactory.weighted = false;
             MyGraphVisualizer.informationPanel.info.append("Created a new directed unweighted graph.");
             createNewGraph(false);
        }

        else if(text.equalsIgnoreCase("Undirected Unweighted Graph")) {
             sgv = new UndirectedSparseGraph<MyVertex, MyEdge>();
             MyEdgeFactory.weighted = false;
             MyGraphVisualizer.informationPanel.info.append("Created a new undirected unweighted graph.");
             createNewGraph(false);
        }

        /* Menu items to create weighted graphs */
        else if(text.equalsIgnoreCase("Directed Weighted Graph"))
        {
             sgv = new DirectedSparseGraph<MyVertex, MyEdge>();
             MyEdgeFactory.weighted = true;
             MyGraphVisualizer.informationPanel.info.append("Created a new directed weighted graph.");
             createNewGraph(false);
        }
        else if(text.equalsIgnoreCase("Undirected Weighted Graph"))
        {
             sgv = new UndirectedSparseGraph<MyVertex, MyEdge>();
             MyEdgeFactory.weighted = true;
             MyGraphVisualizer.informationPanel.info.append("Created a new undirected weighted graph.");
             createNewGraph(false);
        }

        /* Other utility functions */
        else if(text.contains("JPEG")) {
            File f = graphy.fileChooser("Export As JPEG");
            try {
                String path = (f.getAbsolutePath().endsWith(".jpg")) ? f.getAbsolutePath() : f.getAbsolutePath() + ".jpg";
                writeJPEGImage(path);
                MyGraphVisualizer.informationPanel.info.append("Graph successfully exported as a JPEG image.");
            } catch (NullPointerException ex) {
                
            }
        }
        else if(text.contains("EPS")) {
            File f = graphy.fileChooser("Export As EPS");
            try {
                String path = (f.getAbsolutePath().endsWith(".eps")) ? f.getAbsolutePath() : f.getAbsolutePath() + ".eps";
                writeEPSImage(path);
                MyGraphVisualizer.informationPanel.info.append("Graph successfully exported as an EPS image.");
            } catch (NullPointerException ex) {

            }
        }
        else if(text.contains("Vertex Labels")) {
            toggleVertexLabels();
        }
        else if(text.contains("Edge Labels")) {
            toggleEdgeLabels();
        }
        else if(text.contains("Open Graph")) {
          try {
            openGraph();
          }
          catch (ParserConfigurationException e) {}
          catch (SAXException s){}
          catch (IOException i){}
        
        }
        else {
            for (Component x : graphy.mainWindow.getJMenuBar().getMenu(1).getMenuComponents()) {
                if (x.equals(c))
                    showInformationWindow(c.getName());
            }
        }
    }

    private void createNewGraph(boolean loaded) {

            AbstractLayout<MyVertex, MyEdge> layout;
            
            if (!loaded) {
                layout = new StaticLayout(this.sgv);
            } else {
                layout = new StaticLayout(this.sgv, new Transformer<MyVertex, Point2D>() {
                    public Point2D transform(MyVertex v) {
                        Point2D p = new Point2D.Double(v.getX(), v.getY());
                        return p;
                    }                
                });
            }

            Dimension graphViewerSize = new Dimension();
            graphViewerSize.width = graphy.graphPanel.getSize().width - 20;
            graphViewerSize.height = graphy.graphPanel.getSize().height - 20;
            layout.setSize(graphViewerSize);
            
            vv = new VisualizationViewer<MyVertex, MyEdge>(layout);
            vv.setPreferredSize(layout.getSize());
            vv.setBackground(Color.white);
            vv.setForeground(Color.BLACK);
            
            // 4. Show vertex and edge labels
            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

            vv.getRenderer().setVertexRenderer(
        		new GradientVertexRenderer<MyVertex,MyEdge>(
        				Color.white, Color.blue,
        				Color.white, Color.yellow,
        				vv.getPickedVertexState(),
        				false));
            
            vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

            PickedState<MyEdge> pes = vv.getPickedEdgeState();
            vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<MyEdge>(pes, Color.black, Color.red));
            vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.red, true));

            if (MyEdgeFactory.weighted) {
                vv.getRenderContext().setEdgeLabelTransformer(new Transformer<MyEdge, String>() {
                    public String transform(MyEdge e) {
                        return (e.toString() + " " + e.getWeight() + "/" + e.getCapacity());
                    }
                });
            } else {
                vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
            }

            vv.getRenderContext().setLabelOffset(20);

            /* Disable self loops */
            //vv.getRenderContext().setEdgeIncludePredicate(new NotPredicate(new SelfLoopEdgePredicate()));

            MyEdgeFactory.setDefaultCapacity(0);
            MyEdgeFactory.setDefaultWeight(0);
            
            // Create a graph mouse and add it to the visualization viewer
            EditingModalGraphMouse gm = new EditingModalGraphMouse(vv.getRenderContext(),
                                                                    MyVertexFactory.getInstance(),
                                                                    MyEdgeFactory.getInstance());

            // Add our own mouse popup plugin, because the default one is screwed.
            PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
            JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(graphy.mainWindow);
            JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu();
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            myPlugin.setEdgePopup(edgeMenu);
            myPlugin.setVertexPopup(vertexMenu);

            gm.remove(gm.getPopupEditingPlugin());  // Removes the existing popup editing plugin
            gm.add(myPlugin);                       // Add our new plugin to the mouse

            vv.setGraphMouse(gm);

            // Add the mouse mode menu
            JMenu modeMenu = gm.getModeMenu();
            modeMenu.setIcon(null);
            modeMenu.setText("Mouse Mode");
            modeMenu.setPreferredSize(new Dimension(90, 35));
            graphy.mainWindow.getJMenuBar().add(modeMenu, graphy.mainWindow.getJMenuBar().getComponentCount()-1);

            // Start in editing mode
            gm.setMode(ModalGraphMouse.Mode.EDITING);

            // Add key listener for Transform (T), Edit (E) and Pick (P)
            vv.addKeyListener(gm.getModeKeyListener());

            // Adding the visualization viewer to the graph panel of the main frame
            final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);

            graphy.graphPanel.add("Center", panel);
            graphy.graphPanel.add("South", new JLabel(" "));

            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.enableButtonPanel();
            MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.enableButtonPanel();

            fileOptionsAccess(false,false,true, true);

            if (!loaded) {
                MyEdgeFactory.resetLinkcount();
                MyVertexFactory.resetNodecount();
            }
            
            graphy.vv = vv;
            graphy.layout = layout;

            graphy.vv.repaint();
            graphy.graphPanel.repaint();
            graphy.vv.requestFocusInWindow();
    }

    public void openGraph() throws ParserConfigurationException, SAXException, IOException {

        File f = graphy.fileChooser("Open Graph");
        if (f == null) return;
        String filename = f.getAbsolutePath();

        /* Create the File Reader for graphML files */
        try {

            BufferedReader fileReader = new BufferedReader(new FileReader(filename));

            /* Create the Graph Transformer */
            Transformer<GraphMetadata, Graph<MyVertex, MyEdge>> graphTransformer = new Transformer<GraphMetadata, Graph<MyVertex, MyEdge>>() {
                public Graph<MyVertex, MyEdge> transform(GraphMetadata metadata) {
                    if (metadata.getEdgeDefault().equals(metadata.getEdgeDefault().DIRECTED)) {
                        return new DirectedSparseGraph<MyVertex, MyEdge>();
                    }
                    else {
                        return new UndirectedSparseGraph<MyVertex, MyEdge>();
                    }
                }
            };

            /* Create the Vertex Transformer */
            Transformer<NodeMetadata, MyVertex> vertexTransformer = new Transformer<NodeMetadata, MyVertex>() {
                public MyVertex transform(NodeMetadata metadata) {
                    MyVertex v = MyVertexFactory.getInstance().create();
                    v.setX(Double.parseDouble(metadata.getProperty("x")));
                    v.setY(Double.parseDouble(metadata.getProperty("y")));
                    return v;
                }
            };

            /* Create the Edge Transformer */
            Transformer<EdgeMetadata, MyEdge> edgeTransformer = new Transformer<EdgeMetadata, MyEdge>() {
                public MyEdge transform(EdgeMetadata metadata) {
                    MyEdge e = MyEdgeFactory.getInstance().create();
                    try {
                        e.setCapacity(Integer.parseInt(metadata.getProperty("capacity")));
                        e.setWeight(Integer.parseInt(metadata.getProperty("weight")));
                    } catch (Exception ex) {
                        MyGraphVisualizer.informationPanel.info.append("Error loading graph from file; possible unsupported format");
                    }
                    return e;
                }
            };

            /* Create the Hyperedge Transformer */
            Transformer<HyperEdgeMetadata, MyEdge> hyperEdgeTransformer = new Transformer<HyperEdgeMetadata, MyEdge>() {
                public MyEdge transform(HyperEdgeMetadata metadata) {
                    MyEdge e = MyEdgeFactory.getInstance().create();
                    return e;
                }
            };
            /* Create the graphMLReader2 */
            GraphMLReader2<Graph<MyVertex, MyEdge>, MyVertex, MyEdge> graphReader = new GraphMLReader2<Graph<MyVertex, MyEdge>, MyVertex, MyEdge>
                    ( fileReader,
                      graphTransformer,
                      vertexTransformer,
                      edgeTransformer,
                      hyperEdgeTransformer);

            try {
                
                /* Get the new graph object from the GraphML file */
                this.sgv = graphReader.readGraph();

                MyGraphVisualizer.informationPanel.info.append("Loaded graph");

                boolean weighted = false;
                int maximumEdgeIndex = 0;

                for (MyEdge e : sgv.getEdges()) {
                    if (e.getCapacity() != 0 || e.getWeight() != 0) {
                        weighted = true;
                    }
                    if (e.getIndex() > maximumEdgeIndex)
                        maximumEdgeIndex = e.getIndex();
                }

                int maximumVertexIndex = 0;

                for (MyVertex v : sgv.getVertices()) {
                    if (v.getIndex() > maximumVertexIndex)
                        maximumVertexIndex = v.getIndex();
                }

                MyVertexFactory.setNodeCount(maximumVertexIndex + 1);
                MyEdgeFactory.setLinkCount(maximumEdgeIndex + 1);

                MyEdgeFactory.weighted = weighted;

                createNewGraph(true);

                graphReader.close();
                
            } catch (GraphIOException e) {
                MyGraphVisualizer.informationPanel.info.append("Error loading graph from file; possible unsupported format");
            }
            
        } catch (FileNotFoundException ex) {
            MyGraphVisualizer.informationPanel.info.append("Error loading graph from file; file not found");
        }
    }

    public Component componentFinder(Component arr[],String s) {
        int i;
        for(i=0;i<arr.length;i++)
        {
            if(arr[i].toString().contains(s))
            {
                break;
            }
        }
        return arr[i];
    }

    public void writeJPEGImage(String filename) {
        int width = vv.getWidth();
        int height = vv.getHeight();
        Color bg = vv.getBackground();

        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bi.createGraphics();
        graphics.setColor(bg);
        graphics.fillRect(0,0, width, height);
        
        vv.paintAll(graphics);

        try{
           ImageIO.write(bi,"jpeg",new File(filename));
        }catch(Exception e){}
    }

    public void writeEPSImage(String filename) {
        FileOutputStream finalImage = null;
        EpsGraphics2D graphics = null;
        try {
            int width = vv.getWidth();
            int height = vv.getHeight();
            Color bg = vv.getBackground();
            finalImage = new FileOutputStream(filename);
            graphics = new EpsGraphics2D("Graphy EPS", finalImage, 0, 0, width, height);
            graphics.setColor(bg);
            graphics.fillRect(0, 0, width, height);
            vv.paintAll(graphics);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileMenuListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileMenuListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                graphics.flush();
                finalImage.close();
            } catch (IOException ex) {
                Logger.getLogger(FileMenuListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void toggleVertexLabels() {
        if(vv.getRenderer().getVertexLabelRenderer().getClass().toString().contains("NOOP")) {
            graphy.vv.getRenderer().setVertexLabelRenderer(new BasicVertexLabelRenderer<MyVertex, MyEdge>(VertexLabel.Position.AUTO));
        } else {
            graphy.vv.getRenderer().setVertexLabelRenderer(new Renderer.VertexLabel.NOOP());
        }
        graphy.vv.repaint();
    }

    private void toggleEdgeLabels() {
        if(vv.getRenderer().getEdgeLabelRenderer().getClass().toString().contains("NOOP")) {
            graphy.vv.getRenderer().setEdgeLabelRenderer(new BasicEdgeLabelRenderer());
        } else {
            graphy.vv.getRenderer().setEdgeLabelRenderer(new Renderer.EdgeLabel.NOOP());
        }
        graphy.vv.repaint();    }

    public void fileOptionsAccess(boolean newG, boolean openG, boolean exportG, boolean saveG) {

            Component fileComponents[] = mainFrame.getJMenuBar().getMenu(0).getMenuComponents();
            componentFinder(fileComponents,"text=New Graph").setEnabled(newG);
            componentFinder(fileComponents,"text=Open Graph").setEnabled(openG);
            componentFinder(fileComponents,"text=Save Graph").setEnabled(saveG);
            componentFinder(fileComponents,"text=Export as JPEG").setEnabled(exportG);
            componentFinder(fileComponents,"text=Export as EPS").setEnabled(exportG);
            
            graphy.mainWindow.getJMenuBar().getMenu(1).getItem(0).setEnabled(exportG);    /* View Vertex Labels */
            graphy.mainWindow.getJMenuBar().getMenu(1).getItem(1).setEnabled(exportG);    /* View Edge Labels */

            mainFrame.invalidate();
            mainFrame.validate();
            mainFrame.repaint();
    }

    private void showInformationWindow(String algorithm) {

        String bfsInformation =
            "<html>"+
            "<h3>Breadth First search (BFS)</h3>" +
            "<p>BFS is a graph search algorithm that begins at the root node and explores all the neighboring nodes." +
            " Then for each of those nearest nodes, it explores their unexplored neighbor nodes," +
            " and so on, until it finds the goal.</p>" +
            "<p><b>Complexity:  O( | E | + | V | )</b> since every vertex and every edge will be" +
            " explored in the worst case.</p>" +
            "</html>";

        String dfsInformation =
            "<html>"+
            "<h3>Depth First search (DFS)</h3>" +
            "<p>BFS is an algorithm for traversing or searching a tree, tree structure, or graph." +
            " One starts at the root vertex and explores as far as possible along each branch before backtracking.</p>" +
            "<p><b>Complexity: O( | V | + | E | )</b> for explicit graphs traversed without repetition.</p>" +
            "</html>";

        String dijsktrasInformation =
            "<html>"+
            "<h3>Dijkstra's Shortest Path Algorithm</h3>" +
            "<p>Dijkstra's algorithm, conceived by Edsger Dijkstra in  is a graph search algorithm that solves" +
            " the single-source shortest path problem for a graph with nonnegative edge path costs," +
            " producing a shortest path tree. For a given source vertex (node) in the graph," +
            " the algorithm finds the path with lowest cost (i.e. the shortest path) between that vertex" +
            " and every other vertex. It can also be used for finding costs of shortest paths from a single" +
            " vertex to a single destination vertex by stopping the algorithm once the shortest path to the" +
            " destination vertex has been determined. </p>" +
            "<p><b>Complexity: O( | V |<sup>2</sup> + | E | ) = O( | V |<sup>2</sup>)</b> for simplest implementation.</p>" +
            "</html>";

        String fleurysInformation =
            "<html>"+
            "<h3>Fleury's Eulerian Cycle Algorithm</h3>" +
            "<p>Fleury’s Algorithm finds out whether a graph has a Eulerian Circuit or not." +
            " The Graph Works by starting with any edge, then pick the next cyclic edge corresponding to it." +
            " If no cyclic edge is found, ties can be broken arbitrarily, then it selects any other edge.<br></br>" +
            " In graph theory, an Eulerian trail is a trail in a graph which visits every edge exactly once." +
            " Similarly, an Eulerian circuit is an Eulerian trail which starts and ends on the same vertex." +
            " The term Eulerian graph has two common meanings in graph theory. One meaning is a graph with an" +
            " Eulerian circuit, and the other is a graph with every vertex of even degree.</p>" +
            "<p><b>Complexity: O( | E | )</b></p>" +
            "</html>";

        String hierholzersInformation =
            "<html>"+
            "<h3>Hierholzer's Eulerian Cycle Algorithm</h3>" +
            "<p>Hierholzer's Algorithm finds out whether a graph has a Eulerian Circuit or not." +
            " The algorithm works by picking any one vertex and its corresponding closed trail." + 
            " Then for any point in the already covered vertices, it finds another disjoint closed trail" +
            " from that vertex and adds it the already covered set, the algorithm goes on until all disjoint" +
            " cycles are discovered.<br></br>" +
            " In graph theory, an Eulerian trail is a trail in a graph which visits every edge exactly once." +
            " Similarly, an Eulerian circuit is an Eulerian trail which starts and ends on the same vertex." +
            " The term Eulerian graph has two common meanings in graph theory. One meaning is a graph with an" +
            " Eulerian circuit, and the other is a graph with every vertex of even degree.</p>" +
            "</html>";

        String primsInformation =
            "<html>"
            + "<h3>Prim's Minimum Spanning Tree Algorithm</h3>"
            + "<p>Prim's algorithm is an algorithm that finds a minimum spanning tree for a connected weighted"
            + " undirected graph. This means it finds a subset of the edges that forms a tree that includes every"
            + " vertex, where the total weight of all the edges in the tree is minimized.</p>"
            + "<p><b>Complexity:</b></br> "
            + "<ul>"
            + "<li>Adjacency matrix, Searching: <b>O(V<sup>2</sup>)</b><</li><br></br>"
            + "<li>Binary heap and adjacency list: <b>O((V + E) (V)) = O(E log(V))</b></li><br></br>"
            + "<li>Fibonacci heap and adjacency list: <b>O(E + V log(V))</b></li>"
            + "</ul></p>"
            + "</html>";


        String kruskalsInformation =
            "<html>"
            + "<h3>Kruskal's Minimum Spanning Tree Algorithm</h3>"
            + "<p>Kruskal's algorithm is an algorithm that finds a minimum spanning tree for a connected weighted"
            + " graph. This means it finds a subset of the edges that forms a tree that includes every"
            + " vertex, where the total weight of all the edges in the tree is minimized."
            + " If the graph is not connected, then it finds a minimum spanning forest"
            + " (a minimum spanning tree for each connected component).</p>"
            + "<p><b>Complexity:</b></br> "
            + "<ul>"
            + "<li>E is at most V<sup>2</sup> and logV<sup>2</sup> = 2logV is <b>O(log V)</b></li><br></br>"
            + "<li>If we ignore isolated vertices, which will each be their own component of the"
            + " minimum spanning forest, V ≤ E + 1, so log(V) is <b>O(log E)</b></li>"
            + "</ul></p>"
            + "</html>";

        String edmondskarpInformation =
            "<html>"
            + "<h3>Edmonds-Karp Maximum Flow Algorithm</h3>"
            + "<p>Edmonds-Karp algorithm is an implementation of the Ford–Fulkerson method"
            + " for computing the maximum flow in a flow network. The algorithm is identical to the"
            + " Ford–Fulkerson algorithm, except that the search order when finding the augmenting path is"
            + " defined. The path found must be the shortest path which has available capacity."
            + " This can be found by a breadth-first search, ignoring the edge directions.</p>"
            + "<p><b>Complexity: O(|V|.|E|<sup>2</sup>)</b><br></br></p>"
            + "<p>The running time is found by showing that each augmenting path"
            + " can be found in O(|E|) time, that every time at least one of the |E| edges becomes saturated,"
            + " the distance from the saturated edge to the source along the augmenting path must be longer"
            + " than last time it was saturated, and that the distance is at most |V| long."
            + "</p>"
            + "</html>";

        String pruferCodeInformation =
            "<html>"
            + "<h3>Prufer Code Generation Algorithm</h3>"
            + "<p>In combinatorial mathematics, the Prüfer sequence of a labeled tree is a unique sequence"
            + " associated with the tree. The sequence for a tree on n vertices has length n − 2,"
            + " and can be generated by a simple iterative algorithm.</p>"
            + "<p>One can generate a labeled tree's Prüfer sequence by iteratively removing vertices from"
            + " the tree until only two vertices remain. Specifically, consider a labeled tree T with vertices"
            + " {1, 2, ..., n}. At step i, remove the leaf with the smallest label and set the i<sup>th</sup>"
            + " element of the Prüfer sequence to be the label of this leaf's neighbour.</p>"
            + "<p><b>Complexity: O(|V|! / 2)</b></p>"
            + "</html>";

        JFrame infoWindow = new JFrame(algorithm);
        infoWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        infoWindow.setPreferredSize(new Dimension(500, 250));

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle frame = infoWindow.getBounds();
        infoWindow.setLocation((screen.width - frame.width)/2, (screen.height - frame.height)/2);

        JEditorPane infoPane = new JEditorPane();
        infoPane.setContentType(algorithm);
        infoPane.setContentType("text/html");
        infoPane.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(infoPane);
        infoWindow.add(scrollPane);
        
        if (algorithm.equals("Breadth First Search"))
            infoPane.setText(bfsInformation);
        else if (algorithm.equals("Depth First Search"))
            infoPane.setText(dfsInformation);
        else if(algorithm.equals("Prim's Minimum Spanning Tree"))
            infoPane.setText(primsInformation);
        else if (algorithm.equals("Kruskal's Minimum Spanning Tree"))
            infoPane.setText(kruskalsInformation);
        else if (algorithm.equals("Dijkstra's Shortest Path"))
            infoPane.setText(dijsktrasInformation);
        else if (algorithm.equals("Prufer Code Generation"))
            infoPane.setText(pruferCodeInformation);
        else if (algorithm.equals("Fleury's Eulerian Path"))
            infoPane.setText(fleurysInformation);
        else if (algorithm.equals("Hierholzer's Eulerian Path"))
            infoPane.setText(hierholzersInformation);
        else if (algorithm.equals("Edmonds-Karp Maximum Flow"))
            infoPane.setText(edmondskarpInformation);
        
        infoWindow.pack();
        infoWindow.setVisible(true);
    }
}
