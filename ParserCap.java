import java.io.*;
class ParserCap{
	public static void main(String[] args){
		new ParserCap(args);
	}
	//Initialisation of the attribut of the class
	FileInputStream inputstream=null;
	File cap=null;
	byte[] filecontent=null;
	long fileLength=0;
	int	packetLength=0;
	//offset de départ pour ignorer le Global Header
	int startPacket=24;
	int endPacket=0;

	public ParserCap(String [] args){

		if (args.length < 1) {
			System.out.println("No file were specified.\nPlease enter the following command line :\njava Cap file.pcap -option");
			System.exit(1);
		}
		else if(args.length>=2){
			System.out.println("Too many argument specified.\nPlease enter the following command line :\njava Cap file.pcap -option");
			System.exit(1);
		}
		else{
			System.out.println("Program starting with the arguments file :"+args[0]);//+"\n option : "+args[1]);
		}
		parsePcap(args);
		/*Print the pcap in hexa
		  for(int i=0; i<filecontent.length; i++){
		  System.out.format("%02X ", filecontent[i]); 
		  }*/
		if(testMagicNum(filecontent, 0)){
			System.out.println( "\nThe file is a pcap format !\nStarting execution ..." );
		}
		else{
			System.out.println( "\nThe file is not a pcap format !\nExiting program ..." );
			System.exit(1);
		}
		int packetNumber = 1;
		while(true){
			System.out.format("\n--------------Packet %d ------------------------------------------------------------", packetNumber);
			PacketHeader(filecontent);
			System.out.format("Packet length : %d bytes on wire\n\n", packetLength);
			PacketParser(filecontent);
			startPacket=endPacket;
			packetNumber++;
			if(endPacket==fileLength){
				System.out.println("\n\nEnd of while");
				break;
			}
		}
	}
	void parsePcap(String [] args){
		try{
			cap = new File(args[0]);
			fileLength = cap.length();

			inputstream = new FileInputStream(cap);
			filecontent = new byte[(int)fileLength];
			int data = inputstream.read(filecontent);

			while(data != -1){
				data = inputstream.read(filecontent);
			}
			inputstream.close();
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	boolean testMagicNum(byte [] filecontent, int which){
		int startMagicNum=0;
		StringBuilder mNb=null;
		if(which==0){
			startMagicNum=0;
			mNb= new StringBuilder("d4c3b2a1");
		}
		else{
			startMagicNum=236;
			mNb= new StringBuilder("63825363");
		}
		byte [] MagicNum = new byte[4];
		for(int i=startMagicNum; i<startMagicNum+4; i++){
			MagicNum[i-startMagicNum] = filecontent[i];
		}
		StringBuilder sb = new StringBuilder(8);
		for(byte b: MagicNum){
			sb.append(String.format("%02x", b));
		}
		if (sb.toString().equals(mNb.toString())){
			return true;	
		}
		else{
			return false;
		}
	}
	void PacketHeader(byte [] filecontent){
		int packetHSize = 16;
		byte[] PacketHeader= new byte[packetHSize];
		endPacket = startPacket + packetHSize;
		for(int i=startPacket; i<endPacket; i++){
			PacketHeader[i-startPacket]=filecontent[i];
		}  
		System.out.println("\n");
		//Take the octet 8 and 9 the length is data[9]data[8] (inverted) 
		//and converted to an int verify the position F203
		//donne 61955 and 03F2 ->1010
		packetLength=(PacketHeader[9]<< 8)&0xff00|PacketHeader[8]&0x00ff;
	}

	void PacketParser(byte [] filecontent){	
		int udp=0;
		int tcp=0;
		byte[] packetAfterUdp=null;
		byte[] packetAfterTcp=null;
		byte[] FirstPacket=new byte[packetLength];
		for(int i=endPacket; i<packetLength+endPacket; i++){
			FirstPacket[i-endPacket]=filecontent[i];
		}
		int ethSize = 14;
		int endEth = endPacket+ethSize;
		byte[] EthPacket= new byte[ethSize];
		for(int i=endPacket; i<endEth; i++){
			EthPacket[i-endPacket]=filecontent[i];
		}
		Ethernet packetEthernet = new Ethernet();
		int type = packetEthernet.PrintEth(EthPacket);
		endPacket = endPacket+packetLength;
		if(type==14){
			int arpSize = 28;
			byte[] packetarp= new byte[arpSize];
			for(int i=endEth; i<endEth+arpSize; i++){
				packetarp[i-endEth]=filecontent[i];
			}
			Arp packetArp = new Arp();
			packetArp.PrintArp(packetarp);
		}
		else if(type==8){
			int ipSize=20;
			int endIp=endEth+ipSize;
			byte[] packet= new byte[endPacket];
			for(int i=endEth; i<endPacket; i++){
				packet[i-endEth]=filecontent[i];
			}
			Ip packetIp = new Ip();
			int protocol=packetIp.PrintIp(packet);
			//packetIp.ReassemblyIp(packet);
			if(protocol !=0){
				int thisSize = endPacket-endIp;
				byte[] packetL4= new byte[thisSize];

				for(int i=endIp; i<endPacket; i++){
					packetL4[i-endIp]=filecontent[i];
				}

				Layer4 layer4 = new Layer4();
				if(protocol==1){
					packetAfterTcp=new byte[endPacket];
					packetAfterTcp=layer4.PrintTcp(packetL4, packetLength);
					
					if(packetAfterTcp!=null){
						tcp=1;
					}
				}
				else if(protocol==2){
					udp = 1;
					int sizeUdp=8;
					int sizeAfterUdp = thisSize-sizeUdp;
					packetAfterUdp=new byte[sizeAfterUdp];
					packetAfterUdp=layer4.PrintUdp(packetL4);
				}
			}
		}
		AppLayer applicationLayer = new AppLayer();
		if(udp==1){
			if (testMagicNum(packetAfterUdp, 1)){
				applicationLayer.PrintDhcp(packetAfterUdp);
			}
		}
		if(tcp==1){
			applicationLayer.PrintHttp(packetAfterTcp);
		}
	}
}
