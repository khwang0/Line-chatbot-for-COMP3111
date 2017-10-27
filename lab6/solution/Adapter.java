package solution;

public class Adapter {
	public static final String[] BEVERAGES = new String[] {
			"Caffè Americano", "Caffè Mocha", "Caffè Latte", 
			"Cappuccino", "Caramel Macchiato", "Espresso" };

	public String getBeverage(String s){
		// Return null if nothing is similar
		int min = Integer.MAX_VALUE;
		String min_string = null;
		// Compare each word in the array with the query word
		for ( String b : BEVERAGES ) {
			WagnerFischer ob = new WagnerFischer( s, b );
			int dist = ob.getDistance();
			// Record the minimum
			if ( dist <= 3 && dist < min ) {
				min = dist;
				min_string = b;
			}
		}
		return min_string;
	}
}
