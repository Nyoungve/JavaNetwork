package com.NIONetworking;

//Path�������̽�? 
// ����θ� �̿��ؼ� �ҽ����Ͽ� ���� Path��ü�� ��� ���ϸ�, �θ� ���丮 ��, ��ø��μ�, ��λ� �ִ� ��� ���丮 ����ϱ�.
import java.nio.file.Path;  //���ϸ��� ��������� Path�������̽�
import java.nio.file.Paths; // Path������ü�� ������� �ʿ��� PathsŬ������ �����޼ҵ� get()�޼ҵ� ȣ��
import java.util.Iterator;

public class PathExample {

	public static void main(String[] args) throws Exception {
		
		//get�޼ҵ��� �Ű����� ������ ���. ���ڿ��� ������ ���� �ְ�, URI ��ü�� ������ ���� �ִ�.
		Path path = Paths.get("src/com/NIONetworking/PathExample.java");
		System.out.println("[���ϸ�] "+path.getFileName()); //�θ� ��θ� ������ ���� �Ǵ� ���丮 �̸��� ���� Path����
		System.out.println("[�θ� ���丮��]: "+ path.getParent().getFileName());
		System.out.println("��ø ��μ�: "+ path.getNameCount()); //��ø��μ�!!
		
		System.out.println();
		for(int i =0; i<path.getNameCount(); i++){ //��ø��μ���ŭ 
			System.out.println(path.getName(i)); // getName(int index)�� �̸���� 
		}
		
		System.out.println();
		//��ο� �ִ� ��� ���丮�� ������ Path ��ü�� �����ϰ� �ݺ��ڸ� ����
		Iterator<Path> iterator = path.iterator();
		
		while(iterator.hasNext()){
			Path temp = iterator.next();
			System.out.println(temp.getFileName()); //�����̸��� ��´�. 
		}
		
	}
}
