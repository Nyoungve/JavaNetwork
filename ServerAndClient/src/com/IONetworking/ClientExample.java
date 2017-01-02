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

public class ClientExample extends Application{ //JavaFX ���� Ŭ������ ����� ���� Application�� ����Ѵ�.
	
	//Ŭ���̾�Ʈ ����� ���� Socket �ʵ� ����
	Socket socket;
	
	void startClient(){ //��������ڵ� [start]��ư�� Ŭ���ϸ� ȣ��ȴ�.
		
		Thread thread = new Thread(){ //������ ����
			
			@Override
			public void run() {
				try {
					//���ϻ��� �� �����û 
					socket = new Socket(); 
					socket.connect(new InetSocketAddress("192.168.56.1", 5001)); //������ �����û //���ε� IP��ȣ, port��ȣ
					
					Platform.runLater(()->{ //UI�� �������ִ� UI�� ���������
						displayText("[������ ����Ϸ�: "+socket.getRemoteSocketAddress()+"]"); //����Ǿ����� �˷���
						btnConn.setText("stop"); //start ��ư�� ���ڸ� stop���� �ٲ۴�.
						btnSend.setDisable(false); //send��ư�� Ȱ��ȭ ��Ų��. 
					});
				} catch (Exception e) { //���ܰ� �߻��ϸ� 
					Platform.runLater(()->displayText("[���� ��� �ȵ�]")); //�����˸��� ���.
					if(!socket.isClosed()){ stopClient(); } // ������ �������� ������ stopClient�޼ҵ� ȣ��
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
			if(socket!=null && !socket.isClosed()){ //socket�ʵ尡 null�̾ƴϰ�, ���� �������� ���� ��� socket�� �ݴ´�.
				socket.close();
			}
		} catch (Exception e) { }
	}
	
	void receive(){ //�������� ���� �����͸� �޴´�.
		while(true){
			try {
				byte[] byteArr = new byte[100]; //���� �����͸� ������ ���̰� 100�� ����Ʈ �迭�� �����Ѵ�.
				InputStream inputStream = socket.getInputStream(); //�������κ��� inputStream�� ��´�.
				
				//������ ������������ �������� ��� IOException �߻�
				int readByteCount = inputStream.read(byteArr); //�����͹ޱ�
				
				//������ ���������� Socket�� close()�� ȣ������ ��� 
				if(readByteCount==-1 ){throw new IOException();}
				
				String data = new String(byteArr, 0, readByteCount, "UTF-8"); //���ڿ��� ��ȯ
				
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
					byte[] byteArr = data.getBytes("UTF-8"); //���ڵ��� ����Ʈ�迭 ���
					OutputStream outputStream = socket.getOutputStream(); //���Ͽ��� ��½�Ʈ�� ���
					outputStream.write(byteArr); ///����Ʈ�迭 �Ű������� �ؼ� write() �޼ҵ带 ȣ��
					outputStream.flush(); //��½�Ʈ�� ���� �۹��� ������ ��쵵�� flush()�� ȣ���Ѵ�. 
					Platform.runLater(()->displayText("[������ �Ϸ�]")); 
				} catch (Exception e) {
					Platform.runLater(()->displayText("[���� ��� �ȵ�]"));
					stopClient();
				}
			}
		};
		thread.start(); //������ ����
		System.out.println(333);
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
