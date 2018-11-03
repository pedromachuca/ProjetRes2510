class Ip{
	
	byte [] data=null;

	public Ip(byte[] data1){
		this.data=data1;
	}

	public void PrintIp(int icmp){
		if (icmp==0) {
			System.out.print("\n\n   Protocol: Ip \n");
			//System.out.print("	  TTL    ");
			System.out.print("   IP SRC         ");
			System.out.println("IP DST");
			//TTL
			//System.out.format("%d    ", data[8]&255);
			System.out.print("   ");
			for(int i=12; i<16; i++){
				System.out.format("%d ", data[i]&255);
			}
			System.out.print("   ");
			for(int i=16; i<20; i++){
				System.out.format("%d ",data[i]&255);
			}
				
		}
		if (icmp==1) {
		
			System.out.print("\n\n      Protocol:");
			if(data[9]== 1){
				System.out.print("ICMP ");
				if(data[20]==8){
					System.out.print("Echo (ping) Request ");
				}
				else if(data[20]==0){
					System.out.print("Echo (ping) Reply   ");
				}
				System.out.format("id=0x%02X%02X", data[24], data[25]);
				System.out.println("");
			}
		}
	}

	public int nextProto(){
		
		if(data[9]==6){
			return 1;
		}
		else if(data[9]==17){
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

