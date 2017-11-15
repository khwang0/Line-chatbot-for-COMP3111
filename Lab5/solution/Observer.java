package solution;

public class Observer {
	private int id;
	private Subject subject;

	public Observer(int id) {
		this.id = id;
	}

	public int getID(){
		return id;
	}
	
	public void subscribe(Subject sub) {
		this.subject = sub;
	}
	
	public void unsubscribe() {
		subject.unregister(this);
	}
	
	public void update(){
		// Get the message first
		int order = Integer.valueOf(subject.getMessage());
		// React based on the message
		if ( order >= getID()+7 || order == getID() ) {
			unsubscribe();
		}
	}
}
