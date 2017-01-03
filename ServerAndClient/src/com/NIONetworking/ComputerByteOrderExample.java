package com.NIONetworking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ComputerByteOrderExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("운영체제 종류: "+System.getProperty("os.name"));
		System.out.println("네이티브의 바이트 해석 순서: "+ByteOrder.nativeOrder()); //LITTLE_ENDIAN : 뒤쪽 바이트 부터 먼저 처리한다. 
		
		//BIG_ENDIAN은 앞쪽 바이트 부터 먼저 처리함.
		//JVM은 무조건 BIG_ENDIAN으로 동작하도록 되어 있다. 
		//운영체제와 JVM의 바이트 해석 순서가 다를 경우에는 JVM이 운영체제와 데이터를 교환할 때 자동적으로 처리해주기 때문에 문제는 없다. 
		//하지만 directBuffer일 경우에는 운영체제의 native I/O를 사용하므로 운영체제의 기본 해석 순서로 JVM의 해석순서를 맞추는 것이 성능에 도움이 된다. 
		//다음과 같이!
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100).order(ByteOrder.nativeOrder()); 
		System.out.println(byteBuffer);
	}

}
