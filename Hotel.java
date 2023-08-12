

import java.time.LocalDate;
import java.util.ArrayList;

public class Hotel {
	String name;
	String location;
	ArrayList<Room>  rooms = new ArrayList<Room>();
	
	Hotel(String name, String loc,Room room[]){
		this.name=name;
		location=loc;
		
		for(int i=0; i<room.length; i++) {
			rooms.add(room[i]);
		}
	}
	
	public String toStringForFile() {
		String string=name+" at "+location+"\n\t";
		for(int i=0; i<rooms.size(); i++) {
			string=string+"Num: "+(i+1)+" Code: "+rooms.get(i).getCode()+
			" Dates:\n\t"+
			rooms.get(i).getDateForFile();
		}
		return string;
	}
	
	public String toString() {
		return name+" at "+location+"\n\t"+rooms+"\n";
	}
}
