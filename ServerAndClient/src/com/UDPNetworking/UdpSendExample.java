package com.UDPNetworking;

//UDP 네트워킹은 비연결 지향적 프로토콜이다. 
// 비연결 지향적이란, 데이터를 주고받을 때 연결 절차를 거치지 않고, 발신자가 일방적으로 데이터를 발신하는 방식. 
// 연결과정이 없기 때문에 TCP 보다는 빠른 전송을 할 수 있지만, 데이터 전달의 신뢰성은 떨어진다. 
// 신뢰성 보다 속도가 중요한 경우에는 UDP 사용
// 데이터 전달의 신뢰성이 중요한 프로그램에서는 TCP 사용


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;



//발신자
public class UdpSendExample {
	public static void main(String[] args) throws Exception{
		DatagramSocket datagramSocket = new DatagramSocket(); //DatagramSocket 생성
		
		System.out.println("[발신 시작]");
		
		for(int i=0; i<3; i++){
			String data = "메시지" + i;
			byte[] byteArr = data.getBytes("UTF-8");
				DatagramPacket packet = new DatagramPacket( //DatagramPacket 생성
				byteArr, byteArr.length,
				new InetSocketAddress("localhost",5001)
			);
				
			datagramSocket.send(packet);
			System.out.println("[보낸 바이트 수]: "+byteArr.length +" bytes"); //DatagramPacket 전송
		}
		
		System.out.println("[발신 종료]");
		
		datagramSocket.close();
	}
}
