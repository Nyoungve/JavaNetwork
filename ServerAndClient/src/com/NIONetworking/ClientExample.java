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

//!!!! TCP ���ŷ ��� ��� Ŭ���̾�Ʈ �ڵ�  !!!
//Ŭ���̾�Ʈ�� ������ ���� ��û�� �� ������ java.nio.channels.SocketChannel�� �̿��Ѵ�. 
//���� ���� ��û�� connect()�޼ҵ带 ȣ��
//connect() �޼ҵ�� ������ ������ �� ������ ���ŷ �ȴ�. 
//���ŷ �ǹǷ�, UI�� �̺�Ʈ�� ó���ϴ� �����忡�� connect()�� ȣ������ �ʵ��� �Ѵ�. 
/*public class ClientExample {
	public static void main(String[] args) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true); // ���ŷ��� ����� ǥ�� 
			
			System.out.println("[���� ��û]");
			
			socketChannel.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[���� ����!]");
		} catch (Exception e) {}
	}
}*/


//02. ����ä�� ������ ���
//Ŭ���̾�Ʈ�� ���� ��û(connect())�ϰ� ������ ���� ����(accept())�ߴٸ�, ���� Socket Channel ��ü�� 
//read(), write()�޼ҵ带 ȣ���ؼ� ������ ����� �Ҽ� �ִ�. �� �޼ҵ���� ��� ���۸� �̿��ϱ⶧���� ���۷� �а�,���� �۾��� �ؾ��Ѵ�.
//read() �޼ҵ带 ȣ���ϸ� ������ �����͸� ������ �������� ���ŷ ��. 

//read() �޼ҵ尡 ���ŷ �����ǰ� ���ϵǴ� ���� ���� ��������
//1. ������ �����͸� ���� ��� ���� -> ���ϰ��� ���� ����Ʈ �� 
//2. ������ ���������� SocketChannel�� close()�� ȣ���� ��� ���ŷ ���� -> ���ϰ� -1
//3. ������ ������������ �����ؼ� ���ŷ �����Ǵ� ��� -> ���ϰ� IOException �߻� 

/*public class ClientExample {
	public static void main(String[] args) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true); // ���ŷ��� ����� ǥ�� 
			
			System.out.println("[���� ��û]");
			
			socketChannel.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[���� ����!]");
			
			//�����͸� �ְ���� ����Ʈ ���� ����
			ByteBuffer byteBuffer = null;
			Charset charset = Charset.forName("UTF-8");
			
			//������ ������ ������ 
			byteBuffer = charset.encode("Hello Server");
			socketChannel.write(byteBuffer);
			System.out.println("[������ ������ ����]");
			
			//�����κ��� ������ �޾ƿ��� 
			byteBuffer = ByteBuffer.allocate(100); //bytebuffer���� 100 �Ҵ�
			int byteCount = socketChannel.read(byteBuffer); //������ ������������ �������� ��� IOException �߻�
			
			byteBuffer.flip(); //��ġ�Ӽ� �����ϴ� Buffer�߻�Ŭ������ flip�޼ҵ�
			//flip()�� limit�� position���� position�� 0�ε����� �̵�
			String message = charset.decode(byteBuffer).toString();
			System.out.println("[������ �ޱ� ����]: "+ message) ;	
			
		} catch (Exception e) {}
		
		if(socketChannel.isOpen()){
			try{
				socketChannel.close();
			}catch(IOException e1){ }
		}
	}
}
*/

//03. TCP ��� ���� ä�� ä�� Ŭ���̾�Ʈ �����ϱ� !!
public class ClientExample extends Application{ //JavaFX ���� Ŭ������ ����� ���� Application�� ����Ѵ�.
	
	//Ŭ���̾�Ʈ ����� ���� ����ä�� �ʵ� ����
	SocketChannel socketChannel;
	
	void startClient(){ //��������ڵ� [start]��ư�� Ŭ���ϸ� ȣ��ȴ�.
		
		Thread thread = new Thread(){ //������ ����
			
			@Override
			public void run() {
				try {
					//���ϻ��� �� �����û 
					socketChannel = SocketChannel.open(); 
					socketChannel.configureBlocking(true); //���ŷ!
					socketChannel.connect(new InetSocketAddress("localhost", 5001)); //������ �����û //���ε� IP��ȣ, port��ȣ
					
					Platform.runLater(()->{ //UI�� �������ִ� UI�� ���������
						try {
							displayText("[������ ����Ϸ�: " + socketChannel.getRemoteAddress() + "]"); //����Ǿ����� �˷���
							btnConn.setText("stop"); //start ��ư�� ���ڸ� stop���� �ٲ۴�.
							btnSend.setDisable(false); //send��ư�� Ȱ��ȭ ��Ų��. 
						} catch (Exception e) {}
						
					});
				} catch (Exception e) { //���ܰ� �߻��ϸ� 
					Platform.runLater(()->displayText("[���� ��� �ȵ�]")); //�����˸��� ���.
					if(socketChannel.isOpen()){ stopClient(); } // ����ä���� ���������� stopClient()ȣ��
					return; //return �����ؼ� �۾� ����
				}
				receive(); //�������� ���� ������ �ޱ� (���ܰ� �߻����� ������ �޼ҵ� ȣ��.)
			}
		};
		thread.start(); //�۾� ������ ����
	}
	
