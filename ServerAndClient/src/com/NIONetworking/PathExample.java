package com.NIONetworking;

//Path인터페이스? 
// 상대경로를 이용해서 소스파일에 대한 Path객체를 얻고 파일명, 부모 디렉토리 명, 중첩경로수, 경로상에 있는 모든 디렉토리 출력하기.
import java.nio.file.Path;  //제일먼저 살펴봐야할 Path인터페이스
import java.nio.file.Paths; // Path구현객체를 얻기위해 필요한 Paths클래스의 정적메소드 get()메소드 호출
import java.util.Iterator;

public class PathExample {

	public static void main(String[] args) throws Exception {
		
		//get메소드의 매개값은 파일의 경로. 문자열로 지정할 수도 있고, URI 객체로 지정할 수도 있다.
		Path path = Paths.get("src/com/NIONetworking/PathExample.java");
		System.out.println("[파일명] "+path.getFileName()); //부모 경로를 제외한 파일 또는 디렉토리 이름만 가진 Path리턴
		System.out.println("[부모 디렉토리명]: "+ path.getParent().getFileName());
		System.out.println("중첩 경로수: "+ path.getNameCount()); //중첩경로수!!
		
		System.out.println();
		for(int i =0; i<path.getNameCount(); i++){ //중첩경로수만큼 
			System.out.println(path.getName(i)); // getName(int index)로 이름얻기 
		}
		
		System.out.println();
		//경로에 있는 모든 디렉토리와 파일을 Path 객체로 생성하고 반복자를 리턴
		Iterator<Path> iterator = path.iterator();
		
		while(iterator.hasNext()){
			Path temp = iterator.next();
			System.out.println(temp.getFileName()); //파일이름을 얻는다. 
		}
		
	}
}
