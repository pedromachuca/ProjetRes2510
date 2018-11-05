import java.io.*;
import java.util.*; 
		
class ParserCap{

	public static void main(String[] args){
		new ParserCap(args);
	}

	//Initialisation of the attribut of the class
	FileInputStream inputstream=null;
	File cap=null;
	byte[] filecontent=null;
	long fileLength=0;
	//offset de départ pour ignorer le Global Header
	int startPacket=24;
	ArrayList<httpConv> arrayhttpconv=new ArrayList<httpConv>(); 
	long nextSequenceNb =0;
	HashMap<Integer, Long> idNextSeq = new HashMap<Integer, Long>();
	int	packetLength=0, endPacket=0, key=0, eth=0, arp=0, ip=0, icmp=0, tcp=0, udp=0, http=0, dhcp=0, all=0;

	public ParserCap(String [] args){

		if (args.length < 1) {
			System.out.println("Not enough arguments\nPlease enter the following command line :\njava Cap file.pcap -option");
			System.out.println("Available options :\n-eth\n-arp\n-ip\n-tcp\n-udp\n-http\n-dhcp");
			System.exit(1);
		}
		else if(args.length==1){
			all=1;
		}
		else if(args.length>=3){
			System.out.println("Too many argument specified.\nPlease enter the following command line :\njava Cap file.pcap -option");
			System.out.println("Available options :\n-eth\n-arp\n-ip\n-tcp\n-udp\n-http\n-dhcp");
			System.exit(1);
		}
		else{
			System.out.println("\nProgram starting with the arguments :\nFile : "+args[0]+"\nOption : "+args[1]);
			switch(args[1]){
				case "-eth":
					eth=1;
					break;
				case "-arp":
					arp=1;
					break;
				case "-ip":
					ip=1;
					break;
				case "-icmp":
					icmp=1;
					ip=1;
					break;
				case "-tcp":
					tcp=1;
					break;
				case "-udp":
					udp=1;
					break;
				case "-http":
					http=1;
					break;
				case "-dhcp":
					dhcp=1;
					break;
				default:
					break;
			}
		}
		parsePcap(args);
	
		if(testMagicNum(filecontent, 0)){
			System.out.println( "\nThe file is a pcap format !\nStarting execution ..." );
		}
		else{
			System.out.println( "\nThe file is not a pcap format !\nExiting program ..." );
			System.exit(1);
		}

		int packetNumber = 1;
		while(true){
			System.out.format("\n-------------- Packet %d ------------------------------------------------------------", packetNumber);
			PacketHeader(filecontent);
			System.out.format("Packet length : %d bytes on wire\n\n", packetLength);
			PacketParser(filecontent);
			startPacket=endPacket;
			packetNumber++;
			if(endPacket==fileLength){
				System.out.format("\n-------------- End Packet -----------------------------------------------------------\n");
				System.out.println("      \n"+idNextSeq.size()+" HTTP CONVERSATION HAVE BEEN SAVED");
				System.out.println("Enter a number between 1 and "+idNextSeq.size()+" to display the corresponding conversation");
				System.out.println("Enter n instead if you wish to leave the program.");
				char c =' ';
			
				while(c!='n'){
					try{
						BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    	c = (char)input.read();
                         
					}catch(IOException e){
						System.out.println("IOException has been caught");
					}
					if((c!='n')&&(Character.isDigit(c))&&(Character.getNumericValue(c)>0&&Character.getNumericValue(c)<=idNextSeq.size())){
						int value=Character.getNumericValue(c);
						PrintConv(arrayhttpconv, value);
						System.out.println("\nWould you like to display another conversation ?");
						System.out.println("If yes, enter a number between 1 and "+idNextSeq.size()+" to display the corresponding conversation");
						System.out.println("Enter n instead if you wish to leave the program.");
					}
					else{
						System.out.println("You did not enter a correct value...");	
					}
					
				}
				System.out.println("\n\nExiting the program !! ");
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
		else if(which==1&&filecontent.length>236){
			startMagicNum=236;
			mNb= new StringBuilder("63825363");
		}
		else{
			return false;
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
		byte[] PacketH= new byte[packetHSize];
		endPacket = startPacket + packetHSize;

		for(int i=startPacket; i<endPacket; i++){
			PacketH[i-startPacket]=filecontent[i];
		}  
		System.out.println("\n");
		//Take the octet 8 and 9 the length is data[9]data[8] (inverted) 
		//and converted to an int verify the position F203
		//donne 61955 and 03F2 ->1010
		packetLength=(PacketH[9]<< 8)&0xff00|PacketH[8]&0x00ff;
	}

	void PacketParser(byte [] filecontent){	

		int underudp=0;
		int undertcp=0;
		byte[] packetAfterUdp=null;
		byte[] packetAfterTcp=null;

		byte[] FirstPacket=new byte[packetLength];
		for(int i=endPacket; i<packetLength+endPacket; i++){
			FirstPacket[i-endPacket]=filecontent[i];
		}

		int ethSize = 14;
		int endEth = endPacket+ethSize;
		byte[] ethPacket= new byte[ethSize];
		for(int i=endPacket; i<endEth; i++){
			ethPacket[i-endPacket]=filecontent[i];
		}

		Ethernet packetEthernet = new Ethernet(ethPacket);
		if (eth==1||all==1) {
			packetEthernet.PrintEth();
		}
		int type = packetEthernet.nextType();
		endPacket = endPacket+packetLength;

		if(type==14){
			int arpSize = 28;
			byte[] packetarp= new byte[arpSize];
			for(int i=endEth; i<endEth+arpSize; i++){
				packetarp[i-endEth]=filecontent[i];
			}
			if (arp==1||all==1) {
				Arp packetArp = new Arp();
				packetArp.PrintArp(packetarp);	
			}
		}
		else if(type==8){

			int ipSize=20;
			int endIp=endEth+ipSize;
			byte[] packet= new byte[endPacket];
			for(int i=endEth; i<endPacket; i++){
				packet[i-endEth]=filecontent[i];
			}

			Ip packetIp = new Ip(packet);
			int protocol=packetIp.nextProto();
			if (ip==1||all==1) {
				packetIp.PrintIp(icmp);
			}
			//packetIp.ReassemblyIp(packet);
			if(protocol !=0){
				int thisSize = endPacket-endIp;
				byte[] packetL4= new byte[thisSize];

				for(int i=endIp; i<endPacket; i++){
					packetL4[i-endIp]=filecontent[i];
				}

				Layer4 layer4 = new Layer4(packetL4, packetLength);
				if(protocol==1){
					packetAfterTcp=new byte[layer4.sizeHttp()];
					packetAfterTcp=layer4.ParseTcp();

					if (tcp==1||all==1) {
						layer4.PrintTcp();
					}
					nextSequenceNb = layer4.nextSeNb();
					long seqNumber = layer4.seqNb();
					long ackNumber = layer4.ackNb();

				    if(idNextSeq.isEmpty()){
				    	key=1;
				    	idNextSeq.put(key, nextSequenceNb);
				    }
				    else if(idNextSeq.containsValue(seqNumber)||idNextSeq.containsValue(ackNumber)){
				        
				        for(Map.Entry<Integer, Long> entry : idNextSeq.entrySet()){
				  
     						if(seqNumber==entry.getValue()||ackNumber==entry.getValue()){
				                key = entry.getKey();
				                break; //breaking because its one to one map
				            }
     					}
				    	idNextSeq.put(key, nextSequenceNb);
				    }
				    else if(idNextSeq.containsValue((seqNumber+1))){

				        for(Map.Entry<Integer, Long> entry : idNextSeq.entrySet()){
				  
     						if((seqNumber+1)==entry.getValue()){
				                key = entry.getKey();
				                break; //breaking because its one to one map
				            }
     					}
     					nextSequenceNb=nextSequenceNb+1;
				    	idNextSeq.put(key, nextSequenceNb);
				    }
				    else if(idNextSeq.containsValue((ackNumber-1))){
				    	 for(Map.Entry<Integer, Long> entry : idNextSeq.entrySet()){
				  
     						if((ackNumber-1)==entry.getValue()){
				                key = entry.getKey();
				                break; //breaking because its one to one map
				            }
     					}
     					idNextSeq.put(key, nextSequenceNb);
				    }
				    else{
				    	
				    	key++;
				    	idNextSeq.put(key, nextSequenceNb);
				    }
					if(packetAfterTcp!=null){
						undertcp=1;
					}
				}
				else if(protocol==2){
					underudp = 1;
					int sizeUdp=8;
					int sizeAfterUdp = thisSize-sizeUdp;
					packetAfterUdp=new byte[sizeAfterUdp];
					packetAfterUdp=layer4.ParseUdp();

					if (udp==1||all==1) {
						layer4.PrintUdp();
					}
				}
			}
		}
		
	/*	if(underudp==1){
			if (testMagicNum(packetAfterUdp, 1)){

				Dhcp protoDhcp = new Dhcp(packetAfterUdp);
				if (dhcp==1||all==1) {
					protoDhcp.PrintDhcp();
				}
			}
		}*/

		if(undertcp==1){
			httpConv protoHttp = new httpConv(packetAfterTcp, key);
			if (http==1||all==1) {
				protoHttp.PrintHttp();
			}
			arrayhttpconv.add(protoHttp);
		}
	}
	public void PrintConv(ArrayList<httpConv> httpconv, int nbConv){
		char[] cbuf;		

		System.out.println("\n      *****************************");
		System.out.println("      *                           *");
		System.out.println("      *  Following TCP stream "+nbConv+"   *");
		System.out.println("      *                           *");
		System.out.println("      *****************************\n");
		
		for(httpConv b:arrayhttpconv){
			if(b.id==nbConv){	
				cbuf = new char[b.data.length];
				System.out.println("      ID : "+b.id);
			    for (int i = 0; i < b.data.length; i++){
		    			if(b.data[i]>= 0x20 && b.data[i] < 0x7F){
		        	    	cbuf[i] = (char) b.data[i];
						}
		 				else if(b.data[i]==10||b.data[i]==13){
		 					cbuf[i] = (char) b.data[i];
						}
						else{
		        			cbuf[i]='.';
		        		}
		    	}
				System.out.print("      ");
				for (int i=0; i<b.data.length ; i++){
					if(i!=0&&cbuf[i-1]=='\n'){
						System.out.print("      ");	
					}
					System.out.print(cbuf[i]);        		
				}
				System.out.println("");
			}   		
		}
	}
}
