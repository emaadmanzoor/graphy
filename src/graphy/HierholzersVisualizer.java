/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphy;

import graphy.GraphElements.MyVertex;
import graphy.GraphElements.MyVertexFactory;
import java.util.LinkedList;
import java.util.Iterator;

public class HierholzersVisualizer extends AlgorithmVisualizer {

        private class vMark {
        private MyVertex v;
        private int pos;

        vMark (MyVertex v , int pos){
            this.v = v;
            this.pos = pos;
        }

        public MyVertex getvertex(){
            return this.v;
        }

        public int getpos(){
            return this.pos;
        }

        public String toString(){
            return "( " + v.getName() + "," + pos + " )";
        }
    }

    public class flQ
    {
        private LinkedList vMain,vBackup;
        private LinkedList [] adjList;
        private int edgeCount;

        public flQ() {
            vMain = new LinkedList();
            vBackup = new LinkedList();
            adjList = new LinkedList[graph.getVertexCount()];
            for(MyVertex v : graph.getVertices())
            {
                LinkedList temp = new LinkedList();
                for(MyVertex r : graph.getNeighbors(v)){
                    temp.add(r);
                }
                adjList[v.getIndex()] = temp;
            }
            edgeCount = -1;

        }

        public void addQ(MyVertex v, int c){
            vMark temp = new vMark(v, c);
            // Modify the Adjacency Lists of both the vertices
            if(!vMain.isEmpty())
            {
                vMark prev = (vMark) vMain.peekLast();
                adjListDelete(v, prev.getvertex());
                adjListDelete(prev.getvertex(),v);
            }
            vMain.add(temp); // Add to Main Queue
            // Add the possible  adjacent Vertices to the Backup (Possible Paths ) Queue
            LinkedList tempAdjList = adjList[v.getIndex()];
            Iterator i = tempAdjList.iterator();
            while(i.hasNext())
            {
                MyVertex tempV = (MyVertex) i.next();
                vMark temp2 = new vMark(tempV, c);
                vBackup.add(temp2);
            }
            edgeCount++; // Increase Edge Count

        }

        public void removeQ(int c){
            vMark temp;
            temp = (vMark) vMain.peekLast();
            while(temp.getpos() > c)
            {
                temp = (vMark) vMain.removeLast();
                vMark temp2 = (vMark) vMain.peekLast();
                adjListAdd(temp.getvertex(), temp2.getvertex());
                adjListAdd(temp2.getvertex(), temp.getvertex());
                temp = temp2;
                edgeCount--;

            }

        }

        public LinkedList getadjList(MyVertex v){
            return adjList[v.getIndex()];
        }

        private void adjListDelete(MyVertex v1,MyVertex v2){
            LinkedList temp = adjList[v1.getIndex()];
            temp.remove(v2);
        }

        private void adjListAdd(MyVertex v1,MyVertex v2){
            LinkedList temp = adjList[v1.getIndex()];
            temp.add(v2);
        }

        public LinkedList getpath(){
            LinkedList vPath = new LinkedList();
            Iterator i = vMain.iterator();
            while(i.hasNext()){
                vMark temp = (vMark) i.next();
                MyVertex v = temp.getvertex();
                vPath.add(v);
            }
            return vPath;
        }

        public void printPath(){
            String S = new String();
            Iterator i = vMain.iterator();
            while(i.hasNext()){
                vMark temp = (vMark) i.next();
                S = S + temp.getvertex().getName() + " , ";
            }
            MyGraphVisualizer.informationPanel.info.append(" Path = " + S);
        }

        public Object getnextvertex(){
            return vBackup.removeLast();

        }

        public int getcount(){
            return this.edgeCount;
        }

        public int getPrevAdjCount(){
            vMark temp = (vMark ) vMain.peekLast();
            LinkedList tempAdjList = adjList[temp.getvertex().getIndex()];
            return tempAdjList.size();
        }

        public void printQ (){
            //MyGraphVisualizer.informationPanel.info.append("vMain : " + vMain);
            //MyGraphVisualizer.informationPanel.info.append("vBackup : " + vBackup);
        }
    }

    private MyVertex [] vertexArray; /* Array of Vertex Objects */
    public LinkedList vPath;
    public flQ flry;
    public int count;
    public String coveredPath;

    public class hlQ
    {
       private LinkedList cycles , cycleOrder;

       private class vOrder {
           public MyVertex v;
           private int index;
           public LinkedList vQ;

            public vOrder(MyVertex v, int index , LinkedList temp) {
                this.v = v;
                this.index = index;
                vQ = new LinkedList();
                this.vQ = temp;
            }

            public int getindex(){
                return this.index;
            }

       }

       hlQ (){
            cycles = new LinkedList();
        }

        public LinkedList getAllCycles(){
            return cycles;
        }

        public void generateCycles(LinkedList path){
            MyVertex [] vList ;
            if (path.size() == 4) // Shortest Possible Cycle
            {
                cycles.add(path);
                return;
            }

            vList = new MyVertex [path.size()];
            path.toArray(vList);
            int i,j,k;
            boolean match = false;

            for(i=0;i<vList.length-2;i++)
            {
                MyVertex start = vList[i];
                match = false;
                for (j=i+1;j<vList.length-1;j++)
                {
                    if (start == vList[j])
                    {
                        match = true ; break;
                    }

                }
                if(match)
                {
                    LinkedList temp = new LinkedList();
                    LinkedList temp2 = new LinkedList();
                    for (k =i ; k<=j ; k++)
                        temp.add(vList[k]);

                    for (k=0;k<i;k++)
                        temp2.add(vList[k]);

                    for (k=j;k<vList.length;k++)
                         temp2.add(vList[k]);

                    generateCycles(temp);
                    generateCycles(temp2);
                    break;
                }

            }
            if(!match)
            {
                cycles.add(path);
            }


        }

