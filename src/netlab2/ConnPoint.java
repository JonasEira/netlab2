package netlab2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Jonas
 */
public class ConnPoint {
	private InetAddress _remotePoint;
	private InetAddress _localPoint;
	private String _name;
	private ArrayList<DataWatcher> _watchers;

	DataInputStream _input;
	DatagramSocket _server;
	DatagramSocket _clientSock;
	ReceiveSocketThreadObject _rsThreadObject;
	SendSocketThreadObject _ssThreadObject;
	Configuration.modes _mode;
	int _remotePort, _localPort;

	state _localState, _remoteState;
	private byte[][] _datas;

	private void printl(String string) {
		System.out.println(string);
	}

	public ReceiveSocketThreadObject getThreadObject() {
		return this._rsThreadObject;
	}

	public state getRemoteState() {
		return this._remoteState;
	}

	public static enum state {
		closed, waiting, opened, listening
	}

	public void setMode(Configuration.modes mode) {
		_mode = mode;
	}

	public Configuration.modes getMode() {
		return _mode;
	}

	public ConnPoint() {
		try {
			_localPoint = InetAddress.getByName("localhost");
			_remotePoint = InetAddress.getByName("localhost");
			_localState = state.closed;
			_remoteState = state.closed;
			System.out.println("Adress: " + _localPoint.toString());
			_watchers = new ArrayList<>();
			_datas = new byte[201][];
			for (int i = 0; i < _datas.length; i++) {
				_datas[i] = new byte[201];
			}
			populateData();
		} catch (IOException ex) {
			System.err.println("ServerSocket error: \n" + ex.toString());
		}
	}

	private void populateData() {
		for (int i = 0; i < _datas.length; i++) {
			for (int j = 0; j < _datas[0].length; j++) {
				_datas[i][j] = (byte) (Math.floor(8.0*Math.random()));
				System.out.print(" " + _datas[i][j]);
			}
		}		
	}

	public void setRemotePoint(InetAddress remotePoint) {
		if (remotePoint != null) {
			this._remotePoint = remotePoint;
		}
	}

	public InetAddress getRemotePoint() {
		return _remotePoint;
	}

	public String getName() {
		return this._name;
	}

	public void setName(String a) {
		this._name = a;
	}

	public void setLocalPort(int i) {
		System.out.println("Setting local port: " + i);
		this._localPort = i;
	}

	public int getLocalPort() {
		return this._localPort;
	}

	public void setRemotePort(int p) {
		System.out.println("Setting remote port: " + p);
		_remotePort = p;
	}

	public int getRemotePort() {
		return _remotePort;
	}

	public void addDataWatcher(DataWatcher o) {
		if (_rsThreadObject != null) {
			_rsThreadObject.addDataWatcher(o);
		} else {
			_watchers.add(o);
		}
	}

	public void removeDataWatcher(DataWatcher o) {
		if (_rsThreadObject != null) {
			_rsThreadObject.removeDataWatcher(o);
		} else {
			_watchers.remove(o);
		}
	}

	public void connect() {
		_ssThreadObject = new SendSocketThreadObject(this, _remotePort);
		Thread sendThread = new Thread(_ssThreadObject);
		System.out.println("Starting Send Thread for user: " + this._name);
		sendThread.start();
	}

	public void listen() {
		_rsThreadObject = new ReceiveSocketThreadObject(this, _localPort);
		for (DataWatcher watcher : _watchers) {
			_rsThreadObject.addDataWatcher(watcher);
		}
		Thread receiveThread = new Thread(_rsThreadObject);
		System.out.println("Starting Receive Thread for user: "
				+ this._name);
		receiveThread.start();
	}

	public void sendCommand(int x, int y, int newValue) {
			_datas[x][y] = (byte)newValue;
			System.out.println("Sending to " + _remotePoint.toString() + ":" + _remotePort);
			_ssThreadObject.sendData(_datas, _remotePoint, _remotePort);
	}
	
	public void close() {
		if (this._mode == null) {
			System.err.println("Done. You are. :D");
		} else {
			_rsThreadObject.close();
			_ssThreadObject.close();
		}
	}
}
