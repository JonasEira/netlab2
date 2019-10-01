/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netlab2;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;

import netlab2.Configuration;

/**
 *
 * @author Jonas
 */
public class SendSocketThreadObject implements Runnable {
	private boolean _running = true;
	private boolean _fireOnce;
	private DatagramSocket _s;
	private byte[] _data;
	private ConnPoint _p;
	private InetAddress _sendAddress;
	private int _port;

	public SendSocketThreadObject(ConnPoint connPoint, int _remotePort) {
		_port = _remotePort;
		_p = connPoint;
		
	}

	public void close() {
		_running = false;
	}
	
	public void setPort(int port) {
		_port = port;
	}

	private synchronized byte[] getByteArray() {
		return _data;
	}

	public void sendData(byte[][] data) {
		if ((_p._mode == Configuration.modes.Client && _p._remoteState == ConnPoint.state.opened)
				|| (_p._mode == Configuration.modes.Server && _p._localState == ConnPoint.state.opened)) {
			byte[] tmp = new byte[data.length * data[0].length + 3];
			tmp[0] = (byte) DataTypes.GRAPHICAL_DATA;
			tmp[1] = (byte) ((tmp.length >> 8) & 0xFF );
			tmp[2] = (byte) (tmp.length & 0xFF);
			for (int n = 0; n < data.length; n++) {
				for (int i = 0; i < data.length; i++) {
					tmp[n + 3] = data[n][i];
				}
			}
			_data = tmp.clone();
			System.out.println("Data packed and ready! " + tmp.length + " bytes");
			_fireOnce = true;
		}
	}
	
	public void sendData(byte[][] data, InetAddress _remotePoint, int port) {
		System.out.println("Sending to " + _remotePoint.toString() + " port " + port );
		_port = port;
		_sendAddress = _remotePoint;
		sendData(data);
	}

	@Override
	public void run() {
		try {
			_s = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_p._remoteState = ConnPoint.state.opened;
		while (_running) {
			while (!_fireOnce) {
				try {
					Thread.currentThread().sleep(22);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			try {
				System.out.println("Sending "  + _sendAddress.toString() + " - " + this._port + " Length: " + _data.length);
				System.out.println("Send Length Data1: " + ((_data[1] & 0xFF) << 8));
				System.out.println("Send Length Data2: " + (_data[2] & 0xFF));
				DatagramPacket p = new DatagramPacket(_data, _data.length, _sendAddress, this._port);
				_s.send(p);
				System.out.println("Sent packet Data Length: " + _data.length);
				//write(getByteArray(), 0, getByteArray().length);
			} catch (IOException ex) {
				printl("IOEx - Send command failed");
			}
			_fireOnce = false;
		}
	}

	private static void printl(String string) {
		System.out.println(string);
	}

	
}
