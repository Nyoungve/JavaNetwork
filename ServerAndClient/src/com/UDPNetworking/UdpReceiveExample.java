package com.UDPNetworking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

//������
public class UdpReceiveExample {
	public static void main(String[] args) throws Exception{
		DatagramSocket datagramSocket = new DatagramSocket(5001); //5001�� ��Ʈ���� �����ϴ� DatagramSocket ����
		
		Thread thread = new Thread(){
			@Override
			public void run(){
				System.out.println("[���� ����]");
				try {
					while(true){
						DatagramPacket packet = new DatagramPacket(new byte[100], 100); 
						datagramSocket.receive(packet); //datagramPacket ����
						
						String data = new String(packet.getData(),0,packet.getLength(),"UTF-8");
						System.out.println("[���� ����: "+ packet.getSocketAddress()+"] "+data);
					}
				} catch (Exception e) {
					System.out.println("[���� ����]");
				}
			}
		};
		thread.start();
		
		Thread.sleep(10000);
		datagramSocket.close();
	}
}