/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netlab2;

import netlab2.ConnPoint;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 *
 * @author Jonas
 */
public class Configuration {
    private ArrayList<ConnPoint> _hostList;
    
    public static int packetLength = 201*201;

	public static Dimension windowSize = new Dimension(600,600);
    
    public static enum modes{
        Client,
        Server
    };
            
    public Configuration(){
        _hostList = new ArrayList();
    }

    public void addServer(String name, int port) {
        ConnPoint tmpConnPoint = new ConnPoint();
        tmpConnPoint.setRemotePoint(null);
        tmpConnPoint.setLocalPort(port);
        tmpConnPoint.setName(name);
        tmpConnPoint.setMode(modes.Server);
        _hostList.add(tmpConnPoint);
    }
    public void addClient(InetAddress a, int port, String name){
        ConnPoint tmpConnPoint = new ConnPoint();
        tmpConnPoint.setRemotePoint(a);
        tmpConnPoint.setName(name);
        tmpConnPoint.setRemotePort(port);
        tmpConnPoint.setMode(modes.Client);
        _hostList.add(tmpConnPoint);       
    }
    
    public void removeHost(String name){
       for( int n = 0; n < _hostList.size(); n++){
            if( name.equalsIgnoreCase(_hostList.get(n).getName())){
                _hostList.remove(n);
                return;
            }
        }
    }

    public ConnPoint getLastPoint(){
        try{
            return _hostList.get(_hostList.size()-1);
        } catch(ArrayIndexOutOfBoundsException e){
            System.err.println("No ConnPoints available");
            return null;
        }
    }
    
    public int getHostCount(){
        return _hostList.size();
    }
    
    public InetAddress getInetAddress(String name){
        for( int n = 0; n < _hostList.size(); n++){
            if( name.equalsIgnoreCase(_hostList.get(n).getName())){
                return _hostList.get(n).getRemotePoint();
            }
        }
        return null;
    }
    public ConnPoint getConnPoint(String name){
        for( int n = 0; n < _hostList.size(); n++){
            if( name.equalsIgnoreCase(_hostList.get(n).getName())){
                return _hostList.get(n);
            }
        }
        return null;
    }
    
    public static void centerWindow(JFrame c){
        int midX = Toolkit.getDefaultToolkit().getScreenSize().width/2-c.getWidth()/2;
        int midY = Toolkit.getDefaultToolkit().getScreenSize().height/2-c.getHeight()/2;
        c.setLocation(midX, midY);
        c.validate();
    }

    public Object[] getAllConnections() {
        return _hostList.toArray();
    }

	public static Dimension getDimension() {
		// TODO Auto-generated method stub
		return windowSize;
	}
	
}
