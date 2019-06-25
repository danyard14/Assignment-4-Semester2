import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class HashTable {

	HashList [] mainArray;
		
	public HashTable(String m2) {
		checkInputInt(m2);
		mainArray=new HashList[Integer.parseInt(m2)];
		for(int i=0;i<mainArray.length;i++)
			mainArray[i]=new HashList();
	}

	public void updateTable(String string) {
		//updating the table according to the file name we get in the function 
		File inputFile = new File (string); 
		try {
			//read the passwords one by one and determine their key value.
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				int k=getFunctionKey(str);
				mainArray[hashFunction(k)].addToEnd(k);
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR");
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
	
	public boolean isThere(int k) {
		return mainArray[hashFunction(k)].isThere(k);	
	}
	
	public int hashFunction(int k)
	{
		return k%mainArray.length;
	}

	public String getSearchTime(String string) {
		long beginning =System.nanoTime();
		File inputFile = new File (string); 
		try {
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				this.isThere(getFunctionKey(str));
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR bloom constructor");
		}
		Double time = (System.nanoTime()-beginning)/1000000.0;
		DecimalFormat format = new DecimalFormat("0.0000");
		return format.format(time);
	}
	
	private void checkInputInt(String string) {
		Integer.parseInt(string);
	}
}
