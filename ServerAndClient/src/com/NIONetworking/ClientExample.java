package com.NIONetworking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//!!!! TCP 블로킹 방식 통신 클라이언트 코드  !!!
//클라이언트가 서버에 연결 요청을 할 때에는 java.nio.channels.SocketChannel을 이용한다. 
//서버 연결 요청은 connect()메소드를 호출
//connect() 메소드는 서버와 연결이 될 때까지 블로킹 된다. 
//블로킹 되므로, UI및 이벤트를 처리하는 스레드에서 connect()를 호출하지 않도록 한다. 
/*public class ClientExample {
	public static void main(String[] args) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true); // 블로킹방식 명시적 표시 
			
			System.out.println("[연결 요청]");
			
			socketChannel.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[연결 성공!]");
		} catch (Exception e) {}
	}
}*/


//02. 소켓채널 데이터 통신
//클라이언트가 연결 요청(connect())하고 서버가 연결 수락(accept())했다면, 양쪽 Socket Channel 객체의 
//read(), write()메소드를 호출해서 데이터 통신을 할수 있다. 이 메소드들은 모두 버퍼를 이용하기때문에 버퍼로 읽고,쓰는 작업을 해야한다.
//read() 메소드를 호출하면 상대방이 데이터를 보내기 전까지는 블로킹 됨. 

//read() 메소드가 블로킹 해제되고 리턴되는 경우는 다음 세가지임
//1. 상대방이 데이터를 보낼 경우 해제 -> 리턴값은 읽은 바이트 수 
//2. 상대방이 정상적으로 SocketChannel의 close()를 호출할 경우 블로킹 해제 -> 리턴값 -1
//3. 상대방이 비정상적으로 종료해서 블로킹 해제되는 경우 -> 리턴값 IOException 발생 

/*public class ClientExample {
	public static void main(String[] args) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true); // 블로킹방식 명시적 표시 
			
			System.out.println("[연결 요청]");
			
			socketChannel.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[연결 성공!]");
			
			//데이터를 주고받을 바이트 버퍼 생성
			ByteBuffer byteBuffer = null;
			Charset charset = Charset.forName("UTF-8");
			
			//서버로 데이터 보내기 
			byteBuffer = charset.encode("Hello Server");
			socketChannel.write(byteBuffer);
			System.out.println("[데이터 보내기 성공]");
			
			//서버로부터 데이터 받아오기 
			byteBuffer = ByteBuffer.allocate(100); //bytebuffer공간 100 할당
			int byteCount = socketChannel.read(byteBuffer); //상대방이 비정상적으로 종료했을 경우 IOException 발생
			
			byteBuffer.flip(); //위치속성 변경하는 Buffer추상클래스의 flip메소드
			//flip()은 limit을 position으로 position을 0인덱스로 이동
			String message = charset.decode(byteBuffer).toString();
			System.out.println("[데이터 받기 성공]: "+ message) ;	
			
		} catch (Exception e) {}
		
		if(socketChannel.isOpen()){
			try{
				socketChannel.close();
			}catch(IOException e1){ }
		}
	}
}
*/

//03. TCP 방식 소켓 채널 채팅 클라이언트 구현하기 !!
public class ClientExample extends Application{ //JavaFX 메인 클래스로 만들기 위해 Application을 상속한다.
	
	//클라이언트 통신을 위한 소켓채널 필드 선언
	SocketChannel socketChannel;
	
	void startClient(){ //연결시작코드 [start]버튼을 클릭하면 호출된다.
		
		Thread thread = new Thread(){ //스레드 생성
			
			@Override
			public void run() {
				try {
					//소켓생성 및 연결요청 
					socketChannel = SocketChannel.open(); 
					socketChannel.configureBlocking(true); //블로킹!
					socketChannel.connect(new InetSocketAddress("localhost", 5001)); //서버로 연결요청 //바인딩 IP번호, port번호
					
					Platform.runLater(()->{ //UI를 변경해주는 UI용 스레드생성
						try {
							displayText("[서버에 연결완료: " + socketChannel.getRemoteAddress() + "]"); //연결되었음을 알려줌
							btnConn.setText("stop"); //start 버튼의 글자를 stop으로 바꾼다.
							btnSend.setDisable(false); //send버튼을 활성화 시킨다. 
						} catch (Exception e) {}
						
					});
				} catch (Exception e) { //예외가 발생하면 
					Platform.runLater(()->displayText("[서버 통신 안됨]")); //다음알림을 출력.
					if(socketChannel.isOpen()){ stopClient(); } // 소켓채널이 열려있으면 stopClient()호출
					return; //return 실행해서 작업 종료
				}
				receive(); //서버에서 보낸 데이터 받기 (예외가 발생하지 않으면 메소드 호출.)
			}
		};
		thread.start(); //작업 스레드 시작
	}
	
