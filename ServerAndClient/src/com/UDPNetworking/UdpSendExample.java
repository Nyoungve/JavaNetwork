package com.UDPNetworking;

//UDP ��Ʈ��ŷ�� �񿬰� ������ ���������̴�. 
// �񿬰� �������̶�, �����͸� �ְ���� �� ���� ������ ��ġ�� �ʰ�, �߽��ڰ� �Ϲ������� �����͸� �߽��ϴ� ���. 
// ��������� ���� ������ TCP ���ٴ� ���� ������ �� �� ������, ������ ������ �ŷڼ��� ��������. 
// �ŷڼ� ���� �ӵ��� �߿��� ��쿡�� UDP ���
// ������ ������ �ŷڼ��� �߿��� ���α׷������� TCP ���


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;



//�߽���
public class UdpSendExample {
	public static void main(String[] args) throws Exception{
		DatagramSocket datagramSocket = new DatagramSocket(); //DatagramSocket ����
		
		System.out.println("[�߽� ����]");
		
		for(int i=0; i<3; i++){
			String data = "�޽���" + i;
			byte[] byteArr = data.getBytes("UTF-8");
				DatagramPacket packet = new DatagramPacket( //DatagramPacket ����
				byteArr, byteArr.length,
				new InetSocketAddress("localhost",5001)
			);
				
			datagramSocket.send(packet);
			System.out.println("[���� ����Ʈ ��]: "+byteArr.length +" bytes"); //DatagramPacket ����
		}
		
		System.out.println("[�߽� ����]");
		
		datagramSocket.close();
	}
}
