/*
Netty时间服务器客户端
*/
public class TimeClient{
	public void connect(int port,String host) throws Exception{
		//配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			//此处channel需要设置为NioSocketChannel,然后添加HANDLER,
			//为了简单直接创建匿名内部类，是在initChannel方法，作用是
			//当创建NioSocketChannel成功之后，在进行初始化时，将他的
			//ChannelHandler设置到ChannelPipeline中，用于处理网络I/O
			//事件
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_DELAY.class)
			.handler(new ChannelInitializer<SocketChannel>(){
				@Override
				public void InitChannel(SocketChannel ch)
				throws Exception{
					ch.pipeline.addLast(new TimeClientHandler());
				}
			});

			//发起异步连接操作
			ChannelFuture f = b.connect(host,port).sync();
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
		} finally{
			group.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws Exception{
		int port = 8080;
		if(args !=null && args.length >0){
			try{
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e){
				//默认
			}
		}
		new TimeClient().connect(port,"127.0.0.1");
	}
}