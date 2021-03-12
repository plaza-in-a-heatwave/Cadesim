package com.benberi.cadesim.desktop;

import javax.swing.*;
import java.awt.*;
public class SplashScreen extends JWindow {
	
	private static final long serialVersionUID = 1L;
	private Image splashScreen;
	private ImageIcon imageIcon;
	
    public SplashScreen() {
		splashScreen = Toolkit.getDefaultToolkit().getImage("gclogo-splash.png");
	    imageIcon = new ImageIcon(splashScreen);
	    setSize(imageIcon.getIconWidth(),imageIcon.getIconHeight());
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation((screenSize.width-getSize().width)/2,(screenSize.height-getSize().height)/2);
	    setBackground(new Color(0,0,0,1));
	    setVisible(true);
    }

    public void paint(Graphics g) {
    	super.paint(g);
      	g.drawImage(splashScreen, 0, 0, this);
    }
  
}