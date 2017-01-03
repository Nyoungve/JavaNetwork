package com.NIONetworking;
// 파일의 속성을 읽고 출력하는 예제
import java.nio.file.Files; //파일과 디렉토리의 생성 및 삭제 & 이들의 속성을 읽는 메소드를 제공.
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileExample {
	public static void main(String[] args) throws Exception{
		Path path = Paths.get("src/com/NIONetworking/FileExample.java");
		System.out.println("디렉토리 여부: "+ Files.isDirectory(path));
		System.out.println("파일 여부: " + Files.isRegularFile(path));
		System.out.println("마지막 수정 시간: " + Files.getLastModifiedTime(path));
		System.out.println("파일 크기: "+  Files.size(path));
		System.out.println("소유자: "+Files.getOwner(path).getName());
		System.out.println("숨김 파일 여부: "+ Files.isHidden(path));
		System.out.println("읽기 가능 여부: "+ Files.isReadable(path));
		System.out.println("쓰기 가능 여부: "+ Files.isWritable(path));
				
	}
}
