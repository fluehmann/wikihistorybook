package wikiBook2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseWindowListener extends WindowAdapter {

	WikiBookContainer wikibook;
	
    public CloseWindowListener(WikiBookContainer wikibook) {
    	super();
    	this.wikibook = wikibook;
    }

    public void windowClosing(WindowEvent e) {
        wikibook.getDB().closeConnection();
    }
}