        public void orderizeCycles(){

            cycleOrder = new LinkedList();
            vOrder [] vList = new vOrder[cycles.size()];
            Iterator i = cycles.iterator();
            int index = 0,j,k;

            while(i.hasNext()){
                LinkedList temp = (LinkedList) i.next();
                vList[index] = new vOrder((MyVertex) temp.peekFirst(),index,temp);
                index++;
            }
            // Set the first Cycle
            for(j = 0;j<vList.length;j++)
                {
                    if(vList[j].v == MyVertexFactory.sourceVertex)
                    {

                        vOrder temp = vList[j];
                        vList[j] = vList[0];
                        vList[0] = temp;
                        break;
                    }
                }
            // Set Corresponding Other Cycles
            for(j = 0; j<vList.length-1;j++)
            {
                boolean found = false;
                for(k = j+2;k<vList.length;k++)
                {
                    if(vList[j].v == vList[k].v)
                    {
                        found = true;   // Swap
                        vOrder temp = vList[j+1];
                        vList[j+1] = vList[k];
                        vList[k] = temp;
                        break;
                    }
                }
                k=j+2;
                while(!found && k<vList.length)
                {
                    Iterator t = vList[j].vQ.iterator();
                    while(t.hasNext() && !found){
                        MyVertex v = (MyVertex) t.next();
                        if(vList[k].vQ.contains(v))
                        {
                            vOrder temp = vList[j+1];
                            vList[j+1] = vList[k];
                            vList[k] = temp;
                            found = true;
                            break;
                        }
                    }
                    k++;
                }

            }
            for (j=0;j<vList.length;j++)
            {
               cycleOrder.add(vList[j].getindex());
            }


        }
        public void printcycles(){
            MyGraphVisualizer.informationPanel.info.append("Cycles = " + cycles);
        }
        public LinkedList getOrder(){
            return this.cycleOrder;
        }

    }

    public hlQ hier;
    public int cyclesCount;
    public LinkedList allCycles , cycleOrder;

    /* Constructor */
    public HierholzersVisualizer(MyGraphVisualizer graphy) {
        super(graphy);
    }

    @Override
    protected void initialize() {

        if (MyVertexFactory.sourceVertex == null) {
            MyGraphVisualizer.informationPanel.info.append("You'll need to select a source vertex first; choose one by right-clicking on the vertex");
            return;
        }
        
        super.initialize();
        MyGraphVisualizer.informationPanel.info.append("\nInitializing Hierholzer's algorithm:");
        boolean isEulerian = true;

        /* Push vertices into an array in order */
        vertexArray = new GraphElements.MyVertex [graph.getVertexCount()];
        //MyGraphVisualizer.informationPanel.info.append("Number of Vertices = " + graph.getVertexCount());
        //MyGraphVisualizer.informationPanel.info.append("Vertices: " + graph.getVertices());

        for (MyVertex v : graph.getVertices()) {
            vertexArray[v.getIndex()] = v;
            v.setUnvisited();   /* Set all vertices as unvisited */
            if (graph.degree(v)%2!=0)
            {
                MyGraphVisualizer.informationPanel.info.append("Graph Does not Eularian Circuit because "
                        + v.getName() + " has odd degree" );
                isEulerian = false;

            }
        }

        if(isEulerian)
        {
            flry = new flQ();
            count = 0;
            flry.addQ(MyVertexFactory.sourceVertex, count);
            while(flry.getcount() < graph.getEdgeCount())
            {
                vMark temp = (vMark) flry.getnextvertex();
                if (temp.getpos() != count) // The Graph is stuck
                {
                    count = temp.getpos();
                    flry.removeQ(count);

                }
                if (flry.getPrevAdjCount() > 1)  // Only that option is available
                    count++;
                flry.addQ(temp.getvertex(), count);


            }
            vPath = flry.getpath();
            hier = new hlQ();
            hier.generateCycles(vPath);
            allCycles = hier.getAllCycles();
            hier.orderizeCycles();
            cycleOrder = hier.getOrder();
            running = true;

        }
        else {
            terminate();
        }

    }

    @Override
    public void step() {

        if (!running) {
            initialize();
        }

        else
        {
            if(!cycleOrder.isEmpty())
            {
                /* Painting the Selected edges */


                int order = (Integer) cycleOrder.removeFirst();
                LinkedList temp = (LinkedList)allCycles.get(order);
                MyVertex v1 = (MyVertex) temp.peek();
                vv.getRenderContext().getPickedVertexState().pick(v1,true);
                while(!temp.isEmpty())
                {
                    v1 = (MyVertex) temp.pop();
                    MyVertex v2 = (MyVertex) temp.peek();
                    vv.getRenderContext().getPickedVertexState().pick(v2,true);
                    vv.getRenderContext().getPickedEdgeState().pick(graph.findEdge(v1, v2), true);

                }
                vv.repaint();
                return;
            }
            else
            {
                MyGraphVisualizer.informationPanel.info.append("Algorithm Finished");
                MyGraphVisualizer.informationPanel.info.append("The Graph is Eularian");
                running  = false;
                terminate();
            }
          }

        }

    @Override
    public void reset() {
        super.reset();
        running = false;
    }

}
    