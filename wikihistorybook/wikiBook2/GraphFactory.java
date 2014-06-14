package wikiBook2;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JProgressBar;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class GraphFactory extends Thread{
	
	private Graph graph;
	private WikiBookContainer wikibook;
	private DBProvider db;
	private int cur_year;
	private ResultSet people;	
	private ResultSet connections;
	private JProgressBar progressBar;
	private double nodeNum;
	private double conNum;
	private int year;
	
	public GraphFactory(WikiBookContainer wikibook, int year){
		super();
		this.wikibook = wikibook;
		db = DBProvider.getInstance();
		cur_year = year;
		db = wikibook.getDB();
		graph = new AdjacencyListGraph("Wiki");
		this.year = year;
	}
	
	public void run() {
		int maxNodes = wikibook.getMaxNodes();
		people = db.getPeople(cur_year, maxNodes);
		wikibook.setYearText("Year:  "+year+"\n"
				+ "Nodes loading...");
		nodeNum = loadNodes();
		wikibook.setYearText("Year:  "+year+"\n"
				+ "Nodes: "+nodeNum+"\n"
				+ "Connections loading...");
	    conNum = loadConnections();
	    wikibook.setYearText("Year:  "+year+"\n"
				+ "Nodes: "+nodeNum+"\n"
				+ "Connections:	"+conNum+"\n");
		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.quality");
		setIndegrees();  
		
		graph.addAttribute(
				"ui.stylesheet",
				"url('graph_style.css')");
		
		wikibook.showGraph(graph);
	}
	
	private int loadNodes(){
		int num = 0;
		try {
			people.afterLast();
			while (people.previous()) {
				num++;
				try {
					Node n = graph.addNode(people.getString("id"));
					n.addAttribute("label", people.getString("name"));
					n.addAttribute("layout.weight", 0.5);
					n.addAttribute("indegree", people.getString("indegree"));
					n.addAttribute("year_from", people.getString("year_from"));
					n.addAttribute("year_to", people.getString("year_to"));

				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IdAlreadyInUseException f) {
					f.printStackTrace();	
				}
			}
		} catch (SQLException e3) {
			e3.printStackTrace();
		}	
		return num;
	}
	
	private double loadConnections(){
		int num = 0;
		if (progressBar != null) {
			progressBar.setIndeterminate(false);
		}
		
		connections = db.getConnections(cur_year, wikibook.getMaxNodes()	);
		try {		
			connections.first();
			while(connections.next()) {
				num++;
				try{
					graph.addEdge(
					connections.getString("person_from") + connections.getString("person_to"),
					connections.getString("person_from"), connections.getString("person_to"));	
				} catch (ElementNotFoundException e){
							
				} catch (EdgeRejectedException e){
						
				}					
			}
				
		} catch (SQLException e) {
		e.printStackTrace();
		}
		
		return num;
	}
	
	private void setIndegrees(){
		try {
			people.first();
			double maxIn = people.getDouble("indegree"); 
			while (people.next()) {
			    double in = people.getDouble("indegree");
			
			 	double size = in/maxIn* 20;
			   	double weight = in/maxIn;
			   	
			    if (weight < 0.01){
			    	weight = 0;
			    }
			    
			    if (size < 5){
			    	size = 5;
			    }
			    
			    int g = 0;
			    int r =255-(int)(255-(in/maxIn*255));
			    int b = (int)(255-(in/maxIn*255));
			   
			    try {
					graph.getNode(people.getString("id")).changeAttribute("ui.style", "fill-color: rgb("+r+","+g+","+b+"); size: "+size+"px;");
					
					graph.getNode(people.getString("id")).changeAttribute("layout.weight", weight);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			    
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

}
