package org.york.ants;

	import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.demo.JGraphAdapterDemo;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

	/**
	 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
	 *
	 * @author Barak Naveh
	 *
	 * @since Aug 3, 2003
	 */

public class RandDisplay  extends JApplet {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -6426069474974782055L;
		private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
		mxGraph graph = null;
	    // 
	    @SuppressWarnings("rawtypes")
		private JGraphModelAdapter m_jgAdapter;

	    RandWalkComp c;
	    
	    public RandDisplay(RandWalkComp c){
	    	this.c = c;
	    }


	    private void adjustDisplaySettings( JGraph jg ) {
	        jg.setPreferredSize( DEFAULT_SIZE );

	        Color  c        = DEFAULT_BG_COLOR;
	        String colorStr = null;

	        try {
	            colorStr = getParameter( "bgcolor" );
	        }
	         catch( Exception e ) {}

	        if( colorStr != null ) {
	            c = Color.decode( colorStr );
	        }

	        jg.setBackground( c );
	    }


	    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

	    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

	    /**
	     * An alternative starting point for this demo, to also allow running this
	     * applet as an application.
	     *
	     * @param args ignored.
	     */
	    public static void main(String [] args)
	    {
	        JGraphAdapterDemo applet = new JGraphAdapterDemo();
	        applet.init();

	        JFrame frame = new JFrame();
	        frame.getContentPane().add(applet);
	        frame.setTitle("JGraphT Adapter to JGraph Demo");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setVisible(true);
	    }

	    /**
	     * {@inheritDoc}
	     */
	    HashMap<String, Object> vertices = new HashMap<String, Object>();
	    public void init()
	    {

			graph = new mxGraph();
			Object parent = graph.getDefaultParent();

			graph.getModel().beginUpdate();
			try
			{
				int xDiv = 50;
				int yDiv = 50;
				for(AntNode node : c.nodes){
					vertices.put(String.valueOf(node.index), graph.insertVertex(null, String.valueOf(node.index), node.getOp().getName(), xDiv * (2 + node.getX()), yDiv * (1 + node.getY()), 25, 25));
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}

			mxGraphComponent graphComponent = new mxGraphComponent(graph);
			getContentPane().add(graphComponent);
	    }
	    
	    public void update(){
			graph.getModel().beginUpdate();
			try
			{
		    	for(AntNode node : c.nodes){
		    		graph.getModel().setStyle(vertices.get(String.valueOf(node.index)), "fillColor=white");
		    		Object[] edges = graph.getEdges(vertices.get(String.valueOf(node.index)));
		    		for(Object edge : edges){
		    			graph.getModel().remove(edge);
		    		}
		    	}
		    	

		    		    	
		    	for(int i = 0; i < c.qubits; i++){
		    		String style = "";
		    		String edgeStyle = "";
		    		if(i == 0){
		    			style = "fillColor=red";
		    			edgeStyle = "strokeColor=red";
		    		}
		    		if(i == 1){
		    			style = "fillColor=blue";
		    			edgeStyle = "strokeColor=blue";
		    		}
		    		if(i == 2){
		    			style = "fillColor=yellow";
		    			edgeStyle = "strokeColor=yellow";
		    		}
		    		for(AntNode node : c.nodes){
				    	for(AntNode node2 : c.nodes){
				    		if(c.edges.get(i)[node.index][node2.index] > QAntOpt.MIN){
				    			Object e = graph.insertEdge(null, "", c.edges.get(i)[node.index][node2.index], vertices.get(String.valueOf(node.index)), vertices.get(String.valueOf(node2.index)), edgeStyle);
				    		}
				    	}
			    	}
		    		ArrayList<AntNode> path = c.bestPath.get(i);
		    		for(AntNode n : path){
			    		graph.getModel().setStyle(vertices.get(String.valueOf(n.index)), style);
		    		}
		    	}
			}
			finally
			{
				graph.getModel().endUpdate();
			}
	    }
	}

