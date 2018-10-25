class Layer4{
	public void PrintTcp(byte [] data, int packetLength){
		int srcPort= (data[20]<< 8)&0xff00|data[21]&0x00ff;
		int dstPort= (data[22]<< 8)&0xff00|data[23]&0x00ff;
		System.out.println("      Src port :"+srcPort+" ->  Dst port :"+dstPort);
		long sequenceNumber=((long)data[24]&0xFF)<<24|((long)data[25]&0xFF)<<16|((long)data[26]&0xFF)<<8|((long)data[27]&0xFF);
		long ackNumber=((long)data[28]&0xFF)<<24|((long)data[29]&0xFF)<<16|((long)data[30]&0xFF)<<8|((long)data[31]&0xFF);

		System.out.println("\n      Sequence Number:"+sequenceNumber);
		System.out.println("      Acknowlegment Number:"+ackNumber);

		//Format de data[32] :1234 5678
		//1234 -> header length sur 4 bit
		//567 -> Reserved sur 3 bit
		//8 -> Nonce sur 1 bit
		int headerLength= (data[32]>>4)&0x0f;
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
		int syn=(data[33]>>1)&0x01;
		if(syn!=0){
			System.out.print("[SYN] ");
		}
		int ack=(data[33]>>4)&0x01;
		if(ack!=0){
			System.out.print("[ACK] ");
		}
		int fin=data[33]&0x01;
		if(fin!=0){
			System.out.print("[FIN] ");
		}
		int psh=(data[33]>>5)&0x01;
		if(psh!=0){
			System.out.println("[PSH] ");
		}
		int rst=(data[33]>>6)&0x01;
		if(rst!=0){
			System.out.println("[RST] ");
		}
		int dataLength=packetLength-34-headerLength*4;
		//Format data[33], data[34]: 2 octet -> window size value
		//Format data[35], data[36]: checksum 
		//Then Options on 20 bytes, Max segment size, SACK permitted, Timestamps, No-Operation(NOP), window scale       
		long nextSeqNum = sequenceNumber +(long)dataLength;
		System.out.println("[Next Sequence Number :"+nextSeqNum+"]");
	}
	public void PrintUdp(byte [] data){
		int srcPort= (data[20]<< 8)&0xff00|data[21]&0x00ff;
		int dstPort= (data[22]<< 8)&0xff00|data[23]&0x00ff;
		System.out.println("      Src port :"+srcPort+"  Dst port :"+dstPort);
		int Length= (data[24]<< 8)&0xff00|data[25]&0x00ff;
		System.out.println("      Length : "+Length);
		System.out.print("      Checksum : ");
		System.out.format("0x%02X%02X\n", data[26], data[27]);

	}
}
