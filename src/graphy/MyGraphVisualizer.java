package graphy;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.io.GraphMLWriter;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import graphy.GraphElements.MyEdge;
import graphy.GraphElements.MyVertex;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import java.util.Collection;
import java.util.LinkedList;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.Timer;

import org.apache.commons.collections15.Transformer;

/**
 *
 * @author pingu
 */
public class MyGraphVisualizer {

    /* Graph Creation Variables */
    private SparseGraph<MyVertex, MyEdge> g;
    public VisualizationViewer<MyVertex,MyEdge> vv;
    public AbstractLayout<MyVertex, MyEdge> layout;
    
    /* UI Variables */
    public JFrame mainWindow;
    public static infoPanel informationPanel;
    public JPanel graphPanel;
    
    /* Algorithm Runners */
    public static AlgorithmVisualizer currentAlgorithmVisualizer ;
    
    public File fileChooser(String approveButtonText) {

            //String currentWorkingDirectory = System.getProperty("user.dir");
            String currentWorkingDirectory = System.getProperty("user.home") + "/Desktop";
            currentWorkingDirectory.replace("\\", "/");
            //String desktopDirectory = System.getProperty("user.home") + "/Desktop";
            JFileChooser fc = new JFileChooser(currentWorkingDirectory);
            
            FileFilter gph = new FileFilter() {
                @Override
                public boolean accept(File file) {
                   return ( file.getName().endsWith(".gph") || file.isDirectory() );
                }

                @Override
                public String getDescription() {
                    return "Graphy (GPH) Files";
                }
            };

            FileFilter jpg = new FileFilter() {
                @Override
                public boolean accept(File file) {
                   return ( file.getName().endsWith(".jpg") || file.isDirectory() );
                }

                @Override
                public String getDescription() {
                    return "JPEG Files";
                }
            };
            
            FileFilter eps = new FileFilter() {
                @Override
                public boolean accept(File file) {
                   return ( file.getName().endsWith(".eps") || file.isDirectory() );
                }

                @Override
                public String getDescription() {
                    return "EPS Files";
                }
            };

            fc.setAcceptAllFileFilterUsed(false);

            if (approveButtonText.equals("Export As JPEG"))
                fc.addChoosableFileFilter(jpg);
            else if (approveButtonText.equals("Export As EPS"))
                fc.addChoosableFileFilter(eps);
            else
                fc.addChoosableFileFilter(gph);

            fc.setDialogTitle(approveButtonText);

            File file = null;

            int rc = fc.showDialog(this.mainWindow, approveButtonText);

            if (rc == JFileChooser.APPROVE_OPTION) {

                file  = fc.getSelectedFile();

            }

            return file;
    }

