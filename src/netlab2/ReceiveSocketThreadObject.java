/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netlab2;

import netlab2.ConnPoint.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/////// ------ Thread Class ---------------------------
/**
 *
 * @author Jonas
 */
public class ReceiveSocketThreadObject implements Runnable {
	DatagramSocket _s;
	ConnPoint _conn;
	ArrayList<DataWatcher> watchers;
	private boolean _exitInjected;
	private byte[] _datas;
	private int _localPort;

	public ReceiveSocketThreadObject(ConnPoint aThis, int localport) {
		_localPort = localport;
		_conn = aThis;
		_exitInjected = false;
		watchers = new ArrayList();
	}

	public void setLocalPort(int port) {
		_localPort = port;
	}

	@Override
	public void run() {
		if (_conn.getMode() == _conn._mode.Server) {
			try {
				System.out.println("Server starting..");
				_s = new DatagramSocket(_localPort);
				_s.setSoTimeout(99999);
				System.out.println("Server started..");
				_s.setSendBufferSize(200000);
				_s.setReceiveBufferSize(200000);
			} catch (IOException ex) {
				_exitInjected = true;
				ex.printStackTrace();
				return;
			}

			_datas = new byte[Configuration.packetLength + 3];
			while (!_exitInjected) {
				try {
					System.out.println("Waiting for data..");
					DatagramPacket p = new DatagramPacket(_datas, Configuration.packetLength + 3);
					_s.receive(p);
					_datas = p.getData();
					System.out.println("Received Packet: " + _datas.length);
					handleRead(_datas[0] & 0xFF);
				} catch (IOException ex) {
					printl("IOEx - ReceiveThreadLoop" + ex.getLocalizedMessage());
					_exitInjected = true;
				}
			}
		}
		_s.close();
	}

	public void close() {
		System.out.println("Closing sockets");
		_exitInjected = true;
		_s.close();
	}

	public void addDataWatcher(DataWatcher o) {
		watchers.add(o);
		printl("Added DataWatcher " + o.getClass().getSimpleName());
	}

	void removeDataWatcher(DataWatcher o) {
		watchers.remove(o);
	}

	public void sendToWatchers(Object o, int typeOfData) {
		DataPacket p = new DataPacket();
		p.setData(o);
		p.setType(typeOfData);
		System.out.println("Notifying watchers of datatype: " + typeOfData);
		for (DataWatcher obs : watchers) {
			obs.notifyWatchers(p);
		}
	}

	private void printl(String string) {
		System.out.println(string);
	}

	private void handleRead(int typeOfData) throws IOException {
		if (typeOfData == DataTypes.GRAPHICAL_DATA) {
			System.out.println("Length bytes:" + (_datas[0] & 0xFF) + "_" + (_datas[1] & 0xFF)+ "_" 
												+ (_datas[2] & 0xFF) + "_" + (_datas[3] & 0xFF)+ "_" 
												+ (_datas[4] & 0xFF) + (_datas[5] & 0xFF));
			System.out.println("Receive Length Data1: " + ((_datas[1] & 0xFF) << 8));
			System.out.println("Receive Length Data2: " + (_datas[2] & 0xFF));
			int dataLength =  (((_datas[1] & 0xFF) << 8) + (_datas[2] & 0xFF));
			System.out.println("Handling packet of length:" + dataLength);
			byte[] updateData = new byte[dataLength];
			if (dataLength + 3 < _datas.length) {
				throw new IOException("FÖRBANNAT!"); // Citat Gustav Nilsson, 1933 - 2007, Civ.
																			// Ing. Maskinteknik och Världens bästa
																			// lärare.
			}
			for (int i = 3; i < dataLength; i++) {
				updateData[i] = _datas[i];
			}
			sendToWatchers(updateData, typeOfData);
		}
	}
}
