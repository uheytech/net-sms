/**
 * 
 */
package com.blueline.net.sms.codec.cmpp.msg;

import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppTerminateResponseMessage extends DefaultMessage {
	private static final long serialVersionUID = -2657187574508760595L;

	public CmppTerminateResponseMessage(long sequenceId) {
		super(CmppPacketType.CMPPTERMINATERESPONSE,sequenceId);
	}
	public CmppTerminateResponseMessage(Header header) {
		super(CmppPacketType.CMPPTERMINATERESPONSE,header);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("CmppTerminateResponseMessage [toString()=%s]",
				super.toString());
	}
	
}
