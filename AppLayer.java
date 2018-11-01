class AppLayer{
	public void PrintDhcp(byte [] data){
		
		System.out.println("\n      Protocol: DHCP");
		int messageType = data[0];
		if(data[0]==1){
			System.out.println("      Message type: Boot Request ("+messageType+")");
		}
		else{
			System.out.println("      Message type: Boot Reply ("+messageType+")");
		}
		int hwtype=data[1];	
		System.out.println("      Hardware type: (0x0"+hwtype+")");
		int hwaddrlength = data[2];
		System.out.println("      Hardware adresse length:"+hwaddrlength);
		int hops = data[3];
		System.out.format("      Transaction id: 0x%02X%02X%02X%02X", data[4], data[5], data[6], data[7]);
		int startOption = 240;
		int option = data[startOption]&255;
		int lengthOption = 0;
		
		//0 car fin a 255 =>255&255 = 0
		while(option!=0){
			switch(option){
				case 1:
					System.out.println("      Option : (1) Subnet Mask");
					System.out.print("      Subnet Mask : ");
					for(int i=2; i<6; i++){
						System.out.format("%02X ", data[(startOption+i)]);
					}
					System.out.println("");
					break;
				case 50:
					System.out.print("      Option : (50) Requested IP Adress : ");
					for(int i=2; i<6; i++){
						System.out.format("%d ", data[(startOption+i)]&255);
					}
					System.out.println("");
					break;
				case 51:
					System.out.println("      Option : (53) DHCP Message Type");

					break;
				case 53:
					System.out.println("      Option : (53) DHCP Message Type");
					switch(data[startOption+2]){
						case 1:
							System.out.println("      DHCP: Discover ("+data[242]+")");
							break;
						case 2:
							System.out.println("      DHCP: Offer ("+data[242]+")");
							break;
						case 3:
							System.out.println("      DHCP: Request ("+data[242]+")");
							break;
						case 5:
							System.out.println("      DHCP: ACK ("+data[242]+")");
							break;
						default:
							break;
					}
					break;
				case 54:

					break;
				case 55:
	
					break;
				case 58:

					break;
				case 59:

					break;
				case 61:

					break;
	
			}
		 lengthOption=data[(startOption+1)];
		 startOption=startOption+lengthOption+2;
		 option = data[startOption]&255;
		}
	//public void PrintHttp(){


	//}
	}
}
