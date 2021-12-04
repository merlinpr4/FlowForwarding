// Author : Merlin Prasad
//Student number : 19333557
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.io.*;  
import java.net.*;

import tcdIO.Terminal;
import java.util.*;

//Service class handles the management of sending packets to other services or applications
public class Service extends Node {
	static final int DEFAULT_PORT = 51510;
	Terminal terminal;
	InetSocketAddress dstAddress;

	String finalDest   = "";
	DatagramPacket incomingPacket;
	String finalDestPort = "";
	HashMap<String, String> forwardingTable = new HashMap<String, String>();

	Service(Terminal terminal,int port) {
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	//Service sends a connection packet to the controller on startup
	public synchronized void start() throws Exception {
		//send initial connect packet to controller
		DatagramPacket recieve;
		recieve = new RecievePacketContent("6","7","connect","","").toDatagramPacket(); 			
		InetAddress controllerIP = InetAddress.getByName("172.17.0.2"); 	
		dstAddress= new InetSocketAddress(controllerIP, DEFAULT_PORT);
		recieve.setSocketAddress(dstAddress);
		socket.send(recieve);

		terminal.println("Service is waiting for contact....");
		this.wait();
	}

	/**
	 * The Service will be handling multiple incoming packets
	 * Send Acknowledgements to controller tell the device recieved packet sucessfully 
	 * Forward on other Serives or Applications
	 */

	public void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);

			//Router recieved following packet
			terminal.println("\n" +"Service recieved the following packet " + content.toString());			
			terminal.println("Packet recieved from network: " + packet.getAddress().getHostName()); 

			if(content.getType() == PacketContent.RECIEVE){
				finalDestPort = content.getPacketInfo();
				terminal.println("Network " + content.getV() + " is ready to recieve packets");
			}
			else{
				//if you recieve controller packet update forwarding table
				if	(content.getType()==PacketContent.FLOWMOD){
					String nextdest = content.getV() ;	
					forwardingTable.put(finalDest, nextdest);

					//acknowledgement to router
					DatagramPacket response;
					response= new AckPacketContent("2","3","ACK").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
					terminal.println("Service send acknowledgment ");
				}
				//if you recieve packet from forwarding service
				if	(content.getType()==PacketContent.TLV){
					finalDest = content.getV() ; //check header for final destination information
					incomingPacket = packet ;
				}
				String nextContainer = forwardingTable.get(finalDest);		
				if(forwardingTable.containsKey(finalDest)){ //forward to next service if dest is known
					terminal.println("Quickest route to " + finalDest +   " is through: "+ nextContainer); 
					int destPort = DEFAULT_PORT ;

					if(nextContainer.equalsIgnoreCase("appB")){
						terminal.println( "Port number " + finalDestPort);
						destPort = Integer.valueOf(finalDestPort) ;
						nextContainer = "endpointB" ;
						terminal.println("Forwarding service send to application" );
					}
					else if(nextContainer.equalsIgnoreCase("appA")){
						terminal.println( "Port number " + finalDestPort);
						destPort = Integer.valueOf(finalDestPort) ;
						nextContainer = "endpoint" ;
						terminal.println("Forwarding service send to application" );
					}

					dstAddress= new InetSocketAddress(nextContainer, destPort);
					incomingPacket.setSocketAddress(dstAddress);
					socket.send(incomingPacket);
					terminal.println("Packet sent by Service"  + "\n" );
				}
				else  //contact controller
				{	//this ip addresss is set by docker at start when you make container pls ensure to start containers in right order
					InetAddress controllerIP = InetAddress.getByName("172.17.0.2"); 	
					dstAddress= new InetSocketAddress(controllerIP, DEFAULT_PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					terminal.println("Destination is unknown contacting controller");
				}
			}

		}
		catch(Exception e) {e.printStackTrace();}
	}

	public static void main(String[] args) {
		try {
			Terminal terminal= new Terminal("Service");
			(new Service(terminal,DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}