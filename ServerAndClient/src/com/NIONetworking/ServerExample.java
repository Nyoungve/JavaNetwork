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

// NIO �� New Input/Output �� ���� 
// IO�� ��Ʈ�� ����̴�. (���Ϲ���, ��.��� ��Ʈ������ ����) Input/Output Stream�� �����ؾ��Ѵ�. 
// NIO�� Channel ���. ä���� ��Ʈ���� �޸� ��������� �Է°� ����� �����ϴ�. (��.����� ���� ������ ä���� ���� �ʿ䰡 ����. )
// IO�� ��½�Ʈ���� 1byte�� ���� �Է½�Ʈ���� 1byte�� �д´�. �̷� �ý����� ��ü�� ������.
// �׷���, �۹��� ����ؼ� �������� ����Ʈ�� �Ѳ����� �Է¹ް� ����ϴ� ���� ���� ������ ��. 
// IO������ BufferedInputStream/ BufferedOutputStream �� �����ؼ� ����Ѵ�.
// NIO ������ �⺻������ ���۸� ����ؼ� ������� �ϱ� ������ IO���ٴ� ����� ������ ����. 
// ä���� ���ۿ� ����� �����͸� ����ϰ�, �Էµ� �����͸� ���ۿ� �����Ѵ�. 
// NIO�� ���ŷ�� �ͺ��ŷ�� Ư¡�� ��� ������ �ִ�. ���ŷ(������)
// NIO�� ���ŷ�� IO ���ŷ�� �������� �����带 ���ͷ�Ʈ�����ν� �������� ���� �ִ�. 
// NIO�� �ͺ��ŷ�� ����� �۾��غ� �Ϸ�� ä�θ� �����ؼ� �۾������尡 ó���� -> �۾������尡 ���ŷ���� �ʴ´�. 
// NIO �ͺ��ŷ�� �ٽɰ�ü�� Multiplexor�� Selector �̴�. 
// �����ʹ� �������� ä�� �߿��� �غ� �Ϸ�� ä���� �����ϴ� ����� �������ش�. 


//NIO �� �����ϴ� ���� (��Ʈ��ũ ���α׷� ���� �ÿ�)
// ��Ư�� �ټ��� Ŭ���̾�Ʈ ���� �Ǵ� ��Ƽ ���ϵ��� �ͺ��ŷ�̳� �񵿱�� ó���� �� �ֱ� ������ ������ ������ ������ ���ϰ�, �����带 ȿ�������� �����Ѵٴ� ū������ ����.
// �ü���� ���۸� �̿��� ������� �����ϱ⿡ ����� ������ ���ȴ�. 
// ���� Ŭ���̾�Ʈ ���� ����, �ϳ��� ����� �۾��� ���� �ɸ��� �ʴ� ��쿡 ����ϴ� ���� ����. 

//!!!TCP ���ŷ ä��!!!!
/*
NIO�� �̿��ؼ� TCP ����, Ŭ���̾�Ʈ ���ø����̼��� �����Ϸ��� ���ŷ, �ͺ��ŷ, �񵿱� ���� ��� �� �ϳ��� �����ؾ� �Ѵ�. 
*/
/*public class ServerExample {
	public static void main(String[] args) {
		//���������� ���� �켱 ��������ä���� ��ü�� ��´�. 
		ServerSocketChannel serverSocketChannel = null; // java.nio.channels.ServerSocketChannel
		
		try {
			serverSocketChannel = ServerSocketChannel.open(); //��������ä���� ���� �޼ҵ��� open()���� ����
			serverSocketChannel.configureBlocking(true); //�⺻������ ���ŷ������� ����������, ��������� ���ŷ���� ��������(�ͺ��ŷ�� ����)
			serverSocketChannel.bind(new InetSocketAddress(5001)); //��Ʈ�� ���ε� 
			while(true){
				System.out.println("[���� ��ٸ�]");
				SocketChannel socketChannel = serverSocketChannel.accept(); //java.nio.channels.SocketChannel Ŭ���̾�Ʈ ���� ����
				//accept�޼ҵ�� Ŭ���̾�Ʈ�� ���� ��û�ϱ� ������ ���ŷ�Ǳ� ������ UI�� �̺�Ʈó���ϴ� �����忡���� accept�޼ҵ带 ȣ������ �ʵ��� �Ѵ�. 
				InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
				System.out.println("[���� ������]" + isa.getHostName()); //����� Ŭ���̾�Ʈ�� ip �����Ѵ�.
			
			}
		} catch (Exception e) { }
		
		if(serverSocketChannel.isOpen()){ //ServerSocketChannel�� �������� ���
			try {
				serverSocketChannel.close(); //ServerSocketChannel �ݱ�
			} catch (IOException e) {}
		}
		
		
	}
}
*/



