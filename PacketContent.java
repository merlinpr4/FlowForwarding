// Author : Merlin Prasad
//Student number : 19333557
import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * The class is the basis for packet contents of various types.
 *
 *
 */
public abstract class PacketContent {

	public static final int ACKPACKET= 10;  //ack packets
	public static final int RECIEVE = 20 ; //recieve packets send by application
	public static final int TLV = 30 ;      //type length value packets send by user
	public static final int FLOWMOD = 40 ; //flow modification packets send by the controller

	int type= 0;

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet to analyse.
	 */
	public static PacketContent fromDatagramPacket(DatagramPacket packet) {
		PacketContent content= null;

		try {
			int type;

			byte[] data;
			ByteArrayInputStream bin;
			ObjectInputStream oin;

			data= packet.getData();  // use packet content as seed for stream
			bin= new ByteArrayInputStream(data);
			oin= new ObjectInputStream(bin);

			type= oin.readInt();  // read type from beginning of packet

			switch(type) {   // depending on type create content object
				case ACKPACKET:
					content= new AckPacketContent(oin);
					break;
				// case SENDPACKET:
				// 	content= new SendPacketContent(oin);
				// 	break;
				case RECIEVE:
				content= new RecievePacketContent(oin);
				break;
				case TLV:
					content= new TLVPacketContent(oin);
					break;
				case FLOWMOD:
					content= new FlowMod(oin);
					break;
				
				default:
					content= null;
					break;
			}
			oin.close();
			bin.close();
		}
		catch(Exception e) {e.printStackTrace();}
		return content;
	}


	/**
	 * This method is used to transform content into an output stream.
	 *
	 * @param out Stream to write the content for the packet to.
	 */
	protected abstract void toObjectOutputStream(ObjectOutputStream out);

	/**
	 * Returns the content of the object as DatagramPacket.
	 *
	 * @return Returns the content of the object as DatagramPacket.
	 */
	public DatagramPacket toDatagramPacket() {
		DatagramPacket packet= null;

		try {
			ByteArrayOutputStream bout;
			ObjectOutputStream oout;
			byte[] data;

			bout= new ByteArrayOutputStream();
			oout= new ObjectOutputStream(bout);

			oout.writeInt(type);         // write type to stream
			toObjectOutputStream(oout);  // write content to stream depending on type

			oout.flush();
			data= bout.toByteArray(); // convert content to byte array

			packet= new DatagramPacket(data, data.length); // create packet from byte array
			oout.close();
			bout.close();
		}
		catch(Exception e) {e.printStackTrace();}

		return packet;
	}


	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public abstract String toString();

	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String only.
	 */
	public abstract String getPacketInfo();

	/**
	 * Returns the type  of value contained in the packet.
	 *
	 * @return Returns the type of value contained in the packet.
	 */
	public abstract String getT() ;

	/**
	 * Returns the length of value contained in the packet.
	 *
	 * @return Returns the length  of value contained in the packet.
	 */
	public abstract String getL() ;
	
	/**
	 * Returns the value contained in the packet.
	 *
	 * @return Returns the value contained in the packet.
	 */
	public String getV() {
		return "null";
	}

	/**
	 * Returns the type and length and value of any further info contained in the packet.
	 *
	 * @return Returns the type and length and value of any further info contained in the packet.
	 */
	public String gettlv() {
		return "null";
	}

	/**
	 * Returns the type of the packet.
	 *
	 * @return Returns the type of the packet.
	 */
	public int getType() {
		return type;
	}

}
