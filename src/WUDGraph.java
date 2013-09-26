import java.util.Comparator;
import java.util.Iterator;

public class WUDGraph<E>
{
    private static class Node
    {
        // Index of a neighbour
        public int neighbourIndex;
        // Weight of the edge
        public int edgeWeight;
        // Next node
        public Node nextNode;

        public Node (int neighbourIndex, int edgeWeight)
        {
            this.neighbourIndex = neighbourIndex;
            this.edgeWeight = edgeWeight;
            this.nextNode = null;
        }
    }
    
    // Default capacity of the graph
    public static final int DEFAULT_CAPACITY = 100;

    // Value used to enlargen the graph
    public static final int    ENLARGE_VALUE = 25;  // 25%

    // The vertices of the graph
    private E[] vertices;

    // Sequences of neighbours
    private Node[] adjacencySequences;

    // Last index of the graph
    private int lastIndex = -1;

    public WUDGraph()
    {
        vertices = (E[]) new Object[DEFAULT_CAPACITY];
        adjacencySequences = new Node[DEFAULT_CAPACITY];
    }

    public WUDGraph(int initialCapacity)
    {
        vertices = (E[]) new Object[initialCapacity];
        adjacencySequences = new Node[initialCapacity];
    }

    public WUDGraph (E[] vertices)
    {
        this.vertices = (E[]) new Object[vertices.length];
        
        for (int index = 0; index < vertices.length; index++)
            this.vertices[index] = vertices[index];
        
        adjacencySequences = new Node[vertices.length];
        lastIndex = vertices.length - 1;
    }
    
    // Checks if the graph is empty
    public boolean isEmpty ()
    {
        return lastIndex == -1;
    }

    // Returns the amount of vertices in the graph
    public int size ()
    {
        return lastIndex + 1;
    }

    // Enlargens the capacity of the graph
    protected void enlarge ()
    {
        // The new capacity of the graph
        int newLength = 1 + vertices.length + ENLARGE_VALUE * vertices.length / 100;

        E[] newVertices = (E[]) new Object[newLength];
        Node[] newAdjacencySequences = new Node[newLength];
        
        for (int index = 0; index <= lastIndex; index++)
        {
            newVertices[index] = vertices[index];
            vertices[index] = null;
            newAdjacencySequences[index] = adjacencySequences[index];
            adjacencySequences[index] = null;
        }

        vertices = newVertices;
        adjacencySequences = newAdjacencySequences;
    }

    // Returns the index of the given vertex, or -1 if the vertex is not found
    protected int indexOf (E vertex)
    {
        int indexOfVertex = -1;
        for (int index = 0; index <= lastIndex; index++)
        {
            if (vertex.equals(vertices[index]))
            {
                indexOfVertex = index;
                break;
            }
        }

        return indexOfVertex;
    }

    // Checks if the graph contains the given vertex
    public boolean containsVertex (E vertex)
    {
        return this.indexOf(vertex) != -1;
    }
    
    // Adds a vertex as long as the same vertex does not already exist in the graph
    public void addVertex (E vertex)
    {
        if (!this.containsVertex (vertex))
        {
            // Enlargen the graph if needed
            if (lastIndex == vertices.length - 1)
                this.enlarge ();

            lastIndex = lastIndex + 1;
            vertices[lastIndex] = vertex;
        }
    }

    // Returns a view of all the vertices in the graph
    public E[] verticesView ()
    {
        E[] allVertices = (E[]) new Object[lastIndex + 1];
        for (int index = 0; index < allVertices.length; index++)
            allVertices[index] = this.vertices[index];

        return allVertices;
    }