    private void renderSplashFrame(Graphics2D g, int i) {
        final String[] comps = {"Loading", "Loading.", "Loading..", "Loading..."};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0,0,300,200);
        g.setPaintMode();
        g.setColor(Color.DARK_GRAY);
        g.drawString(comps[(i/5)%4], 220, 180);
    }

    private void createMenus(JFrame frame, MyGraphVisualizer graphy) {

        JMenuBar menuBar = new JMenuBar(); // Menu for changing mouse modes

        // Menu for File Options
        JMenu fileMenu = new JMenu("Graph");
        fileMenu.setIcon(null); // I'm using this in a main menu
        //fileMenu.setPreferredSize(new Dimension(47,35)); // Change the size so I can see the text
        FileMenuListener fileEventListener = new FileMenuListener(frame, graphy);

        // Menu for New Graphs
        JMenu newGraphMenu = new JMenu("New Graph");
        newGraphMenu.setIcon(null);

        JMenuItem newUndirectedUnweightedItem = new JMenuItem("Undirected Unweighted Graph");
        JMenuItem newUndirectedWeightedItem = new JMenuItem("Undirected Weighted Graph");
        JMenuItem newDirectedWeightedItem = new JMenuItem("Directed Weighted Graph");      
        JMenuItem newDirectedUnweightedItem = new JMenuItem("Directed Unweighted Graph");

        newGraphMenu.add(newUndirectedUnweightedItem);
        newGraphMenu.add(newDirectedUnweightedItem);
        newGraphMenu.add(newUndirectedWeightedItem);
        newGraphMenu.add(newDirectedWeightedItem);

        newUndirectedWeightedItem.addActionListener(fileEventListener);
        newDirectedWeightedItem.addActionListener(fileEventListener);
        newUndirectedUnweightedItem.addActionListener(fileEventListener);
        newDirectedUnweightedItem.addActionListener(fileEventListener);

        // Other File Options
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem saveGraphItem = new JMenuItem("Save Graph");
        JMenuItem openGraphItem = new JMenuItem("Open Graph");
        JMenuItem exportGraphAsJPEGItem = new JMenuItem("Export as JPEG");
        JMenuItem exportGraphAsEPSItem = new JMenuItem("Export as EPS");
        exportGraphAsJPEGItem.setEnabled(false);
        exportGraphAsEPSItem.setEnabled(false);

        exitItem.addActionListener(fileEventListener);
        exportGraphAsJPEGItem.addActionListener(fileEventListener);
        exportGraphAsEPSItem.addActionListener(fileEventListener);
        openGraphItem.addActionListener(fileEventListener);

        openGraphItem.setAccelerator(KeyStroke.getKeyStroke('O',
                                        Toolkit.getDefaultToolkit(  ).getMenuShortcutKeyMask(  ), false));
        saveGraphItem.setAccelerator(KeyStroke.getKeyStroke('S',
                                        Toolkit.getDefaultToolkit(  ).getMenuShortcutKeyMask(  ), false));

        saveGraphItem.setEnabled(false);

        saveGraphItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.saveBtn.doClick();
            }
        });

        fileMenu.add(newGraphMenu);
        fileMenu.add(openGraphItem);
        fileMenu.add(saveGraphItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exportGraphAsJPEGItem);
        fileMenu.add(exportGraphAsEPSItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);

        // Menu for Help Options
        JMenu helpMenu = new JMenu("Help");
        //helpMenu.setIcon(null); // I'm using this in a main menu
        //helpMenu.setPreferredSize(new Dimension(40,35)); // Change the size so I can see the text

        JMenuItem aboutUsItem = new JMenuItem("About Us");

        aboutUsItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                final AboutBox about = new AboutBox();
                about.pack();

                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle frame = about.getBounds();
                about.setLocation((screen.width - frame.width)/2, (screen.height - frame.height)/2);
                
                about.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        about.dispose();
                    }
                });

                about.setVisible(true);
            }
            
        });

        helpMenu.add(aboutUsItem);

        JMenu algorithmInformationMenu = new JMenu("Algorithm Information");
        algorithmInformationMenu.setName("Algorithm Information");
        algorithmInformationMenu.setIcon(null);
        algorithmInformationMenu.setPreferredSize(new Dimension(143, 35));

        JMenuItem dfsMenuItem = new JMenuItem("Depth First Search");
        JMenuItem bfsMenuItem = new JMenuItem("Breadth First Search");
        JMenuItem primsMenuItem = new JMenuItem("Prim's Minimum Spanning Tree");
        JMenuItem kruskalsMenuItem = new JMenuItem("Kruskal's Minimum Spanning Tree");
        JMenuItem dijkstrasMenuItem = new JMenuItem("Dijkstra's Shortest Path");
        JMenuItem pruferCodeMenuItem = new JMenuItem("Prufer Code Generation");
        JMenuItem fleurysMenuItem = new JMenuItem("Fleury's Eulerian Path");
        JMenuItem hierholzersMenuItem = new JMenuItem("Hierholzer's Eulerian Path");
        JMenuItem edmondskarpMenuItem = new JMenuItem("Edmonds-Karp Maximum Flow");

        dfsMenuItem.setName("Depth First Search");
        bfsMenuItem.setName("Breadth First Search");
        primsMenuItem.setName("Prim's Minimum Spanning Tree");
        kruskalsMenuItem.setName("Kruskal's Minimum Spanning Tree");
        dijkstrasMenuItem.setName("Dijkstra's Shortest Path");
        pruferCodeMenuItem.setName("Prufer Code Generation");
        fleurysMenuItem.setName("Fleury's Eulerian Path");
        hierholzersMenuItem.setName("Hierholzer's Eulerian Path");
        edmondskarpMenuItem.setName("Edmonds-Karp Maximum Flow");

        dfsMenuItem.addActionListener(fileEventListener);
        bfsMenuItem.addActionListener(fileEventListener);
        primsMenuItem.addActionListener(fileEventListener);
        kruskalsMenuItem.addActionListener(fileEventListener);
        dijkstrasMenuItem.addActionListener(fileEventListener);
        fleurysMenuItem.addActionListener(fileEventListener);
        hierholzersMenuItem.addActionListener(fileEventListener);
        pruferCodeMenuItem.addActionListener(fileEventListener);
        edmondskarpMenuItem.addActionListener(fileEventListener);

        algorithmInformationMenu.add(dfsMenuItem);
        algorithmInformationMenu.add(bfsMenuItem);
        algorithmInformationMenu.add(primsMenuItem);
        algorithmInformationMenu.add(kruskalsMenuItem);
        algorithmInformationMenu.add(dijkstrasMenuItem);
        algorithmInformationMenu.add(pruferCodeMenuItem);
        algorithmInformationMenu.add(fleurysMenuItem);
        algorithmInformationMenu.add(hierholzersMenuItem);
        algorithmInformationMenu.add(edmondskarpMenuItem);

        // The View Menu
        JMenu viewMenu = new JMenu("View");

        JCheckBoxMenuItem vertexLabelsItem = new JCheckBoxMenuItem("Vertex Labels");
        JCheckBoxMenuItem edgeLabelsItem = new JCheckBoxMenuItem("Edge Labels");
        vertexLabelsItem.setSelected(true);
        edgeLabelsItem.setSelected(true);
        
        vertexLabelsItem.addActionListener(fileEventListener);
        edgeLabelsItem.addActionListener(fileEventListener);

        vertexLabelsItem.setEnabled(false);
        edgeLabelsItem.setEnabled(false);

        viewMenu.add(vertexLabelsItem);
        viewMenu.add(edgeLabelsItem);

        // Adding Individual Menus to MenuBar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(algorithmInformationMenu);
        menuBar.add(helpMenu);
        
        // Attaching menubar to frame
        frame.setJMenuBar(menuBar);
    }

    public static void main(String args[]) throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {

        /* Set the look and feel */
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        
        MyGraphVisualizer graphy = new MyGraphVisualizer();
        
        /* Show the splash screen */
        final SplashScreen splash = SplashScreen.getSplashScreen();
        Graphics2D g = splash.createGraphics();
        for(int i=0; i<100; i++) {
            graphy.renderSplashFrame(g, i);
            splash.update();
            Thread.sleep(10);
        }
        splash.close();

        /* Set up the user interface */
        
        graphy.mainWindow = new JFrame("Graphy");

        MyGraphVisualizer.informationPanel = new infoPanel(graphy);
        graphy.mainWindow.getContentPane().add("South", MyGraphVisualizer.informationPanel);

        graphy.graphPanel = new JPanel();
        graphy.graphPanel.setLayout(new BorderLayout());
        graphy.mainWindow.getContentPane().add("Center", graphy.graphPanel);

        graphy.createMenus(graphy.mainWindow, graphy);
        
        graphy.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphy.mainWindow.setSize(1000, 600);
        graphy.mainWindow.setLocationRelativeTo(null);
        graphy.mainWindow.setVisible(true);
        
    }


}

