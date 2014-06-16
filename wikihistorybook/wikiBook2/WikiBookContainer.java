package wikiBook2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.Viewer.ThreadingModel;

public class WikiBookContainer extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JPanel sidePanel;
	private JPanel btnPnl;
	private DBProvider db;
	private Viewer viewer;
	private View view;
    private GraphicElement lastElement;
    private JSlider tLine; 
    private GraphFactory graphFactory;
    private JEditorPane outputText;
    private JTextArea yearText;
    private JPanel controlPanel;
    private int maxNodes;
    private JSpinner spinner;
    private JButton btnZoomIn, btnZoomOut;
    
    private final int SLIDER_MIN = -2000;
	private final int SLIDER_MAX = 2000;
	private final int SLIDER_INIT = 0;
	
	private final int YEAR_INIT = 0;
	
	private Graph graph;
		
	public WikiBookContainer(){
		super("The-Wiki-History-Book");
		
		db = DBProvider.getInstance();
		db.getConnection();
		
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		panel = new JPanel(new java.awt.BorderLayout(1,1));
		sidePanel = new JPanel(new java.awt.GridLayout(0,1));
		sidePanel.setPreferredSize(new Dimension(200,500));
		sidePanel.setBorder(BorderFactory.createMatteBorder(0,2,0,0, Color.LIGHT_GRAY));
		
		outputText = new JEditorPane();
		outputText.setContentType("text/html");
		outputText.setEditable(false);
		outputText.setBorder(new EmptyBorder(10,10,0,0));
		outputText.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		           try {
					Desktop.getDesktop().browse(e.getURL().toURI());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        }
		    }
		});
		
		controlPanel = new JPanel(new java.awt.GridLayout(0,1));
		maxNodes = 1000;
		SpinnerModel yearModel = new SpinnerNumberModel(1000, 1, 2000, 10); 
		spinner = new JSpinner(yearModel);
		spinner.addChangeListener(new ChangeListener() {
		        @Override
		        public void stateChanged(ChangeEvent e) {
		           maxNodes = (int) spinner.getValue();
		        }
		    });
		controlPanel.add(spinner);
		controlPanel.setFocusable(false);
		controlPanel.setBorder(BorderFactory.createTitledBorder("max. Nodes"));
		
		
		sidePanel.setBorder(BorderFactory.createMatteBorder(0,2,0,0, Color.LIGHT_GRAY));
		
		yearText = new JTextArea(3,1);
		yearText.setFocusable(false);
		yearText.setText("Year: "+YEAR_INIT);
		yearText.setBorder(new EmptyBorder(10,10,0,0));
		
		btnPnl = new JPanel();
		btnPnl.setBorder(BorderFactory.createTitledBorder("Zooming"));
		btnZoomIn = new JButton("+");
		btnZoomOut = new JButton("-");
		btnPnl.add(btnZoomIn);
		btnPnl.add(btnZoomOut);
				
		sidePanel.add(yearText);
		sidePanel.add(controlPanel);
		sidePanel.add(btnPnl);
		sidePanel.add(outputText);

		
		
		tLine = new JSlider(JSlider.HORIZONTAL,
				SLIDER_MIN, SLIDER_MAX, SLIDER_INIT);
		tLine.setMajorTickSpacing(1000);
		tLine.setMinorTickSpacing(100);
		tLine.setPaintTicks(true);
		tLine.setPaintLabels(true);
		tLine.addChangeListener(new Timeline(this));
		tLine.setFocusable(false);
		
		graphFactory = new GraphFactory(this, YEAR_INIT);
		graphFactory.start();
		
		btnZoomIn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	double p = view.getCamera().getViewPercent();
		    	view.getCamera().setViewPercent(p - 0.1);		    }
		});
		
		btnZoomOut.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	double p = view.getCamera().getViewPercent();
		    	view.getCamera().setViewPercent(p + 0.1);		    }
		});

		panel.add(sidePanel,BorderLayout.EAST);
		panel.add(tLine,BorderLayout.SOUTH);

		getContentPane().add(panel);
		setSize(1000, 2000);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addWindowListener(new CloseWindowListener(this));
	
	}
	
	public void showGraph(Graph graph){
		this.graph = graph;
		View newView;
 	    viewer = new Viewer(graph, ThreadingModel.GRAPH_IN_SWING_THREAD);
 		viewer.enableAutoLayout();
 		newView = viewer.addDefaultView(false);
 		try{
 			panel.remove(view);
 		}catch(NullPointerException e){
 			
 		}
 		newView.setMouseManager(new InternalMouseManager(lastElement));
 		newView.setShortcutManager(new InternalKeyManager());
 		view = newView;
 		view.setFocusable(true);
 		view.getCamera().setViewPercent(0.7);
 		panel.add(newView, BorderLayout.CENTER);
 		panel.updateUI();
 		view.requestFocusInWindow();
	}
	
	public DBProvider getDB(){
		return db;
	}
	
	public void setOutputText(String output){
		outputText.setText(output);
	}
	
	public void setYearText(String text){
		yearText.setText(text);
	}
	
	public Graph getGraph(){
		return graph;
	}
	
	public int getMaxNodes(){
		return maxNodes;
	}
	
}










