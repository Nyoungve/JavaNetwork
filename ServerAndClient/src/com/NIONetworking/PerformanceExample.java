package com.NIONetworking;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class PerformanceExample {
	public static void main(String[] args) throws Exception {
		Path from = Paths.get("src/com/NIONetworking/1.jpg");
		Path to1 = Paths.get("src/com/NIONetworking/2.jpg");
		Path to2 = Paths.get("src/com/NIONetworking/3.jpg");
		
		long size = Files.size(from); //이미지파일 1.jpg의 사이즈 
		
		FileChannel fileChannel_from = FileChannel.open(from); 
		FileChannel fileChannel_to1 = FileChannel.open(to1, 
									EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
		FileChannel fileChannel_to2 = FileChannel.open(to2, 
				EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
		
		ByteBuffer nonDirectBuffer = ByteBuffer.allocate((int) size); //넌다이렉트버퍼 생성 : allocate()메소드는 JVM 힙메모리에 넌다이렉트 버퍼를 생성.
		ByteBuffer directBuffer = ByteBuffer.allocateDirect((int)size); //다이렉트버퍼 생성
		
		
		long start, end;
		
		
		//넌다이렉트 버퍼 테스트 
		start = System.nanoTime();
	
		for(int i=0; i<100; i++){
			fileChannel_from.read(nonDirectBuffer);
			nonDirectBuffer.flip();
			fileChannel_to1.write(nonDirectBuffer);
			nonDirectBuffer.clear();
		}
		
		end = System.nanoTime();
		System.out.println("넌다이렉트:\t"+(end-start)+"ns");
		
		///파일의 위치를 다시 0으로 설정 (초기화)
		fileChannel_from.position(0);
		
		//다이렉트 버퍼 테스트 
				start = System.nanoTime();
			
				for(int i=0; i<100; i++){
					fileChannel_from.read(directBuffer);
					directBuffer.flip();
					fileChannel_to1.write(directBuffer);
					directBuffer.clear();
				}
				
				end = System.nanoTime();
				System.out.println("다이렉트:\t"+(end-start)+" ns");
				
				fileChannel_from.close();
				fileChannel_to1.close();
				fileChannel_to2.close();
		
				/*
				 * 결과
				넌다이렉트:	1458007ns
				다이렉트:	584172 ns --> 훨씬 빠름
				*/
	}
}
