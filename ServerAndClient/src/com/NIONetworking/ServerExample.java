package com.NIONetworking;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// NIO 는 New Input/Output 의 약자 
// IO는 스트림 기반이다. (단일방향, 입.출력 스트림으로 구분) Input/Output Stream을 생성해야한다. 
// NIO는 Channel 기반. 채널은 스트림과 달리 양방향으로 입력과 출력이 가능하다. (입.출력을 위한 별도의 채널을 만들 필요가 없다. )
// IO는 출력스트림이 1byte를 쓰면 입력스트림이 1byte를 읽는다. 이런 시스템은 대체로 느리다.
// 그래서, 퍼버를 사용해서 복수개의 바이트를 한꺼번에 입력받고 출력하는 것이 빠른 성능을 냄. 
// IO에서는 BufferedInputStream/ BufferedOutputStream 을 연결해서 사용한다.
// NIO 에서는 기본적으로 버퍼를 사용해서 입출력을 하기 때문에 IO보다는 입출력 성능이 좋다. 
// 채널은 버퍼에 저장된 데이터를 출력하고, 입력된 데이터를 버퍼에 저장한다. 
// NIO는 블로킹과 넌블로킹의 특징을 모두 가지고 있다. 블로킹(대기상태)
// NIO의 블로킹과 IO 블로킹의 차이점은 스레드를 인터럽트함으로써 빠져나올 수가 있다. 
// NIO의 넌블로킹은 입출력 작업준비가 완료된 채널만 선택해서 작업스레드가 처리함 -> 작업스레드가 블로킹되지 않는다. 
// NIO 넌블로킹의 핵심객체는 Multiplexor인 Selector 이다. 
// 셀렉터는 복수개의 채널 중에서 준비 완료된 채널을 선택하는 방법을 제공해준다. 


//NIO 를 선택하는 이유 (네트워크 프로그램 개발 시에)
// 불특정 다수의 클라이언트 연결 또는 멀티 파일들을 넌블로킹이나 비동기로 처리할 수 있기 때문에 과도한 스레드 생성을 피하고, 스레드를 효과적으로 재사용한다는 큰장점이 있음.
// 운영체제의 버퍼를 이용한 입출력이 가능하기에 입출력 성능이 향상된다. 
// 연결 클라이언트 수가 많고, 하나의 입출력 작업이 오래 걸리지 않는 경우에 사용하는 것이 좋음. 

//!!!TCP 블로킹 채널!!!!
/*
NIO를 이용해서 TCP 서버, 클라이언트 애플리케이션을 개발하려면 블로킹, 넌블로킹, 비동기 구현 방식 중 하나를 결정해야 한다. 
*/
/*public class ServerExample {
	public static void main(String[] args) {
		//서버개발을 위해 우선 서버소켓채널의 객체를 얻는다. 
		ServerSocketChannel serverSocketChannel = null; // java.nio.channels.ServerSocketChannel
		
		try {
			serverSocketChannel = ServerSocketChannel.open(); //서버소켓채널은 정적 메소드인 open()으로 생성
			serverSocketChannel.configureBlocking(true); //기본적으로 블로킹방식으로 동작하지만, 명시적으로 블로킹임을 설정해줌(넌블로킹과 구분)
			serverSocketChannel.bind(new InetSocketAddress(5001)); //포트에 바인딩 
			while(true){
				System.out.println("[연결 기다림]");
				SocketChannel socketChannel = serverSocketChannel.accept(); //java.nio.channels.SocketChannel 클라이언트 연결 수락
				//accept메소드는 클라이언트가 연결 요청하기 전까지 블로킹되기 때문에 UI및 이벤트처리하는 스레드에서는 accept메소드를 호출하지 않도록 한다. 
				InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
				System.out.println("[연결 수락함]" + isa.getHostName()); //연결된 클라이언트의 ip 리턴한다.
			
			}
		} catch (Exception e) { }
		
		if(serverSocketChannel.isOpen()){ //ServerSocketChannel이 열려있을 경우
			try {
				serverSocketChannel.close(); //ServerSocketChannel 닫기
			} catch (IOException e) {}
		}
		
		
	}
}
*/



