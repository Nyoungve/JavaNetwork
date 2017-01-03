package com.NIONetworking;


import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// 와치서비스  : 디렉토리 내부에서 파일 생성, 삭제, 수정 등의 내용 변화를 감시하는데 사용된다. 
// 와치 서비스의 예 -> 에디터에서 파일을 편집하고 있을 때, 에디터 바깥에서 파일 내용을 수정하게 되면 파일 내용이 변경되었으니
//              파일을 다시 불러 올 것인지 묻는 대화상자를 띄우는 것. 
// 와치 서비스는 일반적으로 파일 변경 통지 매커니즘으로 알려져있음. 
// WatchService를 생성하려면, 다음과 같이 FileSystem의 newWatchService() 메소드를 호출하면 된다. 


public class WatchServiceExample extends Application {
	
	//WatchServiceThread 클래스 선언 
	class WatchServiceThread extends Thread{
		@Override
		public void run() {
			try {
				WatchService watchService = FileSystems.getDefault().newWatchService();
				Path directory = Paths.get("C:/Windows/Temp"); //C드라이버 Temp디렉토리에 Watch서비스등록
				directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
												StandardWatchEventKinds.ENTRY_DELETE,
												StandardWatchEventKinds.ENTRY_MODIFY);
				while(true){
					
					WatchKey watchKey = watchService.take(); //블로킹(Watchkey가 큐에 들어올 때 까지)
					List <WatchEvent<?>> list = watchKey.pollEvents(); // WatchEvent 목록 얻기
					for(WatchEvent watchEvent : list){
						//이벤트 종류 얻기 
						Kind kind = watchEvent.kind();
						//감지된 Path 얻기 
						Path path = (Path)watchEvent.kind();
						if(kind == StandardWatchEventKinds.ENTRY_CREATE){
							//생성되었을 경우, 실행할 코드
							Platform.runLater(()->textArea.appendText("파일 생성됨-> "+
											path.getFileName()+"\n"));
						}else if(kind == StandardWatchEventKinds.ENTRY_DELETE){
							//삭제되었을 경우, 실행할 코드
							Platform.runLater(()->textArea.appendText("파일 삭제됨-> "+
											path.getFileName()+"\n"));
							
						}else if(kind == StandardWatchEventKinds.ENTRY_MODIFY){
							//변경되었을 경우, 실행할 코드
							Platform.runLater(()->textArea.appendText("파일 변경됨-> "+
											path.getFileName()+"\n"));
							
						}else if(kind == StandardWatchEventKinds.OVERFLOW){
						}
						
					}
					boolean valid = watchKey.reset();
					if(!valid){break;}
				}
			} catch (Exception e) {}
		}
	}
	
	TextArea textArea;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);
		
		textArea= new TextArea();
		textArea.setEditable(false);
		root.setCenter(textArea);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("WatchServiceExample");
		primaryStage.show();
		
		//WatchServiceThread 생성 및 시작
		WatchServiceThread wst = new WatchServiceThread(); 
		wst.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
