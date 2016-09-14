package org.york.zx;

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

public class GraphDisplay  extends JApplet {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -6426069474974782055L;
		private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
		mxGraph graph = null;
	    // 
	    @SuppressWarnings("rawtypes")
		private JGraphModelAdapter m_jgAdapter;

	    ZXGraph c;
	    
	    public GraphDisplay(ZXGraph g){
	    	this.c = g;
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
				for(int in = 0; in < c.in; in++){
					Object v = graph.insertVertex(parent, "0-" + in, "Node", xDiv, yDiv * (in + 1), 25, 25);
					vertices.put("0-" + in, v);
				}
				for(int i = 1; i < c.layers + 1; i++){
					for(int j = 0; j < c.layerWidth; j++){
						Object v = graph.insertVertex(parent, i + "-" + j, "", xDiv * (i + 1), yDiv * (j + 1), 25, 25);
						vertices.put(i + "-" + j, v);
					}
				}
				for(int ou = 0; ou < c.ou; ou++){
					Object v = graph.insertVertex(parent, (c.layers + 1) + "-" + ou, "Node", (c.layers + 2) * xDiv, yDiv * (ou + 1), 25, 25);
					vertices.put((c.layers + 1) + "-" + ou, v);
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}

			mxGraphComponent graphComponent = new mxGraphComponent(graph);
			getContentPane().add(graphComponent);
	    }
	    
	    public String getStyle(ZXNode n){
	    	if(n.getOp().getName().equals("Green")){
	    		return "shape=ellipse;fillColor=green";
	    	}
	    	if(n.getOp().getName().equals("Red")){
	    		return "shape=ellipse;fillColor=red";
	    	}
	    	if(n.getOp().getName().equals("Hadamard")){
	    		return "shape=rectangle;fillColor=yellow";
	    	}
	    	return "";
	    }
	    
	    public String getLabel(ZXNode n){
	    	if(n.getOp().getName().equals("Green") || n.getOp().getName().equals("Red")){
	    		return String.valueOf(n.getPhase());
	    	}
	    	if(n.getOp().getName().equals("Hadamard")){
	    		return "H";
	    	}
	    	
	    	return "Undefined";
	    }
	    
	    public void update(ZXGraph g){
			graph.getModel().beginUpdate();
			try
			{ 
				for(int in = 0; in < c.in; in++){
					ZXNode n = g.getInput(in);
			    	graph.getModel().setStyle(vertices.get("0-" + in), getStyle(n));
			    	graph.getModel().setValue(vertices.get("0-" + in), getLabel(n));

		    		Object[] edges = graph.getEdges(vertices.get("0-" + in));
		    		for(Object edge : edges){
		    			graph.getModel().remove(edge);
		    		}
				}
				for(int i = 1; i < c.layers + 1; i++){
					for(int j = 0; j < c.layerWidth; j++){
						ZXNode n = g.getNode(j, i - 1);
				    	graph.getModel().setStyle(vertices.get(i + "-" + j), getStyle(n));
				    	graph.getModel().setValue(vertices.get(i + "-" + j), getLabel(n));
				    	

			    		Object[] edges = graph.getEdges(vertices.get(i + "-" + j));
			    		for(Object edge : edges){
			    			graph.getModel().remove(edge);
			    		}
					}
				}
				for(int ou = 0; ou < c.ou; ou++){
					ZXNode n = g.getOutput(ou);
			    	graph.getModel().setStyle(vertices.get((c.layers + 1) + "-" + ou), getStyle(n));
			    	graph.getModel().setValue(vertices.get((c.layers + 1) + "-" + ou), getLabel(n));

		    		Object[] edges = graph.getEdges(vertices.get((c.layers + 1) + "-" + ou));
		    		for(Object edge : edges){
		    			graph.getModel().remove(edge);
		    		}
				}
				ArrayList<ZXNode> activeNodes = new ArrayList<ZXNode>();
				for(int ou = 0; ou < c.ou; ou++){
					activeNodes.add(g.getOutput(ou));
				}
				
				while(!activeNodes.isEmpty()){
					ArrayList<ZXNode> toDrop = new ArrayList<ZXNode>();
					ArrayList<ZXNode> toAdd = new ArrayList<ZXNode>();
					
					for(ZXNode node : activeNodes){
						for(ZXNodeVertex source : node.inputs){
							if(source != null){
								ZXNode sourceN = g.getNode(source.getX(), source.getY());
								toAdd.add(sourceN);
								String targetID = (node.getY() + 1) + "-" + node.getX();
								String sourceID = (source.getY() + 1) + "-" + source.getX();
								graph.insertEdge(null, "edge", "", vertices.get(sourceID), vertices.get(targetID));
							}
						}
						toDrop.add(node);
					}
					
					for(ZXNode d : toDrop){
						activeNodes.remove(d);
					}
					for(ZXNode d : toAdd){
						activeNodes.add(d);
					}
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}
	    }
	}

