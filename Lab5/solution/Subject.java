package solution;

import java.util.ArrayList;
import java.util.List;

public class Subject {
	private List<Observer> observers;
	private String message;
	private boolean changed;

	public Subject() {
		observers = new ArrayList<Observer>();
		message = null;
		changed = false;
	}

	public void register(Observer obj) {
		if ( !observers.contains(obj) ) observers.add(obj);
	}

	public void unregister(Observer obj) {
		observers.remove(obj);
	}

	public void notifyObservers() {
		// if nothing is changed, we can skip this
		if ( !changed ) return;
		
		// we need to use a new list because observers may unsubscribe from the list
		// and this might cause unexpected behaviours
		// forEach is a faster way to loop over a list (see Java 1.8 lambda expression)
		new ArrayList<>(observers).forEach( o -> o.update() );
		
		// another approach to do it is to traverse the list starting from the end
		for ( int i = observers.size()-1; i >= 0; --i ) {
			observers.get(i).update();
		}
		
		// reset parameter
		changed = false;
	}

	public void setMessage(String msg) {
		this.message=msg;
		this.changed=true;
		notifyObservers();
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public List<Observer> getQueue() {
		return observers;
	}
}
