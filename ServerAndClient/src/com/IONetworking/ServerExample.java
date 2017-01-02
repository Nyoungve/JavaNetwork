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


//ä�ü��� ���� �ڵ� 
/*
��  ExcecutorService
Thread�� �޸� ExecutorService�� ����� �۾��� �ϴ� �����带 ���ķ� ó���ϰ��� �� �� �ַ� ���ɼ� �ְڴ�.
�׸��� Thread�� �ٸ� ExecutorService���� Ư������ �������� ó�� ����� �޾ƺ��� �ִٴ°�~! 


*/
//������Ǯ(ExcecutorService), ServerSocket, Socket ����. 
public class ServerExample extends Application{ //javaFX ����Ŭ������ ����� ���� Application ���
	
	//�ʵ� 
	ExecutorService executorService; //������Ǯ�� ExecutorService �ʵ带 �����Ѵ�. 
	ServerSocket serverSocket; //Ŭ���̾�Ʈ�� ������ �����ϴ� SererSocket �ʵ� ����!
	List<Client> connections = new Vector<Client>(); //ClientŬ������ ����Ʈ�� �����ϴ� connections �ʵ� ���� , �����忡 ������ Vector�� �ʱ�ȭ�Ѵ�.
	
	
	//���� ���� �ڵ� : ���۹�ư ������ ����. ��������ä�� ���� �� ��Ʈ���ε�, ���� �����ڵ� 
	void startServer(){
		
		/*
		//ExecutorService���� -> ��ü�� ��� ���� Excutors.newFixedThreadPool()�޼ҵ带 ȣ��
		executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()+1);//������ ����ó�� �����ش�? cpu�ھ��� ����ŭ ������ �����ؼ� ������
				System.out.println(Runtime.getRuntime().availableProcessors()+1);// �ִ� ������ ���� 4 (��Ÿ�ӻ� ��밡���� ���μ������� ����)
		*/
		
		//ExecutorService���� -> ��ü�� ��� ���� Excutors.newFixedThreadPool()�޼ҵ带 ȣ��
	
		executorService = Executors.newFixedThreadPool(100); //������ 100�� ����
		
		//�������� ���� �� ��Ʈ ���ε�(5001����Ʈ���� Ŭ���̾�Ʈ�� ������ �����ϴ� ServerSocketChannel ����) 
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("192.168.56.1",5001)); //Ŭ���̾�Ʈ ���� ���� ���ε� IP��ȣ, port��ȣ
		} catch (Exception e) {
			if(!serverSocket.isClosed()){stopServer();} //���ܰ� �߻��� ��� ServerSocket�� �������� ������ stopServer()�޼ҵ带 ȣ��.
			return;
		}
		
		
		//Ŭ���̾�Ʈ�� ������ �����ϴ� �ڵ� (Runnable��ü�� �����.) ������Ǯ�� �۾�������� �����Ű�� �ڵ�. 
		Runnable runnable = new Runnable() { //�����۾�����
			
			//run �޼ҵ� ������
			@Override
			public void run() {
				Platform.runLater(()->{ //UI ������ ���� 
					displayText("[���� ����]"); //���������� ����ϵ��� displayText()ȣ��
					btnStartStop.setText("stop"); //start��ư�� ���ڸ� stop���� �����Ѵ�.
				});
				while(true){ //Ŭ���̾�Ʈ�� ���� ������ ������ �ݺ��ϵ��� �Ѵ�. 
					try {
						
						Socket socket = serverSocket.accept(); //�������
						String message = "[(����)Ŭ���̾�Ʈ ���� ����: "+socket.getRemoteSocketAddress() + ": "+Thread.currentThread().getName()+"]";
						Platform.runLater(()->displayText(message)); //������ �����Ǹ� Ŭ���̾�Ʈ IP �ּҿ� �������̸��� ���Ե� ������� �޽��� ���ڿ��� �����. 
						
						
						Client client = new Client(socket); //Client ��ü�� �����Ѵ�. 
						connections.add(client); //Client ��ü�� connections �÷��ǿ� �߰��Ѵ�. 
						Platform.runLater(()->displayText("[������ ����� Ŭ���̾�Ʈ ����:"+connections.size()+"]"));
						
					} catch (Exception e) {
						if(!serverSocket.isClosed()){stopServer();} //���ܰ� �߻����� ���, serverSocket�� �������� ������, stopServer�� ȣ���Ѵ�. 
						break; // break�� ����ؼ� while���� �����
					}
				}
			}
		};
		executorService.submit(runnable); //������Ǯ�� �۾� �����忡�� ���� ���� �۾��� ó���ϱ� ���� submit()�� ȣ���Ѵ�. 
		
	} //startServer �޼ҵ� �� .
	
	
	//stop��ư�� ������ stopServer() �޼ҵ尡 ����. 
	//stopServer() �޼ҵ忡�� ����� ��� Socket �ݱ�, ServerSocket�ݱ�, ExecutorService �����ڵ�.
	void stopServer(){ //���� ���� �ڵ�
		try {
			Iterator<Client> iterator = connections.iterator();  //connections �÷������� ���� �ݺ��ڸ� ����. 
			while(iterator.hasNext()){ //while������ �ݺ��ڸ� �ݺ��Ѵ�.   
				Client client= iterator.next(); //�ݺ��ڷκ��� Client�� �ϳ��� ��´�. 
				client.socket.close();// Client�� �������ִ� Socket�� �ݴ´�.
				iterator.remove(); //connections �÷��ǿ��� Client�� ����
			}
			if(serverSocket!=null && !serverSocket.isClosed()){ //ServerSocket�� null�� �ƴϰ�, �������� ������
				serverSocket.close(); //ServerSocket�� �ݴ´�.
			}
			if(executorService!=null && !executorService.isShutdown()){ //ExecutorService�� null�� �ƴϰ�, ���� ���°� �ƴϸ�
				executorService.shutdown(); //ExecutorService����
			}
			
			Platform.runLater(()->{ //UI ������ ����
				displayText("[���� ����]");
				btnStartStop.setText("start"); //stop ��ư�� ���ڸ� start�� �ٲٱ�. 
			});
			
		} catch (Exception e) {}
	
	}

	//������ �ټ��� Ŭ���̾�Ʈ�� �����ϱ� ������ ������ Ŭ���̾�Ʈ�� �����ؾ� �Ѵ�. 
	//Ŭ���̾�Ʈ ���� ������ �����͸� ������ �ʿ䵵 �ֱ� ������ ClientŬ������ �ۼ��ϰ�, ���� ���� �� ���� Client �ν��Ͻ��� �����ؼ� �����ϴ� ���� ����. 
	
	class Client{ //ServerExample�� ���� Ŭ������ �����Ѵ�. 
		Socket socket; //Socket �ʵ带 �����Ѵ�. 
	
		Client(Socket socket){ //Client�����ڸ� �����Ѵ�. 
			this.socket = socket; //�Ű������� ���� Socket�� �ʵ� ������ �����Ѵ�. 
			receive(); // receive()�޼ҵ� ȣ��
		}
		
		void receive(){//Ŭ���Ʈ�� ������ �ޱ� ���� �޼ҵ�.
			Runnable runnable = new Runnable(){ //Ŭ���̾�Ʈ�� ���� �����͹޴� �۾�Runnable�� ����
				@Override
				public void run(){ //run�޼ҵ� ������
					try {
						while(true){ //�۾� ���� �ݺ�
							byte []byteArr = new byte[100]; //���� ������ ������ byte[] �迭�� byteArr�� ���� 
							InputStream inputStream = socket.getInputStream(); //socket���� ���� inputStream�� ��´�.
							

							int readByteCount = inputStream.read(byteArr); //inputStream�� read()�޼ҵ� ȣ��.
							
							//Ŭ���̾�Ʈ�� ���������� Socket�� close()�� ȣ������ ���, read()�޼ҵ�� -1�� �����Ѵ�.
							if(readByteCount == -1){
								throw new IOException(); //�� ���, IOException�� ������ �߻�
							}
							
							//���������� �����͸� �޾��� ���, ������ ���ڿ��� �����. "[��û ó��: Ŭ���̾�Ʈ IP : �۾������� �̸�]"
							String message = "[Ŭ���̾�Ʈ�� ��û�� ó��: "+socket.getRemoteSocketAddress() + ": "+ Thread.currentThread().getName()+"]";
							Platform.runLater(()->displayText(message)); //���ڿ��� ����ϵ��� displayText()�� ȣ���Ѵ�.
							
							String data = new String(byteArr, 0, readByteCount, "UTF-8"); //���ڿ��� ��ȯ 
							
							//���ڿ��� ��� Ŭ���̾�Ʈ���� ������ ���� connections�� ����� Client�� �ϳ��� ��� send() �޼ҵ带 ȣ���Ѵ�.
							for(Client client : connections){
								client.send(data); //��� Ŭ���̾�Ʈ�鿡�� �����͸� �����ִ� send �޼ҵ�
							}
						}
					} catch (Exception e) { //���ܰ� �߻��ϸ� 
						try {
							
							connections.remove(Client.this); //connections�� ����� Client�� �ϳ��� ��� send() �޼ҵ� ȣ���Ѵ�.
							String message = "[Ŭ���̾�Ʈ ��� �ȵ�: "+ socket.getRemoteSocketAddress() +": "+ Thread.currentThread().getName() +"]";
							Platform.runLater(()->displayText(message)); // UIâ�� �޽����� ����ش�.
							socket.close(); //socket�� �ݴ´�.
							
						} catch (Exception e2) { }
					}
				}
			};
			executorService.submit(runnable); //������ Ǯ���� �۾��� ó���ϱ� ���� submit�� ȣ���Ѵ�.
		}
		
		void send(String data){//Ŭ���̾�Ʈ�� �޽����� ������ �޼ҵ�.
			Runnable runnable = new Runnable(){ //�����͸� Ŭ���̾�Ʈ�� ������ �۾��� Runnable�� �����Ѵ�. 
				
				@Override
				public void run(){ //run�� �������Ѵ�.
					try {
						
						//client�� ������ ������
						byte []byteArr = data.getBytes("UTF-8");
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(byteArr);
						outputStream.flush();
					} catch (Exception e) {
						try {
							
							String message = "[Ŭ���̾�Ʈ ��� �ȵ�: "+ socket.getRemoteSocketAddress() + ": "+ Thread.currentThread().getName() +"]";
							Platform.runLater(()->displayText(message)); //UI �޼��� ó�� 
							connections.remove(Client.this);
							socket.close();
							
						} catch (Exception e2) {}
					}
				}
			};
			executorService.submit(runnable); //������ Ǯ���� ó�� 
		}
	}
	
	/////UI ����
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
		
		btnStartStop.setOnAction(e->{  //start��ư�� stop��ư�� Ŭ������ �� �̺�Ʈ ó��. 
			if(btnStartStop.getText().equals("start")){
				startServer();
			}else if(btnStartStop.getText().equals("stop")){
				stopServer();
			}
		});
		
		
		root.setBottom(btnStartStop);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResourceAsStream("app.css").toString()); // �ܺ� CSS ������ Scene�� ����.
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event->stopServer()); //������ ���� ��� �ݱ� ��ư�� Ŭ������ �� �̺�Ʈ ó��.
		primaryStage.show();
		
	}
	
	void displayText(String text){ //�۾� �������� �۾�ó�� ������ ����� �� ȣ�� �ϴ� �޼ҵ� 
		txtDisplay.appendText(text+"\n");
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
	
}
