package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Anthony Mazzie
 * 
 *         Using a binary tree based algorithm, this class decodes a a message
 *         from a .arch file.
 */

public class MsgTree {

	private static int staticCharIdx = 0;
	private static String encString;
	private static String tempStr;
	private static String remainderStr;
	private static String inputFileName;
	public char payloadChar;
	public MsgTree left;
	public MsgTree right;

	/**
	 * Creates the codes tree and then uses it to decode the message it is given
	 * from an arch file.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		getFileName();

		buildCodeStrings();

		MsgTree mainTree = new MsgTree(encString);

		System.out.println("Character\tCode: \n------------------------");

		printCodes(mainTree, "");

		System.out.println("------------------------\nMessage:");

		decode(mainTree, remainderStr);
	}

	/**
	 * Constructs a tree from the incomingEncodedStr.
	 *
	 * @param incomingEncodedStr string
	 */
	public MsgTree(String incomingEncodedStr) {

		this.payloadChar = incomingEncodedStr.charAt(staticCharIdx);

		staticCharIdx = staticCharIdx + 1;

		this.left = new MsgTree(incomingEncodedStr.charAt(staticCharIdx));

		// If left char equals ^, create new tree
		if (this.left.payloadChar == '^') {
			this.left = new MsgTree(incomingEncodedStr);
		}

		staticCharIdx = staticCharIdx + 1;

		this.right = new MsgTree(incomingEncodedStr.charAt(staticCharIdx));

		// If right char equals ^, create new right tree
		if (this.right.payloadChar == '^') {
			this.right = new MsgTree(incomingEncodedStr);
		}
	}

	/**
	 * Constructor for a single node with null children
	 * 
	 * @param payloadChar char being set as payloadChar.
	 */
	public MsgTree(char payloadChar) {

		// Only need to set payload char
		this.payloadChar = payloadChar;

	}

	/**
	 * Prints tree of all characters and their binary codes
	 *
	 * @param root    = root tree
	 * @param codeMsg
	 */
	public static void printCodes(MsgTree root, String codeMsg) {

		if (root == null) {
			return;
		}

		// '^' indicates internal node
		if (root.payloadChar != '^') {
			System.out.print(root.payloadChar + "\t\t");
			System.out.println(codeMsg);
		}

		printCodes(root.left, codeMsg + "0");
		printCodes(root.right, codeMsg + "1");
	}

	/**
	 * Decodes a string message using tree
	 * 
	 * @param codes a binary search tree given character codes.
	 * @param msg   = to be decoded
	 */
	public static void decode(MsgTree codes, String msg) {

		MsgTree thisTree = codes;
		char curChar;
		int curCharIndex = 0;
		int msgLength = msg.length();
		int msgLastIndex = msg.length() - 1;
		String decodedMsg = "";

		while (curCharIndex < msgLength) {
			curChar = msg.charAt(curCharIndex);
			if (curChar == '0') {
				if (thisTree.left == null) {
					decodedMsg += thisTree.payloadChar;
					thisTree = codes;
				} else {
					thisTree = thisTree.left;
					curCharIndex = curCharIndex + 1;
				}
			} else if (curChar == '1') {
				if (thisTree.right == null) {
					decodedMsg += thisTree.payloadChar;
					thisTree = codes;
				} else {
					thisTree = thisTree.right;
					curCharIndex = curCharIndex + 1;
				}
			}

			// At end of msg
			if (curCharIndex == msgLastIndex) {
				if (curChar == '0') {
					decodedMsg += thisTree.left.payloadChar;
					curCharIndex = curCharIndex + 1;
				}
				if (curChar == '1') {
					decodedMsg += thisTree.right.payloadChar;
					curCharIndex = curCharIndex + 1;
				}
			}
		}
		System.out.println(decodedMsg);
	}

	/**
	 * Ask the user for a file name, and store the contents of filename to String
	 * inputFileName
	 * 
	 * @throws FileNotFoundException
	 */
	private static void getFileName() throws FileNotFoundException {

		Scanner inputScanner = new Scanner(System.in);

		System.out.print("Please enter filename to decode: ");
		inputFileName = inputScanner.nextLine().trim();

		inputScanner.close();
	}

	/**
	 * Builds strings from input file.
	 * 
	 * @throws FileNotFoundException
	 */
	private static void buildCodeStrings() throws FileNotFoundException {

		File inputFile = new File(inputFileName);

		Scanner fileScanner;
		try {
			fileScanner = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(" File '" + inputFile + "' does not exist. ");
		}

		encString = fileScanner.nextLine();
		tempStr = fileScanner.nextLine();
		remainderStr = "";

		/**
		 * The encoding scheme representations being tested may include a space
		 * character and/or a newline character.
		 * 
		 * This will break tree string into two lines.
		 */
		for (int x = 0; x < tempStr.length(); x++) {
			if (tempStr.charAt(x) != '1' && tempStr.charAt(x) != '0') {
				encString += "\n";
				encString += tempStr;
				remainderStr = fileScanner.nextLine();
				x = tempStr.length();
			} else {
				remainderStr = tempStr;
			}
		}
		fileScanner.close();
	}
}