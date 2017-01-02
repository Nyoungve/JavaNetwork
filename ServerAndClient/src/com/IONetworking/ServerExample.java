package com.IONetworking;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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


//채팅서버 구현 코드 
/*
★  ExcecutorService
Thread와 달리 ExecutorService는 비슷한 작업을 하는 스레드를 병렬로 처리하고자 할 때 주로 사용될수 있겠다.
그리고 Thread와 다른 ExecutorService만의 특장점은 스레드의 처리 결과를 받아볼수 있다는것~! 


*/
//스레드풀(ExcecutorService), ServerSocket, Socket 사용법. 
public class ServerExample extends Application{ //javaFX 메인클래스로 만들기 위해 Application 상속
	
	//필드 
	ExecutorService executorService; //스레드풀인 ExecutorService 필드를 선언한다. 
	ServerSocket serverSocket; //클라이언트의 연결을 수락하는 SererSocket 필드 선언!
	List<Client> connections = new Vector<Client>(); //Client클래스를 리스트로 저장하는 connections 필드 선언 , 스레드에 안전한 Vector로 초기화한다.
	
	
	//서버 시작 코드 : 시작버튼 누르면 실행. 서버소켓채널 생성 및 포트바인딩, 연결 수락코드 
	void startServer(){
		
		/*
		//ExecutorService생성 -> 객체를 얻기 위해 Excutors.newFixedThreadPool()메소드를 호출
		executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()+1);//스레드 병렬처리 시켜준다? cpu코어의 수만큼 스레드 생성해서 관리함
				System.out.println(Runtime.getRuntime().availableProcessors()+1);// 최대 스레드 수가 4 (런타임상 사용가능한 프로세서수로 제한)
		*/
		
		//ExecutorService생성 -> 객체를 얻기 위해 Excutors.newFixedThreadPool()메소드를 호출
	
		executorService = Executors.newFixedThreadPool(100); //스레드 100개 생성
		
		//서버소켓 생성 및 포트 바인딩(5001번포트에서 클라이언트의 연결을 수락하는 ServerSocketChannel 생성) 
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("192.168.56.1",5001)); //클라이언트 연결 수락 바인딩 IP번호, port번호
		} catch (Exception e) {
			if(!serverSocket.isClosed()){stopServer();} //예외가 발생할 경우 ServerSocket이 닫혀있지 않으면 stopServer()메소드를 호출.
			return;
		}
		
		
		//클라이언트의 연결을 수락하는 코드 (Runnable객체로 만든다.) 스레드풀의 작업스레드로 실행시키는 코드. 
		Runnable runnable = new Runnable() { //수락작업생성
			
			//run 메소드 재정의
			@Override
			public void run() {
				Platform.runLater(()->{ //UI 스레드 생성 
					displayText("[서버 시작]"); //서버시작을 출력하도록 displayText()호출
					btnStartStop.setText("stop"); //start버튼의 글자를 stop으로 변경한다.
				});
				while(true){ //클라이언트의 연결 수락을 무한히 반복하도록 한다. 
					try {
						
						Socket socket = serverSocket.accept(); //연결수락
						String message = "[(서버)클라이언트 연결 수락: "+socket.getRemoteSocketAddress() + ": "+Thread.currentThread().getName()+"]";
						Platform.runLater(()->displayText(message)); //연결이 수락되면 클라이언트 IP 주소와 스레드이름이 포함된 연결수락 메시지 문자열을 만든다. 
						
						
						Client client = new Client(socket); //Client 객체를 생성한다. 
						connections.add(client); //Client 객체를 connections 컬렉션에 추가한다. 
						Platform.runLater(()->displayText("[서버에 연결된 클라이언트 개수:"+connections.size()+"]"));
						
					} catch (Exception e) {
						if(!serverSocket.isClosed()){stopServer();} //예외가 발생했을 경우, serverSocket이 닫혀있지 않으면, stopServer를 호출한다. 
						break; // break를 사용해서 while문을 멈춘다
					}
				}
			}
		};
		executorService.submit(runnable); //스레드풀의 작업 스레드에서 연결 수락 작업을 처리하기 위해 submit()을 호출한다. 
		
	} //startServer 메소드 콜 .
	
	
	//stop버튼을 누르면 stopServer() 메소드가 실행. 
	//stopServer() 메소드에는 연결된 모든 Socket 닫기, ServerSocket닫기, ExecutorService 종료코드.
	void stopServer(){ //서버 종료 코드
		try {
			Iterator<Client> iterator = connections.iterator();  //connections 컬렉션으로 부터 반복자를 얻어낸다. 
			while(iterator.hasNext()){ //while문으로 반복자를 반복한다.   
				Client client= iterator.next(); //반복자로부터 Client를 하나씩 얻는다. 
				client.socket.close();// Client가 가지고있는 Socket을 닫는다.
				iterator.remove(); //connections 컬렉션에서 Client를 제거
			}
			if(serverSocket!=null && !serverSocket.isClosed()){ //ServerSocket이 null이 아니고, 닫혀있지 않으면
				serverSocket.close(); //ServerSocket을 닫는다.
			}
			if(executorService!=null && !executorService.isShutdown()){ //ExecutorService가 null이 아니고, 종료 상태가 아니면
				executorService.shutdown(); //ExecutorService종료
			}
			
			Platform.runLater(()->{ //UI 스레드 생성
				displayText("[서버 멈춤]");
				btnStartStop.setText("start"); //stop 버튼의 글자를 start로 바꾸기. 
			});
			
		} catch (Exception e) {}
	
	}

	//서버에 다수의 클라이언트가 연결하기 때문에 서버는 클라이언트를 관리해야 한다. 
	//클라이언트 별로 고유한 데이터를 저장할 필요도 있기 때문에 Client클래스를 작성하고, 연결 수락 시 마다 Client 인스턴스를 생성해서 관리하는 것이 좋다. 
	
	class Client{ //ServerExample의 내부 클래스로 선언한다. 
		Socket socket; //Socket 필드를 선언한다. 
	
		Client(Socket socket){ //Client생성자를 선언한다. 
			this.socket = socket; //매개값으로 받은 Socket을 필드 값으로 저장한다. 
			receive(); // receive()메소드 호출
		}
		
		void receive(){//클라언트의 데이터 받기 위한 메소드.
			Runnable runnable = new Runnable(){ //클라이언트로 부터 데이터받는 작업Runnable로 정의
				@Override
				public void run(){ //run메소드 재정의
					try {
						while(true){ //작업 무한 반복
							byte []byteArr = new byte[100]; //받은 데이터 저장할 byte[] 배열인 byteArr를 생성 
							InputStream inputStream = socket.getInputStream(); //socket으로 부터 inputStream을 얻는다.
							

							int readByteCount = inputStream.read(byteArr); //inputStream의 read()메소드 호출.
							
							//클라이언트가 정상적으로 Socket의 close()를 호출했을 경우, read()메소드는 -1을 리턴한다.
							if(readByteCount == -1){
								throw new IOException(); //이 경우, IOException을 강제로 발생
							}
							
							//정상적으로 데이터를 받았을 경우, 다음의 문자열을 만든다. "[요청 처리: 클라이언트 IP : 작업스레드 이름]"
							String message = "[클라이언트의 요청을 처리: "+socket.getRemoteSocketAddress() + ": "+ Thread.currentThread().getName()+"]";
							Platform.runLater(()->displayText(message)); //문자열을 출력하도록 displayText()를 호출한다.
							
							String data = new String(byteArr, 0, readByteCount, "UTF-8"); //문자열로 변환 
							
							//문자열을 모든 클라이언트에게 보내기 위해 connections에 저장된 Client를 하나씩 얻어 send() 메소드를 호출한다.
							for(Client client : connections){
								client.send(data); //모든 클라이언트들에게 데이터를 보내주는 send 메소드
							}
						}
					} catch (Exception e) { //예외가 발생하면 
						try {
							
							connections.remove(Client.this); //connections에 저장된 Client를 하나씩 얻어 send() 메소드 호출한다.
							String message = "[클라이언트 통신 안됨: "+ socket.getRemoteSocketAddress() +": "+ Thread.currentThread().getName() +"]";
							Platform.runLater(()->displayText(message)); // UI창에 메시지를 띄워준다.
							socket.close(); //socket을 닫는다.
							
						} catch (Exception e2) { }
					}
				}
			};
			executorService.submit(runnable); //스레드 풀에서 작업을 처리하기 위해 submit을 호출한다.
		}
		
		void send(String data){//클라이언트로 메시지를 보내는 메소드.
			Runnable runnable = new Runnable(){ //데이터를 클라이언트로 보내는 작업을 Runnable로 생성한다. 
				
				@Override
				public void run(){ //run을 재정의한다.
					try {
						
						//client로 데이터 보내기
						byte []byteArr = data.getBytes("UTF-8");
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(byteArr);
						outputStream.flush();
					} catch (Exception e) {
						try {
							
							String message = "[클라이언트 통신 안됨: "+ socket.getRemoteSocketAddress() + ": "+ Thread.currentThread().getName() +"]";
							Platform.runLater(()->displayText(message)); //UI 메세지 처리 
							connections.remove(Client.this);
							socket.close();
							
						} catch (Exception e2) {}
					}
				}
			};
			executorService.submit(runnable); //스레드 풀에서 처리 
		}
	}
	
	/////UI 구현
	TextArea txtDisplay;
	Button btnStartStop;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		BorderPane root= new BorderPane();
		root.setPrefSize(500, 300);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0,0,2,0));
		root.setCenter(txtDisplay);
		
		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		
		btnStartStop.setOnAction(e->{  //start버튼과 stop버튼을 클릭했을 때 이벤트 처리. 
			if(btnStartStop.getText().equals("start")){
				startServer();
			}else if(btnStartStop.getText().equals("stop")){
				stopServer();
			}
		});
		
		
		root.setBottom(btnStartStop);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResourceAsStream("app.css").toString()); // 외부 CSS 파일을 Scene에 적용.
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event->stopServer()); //윈도우 우측 상단 닫기 버튼을 클릭했을 때 이벤트 처리.
		primaryStage.show();
		
	}
	
	void displayText(String text){ //작업 스레드의 작업처리 내용을 출력할 때 호출 하는 메소드 
		txtDisplay.appendText(text+"\n");
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
	
}