	void stopClient(){ //��������ڵ� [stop]��ư�� Ŭ���ϸ� ȣ��ȴ�.
		try {
			Platform.runLater(()->{
				displayText("[�������]"); 
				btnConn.setText("start"); //��ư�� ���ڸ� start�� �ٲ۴�. 
				btnSend.setDisable(true); //send��ư�� ��Ȱ��ȭ ��Ų��.
			});
			if(socketChannel!=null && socketChannel.isOpen()){ //socket�ʵ尡 null�̾ƴϰ�, ���� �������� ���� ��� socket�� �ݴ´�.
				socketChannel.close(); //�������
			}
		} catch (Exception e) { }
	}
	
	void receive(){ //�������� ���� �����͸� �޴´�.
		while(true){
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(100);
				
				//������ ������������ �������� ��� IOException �߻�
				int readByteCount = socketChannel.read(byteBuffer); //�����͹ޱ�
				
				//������ ���������� Socket�� close()�� ȣ������ ��� 
				if(readByteCount==-1 ){throw new IOException();}
				
				//���ڿ��� ��ȯ
				byteBuffer.flip();
				Charset charset = Charset.forName("UTF-8");
				String data = charset.decode(byteBuffer).toString();
				Platform.runLater(()->displayText("[�ޱ�Ϸ�] "+ data));
			} catch (Exception e) { //���ܰ� �߻����� ��� 
				Platform.runLater(()->displayText("[���� ��� �ȵ�]"));
				stopClient();
				break;  //���ѷ����� �������� break;
			}
		}
	}
	
	
	void send(String data){//[send]��ư�� Ŭ���ϸ� ȣ�� , ������ �����͸� ������. 
		Thread thread = new Thread(){ //�����͸� ������ ������ ���ο� �۾� ������ ����
			@Override
			public void run() { //run�� ������ �Ѵ�. 
				try {
					Charset charset = Charset.forName("UTF-8");
					ByteBuffer byteBuffer = charset.encode(data);
					socketChannel.write(byteBuffer); //������ ������ ������ 
					Platform.runLater(()->displayText("[������ �Ϸ�]")); 
				} catch (Exception e) {
					Platform.runLater(()->displayText("[���� ��� �ȵ�]"));
					stopClient();
				}
			}
		};
		thread.start(); //������ ����
		
	}
	
	////UI �����ڵ� : ���̾ƿ��� �����ϰ�, ClientExample�� �����Ų��. 
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
		
		//����Ű ������ txtInput ���� �ؽ�Ʈ ������ ���۽�Ű��.
		txtInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke){
				//������ �ڵ� 
				if(ke.getCode().equals(KeyCode.ENTER)){
					send(txtInput.getText());
				}
			}
		});
		
		btnConn = new Button("start");
		btnConn.setPrefSize(60, 30);
		
		//start�� stop��ư�� Ŭ������ �� �̺�Ʈ ó�� �ڵ�
		btnConn.setOnAction(e->{
			if(btnConn.getText().equals("start")){ //��ư�� �ؽ�Ʈ�� start�϶� 
				startClient();//startClient�޼ҵ� ����
			}else if(btnConn.getText().equals("stop")){ //��ư�� �ؽ�Ʈ�� stop�� �� 
				stopClient(); //stopClient�޼ҵ� ����
			}
		});
	
		btnSend = new Button("send");
		btnSend.setPrefSize(60 , 30);
		btnSend.setDisable(true); //send��ư �⺻������ ��Ȱ��ȭ ���� 
		btnSend.setOnAction(e->send(txtInput.getText())); //send ��ư�� Ŭ������ �� �̺�Ʈ ó���ڵ�
		
		bottom.setCenter(txtInput);
		bottom.setLeft(btnConn);
		bottom.setRight(btnSend);
		
		root.setBottom(bottom);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString()); //css ����
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		primaryStage.setOnCloseRequest(event->stopClient()); // ������ ���� ��� ��ư Ŭ������ �� �̺�Ʈ ó�� �ڵ�
	
		primaryStage.show();
	}
	
	void displayText(String text){ //TextArea�� ���ڿ��� �߰��ϴ� �޼ҵ�  
		txtDisplay.appendText(text+"\n");
		this.txtInput.setText(""); //����ġ�� ���� ���� ġ�� �� �ٽ� �������� �ϱ�.
	}
	
	public static void main(String[] args){
		launch(args);
	}
}

