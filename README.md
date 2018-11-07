## FFWS : Far From WireShark

  *wireshark like v1 : 07/11/2018* 
  
  ### Java environment :  
  
  Java 11 2018-09-25  
  Java(TM) SE Runtime Environment 18.9 (build 11+28)  
  Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11+28, mixed mode)  
  
  This program allows you to display the informations contained in  
  a pcap file as wireshark would. The program is limited i.e. not   
  all protocols can be printed.  
  
  ### The following protocols can be displayed :  
  
  1. Ethernet  
  2. ARP  
  3. ICMP  
  4. IP  
  5. TCP  
  6. UDP  
  7. HTTP  
  8. DHCP  
  9. FTP  
  	
  TODO: DNS, FTP(to finish), Fragmentation IP  

  You will see later (section Arguments) that you will have to specify the pcap
  file you wish to display. In addition you can specify a particular protocol
  to display only the packet information's for this protocol. Or leave it with
  no arguments. By default the program will display all protocols.

  ### How to launch - To start the program please compile this code with the following command line :
  
  ```javac *a```
  
  Then to launch the program enter the following command line :
  
  ```java ParserCap [argument0] [argument1]```
	
   Arguments:

   argument0 : pcap file (a folder with pcap test file is included)   
   for example you can enter ```java ParserCap pcap/arp.pcap```  
   argument1 : protocol you wish to display leave it empty if you want to display all protocols
   for example you can enter ```java ParserCap pcap/http.pcap -http``` and only the http part 
   of the packet will be printed.

   For http and conversation they are saved during the execution of the program.
   You will be asked if you want to display the conversation. 

   ## Issue :  
   Not enough time to finish FTP and DNS.    
   You can print FTP but not FTP-DATA the converstation ftp will be display at the end all the time.  
   It does not take every possible issues within TCP segmentation.   
   So the conversations might not be complete.  
   For example : TCP keep alive is taken care of, however TCP Spurious Retransmission is not.  
	
   ## To continue :  
   Finish correctly the FTP, FTP-data protocol.  
   Do the protocol DNS.  
   Use an interface to display all packets to be able to choose the ones you  
   whish to display.  
