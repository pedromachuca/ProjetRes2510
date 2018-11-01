class Layer4{
	public void PrintTcp(byte [] data, int packetLength){
		int srcPort= (data[0]<< 8)&0xff00|data[1]&0x00ff;
		int dstPort= (data[2]<< 8)&0xff00|data[3]&0x00ff;
		System.out.println("      Src port :"+srcPort+" ->  Dst port :"+dstPort);
		long sequenceNumber=((long)data[4]&0xFF)<<24|((long)data[5]&0xFF)<<16|((long)data[6]&0xFF)<<8|((long)data[7]&0xFF);
		long ackNumber=((long)data[8]&0xFF)<<24|((long)data[9]&0xFF)<<16|((long)data[10]&0xFF)<<8|((long)data[11]&0xFF);

		System.out.println("\n      Sequence Number:"+sequenceNumber);
		System.out.println("      Acknowlegment Number:"+ackNumber);

		//Format de data[32] :1234 5678
		//1234 -> header length sur 4 bit
		//567 -> Reserved sur 3 bit
		//8 -> Nonce sur 1 bit
		int headerLength= (data[12]>>4)&0x0f;
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
		int syn=(data[13]>>1)&0x01;
		if(syn!=0){
			System.out.print("[SYN] ");
		}
		int ack=(data[13]>>4)&0x01;
		if(ack!=0){
			System.out.print("[ACK] ");
		}
		int fin=data[13]&0x01;
		if(fin!=0){
			System.out.print("[FIN] ");
		}
		int psh=(data[13]>>5)&0x01;
		if(psh!=0){
			System.out.println("[PSH] ");
		}
		int rst=(data[13]>>6)&0x01;
		if(rst!=0){
			System.out.println("[RST] ");
		}
		int dataLength=packetLength-34-headerLength*4;
		//Format data[13], data[14]: 2 octet -> window size value
		//Format data[15], data[16]: checksum 
		//Then Options on 20 bytes, Max segment size, SACK permitted, Timestamps, No-Operation(NOP), window scale       
		long nextSeqNum = sequenceNumber +(long)dataLength;
		System.out.println("[Next Sequence Number :"+nextSeqNum+"]");
	}
	public byte [] PrintUdp(byte [] data, int packetLength){
		int sizeUdp = 8;
		int srcPort= (data[0]<< 8)&0xff00|data[1]&0x00ff;
		int dstPort= (data[2]<< 8)&0xff00|data[3]&0x00ff;
		System.out.println("      Src port :"+srcPort+"  Dst port :"+dstPort);
		int Length= (data[4]<< 8)&0xff00|data[5]&0x00ff;
		System.out.println("      Length : "+Length);
		System.out.print("      Checksum : ");
		System.out.format("0x%02X%02X\n", data[6], data[7]);
	
		int lengthAfterUdp = Length -sizeUdp;
		byte [] packetAfterUdp = new byte[lengthAfterUdp];
		for(int i=8; i<lengthAfterUdp; i++){
			packetAfterUdp[i-8]=data[i];
		}
		return packetAfterUdp;
	}
}
