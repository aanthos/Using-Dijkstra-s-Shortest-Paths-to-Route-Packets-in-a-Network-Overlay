package cs455.overlay.node;

import cs455.overlay.wireformats.Event;

public interface Node {

	/*
	 * Example interface variable. Can be accessed directly using 
	 * "System.out.println(MyInterface.hello);" if needed.
	 * Works like a static var in a class 
	 */
	//public String hello = "Hello";
	
	/*
	 * method that can be implemented in other classes that implement this one
	 */
	//public void sayHello();
	
	public void onEvent(Event e);
}