//02. ����ä�� ������ ���
/*
public class ServerExample {
	public static void main(String[] args) {
		//���������� ���� �켱 ��������ä���� ��ü�� ��´�. 
		ServerSocketChannel serverSocketChannel = null; // java.nio.channels.ServerSocketChannel
		
		try {
			serverSocketChannel = ServerSocketChannel.open(); //��������ä���� ���� �޼ҵ��� open()���� ����
			serverSocketChannel.configureBlocking(true); //�⺻������ ���ŷ������� ����������, ��������� ���ŷ���� ��������(�ͺ��ŷ�� ����)
			serverSocketChannel.bind(new InetSocketAddress(5001)); //��Ʈ�� ���ε� 
			while(true){
				System.out.println("[���� ��ٸ�]");
				SocketChannel socketChannel = serverSocketChannel.accept(); //java.nio.channels.SocketChannel Ŭ���̾�Ʈ ���� ����
				//accept�޼ҵ�� Ŭ���̾�Ʈ�� ���� ��û�ϱ� ������ ���ŷ�Ǳ� ������ UI�� �̺�Ʈó���ϴ� �����忡���� accept�޼ҵ带 ȣ������ �ʵ��� �Ѵ�. 
				InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
				System.out.println("[���� ������]" + isa.getHostName()); //����� Ŭ���̾�Ʈ�� ip �����Ѵ�.
			
				//���۸� �̿��� ������ �ְ�ޱ� 
				ByteBuffer byteBuffer = null;
				Charset charset = Charset.forName("UTF-8");
				
				//Ŭ���̾�Ʈ�κ��� ������ �޾ƿ���
				byteBuffer = ByteBuffer.allocate(100);
				int byteCount = socketChannel.read(byteBuffer);
				byteBuffer.flip();
				String message = charset.decode(byteBuffer).toString();
				System.out.println("[������ �ޱ� ����]: "+ message);
				
				//Ŭ���̾�Ʈ���� ������ ������ 
				byteBuffer = charset.encode("Hello Client");
				socketChannel.write(byteBuffer);
				System.out.println("[������ ������ ����]");
			}
		} catch (Exception e) { }
		
		if(serverSocketChannel.isOpen()){ //ServerSocketChannel�� �������� ���
			try {
				serverSocketChannel.close(); //ServerSocketChannel �ݱ�
			} catch (IOException e) {}
		}
		
		
	}
}
*/


//03. TCP ��� ���� ä�� ä�� ���� �����ϱ� !!
//TCP �ǹ� ! Transmission Control Protocol �� ���Ӹ� 
//������ Ŭ���̾�Ʈ ���� �����͸� �ŷڼ��ְ� �����ϱ� ���� ������� ��������. 
//�����ʹ� ��Ʈ��ũ ���θ� ���� ���޵Ǵ� �������� �սǵǰų� ������ �ڹٱ� �� �ִµ� TCP�� �ս��� �˻��س��� �̸� �����ϰ� ������ ������ �� �� �ֵ��� ���ش�. 
//�����͸� �����ϱ� ���� ������ ������ ���� ������ ����� �������� ����������.

public class ServerExample extends Application{ //JavaFX ���� Ŭ������ ����� ���� Application�� ��ӹ޴´�.
	
	ExecutorService executorService; //������Ǯ�� ExecutorService �ʵ弱��
	ServerSocketChannel serverSocketChannel; //��������ä�� �ʵ� ���� : Ŭ���̾�Ʈ ���� ����
	List<Client> connections = new Vector<Client>(); //����Ŭ���̾�Ʈ������ ���� ����Ʈ�ۼ�
	
