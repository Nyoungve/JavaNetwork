package com.NIONetworking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ComputerByteOrderExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("�ü�� ����: "+System.getProperty("os.name"));
		System.out.println("����Ƽ���� ����Ʈ �ؼ� ����: "+ByteOrder.nativeOrder()); //LITTLE_ENDIAN : ���� ����Ʈ ���� ���� ó���Ѵ�. 
		
		//BIG_ENDIAN�� ���� ����Ʈ ���� ���� ó����.
		//JVM�� ������ BIG_ENDIAN���� �����ϵ��� �Ǿ� �ִ�. 
		//�ü���� JVM�� ����Ʈ �ؼ� ������ �ٸ� ��쿡�� JVM�� �ü���� �����͸� ��ȯ�� �� �ڵ������� ó�����ֱ� ������ ������ ����. 
		//������ directBuffer�� ��쿡�� �ü���� native I/O�� ����ϹǷ� �ü���� �⺻ �ؼ� ������ JVM�� �ؼ������� ���ߴ� ���� ���ɿ� ������ �ȴ�. 
		//������ ����!
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100).order(ByteOrder.nativeOrder()); 
		System.out.println(byteBuffer);
	}

}
