// IN1002 Introduction to Algorithms
// Coursework 2022/2023
//
// Submission by
// Adam Rezzag Salem
// Adam.Rezzag-Salem@city.ac.uk

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Solver {

    private int[][] clauseDatabase = null;
    private int numberOfVariables = 0;

    /* Your  answers go below here */

    // Part A.1
    // Worst case complexity : O(v) as you must go through the whole array as the other literals gave unsatisfiable assignments.
    // Best case complexity : O(1) as the best case assumes the first literal is true and so there is no need to go through the whole array.
    public boolean checkClause(int[] assignment, int[] clause) {
        for (int element : clause) {
            int assignmentElement = assignment[Math.abs(element)];
            if ((element > 0 && (assignmentElement == 1)) || ((element < 0 && (assignmentElement == -1)))) {
                return true;
            }
        }
        return false;
    }

    // Part A.2
    // Worst case complexity : O(cl) as one must loop through each clause as well as the length of the clauses.
    // Best case complexity : O(l) as we assume the first clause is false which means we only had to loop through the first clause.
    public boolean checkClauseDatabase(int[] assignment, int[][] clauseDatabase) {
        boolean isChecking;
        for (int i = 0; i < clauseDatabase.length; i++) {
            int[] clause = clauseDatabase[i];
            isChecking = checkClause(assignment, clause);
            if (isChecking == false) {
                return false;
            }
        }
        return true;
    }

    // if one literal is true, then the clause is satisfiable.
    // if all literals are false, then the clause is unsatisfiable.
    // if all literals are unknown or the literals are mixed with unknowns and "falses" or unknowns, then the clause should output 0 (unknown assignment).

    // Part A.3
    // Worst case complexity : O(v) because we assume that the worst case is when we have to go through all the literals to attain either satisfiable, unsatisfiable or unknown clause.
    // Best case complexity : O(1) because it occurs when the first literal within the clause is either assigned an unknown or outputs true.
    public int checkClausePartial(int[] partialAssignment, int[] clause) {
        int numberUnknowns = 0;
        for (int c : clause) {
            int clauseAssignment = partialAssignment[Math.abs(c)];
            if ((c > 0 && clauseAssignment == 1) || (c < 0 && clauseAssignment == -1)) {
                return 1;
            }
            if (clauseAssignment == 0) {
                numberUnknowns++;
            }
        }
        if (numberUnknowns > 0) {
            return 0;
        }
        return -1;
    }

    // Part A.4
    // Worst case complexity : O(v) as the compiler must loop through the whole array to check if there is only one unknown (unknown assignment can be found all the way at the end).
    // Best case complexity : O(1) for when the first literal gives a satisfiable argument.

    public int findUnit(int[] partialAssignment, int[] clause) {
        int numberUnknowns = 0;
        int unknownLiteral = 0;
        for (int c : clause) {
            int clauseAssignment = partialAssignment[Math.abs(c)];
            if ((c > 0 && clauseAssignment == 1) || (c < 0 && clauseAssignment == -1)) {
                return 0;
            }
            if (clauseAssignment == 0) {
                numberUnknowns++;
                unknownLiteral = c;
            }
        }
        if (numberUnknowns == 1) {
            return unknownLiteral;
        }
        return 0;
    }

    // Part B
    // I think this can solve ????    
    int[] checkSat(int[][] clauseDatabase) {


        int[] assignment;
        int lengthAssignment = 0;

        // find maximum number to get array length and add one for the 0th element
        for (int[] clause : clauseDatabase) {
            for (int c : clause) {
                if (Math.abs(c) > lengthAssignment) {
                    lengthAssignment = Math.abs(c);
                }
            }
        }


        assignment = new int[lengthAssignment + 1];
        assignment[0] = 0;

		/* create an empty hashset and whenever we assign a truth value to a literal, we add it on
		to the set. When we loop through we check if it has already been assigned to prevent reassignment
		of a literal
		 */

        HashSet<Integer> literalSet = new HashSet<>();


        for (int i = 0; i < clauseDatabase.length; i++) {
            for (int j = 0; j < clauseDatabase[i].length; j++) {
                if (findUnit(assignment, clauseDatabase[i]) != 0) {
                    if (clauseDatabase[i][j] > 0) {
                        assignment[Math.abs(clauseDatabase[i][j])] = 1;
                    } else {
                        assignment[Math.abs(clauseDatabase[i][j])] = -1;
                    }
                }
                if (!alreadyAssigned(literalSet, Math.abs(clauseDatabase[i][j]))) {
                    for (int truthValue = -1; truthValue <= 1; truthValue += 2) {
                        assignment[Math.abs(clauseDatabase[i][j])] = truthValue;
                        if (checkAllConditions(assignment, clauseDatabase[i])) {
                            literalSet.add(Math.abs(clauseDatabase[i][j]));
                        }
                    }
                }

            }
            if (checkClauseDatabase(assignment, clauseDatabase)) {
                return assignment;
            }
        }
        for (int a : assignment) {
            System.out.println(a);
        }
        return assignment;

    }

    public boolean checkAllConditions(int[] assignment, int[] clause) {
        if (checkClause(assignment, clause) && checkClausePartial(assignment, clause) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean alreadyAssigned(HashSet<Integer> literalList, int literal) {

        if (literalList.contains(literal)) {
            return true;
        }
        return false;
    }


    /*****************************************************************\
     *** DO NOT CHANGE! DO NOT CHANGE! DO NOT CHANGE! DO NOT CHANGE! ***
     *******************************************************************
     *********** Do not change anything below this comment! ************
     \*****************************************************************/

    public static void main(String[] args) {

        try {
            Solver mySolver = new Solver();

            System.out.println("Enter the file to check");

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String fileName = br.readLine();

            int returnValue = 0;

            Path file = Paths.get(fileName);
            BufferedReader reader = Files.newBufferedReader(file);
            returnValue = mySolver.runSatSolver(reader);

            return;

        } catch (Exception e) {
            System.err.println("Solver failed :-(");
            e.printStackTrace(System.err);
            return;

        }
    }

    public int runSatSolver(BufferedReader reader) throws Exception, IOException {

        // First load the problem in, this will initialise the clause
        // database and the number of variables.
        loadDimacs(reader);

        // Then we run the part B algorithm
        int[] assignment = checkSat(clauseDatabase);

        // Depending on the output do different checks
        if (assignment == null) {
            // No assignment to check, will have to trust the result
            // is correct...
            System.out.println("s UNSATISFIABLE");
            return 20;

        } else {
            // Cross check using the part A algorithm
            boolean checkResult = checkClauseDatabase(assignment, clauseDatabase);

            if (checkResult == false) {
                throw new Exception("The assignment returned by checkSat is not satisfiable according to checkClauseDatabase?");
            }

            System.out.println("s SATISFIABLE");

            // Check that it is a well structured assignment
            if (assignment.length != numberOfVariables + 1) {
                throw new Exception("Assignment should have one element per variable.");
            }
            if (assignment[0] != 0) {
                throw new Exception("The first element of an assignment must be zero.");
            }
            for (int i = 1; i <= numberOfVariables; ++i) {
                if (assignment[i] == 1 || assignment[i] == -1) {
                    System.out.println("v " + (i * assignment[i]));
                } else {
                    throw new Exception("assignment[" + i + "] should be 1 or -1, is " + assignment[i]);
                }
            }

            return 10;
        }
    }

    // This is a simple parser for DIMACS file format
    void loadDimacs(BufferedReader reader) throws Exception, IOException {
        int numberOfClauses = 0;

        // Find the header line
        do {
            String line = reader.readLine();

            if (line == null) {
                throw new Exception("Found end of file before a header?");
            } else if (line.startsWith("c")) {
                // Comment line, ignore
                continue;
            } else if (line.startsWith("p cnf ")) {
                // Found the header
                String counters = line.substring(6);
                int split = counters.indexOf(" ");
                numberOfVariables = Integer.parseInt(counters.substring(0, split));
                numberOfClauses = Integer.parseInt(counters.substring(split + 1));

                if (numberOfVariables <= 0) {
                    throw new Exception("Variables should be positive?");
                }
                if (numberOfClauses < 0) {
                    throw new Exception("A negative number of clauses?");
                }
                break;
            } else {
                throw new Exception("Unexpected line?");
            }
        } while (true);

        // Set up the clauseDatabase
        clauseDatabase = new int[numberOfClauses][];

        // Parse the clauses
        for (int i = 0; i < numberOfClauses; ++i) {
            String line = reader.readLine();

            if (line == null) {
                throw new Exception("Unexpected end of file before clauses have been parsed");
            } else if (line.startsWith("c")) {
                // Comment; skip
                --i;
                continue;
            } else {
                // Try to parse as a clause
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                String working = line;

                do {
                    int split = working.indexOf(" ");

                    if (split == -1) {
                        // No space found so working should just be
                        // the final "0"
                        if (!working.equals("0")) {
                            throw new Exception("Unexpected end of clause string : \"" + working + "\"");
                        } else {
                            // Clause is correct and complete
                            break;
                        }
                    } else {
                        int var = Integer.parseInt(working.substring(0, split));

                        if (var == 0) {
                            throw new Exception("Unexpected 0 in the middle of a clause");
                        } else {
                            tmp.add(var);
                        }

                        working = working.substring(split + 1);
                    }
                } while (true);

                // Add to the clause database
                clauseDatabase[i] = new int[tmp.size()];
                for (int j = 0; j < tmp.size(); ++j) {
                    clauseDatabase[i][j] = tmp.get(j);
                }
            }
        }

        // All clauses loaded successfully!
        return;
    }

}