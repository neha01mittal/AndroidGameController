
public class Controller {

	Vector previousVec;
	Vector currentVec;
	static double start=-1;
	
	/**
	 * process values received from client
	 * @param data
	 */
	public void processData(String data){
		
		String[] parts = data.split("||");
		
		if(parts.length<3) {
			System.out.println("Error in data transmission");
			return;
		}
		double touchCount = Integer.parseInt(parts[0]); 
		double x_coordinate = Integer.parseInt(parts[1]); 
		double y_coordinate= Integer.parseInt(parts[2]);
		
		if(touchCount!=start) {
			currentVec = new Vector(0, 0);
			start= touchCount;
			previousVec = new Vector(x_coordinate, y_coordinate);
		}
		
		else {
			while(currentVec.magnitude()<200){
			currentVec = currentVec.plusEq(previousVec);
			previousVec = currentVec;
			}
		}
		
	}
}
