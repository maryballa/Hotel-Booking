import java.io.*;
import java.util.concurrent.Semaphore;  
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalDate;
public class ClientHandler extends Thread implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    
    Socket socket;
    Semaphore sem;
	BufferedReader buffRdr = null;
	BufferedWriter buffWrtr = null;
	

    public ClientHandler(Socket socket,Semaphore sem){
    	this.sem=sem;
    	try{
            this.socket = socket; 
            this.buffWrtr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.buffRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientHandlers.add(this);
            
        } catch (IOException e){ 
        	try {
    			if(buffRdr != null){
    				buffRdr.close();
    		    }
    		    if(buffWrtr != null){
    		        buffWrtr.close();
    		    }
    		    if(socket != null){
    		         socket.close();
    		    }
        	}catch(IOException ee) {
        		ee.printStackTrace();
        	}
        }
    }
    @Override
    public void run() {
    	
    	String listForClient="";
    	
    	codeRoomGenerating(AllTheHotels.hotels);
    	sorting(AllTheHotels.hotels);	//sorting every hotels' rooms according to their number of beds
    	
    	Hotel selectedHotels[]; //hotels that match client's preferences
        while(socket.isConnected()){
            try {
            	String clientReq = buffRdr.readLine(); //reads client's request (hotel info or exit)
    			String[] words = clientReq.split(" ");	//splitting the line from socket
            	if(words.length==1) {//if length=1 client entered exit 
                	break;
                }else {
                
                	String loc=words[0];
    	            Room idealRoom=new Room(Integer.parseInt(words[1]),Integer.parseInt(words[2]));
    	            
    	            clientReq=buffRdr.readLine();
    	            words = clientReq.split(" ");
    	            
    				LocalDate date1=LocalDate.of(Integer.parseInt(words[2]),Integer.parseInt(words[1]),Integer.parseInt(words[0]));
    				LocalDate date2=LocalDate.of(Integer.parseInt(words[5]),Integer.parseInt(words[4]),Integer.parseInt(words[3]));;
    				
    				selectedHotels=selHotels(loc,idealRoom,AllTheHotels.hotels,date1,date2);	//finding hotels for client
    				if(selectedHotels==null) {	//there are no results
    					listForClient="No room fulfils this very criteria :(";
    					
    					//Process of sending results to client
        				String wordForClient[]=listForClient.split("\n");
        				String length=Integer.toString(wordForClient.length);
        				buffWrtr.write(length); //length is the number of options
        				buffWrtr.newLine();
        				buffWrtr.flush();
        				for(int i=0; i<wordForClient.length; i++) {
        					buffWrtr.write(wordForClient[i]);
        					buffWrtr.newLine();
        					buffWrtr.flush();
        				}
    				}
    				else { //there is at least one result
    					for(int i=0; i<selectedHotels.length; i++) {
    						if(i==0) {
    							listForClient=selectedHotels[i].toString();
    						}
    						else {
    							listForClient=listForClient+selectedHotels[i].toString();
    						}
    					}
    					
    					//Process of sending results to client
        				String wordForClient[]=listForClient.split("\n");
        				String length=Integer.toString(wordForClient.length);
        				buffWrtr.write(length); //length is the number of options
        				buffWrtr.newLine();
        				buffWrtr.flush();
        				for(int i=0; i<wordForClient.length; i++) {
        					buffWrtr.write(wordForClient[i]);
        					buffWrtr.newLine();
        					buffWrtr.flush();
        				}
        				
        				int option=Integer.parseInt(buffRdr.readLine());	//cleint's option
        				
        				if(option!=0) {	//client wants to book a room
        					//and dates!
        					sem.acquire();
        					int[] hotelRoomInfo=roomChecking(selectedHotels[option-1].rooms.get(0).getCode(),AllTheHotels.hotels,date1,date2);
        					if(hotelRoomInfo[0]>=0 && hotelRoomInfo[1]>=0) {	//success
        						System.out.println("The booking was successful from "+this.getName()+"!");
        						AllTheHotels.hotels[hotelRoomInfo[0]].rooms.get(hotelRoomInfo[1]).setDate(date1, date2);
        						
        						buffWrtr.write("Your booking process was successufull:).");	//sending the news to the other side
            					buffWrtr.newLine();
            					buffWrtr.flush();
            					
            					//write in File!
            					AllTheHotels.writeHotelInfo();
        					}else {
        						System.out.println("The booking was not successful from "+this.getName()+"!");      
        						buffWrtr.write("Your booking process was not successufull:(.");	//sending the news to the other side
            					buffWrtr.newLine();
            					buffWrtr.flush();
        					}
        					sem.release();
        				}else {	//client does not want to book
        				}
    				}
                }        
            } catch (IOException e){
                break;
            } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        removeClientHandler();
        try{
			if(buffRdr != null){
				buffRdr.close();
		    }
		    if(buffWrtr != null){
		        buffWrtr.close();
		    }
		    if(socket != null){
		         socket.close();
		    }
		   
		}catch (IOException e){
		    e.printStackTrace();
		}		
    }

    public void removeClientHandler(){
        System.out.println(this.getName()+" client has left.");
        clientHandlers.remove(this);
    }
    
    public int[] roomChecking(int code, Hotel[] hotels, LocalDate dayStart, LocalDate dayEnd) {
    	int[] a=new int[2];
    	
    	for(int i=0; i<hotels.length; i++) { //for every hotel
    		for(int j=0; j<hotels[i].rooms.size(); j++) { // for every room
    			if(hotels[i].rooms.get(j).getCode()==code) {	//WE FOUND OUR MATCH	
    				//need to check for date
    				if(hotels[i].rooms.get(j).compareDate(dayStart, dayEnd)==0) {
    					a[0]=i; //the book can be completed :)
    					a[1]=j;
    					return a;
    				}
    			}
    		}
    	}
    	
    	a[0]=-1;
    	a[1]=-1;
    	return a;	//the book can not be completed :(
    }
    
    public static void codeRoomGenerating(Hotel[] hotels) {
    	int code=40;
    	for(int i=0; i<hotels.length; i++) {	//for each hotel
    		for(int j=0; j<hotels[i].rooms.size(); j++) {	//for every room of the hotel
    			hotels[i].rooms.get(j).setCode(code);
    			code=code+5;
    		}
    	}
    }
    
    public static void sorting(Hotel[] hotels) {	
		Room swap;
		
		for(int i=0; i<hotels.length; i++) {	//each hotel
			for(int k=0; k<hotels[i].rooms.size()-1; k++) {
				for(int j=0; j<hotels[i].rooms.size()-k-1; j++) {
					if(hotels[i].rooms.get(j).numOfBeds>hotels[i].rooms.get(j+1).numOfBeds) {
						swap=hotels[i].rooms.get(j);
						hotels[i].rooms.set(j, hotels[i].rooms.get(j+1));
						hotels[i].rooms.set(j+1, swap);
					}
				}
			}
		}
	}
	
    //add argument for date
	public static Hotel[] selHotels(String area, Room idealRoom, Hotel hotels[], LocalDate dayStart, LocalDate dayEnd) {
		
		Room[] roomList;
		Hotel[] wantedHotels;
		int count=0;
		
		for(int i=0; i<hotels.length; i++) {
			if(area.equals(hotels[i].location)) {
				roomList=selRooms(idealRoom, hotels[i], dayStart, dayEnd);
				if(roomList!=null) {
					count=count+1;
				}
			}
		}
		
		if(count==0) {
			return null;
		}
		wantedHotels=new Hotel[count];
		count=0;
		
		for(int i=0; i<hotels.length; i++) {
			if(area.equals(hotels[i].location)) {
				roomList=selRooms(idealRoom, hotels[i], dayStart, dayEnd);
				if(roomList!=null) {
					wantedHotels[count]=new Hotel(hotels[i].name,hotels[i].location,roomList);
					count=count+1;
				}
			}
		}
		
		return wantedHotels;
	}
	
	public static Room[] selRooms(Room idealRoom,Hotel hotel,LocalDate dayStart, LocalDate dayEnd) {

		int count=0;
		for(int i=0; i<hotel.rooms.size(); i++) {		//every room
			if(idealRoom.numOfBeds==hotel.rooms.get(i).numOfBeds) { //comparing clients need of num beds to rooms
				hotel.rooms.get(i).setPrice(dayStart); 	// reseting price for room depending on the date
				if(idealRoom.firstPrice>=hotel.rooms.get(i).price) {	//comparing clients budget to rooms price
					if(hotel.rooms.get(i).compareDate(dayStart, dayEnd)==0) {
						count=count+1;
					}
				}	
			}
		}
		
		Room idealRoomList[]=new Room[count];
		if(count==0) {
			return null;
		}
		
		count=0;
		
		for(int i=0; i<hotel.rooms.size(); i++) {
			if(idealRoom.numOfBeds==hotel.rooms.get(i).numOfBeds) {
				if(idealRoom.price>=hotel.rooms.get(i).price) {
					if(hotel.rooms.get(i).compareDate(dayStart, dayEnd)==0) {
						idealRoomList[count]=hotel.rooms.get(i);
						count=count+1;
					}
				}
			}
		}
			
		return idealRoomList;
	}
}