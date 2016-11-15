/*
Http文件服务器启动类
*/
public class HttpFileServer{
	private static final String DEFAULT_URL="/src/com/phei/netty/";

	public void run(final int port,final String url) throws Exception{
		EventLoopGroup bossGroup = new NioEvenetloopGroup();
		EventLoopGroup workerGroup = new NioEvenetloopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>(){
				@Override
				protected void initChannel(SocketChannel ch) throws Exception{
					// 向ChannelPipeline中添加HTTP请求要求解码器
					ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
					// 添加HttpObjectAggregator解码器，作用是将多个消息转换为单一的FullHttpRequest
					// 或者FullHttpResponse,原因是http解码器在每个HTTP消息中会生成多个消息对象。
					// 1.HttpRequest/HttpRespoonse;2.HttpContent;3.LastHttpContent.
					ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
					// 新增http响应编码器，对HTTP响应消息进行编码
					ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
					// 新增Chunked handler，作用是支持异步发送打的码流（例如大的文件传输），但不占
					// 过多的内存，防止发送JAVA内存溢出错误。
					ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
					// 最后添加HttpFileServerHandler，用于文件服务器的业务逻辑处理。
					ch.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(url));
				}
			});
			ChannelFuture future = b.bind("127.0.0.1",port).sync();
			System.out.println("Http 文件目录服务器启动，网址是："+
				"http://192.168.1.102:" + port+url);
			future.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws Exception{
		int port = 8080;
		if(args.lenth >0){
			try{
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		String url = DEFAULT_URL;
		if (args.length >1)
			url = args[1];
		new HttpFileServer().run(port,url);
	}
}