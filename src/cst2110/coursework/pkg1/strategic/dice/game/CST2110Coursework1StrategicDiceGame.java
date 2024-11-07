package cst2110.coursework.pkg1.strategic.dice.game;

import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;

public class CST2110Coursework1StrategicDiceGame {

static Scanner scanner = new Scanner(System.in); 
static Random random = new Random(); 
    
    public static void main(String[] args) {
        
        // This displays the welcome message and how to play
        System.out.println("************************");
        System.out.println("Welcome to the Dice Game");
        System.out.println("************************");
        System.out.println("");
        System.out.println("***************************************************************************************************");
        System.out.println("This is a relatively simple but strategic dice game.");
        System.out.println("The game is played in the console over 11 rounds.");
        System.out.println("Each round, you throw 2 dice, and you select where you would like to place your number in the table.");
        System.out.println("In the first round, each player must put their number in different columns in the table.");
        System.out.println("The person with the highest score in that column, wins the column, and gets added to their score");
        System.out.println("The winner is the player with the most points at the end.");
        System.out.println("***************************************************************************************************");
        System.out.println("");

        initializeTable();
        playGame();
        displayResults();
    }

    // This is how many columns there are that will be used to play (11 rounds)
    static final int COLUMN_COUNT = 11; 
    
    // This is for the table, in rows 1 to 3, you get the players
    static String[][] table = new String[4][COLUMN_COUNT + 1]; 

    // This gets the table which has the player names + column numbers 
    static void initializeTable() {
        for (int i = 1; i < table.length; i++) {
            table[i][0] = "Player " + i;
        }
        table[0][0] = " ";
        for (int i = 1; i <= COLUMN_COUNT; i++) {
            table[0][i] = "Column " + i;
        }
    }

    // This loop runs the game for 11 rounds in total
    static void playGame() {
        for (int round = 1; round <= 11; round++) {
            System.out.println("\n--- Round " + round + " ---");
            int[] chosenColumns = new int[table.length - 1];

            // Player has to press enter in order to roll the dice
            for (int player = 0; player < table.length - 1; player++) {
                System.out.println("Press Enter to roll the dice for " + table[player + 1][0]);
                scanner.nextLine();

                int total = rollDice();
                int column = getValidColumn(player, round, chosenColumns);
                table[player + 1][column] = String.valueOf(total);

                if (round == 1) {
                    chosenColumns[player] = column;
                }
                System.out.println(printTable(table));

                if (isColumnFilled(column)) {
                    updateScores(column);
                }
            }
        }
    }

    // This will track each player's score
    static int[] playerScores = new int[3]; 

    // This rolls the 2 dice, then gives output 
    static int rollDice() {
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;
        System.out.println("You rolled: " + dice1 + " and " + dice2 + " (Total: " + (dice1 + dice2) + ")");
        return dice1 + dice2;
    }

    // This one will be actively recording the winner for each column
    static String[] columnWinners = new String[COLUMN_COUNT]; 

    // This will run checks to see if the user enters a valid number
    static int getValidColumn(int player, int round, int[] chosenColumns) {
        while (true) {
            System.out.print("Choose a column (1-11) to place the total" +
                    (round == 1 ? " (For Round 1, it has to be different from other players): " : ": "));

            try {
                // This checks if the input is actually an integer
                if (!scanner.hasNextInt()) {
                    String invalidInput = scanner.next(); // consume the invalid input
                    System.out.println("Error: '" + invalidInput + "' is not a valid number. Please enter a number between 1 and 11.");
                    continue;
                }

                int column = scanner.nextInt();
                scanner.nextLine(); // consume the newline

                // This checks if the number is within valid range for the column
                if (column < 1 || column > COLUMN_COUNT) {
                    System.out.println("Error: The column number must be between 1 and 11. You entered: " + column);
                    continue;
                }

                // This checks if the column is already taken 
                if (table[player + 1][column] != null) {
                    System.out.println("Error: Column " + column + " is already filled in. Please choose another column.");
                    continue;
                }

                // This checks for the round 1 constraints (the players can't all input their numbers in the same column in round 1)
                if (round == 1 && isColumnChosen(chosenColumns, column)) {
                    System.out.println("Error: In Round 1, you must choose a different column than other players. Column " + column + " is already taken.");
                    continue;
                }

                return column;

            } catch (InputMismatchException e) {
                // This is catch block is a backup in case something goes wrong with the input handling
                scanner.nextLine(); 
                System.out.println("Error: Please enter a valid number between 1 and 11.");
            }
        }
    }

    // This will check if a column has already been chosen in the first round 
    static boolean isColumnChosen(int[] chosenColumns, int column) {
        for (int chosenColumn : chosenColumns) {
            if (chosenColumn == column) {
                return true;
            }
        }
        return false;
    }

    // This will track which columns are filled in 
    static boolean[] columnFilled = new boolean[COLUMN_COUNT];

    // This will check if all players have placed numbers in a column
    static boolean isColumnFilled(int column) {
        for (int i = 1; i < table.length; i++) {
            if (table[i][column] == null) return false;
        }
        return true;
    }
    
    //This will update the score when a column is completed 
    // It will determine who the winner is and give the right amount of points (e.g. you win column 1, you get 1 point)
    static void updateScores(int column) {
        if (columnFilled[column - 1]) return;

        // This will the string scores into integers to be compared 
        int[] scores = new int[table.length - 1];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = Integer.parseInt(table[i + 1][column]);
        }

        // This finds the highest score and count winners 
        int maxScore = scores[0];
        int winnerCount = 1;

        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                winnerCount = 1;
            } else if (scores[i] == maxScore) {
                winnerCount++;
            }
        }

        // This will awards points
        // And it will also mark losing scores with a *
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == maxScore) {
                if (winnerCount == 1) {
                    playerScores[i] += column;
                    columnWinners[column - 1] = "Player " + (i + 1);
                    System.out.println("Player " + (i + 1) + " has the highest score for Column " +
                            column + ", they will get " + column + " points.");
                }
            } else {
                table[i + 1][column] = "*"; 
            }
        }

        // This ensures that if there is a tie, no points will be allocated to any palyer 
        if (winnerCount > 1) {
            columnWinners[column - 1] = "Tie - No Points";
            System.out.println("Tie in Column " + column + ". No points awarded.");
        }

        columnFilled[column - 1] = true; // This makes sure that specific columnes that are finished, don't get calculated for the score again
    }

    // This will create the table 
    static String printTable(String[][] table) {
        String result = "\nCurrent Table:\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                result += String.format("%-10s", (table[i][j] != null) ? table[i][j] : " ");
            }
            result += "\n";  // This line was added so that the rows are separated instead of all being on one line 
        }
        return result + "\n"; // This just adds an extra blank line after the table
    }

    // This will display the final scores 
    static void displayResults() {
        System.out.println("\n--- Final Scores ---");
        int maxScore = 0;
        int winner = 0;

        // This will display individual game scores 
        for (int i = 0; i < playerScores.length; i++) {
            System.out.println("Player " + (i + 1) + " total score: " + playerScores[i]);
            if (playerScores[i] > maxScore) {
                maxScore = playerScores[i];
                winner = i + 1;
            }
        }

        // This displays who the winner is 
        System.out.println("Player " + winner + " wins the game with " + maxScore + " points!");

        // This displays the final table 
        System.out.println("\n--- Final Game Table ---");
        System.out.println(printTable(table));

        //This displays which player won which column
        System.out.println("\n--- Column Winners ---");
        for (int j = 1; j <= COLUMN_COUNT; j++) {
            System.out.println("Column " + j + " winner: " + columnWinners[j - 1]);
        }
    }
}
