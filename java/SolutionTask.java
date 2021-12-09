/*
 * Authors: 
 *      Angel Cardenas		651018873		acarde36
 *      Kartik Maheshwari	665023848		kmahes5
 *      SolutionTask solves the given puzzle in the background using the heuristic specified.
 *      Used in JavaFXTemplate to prevent the application thread from being blocked.
 */
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.concurrent.Task;

public class SolutionTask extends Task<Void> {
	Consumer<ArrayList<Node>> callback;  // allows task to update GUI
	String heuristic;					 // stores the heuristic chosen
	int[] currentPuzzle;				 // stores the start of the puzzle
	
	// creates a task with all necessary parameters defined
	public SolutionTask(Consumer<ArrayList<Node>> c, String h, int[] p) {
		callback = c;
		heuristic = h;
		currentPuzzle = p;
	}

	// void call() returns nothing but uses callback.accept() to update GUI with solution
	@Override
	protected Void call() throws Exception {
		// stores the solution path
		ArrayList<Node> solutionPath = new ArrayList<Node>();
		
		// creates a starting Node from the puzzle
		Node startState = new Node(currentPuzzle);
		startState.setDepth(0);
		
		// solves and returns solution using callback.accept()
		solutionPath = A_IDS_A_15solver.A_Star(startState, heuristic);
		callback.accept(solutionPath);
		
		return null;
	}

}
