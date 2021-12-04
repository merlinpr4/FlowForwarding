// Author : Merlin Prasad
//Student number : 19333557

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import tcdIO.*;

/**
 * Application class
 * An instance accepts user input
 */
public class ApplicationA extends Node {
	static final int DEFAULT_SRC_PORT = 49600; 
	static final int DEFAULT_DST_PORT = 51510;
	static final String DEFAULT_DST_NODE = "endpoint";

	InetSocketAddress dstAddress;
	Terminal terminal;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */

	ApplicationA(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal = terminal;
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(srcPort);
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Dashboard will recieve both Subscriber (Sub) packets and ACKs.
	 * If Dashboard is publishing or subscribing it recieves an ACK from Broker to show this was succefull
	 * If Dashboard is subscribed to Sensors it will get packets from the relevant sensors.
	 * Information on whether these readings are suitable is also supplied to terminal
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		PacketContent content = PacketContent.fromDatagramPacket(packet);

		terminal.println("Application A received the following packet:");
		terminal.println(content.toString());
	}

	/**
	 * Dashboard will either send Pub or Sub packets to the Broker on startup
	 * It accepts user input which it encrpts into a packet to send the the Broker
	 */
	public synchronized void start() throws Exception {

		terminal.println("Application A is ready. ");
		DatagramPacket packet;

		String userInput;
		userInput = terminal.readString("Type recieve or send or quit to exit: ");

		boolean finished = false;
		while (!finished) {

			if(userInput.equalsIgnoreCase("quit")){ 
				finished = true ;
			}
			else if (userInput.equalsIgnoreCase("recieve")){
				boolean recieve = true ;
				while (recieve = true){
					terminal.println("Application A is waiting to recieve packets from host...");
					packet = new RecievePacketContent("421","4","scss","75","49600").toDatagramPacket(); 				
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					this.wait();
			}
		}
			else if (userInput.equalsIgnoreCase("send")){
				boolean send = true ;
				while (send = true){
						boolean done = false;
						while (!done) {
							userInput = terminal.readString("Which app / network would you like to forward to?");
				
						if(userInput.equalsIgnoreCase("maths") ||userInput.equalsIgnoreCase("B") || userInput.equalsIgnoreCase("appb") ){
							    String message =  terminal.readString("Type in message to send ");
								int messageLength = message.length() ;
								
								packet = new TLVPacketContent("431","5","maths","13tcd","5" + messageLength + message ).toDatagramPacket(); 
								packet.setSocketAddress(dstAddress);
								socket.send(packet);
								terminal.println("Application A has published to forwarding service:");
						}
						else if(userInput.equalsIgnoreCase("quit")){ 
							done = true;
						}
						else if(userInput.equalsIgnoreCase("a")|| userInput.equalsIgnoreCase("scss")){ 
							terminal.println("Error.You are currently on application a i.e scss please select a valid desitnation i.e b or maths");
						}
						else {
							terminal.println("Error. Please select a valid destiation i.e b or maths");
						}
					}
				}
			  }
			else {
				terminal.println("Error . Not a valid input.");
				userInput = terminal.readString("Type send, recieve or quit to exit: ");
			}
		}
	}

	/**
	 * Recieve a packet from a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("ApplicationA");
			(new ApplicationA(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}