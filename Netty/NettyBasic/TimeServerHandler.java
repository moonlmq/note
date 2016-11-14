/*
* Netty时间服务器服务端
* 用于对网络事件进行读写操作
*/
public class TimeServerHandler extends ChannelHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
		throws Exception{
			/*
			将msg转换成Netty的ByteBuf对象。ByteBuf类似于JDK中的java.nio.ByteBuffer
			对象，不过它提供了更加强大和灵活的功能。通过ByteBuf的readableBytes方法
			可以获取缓冲区可读的字节数，根据可读的字节数创建byte数组，通过ByteBuf的
			readBytes方法将缓冲区中的字节数组复制到新建的byte数组中，最后通过new String
			构造函数获取请求消息。这时对请求消息进行判断，如果是正确消息则创建应答消息，
			通过ChannelHandlerContext的write方法异步发送消息给客户端
			*/
			ByteBuf buf = (ByteBuf) msg;
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req);
			String body = new String(req,"UTF-8");
			System.out.println("The time server receive order : "+ body);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new
			java.util.Date(System.currentTimeMillis()).toString(): "BAD ORDER";
			ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
			ctx.write(resp);
		}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		/*调用flush方法，作用是将消息发送队列中的消息写入到SocketChannel中发送给对方。
		从性能上考虑，为了防止频繁的唤醒Selector进行消息发送，Netty的write方法并不
		直接将消息写入SocketChannel中，调用write方法只是把待发送的消息放到发送缓冲
		数组中，再通过调用flush方法，将发送缓冲区中的消息全部写道SocketChannel中。
		*/
		ctx.flush();
	}

	@Override
	//当发生异常时，关闭ChannelHandlerContext,释放和ChannelHandlerContext相关的
	// 句柄等资源。
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		ctx.close();
	}
}