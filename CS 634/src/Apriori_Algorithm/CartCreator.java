package Apriori_Algorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.opencsv.CSVWriter;


public class CartCreator {

	public static void main(String[] args) throws IOException {
		String items[] = {"Diapers", "Rice", "Tomatoes", "Pasta", "Juice", "Eggs", "Milk", "Bread", "Bagels", "Cheese",
				"Butter", "Soda", "Lotion", "Perfume", "Books", "Pens", "Pencils", "Notebook", "Onion", "Mask", "Paper Toilets",
				"Toilet Paper", "Pants", "Shirts", "Cell Phone", "Plates", "Chairs", "Cups", "Spoon", "Knifes"};
		
		CSVWriter writer = new CSVWriter(new FileWriter("G:\\My Drive\\School\\Grad\\CS 634\\Midterm Project\\cart1.csv"));

		for (int x = 0; x < 20; x++) {
			System.out.print( x + " ");
			ArrayList<String> order = new ArrayList<String>();
			for (int y = 0; y < items.length; y++) {		
				Random randGet = new Random();
				double rand = randGet.nextDouble();
				//System.out.printf("%f%n", rand);
				if (randGet.nextDouble() <= 0.3 ) {
					order.add(items[y]);
					System.out.printf("%s ", items[y]);
				}
			}
			order.trimToSize();
			System.out.printf("%n");
			writer.writeNext(GetStringArray(order));
		}
		
	     writer.close();
	}
	
	
	private static String[] GetStringArray(ArrayList<String> arr) { 
  
        // declaration and initialise String Array 
        String str[] = new String[arr.size()]; 
  
        // Convert ArrayList to object array 
        Object[] objArr = arr.toArray(); 
  
        // Iterating and converting to String 
        int i = 0; 
        for (Object obj : objArr) { 
            str[i++] = (String)obj; 
        } 
  
        return str; 
    } 
}