	void startServer(){ 
		//���� ���� �ڵ��ۼ�
		executorService = Executors.newFixedThreadPool(
				100 //CPU �ھ��� ���� �°� �����带 �����ؼ� �����ϴ� ExecutorService�� ����
		);
		System.out.println(Runtime.getRuntime().availableProcessors());
		
		//5001����Ʈ���� Ŭ���̾�Ʈ�� ������ �����ϴ� ServerSocketChannel�� ����
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress(5001));
		} catch (Exception e) {
			if(serverSocketChannel.isOpen()){ stopServer(); }
			return;
		}
		
		//������� �۾��� Runnable��ü�� ����� ������Ǯ�� �۾� ������� �����Ű�� �ڵ� 
		//ServerSocketChannel�� �ݺ��ؼ� Ŭ���̾�Ʈ �����û�� ��ٷ����ϹǷ� ������Ǯ�� �۾�������󿡼� accept()�޼ҵ带 �ݺ������� ȣ�����־����
		
		Runnable runnable = new Runnable(){
			@Override
			public void run() {
				Platform.runLater(()->{
					displayText("[���� ����]");
					btnStartStop.setText("stop");
				});
				while(true){
					try {
						SocketChannel socketChannel = serverSocketChannel.accept(); // ������� 
						String message = "[�������: ]"+ socketChannel.getRemoteAddress() +
										": "+Thread.currentThread().getName() +"]"; 
						Platform.runLater(()->displayText(message));
						
						Client client = new Client(socketChannel); 
						connections.add(client); //client ��ü����
						
						Platform.runLater(()->displayText("���� ����: " + connections.size()+"]"));
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
		//���� ���� �ڵ��ۼ�
		try {
			//connections �÷������κ��� �ݺ��ڸ� ����. 
			Iterator<Client> iterator = connections.iterator();
			
			//while ������ �ݺ��ڸ� �ݺ��ϸ鼭 Client�� �ϳ��� ��´�. Client�� �������ִ� SocketChannel�� �ݰ� connections �÷��ǿ��� Client�� �����Ѵ�.
			while(iterator.hasNext()){
				Client client = iterator.next();
				client.socketChannel.close();
				iterator.remove();
			}
			
			//��������ä����  null�� �ƴϰ�, ���������� ��������ä���� �ݴ´�. 
			if(serverSocketChannel!=null && serverSocketChannel.isOpen()){
				serverSocketChannel.close();
			}
			
			//�����庴��ó����ü�� null�̾ƴϰ�, ������°� �ƴϸ� excutorService�� �����Ѵ�.
			if(executorService!=null && !executorService.isShutdown()){
				executorService.shutdown();
			}
			
			//�۾�������� UI�� �������� ���ϹǷ� Platform.runLater() �� ���Ǿ���. 
			Platform.runLater(()->{
				displayText("[���� ����]");
				btnStartStop.setText("start");
			});
			
		} catch (Exception e) {}
	}
	
	//Client�� ServerExample�� ���� Ŭ������ ����
	class Client{ 
		//����� Ŭ���̾�Ʈ ǥ��
		//������ ��� �ڵ��ۼ�
		
		SocketChannel socketChannel; //��ſ� ����ä���� �ʵ�� ����!
		Client(SocketChannel socketChannel){ //������ ����! �Ű������� ����ä���ʵ带 �ʱ�ȭ�ϰ� �����͹޴� �Լ��� ȣ��!
			this.socketChannel = socketChannel;
			receive();
		}
		void receive(){
			//������ �ޱ� �ڵ� (Ŭ���̾�Ʈ�κ��� �����͹޴� �۾��� Runnable�� �����Ѵ�) 
			Runnable runnable = new Runnable(){
				@Override
				public void run() {
					while(true){ //�۾��� ���ѹݺ��Ѵ�.
						try {
							ByteBuffer byteBuffer = ByteBuffer.allocate(100); //100���� ����Ʈ�� ������ �� �ִ� ����Ʈ���� ����
							
							//Ŭ���̾�Ʈ�� ������ ���Ḧ ���� ��� IOException�߻�
							int readByteCount = socketChannel.read(byteBuffer); //����ä���� read() �޼ҵ� ȣ�� . 
							
							//Ŭ���̾�Ʈ�� ���������� SocketChannel�� close()�� ȣ�� ���� ��� 
							if(readByteCount == -1){
								throw new IOException();
							}
							
							String message = "[��û ó��: "+ socketChannel.getRemoteAddress() 
										+ ": "+ Thread.currentThread().getName() + "] ";
							
							Platform.runLater(()->displayText(message));
							
							//���ڿ��� ��ȯ
							byteBuffer.flip(); //���������� �����͸� �ް� �Ǹ�, ���� �����Ͱ� ����� ����Ʈ ������ flip()�޼ҵ带 ȣ���ؼ� ��ġ �Ӽ����� ����.
							Charset charset = Charset.forName("UTF-8");
							String data = charset.decode(byteBuffer).toString();
			
							//��� Ŭ���̾�Ʈ���� ����
							for(Client client : connections){
								client.send(data);
							}
						
						} catch (IOException e) {
							try{
								connections.remove(Client.this);
								String message = "[Ŭ���̾�Ʈ ��� �ȵ�: "+
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
			//�����͸� Ŭ���̾�Ʈ�� ������ �۾��� runnable�� �����Ѵ�.
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					try {
						Charset charset = Charset.forName("UTF-8");
						ByteBuffer byteBuffer = charset.encode(data);
						socketChannel.write(byteBuffer); //Ŭ���̾�Ʈ�� ������(write()�ϱ�)
					} catch (Exception e) {
						try {
							String message ="[Ŭ���̾�Ʈ ��� �ȵ�: " +
											socketChannel.getRemoteAddress() +": "+
											Thread.currentThread().getName() +"] ";
							Platform.runLater(()->displayText(message));
							connections.remove(Client.this); //connections�÷��ǿ��� ���ܰ� �߻��� Client�� �����Ѵ�. 
							socketChannel.close(); //���� ä���� �ݴ´�.
						} catch (Exception e2) {}
					}
				}
			};
			executorService.submit(runnable); //������ Ǯ���� ó��.
		}
		
	}
	
	/////////////////////////////////////////
	//UI ���� �ڵ� (���̾ƿ� ����, ServerExample�� �����Ų��.)
	TextArea txtDisplay; //�ؽ�Ʈ ������ü 
	Button btnStartStop; //��ư��ü 
	
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
		primaryStage.setOnCloseRequest(event->stopServer()); //������ ������� �ݱ��ư Ŭ������ �� �̺�Ʈó��
		primaryStage.show();
		
	}
	//�۾��������� �۾� ó�� ������ ����� �� ȣ���ϴ� �޼ҵ� 
	void displayText(String text){ 
		txtDisplay.appendText(text+"\n");
	}
	public static void main(String[] args) {
		launch(args);
	}
}