//02. 소켓채널 데이터 통신
/*
public class ServerExample {
	public static void main(String[] args) {
		//서버개발을 위해 우선 서버소켓채널의 객체를 얻는다. 
		ServerSocketChannel serverSocketChannel = null; // java.nio.channels.ServerSocketChannel
		
		try {
			serverSocketChannel = ServerSocketChannel.open(); //서버소켓채널은 정적 메소드인 open()으로 생성
			serverSocketChannel.configureBlocking(true); //기본적으로 블로킹방식으로 동작하지만, 명시적으로 블로킹임을 설정해줌(넌블로킹과 구분)
			serverSocketChannel.bind(new InetSocketAddress(5001)); //포트에 바인딩 
			while(true){
				System.out.println("[연결 기다림]");
				SocketChannel socketChannel = serverSocketChannel.accept(); //java.nio.channels.SocketChannel 클라이언트 연결 수락
				//accept메소드는 클라이언트가 연결 요청하기 전까지 블로킹되기 때문에 UI및 이벤트처리하는 스레드에서는 accept메소드를 호출하지 않도록 한다. 
				InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
				System.out.println("[연결 수락함]" + isa.getHostName()); //연결된 클라이언트의 ip 리턴한다.
			
				//버퍼를 이용한 데이터 주고받기 
				ByteBuffer byteBuffer = null;
				Charset charset = Charset.forName("UTF-8");
				
				//클라이언트로부터 데이터 받아오기
				byteBuffer = ByteBuffer.allocate(100);
				int byteCount = socketChannel.read(byteBuffer);
				byteBuffer.flip();
				String message = charset.decode(byteBuffer).toString();
				System.out.println("[데이터 받기 성공]: "+ message);
				
				//클라이언트에게 데이터 보내기 
				byteBuffer = charset.encode("Hello Client");
				socketChannel.write(byteBuffer);
				System.out.println("[데이터 보내기 성공]");
			}
		} catch (Exception e) { }
		
		if(serverSocketChannel.isOpen()){ //ServerSocketChannel이 열려있을 경우
			try {
				serverSocketChannel.close(); //ServerSocketChannel 닫기
			} catch (IOException e) {}
		}
		
		
	}
}
*/


//03. TCP 방식 소켓 채널 채팅 서버 구현하기 !!
//TCP 의미 ! Transmission Control Protocol 의 줄임말 
//서버와 클라이언트 간에 데이터를 신뢰성있게 전달하기 위해 만들어진 프로토콜. 
//데이터는 네트워크 선로를 통해 전달되는 과정에서 손실되거나 순서가 뒤바귈 수 있는데 TCP는 손실을 검색해내서 이를 교정하고 순서를 재조합 할 수 있도록 해준다. 
//데이터를 전송하기 전에 데이터 전송을 위한 연결을 만드는 연결지향 프로토콜임.

public class ServerExample extends Application{ //JavaFX 메인 클래스로 만들기 위해 Application을 상속받는다.
	
	ExecutorService executorService; //스레드풀인 ExecutorService 필드선언
	ServerSocketChannel serverSocketChannel; //서버소켓채널 필드 선언 : 클라이언트 연결 수락
	List<Client> connections = new Vector<Client>(); //다중클라이언트접속을 위한 리스트작성
	
