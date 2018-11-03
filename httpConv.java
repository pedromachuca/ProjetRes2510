class httpConv{
	
	byte[] data=null;
	int id=0;

	public httpConv(byte [] data1, int id){
		this.data=data1;
		this.id =id;
	}

	public byte [] Conversation(){
		return this.data;
	}

	public void PrintHttp(){

        char[] cbuf = new char[data.length];
        for (int i = 0; i < data.length; i++){
        		if(data[i]>= 0x20 && data[i] < 0x7F){
	            	cbuf[i] = (char) data[i];
    			}
     			else if(data[i]==10||data[i]==13){
     				cbuf[i] = (char) data[i];
				}
    			else{
            		cbuf[i]='.';
            	}
        }
        System.out.println("\n      TCP content : HTTP ");
        System.out.println("      ID : "+this.id);
        System.out.print("      ");
        for (int i=0; i<data.length ; i++){
        		if(i!=0&&cbuf[i-1]=='\n'){
        			System.out.print("      ");	
        		}
        	    System.out.print(cbuf[i]);        		
        }
        System.out.println("");		
	}
}