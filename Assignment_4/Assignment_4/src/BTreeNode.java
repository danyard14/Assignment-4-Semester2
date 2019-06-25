public class BTreeNode {
	
	//fields
	String[] nodeArray;
	BTreeNode[] sonsArray;
	int t;
	int numOfkeys;
	int numOfSons;
	boolean isLeaf;
	BTreeNode father;
	
	//constructor
	public BTreeNode(int t) {
		nodeArray = new String[(2*t)-1];
		sonsArray = new BTreeNode[2*t];
		numOfSons=0;
		numOfkeys=0;
		isLeaf=true;
		father=null;
		this.t=t;
		this.initializeArraysToNull();
	}	
	
	public void insert(String password) {
		BTreeNode current=this;			
		boolean found=false;
		//split the current node if it is full, we will traverse its father (current) so we could determine which son to go to.
		if(current.numOfkeys>=2*t-1) {
			current = split(); 			
		}
		//stop condition for the recursion.
		if (current.isLeaf) {			
			current.insertToLeaf(password);
		}
		else {
			//if the current node is not a leaf find the correct son of the node to insert to.
			int i=0;
			for (; i<current.numOfkeys & !found; i++) {	
				 //the password should be inserted before this value in a lexicographic order.
				if (current.nodeArray[i].compareTo(password)>=0) {
					found=true;
					current.sonsArray[i].insert(password);
				}
			}
			//if it is the last son
			if(!found) {
				current.sonsArray[i].insert(password);
			}			
		}
	}
	
	private void insertToLeaf(String password) {
		//find the first value that is bigger than the key inserted, we want the key to be at its place and move 
		//all other values from the right one step to the right.
		int i=0; boolean found =false;
		for (;!found & i<numOfkeys;i++) {				
			if(nodeArray[i].compareTo(password)>=0) {
				found=true;
				//moving each value one step to the right.
				for (int j=numOfkeys;j>i;j--) {		
					nodeArray[j]=nodeArray[j-1];
				}
				//placing the inserted value to the right spot.
				nodeArray[i]=password;				
				numOfkeys++;
			}
		}
		//if we did not find a bigger value that the inserted value should be inserted at the end.
		if(!found) {							
			nodeArray[i]=password;					
			numOfkeys++;
		}		
	}
	
	public boolean search(String password) {
		for(int i=0; i<numOfkeys; i++) {
			//positive if nodeArray[i] > password
			if (nodeArray[i].compareTo(password)>0) { 
				if(isLeaf) return false; 
				return sonsArray[i].search(password);
			}
			if (nodeArray[i].compareTo(password)==0) return true;
		}		
		//negative if nodeArray[i] < password
		if (nodeArray[numOfkeys-1].compareTo(password)<0) {
			if(isLeaf) return false;
			return sonsArray[numOfkeys].search(password);
		}
		return false;
	}
	
	//function that splits the node and raises the middle value to the father node, then returns the updated father node.
	public BTreeNode split() {												
		//creating two sons for the split
		BTreeNode leftSon = new BTreeNode(t); BTreeNode rightSon = new BTreeNode(t);  
		
		if(!isLeaf) {	
			//new nodes will be initialized as leafs as a default option, 	
			// child nodes will be at the same depth of the split node, therefore be leafs iff the original was a leaf.
			leftSon.isLeaf=false;	rightSon.isLeaf=false; 							
		}
		//update the arrays of the new sons (both passwords array and sons array) using "update sons" function.
		updateSons(this, leftSon, rightSon);										
		BTreeNode splittedNode =this;
		BTreeNode fatherOfsplit = splittedNode.father;
		int i=0;
		if(fatherOfsplit==null) {
			fatherOfsplit =splittedNode.createNewFather(splittedNode.nodeArray[t-1]);
		}
		else {
			i=splittedNode.updateFather(splittedNode.nodeArray[t-1]);
		}
		//updating the father field to point to the original father of the original node.
		leftSon.father=fatherOfsplit; rightSon.father=fatherOfsplit; 					
		//updating the father's sonsArray
		fatherOfsplit.sonsArray[i]=leftSon;												
		fatherOfsplit.sonsArray[i+1]=rightSon;
		return fatherOfsplit;	//would be the same if we returned right son's father.														
	}
	//15
	
	private BTreeNode createNewFather(String password) {
		//creates and returns a father node with one key and 2 sons
		BTreeNode newFather = new BTreeNode(t);
		newFather.isLeaf=false;
		newFather.father=null;
		newFather.nodeArray[0]=password;
		newFather.numOfkeys=1;
		newFather.numOfSons=2;
		return newFather;	
	}
	
	// adding to the father the given key and returns the position it was added at 
	private int updateFather(String password) {
		boolean found =false; int i=0; int temp=0;
		for(; i<father.numOfkeys & !found; i++) { 
			//find the first value that is bigger than the key inserted, we want the key to be at its place
			if(father.nodeArray[i].compareTo(password)>=0) {
				found=true;
				//moving each key to the right by one step to make place for the new key.
				for(int j=father.numOfkeys;j>i;j--) {			
					father.nodeArray[j]=father.nodeArray[j-1]; 
					father.sonsArray[j+1]=father.sonsArray[j];
				
				}
				father.nodeArray[i]=password; father.numOfkeys++; father.numOfSons++;
				temp=i;
			}
		}
		//if we didn't find a bigger number than the inserted value, then we should add the value at the end of the father's node array.
		if(found==false) { 	father.nodeArray[i]=password; father.numOfkeys++; father.numOfSons++; return i; }				
			//will be the only place to insert the value if we created a new root. 
		return temp;
	}
	
	private void updateSons(BTreeNode originalNode, BTreeNode leftSon, BTreeNode rightSon ) {
		int i=0;
		//copying the left part of the password array to the left son's array,
		//not copying the key that goes up to the father.
		for (; i<t-1;i++) { 															
			leftSon.nodeArray[i]=originalNode.nodeArray[i];	leftSon.numOfkeys++;		
			if(!originalNode.isLeaf) {
				//updating the number of passwords inserted to the node.
				leftSon.sonsArray[i]=originalNode.sonsArray[i]; leftSon.numOfSons++;	
				leftSon.sonsArray[i].father=leftSon;
			}																		
		}				
		if(!originalNode.isLeaf) {leftSon.sonsArray[i]=originalNode.sonsArray[i]; leftSon.numOfSons++; leftSon.sonsArray[i].father=leftSon;}		//updating the last cell of the sons array (there's always one more cell in the sons array than in the node array and therefore this action is needed.
		//copying the right part of the password array to the right son's array, 
		//not copying the key that goes up to the father.
		int j=t; int k=0;
		for (; j<originalNode.numOfkeys; j++,k++) { 									
			rightSon.nodeArray[k]=originalNode.nodeArray[j]; rightSon.numOfkeys++;		
			if(!originalNode.isLeaf) {
				//updating the number of passwords inserted to the node.
				rightSon.sonsArray[k]=originalNode.sonsArray[j]; rightSon.numOfSons++;	
				rightSon.sonsArray[k].father=rightSon;
			}
		}
		if(!originalNode.isLeaf) {rightSon.sonsArray[k]=originalNode.sonsArray[j]; rightSon.numOfSons++;		//updating the last cell of the sons array 
		rightSon.sonsArray[k].father=rightSon;}
	}
	
	private void initializeArraysToNull() {
		int i=0;
		for(; i<nodeArray.length; i++) {
			nodeArray[i]=null;
			sonsArray[i]=null;
		}
		sonsArray[i]=null;
	}

	public String inOrderPrint() {
		//returns a string of the tree by in order
		String output= inOrderPrint(0,"");
		if(output.length()==0) return null;
		return output.substring(0,output.length()-1);
	}

	public String inOrderPrint(int depth, String output) {
		//returns a string of the tree that is rooted by the current node by in order
		if(this.isLeaf) return createString(depth, output); //if we reached a leaf
		int i=0;
		for(;i<numOfkeys;i++) {						
			//left child
			output = sonsArray[i].inOrderPrint(depth+1,output); 
			//me
			output = output + nodeArray[i]+'_'+depth+',';		
		}
		//right child
		output = sonsArray[i].inOrderPrint(depth+1,output);             
		return output;
	}

	private String createString(int depth, String output) {
		//returns a string of the node according to the depth
		for(int i=0; i<this.numOfkeys;i++) {
			output=output+this.nodeArray[i]+'_'+depth+',';
		}
		return output;
	}
	
	//function that deletes from a node given that it is a leaf, (assuming it has more than minimal number of keys)
	private void deleteFromLeaf(String string) {
		boolean found=false;
		for(int i=0; i<numOfkeys & !found; i++) {
			if(nodeArray[i].compareTo(string)==0) {
				for(int j=i;j<numOfkeys-1;j++) {
					nodeArray[j]=nodeArray[j+1];
				}
				nodeArray[numOfkeys-1]=null;
				found=true;
			}
		}
		this.numOfkeys--;
	}

	// function that merges the two nodes on both sides of the given index, returns true if the father node was united with sons.
	public boolean merge(int position) {
		// assigning new names for the variables for convenience.
		BTreeNode parent = this; BTreeNode leftChild = this.sonsArray[position]; BTreeNode rightChild = this.sonsArray[position+1];
		
		// if the number of keys in parent is only one, the new merged node will be at the 
		// depth of the parent (new root in case of parent==root).
		if (parent.numOfkeys==1) {
			mergeCase1(parent, leftChild, rightChild);
			return true;
		}
		// number of keys in parent is more than one.
		else {
			mergeCase2(parent, leftChild, rightChild, position);
			return false;
		}
	}
	
	// function that merges a father node with its to sons in case of a father with size 1.
	private void mergeCase1(BTreeNode parent, BTreeNode leftChild, BTreeNode rightChild) {
		// copying the content in left child to the beginning of parent node,
		// and the content of right child to the end of the parent node.
		String middleKeyFromParent = parent.nodeArray[0];
		int i=0; int j=leftChild.numOfkeys+1;
		for(; i<leftChild.numOfkeys; i++, j++) {	
			parent.updatePointers(leftChild, rightChild, i, j);
		}
		parent.sonsArray[i]=leftChild.sonsArray[i];
		if(!leftChild.isLeaf) {leftChild.sonsArray[i].father=parent;}
		parent.sonsArray[j]=rightChild.sonsArray[i];
		if(!rightChild.isLeaf) {rightChild.sonsArray[i].father=parent;}
		parent.nodeArray[leftChild.numOfkeys] = middleKeyFromParent;
		parent.numOfkeys = leftChild.numOfkeys+rightChild.numOfkeys+1;
		parent.numOfSons =	leftChild.numOfkeys+rightChild.numOfkeys+1+1;
		// if the children of this node are leafs then after the change it will also be a leaf.
		if(rightChild.isLeaf) this.isLeaf=true;
	}

	private void updatePointers(BTreeNode leftChild, BTreeNode rightChild, int i, int j) {
		//updates the pointers of the current node - it's keys and sons
		BTreeNode parent = this;
		// left child
		parent.nodeArray[i]=leftChild.nodeArray[i];
		parent.sonsArray[i]=leftChild.sonsArray[i];
		if(!leftChild.isLeaf) {leftChild.sonsArray[i].father=parent;}
		// right child
		parent.nodeArray[j]=rightChild.nodeArray[i];
		parent.sonsArray[j]=rightChild.sonsArray[i];
		if(!rightChild.isLeaf) {rightChild.sonsArray[i].father=parent;}
	}
	
	// function that merges two nodes with minimal number of keys with each other while inserting a middle value from their shared parent.
	// the merged node will be the left child manipulated.
	private void mergeCase2(BTreeNode parent, BTreeNode leftChild, BTreeNode rightChild, int position) {			
		// inserting the "middle value" from the parent node, will be at the end of the leftChild node.
		leftChild.nodeArray[leftChild.numOfkeys] = parent.nodeArray[position];
		// copying the right child's content to the left child after insertion of the middle value from parent.
		int i=0; int j=leftChild.numOfkeys+1;
		// index j will "run" on the left child's array (therefore it starts from the spot after the middle value taken from father).
		// index i will "run" on the right child's array (therefore it starts from 0).
		for(; i<rightChild.numOfkeys; i++,j++) {
			leftChild.nodeArray[j]=rightChild.nodeArray[i];
			leftChild.sonsArray[j]=rightChild.sonsArray[i];
		}
		leftChild.sonsArray[j]=rightChild.sonsArray[i];
		//updating the size fields of left child.
		if(!leftChild.isLeaf)leftChild.numOfSons = leftChild.numOfkeys + rightChild.numOfkeys +1+1;
		else leftChild.numOfSons=0; 
		leftChild.numOfkeys = leftChild.numOfkeys + rightChild.numOfkeys +1;
		
	
		// making adjustments in the parent node, meaning moving all values to the left by one spot.
		int k=position;
		for (;k<parent.numOfkeys-1; k++) {
			parent.nodeArray[k]=parent.nodeArray[k+1];
			parent.sonsArray[k+1]=parent.sonsArray[k+2];
		}
		parent.nodeArray[k]=null; parent.sonsArray[k+1]=null;	
		parent.numOfkeys--; parent.numOfSons--;
	}
	
	public void delete(String password){
		boolean fatherWasUnitedWithSons =false;
		// in each node we reach, we check to see if the password we want to delete is in the node (position in node gives -1 if it isn't there).
		int position = this.getPositionInNode(password);
		// in this point we assume we reached a leaf that has more than minimum number of keys, therefore we can delete from it.
		if(this.isLeaf & position>-1) {	this.deleteFromLeaf(password);	}
		// if we found the node containing the password we want to delete, and it is internal (not a leaf).
		else if((position>-1) & !this.isLeaf){	deleteCase1(password, position);	}
		//the password we want to delete is not in the current node, and the current node is internal.
		else if(position==-1 & !this.isLeaf) {
			// find the son to look in for the password. 
				int i=0;  boolean found=false;
				//find the correct son.
				for(;i<numOfkeys & !found; i++) {
					//if we found a value bigger than the password, we should look at its left son.
					if(nodeArray[i].compareTo(password)>0) { found=true;
					}
				}
				if(found == true) i= i-1;
				// if i reached numOfKeys then the son is the last son at the index numOfKeys.
				// if this son is minimal in its number of keys, try to find a brother downer.
				if(sonsArray[i].numOfkeys<t) {
					// if a downer was found, take the closest value at that downer's array and give it to father, 
					// then give the needed node what was in father between the siblings (all that using the function "shift" below).
					fatherWasUnitedWithSons = deleteCase2(fatherWasUnitedWithSons, i);
				}	
				if (fatherWasUnitedWithSons==true) { this.delete(password);	}
				else {
					int j=this.findNextSon(password); this.sonsArray[j].delete(password);
				}
			}
	}
	private int findNextSon(String password) {
		int i=0;
		for (; i<this.numOfkeys; i++) {
			if(this.nodeArray[i].compareTo(password)>0) {
				return i;
			}
		}
		return i;
	}

	private boolean deleteCase2(boolean fatherWasUnitedWithSons, int i) {
		// if a downer was found, take the closest value at that downer's array and give it to father, 
		// then give the needed node what was in father between the siblings (all that using the function "shift" below).
		if(sonsArray[i].getHelpingSibling(i)>-1) {
			sonsArray[i].shift(i,(sonsArray[i].getHelpingSibling(i)));
		}
		else {//no downer was found.
			if(i<this.numOfkeys) {
				fatherWasUnitedWithSons=this.merge(i);
			}
			else {
				fatherWasUnitedWithSons=this.merge(i-1);
			}
		}
		return fatherWasUnitedWithSons;
	}

	private void deleteCase1(String password, int position) {
		// if the left child is not minimal in its number of keys we'll find the maximum value in its sub tree, delete it from it and replace the deleted value by it.
		if(this.sonsArray[position].numOfkeys>t-1) {
				String replacement = this.sonsArray[position].findMax();
				sonsArray[position].delete(replacement);
				nodeArray[position]=replacement;
		}
		// Symmetrically, if the right child is not minimal in its number of keys we'll find the minimum value in its sub tree, delete it from it and replace the deleted value by it. 
		else if(this.sonsArray[position+1].numOfkeys>t-1) {
				String replacement = this.sonsArray[position+1].findMin();
				sonsArray[position+1].delete(replacement);
				nodeArray[position]=replacement;
		}
		// if both sons have minimal number of keys, we'll merge them with the value we want to delete.
		else if(this.sonsArray[position+1].numOfkeys<=t-1 & this.sonsArray[position].numOfkeys<=t-1){
				this.merge(position);//need delete
				this.delete(password); //change to delete normal
		}
	}	

	private void shift(int needHelp, int helper) {	
		//declaration of variables with convenient names.
		BTreeNode parent = this.father;							 
		BTreeNode helperNode = parent.sonsArray[helper];
		BTreeNode needHelpNode = this; 
		
		// the helper is in front of the one getting the help.
		if(needHelp<helper) {						
			shiftCase1(needHelp, parent, helperNode, needHelpNode);
		}	
		// opposite case, helper is "behind need help.
		else if(helper<needHelp) {
			shiftCase2(helper, parent, helperNode, needHelpNode);
		}
	}

	private void shiftCase1(int needHelp, BTreeNode parent, BTreeNode helperNode, BTreeNode needHelpNode) {
		// the given string will be the smallest password in the giver array and therefore be in spot 0 in its array
		String givenString = helperNode.nodeArray[0]; 		
		
		//get the pointers to the strings that will be replaced (and the node).
		BTreeNode givenNode = helperNode.sonsArray[0]; 	String parentString = parent.nodeArray[needHelp];	
		
		//insert the string and the node of the father and the sibling to the node in need in the last place. 
		needHelpNode.nodeArray[needHelpNode.numOfkeys]=parentString;  needHelpNode.sonsArray[needHelpNode.numOfkeys]=givenNode;
		if(givenNode!=null) givenNode.father=needHelpNode; 
		// update the fields of the node in need.
		needHelpNode.numOfkeys++; needHelpNode.numOfSons++; 
		
		// insert the string of the helper to the father.
		parent.nodeArray[needHelp] = givenString; //dont forget update new sons dad if not null
		
		// update helper's fields and move its values one step left.
		helperNode.shiftLeft(); helperNode.numOfkeys--; helperNode.numOfSons--;
	}

	private void shiftCase2(int helper, BTreeNode parent, BTreeNode helperNode, BTreeNode needHelpNode) {
		String givenString2 =helperNode.nodeArray[helperNode.numOfkeys-1]; 
		 String parentString2 = parent.nodeArray[helper]; BTreeNode givenNode2 = helperNode.sonsArray[helperNode.numOfkeys];
		// make place for new node and password in the beginning of need help by shiftRight, then insert those values there.
		needHelpNode.shiftRight();
		needHelpNode.nodeArray[0]=parentString2;
		needHelpNode.sonsArray[0] =givenNode2;
		parent.nodeArray[helper]=givenString2;
		// update the fields of the node that got the donation.
		needHelpNode.numOfkeys++; needHelpNode.numOfSons++;
		// delete those values from the helper node
		helperNode.nodeArray[helperNode.numOfkeys-1]=null;
		helperNode.sonsArray[helperNode.numOfkeys]=null;
		// update helpernode's fields.
		helperNode.numOfkeys--; helperNode.numOfSons--; 
		if(givenNode2!=null) givenNode2.father=needHelpNode;
	}

	private void shiftRight() {
		//shifts all node's values and sons one step to the right
		for(int i=this.numOfkeys; i>0; i--) {
			this.nodeArray[i]=this.nodeArray[i-1];
		}
		 
		for(int i=this.numOfkeys+1;i>=0;i--) {
			this.sonsArray[i+1]=this.sonsArray[i];
		}
	}

	private void shiftLeft() {
		//shifts all node's values and sons one step to the left
		int i=0;
		for(; i<this.numOfkeys-1; i++) {
			this.nodeArray[i]=this.nodeArray[i+1];
			this.sonsArray[i]=this.sonsArray[i+1];
		}
		this.nodeArray[i]=null;
		this.sonsArray[i]=this.sonsArray[i+1];
		this.sonsArray[i+1]=null;
	}

	// a function that determines if one of the siblings of one node can donate to the sibling, returns -1 if it can't find a downer. 
	private int getHelpingSibling(int i) {
		BTreeNode parent = this.father;
		//search in the left sibling if it exists.
		if(i>=1){
			if(parent.sonsArray[i-1].numOfkeys>=t) return (i-1);
		}
		//search in the right sibling if it exists.
		if(i<parent.numOfkeys) {
			if (parent.sonsArray[i+1].numOfkeys>=t) return (i+1);
		}
		return (-1);
	}

	private String findMin() {
		if(isLeaf) return this.nodeArray[0];
		return this.sonsArray[0].findMin();
	}

	private String findMax() {
		if(isLeaf) return this.nodeArray[numOfkeys-1];
		return this.sonsArray[numOfkeys].findMax();
	}

	private int getPositionInNode(String password) {
		//returns the position of password in the current node, if it's not there, -1 will be returned
		for (int i=0; i<this.numOfkeys; i++) {
			if(nodeArray[i].compareTo(password)==0) return i;
		}
		return (-1);
	}
}