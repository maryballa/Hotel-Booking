import java.io.FileWriter;
import java.io.IOException;
import java.io.FileWriter;
public class AllTheHotels {
	
	public static Hotel hotels[]= {
				new Hotel("Hotel1","Athens",new Room[]{
						new Room(3,100),
						new Room(1,230)}),
				new Hotel("Hotel2","Lamia",new Room[]{
						new Room(2,50),
						new Room(2,60),
						new Room(1,40)}),
				new Hotel("Hotel3","Athens",new Room[]{
						new Room(2,120),
						new Room(1,50),
						new Room(3,70),
						new Room(1,200)}),
				new Hotel("Hotel4","Lamia",new Room[]{
						new Room(2,45),
						new Room(1,30),
						new Room(3,100)}),
				new Hotel("Hotel5","Lamia",new Room[] {
						new Room(4,200),
						new Room(3,150)})
			};
	
	public static void writeHotelInfo() throws IOException {
		FileWriter file=new FileWriter("file.txt");
		for(int i=0; i<hotels.length; i++) {	//for every hotel
			file.write(hotels[i].toStringForFile());
			file.write("\n");
		}
		file.close();
	}	
}