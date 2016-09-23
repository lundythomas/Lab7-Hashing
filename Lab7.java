import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Lab7
{
	// This is the size of the hash table - hashNum1 prime number is best. Chose
	// 433099 as it is the prime just smaller than (dictionary.length * 2). This
	// gives us the maximum size with the acceptable load factor of 0.5
	private static int size = 433099;
	// Create the hash table
	private static String[] hashTable = new String[size];
	// Make sure your String dictionary is big enough to hold all the data
	private static String[] dictionary = new String[216555];

	// Three changeable numbers to try and lower collisions
	private static int hashNum1 = 257;
	private static int hashNum2 = 65537;
	private static int hashNum3 = 10007;

	public static void main(String[] args)
	{

		File fileLocation = new File("dictionary.txt"); // TODO
		getContents(fileLocation); // Loads up the file
		// Prints user menu
		System.out.println(
				"Which type of open addressing would you like to use?");
		System.out.println("1) Linear Probing");
		System.out.println("2) Quadratic Probing");
		System.out.println("3) Double Hashing");
		Scanner in = new Scanner(System.in); // Create Scanner
		// The user enters a number for the hashing strategy they want to use
		int strategy = in.nextInt();

		// Implement appropriate hashing algorithm
		switch (strategy)
		{
			case 1:
				fillLinearProbing();
				break;
			case 2:
				fillQuadraticProbing();
				break;
			case 3:
				fillDoubleHash();
				break;
		}

		double loadFactor = (double) dictionary.length / size;
		System.out.println("Load factor: " + loadFactor);

		in.nextLine(); // Reads extra newline character from buffer
		// Use 'q0' as an exit condition as it is not in the dictionary
		System.out.println("\nType 'q0' to exit the program.");
		System.out.print("Enter a word to find: ");
		String word = in.nextLine(); // Reads user input

		// Use 'q0' as an exit condition as it is not in the dictionary
		while (!word.equals("q0")) {
			find(word, strategy); // Find the user specified word
			// The user is asked to enter words to search for until they enter
			// the word 'q0'
			System.out.println("\nType 'q0' to exit the program.");
			System.out.print("Enter a word to find: ");
			word = in.nextLine(); // Reads user input
		}

		in.close(); // Close Scanner
	}

	/*
	 * This method takes in a word to look for and the strategy by which it has
	 * been placed in the hash table
	 */
	private static void find(String word, int strategy)
	{
		int probes = 1; // How many lookup attempts
		int index = getHashKey(word); // Calculate the hash key for the word
		System.out.println();

		// As long as you do not stumble across either the word or a
		// blank keep searching
		while (hashTable[index] != null && !hashTable[index].equals(word))
		{
			System.out.println("Checking slot: " + index
					+ " ... collision with: " + hashTable[index]);

			// Depending on the strategy go up in linear jumps, quadratic jumps
			// or the double hash jump
			if (strategy == 1)
			{
				index++;
			}

			else if (strategy == 2)
			{
				index += (probes * probes);
			}

			else if (strategy == 3)
			{
				index += getDoubleHashKey(word);
			}

			probes++; // Increment number of lookup attempts
			// Always mod the index size so it doesn't go out of bounds
			index %= size;
		}

		// If you've found a space then the word cannot be in the hash table
		if (hashTable[index] == null)
		{
			System.out.println("NOT IN HASHTABLE");
		}

		else
		{
			System.out.println("The word '" + word + "' was found in slot: "
					+ index + " of the hashtable");
		}

		// Print out the total number of attempts to find the correct slot
		System.out.println("Number of hash table probes: " + probes);
	}

	/*
	 * This is the primary hash key function - it should return a number which
	 * is a slot in the hash table for words, a good strategy is to raise each
	 * character to successive powers of the alphabet size assume that the
	 * alphabet is ASCII characters - a total of 256 possibilities each
	 * successive character value should be raised to successive powers of 256
	 * the whole thing is added together and then moduloed to create a hash
	 * table index
	 */
								private static int getHashKey(String word)
								{
									int total = 0;

									for (int i=0;i<word.length();i++)
									{
										int temp = modPow(hashNum1, i, size); // (hashNum1 to the power of i) mod (size).

										total += modMult((int) word.charAt(i), temp, size); // (Character * temp) % size
									}


									return total % size;	// mod again to keep it within bounds.
								}

	/*
	 * This method should be different to the primary hash function it should
	 * return a different number for words which generated the same primary hash
	 * key value for example, you could just add up all of the letters in the
	 * word
	 */
								private static int getDoubleHashKey(String word)
								{
									int total = 0;

									for (int i = 0; i < word.length(); i++)
									{
										total += (int) word.charAt(i); // Add each current letter value to total.
									}

									total += hashNum2; // Add jump value you unique number.
									total %= hashNum3; // Add more randomness to jump.

									return hashNum3 - total;
								}

	private static void fillLinearProbing()
	{
		// This variable stores the total number of collisions that have
		// occurred for every word
		int totalCollisions = 0;

		// Go through all words
		for (int i = 0; i < dictionary.length; i++)
		{
			int collisions = 0; // Collisions for this word
			int index = getHashKey(dictionary[i]); // Generate a hash key

			// If that slot is already filled move onto the next slot and
			// increment the collisions
			while (hashTable[index] != null)
			{
				collisions++;
				index++;
				// make sure you don't go off the edge of the hash table
				index %= size;
			}

			hashTable[index] = dictionary[i];

			// Print every 10000th word
			if (i % 10000 == 0) {
				System.out.println(dictionary[i] + " was placed in slot "
						+ index + " of the hash table after " + collisions
						+ " collisions");
			}

			// Add this words collision count to the total amount of collisions
			totalCollisions += collisions;
		}

		System.out.println(
				"\nThe total number of collisions was " + totalCollisions);
	}

	// Mostly same code as Linear Probing
	private static void fillQuadraticProbing()
	{
		int totalCollisions = 0;

		for (int i = 0; i < dictionary.length; i++)
		{
			int collisions = 0;
			int index = getHashKey(dictionary[i]);
			int queries = 1;

			while (hashTable[index] != null)
			{
				collisions++;
				index += (queries * queries);
				index %= size;
				queries++;
			}

			hashTable[index] = dictionary[i];

			if (i % 10000 == 0)
			{
				System.out.println(dictionary[i] + " was placed in slot "
						+ index + " of the hash table after " + collisions
						+ " collisions");
			}

			totalCollisions += collisions;
		}

		System.out.println(
				"\nThe total number of collisions was " + totalCollisions);
	}

	// Mostly same code as Linear Probing
	private static void fillDoubleHash()
	{
		int totalCollisions = 0;

		for (int i = 0; i < dictionary.length; i++)
		{
			int collisions = 0;
			int index = getHashKey(dictionary[i]);
			int doubleHash = getDoubleHashKey(dictionary[i]);

			while (hashTable[index] != null)
			{
				collisions++;
				index += doubleHash;
				index %= size;
			}

			hashTable[index] = dictionary[i];

			if (i % 10000 == 0)
			{
				System.out.println(dictionary[i] + " was placed in slot "
						+ index + " of the hash table after " + collisions
						+ " collisions");
			}

			totalCollisions += collisions;
		}

		System.out.println(
				"\nThe total number of collisions was " + totalCollisions);
	}

	/*
	 * Raises a number to a power with the given modulus when raising a number
	 * to a power, the number quickly becomes too large to handle you need to
	 * multiply numbers in such a way that the result is consistently moduloed
	 * to keep it in the range however you want the algorithm to work quickly -
	 * having a multiplication loop would result in an O(n) algorithm! the trick
	 * is to use recursion - keep breaking the problem down into smaller pieces
	 * and use the modMult method to join them back together
	 */
	private static int modPow(int number, int power, int modulus)
	{
		if (power == 0)
		{
			return 1;
		}

		else if (power % 2 == 0)
		{
			int halfPower = modPow(number, power / 2, modulus);

			return modMult(halfPower, halfPower, modulus);
		}

		else
		{
			int halfPower = modPow(number, power / 2, modulus);
			int firstBit = modMult(halfPower, halfPower, modulus);

			return modMult(firstBit, number, modulus);
		}
	}

	/*
	 * Multiplies the first number by the second number with the given modulus a
	 * long can have a maximum of 19 digits. Therefore, if you're multiplying
	 * two ten digits numbers the usual way, things will go wrong you need to
	 * multiply numbers in such a way that the result is consistently moduloed
	 * to keep it in the range however you want the algorithm to work quickly -
	 * having an addition loop would result in an O(n) algorithm! the trick is
	 * to use recursion - keep breaking down the multiplication into smaller
	 * pieces and mod each of the pieces individually
	 */
	private static int modMult(int first, int second, int modulus)
	{
		if (second == 0)
		{
			return 0;
		}

		else if (second % 2 == 0)
		{
			int half = modMult(first, second / 2, modulus);

			return (half + half) % modulus;
		}

		else
		{
			int half = modMult(first, second / 2, modulus);

			return (half + half + first) % modulus;
		}
	}

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 *
	 * @param aFile
	 *            is a file which already exists and can be read.
	 */
	private static String getContents(File aFile)
	{
		// ...checks on aFile are elided
		StringBuffer sb = new StringBuffer();
		// Declared here only to make visible to finally clause
		BufferedReader br = null;

		try {
			// Use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			br = new BufferedReader(new FileReader(aFile));
			String input = null; // Not declared within while loop
			int i = 0;

			// readLine() is a bit quirky: It returns the content of a line
			// MINUS the newline. It returns null only for the END of the
			// stream. It returns an empty String if two newlines appear in a
			// row.
			while ((input = br.readLine()) != null)
			{
				dictionary[i++] = input;
				sb.append(System.getProperty("\n"));
			}
		}

		catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}

		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		finally
		{
			try
			{
				if (br != null)
				{
					// Flush and close both "input" and its underlying
					// FileReader
					br.close();
				}
			}

			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

		return sb.toString();
	}
}