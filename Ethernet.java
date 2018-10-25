import java.io.*;

class Ethernet{
	public int PrintEth(byte[] data){
		System.out.print("Ethernet    ");
		System.out.print("Mac DST              ");
		System.out.println("Mac SRC  ");
		System.out.print("            ");
		for(int i=0; i<6; i++){
			System.out.format("%02X ", data[i]);    
		}
		System.out.print("   ");
		for(int i=6; i<12; i++){
			System.out.format("%02X ", data[i]);    
		}
		byte data1 = 0x0000;
		for(int i=12; i<14;i++){
			data1+= data[i];
		}
		switch (data1){
			case 14: 
				return 14; 
			case 8:
				return 8;
			default: 
				System.out.println("Default");
				break;
		}
		return 0;
	}   
}

