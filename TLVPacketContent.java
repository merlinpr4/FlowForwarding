import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TLVPacketContent extends PacketContent {

    String t ;
    String l ;
    String v ;
	String tlv;
	String info;
   
    /**
	 * Constructor that takes in information about a file.
     * @param t
     * @param l
     * @param v
	 * @param tlv
	 * @param info info contained inside the acknowledgement
	 */

	TLVPacketContent(String t, String l, String v , String tlv ,String info) {
		type= TLV;
		this.t = t;
        this.l = l ;
        this.v = v ;
		this.tlv = tlv ;
        this.info = info;
	}

    /**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains information about a header.
	 */
    protected TLVPacketContent(ObjectInputStream oin) {
		try {
			type= TLV;
            t = oin.readUTF();
            l = oin.readUTF();
            v = oin.readUTF();
			tlv =   oin.readUTF();
			info= oin.readUTF();
		}
		catch(Exception e) {e.printStackTrace();}
	}

    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(t);
            oout.writeUTF(l);
            oout.writeUTF(v);
			oout.writeUTF(tlv);
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
        return "" + t + l + v + tlv + info ;
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
	 * @return Returns the type  of value contained in the packet.With TLV combination packets this will include number of combinations included aswell type of first information
	 */
	@Override public String getT() {
		return t;
	}

    	/**
	 * Returns the length  of value contained in the packet.
	 *
	 * @return Returns the length  of value contained in the packet.
	 */
	@Override public String getL() {
		return l;
	}

    /**
	 * Returns the value contained in the packet.
	 *
	 * @return Returns the value  contained in the packet.
	 */
	@Override public String getV() {
		return v;
	}

	/**
	 * Returns the  type and length and value of any further info contained in the packet.
	 *
	 * @return Returns the second type and length and value contained in the packet.
	 */
	@Override public String gettlv() {
		return tlv;
	}

}
