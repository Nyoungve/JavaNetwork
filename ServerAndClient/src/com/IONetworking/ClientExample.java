package com.IONetworking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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

public class ClientExample extends Application{ //JavaFX 메인 클래스로 만들기 위해 Application을 상속한다.
	
	//클라이언트 통신을 위한 Socket 필드 선언
	Socket socket;
	
	void startClient(){ //연결시작코드 [start]버튼을 클릭하면 호출된다.
		
		Thread thread = new Thread(){ //스레드 생성
			
			@Override
			public void run() {
				try {
					//소켓생성 및 연결요청 
					socket = new Socket(); 
					socket.connect(new InetSocketAddress("192.168.56.1", 5001)); //서버로 연결요청 //바인딩 IP번호, port번호
					
					Platform.runLater(()->{ //UI를 변경해주는 UI용 스레드생성
						displayText("[서버에 연결완료: "+socket.getRemoteSocketAddress()+"]"); //연결되었음을 알려줌
						btnConn.setText("stop"); //start 버튼의 글자를 stop으로 바꾼다.
						btnSend.setDisable(false); //send버튼을 활성화 시킨다. 
					});
				} catch (Exception e) { //예외가 발생하면 
					Platform.runLater(()->displayText("[서버 통신 안됨]")); //다음알림을 출력.
					if(!socket.isClosed()){ stopClient(); } // 소켓이 닫혀있지 않으면 stopClient메소드 호출
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
			if(socket!=null && !socket.isClosed()){ //socket필드가 null이아니고, 현재 닫혀있지 않을 경우 socket을 닫는다.
				socket.close();
			}
		} catch (Exception e) { }
	}
	
	void receive(){ //서버에서 보낸 데이터를 받는다.
		while(true){
			try {
				byte[] byteArr = new byte[100]; //받은 데이터를 저장할 길이가 100인 바이트 배열을 생성한다.
				InputStream inputStream = socket.getInputStream(); //소켓으로부터 inputStream을 얻는다.
				
				//서버가 비정상적으로 종료했을 경우 IOException 발생
				int readByteCount = inputStream.read(byteArr); //데이터받기
				
				//서버가 정상적으로 Socket의 close()를 호출했을 경우 
				if(readByteCount==-1 ){throw new IOException();}
				
				String data = new String(byteArr, 0, readByteCount, "UTF-8"); //문자열로 변환
				
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
					byte[] byteArr = data.getBytes("UTF-8"); //인코딩한 바이트배열 얻기
					OutputStream outputStream = socket.getOutputStream(); //소켓에서 출력스트림 얻기
					outputStream.write(byteArr); ///바이트배열 매개값으로 해서 write() 메소드를 호출
					outputStream.flush(); //출력스트림 내부 퍼버를 완전히 비우도록 flush()를 호출한다. 
					Platform.runLater(()->displayText("[보내기 완료]")); 
				} catch (Exception e) {
					Platform.runLater(()->displayText("[서버 통신 안됨]"));
					stopClient();
				}
			}
		};
		thread.start(); //스레드 생성
		System.out.println(333);
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
