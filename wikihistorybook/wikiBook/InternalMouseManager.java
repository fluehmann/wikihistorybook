package wikiBook;

import java.awt.event.MouseEvent;
import java.util.ArrayList;


import javax.swing.JOptionPane;


import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.util.DefaultMouseManager;

public class InternalMouseManager extends DefaultMouseManager {
	
	GraphicElement ge = null;
	
	public InternalMouseManager(GraphicElement e){
		super();
		this.ge = e;
	}
	
	public InternalMouseManager(){
		super();
	}
	
	public void mousePressed(MouseEvent e){
		ArrayList<GraphicElement> a = view.allNodesOrSpritesIn(e.getX()-1, e.getY()-1, e.getX()+1, e.getY()+1);
		if(!a.isEmpty()){
			JOptionPane.showMessageDialog(view,
					"You click on " + a.get(0).getLabel());
		}
	}
	
	
	public void mouseMoved(MouseEvent e){
		super.mouseMoved(e);
		ArrayList<GraphicElement> a = view.allNodesOrSpritesIn(e.getX()-2, e.getY()-2, e.getX()+2, e.getY()+2);
		if(!a.isEmpty()){
			if(ge!= null){
				ge.removeAttribute("ui.class");
			}
			a.get(0).addAttribute("ui.class", "selected");
			ge = a.get(0);
			
		}else{
			if(ge!= null){
				ge.removeAttribute("ui.class");
			}
		}
		
	}
}