    // Returns the neighbours of the given vertex
    public E[] getNeighbours (E vertex) throws IllegalArgumentException
    {
        int index = this.indexOf (vertex);

        // Check if the vertex exists in the graph
        if (index < 0)
            throw new IllegalArgumentException (vertex + " was not found!");


        Node node = this.adjacencySequences[index];
        int countNeighbours = 0;
        while(node != null)
        {
            countNeighbours++;
            node = node.nextNode;
        }

        E[] neighbours = (E[]) new Object[countNeighbours];
        node = this.adjacencySequences[index];
        int neighbourIndex = 0;
        
        while(node != null)
        {
            neighbours[neighbourIndex++] = vertices[node.neighbourIndex];
            node = node.nextNode;
        }

        return neighbours;
    }


    // Checks if an edge exists between the given vertices 
    public boolean hasEdge (E vertex1, E vertex2) throws IllegalArgumentException
    {
        int index1 = this.indexOf (vertex1);
        if (index1 < 0)
            throw new IllegalArgumentException (vertex1 + " was not found!");
        int index2 = this.indexOf (vertex2);
        if (index2 < 0)
            throw new IllegalArgumentException (vertex2 + " was not found!");
      
        Node node = adjacencySequences[index1];
        boolean hasEdge = false;
        while (!hasEdge && node != null)
        {
            if (node.neighbourIndex == index2)
                hasEdge = true;
            else
                node = node.nextNode;
        }

        return hasEdge;
    }


    // Returns the weight of the edge between the given vertices, if one exists.
    public int edgeWeight (E vertex1, E vertex2) throws IllegalArgumentException
    {
        int index1 = this.indexOf (vertex1);
        if (index1 < 0)
            throw new IllegalArgumentException (vertex1 + " was not found!");
        int index2 = this.indexOf (vertex2);
        if (index2 < 0)
            throw new IllegalArgumentException (vertex2 + " was not found!");

        Node node = adjacencySequences[index1];
        int edgeWeight = -1;
        while(node != null)
        {
            if (node.neighbourIndex == index2)
            {
                edgeWeight = node.edgeWeight;
                break;
            }
            else
                node = node.nextNode;
        }

        return edgeWeight;
    }


    // Adds the given node to the sequence of the given index, basically adding a neighbour.
    protected void addNode (Node node, int index)
    {
        Node currentNode = adjacencySequences[index];
        if (currentNode == null)
            adjacencySequences[index] = node;
        else
        {
            Node previousNode = null;
            while (currentNode != null && currentNode.edgeWeight < node.edgeWeight)
            {
                previousNode = currentNode;
                currentNode = currentNode.nextNode;
            }

            if (previousNode == null)
                adjacencySequences[index] = node;
            else
                previousNode.nextNode = node;
    
            node.nextNode = currentNode;
        }
    }


    // Removes a node containing the second parameter 
    //from the sequence decided by the first paramater
    protected void removeNode (int seq, int neighbourIndex)
    {
        Node currentNode = adjacencySequences[seq];
        Node previousNode = null;
        while (currentNode != null && currentNode.neighbourIndex != neighbourIndex)
        {
            previousNode = currentNode;
            currentNode = currentNode.nextNode;
        }

        if (currentNode != null)
        {
            if (previousNode != null)
                previousNode.nextNode = currentNode.nextNode;
            else
                adjacencySequences[seq] = currentNode.nextNode;
        }
    }

    // Adds and edge between the given vertices with the given weight
    public void addEdge (E vertex1, E vertex2, int edgeWeight) throws IllegalArgumentException
    {
        if(vertex1 == vertex2)
            return;
        
        int index1 = this.indexOf (vertex1);
        if (index1 < 0)
            throw new IllegalArgumentException (vertex1 + " was not found!");
        
        int index2 = this.indexOf (vertex2);
        if (index2 < 0)
            throw new IllegalArgumentException (vertex2 + " was not found!");


        if (this.hasEdge (vertex1, vertex2))
        {
            this.removeNode (index1, index2);
            this.removeNode (index2, index1);
        }
        
        Node node = new Node(index2, edgeWeight);
        this.addNode(node, index1);
        node = new Node(index1, edgeWeight);
        this.addNode(node, index2);
    }


