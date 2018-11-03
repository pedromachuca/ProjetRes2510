class Layer4{
	
	byte [] data=null;
	byte [] packetAfterUdp=null;
	int packetLength=0, srcPort=0, dstPort=0, headerLength=0, syn=0, ack=0, fin=0, rst=0, psh=0, headerTcpLength=0, dataLength=0;
	int length=0, srcPortUdp=0, dstPortUdp=0, lengthAfterUdp = 0;	
	long nextSeqNum =0, sequenceNumber =0, ackNumber=0;
	int sizeUdp = 8;
	
	public Layer4(byte [] data1, int packetLength){
		this.data=data1;
		this.packetLength=packetLength;
	}

	public byte [] ParseTcp(){

		byte [] packetAfterTcp = null;

		srcPort= (data[0]<< 8)&0xff00|data[1]&0x00ff;
		dstPort= (data[2]<< 8)&0xff00|data[3]&0x00ff;

		sequenceNumber=((long)data[4]&0xFF)<<24|((long)data[5]&0xFF)<<16|((long)data[6]&0xFF)<<8|((long)data[7]&0xFF);
		ackNumber=((long)data[8]&0xFF)<<24|((long)data[9]&0xFF)<<16|((long)data[10]&0xFF)<<8|((long)data[11]&0xFF);

		headerLength=(data[12]>>4)&0x0f;
		syn=(data[13]>>1)&0x01;
		ack=(data[13]>>4)&0x01;
		fin=data[13]&0x01;
		psh=(data[13]>>5)&0x01;
		rst=(data[13]>>6)&0x01;

		headerTcpLength=headerLength*4;
		dataLength=packetLength-34-headerTcpLength;

		if(dataLength>6){
			nextSeqNum= sequenceNumber +(long)dataLength;
		} 
		else if(fin==1){
			nextSeqNum= sequenceNumber+1;	
		}
		else if(syn==1&&ack==1){
			nextSeqNum=sequenceNumber+1;
		}
		else if(syn==1&&ack==0){
			nextSeqNum= sequenceNumber+1;
		}
		else if(syn==0&&ack==1){
			nextSeqNum= sequenceNumber;
		}

		if(data.length>dataLength&&dataLength>6){

			packetAfterTcp = new byte[dataLength];
			for(int i=headerTcpLength; i<dataLength+headerTcpLength; i++){
				packetAfterTcp[i-headerTcpLength]=data[i];
			}
		}
		
		return packetAfterTcp;
	}

	public void PrintTcp(){

		System.out.println("\n\n      Protocol : TCP");
		System.out.println("      Src port :"+srcPort+" ->  Dst port :"+dstPort);
		System.out.println("      Sequence Number:"+sequenceNumber);
		System.out.println("      Acknowlegment Number:"+ackNumber);

		//Format de data[32] :1234 5678
		//1234 -> header length sur 4 bit
		//567 -> Reserved sur 3 bit
		//8 -> Nonce sur 1 bit
		System.out.println("      header length:"+(headerLength*4)+"bytes");
		//Format de data[33]: 1234 5678
		//1 -> Congestion Window Reduced
		//2 -> ECN-ECHO
		//3 -> Urgent
		//4 -> Acknowledgment !!
		//5 ->Push
		//6 ->Reset
		//7 ->Syn
		//8 ->Fin
		System.out.print("      FLAGS: ");
		if(syn!=0){
			System.out.print("[SYN] ");
		}
		if(ack!=0){
			System.out.print("[ACK] ");
		}
		if(fin!=0){
			System.out.print("[FIN] ");
		}
		if(psh!=0){
			System.out.println("[PSH] ");
		}
		if(rst!=0){
			System.out.println("[RST] ");
		}
		//Format data[13], data[14]: 2 octet -> window size value
		//Format data[15], data[16]: checksum 
		//Then Options on 20 bytes, Max segment size, SACK permitted, Timestamps, No-Operation(NOP), window scale 
		System.out.println("\n      [Next Sequence Number :"+nextSeqNum+"]");
	}

	public int sizeHttp(){
		int headerLength=(data[12]>>4)&0x0f;
		int dataLength=packetLength-34-headerLength*4;
		return dataLength;
	}

	public long nextSeNb(){
		return nextSeqNum;
	}

	public long seqNb(){
		return sequenceNumber;
	}

	public long ackNb(){
		return ackNumber;
	}

	public void PrintUdp(){

		System.out.println("\n\n      Protocol : UDP");
		System.out.println("      Src port :"+srcPortUdp+"  Dst port :"+dstPortUdp);
		System.out.println("      Length : "+length);
		System.out.print("      Checksum : ");
		System.out.format("0x%02X%02X\n", data[6], data[7]);
	}

	public byte [] ParseUdp(){

		srcPortUdp= (data[0]<< 8)&0xff00|data[1]&0x00ff;
		dstPortUdp= (data[2]<< 8)&0xff00|data[3]&0x00ff;
		length= (data[4]<< 8)&0xff00|data[5]&0x00ff;
		lengthAfterUdp = length -sizeUdp;
		packetAfterUdp = new byte[lengthAfterUdp];

		for(int i=8; i<lengthAfterUdp; i++){
			packetAfterUdp[i-8]=data[i];
		}
		return packetAfterUdp;
	}
}