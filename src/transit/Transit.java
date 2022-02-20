package transit;

import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
		trainZero = new TNode(0);
		TNode busZero = new TNode(0);
		TNode locationZero = new TNode(0);
		trainZero.setDown(busZero);
		trainZero.getDown().setDown(locationZero);
		//Add train stations
		for(int i = 0; i < trainStations.length; i++){
			TNode curNode = trainZero;
			while(curNode.getNext() != null){
				curNode = curNode.getNext();
			}
			curNode.setNext(new TNode(trainStations[i]));
		}
		//Add bus stops
		for(int i = 0; i < busStops.length; i++){
			TNode curNodeBus = busZero;
			while(curNodeBus.getNext() != null){
				curNodeBus = curNodeBus.getNext();
			}
			curNodeBus.setNext(new TNode(busStops[i]));
			TNode curNodeTrain = trainZero;
			while(curNodeTrain.getNext() != null){
				if(curNodeTrain.getNext().getLocation() == busStops[i]){
					curNodeTrain.getNext().setDown(curNodeBus.getNext());
				}
				curNodeTrain = curNodeTrain.getNext();
			}
		}
		//Add locations
		for(int i = 0; i < locations.length; i++){
			TNode curNodeLoc = locationZero;
			while(curNodeLoc.getNext() != null){
				curNodeLoc = curNodeLoc.getNext();
			}
			curNodeLoc.setNext(new TNode(locations[i]));
			TNode curNodeBus = busZero;
			while(curNodeBus.getNext() != null){
				if(curNodeBus.getNext().getLocation() == locations[i]){
					curNodeBus.getNext().setDown(curNodeLoc.getNext());
				}
				curNodeBus = curNodeBus.getNext();
			}
		}
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
		TNode curNode = trainZero;
		//Check if Node is present
		boolean nodeExist = false;
		while(curNode != null){
			if(curNode.getLocation() == station){
				nodeExist = true;
				break;
			}
			curNode = curNode.getNext();
		}
		if(nodeExist){
	    	curNode = trainZero;
			while(curNode.getNext() != null){
				if(curNode.getNext().getLocation() == station){
					curNode.setNext(curNode.getNext().getNext());
					break;
				}
				else if(curNode.getNext().getNext().getNext() == null && curNode.getNext().getNext().getLocation() == station){
					curNode.getNext().setNext(null);
				}
				curNode = curNode.getNext();
			}
		}
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
	    TNode curNode = trainZero.getDown();
		TNode last = trainZero.getDown();

		//Identify last node
		while(last.getNext() != null){
			last = last.getNext();
		}
		//If we want to add last bus stop
		if(last.getLocation() < busStop){
			last.setNext(new TNode(busStop));
		//Connect train & bus
		TNode currentTrain = trainZero; 
		TNode currentBus = trainZero.getDown();
		while (currentTrain != null){
			currentBus = trainZero.getDown();
			while(currentBus != null){
				if(currentTrain.getLocation() == currentBus.getLocation()){
					currentTrain.setDown(currentBus);
					break;
				}
				currentBus = currentBus.getNext();
			}
			currentTrain = currentTrain.getNext();
		}
		//Connect bus & walk
		currentBus = trainZero.getDown();
		TNode currentWalk = trainZero.getDown().getDown(); 
		while (currentBus != null){
			currentWalk = trainZero.getDown().getDown();
			while(currentWalk != null){
				if(currentBus.getLocation() == currentWalk.getLocation()){
					currentBus.setDown(currentWalk);
					break;
				}
				currentWalk = currentWalk.getNext();
			}
			currentBus = currentBus.getNext();
		}
		}
		while(curNode != null){
			//General case
			if(curNode.getLocation() < busStop && curNode.getNext().getLocation() > busStop){
				TNode temp = curNode.getNext();
				curNode.setNext(new TNode(busStop));
				curNode.getNext().setNext(temp);
				//Link to walk layer
				TNode curNodeLoc = trainZero.getDown().getDown();
				while(curNodeLoc.getNext() != null){
					if(curNodeLoc.getNext().getLocation() == busStop){
						curNode.getNext().setDown(curNodeLoc.getNext());
					}
					curNodeLoc = curNodeLoc.getNext();
				}
				//Link to train layer
				curNodeLoc = trainZero;
				while(curNodeLoc.getNext() != null){
					if(curNodeLoc.getNext().getLocation() == busStop){
						curNodeLoc.getNext().setDown(curNode.getNext());
					}
					curNodeLoc = curNodeLoc.getNext();
				}
				break;
			}
			curNode = curNode.getNext();
		}
	}

	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {
		ArrayList<TNode> path = new ArrayList<>();
		TNode head = trainZero;
	    while(head.getNext() != null){
			path.add(head);
			//If head.getNext() is at destination
			if(head.getNext().getLocation() == destination){
				//If there is something below
				if(head.getNext().getDown() != null){
					head = head.getNext();
					path.add(head);
					while(head.getDown() != null){
						head = head.getDown();
						path.add(head);
					}
					return path;
				}
				//Exactly at destination
				else{
					path.add(head.getNext());
					return path;
				}
			}
			//If head.getNext() is greater than destination
			else if(head.getNext().getLocation() > destination){
				head = head.getDown();
			}
			//If head.getNext() is less than destination
			else{
				if(head.getNext().getNext() == null){
					head = head.getNext();
					path.add(head);
					head = head.getDown();
				}
				else{
					head = head.getNext();
				}
			}
		}
	    return path;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {
		//Copy train layer
		TNode current = trainZero;    
        TNode newHeadTrain = null;    
        TNode tail = null;
		
        while (current != null)
        {
            if (newHeadTrain == null)
            {
                newHeadTrain = new TNode(current.getLocation());
                tail = newHeadTrain;
            }
            else {
                tail.setNext(new TNode());
                tail = tail.getNext();
                tail.setLocation(current.getLocation());
                tail.setNext(null);
            }
            current = current.getNext();
        }
		//Copy bus layer
		current = trainZero.getDown();    
        TNode newHeadBus = null;    
        tail = null;

 		while (current != null)
        {
            if (newHeadBus == null)
            {
                newHeadBus = new TNode(current.getLocation());
                tail = newHeadBus;
            }
            else {
                tail.setNext(new TNode());
                tail = tail.getNext();
                tail.setLocation(current.getLocation());
                tail.setNext(null);
            }
            current = current.getNext();
        }
		//Copy walking layer
		current = trainZero.getDown().getDown();    
        TNode newHeadWalk = null;    
        tail = null;
		
		while (current != null)
        {
            if (newHeadWalk == null)
            {
                newHeadWalk = new TNode(current.getLocation());
                tail = newHeadWalk;
            }
            else {
                tail.setNext(new TNode());
                tail = tail.getNext();
                tail.setLocation(current.getLocation());
                tail.setNext(null);
            }
            current = current.getNext();
        }
		//Connect train & bus
		TNode currentTrain = newHeadTrain; 
		TNode currentBus = newHeadBus;
		while (currentTrain != null){
			currentBus = newHeadBus;
			while(currentBus != null){
				if(currentTrain.getLocation() == currentBus.getLocation()){
					currentTrain.setDown(currentBus);
					break;
				}
				currentBus = currentBus.getNext();
			}
			currentTrain = currentTrain.getNext();
		}
		//Connect bus & walk
		currentBus = newHeadBus;
		TNode currentWalk = newHeadWalk; 
		while (currentBus != null){
			currentWalk = newHeadWalk;
			while(currentWalk != null){
				if(currentBus.getLocation() == currentWalk.getLocation()){
					currentBus.setDown(currentWalk);
					break;
				}
				currentWalk = currentWalk.getNext();
			}
			currentBus = currentBus.getNext();
		}
	    return newHeadTrain;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {
		TNode curBus = trainZero.getDown();
		TNode walkZero = trainZero.getDown().getDown();
		TNode curWalk = trainZero.getDown().getDown();
		TNode scooterZero = new TNode(0);
		TNode curScooter = scooterZero;
		//Create scooter row
		for(int i = 0; i < scooterStops.length; i++){
			TNode curNode = scooterZero;
			while(curNode.getNext() != null){
				curNode = curNode.getNext();
			}
			curNode.setNext(new TNode(scooterStops[i]));
		}
		//Link bus to scooter
		while(curBus != null){
			curScooter = scooterZero;
			while(curScooter != null){
				if(curBus.getLocation() == curScooter.getLocation()){
					curBus.setDown(curScooter);
					break;
				}
				curScooter = curScooter.getNext();
			}
			curBus = curBus.getNext();
		}
		//Link scooter to walk
		curScooter = scooterZero; 
		while(curScooter != null){
			curWalk = walkZero;
			while(curWalk != null){
				if(curWalk.getLocation() == curScooter.getLocation()){
					curScooter.setDown(curWalk);
				}
				curWalk = curWalk.getNext();
			}
			curScooter = curScooter.getNext();
		}
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
