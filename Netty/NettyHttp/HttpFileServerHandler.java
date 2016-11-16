/*
HTTP文件服务器
处理类
*/
public class HttpFileServerHandler extends SimpleChannelInboindHandler<FullHttpRequest>{
	@Override
	public void messageReceived(ChannelHandlerContext ctx,FullHttpRequest request) throws Exception{
		// 对HTTP请求行中的方法进行判断，如果不是从浏览器或者表单设置为GET发起的请求，则构造HTTP 405错误
		// 返回
		if (!request.getDecoderResult().isSuccess()){
			sendError(ctx,BAD_REQUEST);
			return;
		}
		if(request.getMethod()!=GET){
			sendError(ctx,METHOD_NOT_ALLOWED);
			return;
		}
		final String url = request.getUrl();
		// 对请求URL进行包装，然后对sanitizeUri方法展开解析
		final String path = sanitizeUri(url);
		// 如果构造的URI不合法，则返回403错误
		if(path == null){
			sendError(ctx,FoRBIDDEN);
			return;
		}
		// 使用新组装的URI路径构造File对象
		File file = new File(path);
		// 如果文件不存在或是系统隐藏文件，则构造404异常返回
		if(file.isHidden() || !file.exists()){
			sendError(ctx,NOT_FOUND);
			return;
		}
		//此处代码省略
		if(!file.isFile()){
			sendError(ctx,FoRBIDDEN);
			return;
		}
		RandomAccessFile randomAccessFile = null;
		try{
			randomAccessFile = new RandomAccessFile(file,"r")//以只读的方式打开
		} catch(FileNotFoundException fnfe){
			sendError(ctx,NOT_FOUND);
			return;
		}
		// 获取文件的长度，构造成功的HTTP应答消息，然后在消息头中contentlength和
		// contenttype，判断是否是Keep-Alive，如果是，则在应答消息头中设置Connection
		// 为Keep-Alive
		long fileLength = randomAccessFile.length();
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1,OK);
		setContentLength(response,fileLength);
		setContentTypeHeader(response,file);
		if(isKeepAlive(request)){
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		// 发送响应消息
		ctx.write(response);
		ChannelFuture sendFileFuture;
		// 通过Netty的ChunkedFile对象直接将文件写入到发送缓冲区
		sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile,0,
			fileLength,8192),ctx.newProgressivePromise());
		// 为sendFileFuture增加GenericFutureListener，如果发送完成，打印“Transfer complete
		
		/*
		如果使用chunked编码，最后要发送一个编码结束的空消息体，将LastHttpContent的
		EMPTY_LAST_CONTENT发送到缓冲区，标识所有的消息体已经发送完成，同时调用flush
		方法将之前在发送缓冲区的消息刷新到SocketChannel中发给对方。
		如果是非Keep-Alive的，最后一包消息发送完成之后，服务端主动关闭连接。
		*/
		sendFileFuture.addListener(new ChannelProgressiveFutureListener(){
			//此处代码省略
		})
		}
		private String sanitizeUri(String url){
			try{
			// 使用JDK的java.net.URLDecoder对URL进行解码。使用UTF-8字符集，解码成功
			// 后对URI进行合法性判断，如果URI与允许访问的URI一致或者是其子目录（文件），
			// 则校验通过，否则返回空。
				url = URLDecoder.decode(url,"UTF-8");
			} catch(UnsupportedEncodingException e){
				try{
					url = URLDecoder.decode(url,"ISO-8859-1");
				} catch (UnsupportedEncodingException e1){
					throw new Error();
				}
			}
			//此处代码省略
			// 将硬编码的文件路径分隔符替换为本地操作系统的文件路径分隔符
			url = url.replacce('/',File.separatorChar);
			// 对新的URI做二次合法性校验，如果校验失败则直接返回空
			if(url.contains(File.separator +'.')||
			 url.contains('.'+File.separator)||url.startWith(".")||
			 url.endsWith(".")||INSECURE_URL.matcher(url).matches()){
				return null;
			}
			// 对文件进行拼接，使用当前运行程序所在的工程目录+URI构造绝对路径返回
			return System.getProperty("user.dir")+File.separator+url;
		}

		private static final Pattern ALLOWED_FILE_NAME = Pattern
		.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

		private static void sendListing(ChannelHandlerContext ctx,File dir){
			// 创建成功的HTTP响应消息，随后设置消息头的类型为"text/html;charset=UTF-8"
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,OK);
			response.headerss().set(CONTENT_TYPE,"text/html;charset=UTF-8");
			// 构造响应消息体，由于需将结果显示在浏览器去上，所以采用HTML格式
			StringBuilder buf = new StringBuilder();
			//此处代码省略
			// 打印了一个".."的连接
			buf.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
			// 展示根目录下的所有文件和文件夹，同时使用超链接来标识
			for (File f: dir.listFiles()){
				// 此处代码省略
				buf.append("<li>链接：<a href=\"");
				// 此处代码省略
			}
			buf.append("</ul></body></html>\r\n");
			// 分配对应消息的缓冲对象
			ByteBuf  buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
			// 将缓冲区的响应消息存放到HTTP应答消息中，然后释放缓冲区，最后调用writeAndFlush
			// 将响应消息发送到缓冲区并刷新到SocketChannel中。
			response.content().writeBytes(buffer);
			buffer.release();
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}

		//此处代码省略

		
	
}