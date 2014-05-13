package wikiBook;


import java.awt.event.KeyEvent;

import org.graphstream.ui.swingViewer.util.DefaultShortcutManager;

public class InternalKeyManager extends DefaultShortcutManager {
	
	
	public InternalKeyManager(){
		super();
	}

	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		double p = view.getCamera().getViewPercent();
		switch (e.getKeyCode()) {

		case KeyEvent.VK_A:
			view.getCamera().setViewPercent(p + 0.1);
			System.out.println("s");
			break;

		case KeyEvent.VK_Q:
			view.getCamera().setViewPercent(p - 0.1);
			break;

		}
	}

}
