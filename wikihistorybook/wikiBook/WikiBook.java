package wikiBook;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.Viewer.ThreadingModel;

import dbTest.DBProvider;

public class WikiBook extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JProgressBar progressBar;
	private DBProvider db;
	private Viewer viewer;
	private View view;
    private GraphicElement lastElement;
    private JSlider tLine; 
    private GraphFactory graphFactory;
    private JTextArea outputText;
    
    private final int SLIDER_MIN = -2000;
	private final int SLIDER_MAX = 2000;
	private final int SLIDER_INIT = 0;
	
	private final int YEAR_INIT = 500;
		
	public WikiBook(){
		super("The-Wiki-History-Book");
		
		db = DBProvider.getInstance();
		db.getConnection();
		
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		panel = new JPanel(new java.awt.BorderLayout());
		
		outputText = new JTextArea("\n    select year	   ");
		outputText.setFocusable(false);
		outputText.setBackground(Color.LIGHT_GRAY);
		outputText.setPreferredSize(new Dimension(100,50));
		
		tLine = new JSlider(JSlider.HORIZONTAL,
				SLIDER_MIN, SLIDER_MAX, SLIDER_INIT);
		tLine.setMajorTickSpacing(1000);
		tLine.setMinorTickSpacing(100);
		tLine.setPaintTicks(true);
		tLine.setPaintLabels(true);
		tLine.addChangeListener(new SliderListener(this));
		tLine.setFocusable(false);
		
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBar.setFocusable(false);
		
		graphFactory = new GraphFactory(this, YEAR_INIT);
		graphFactory.start();

		panel.add(outputText,BorderLayout.WEST);
		panel.add(tLine,BorderLayout.SOUTH);
		panel.add(progressBar, BorderLayout.NORTH);

		getContentPane().add(panel);
		setSize(1000, 2000);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addWindowListener(new CloseWindowListener(this));
	}
	
	public void showGraph(Graph graph){
		View newView;
 	    viewer = new Viewer(graph, ThreadingModel.GRAPH_IN_SWING_THREAD);
 		viewer.enableAutoLayout();
 		newView = viewer.addDefaultView(false);
 		newView.getCamera().setViewPercent(0.8);
 		
 		try{
 			panel.remove(view);
 		}catch(NullPointerException e){
 			
 		}
 		
 		newView.setMouseManager(new InternalMouseManager(lastElement));
 		newView.setShortcutManager(new InternalKeyManager());
 		
 		view = newView;
 		view.setFocusable(true);
 		panel.add(newView, BorderLayout.CENTER);
 		panel.updateUI();
 		view.requestFocusInWindow();
	}
	
	public DBProvider getDB(){
		return db;
	}
	
	public JProgressBar getPG(){
		return progressBar;
	}
	
	public void setOutputText(String text){
		outputText.setText(text);
	}
	
}










