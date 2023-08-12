

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class Client {
	   
    public static void main(String[] args) throws IOException {       
    	Scanner input = new Scanner(System.in);
		String choice=null;
		
		boolean swift=true;
		
		Socket socket=null;
		InputStreamReader inStrRdr = null;
		OutputStreamWriter outStrWrtr = null;
		BufferedReader buffRdr = null;
		BufferedWriter buffWrtr = null;
		
		try {
			socket = new Socket("localhost",1234);
			inStrRdr = new InputStreamReader(socket.getInputStream());
			outStrWrtr = new OutputStreamWriter(socket.getOutputStream());
		    buffRdr = new BufferedReader(inStrRdr);
			buffWrtr = new BufferedWriter(outStrWrtr);
			
			while(swift) {
				
				menu();
				choice=input.nextLine();
				String counter[]=choice.split(" ");
				while(choice.equalsIgnoreCase("EXIT")==false && counter.length!=3) {	//program safety when input is not correct
					System.out.println("Wrong Input.\nPlease read the instructions carefully.");
					choice=input.nextLine();
					counter=choice.split(" ");
				}
				
				if(choice.equalsIgnoreCase("EXIT")) {
					buffWrtr.write(choice);
					buffWrtr.flush();
					swift = false;
					break;
				}
				else {	//that means client entered an area

					try {
						
						buffWrtr.write(choice); //write client's choice to buff so server can read it
						buffWrtr.newLine();
						buffWrtr.flush();
						
						menupt2();			//asks for date range specifically date1.
						choice=check1(counter,input, choice);
						counter=choice.split(" ");
						LocalDate date1=LocalDate.of(Integer.parseInt(counter[2]),Integer.parseInt(counter[1]), Integer.parseInt(counter[0]));
						
						menupt3();
						counter=check2(counter,input,date1).split(" "); //program safety
						choice=choice+" "+ counter[0]+" "+counter[1]+" "+counter[2];
						
						buffWrtr.write(choice); //write client's dates
						buffWrtr.newLine();
						buffWrtr.flush();
						
						System.out.println("\nResults:");
						int length=Integer.parseInt(buffRdr.readLine()); //number of hotels available for client's needs			
						int l=0;
						
						if(length==1) { //There are NO room results
							System.out.println(buffRdr.readLine());
						}
						else {	//There are room results
							for(int i=0; i<length; i++) { //printing the options
								if((i%2)==0) {
									l=l+1;
									System.out.println("Option:"+l+" "+buffRdr.readLine());
								}
								else {
									System.out.println(buffRdr.readLine());
								}
							}
							
							//phase two where client decides whether to book a room or not
							System.out.println("\nIf you wish to book any of the options above,\nwrite the num of the option otherwise press (n).");
							choice=input.nextLine();
							if(!choice.equalsIgnoreCase("N")) { // client wants to book the room
								
								while((Integer.parseInt(choice)) > (length/2) || (Integer.parseInt(choice)) < 1) { //wrong option
									System.out.println("Incorrect input try again.");
									choice=input.nextLine();
								}
								buffWrtr.write(choice);	//sending our option 
								buffWrtr.newLine();
								buffWrtr.flush();
								
								
								System.out.println(buffRdr.readLine());
							}else {
								buffWrtr.write("0"); 
								buffWrtr.newLine();
								buffWrtr.flush();
							}
						}
						
					} catch(IOException e) {
						e.printStackTrace();
					} 
				}
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
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
			    if(inStrRdr != null){
			    	inStrRdr.close();
			    }
			    if(outStrWrtr != null){
			    	outStrWrtr.close();
			    }
			}catch (IOException e){
			    e.printStackTrace();
			}		
		}
		
		System.out.print("Program exited succesfully. :)");
    }
	
    public static String check1(String[] words, Scanner input,String choice) {
    	choice=input.nextLine();
		words=choice.split(" ");
		LocalDate today= LocalDate.now();
		
    	if(words.length!=3) {										//input needs to have 3 numbers!
    		String string="-1 -2 -3";
    		words=string.split(" ");
    		System.out.println("Date needs to be seperated by space.\n");
    	}
    	
		if((Integer.parseInt(words[0])<1 || Integer.parseInt(words[0])>31) 			// 0<day<31
				||(Integer.parseInt(words[1])<1 || Integer.parseInt(words[1])>12) 	//0<month<13
				|| (Integer.parseInt(words[2])<0)									//year>0
				) {
			System.out.println("Incorrect input.\nPlease try again.");
			System.out.print("from:");
			choice=check1(words,input,choice);										//checking again
			words=choice.split(" ");
		}
		int day=Integer.parseInt(words[0]);
		int month=Integer.parseInt(words[1]);
		int year=Integer.parseInt(words[2]);
		LocalDate date1 = LocalDate.of( year, month , day );
		
		if(date1.getDayOfYear()<today.getDayOfYear()) {		//this means date1 belongs 	[First checking the year]
			if(date1.getYear()<=today.getYear()) {			//to the past				[Then month]
				System.out.println("The date:"+date1.getDayOfMonth()+
				"/"+date1.getMonthValue()+"/"
				+date1.getYear()+" belongs in the past. :(");
				choice=check1(words,input,choice);									// checking again
				words=choice.split(" ");
			}
		}
		choice=null;
		choice=words[0]+" "+words[1]+" "+words[2];
		return choice;
    }
    
    public static String check2(String[] words, Scanner input, LocalDate date1) {
    	String choice=input.nextLine();
		words=choice.split(" ");
		
    	if(words.length!=3) {										//input needs to have 3 numbers!
    		String string="-1 -2 -3";
    		words=string.split(" ");
    		System.out.println("Date needs to be seperated by space.\n");
    	}
    	
		if((Integer.parseInt(words[0])<1 || Integer.parseInt(words[0])>31) 			// 0<day<31
				||(Integer.parseInt(words[1])<1 || Integer.parseInt(words[1])>12) 	//0<month<13
				|| (Integer.parseInt(words[2])<0)									//year>0
				) {
			System.out.println("Incorrect input.\nPlease try again.");
			System.out.print("till:");
			choice=check2(words,input,date1);										//checking again
			words=choice.split(" ");
		}
		
		int day=Integer.parseInt(words[0]);
		int month=Integer.parseInt(words[1]);
		int year=Integer.parseInt(words[2]);
		LocalDate date2 = LocalDate.of( year, month , day );
		
		if(date2.getDayOfYear()<date1.getDayOfYear()) {		//this means date2 belongs 
			if(date2.getYear()<=date1.getYear()) {			//before date1....
				System.out.println("The date:"+date2.getDayOfMonth()+
				"/"+date2.getMonthValue()+"/"
				+date2.getYear()+" is before your starting date:"
				+date1.getDayOfMonth()+
				"/"+date1.getMonthValue()+"/"
				+date1.getYear()+ ". :(");
				choice=check2(words,input,date1);										//checking again
				words=choice.split(" ");
			}
		}
		choice=null;
		choice=words[0]+" "+words[1]+" "+words[2];
		return choice;
    }
    
	public static void menu() {
		System.out.println("\n<<< Welcome to our main menu. >>>\n");
		System.out.println("In order to search the perfect hotel for you.");
		System.out.println("Enter the area you are searching a hotel for.");
		System.out.println("The number of beds.");
		System.out.println("And the maximum price.\n");
		System.out.println("If you wish to exit enter 'Exit'.");
	}
	
	public static void menupt2() {
		System.out.println("\nNext enter the date range of your choice.");
		System.out.println("Example of input: Day Month Year\n[Divided by space!]");
		System.out.print("You would like to stay from:");
	}

	
	public static void menupt3() {
		System.out.print("till:");
	}
}