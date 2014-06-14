package wikiBook2;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Timeline implements ChangeListener {

	private WikiBookContainer wikiBookContainer;
	GraphFactory factory;

	public Timeline(WikiBookContainer wikiBookContainer) {
		this.wikiBookContainer = wikiBookContainer;
	}

	public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            int time = (int)source.getValue();
            if(factory!= null && factory.isAlive()){
            	try {
					factory.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            factory = new GraphFactory(wikiBookContainer, time);
            factory.start(); 
        }    
    }
}