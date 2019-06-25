import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//import org.omg.CORBA.SystemException;

//import javax.management.RuntimeErrorException;
import java.text.DecimalFormat;

public class BTree {
	
	//fields
	BTreeNode root;
	int t;
	
	//constructor
	public BTree(String tVal) {
		checkInputInt(tVal);
		t=Integer.parseInt(tVal);	
		root=null;
	}
	
	public void insert(String password) {
		if (root==null) {
			root = new BTreeNode(t);
		}
		if(root.numOfkeys>=2*t-1)
			root=root.split();
		root.insert(password.toLowerCase());
	}	
	
	public void delete(String string) {
		//making sure the key we are trying to delete is in the tree.
		if(root==null || root.search(string)==false ) throw new RuntimeException("The password you want to delete does not exist in the tree.");
		// if the tree is only a root that contains one node, make this node null (the tree contains only the node being deleted). 
		if(root.isLeaf & root.numOfkeys==1) root=null;
		// delete key.
		else root.delete(string);
	}
	
	public boolean search(String password) {
		//if the tree is empty print appropriate message and return false
		if (root==null) {
			System.out.println("root=null");
			return false;
		}
		return root.search(password);
	}
	
	// create a tree using all passwords from a given document
	public void createFullTree(String string) {
		File inputFile = new File (string); 
		try {
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				str.toLowerCase();
				this.insert(str);
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR BTree");
		}
	}
	
	// print the tree in inOrder.
	public String inOrderPrint(){
		if(root==null)
			return "";
		return root.inOrderPrint();
	}

	public String getSearchTime(String string) {
		long beginning =System.nanoTime();
		File inputFile = new File (string); 
		try {
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				this.search(str);
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
	
	public String toString() {
		return this.inOrderPrint();
	}
	
	public void deleteKeysFromTree(String string) {
		// delete from tree all passwords from a given document
		File inputFile = new File (string); 
		try {
			Scanner scan = new Scanner(inputFile);		
			while(scan.hasNextLine()){
				String str = scan.nextLine();
				this.delete(str);
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR BTree");
		}		
	}
	
	private void checkInputInt(String string) {
		Integer.parseInt(string);
		if(Integer.parseInt(string)<2)
			throw new RuntimeException("The t you want to use is not legal.");
	}
}
