package wikiBook;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JProgressBar;

import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import dbTest.DBProvider;

public class GraphFactory extends Thread{
	
	private Graph graph;
	private WikiBook wikibook;
	private DBProvider db;
	private int prg_stat;
	private int cur_year;
	private ArrayList<String> peopleList;
	private ResultSet connections;
	private ResultSet people;	
	private double prg;
	private JProgressBar progressBar;
	
	public GraphFactory(WikiBook wikiBook, int year){
		super();
		this.wikibook = wikiBook;
		db = DBProvider.getInstance();
		cur_year = year;
		db = wikiBook.getDB();
		peopleList = new ArrayList<String>();
		graph = new SingleGraph("Wiki");
		prg = 0;
		progressBar = wikiBook.getPG();
		
	}
	
	public void run() {
		if (progressBar != null) {
			progressBar.setIndeterminate(true);
		}
		people = db.getPeople(cur_year);
		try {
			while (people.next()) {
				try {
					Node n = graph.addNode(people.getString("id"));
					n.addAttribute("label", people.getString("name"));
					n.addAttribute("layout.weight", 0.5);
					peopleList.add(people.getString("id"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			wikibook.setOutputText("Personen: "+peopleList.size());
		} catch (IdAlreadyInUseException f) {
			f.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (progressBar != null) {
			progressBar.setIndeterminate(false);
		}
				
		for (String id : peopleList) {
			prg++;
			connections = db.getConnectionsFrom(id, cur_year);
			try {
				while (connections.next()) {
					try {
						if (peopleList.contains(connections
								.getString("person_to"))) {
							graph.addEdge(
									id + connections.getString("person_to"),
									id, connections.getString("person_to"));
						}
					} catch (ElementNotFoundException e) {
					} catch (EdgeRejectedException e) {
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (prg_stat != (int) (100.0 * (prg / peopleList.size()))) {
				prg_stat = (int) (100.0 * (prg / peopleList.size()));
				if (progressBar != null) {
					progressBar.setValue(prg_stat);
				}
			}
		}

		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.quality");
		 BetweennessCentrality bcb = new BetweennessCentrality();
	        bcb.init(graph);
	        bcb.compute();
	        
	       double maxBt = 0.0001;
	       for (String id : peopleList) {
	    	   double bt = graph.getNode(id).getInDegree();
	    	   if(bt > maxBt){
	    		   maxBt = bt;
	    	   }
	       }
	       
	        for (String id : peopleList) {
	        		
	        	double bt = graph.getNode(id).getInDegree();
	        		double size = bt/maxBt* 20;
	        		double weight = bt/maxBt;
	        		if (weight == 0.0){
	        			weight = 0.0001;
	        		}
	        		if (size < 7){
	        			size = 7;
	        		}
	        		int g = 0;
	        		int r =255-(int)(255-(bt/maxBt*255));
	        		int b = (int)(255-(bt/maxBt*255));
	        		if(r == 0){
	        			g = 255;
	        		}
	        		graph.getNode(id).changeAttribute("ui.style", "fill-color: rgb("+r+","+g+","+b+"); size: "+size+"px;");
	        		graph.getNode(id).changeAttribute("layout.weight", bt/maxBt);
	        } 
	        
		graph.addAttribute(
				"ui.stylesheet",
				"url('./css/graph_style.css')");
		wikibook.showGraph(graph);

	}

}
