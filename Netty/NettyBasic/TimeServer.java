/**
*netty时间服务器服务端
*/
public class TimeServer{
	public void bind(int port) throws Exception{
		//配置服务端的NIO线程组
		/*
		创建两个NiuEventLoopGroup实例。NioEventLoopGroup是个线程组，它
		包含了一组NIO线程，专门用于网络事件的处理，实际上它们就是Reactor
		线程组。这里创建两个原因是一个用于服务端接受客户端的连接，另一个
		用于进行SocketChannel的网络读写。
		*/
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			//创建ServerBootstrap对象，它是Netty用于启动NIO服务端的辅助启动类
			// 目的是降低服务端的开发复杂度
			ServerBootstrap b = new ServerBootstrap();
			//将两个NIO线程组当做入参传递到ServerBootstrap中
			b.group(bossGroup,workerGroup)
			// 设置创建的Channel为NioServerSocketChannel,他的功能对应于JDK NIO类库
			// 的ServerSocketChannel类
			.channel(NioServerSocketChannel.class)
			// 配置NioServerSocketChannel的TCP参数
			.option(ChanenelOption.SO_BACKLOG,1024)
			// 绑定I/O事件的处理类ChildChannelHandler,它的作用类似于Reactor模式中
			// 的handler类，主要用于处理网络I/O事件，例如记录日志，对消息进行编码等。
			.childHandler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			// 服务端启动辅助类匹配完成之后，调用它的bind方法绑定监听端口，随后调用它的
			// 同步阻塞方法sync等待绑定操作完成。完成之后Netty会返回一个ChannelFuture，
			// 它的功能类似于JDK的java.util.concurrent.Future，主要用于异步操作的通知回调。
			ChannelFuture f = b.bind(port).sync();
			//等待服务端监听端口关闭
			// 进行阻塞，等待服务端链路关闭之后main函数才退出
			f.channel().closeFuture().sync();
		} finally{
			//退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel)(SocketChannel arg0) throws Exception{
			arg0.pipeline().addLast(new TimeServerHandler());
		}
	}

	/**
	* @param args
	* @throws Exception
	*/
	public static void main(String[] args) throws Exception{
		int port =  8080;
		if (args != null && args.length >0){
			try{
				port = Integer.valueOf(args[0]);
			} catch(NumberFormatException e){
				//采用默认值
			}
		}
		new TimeServer().bind(port);
	}
}