    // Removes an edge between two vertices
    public void removeEdge (E vertex1, E vertex2) throws IllegalArgumentException
    {
        int index1 = this.indexOf (vertex1);
        if (index1 < 0)
            throw new IllegalArgumentException (vertex1 + " was not found!");
        
        int index2 = this.indexOf (vertex2);
        if (index2 < 0)
            throw new IllegalArgumentException (vertex2 + " was not found!");

	this.removeNode (index1, index2);
        this.removeNode (index2, index1);
    }


    // Removes all the edges of the given vertex
    public void removeEdges (E vertex) throws IllegalArgumentException
    {
        int index = this.indexOf (vertex);
        if (index < 0)
            throw new IllegalArgumentException (vertex + " was not found!");

        Node currentNode = adjacencySequences[index];
        while (currentNode != null)
        {
            this.removeNode (currentNode.neighbourIndex, index);
            currentNode = currentNode.nextNode;
        }

        adjacencySequences[index] = null;
    }

    // Removes the given vertext from the graph
    public void removeVertex (E vertex)
    {
        int indexOfVertex = this.indexOf (vertex);
        if (indexOfVertex != -1)
        {
            this.removeEdges (vertex);
	    for (int index = indexOfVertex + 1; index <= lastIndex; index++)
            {
                vertices[index - 1] = vertices[index];
                adjacencySequences[index - 1] = adjacencySequences[index];
            }
            
            vertices[lastIndex] = null;
            adjacencySequences[lastIndex] = null;
            lastIndex--;
            
            for (int index = 0; index <= lastIndex; index++)
            {
                Node    node = adjacencySequences[index];
                while (node != null)
                {
                    if (node.neighbourIndex > indexOfVertex)
                        node.neighbourIndex--;
                    node = node.nextNode;
                }
            }
	}
    }

    // Clears the graph of vertices and edges
    public void clear ()
    {
        for (int index = 0; index <= lastIndex; index++)
        {
            vertices[index] = null;
            adjacencySequences[index] = null;
        }
        lastIndex = -1;
    }

