/*
Netty事件服务器客户端
*/
public class TimeClientHandler extends ChannelHandlerAdapter{
	private static final Logger logger = Logger
		.getLogger(TimeClientHandler.class.getName());

	private final ByteBuf firstMessage;

	/*
	* Creates a client-side handler
	*/
	public TimeClientHandler(){
		byte[] req = "QUERY TIME ORDER".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx){
		ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void ChannelRead(ChannelHandlerContext ctx,Object msg)
		throws Exception{
			ByteBuf buf = (ByteBuf) msg;
			byte[] req = new byte[buf.readableBytes()];
			buf.readableBytes(req);
			String body = new String(req,"UTF-8");
			System.out.println("Noew is: "+body);
		}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		//释放资源
		logger.warning("Unexpected exception from downstream :"
			+cause.getMessage());
		ctx.close();
	}
}