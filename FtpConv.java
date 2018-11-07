class FtpConv{
    
    byte[] data=null;

    public FtpConv(byte [] data1){
        this.data=data1;
    }   

    public byte [] Conversation(){
        return this.data;
    }   

    public void PrintFtp(){

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
        System.out.println("\n      TCP content : FTP ");
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

