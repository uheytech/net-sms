/**
 * 
 */
package com.blueline.net.sms.codec.cmpp;

import com.blueline.net.sms.codec.cmpp.msg.CmppCancelRequestMessage;
import com.blueline.net.sms.codec.cmpp.msg.Message;
import com.blueline.net.sms.codec.cmpp.packet.CmppCancelRequest;
import com.blueline.net.sms.codec.cmpp.packet.CmppPacketType;
import com.blueline.net.sms.codec.cmpp.packet.PacketType;
import com.blueline.net.sms.common.util.DefaultMsgIdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import static com.blueline.net.sms.common.util.NettyByteBufUtil.toArray;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppCancelRequestMessageCodec extends MessageToMessageCodec<Message, CmppCancelRequestMessage> {
	private PacketType packetType;

	public CmppCancelRequestMessageCodec() {
		this(CmppPacketType.CMPPCANCELREQUEST);
	}

	public CmppCancelRequestMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}

		CmppCancelRequestMessage requestMessage = new CmppCancelRequestMessage(msg.getHeader());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		requestMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer,CmppCancelRequest.MSGID.getLength())));
		ReferenceCountUtil.release(bodyBuffer);
		out.add(requestMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppCancelRequestMessage msg, List<Object> out) throws Exception {

		msg.setBodyBuffer(DefaultMsgIdUtil.msgId2Bytes(msg.getMsgId()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		out.add(msg);
	}

}