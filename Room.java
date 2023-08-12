
import java.time.LocalDate;
import java.util.Objects;
import java.util.ArrayList;

public class Room {
	int code;
	int numOfBeds;
	int firstPrice;
	int price;
	
	ArrayList<LocalDate>  dates = new ArrayList<LocalDate>();
//	LocalDate localDate = LocalDate.of( 2012 , 12 , 7 );
	Room(int numOfBeds, int price){
		this.numOfBeds=numOfBeds;
		this.firstPrice=price;
		this.price=price;
	}
	
	public void setPrice(LocalDate dayStart) {
		double x=dateToDouble(dayStart);
		double price= 3/(Math.sin(0.2*Math.pow(0.93*x-6.05,2)+100)+2)+1;
		this.price=(int) (price/2*(double)this.firstPrice);
	}
	
	public double dateToDouble(LocalDate day) {
		double x= (double) day.getMonthValue()+(double) day.getDayOfMonth()/31;
		return x;
	}
	
	public int compareDate(LocalDate dayStart, LocalDate dayEnd) {
		for(int i=0; i<dates.size(); i=i+2) {
			if(dates.get(i).compareTo(dayEnd)<=0 && dates.get(i+1).compareTo(dayStart)>=0) {
				return 1;		//overlapping :/
			}
		}
		return 0;		// not overlapping :)
	}
	
	public void setCode(int code) {
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setDate(LocalDate dayStart, LocalDate dayEnd){
		dates.add(dayStart);
		dates.add(dayEnd);
	}
	
	public ArrayList<LocalDate> getDate(){
		if(dates.size()==0) {
			return null;
		}
		return dates;
	}
	
	public String getDateForFile(){
		String string="";
		for(int i=0; i<dates.size(); i=i+2) {
			string=string+"\tFrom: "+dates.get(i).getDayOfMonth()+"/"+
					+dates.get(i).getMonthValue()+"/"
					+dates.get(i).getYear()
					+" Till: "+dates.get(i+1).getDayOfMonth()+"/"
					+dates.get(i+1).getMonthValue()+"/"
					+dates.get(i+1).getYear()+"\n\t";
		}
		return string;
	}
	
	public String toString() {
		return "Number of beds: "+numOfBeds+" cost: "+price+"$";
	}
}