    public WUDGraph minimumSpandingTree(boolean info)
    {
        boolean activateInfo = info;
        // Local class needed for the algorithm
        class PrimNode implements Comparable
        {
            int vertex,preVertex,SPWeight;
            public PrimNode(int vertex, int preVertex, int SPWeight) 
            {
                this.vertex = vertex;
                this.preVertex = preVertex;
                this.SPWeight = SPWeight;
            }
            
            @Override
            public String toString()
            {
                return "(" + vertices[this.vertex] + ", " + 
                       vertices[this.preVertex] + ", " + this.SPWeight + ")";
            }
            
            @Override
            public int compareTo(Object o)
            {
                PrimNode pn = (PrimNode) o;
                if(this.SPWeight == pn.SPWeight)
                    return 0;
                else if(this.SPWeight < pn.SPWeight)
                    return -1;
                else
                    return 1;
            }
        }
        
        PrimNode npn = null;
        WUDGraph mst = new WUDGraph();
        BSTSet<PrimNode> niv = new BSTSet();
        int[] wfiv = new int[size()];
        
        E vertex = vertices[0];
        mst.addVertex(vertex);
        
        // Update wfiv
        for(E v : vertices)
        {
            if(v != null)
            {
                if(this.hasEdge(vertex, v))
                    wfiv[indexOf(v)] = this.edgeWeight(vertex, v);
                else
                    // No Path
                    wfiv[indexOf(v)] = -1;
            }
        }
        // Path from a vertex to itself is 0
        wfiv[0] = 0;
        
        for(int i = 0; i < size(); i++)
        {
            // Initialize NIV
            if(vertices[i] != null && vertices[i] != vertex)
                niv.add(new PrimNode(i, 0, (hasEdge(vertex,vertices[i])) ? 
                        this.edgeWeight(vertex, vertices[i]) : -1));
        }
        
        // Print Information
        if(activateInfo)
        {
            System.out.println("Initial phase: ");
            System.out.println("Resultgraph: " + mst);
            System.out.print("WFIV: ");
            for(int i = 0; i < wfiv.length;i++)
            {
                System.out.print(wfiv[i]);
                if(i != wfiv.length - 1)
                    System.out.print(", ");
            }
            System.out.println("\nNIV: " + niv);
        }
        
        int iteration = 0;
        while(!niv.isEmpty())
        {
            if(activateInfo)
                System.out.println("\nIteration: " + ++iteration);
        
            // Pick the next NPN from NIV that has a valid path
            Iterator itr = niv.iterator();
            while(itr.hasNext())
            {
                PrimNode tmp = (PrimNode) itr.next();
                if(tmp.SPWeight != -1)
                {
                    npn = tmp;
                    break;
                }
            }
            // Add the vertex from the chosen NPN to the resultgraph
            mst.addVertex(vertices[npn.vertex]);
            // Add the edge between the two vertices from the chosen NPN to the resultgraph
            mst.addEdge(vertices[npn.preVertex], vertices[npn.vertex], 
                        edgeWeight(vertices[npn.preVertex], vertices[npn.vertex]));
            // Remove the chosen NPN from the NIV set
            niv.remove(npn);
            
            if(activateInfo)
            {
                System.out.println(npn);
                System.out.println(mst);
                System.out.println("NIV: " + niv);
            }
            
            // Update WFIV
            for(E v : vertices)
            {
                if(v != null)
                {
                    if(this.hasEdge(vertices[npn.vertex], v))
                        wfiv[indexOf(v)] = this.edgeWeight(vertices[npn.vertex], v);
                    else
                        wfiv[indexOf(v)] = -1;
                }
            }
            wfiv[npn.vertex] = 0;
            
            if(activateInfo)
            {
                System.out.print("WFIV: ");
                for(int i = 0; i < wfiv.length;i++)
                {
                    System.out.print(wfiv[i]);
                    if(i != wfiv.length - 1)
                        System.out.print(", ");
                }
            }
            
            // Update NIV
            for(int i = 0; i < wfiv.length; i++)
            {
                // Look through WFIV for valid paths
                if(wfiv[i] != -1)
                {
                    for(PrimNode pn : niv)
                    {
                        // Update the node containing the vertex
                        // corresponding to the current index in WFIV
                        if(pn.vertex == i)
                        {
                            if(pn.vertex != npn.vertex)
                            {
                                // Since a path was now found, the previous vertex 
                                // is now the vertex of the chosen NPN
                                pn.preVertex = npn.vertex;
                                // Update the path from the original vertex to the current vertex
                                pn.SPWeight = wfiv[i];
                            }
                        }
                    }
                }
            }
            // Balance the tree in order to pick NPN in a sorted manner
            if(!niv.isEmpty())
                niv.balance();
            if(activateInfo)
                System.out.println("\nNiv: " + niv);
        }
        
        return mst;
    }
    
    @Override
    public String toString()
    {
        String verticesString = "Vertices: {";
        for (int index = 0; index < lastIndex; index++)
            verticesString += vertices[index] + ", ";
        if (lastIndex >= 0)
            verticesString += vertices[lastIndex];
        verticesString += "}";
        String    edgesString = "{";
        int    counter = 0; 
        for (int index = 0; index <= lastIndex; index++)
        {
            Node    node = adjacencySequences[index];
            while (node != null)
            {
                Comparable c1 = (Comparable) vertices[index];
                Comparable c2 = (Comparable) vertices[node.neighbourIndex];
                if(c1.compareTo(c2) <= 0)
                {
                    if (counter > 0  &&  counter % 5 == 0)
                        edgesString += "\n   ";
                    edgesString += "(" + vertices[index] + ", " + 
                                   vertices[node.neighbourIndex] + ", " + 
                                   node.edgeWeight + "), ";
                    counter++;
                }
                node = node.nextNode;
            }   
        }
        if (edgesString.length () > 1)
            edgesString = edgesString.substring (0,edgesString.length () - 2);
        String string = verticesString + ", Edges: " + edgesString + "}";
        return string;
    }
}