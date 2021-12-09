
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class JavaFXTemplate extends Application {
	Stage ourstage;											// stage for easy access.
	PauseTransition pause;									// pause transition for welcome scene to game scene
	GridPane puzzleBoard;									// main board to play 15 puzzle
	Button newPuzzleB, showSolutionB, solveH1B, solveH2B;	// easy access for buttons on the game screen
	int puzzleCounter;										// counter keeping track of the current puzzle
	ArrayList<GameButton> puzzleList;						// list that is being casted on the grid pane
	/*11 solvable puzzles that get picked randomly in a sequential order*/
	ArrayList<Integer> puzzle1 = new ArrayList<>(Arrays.asList(4, 1, 2, 3, 5, 9, 6, 7, 8, 0, 10, 11, 12, 13, 14, 15));
	ArrayList<Integer> puzzle2 = new ArrayList<>(Arrays.asList(4, 0, 1, 2, 5, 9, 7, 3, 12, 8, 6, 10, 13, 14, 15, 11));
	ArrayList<Integer> puzzle3 = new ArrayList<>(Arrays.asList(5, 14, 4, 1, 7, 15, 12, 2, 13, 0, 6, 10, 8, 9, 3, 11));
	ArrayList<Integer> puzzle4 = new ArrayList<>(Arrays.asList(13, 7, 12, 14, 5, 8, 9, 1, 4, 6, 3, 11, 0, 15, 2, 10));
	ArrayList<Integer> puzzle5 = new ArrayList<>(Arrays.asList(6, 0, 2, 9, 5, 7, 15, 1, 8, 13, 12, 3, 4, 11, 14, 10));
	ArrayList<Integer> puzzle6 = new ArrayList<>(Arrays.asList(0, 7, 13, 2, 4, 1, 15, 10, 6, 5, 3, 14, 8, 11, 9, 12));
	ArrayList<Integer> puzzle7 = new ArrayList<>(Arrays.asList(15, 1, 6, 0, 9, 7, 11, 13, 4, 14, 5, 3, 8, 2, 12, 10));
	ArrayList<Integer> puzzle8 = new ArrayList<>(Arrays.asList(15, 14, 8, 6, 11, 3, 5, 7, 9, 2, 13, 10, 0, 4, 12, 1));
	ArrayList<Integer> puzzle9 = new ArrayList<>(Arrays.asList(15, 8, 1, 6, 11, 9, 2, 5, 3, 12, 10, 7, 14, 4, 13, 0));
	ArrayList<Integer> puzzle10 = new ArrayList<>(Arrays.asList(9, 14, 12, 15, 11, 5, 6, 2, 8, 0, 10, 1, 4, 13, 3, 7));
	ArrayList<Integer> puzzle11 = new ArrayList<>(Arrays.asList(9, 2, 14, 15, 3, 12, 0, 8, 7, 13, 4, 5, 6, 10, 1, 11));
	ArrayList<Node> solution;								// list of nodes that gets returned from the AI algorithm
	ExecutorService threads;								// executor service thread to run tasks
	
	//-------------------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ourstage = primaryStage;
		ourstage.setTitle("Welcome to 15 Puzzle!");
		
		// making a thread pool of 11 one for each possible puzzle.
		threads = Executors.newFixedThreadPool(11);
		// ensures threads are shutdown before closing the window
		ourstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	threads.shutdown();
                Platform.exit();
                System.exit(0);
            }
        });
		
		// randomly picking a number to pick the puzzle to display first on GUI
		Random rand = new Random();
		puzzleCounter = rand.nextInt(11) + 1;

		// initializes empty solution array for later holding the solution path
		solution = new ArrayList<Node>();
		
		// change scene to the welcome scene
		ourstage.setScene(welcomeScene());
		ourstage.show();
		
		// 3 second pause to automatically transition to the game scene from welcome
		pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e-> {
			ourstage.setScene(gameScene());
			ourstage.show();
		});
		pause.play();
	}
	
	/*returns the welcome screen with predetermined settings*/
	private Scene welcomeScene() {
		// Text with heading
		Text message = new Text("Welcome to 15-Puzzle!");
		message.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
		// Text with creators names
		Text names = new Text("Hamza Sheikh");
		names.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		
		// Adding text to VBox for alignment
		VBox align = new VBox(message, names);
		align.setAlignment(Pos.CENTER);
		HBox align2 = new HBox(align);
		align2.setAlignment(Pos.CENTER);
		
		// set background as welcome2.gif
		BorderPane welcome = new BorderPane(align2);
		Image image1 = new Image("welcome2.gif", 550, 500, false, true);
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		welcome.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		// returns Scene created from welcomePane at specified size
		return new Scene(welcome, 550, 500);
	}
	
	/*Helper function that returns a sequentially randomly picked 
	 * array list to load into the grid pane*/
	public ArrayList<Integer> pickPuzzle() {
		if(puzzleCounter >= 12) {
			puzzleCounter = 1;
		}
		if(puzzleCounter == 1) {
			puzzleCounter++;
			return puzzle1;
		} else if(puzzleCounter == 2) {
			puzzleCounter++;
			return puzzle2;
		} else if(puzzleCounter == 3) {
			puzzleCounter++;
			return puzzle3;
		} else if(puzzleCounter == 4) {
			puzzleCounter++;
			return puzzle4;
		} else if(puzzleCounter == 5) {
			puzzleCounter++;
			return puzzle5;
		} else if(puzzleCounter == 6) {
			puzzleCounter++;
			return puzzle6;
		} else if(puzzleCounter == 7) {
			puzzleCounter++;
			return puzzle7;
		} else if(puzzleCounter == 8) {
			puzzleCounter++;
			return puzzle8;
		} else if(puzzleCounter == 9) {
			puzzleCounter++;
			return puzzle9;
		} else if(puzzleCounter == 10) {
			puzzleCounter++;
			return puzzle10;
		} else if(puzzleCounter == 11) {
			puzzleCounter++;
			return puzzle11;
		}
		return null;
	}
	
	/*Helper function that converts an array list of game button to an array*/
	public int[] ArrayListToArray(ArrayList<GameButton> p) {
		int[] temp = new int[p.size()];		// creating an array to return
		
		// Traverse the array list of game buttons and populate the array
		for (int i = 0; i < p.size(); i++) {
			temp[i] = p.get(i).num;
		}
		return temp;
	}
	
	/*Returns a game scene with the grid pane*/
	private Scene gameScene() {
		ourstage.setTitle("15 Puzzle");
		// initializing the grid pane with specific visual settings
		puzzleBoard = new GridPane();
		puzzleBoard.setAlignment(Pos.TOP_RIGHT);
		puzzleBoard.setPadding(new Insets(10.0));
		puzzleBoard.setVgap(10.0);
		puzzleBoard.setHgap(10.0);
		
		// initializing array of game button that will populate the grid pane
		puzzleList = new ArrayList<GameButton>();
		ArrayList<Integer> currPuzzle = pickPuzzle();	// picking a random puzzle
		
		// populating the array list of game buttons with game buttons
		for (int i = 0; i < 16; i++) {
			puzzleList.add(new GameButton(currPuzzle.get(i), i));
		}
		
		// populating the grid pane with the array list of game buttons
		for (int i = 0; i < 16; i++) {
			if (i < 4) {		 // for the last row
				puzzleBoard.add(puzzleList.get(i), i, 0);
			} else if (i < 8) {  // for third row
				puzzleBoard.add(puzzleList.get(i), i-4, 1);
			} else if (i < 12) {  // for second row
				puzzleBoard.add(puzzleList.get(i), i-8, 2);
			} else if (i < 16) {  // for top row
				puzzleBoard.add(puzzleList.get(i), i-12, 3);
			}
		}
		
		// initializing the border pane with specific settings
		BorderPane gameScreen = new BorderPane();
		HBox gameScreenH = new HBox(puzzleBoard);
		gameScreenH.setAlignment(Pos.CENTER);
		VBox gameScreenV = new VBox(gameScreenH);
		gameScreenV.setAlignment(Pos.CENTER);
		
		//------initializing all the buttons and their functionality----- 
		newPuzzleB = new Button("New Puzzle");
		newPuzzleB.setOnAction(newPuzzle);
		newPuzzleB.setPadding(new Insets(10,10,10,10));
		newPuzzleB.setMinWidth(110);
		HBox newPuzzleH = new HBox(newPuzzleB);
		newPuzzleH.setPadding(new Insets(15,15,15,15));
		newPuzzleH.setAlignment(Pos.CENTER);
		
		solveH1B = new Button("Solve with H1");
		solveH1B.setOnAction(H1);
		solveH1B.setPadding(new Insets(10,10,10,10));
		solveH1B.setMinWidth(110);
		HBox solveH1H = new HBox(solveH1B);
		solveH1H.setPadding(new Insets(15,15,15,15));
		solveH1H.setAlignment(Pos.CENTER);
		
		solveH2B = new Button("Solve with H2");
		solveH2B.setOnAction(H2);
		solveH2B.setPadding(new Insets(10,10,10,10));
		solveH2B.setMinWidth(110);
		HBox solveH2H = new HBox(solveH2B);
		solveH2H.setPadding(new Insets(15,15,15,15));
		solveH2H.setAlignment(Pos.CENTER);
		
		showSolutionB = new Button("Show Solution");
		showSolutionB.setOnAction(solve);
		showSolutionB.setDisable(true);
		showSolutionB.setPadding(new Insets(10,10,10,10));
		showSolutionB.setMinWidth(110);
		HBox showSolutionH = new HBox(showSolutionB);
		showSolutionH.setPadding(new Insets(15,15,15,15));
		showSolutionH.setAlignment(Pos.CENTER);
		
		Button exit = new Button("Exit Game");
		exit.setOnAction(quit);
		exit.setPadding(new Insets(10,10,10,10));
		exit.setMinWidth(110);
		HBox exitH = new HBox(exit);
		exitH.setPadding(new Insets(15,15,15,15));
		exitH.setAlignment(Pos.CENTER);
		
		Button HTPB = new Button("How to Play?");
		HTPB.setOnAction(displayInst);
		HTPB.setPadding(new Insets(10,10,10,10));
		HTPB.setMinWidth(110);
		HBox HTPH = new HBox(HTPB);
		HTPH.setPadding(new Insets(15,15,15,15));
		HTPH.setAlignment(Pos.CENTER);
		//------------------------------------------------------------ 
		
		// VBox and HBox for aligning the buttons
		VBox node = new VBox(newPuzzleH, solveH1H, solveH2H, showSolutionH, exitH, HTPH);
		node.setAlignment(Pos.CENTER);
		
		HBox everything = new HBox(node, gameScreenV);
		everything.setAlignment(Pos.CENTER);
		gameScreen.setCenter(everything);
		
		// set background as main.gif
		Image image1 = new Image("main.gif", 550, 500, false, true);
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		gameScreen.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(gameScreen, 550, 500);
	}
	
	/*Event handler for how to play button which displays a dialog box with directions*/
	EventHandler<ActionEvent> displayInst = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent e) {
			// creates a new Dialog box for displaying instructions
			Dialog<String> directions = new Dialog<String>();
			directions.setTitle("How to play 15 Puzzle");
			directions.setResizable(true);
			directions.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			directions.getDialogPane().setMinWidth(500.0);
			
			// instructions for playing the game
			directions.setContentText("Numbered tiles can be swapped with the blank spot one at a time. \r\n"
					+ "A solved puzzle should have all of the tiles in order from lowest number to highest number with the blank spot in the upper left.\r\n\n"
					+ "You can click either of the Solve Buttons to solve 10 moves in the background.\r\n"
					+ "After it is solved, you can see those 10 moves by clicking the Show Solution Button.\r\n\n"
					+ "You are not allowed make a move while the solution is being created or while the solution is being shown. \r\n\n"
					+ "H1 uses the number of tiles that are out of place to solve the board.\r\n"
					+ "H2 uses the manhattan distance to solve the board.\r\n\n"
					+ "If you have trouble you can get a new puzzle by clicking New Puzzle or exit by clicking Exit.\r\n");
			
			// Button to close the dialog box
			ButtonType ok = new ButtonType("OK", ButtonData.OK_DONE);
			directions.getDialogPane().getButtonTypes().add(ok);
			
			// display dialog box
			directions.showAndWait();
		}
	};
	
	/*Event handler for new puzzle button which calls the game scene which is set to default*/
	EventHandler<ActionEvent> newPuzzle = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent e) {
			ourstage.setScene(gameScene());
		}
	};
	
	//--------------ADD COMMENTS ----------------------- (delete later)
	/*Event handler for Heuristic one*/
	EventHandler<ActionEvent> H1 = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent e) {
			// Disable buttons to avoid messing with the puzzle as it is being solved
			newPuzzleB.setDisable(true);
			setDisableGridPane(true);
			solveH1B.setDisable(true);
			solveH2B.setDisable(true);
			
			// a new task is created to solve the board and update the game with runLater()
			SolutionTask task = new SolutionTask(data->{
				Platform.runLater(()->{
					solveH1B.setText("Solved with H1");
					solution = data;
					showSolutionB.setDisable(false);
					newPuzzleB.setDisable(false);
				});
			}, "heuristicOne", ArrayListToArray(puzzleList));
			threads.submit(task);
			
			// Let user know which heuristic is being solved
			solveH1B.setText("Solving with H1");
			solveH2B.setText("̵S̵o̵l̵v̵e̵ ̵w̵i̵t̵h̵ ̵H̵2");
		}
	};
	
	//--------------ADD COMMENTS ----------------------- (delete later)
	EventHandler<ActionEvent> H2 = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent e) {
			// Disable buttons to avoid messing with the puzzle as it is being solved
			newPuzzleB.setDisable(true);
			setDisableGridPane(true);
			solveH1B.setDisable(true);
			solveH2B.setDisable(true);
			
			// a new task is created to solve the board and update the game with runLater()
			SolutionTask task = new SolutionTask(data->{
				Platform.runLater(()->{
				solveH2B.setText("Solved with H2");
				solution = data;
				showSolutionB.setDisable(false);
				});
			}, "heuristicTwo", ArrayListToArray(puzzleList));
			threads.submit(task);
			
			// Let user know which heuristic is being solved
			solveH1B.setText("̵S̵o̵l̵v̵e̵ ̵w̵i̵t̵h̵ ̵H̵1");
			solveH2B.setText("Solving with H2");
		}
	};
	
	/*Helper function that accepts a node and copies the array stored in the node to puzzleList*/
	private void displayState(Node n) {
		int[] puzzleArray = n.getKey();		// storing the array from the node
		
		// Traversing the array from the node and copying to the puzzle list
		for(int i = 0; i< puzzleArray.length; i++){
			puzzleList.get(i).updateNum(puzzleArray[i]);
		}
	}
	
	/*Helper function that displays ten solution steps one at a time (animation)*/
	public void displaySolution() {
		// creating a new pause transition to have between each AI move
		PauseTransition pause2 = new PauseTransition(Duration.seconds(1));
		pause2.play();
		
		// Creating atomic integer because it can be changed inside setOnFinished
		AtomicInteger count = new AtomicInteger(1);
		
		pause2.setOnFinished(e-> {
			if (checkWin()) {					// when win is achieved the function calls win screen
				;
			} else if (count.get() <= 10) {		// when win is not achieved and 10 moves are not made yet
				displayState(solution.get(count.get()));
				count.set(count.get() + 1);;
				pause2.play();
			} else {							// when ten moves are made and puzzle isn't solved yet
				solveH1B.setDisable(false);
				solveH2B.setDisable(false);
				
				solveH1B.setText("Solve with H1");
				solveH2B.setText("Solve with H2");
				
				newPuzzleB.setDisable(false);
				setDisableGridPane(false);
			}
		});
		
	}
	
	/*Event handler for solve button which calls the helper function to make AI moves*/
	EventHandler<ActionEvent> solve = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent e) {
			newPuzzleB.setDisable(true);
			showSolutionB.setDisable(true);
			displaySolution();
		}
	};
	
	/*Event handler for quit button which closes the stage*/
	EventHandler<ActionEvent> quit = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent e) {
			threads.shutdown();
            Platform.exit();
            System.exit(0);
			ourstage.close();
		}
	};
	
	/*Helper function that checks if the win is achieved based of array list of game buttons*/
	public boolean checkWin() {
		// check is the top left button is 0 to minimize time complexity
		if (puzzleList.get(0).num != 0) {
			return false;
		}
		// check the rest of the game button list if they are in ascending order
		for (int i = 2; i<16; i++) {
			if (puzzleList.get(i-1).num > puzzleList.get(i).num) {
				return false;
			}
		}
		// when all checks are passed win is achieved and we switch scenes.
		ourstage.setScene(winScene());
		ourstage.show();
		return true;
	}
	
	/*Helper function that disable the use to click on the grid 
	 * pane while AI solution is in progress*/
	public void setDisableGridPane(boolean choice) {
		puzzleBoard.setMouseTransparent(choice);
	}
	
	/*returns post-game scene with a message with two options to quit or play again*/
	public Scene winScene() {
		ourstage.setTitle("15 Puzzle: Game Over");
		// Adding the Text Message
		Text message = new Text("Congratulations!");
		message.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
		HBox messageH = new HBox(message);
		messageH.setAlignment(Pos.CENTER);
		
		// Adding buttons to the pane
		Button replay = new Button("Play Again!");
		replay.setOnAction(newPuzzle);
		VBox replayV = new VBox(replay);
		replayV.setPadding(new Insets(15,15,15,15));
		
		Button quitB = new Button("Quit Game.");
		quitB.setOnAction(quit);
		VBox quitV = new VBox(quitB);
		quitV.setPadding(new Insets(15,15,15,15));
		
		// Adding buttons to HBox and VBox for alignment
		HBox options = new HBox(replayV, quitV);
		options.setAlignment(Pos.CENTER);
		
		VBox align = new VBox(messageH, options);
		align.setAlignment(Pos.CENTER);
		
		// Creating border pane to return
		BorderPane endScreen = new BorderPane(align);
		
		// Sets background as honeycomb.gif
		Image image1 = new Image("honeycomb.gif", 550, 500, true, true);
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		endScreen.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(endScreen, 500, 500);
	}
	
	//--------------------------------------------------------------------------
	/*Personalized Button that stores the value on the button and their color*/
	public class GameButton extends Button {
		public Integer num;  // number on the button
		
		/*Constructor that sets the number and the position*/
		public GameButton(Integer n, int pos) {
			super();
			this.num = n;
			
			// setting the text and based of the position and number a color is assigned 
			if (n == 0) {
				this.setText("");
				this.setStyle("-fx-background-color: #fbfb6a;");
			} else {
				this.setText(n.toString());
				if (pos == n) {
					this.setStyle("-fx-background-color: #9cfc9c;");
				} else {
					this.setStyle("-fx-background-color: #9cccfc;");
				}
			}
			// setting size of each buttons
			this.setPrefSize(95, 95);
			this.setOnAction(e-> makeMove());  // calling helper function when clicked on
		}
		
		/*Helper function that moves the piece on the grid pane*/
		private void makeMove() {
			int bPos = puzzleList.indexOf(this);		// getting the position of the button pressed
			
			if ((bPos)%4 != 0 && puzzleList.get(bPos - 1).num == 0) {				// moving left
				swapButton(this, puzzleList.get(bPos - 1));
			} else if ((bPos+1)%4 != 0 && puzzleList.get(bPos + 1).num == 0) {		// moving right
				swapButton(this, puzzleList.get(bPos + 1));
			} else if (bPos - 4 >= 0 && puzzleList.get(bPos - 4).num == 0) {		// moving up
				swapButton(this, puzzleList.get(bPos - 4));
			} else if (bPos + 4 <= 15 && puzzleList.get(bPos + 4).num == 0) {		// moving down
				swapButton(this, puzzleList.get(bPos + 4));
			}
			
		}
		
		/*Helper function that swaps zero with other game button*/
		private void swapButton(GameButton b1, GameButton b2) {
			if (b2 == null) {
				return;
			}
			
			int tempNum = b1.num;
			
			b1.updateNum(b2.num);
			b2.updateNum(tempNum);
			
			checkWin();		// checking for win after swapping
		}
		
		/*Helper function for swapButton which switches the number on the array and then sets color*/
		public void updateNum(Integer n) {
			this.num = n;
			// if this is the zero button
			if (n == 0) {
				//colors the zero button golden and makes it show blank
				this.setText("");
				this.setStyle("-fx-background-color: #fbfb6a;");
			} else {
				this.setText(n.toString());
				// checks if button position matches it's placement
				if (puzzleList.indexOf(this) == n) {
					// if button position matches it's number color it green
					this.setStyle("-fx-background-color: #9cfc9c;");	// green
				} else {
					// if button position does not match its placement, color it blue
					this.setStyle("-fx-background-color: #9cccfc;");	// blue
				}
			}
		}
	}  // end of button class
	//--------------------------------------------------------------------------
}
