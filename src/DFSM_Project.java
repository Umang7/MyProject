/**
 * Program Asks you for the file path containing the information about DFSM. After that users will be asked
 * to input a string to check if the string is in the language or not. User can do this indefinitely.
 * 
 * @author Umang Godhani
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DFSM_Project {
	
	private State[] states; // All the states of the machine
	private String[] alphabet; // Array containing the alphabets
	private int[] accepting_States; // List of Accepting states
	private int num_State; // Number of states
	
	

	/**
	 * Filling the array with the data recieved from the path file.
	 * 
	 * @param source
	 * @return: Array with properly filled positions.
	 */
	public String[] readList(String path) {
		String[] temp = new String[path.length()];
		int len = 0;
		for (int i = 0, first = 0; i < temp.length; i++) {
			int x = path.indexOf(',', first);
			if (x != -1) {
				temp[i] = (path.substring(first, x)).trim();
				first = ++x;
			} else {
				temp[i] = (path.substring(first)).trim();
				len = ++i;
				break;
			}
		}
		String[] finalized_List = new String[len];
		for (int i = 0; i < len; i++) {
			finalized_List[i] = temp[i];
		}
		return finalized_List;
	}

	/**
	 * DFSM is being made from the path file data.
	 * 
	 * @param source: path file with the information about the specifications of the DFSM
	 */
	public DFSM_Project(File path) {
		try {
			Scanner io = new Scanner(path);

			// Getting information from the path file and saving it into class variables.
			String initialAlphabet = io.nextLine();
			alphabet = readList(initialAlphabet);
			
			num_State = io.nextInt();
			this.states = new State[num_State];

			io.nextLine();
			String accepting_StatesTemp = io.nextLine();

			String[] temp = readList(accepting_StatesTemp);
			accepting_States = new int[temp.length];
			for (int i = 0; i < temp.length; i++) {
				accepting_States[i] = Integer.parseInt(temp[i]);
			}

			// Filtering the states
			for (int i = 0; i < num_State; i++) {
				boolean acceptings = false;
				for (int k = 0; k < accepting_States.length; k++) {
					if (accepting_States[k] == i) {
						acceptings = true;
					}
				}
				states[i] = new State(i, acceptings, alphabet);
			}

			// Logic of the Machine Function in the file.
			for (int i = 0; i < num_State; i++) {
				String function = io.nextLine();
				for (int j = 0; j < alphabet.length; j++) {

					int left_Paranthesis = function.indexOf("(");
					int comma = function.indexOf(",");
					int state_Num = Integer.parseInt(function.substring(left_Paranthesis + 1, comma));
					int comma_Next = function.indexOf(",", comma + 1);
					String alphabet_Input = (function.substring(comma + 1, comma_Next).trim());

					int right_Paranthesis = function.indexOf(")");
					int output_State = Integer
							.parseInt(function.substring(comma_Next + 1, right_Paranthesis).trim());

					states[state_Num].setNextState(states[output_State], alphabet_Input);

					if (j != alphabet.length - 1) {
						function = function.substring(right_Paranthesis + 3);
					} else {
						break;
					}
				}
			}

			io.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could Not Find the File.");
			e.printStackTrace();
		}
	}

	/**
	 * User's string inputs are examined with the given machine.
	 * 
	 * @param input: user's string input
	 * @return: true or false if the input is accepted or not by the given machine.
	 */
	public boolean isAccepted(String[] input) {
		State temp = states[0];
		for (int i = 0; i < input.length; i++) {
			temp = temp.nextState(input[i]);
		}
		return temp.isAccepting();
	}

	public String[] getAlphabet() {
		return alphabet;
	}

	/**
	 * checking if input is in the language or not.
	 * 
	 * @param input: user's input string
	 * @return: true or false if the input is in the language or not.
	 */
	public boolean isInLanguage(String input) {
		if (input.length() == 0) {
			return true;
		}
		String[] temp = new String[input.length()];
		for (int i = 0; i < input.length(); i++) {
			temp[i] = input.substring(i, i + 1);
		}
		for (int i = 0; i < temp.length; i++) {
			boolean test = false;
			for (int k = 0; k < alphabet.length; k++) {
				if (alphabet[k].equals(temp[i])) {
					test = true;
				}
			}
			if (!test) {
				return false;
			}
		}
		return true;
	}
	/**
	 * This is where we actually call and execute all the methods and constructors. 
	 * @param arg
	 */
	public static void main(String[] arg) {
		Scanner in = new Scanner(System.in);
		System.out.print("Insert (paste) the path of the DFSM file :");
		String file_Path = in.nextLine();
		File source = new File(file_Path);
		DFSM_Project DFSM = new DFSM_Project(source);

		// Loop to ask user if they want to continue or not.
		while (true) {
			String input;
			String[] temp;
			do {
				System.out.print("\nEnter the String to check in the DFSM : ");
				input = in.nextLine();

				temp = new String[input.length()];
				if (!DFSM.isInLanguage(input)) {
					System.out.println("Error: make sure to use the alphabet that are in the langugage.");
				}
			} while (!DFSM.isInLanguage(input));

			for (int i = 0; i < input.length(); i++) {
				temp[i] = input.substring(i, i + 1);
			}
			if (DFSM.isAccepted(temp)) {
				System.out.println("\nYour String is Accepted by the given DFSM.");
			} else {
				System.out.println("\nYour String is Not Accepted by the given DFSM.");
			}

			System.out.print("Wanna check more strings? y/n: ");
			String ans = in.nextLine();

			if (!ans.equals("y")) {
				in.close();
				break;
			}
		}
	}

}