/* The info panel at the bottom of the window, and all its components */

class infoPanel extends JPanel {

    public infoTextArea info;
    public graphButtonPanel graphButtons;
    
    public infoPanel(MyGraphVisualizer graphy) {

        info = new infoTextArea(graphy);
        graphButtons = new graphButtonPanel(graphy);
        JScrollPane scrollpane = new JScrollPane(info,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.setLayout(new BorderLayout(10, 10));
        this.add("North", graphButtons);
        this.add("Center", scrollpane);

    }

}

class infoTextArea extends JTextArea {

    final String initialInformation = "Graphy v1.0 Algorithm Visualizer\n\n" +
                                        "A project under Dr. Tarkeshwar Singh, by:\n" +
                                        "Emaad Ahmed Manzoor\n" +
                                        "Rijul Jain\n";
    MyGraphVisualizer graphy;
    
    public infoTextArea(MyGraphVisualizer graphy) {
        super(7, 2);
        this.graphy = graphy;
        this.setEditable(false);
        this.setBackground(Color.black);
        this.setForeground(Color.gray);
        this.setText(initialInformation);
    }

    @Override
    public void append (String text) {
        String oldText = this.getText();
        String textToAppend = oldText + "\n" + text;
        this.setText(textToAppend);
    }

}

class graphButtonPanel extends JPanel {

