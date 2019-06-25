import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class BloomFilter {
	
	boolean[] bloomArray;
	LinkList listVar;
	
	public BloomFilter(String m1, String fileName) {
		checkInputInt(m1);
		//updating bloomfilter's fields.
		bloomArray = new boolean[Integer.parseInt(m1)]; 
		listVar = new LinkList();
		 //initializing all array values with false.
		for(int j=0; j<bloomArray.length; j++) {
			bloomArray[j] =false;
		}
		File inputFile = new File (fileName); 
		try {
			//reading the lines and adding alpha and beta to the list
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				int i = str.indexOf("_");
				//adding links containing alpha and beta values to the list.
				listVar.addToEnd(Integer.parseInt(str.substring(0, i)),Integer.parseInt(str.substring(i+1))); 
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR bloom constructor");
		}
	}

	public void updateTable(String string) {
		//updating the table according to the file name we get in the function 
		File inputFile = new File (string); 
		try {
			//read the passwords one by one and determine their key value.
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				int k =getFunctionKey(str);
				updateBloomArray(k, str);
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR update table");
		}
	}

	private int getFunctionKey(String line) {
		//converting string to number in 256 base
		long k =(int)(line.charAt(0));
		for(int i=1;i<line.length();i++) {
			int a=(int)(line.charAt(i));
			k=(a+256*k)%15486907;
		}
		return (int)k;
	}
	
	private void updateBloomArray(int k, String str){
		//updating the bloom filter by the k we get in the function for every alpha and beta we have
		Link current = listVar.startLink;	
		while(current!=null) {			
			long functionResult = ((current.keyAlpha*k+current.keyBeta)% 15486907)%(bloomArray.length);
			bloomArray[(int)functionResult]=true;
			current=current.next;
		}
	}
	
	public String getFalsePositivePercentage(HashTable hashTable, String string) {
		// calculating all the passwords that were rejected by mistake, meaning the bloomfilter recognized them as bad even though they are good.
		Double rejectedByMistake =(double) 0, goodPasswords=(double) 0; 
		File inputFile = new File (string); 
		try {
			Scanner scan = new Scanner(inputFile);	
			while(scan.hasNextLine()){
				int k =getFunctionKey(scan.nextLine());
				//if bloom filter rejects the password 
				if(toReject(k)) { 
					//if the password is not among the bad passwords
					if(hashTable.isThere(k)!=true) 
						rejectedByMistake++;
				}
				//if the password is not among the bad passwords
				if(hashTable.isThere(k)!=true) 
					goodPasswords++;
			}	
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR false positive");
		}
		return String.valueOf(rejectedByMistake/goodPasswords);
	}
	
	public String getRejectedPasswordsAmount(String string) {
		//counting the amount of rejected passwords
		Integer passwordCounter=0;
		File inputFile = new File (string); 
		try {
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				int k =getFunctionKey(str);
				if (toReject(k)) {
					passwordCounter++;
				}
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR rejected password");
		}
		return passwordCounter.toString();
	}
		
	private boolean toReject(int k) {
		 //getting the hash functions variables from the list in the bloom filter's field.
		Link current = listVar.startLink;
		while(current!=null) { 
			int functionResult = ((current.keyAlpha*k+current.keyBeta)% 15486907)%(bloomArray.length);
			 //if the function leads to a false cell in the array, the password should not be rejected. 
			if(bloomArray[functionResult]!=true) {
				return false;
			}
			current=current.next;
		}
		return true;
	}
	
	private void checkInputInt(String string) {
		Integer.parseInt(string);
	}
}