/**
 * By using given information this class with create logical steps of the machine. 
 * Basically picturing the DFSM.
 * 
 * @author Umang Godhani
 *
 */
class State {
	
	private int state_Number;
	private boolean accepting;
	private State[] nextStates; // An array of all the states that this state points to
	private String[][] ptr; // A 2D array containing each state that this state points to, and the
								// input containing the state in the second row.
	private int states_Count;

	/**
	 * State constructor
	 * 
	 * @param state_Number: Number of states in the DFSM
	 * @param accepting: Checks if the state is an accepting state
	 * @param alphabet: The alphabet that the state accepts as input/outputs
	 */
	public State(int state_Number, boolean accepting, String[] alphabet) 
	{
		this.state_Number = state_Number;
		this.accepting = accepting;
		this.nextStates = new State[alphabet.length];
		states_Count = 0;
		this.ptr = new String[2][alphabet.length];
		this.ptr[0] = alphabet;
		for (int i = 0; i < alphabet.length; i++) {
			this.ptr[1][i] = "-1"; // The second row of this ptr, indicates the state which each alphabet
											// above it points to.
		}
	}

	/**
	 * 
	 * @return number of the state
	 */
	public int getstate_Number() {
		return state_Number;
	}

	/**
	 * 
	 * @return true if current state is an accepting state
	 */
	public boolean isAccepting() {
		return accepting;
	}

	/**
	 * 
	 * @param next:  setting the state as next state
	 * @param input: The input corresponding to the next state
	 */
	public void setNextState(State next, String input) {
		String state_Number = Integer.toString(next.getstate_Number());
		for (int i = 0; i < ptr[0].length; i++) {
			if (ptr[0][i].equals(input)) {
				ptr[1][i] = state_Number;
			}
		}
		nextStates[states_Count] = next;
		states_Count++;
	}

	/**
	 * 
	 * @param input: to read into states
	 * @return: the state that current state points to
	 */
	public State nextState(String input) {
		for (int i = 0; i < ptr[0].length; i++) {
			if (ptr[0][i].equals(input)) {
				int state = Integer.parseInt(ptr[1][i]);
				for (int j = 0; j < states_Count; j++) {
					if (nextStates[j].getstate_Number() == state) {
						return nextStates[j];
					}
				}
			}
		}
		return null;
	}
}

	
	

