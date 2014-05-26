package testClasses;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

public class NZClass extends JFrame implements ActionListener{
	
	private JList list;
	private DefaultListModel listModel;
	private JButton select;
	private JPanel panCatList;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Tüdelü");
		NZClass nz = new NZClass();
		
	}
	
	public NZClass(){
		//1. Create the frame.
		JFrame frame = new JFrame("FrameDemo");

		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panCatList = new JPanel();
		
		BoxLayout boxLayout = new BoxLayout(panCatList, BoxLayout.Y_AXIS);
		panCatList.setLayout(boxLayout);
		
		listModel = new DefaultListModel();
		
		
		// testElemente
		listModel.addElement("Jane Doe");
		listModel.addElement("Ivan Smith");
		listModel.addElement("Kathy Green");
		listModel.addElement("Avril");
		listModel.addElement("Bunny");
		listModel.addElement("Christy");
		listModel.addElement("Doorli");
		listModel.addElement("Eva");
		listModel.addElement("Fränzi");
		listModel.addElement("Gabi");
		listModel.addElement("Hanny");
		
		
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(200, 500));

		panCatList.add(listScroller);
		
		select = new JButton("select");
		select.addActionListener(this);
		panCatList.add(select);

		frame.add(panCatList);
		
		
		
		
		//4. Size the frame.
		frame.pack();

		//5. Show it.
		frame.setVisible(true);
	}
	
	public void valueChanged(ListSelectionEvent e) {
	    if (e.getValueIsAdjusting() == false) {

	        if (list.getSelectedIndex() == -1) {
	        //No selection, disable fire button.
	            select.setEnabled(false);

	        } else {
	        //Selection, enable the fire button.
	            select.setEnabled(true);
	        }
	    }
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == this.select){
            for(int Object: list.getSelectedIndices()){
            	System.out.print(Object + " ");
            }
            System.out.println();
            for(Object Object: list.getSelectedValuesList()){
            	System.out.print(Object.toString() + " ");
            }
        }
	}

}
