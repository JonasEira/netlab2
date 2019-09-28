package netlab2;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

public class GameInterfaceView extends JPanel implements DataWatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 213181301L;
	byte[][] array;
	private int width;
	private int height;
	Color[] colors = new Color[9];

	public GameInterfaceView() {
		array = new byte[201][201];
		Dimension d = Configuration.windowSize;
		width = d.width;
		height = d.height;
		makeColors();
		testArray();
	}
	private void makeColors(){
		for (int i = 0; i < colors.length; i++) {
			colors[i] = new Color(255/8*i, 255 - 255/8*i, 255/8*i);
		}
	}
	
	private void testArray() {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				array[i][j] = (byte) (Math.random()*8);
			}
		}
	}
	
	@Override
	public void paint(java.awt.Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(colors[0]);
		g.fillRect(0, 0, width, height);
		
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				g.setColor(colors[array[i][j] & 0xFF]);
				g.fillRect(i*width/array.length, j*height/array.length, width/array.length+1, height/array[0].length+1);	
			}
		}
	}
	
	public void notifyWatchers(DataPacket p) {
		int d = p.getType();
		System.out.println("Notified! Packet type=" + d);
		if(d == DataTypes.GRAPHICAL_DATA) {
			System.out.println("Notified!" + d);
			byte[] received = (byte[])p.getData();
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[0].length; j++) {
					array[i][j] = received[(i+1)*j];
				}
			}
			this.repaint();
		}
	}
}
