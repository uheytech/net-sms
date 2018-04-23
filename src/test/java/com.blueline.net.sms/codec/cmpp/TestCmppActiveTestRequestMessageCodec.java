package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.AbstractTestMessageCodec;
import com.blueline.net.sms.codec.cmpp.msg.CmppActiveTestRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.DefaultHeader;
import com.blueline.net.sms.codec.cmpp.msg.Header;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

public class TestCmppActiveTestRequestMessageCodec extends AbstractTestMessageCodec<CmppActiveTestRequestMessage> {

	protected CmppActiveTestRequestMessage createMsg(){
		Header header = new DefaultHeader();
		
		header.setSequenceId(0X761aeL);
		
		CmppActiveTestRequestMessage msg = new CmppActiveTestRequestMessage(header);
		return msg;
	}
	@Test
	public void testCodec()
	{
		CmppActiveTestRequestMessage msg = createMsg();
		
		ByteBuf buf =encode(msg);
		
		ByteBuf copybuf = buf.copy();
		
		Assert.assertEquals(12, buf.readableBytes());
		
		Assert.assertEquals(12, buf.readUnsignedInt());
		Assert.assertEquals(msg.getPacketType().getCommandId(),buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
		
		CmppActiveTestRequestMessage result = decode(copybuf);
		
		Assert.assertTrue(result instanceof CmppActiveTestRequestMessage); 
		
		Assert.assertEquals(msg.getHeader().getSequenceId(),((CmppActiveTestRequestMessage)result).getHeader().getSequenceId());
	}
}