	void stopClient(){ //연결끊기코드 [stop]버튼을 클릭하면 호출된다.
		try {
			Platform.runLater(()->{
				displayText("[연결끊음]"); 
				btnConn.setText("start"); //버튼의 글자를 start로 바꾼다. 
				btnSend.setDisable(true); //send버튼을 비활성화 시킨다.
			});
			if(socketChannel!=null && socketChannel.isOpen()){ //socket필드가 null이아니고, 현재 닫혀있지 않을 경우 socket을 닫는다.
				socketChannel.close(); //연결끊기
			}
		} catch (Exception e) { }
	}
	
	void receive(){ //서버에서 보낸 데이터를 받는다.
		while(true){
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(100);
				
				//서버가 비정상적으로 종료했을 경우 IOException 발생
				int readByteCount = socketChannel.read(byteBuffer); //데이터받기
				
				//서버가 정상적으로 Socket의 close()를 호출했을 경우 
				if(readByteCount==-1 ){throw new IOException();}
				
				//문자열로 변환
				byteBuffer.flip();
				Charset charset = Charset.forName("UTF-8");
				String data = charset.decode(byteBuffer).toString();
				Platform.runLater(()->displayText("[받기완료] "+ data));
			} catch (Exception e) { //예외가 발생했을 경우 
				Platform.runLater(()->displayText("[서버 통신 안됨]"));
				stopClient();
				break;  //무한루프를 빠져나올 break;
			}
		}
	}
	
	
	void send(String data){//[send]버튼을 클릭하면 호출 , 서버로 데이터를 보낸다. 
		Thread thread = new Thread(){ //데이터를 서버로 보내는 새로운 작업 스레드 생성
			@Override
			public void run() { //run을 재정의 한다. 
				try {
					Charset charset = Charset.forName("UTF-8");
					ByteBuffer byteBuffer = charset.encode(data);
					socketChannel.write(byteBuffer); //서버로 데이터 보내기 
					Platform.runLater(()->displayText("[보내기 완료]")); 
				} catch (Exception e) {
					Platform.runLater(()->displayText("[서버 통신 안됨]"));
					stopClient();
				}
			}
		};
		thread.start(); //스레드 생성
		
	}
	
	////UI 생성코드 : 레이아웃을 구성하고, ClientExample을 실행시킨다. 
	TextArea txtDisplay;
	TextField txtInput;
	Button btnConn, btnSend;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0,0,2,0));
		root.setCenter(txtDisplay);
		
		BorderPane bottom = new BorderPane();
		txtInput = new TextField();
		txtInput.setPrefSize(60, 30); 
		BorderPane.setMargin(txtInput, new Insets(0,1,1,1));
		
		//엔터키 누르면 txtInput 안의 텍스트 내용을 전송시키기.
		txtInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke){
				//실행할 코드 
				if(ke.getCode().equals(KeyCode.ENTER)){
					send(txtInput.getText());
				}
			}
		});
		
		btnConn = new Button("start");
		btnConn.setPrefSize(60, 30);
		
		//start와 stop버튼을 클릭했을 때 이벤트 처리 코드
		btnConn.setOnAction(e->{
			if(btnConn.getText().equals("start")){ //버튼의 텍스트가 start일때 
				startClient();//startClient메소드 실행
			}else if(btnConn.getText().equals("stop")){ //버튼의 텍스트가 stop일 때 
				stopClient(); //stopClient메소드 실행
			}
		});
	
		btnSend = new Button("send");
		btnSend.setPrefSize(60 , 30);
		btnSend.setDisable(true); //send버튼 기본적으로 비활성화 상태 
		btnSend.setOnAction(e->send(txtInput.getText())); //send 버튼을 클릭했을 때 이벤트 처리코드
		
		bottom.setCenter(txtInput);
		bottom.setLeft(btnConn);
		bottom.setRight(btnSend);
		
		root.setBottom(bottom);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString()); //css 적용
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		primaryStage.setOnCloseRequest(event->stopClient()); // 윈도우 우측 상단 버튼 클릭했을 때 이벤트 처리 코드
	
		primaryStage.show();
	}
	
	void displayText(String text){ //TextArea에 문자열을 추가하는 메소드  
		txtDisplay.appendText(text+"\n");
		this.txtInput.setText(""); //엔터치고 나면 글자 치는 곳 다시 공백으로 하기.
	}
	
	public static void main(String[] args){
		launch(args);
	}
}

