import java.io.*;

class Ethernet{

	byte [] data=null;

	public Ethernet(byte [] data1){
		this.data=data1;
	}
	
	public void PrintEth(){

		System.out.println("Protocol: Ethernet    ");
		System.out.print("Mac DST              ");
		System.out.println("Mac SRC  ");

		for(int i=0; i<6; i++){
			System.out.format("%02X ", data[i]);    
		}
		System.out.print("   ");

		for(int i=6; i<12; i++){
			System.out.format("%02X ", data[i]);    
		}
	}   
	public int nextType(){
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

