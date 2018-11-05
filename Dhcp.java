class Dhcp{

	byte [] data=null;

	public Dhcp(byte [] data1){
		this.data=data1;
	}

	public void PrintDhcp(){
		
		System.out.println("\n        Protocol: DHCP");
		int messageType = data[0];
		if(data[0]==1){
			System.out.println("        Message type: Boot Request ("+messageType+")");
		}
		else{
			System.out.println("        Message type: Boot Reply ("+messageType+")");
		}
		int hwtype=data[1];	
		System.out.println("        Hardware type: (0x0"+hwtype+")");
		int hwaddrlength = data[2];
		System.out.println("        Hardware adresse length:"+hwaddrlength);
		int hops = data[3];
		System.out.format("        Transaction id: 0x%02X%02X%02X%02X\n", data[4], data[5], data[6], data[7]);
		System.out.format("        Client IP address : %d.%d.%d.%d\n", data[12]&255, data[13]&255, data[14]&255, data[15]&255);
		System.out.format("        Your (client) IP address : %d.%d.%d.%d\n", data[16]&255, data[17]&255, data[18]&255, data[19]&255);
		System.out.format("        Next server IP address : %d.%d.%d.%d\n", data[20]&255, data[21]&255, data[22]&255, data[23]&255);
		System.out.format("        Relay agent IP address : %d.%d.%d.%d\n", data[24]&255, data[25]&255, data[26]&255, data[27]&255);
		System.out.format("        Client MAC address: Grandstr_%02X:%02X:%02X (%02X:%02X:%02X:%02X:%02X:%02X)\n", data[31], data[32], data[33], data[28], data[29], data[30], data[31], data[32], data[33]);

		int startOption = 240;
		int option = data[startOption]&255;
		int lengthOption = 0;
		
		//0 car fin a 255 =>255&255 = 0
		while(option!=0){
			switch(option){
				case 1:
					System.out.println("        Option : (1) Subnet Mask");
					System.out.print("          Subnet Mask : ");
					for(int i=2; i<6; i++){
						System.out.format("%02X ", data[(startOption+i)]);
					}
					System.out.println("");
					break;
				case 50:
					System.out.print("        Option : (50) Requested IP Adress :\n 		");
					for(int i=2; i<6; i++){ 
						System.out.format("%d ", data[(startOption+i)]&255);
					}
					System.out.println("");
					break;
				case 51:
					System.out.println("        Option : (51) DHCP Message Type");
					long addrLeaseT=((long)data[startOption+2]&0xFF)<<24|((long)data[startOption+3]&0xFF)<<16|((long)data[startOption+4]&0xFF)<<8|((long)data[startOption+5]&0xFF);
					System.out.format("          IP Addrress LeaseTime : (%ds)\n", addrLeaseT);
					break;
				case 53:
					System.out.println("        Option : (53) DHCP Message Type");
					switch(data[startOption+2]){
						case 1:
							System.out.println("          DHCP: Discover ("+data[startOption+2]+")");
							break;
						case 2:
							System.out.println("          DHCP: Offer ("+data[startOption+2]+")");
							break; 
						case 3:
							System.out.println("          DHCP: Request ("+data[startOption+2]+")");
							break;
						case 5:
							System.out.println("          DHCP: ACK ("+data[startOption+2]+")");
							break;
						default:
							break;
					}
					break;
				case 54:
					System.out.format("        Option : (54) DHCP Server Identifier : %d.%d.%d.%d\n", data[startOption+2]&255, data[startOption+3]&255, data[startOption+4]&255, data[startOption+5]&255);
					break;
				case 55:
						System.out.println("        Option : (55) Parameter Request List");
						for(int i=0; i<4; i++){
							System.out.format("         Parameter Request List Item : (%d) ", data[startOption+2+i]&255);
							switch((data[startOption+2+i]&255)){
								case 1:
									System.out.println("Subnet Mask");
									break;
								case 3:
									System.out.println("Router");
									break;
								case 6:
									System.out.println("Domain Name Server");
									break;
								case 42:
									System.out.println("Network Time Protocol Servers");
									break;
								default:
									break;
							}
						}
					break;
				case 58:
					System.out.println("        Option : (58) Renewal Time Value");
					long renewValueT=((long)data[startOption+2]&0xFF)<<24|((long)data[startOption+3]&0xFF)<<16|((long)data[startOption+4]&0xFF)<<8|((long)data[startOption+5]&0xFF);
					System.out.format("        Renewal Time Value : (%ds)\n", renewValueT);
					break;
				case 59:
					System.out.println("        Option : (59) Rebinding Time Value");
					long rebindValueT=((long)data[startOption+2]&0xFF)<<24|((long)data[startOption+3]&0xFF)<<16|((long)data[startOption+4]&0xFF)<<8|((long)data[startOption+5]&0xFF);
					System.out.format("          Rebinding Time Value : (%ds)\n", rebindValueT);
					break;
				default:
					break;
			}
		 lengthOption=data[(startOption+1)];
		 startOption=startOption+lengthOption+2;
		 option = data[startOption]&255;
		}
	}
}
