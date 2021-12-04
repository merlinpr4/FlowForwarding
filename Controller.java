// Author : Merlin Prasad
//Student number : 19333557


/*controller uses the default network bridge and so the ip addresses are hardcoded
ensure you start up containers in the following order on your first try 
1.Controller  ip ondefault 172.17.02 
2.EndpointB   ip ondefault 172.17.03
3.Router      ip ondefault 172.17.04 
4.RouterB     ip ondefault 172.17.05 
5.RouterC     ip ondefault 172.17.06
6.Endpoint    ip ondefault 172.17.07 
7.RouterD     ip ondefault 172.17.08 

check the ip address configuration of each container with following command
		docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' controller
this should print out 172.17.0.2 otherwise the code wont work
if there are issues test the ip address of each service with command 
docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' containername
and make sure they match the ip address listed in table below

*/

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.io.*;  
import java.net.*;
import tcdIO.Terminal;
import java.util.*;

//Router class handles the management of sending packets to other routers
public class Controller extends Node {
	static final int DEFAULT_PORT = 51510;
	static final int ENDPOINTB = 53521 ;

	Terminal terminal;
	InetSocketAddress dstAddress;
	String routerName ;
	HashMap<String, String> forwardingTable = new HashMap<String, String>();

	Controller(Terminal terminal,int port) {
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public synchronized void start() throws Exception {
     	    terminal.println("Controller is waiting for contact...");
        	this.wait();
	}

	/**
	 * The Controller will be handling multiple incoming packetsl 
	 * Gets forwarded packets from forwarding services and tells that forwarding service the best place to hop to get closer to final destination
	 * Recives ACK from routers on flow mod packets being recieved successfully
	 */

	public void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);
			
			String ipAddress = packet.getAddress().getHostName(); 
			
			if(content.getType()== PacketContent.TLV ){

				terminal.println("IP Address of packet recieved : " + ipAddress ); 

			//forwarding services forwarding table locations based on shortest hops to desired network
				if(ipAddress.equalsIgnoreCase("172.17.0.3")){//endpointB
					forwardingTable.put("maths", "appB");	
					forwardingTable.put("scss", "routerC");
				}
                else if(ipAddress.equalsIgnoreCase("172.17.0.4")){//router
					forwardingTable.put("maths", "routerB");
					forwardingTable.put("scss", "endpoint");				
                }
				else if (ipAddress.equalsIgnoreCase("172.17.0.5")){//routerB
					forwardingTable.put("maths", "routerC");
					forwardingTable.put("scss", "router");			
				}
				else if(ipAddress.equalsIgnoreCase("172.17.0.6")){//routerC
					forwardingTable.put("maths", "endpointB");	
					forwardingTable.put("scss", "routerB");
				}
				else if(ipAddress.equalsIgnoreCase("172.17.0.7")){//endpoint
					forwardingTable.put("maths", "router");
					forwardingTable.put("scss", "appA");
				}
				else if(ipAddress.equalsIgnoreCase("172.17.0.8")){ //routerD
					forwardingTable.put("maths", "routerB");
					forwardingTable.put("scss", "router");
				}
				String nextDest = content.getV() ;
				String nextContainer = forwardingTable.get(nextDest);
                DatagramPacket flowmod;

				int length = nextContainer.length() ;
				flowmod = new FlowMod("3", "" +length, nextContainer, "").toDatagramPacket();
                flowmod.setSocketAddress(packet.getSocketAddress());
                socket.send(flowmod);
                terminal.println("Controller send update to forwarding table.");
            }
			else if(content.getType()== PacketContent.ACKPACKET ){

				terminal.println("Controller recieved the following packet " + content.toString() + "\n" );	
			}
			else{
				 terminal.println("Controller recieved the following packet " + content.toString() + " from" + ipAddress + "\n" );
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public static void main(String[] args) {
		try {
			Terminal terminal= new Terminal("Controller");
			(new Controller(terminal,DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
