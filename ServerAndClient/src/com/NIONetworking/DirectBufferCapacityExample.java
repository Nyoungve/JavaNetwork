package com.NIONetworking;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

//allocateDirect() 메소드는 JVM 힙 메모리 바깥 쪽, 즉 운영체제가 관리하는 메모리에 다이렉트 버퍼를 생성한다. 

public class DirectBufferCapacityExample {
	public static void main(String[] args) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
		System.out.println("저장용량: "+byteBuffer.capacity() +" 바이트");
		
		CharBuffer charBuffer = ByteBuffer.allocateDirect(100).asCharBuffer(); //2byte 크기를 가지고 있음
		System.out.println("저장용량: "+charBuffer.capacity() +" 문자");
		
		IntBuffer intBuffer = ByteBuffer.allocateDirect(100).asIntBuffer(); //4byte 크기를 가지고 있음 
		System.out.println("저장용량: "+intBuffer.capacity() + " 정수");
	}
}
