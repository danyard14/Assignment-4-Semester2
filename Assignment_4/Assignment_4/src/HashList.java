public class HashList {
	HashListElement startHashListElement;
	HashListElement endHashListElement;
	int length;
	
	public HashList(HashListElement a) {
		startHashListElement = a;
		endHashListElement = a;
		a.next=null;
		a.prev=null;
		length=1;
	}
	public HashList() {
		startHashListElement = null;
		endHashListElement =null;
		length=0;
	}
	public void addToEnd(int k) {
		HashListElement element=new HashListElement(k);
		addToEnd(element);
	}
	public void addToEnd(HashListElement element) {
		//if the list is empty update the first HashListElement
		if(length==0) { 
			startHashListElement=element;
			endHashListElement=element;
			element.next=null;
			element.prev=null;
		}
		else {
			endHashListElement.next = element;
			element.prev=endHashListElement;
			element.next=null;
			endHashListElement= element;
		}
		length ++;
	}
	
	public boolean isThere(int k) {
		if(length==0)
			return false;
		HashListElement current=startHashListElement;
		while(current!=null) {
			if(current.key==k)
				return true;
			current=current.next;
		}
		return false;
	}
}
