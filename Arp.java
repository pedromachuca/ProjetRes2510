class Arp{
	public void PrintArp(byte[] data){
		System.out.print("\n\n   ARP ");
		if(data[7]== 1){ 
			System.out.print("Request ");
		}
		else{
			System.out.print("Reply   ");   
		}
		System.out.print("IP SRC            ");
		System.out.print("MAC SRC           ");
		System.out.print("IP DST            ");  
		System.out.println("MAC DST       ");   
		System.out.print("               ");
		for(int i=14; i<18; i++){
			System.out.format("%d ", data[i]&255);  
		}
		System.out.print("   ");
		for(int i=8; i<14; i++){    
			System.out.format("%02X ",data[i]);
		}
		System.out.print("   ");
		for(int i=24; i<28; i++){
			System.out.format("%d ", data[i]&255);
		}
		System.out.print("   ");
		for(int i=18; i<24; i++){
			System.out.format("%02X ",data[i]);
		}
		System.out.print("\n");

	}
}

