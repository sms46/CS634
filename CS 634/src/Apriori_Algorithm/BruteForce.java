package Apriori_Algorithm;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class BruteForce {
	
	private static int minSup = 20;
	private static int minConf = 50;
	private static List<List<String>> trans = new ArrayList<List<String>>();
	private static List<List<String>> lk = new ArrayList<List<String>>();
	private static java.util.Map<List<String>, Float> supportCount = new java.util.HashMap<List<String>, Float>();
	private static int totalTrans;

	public static void main(String[] args) throws CsvValidationException, IOException {
		List<List<String>> k_1 = new ArrayList<List<String>>();
		List<List<String>> cKItemset = new ArrayList<List<String>>();		
		CSVReader reader = new CSVReader(new FileReader("G:\\My Drive\\School\\Grad\\CS 634\\Midterm Project\\cart1.csv"));
		
		String [] nextLine; 
		while ((nextLine = reader.readNext()) != null) {
			totalTrans++;
			trans.add(Arrays.asList(nextLine));
		}
		reader.close();
		
		List<List<String>> itemset1 = get1Itemsets();
		k_1.addAll(getFequentitems(itemset1));
		
		int k = 2;
		while (true) {
			
			k_1.addAll(getktimeSet(itemset1, k));
			cKItemset = getFequentitems(k_1);
			if (cKItemset.isEmpty()) {
				break;
			}			
			lk.addAll(cKItemset);
			k_1.clear();
			k++;
		}
		
		getAssocRules();
	}
	
	
	private static List<List<String>> getktimeSet(List<List<String>> itemset1, int k) {
		
		List<List<String>> subsets = new ArrayList<List<String>>();
		int[] s = new int[k];                  // here we'll keep indices pointing to elements in input array

		if (k <= itemset1.size()) {
		    // first index sequence: 0, 1, 2, ...
		    for (int i = 0; (s[i] = i) < k - 1; i++);  
		    subsets.add(getSubset(itemset1, s));
		    for(;;) {
		        int i;
		        // find position of item that can be incremented
		        for (i = k - 1; i >= 0 && s[i] == itemset1.size() - k + i; i--); 
		        if (i < 0) {
		            break;
		        }
		        s[i]++;                    // increment this item
		        for (++i; i < k; i++) {    // fill up remaining items
		            s[i] = s[i - 1] + 1; 
		        }
		        subsets.add(getSubset(itemset1, s));
		    }
		    //System.out.println(subsets.toString());
		    System.out.println(subsets.size());
		}				
		return subsets;
	}
	
	private static List<String> getSubset(List<List<String>> input, int[] subset) {
		List<String> result = new ArrayList<String>(); 
	    for (int i = 0; i < subset.length; i++) 
	        result.addAll(input.get(subset[i]));
	    return result;
	}	
	
	private static List<List<String>> getFequentitems(List<List<String>> canidates) {
		java.util.Map<List<String>, Integer> map = new java.util.HashMap<List<String>, Integer>();
		for (List<String> i: canidates) {
			float count = 0; 
			for (int n = 0; n < trans.size(); n++) {
				if (!trans.get(n).containsAll(i)) {
					continue;
				}
				else {
					count++;
				}					
			}

			if (count/totalTrans >= minSup/100.0) {
				map.put(i, (int)count);
				supportCount.put(i, (float)count);
			}
		}
		
		List<List<String>> helperList = new ArrayList<List<String>>();
		for (List<String> i : map.keySet()) {
			helperList.add(i); 
		}
		return helperList;
	}
	
	private static List<List<String>> get1Itemsets() {
		java.util.Map<String, Integer> map = new java.util.HashMap<String, Integer>();
		for (List<String> order: trans) {
			for (String i: order) {
				if (map.containsKey(i)) {
					continue;
				}
				float count = 0; 
				for (int n = 0; n < trans.size(); n++) {
					if (!trans.get(n).contains(i)) {
						continue;
					}
					else {
						count++;
					}					
				}
				map.put(i, (int)count);
			}
		}
		List<List<String>> helperList = new ArrayList<List<String>>();
		for (String i : map.keySet()) {
			List<String> holder = new ArrayList<String>();
			holder.add(i); 
			helperList.add(holder);
		}
		
//		String[] helperList = map.keySet().toArray(new String[map.keySet().size()]);
//		//System.out.println(map.keySet());
		return helperList;
	}
	
	private static void getAssocRules() {
		System.out.printf("%nMinimum Support: %d%%%nMinimum Confidence: %d%%%n%nAssociation Rules%n%n", minSup, minConf);
		for (List<String> e: lk) {
			Set<List<String>> subSet = new LinkedHashSet<List<String>>();
			for (int i = 0; i < (1<<e.size()); i++) {
				List<String> temp = new ArrayList<String>();
				for (int j = 0; j < e.size(); j++) {
					if ((i & (1 << j)) > 0){
						temp.add(e.get(j));
						subSet.add(temp);
					}
				}
			}
			List<List<String>> helper = new ArrayList<List<String>>(subSet);
			//System.out.println(helper.toString());
			for (int x = 0; x < helper.size(); x++) {
				for(int y = x + 1; y < helper.size(); y++) {
					if((helper.get(x).size() + helper.get(y).size() < helper.size()) && 
							!(helper.get(x).containsAll(helper.get(y)) || helper.get(y).containsAll(helper.get(x)))) {
						float countXY = 0;
						for (List<String> order: trans) {
							if (order.containsAll(helper.get(x)) && order.containsAll(helper.get(y))) {
								countXY++;
							}							
						}
						String m = helper.get(x).toString();
						String n = helper.get(y).toString();
						
						Float a = supportCount.get(helper.get(x));
						Float confidence = (countXY / a) * 100;
						//System.out.printf("helper size: %d%n%s:%f%n%s:%f%n%s => %s: %f%%%n", helper.size(), m, a, n, countXY, m, n, confidence);
						if (confidence > minConf) {
							System.out.printf("%s => %s: %.2f%%%n",  m, n, confidence);
						}
					}
				}
			}
		}
	}

}
