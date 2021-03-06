/**
 * 
 */
package com.blueline.net.sms.codec.sgip12.msg;

import com.blueline.net.sms.codec.cmpp.msg.DefaultMessage;
import com.blueline.net.sms.codec.cmpp.msg.Header;
import com.blueline.net.sms.codec.sgip12.packet.SgipPacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class SgipUnbindRequestMessage extends DefaultMessage {
	
	private static final long serialVersionUID = 6344903835739798820L;
	public SgipUnbindRequestMessage() {
		super(SgipPacketType.UNBINDREQUEST);
	}
	
	public SgipUnbindRequestMessage(Header header) {
		super(SgipPacketType.UNBINDREQUEST,header);
	}
	
}
