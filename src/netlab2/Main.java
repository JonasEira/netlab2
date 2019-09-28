package netlab2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import netlab2.Configuration.modes;

public class Main {

	public Main() {

	}

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv6Addresses", "true");
		
		initGui();
	}

	static void initGui(){
		GameInterfaceView view = new GameInterfaceView();
		JFrame mainWindow = new JFrame("Jättecoolt spel!");
		mainWindow.setLayout(new BorderLayout());
		mainWindow.add(view, BorderLayout.CENTER);
		mainWindow.setSize(Configuration.windowSize);
		mainWindow.setVisible(true);
		ConnPoint server = new ConnPoint();
		server.setMode(modes.Server);
		server.setName("Server");
		server.addDataWatcher(view);
		server.setLocalPort(5566);
		try {
			server.setRemotePoint(Inet6Address.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.listen();
		ConnPoint client = new ConnPoint();
		client.setName("Client");
		client.setMode(modes.Client);
		client.setRemotePort(5566);
		client.connect();
		JFrame testWindow = new JFrame("Klient som testar");
		testWindow.setSize(new Dimension(300, 200));
		testWindow.setLayout(new FlowLayout());

		JButton knappen = new JButton("Skicka");
		JTextField textruta = new JTextField(10);
		knappen.setActionCommand("skicka");
		knappen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = textruta.getText();
				String[] input = text.split(" ");
				System.out.println("Sendcommand \"" + text + "\"" + input.length);
				int x = Integer.parseInt(input[0]);
				int y = Integer.parseInt(input[1]);
				int d = Integer.parseInt(input[2]);
				client.sendCommand(x, y, d);
			}
		});
		
		WindowListener l2 = new WindowAdapter() {
			@SuppressWarnings("unused")
			public void WindowClosing(WindowEvent e) {
				System.out.println("Closed the main window! Rensar sockar");
				server.close();
				mainWindow.dispose();
			}
		};
		mainWindow.addWindowListener(l2);
		WindowListener l = new WindowAdapter() {
			@SuppressWarnings("unused")
			public void WindowClosing(WindowEvent e) {
				System.out.println("Closed the client window! Rensar sockar");
				client.close();
				testWindow.dispose();
			}
		};
		
		testWindow.addWindowListener(l);
		testWindow.add(textruta);
		testWindow.add(knappen);
		testWindow.setVisible(true);
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				client.sendCommand(i, j, 1);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	
}
