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
		
		long size = Files.size(from); //�̹������� 1.jpg�� ������ 
		
		FileChannel fileChannel_from = FileChannel.open(from); 
		FileChannel fileChannel_to1 = FileChannel.open(to1, 
									EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
		FileChannel fileChannel_to2 = FileChannel.open(to2, 
				EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
		
		ByteBuffer nonDirectBuffer = ByteBuffer.allocate((int) size); //�ʹ��̷�Ʈ���� ���� : allocate()�޼ҵ�� JVM ���޸𸮿� �ʹ��̷�Ʈ ���۸� ����.
		ByteBuffer directBuffer = ByteBuffer.allocateDirect((int)size); //���̷�Ʈ���� ����
		
		
		long start, end;
		
		
		//�ʹ��̷�Ʈ ���� �׽�Ʈ 
		start = System.nanoTime();
	
		for(int i=0; i<100; i++){
			fileChannel_from.read(nonDirectBuffer);
			nonDirectBuffer.flip();
			fileChannel_to1.write(nonDirectBuffer);
			nonDirectBuffer.clear();
		}
		
		end = System.nanoTime();
		System.out.println("�ʹ��̷�Ʈ:\t"+(end-start)+"ns");
		
		///������ ��ġ�� �ٽ� 0���� ���� (�ʱ�ȭ)
		fileChannel_from.position(0);
		
		//���̷�Ʈ ���� �׽�Ʈ 
				start = System.nanoTime();
			
				for(int i=0; i<100; i++){
					fileChannel_from.read(directBuffer);
					directBuffer.flip();
					fileChannel_to1.write(directBuffer);
					directBuffer.clear();
				}
				
				end = System.nanoTime();
				System.out.println("���̷�Ʈ:\t"+(end-start)+" ns");
				
				fileChannel_from.close();
				fileChannel_to1.close();
				fileChannel_to2.close();
		
				/*
				 * ���
				�ʹ��̷�Ʈ:	1458007ns
				���̷�Ʈ:	584172 ns --> �ξ� ����
				*/
	}
}
