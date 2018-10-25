class Ip{
	public int PrintIp(byte[] data){
		System.out.print("\nIp\n");
		System.out.print("TTL    ");
		System.out.print("IP SRC        ");
		System.out.println("IP DST       ");
		System.out.format("%d    ", data[8]&255);
		for(int i=12; i<16; i++){
			System.out.format("%d ", data[i]&255);
		}
		System.out.print("   ");
		for(int i=16; i<20; i++){
			System.out.format("%d ",data[i]&255);
		}
		System.out.print("\nProtocol :");
		if(data[9]== 1){
			System.out.print("ICMP    ");
		}
		else if(data[9]==6){
			System.out.print("TCP   ");
			return 1;
		}
		else if(data[9]==17){
			System.out.print("UDP   ");
			return 2;
		}
		return 0;
	}
	/*public byte[] ReassemblyIp(byte [] data){
	  String s =Integer.toBinaryString(data[6] & 0xFF);
	  String s1 =Integer.toBinaryString(data[7] & 0xFF);
	  System.out.print("data[6] : "+s+"data[7]"+s1);  
	//System.out.print("\ntest reassembly :");  
	//System.out.format("%02X ",data[6]);   
	//System.out.format("%02X ",data[7]);   

	return data;
	}*/
}

