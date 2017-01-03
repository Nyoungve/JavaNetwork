package com.NIONetworking;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

//allocateDirect() �޼ҵ�� JVM �� �޸� �ٱ� ��, �� �ü���� �����ϴ� �޸𸮿� ���̷�Ʈ ���۸� �����Ѵ�. 

public class DirectBufferCapacityExample {
	public static void main(String[] args) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
		System.out.println("����뷮: "+byteBuffer.capacity() +" ����Ʈ");
		
		CharBuffer charBuffer = ByteBuffer.allocateDirect(100).asCharBuffer(); //2byte ũ�⸦ ������ ����
		System.out.println("����뷮: "+charBuffer.capacity() +" ����");
		
		IntBuffer intBuffer = ByteBuffer.allocateDirect(100).asIntBuffer(); //4byte ũ�⸦ ������ ���� 
		System.out.println("����뷮: "+intBuffer.capacity() + " ����");
	}
}
