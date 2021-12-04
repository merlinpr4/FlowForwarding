//AckPacketContent class adapted from Docker hello world tuturial
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents acknowledgements
 */
public class AckPacketContent extends PacketContent {
	String t;
	String l ;
	String info;

	/**
	 * Constructor that takes in information about a file.
	 * @param info info contained inside the acknowledgement
	 */
	AckPacketContent(String t, String l, String info) {
		type= ACKPACKET;
		this.t = t;
        this.l = l ;
        this.info = info;
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains information about a header.
	 */
	protected AckPacketContent(ObjectInputStream oin) {
		try {
			type= ACKPACKET;
			t = oin.readUTF();
            l = oin.readUTF();
			info= oin.readUTF();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeUTF(t);
            oout.writeUTF(l);
			oout.writeUTF(info);
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return  "" + t + l + info;
	}

	/**
	 * Returns the info contained in the packet.
	 *
	 * @return Returns the info contained in the packet.
	 */
	public String getPacketInfo() {
		return info;
	}

		/**
	 * Returns the type  of value contained in the packet.
	 *
	 * @return Returns the type  of value contained in the packet.
	 */
	public String getT() {
		return t;
	}

    	/**
	 * Returns the length  of value contained in the packet.
	 *
	 * @return Returns the length  of value contained in the packet.
	 */
	public String getL() {
		return l;
	}
}
