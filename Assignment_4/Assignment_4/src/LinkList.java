public class LinkList {
	Link startLink;
	Link endLink;
	int length;
	
	public LinkList(Link a) {
		startLink = a;
		endLink = a;
		a.next=null;
		a.prev=null;
		length=1;
	}
	public LinkList() {
		startLink = null;
		endLink =null;
		length=0;
	}
	public void addToEnd(int a, int b) {
		Link link=new Link(a,b);
		addToEnd(link);
	}
	public void addToEnd(Link link) {
		//if the list is empty update the first link
		if(length==0) { 
			startLink=link;
			endLink=link;
			link.next=null;
			link.prev=null;
		}
		else {
			endLink.next = link;
			link.prev=endLink;
			link.next=null;
			endLink= link;
		}
		length ++;
	}
	
}