    public graphFileButtonPanel graphFileButtons;
    public graphAlgorithmButtonPanel graphAlgoButtons;
    
    public graphButtonPanel(MyGraphVisualizer graphy) {

        graphFileButtons = new graphFileButtonPanel(graphy);
        graphAlgoButtons = new graphAlgorithmButtonPanel(graphy);

        this.setLayout(new BorderLayout());
        this.add("West", graphFileButtons);
        this.add("East", graphAlgoButtons);
        
    }

}

class graphFileButtonPanel extends JPanel {  
    
    JButton saveBtn = new JButton("Save Graph");
    JButton clearBtn = new JButton("Clear Graph");
    JButton closeBtn = new JButton("Close Graph");

    MyGraphVisualizer graphy;
            
    public graphFileButtonPanel(final MyGraphVisualizer graphy) {

        this.graphy = graphy;

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        this.add(clearBtn);
        this.add(saveBtn);
        this.add(closeBtn);
        
        clearBtn.addActionListener(new ActionListener() {

            /* IMPORTANT: If you use a for-each loop to iterate over the getVertices() collection
             * and then delete each vertex, it leads to a ConcurrentModificationException; you can't modify
             * the collection you are iterating over. So you need to iterate over a different collection
             * you need to modify
             */
            
            public void actionPerformed(ActionEvent ae) {

                Graph<MyVertex, MyEdge> g = graphy.vv.getGraphLayout().getGraph();

                Collection<MyVertex> vertices = new LinkedList<MyVertex>();
                Collection<MyEdge> edges = new LinkedList<MyEdge>();

                vertices.addAll(g.getVertices());
                edges.addAll(g.getEdges());
                
                for (MyVertex v : vertices)
                    g.removeVertex(v);
                for (MyEdge e : edges)
                    g.removeEdge(e);

                graphy.vv.repaint();

                GraphElements.MyVertexFactory.sourceVertex = null;
                GraphElements.MyVertexFactory.sinkVertex = null;
                GraphElements.MyVertexFactory.resetNodecount();
                GraphElements.MyEdgeFactory.resetLinkcount();
                
                MyGraphVisualizer.informationPanel.info.append("Cleared the Graph Canvas");
                graphy.vv.requestFocusInWindow();
            }
            
        });

        closeBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                graphy.vv = null;
                MyGraphVisualizer.currentAlgorithmVisualizer = null;
                graphy.layout = null;
                
                for (Component c : graphy.graphPanel.getComponents())
                    graphy.graphPanel.remove(c);

                graphy.mainWindow.getJMenuBar().getMenu(0).getItem(0).setEnabled(true);     /* New Graph */
                graphy.mainWindow.getJMenuBar().getMenu(0).getItem(1).setEnabled(true);     /* Load Graph */
                graphy.mainWindow.getJMenuBar().getMenu(0).getItem(2).setEnabled(false);    /* Save Graph */
                graphy.mainWindow.getJMenuBar().getMenu(0).getItem(4).setEnabled(false);    /* Export Graph As JPEG */
                graphy.mainWindow.getJMenuBar().getMenu(0).getItem(5).setEnabled(false);    /* Export Graph As EPS */
                graphy.mainWindow.getJMenuBar().getMenu(1).getItem(0).setEnabled(false);    /* View Vertex Labels */
                graphy.mainWindow.getJMenuBar().getMenu(1).getItem(1).setEnabled(false);    /* View Edge Labels */
                graphy.mainWindow.getJMenuBar().remove(graphy.mainWindow.getJMenuBar().getMenu(graphy.mainWindow.getJMenuBar().getMenuCount() - 2));
                graphy.mainWindow.getJMenuBar().repaint();
                graphy.mainWindow.getJMenuBar().revalidate();
                
                MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.disableButtonPanel();
                MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.disableButtonPanel();

                GraphElements.MyVertexFactory.sourceVertex = null;
                GraphElements.MyVertexFactory.sinkVertex = null;
                GraphElements.MyVertexFactory.resetNodecount();
                GraphElements.MyEdgeFactory.resetLinkcount();
                GraphElements.MyEdgeFactory.weighted = false;
                
                graphy.graphPanel.repaint();
                
                MyGraphVisualizer.informationPanel.info.append("Closed the current graph.");
            }
            
        });

        saveBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            try {
                    GraphMLWriter<MyVertex, MyEdge> graphWriter = new GraphMLWriter<MyVertex, MyEdge> ();
                    File f = graphy.fileChooser("Save Graph");

                    if (f == null) return;

                    String filename = f.getAbsolutePath();

                    if (!(filename.endsWith(".gph")))
                        filename = filename + ".gph";

                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

                    final Graph g = graphy.vv.getGraphLayout().getGraph();
                    
                    /* Add the X and Y coordinates for the vertices */
                    graphWriter.addVertexData("x", null, "0", new Transformer<MyVertex, String>() {
                        public String transform(MyVertex v) {
                            return Double.toString(graphy.layout.getX(v));
                        }

                    });

                    graphWriter.addVertexData("y", null, "0", new Transformer<MyVertex, String>() {
                        public String transform(MyVertex v) {
                            return Double.toString(graphy.layout.getY(v));
                        }
                    });

                    graphWriter.addEdgeData("weight", null, "0", new Transformer<MyEdge, String>() {
                        public String transform(MyEdge e) {
                            return String.valueOf(e.getWeight());
                        }                        
                    });

                    graphWriter.addEdgeData("capacity", null, "0", new Transformer<MyEdge, String>() {
                        public String transform(MyEdge e) {
                            return String.valueOf(e.getCapacity());
                        }
                    });

                    graphWriter.save(g, out);
                    MyGraphVisualizer.informationPanel.info.append("Graph saved");
                    graphy.vv.requestFocusInWindow();
                    
                } catch (IOException ex) {
                    System.err.println("Exception: "+ex);
                }
            }

        });
        
        this.disableButtonPanel();
        
    }

    public void disableButtonPanel() {
        for (Component c : this.getComponents())
            c.setEnabled(false);
    }

    public void enableButtonPanel() {
        for (Component c : this.getComponents())
            c.setEnabled(true);
    }

}