	void startServer(){ 
		//서버 시작 코드작성
		executorService = Executors.newFixedThreadPool(
				100 //CPU 코어의 수에 맞게 스레드를 생성해서 관리하는 ExecutorService를 생성
		);
		System.out.println(Runtime.getRuntime().availableProcessors());
		
		//5001번포트에서 클라이언트의 연결의 수락하는 ServerSocketChannel을 생성
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress(5001));
		} catch (Exception e) {
			if(serverSocketChannel.isOpen()){ stopServer(); }
			return;
		}
		
		//연결수락 작업을 Runnable객체로 만들고 스레드풀의 작업 스레드로 실행시키는 코드 
		//ServerSocketChannel은 반복해서 클라이언트 연결요청을 기다려야하므로 스레드풀의 작업스레드상에서 accept()메소드를 반복적으로 호출해주어야함
		
		Runnable runnable = new Runnable(){
			@Override
			public void run() {
				Platform.runLater(()->{
					displayText("[서버 시작]");
					btnStartStop.setText("stop");
				});
				while(true){
					try {
						SocketChannel socketChannel = serverSocketChannel.accept(); // 연결수락 
						String message = "[연결수락: ]"+ socketChannel.getRemoteAddress() +
										": "+Thread.currentThread().getName() +"]"; 
						Platform.runLater(()->displayText(message));
						
						Client client = new Client(socketChannel); 
						connections.add(client); //client 객체저장
						
						Platform.runLater(()->displayText("연결 개수: " + connections.size()+"]"));
					} catch (Exception e) {
						if(serverSocketChannel.isOpen()){ stopServer();}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);
	}
	void stopServer(){ 
		//서버 종료 코드작성
		try {
			//connections 컬렉션으로부터 반복자를 얻어낸다. 
			Iterator<Client> iterator = connections.iterator();
			
			//while 문으로 반복자를 반복하면서 Client를 하나씩 얻는다. Client가 가지고있는 SocketChannel을 닫고 connections 컬렉션에서 Client를 제거한다.
			while(iterator.hasNext()){
				Client client = iterator.next();
				client.socketChannel.close();
				iterator.remove();
			}
			
			//서버소켓채널이  null이 아니고, 열려있으면 서버소켓채널을 닫는다. 
			if(serverSocketChannel!=null && serverSocketChannel.isOpen()){
				serverSocketChannel.close();
			}
			
			//스레드병렬처리객체가 null이아니고, 종료상태가 아니면 excutorService를 종료한다.
			if(executorService!=null && !executorService.isShutdown()){
				executorService.shutdown();
			}
			
			//작업스레드는 UI를 변경하지 못하므로 Platform.runLater() 가 사용되었다. 
			Platform.runLater(()->{
				displayText("[서버 멈춤]");
				btnStartStop.setText("start");
			});
			
		} catch (Exception e) {}
	}
	
	//Client를 ServerExample의 내부 클래스로 선언
	class Client{ 
		//연결된 클라이언트 표현
		//데이터 통신 코드작성
		
		SocketChannel socketChannel; //통신용 소켓채널을 필드로 선언!
		Client(SocketChannel socketChannel){ //생성자 선언! 매개값으로 소켓채널필드를 초기화하고 데이터받는 함수를 호출!
			this.socketChannel = socketChannel;
			receive();
		}
		void receive(){
			//데이터 받기 코드 (클라이언트로부터 데이터받는 작업을 Runnable로 생성한다) 
			Runnable runnable = new Runnable(){
				@Override
				public void run() {
					while(true){ //작업을 무한반복한다.
						try {
							ByteBuffer byteBuffer = ByteBuffer.allocate(100); //100개의 바이트를 저장할 수 있는 바이트버퍼 생성
							
							//클라이언트가 비정상 종료를 했을 경우 IOException발생
							int readByteCount = socketChannel.read(byteBuffer); //소켓채널의 read() 메소드 호출 . 
							
							//클라이언트가 정상적으로 SocketChannel의 close()를 호출 했을 경우 
							if(readByteCount == -1){
								throw new IOException();
							}
							
							String message = "[요청 처리: "+ socketChannel.getRemoteAddress() 
										+ ": "+ Thread.currentThread().getName() + "] ";
							
							Platform.runLater(()->displayText(message));
							
							//문자열로 변환
							byteBuffer.flip(); //정상적으로 데이터를 받게 되면, 받은 데이터가 저장된 바이트 버퍼의 flip()메소드를 호출해서 위치 속성값을 변경.
							Charset charset = Charset.forName("UTF-8");
							String data = charset.decode(byteBuffer).toString();
			
							//모든 클라이언트에게 보냄
							for(Client client : connections){
								client.send(data);
							}
						
						} catch (IOException e) {
							try{
								connections.remove(Client.this);
								String message = "[클라이언트 통신 안됨: "+
												socketChannel.getRemoteAddress() +": "+
												Thread.currentThread().getName() +"]";
								Platform.runLater(()->displayText(message));
							}catch(IOException e2){}
							break;
						}
					}
					
				}
			};
			executorService.submit(runnable);
		}
		void send(String data){
			//데이터를 클라이언트로 보내는 작업을 runnable로 생성한다.
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					try {
						Charset charset = Charset.forName("UTF-8");
						ByteBuffer byteBuffer = charset.encode(data);
						socketChannel.write(byteBuffer); //클라이언트로 보내기(write()하기)
					} catch (Exception e) {
						try {
							String message ="[클라이언트 통신 안됨: " +
											socketChannel.getRemoteAddress() +": "+
											Thread.currentThread().getName() +"] ";
							Platform.runLater(()->displayText(message));
							connections.remove(Client.this); //connections컬렉션에서 예외가 발생한 Client를 제거한다. 
							socketChannel.close(); //소켓 채널을 닫는다.
						} catch (Exception e2) {}
					}
				}
			};
			executorService.submit(runnable); //스레드 풀에서 처리.
		}
		
	}
	
	/////////////////////////////////////////
	//UI 생성 코드 (레이아웃 구성, ServerExample을 실행시킨다.)
	TextArea txtDisplay; //텍스트 공간객체 
	Button btnStartStop; //버튼객체 
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0,0,2,0));
		root.setCenter(txtDisplay);
		
		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		btnStartStop.setOnAction(e->{
			if(btnStartStop.getText().equals("start")){
				startServer();
			}else if(btnStartStop.getText().equals("stop")){
				stopServer();
			}
		});
		root.setBottom(btnStartStop);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event->stopServer()); //윈도우 우측상단 닫기버튼 클릭했을 때 이벤트처리
		primaryStage.show();
		
	}
	//작업스레드의 작업 처리 내용을 출력할 때 호출하는 메소드 
	void displayText(String text){ 
		txtDisplay.appendText(text+"\n");
	}
	public static void main(String[] args) {
		launch(args);
	}
}

