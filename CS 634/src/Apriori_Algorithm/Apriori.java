import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
//import org.apache.
import com.opencsv.exceptions.CsvValidationException;

public class Apriori {
	private static int minSup = 50;
	private static int minConf = 70;
	private static List<List<String>> trans = new ArrayList<List<String>>();
	private static List<List<String>> lk = new ArrayList<List<String>>();
	private static java.util.Map<List<String>, Float> supportCount = new java.util.HashMap<List<String>, Float>();
	private static int totalTrans;

	public static void main(String[] args) throws CsvValidationException, IOException {		
		start();
	}
	
	private static void start() throws CsvValidationException, IOException {
		CSVReader reader = new CSVReader(new FileReader("G:\\My Drive\\School\\Grad\\CS 634\\Midterm Project\\cart1.csv"));
		String [] nextLine; 
		while ((nextLine = reader.readNext()) != null) {
			totalTrans++;
			trans.add(Arrays.asList(nextLine));
		}
		reader.close();
		
		List<List<String>> k_1 = new ArrayList<List<String>>();
		List<List<String>> cKItemset = new ArrayList<List<String>>();
		
		
		k_1.addAll(get1Itemsets());
		//System.out.println(k_1.toString());
		while (true) {
			cKItemset = aprioriGen(k_1);
			
			if (cKItemset.isEmpty()) {
				break;
			}
			
			k_1.clear();
			k_1.addAll(getFequentitems(cKItemset));
			lk.addAll(k_1);
			
//			cKItemset = aprioriGen(k_1);
//			k_1.addAll(getFequentitems(cKItemset));		
			
		}

		//System.out.println(lk.toString());
		//System.out.println(supportCount.toString());
		getAssocRules();
	}
	
	private static List<List<String>> aprioriGen(List<List<String>> list) {
		List<List<String>> cItemset = new ArrayList<List<String>>();
		for(List<String> l1: list){			
			for (List<String> l2: list) {
				//List<String> tempList = new ArrayList<>();
				if ((list.indexOf(l1) == list.indexOf(l2)) || list.indexOf(l1) < list.indexOf(l2)) {
					Set<String> temp = new LinkedHashSet<>(l1);
					temp.addAll(l2);
					List<String> tempList = new ArrayList<>(temp);
//					tempList.addAll(l1);			
//					tempList.addAll(l2);
					Collections.sort(tempList);
					if ((tempList.size() == l1.size() + 1) && !hasInfrequentSubset(tempList, list)) {
						cItemset.add(tempList);
					}
				}
			}			
		}
		return cItemset;		
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
					//System.out.println(trans.get(n).contains(i));
					if (!trans.get(n).contains(i)) {
						continue;
					}
					else {
						//System.out.printf("n=%d%ns=%s%n", n, i);
						count++;
					}					
				}
				if (count/totalTrans >= minSup/100.0) {
					map.put(i, (int)count);
					List<String> helper = new ArrayList<String>();
					helper.add(i);
					supportCount.put(helper, (float)count);
				}
			}
		}
		//System.out.printf("%s%n", map.toString());
		List<List<String>> helperList = new ArrayList<List<String>>();
		for (String i : map.keySet()) {
			List<String> holder = new ArrayList<String>();
			holder.add(i); 
			helperList.add(holder);
		}
		return helperList;
	}
	
	private static boolean hasInfrequentSubset(List<String> candidate, List<List<String>> list) {
		boolean InfrequentSubset = true;
		for (String e : candidate) {
			List<String> temp = new ArrayList<String>();
			temp.add(e);
			for (int x = 0; x < candidate.size() - 1; x++) {
				if (!e.equals(candidate.get(x)) && (candidate.indexOf(e) < x)) {
					temp.add(candidate.get(x));
				}
				if (temp.size() == candidate.size() - 1) {
					InfrequentSubset = true;
					Collections.sort(temp);
					for (int j = 0; j < list.size(); j++) {
						//Collections.sort(list.get(j));
						if (temp.equals(list.get(j))) {
							InfrequentSubset = false;
							break;
						}
					}
				}
			}
		}
		return InfrequentSubset;		
	}
	
	private static List<List<String>> getFequentitems(List<List<String>> canidates) {
		java.util.Map<List<String>, Integer> map = new java.util.HashMap<List<String>, Integer>();
		for (List<String> i: canidates) {
			float count = 0; 
			for (int n = 0; n < trans.size(); n++) {
				//System.out.println(trans.get(n).contains(i));
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
		
		//System.out.println(map.toString());
		List<List<String>> helperList = new ArrayList<List<String>>();
		for (List<String> i : map.keySet()) {
			helperList.add(i); 
			//lk.add(helperList);
		}		
		return helperList;
	}
	
	//Generates Association rules by using 
	private static void getAssocRules() {
		System.out.printf("Minimum Support: %d%%%nMinimum Confidence: %d%%%n%nAssociation Rules%n%n", minSup, minConf);
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
				//System.out.print("} \n");
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