class graphAlgorithmButtonPanel extends JPanel {

    final String [] algorithms = {"Depth First Search", "Breadth First Search", "Prim's Algorithm", "Kruskal's Algorithm", 
                                    "Dijkstra's Algorithm", "Prufer Code Generation", "Fleury's Algorithm",
                                    "Hierholzer's Algorithm", "Edmonds-Karp Algorithm", "Preorder Traversal", "Inorder Traversal",
                                    "Postorder Traversal", "Maximum Degree First Coloring"};

    JButton stepBtn = new JButton("Step Through");
    JButton resetBtn = new JButton("Reset Algorithm");
    JButton autoBtn = new JButton("Auto Run");

    public JComboBox algoChooser = new JComboBox(algorithms);
    private final Timer timer;
            
    public graphAlgorithmButtonPanel(final MyGraphVisualizer graphy) {
        
        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        algoChooser.setSelectedItem(null);
        algoChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                String algorithm = (String) algoChooser.getSelectedItem();
                try {
                    if (algorithm.equals("Depth First Search")) {
                        if ( GraphElements.MyEdgeFactory.weighted == true ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new DFSVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Depth First Search visualizer");
                    } else if (algorithm.equals("Breadth First Search")) {
                        if ( GraphElements.MyEdgeFactory.weighted == true ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new BFSVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Breadth First Search visualizer");
                    } else if (algorithm.equals("Edmonds-Karp Algorithm")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() != EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == false) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires a directed weighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new EdmondsKarpVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned an Edmonds-Karp visualizer");
                    } else if (algorithm.equals("Kruskal's Algorithm")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == false) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected weighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new KruskalsVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Kruskals visualizer");
                    } else if (algorithm.equals("Prim's Algorithm")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == false) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected weighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new PrimsVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Prims visualizer");
                    } else if (algorithm.equals("Prufer Code Generation")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new PruferVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Prufer Code visualizer");
                    } else if (algorithm.equals("Fleury's Algorithm")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new FleurysVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Fleury's visualizer");
                    } else if (algorithm.equals("Hierholzer's Algorithm")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new HierholzersVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Hierholzer's visualizer");
                    } else if (algorithm.equals("Dijkstra's Algorithm")) {
                        if ( GraphElements.MyEdgeFactory.weighted == false ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires a weighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new DijkstrasVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Dijkstra's Algorithm visualizer");
                    } else if (algorithm.equals("Preorder Traversal")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new PreorderTraversalVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Preorder Traversal Visualizer");
                    } else if (algorithm.equals("Inorder Traversal")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new InorderTraversalVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned an Inorder Traversal Visualizer");
                    } else if (algorithm.equals("Postorder Traversal")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new PostorderTraversalVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Postorder Traversal Visualizer");
                    } else if (algorithm.equals("Maximum Degree First Coloring")) {
                        if ( (graphy.vv.getGraphLayout().getGraph().getDefaultEdgeType() == EdgeType.DIRECTED) || (GraphElements.MyEdgeFactory.weighted == true) ) {
                            MyGraphVisualizer.informationPanel.info.append("This algorithm requires an undirected unweighted graph");
                            MyGraphVisualizer.informationPanel.graphButtons.graphAlgoButtons.algoChooser.setSelectedItem(null);
                            MyGraphVisualizer.currentAlgorithmVisualizer = null;
                            return;
                        }
                        MyGraphVisualizer.currentAlgorithmVisualizer = new MaximumDegreeFirstColoringVisualizer(graphy);
                        MyGraphVisualizer.informationPanel.info.append("Spawned a Maximum Degree First Coloring Visualizer");
                    } else {
                        MyGraphVisualizer.informationPanel.info.append("An unknown error occurred while selecting the algorithm");
                    }
                } catch (NullPointerException ex) {
                    
                }
            }
            
        });

        stepBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (MyGraphVisualizer.currentAlgorithmVisualizer == null) {
                    MyGraphVisualizer.informationPanel.info.append("No algorithm selected to step through");
                    return;
                } else {
                    MyGraphVisualizer.currentAlgorithmVisualizer.step();
                }
            }
            
        });

        resetBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (MyGraphVisualizer.currentAlgorithmVisualizer == null) {
                    MyGraphVisualizer.informationPanel.info.append("No algorithm selected to reset");
                    return;
                } else {
                    MyGraphVisualizer.currentAlgorithmVisualizer.reset();
                    MyGraphVisualizer.informationPanel.info.append("Reset current algorithm");
                }
            }
            
        });

        timer  = new Timer (750, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!(MyGraphVisualizer.informationPanel.graphButtons.graphFileButtons.clearBtn.isEnabled()))
                    stepBtn.doClick();
                else
                    timer.stop();
            }
        });
        
        autoBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                stepBtn.doClick();
                timer.start();
            }           
        });

        this.add(stepBtn);
        this.add(autoBtn);
        this.add(algoChooser);
        this.add(resetBtn);
        
        this.disableButtonPanel();

    }

    public void disableButtonPanel() {
        for (Component c : this.getComponents())
            c.setEnabled(false);
    }

    public void enableButtonPanel() {
        for (Component c : this.getComponents())
            c.setEnabled(true);
    }
}

/* End the info panel */

/* The about box */

class AboutBox extends JWindow {
    private JPanel imagePanel;
    BufferedImage image;
    Dimension dim = new Dimension();

    public AboutBox() {

        //String path = "../images/about.jpg";
        URL url = this.getClass().getResource("/images/about.jpg");
        
        try {
            image = ImageIO.read(url);
        } catch (IOException ex) {
           
        }

        dim.height = image.getHeight();
        dim.width = image.getWidth();
        
        imagePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };

        imagePanel.setPreferredSize(dim);
        this.add(imagePanel);

    }
}