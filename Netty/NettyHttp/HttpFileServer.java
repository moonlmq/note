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
					ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
					ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
					ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
					ch.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(url));
				}
			});
			ChannelFuture future = b.bind("127.0.0.1",)
		}
	}
}