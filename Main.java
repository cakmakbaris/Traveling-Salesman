/**
 * This is a java program that utilizes ant colony optimization and compare the results to brute force method
 * To show how fast and accurate results does ant colony optimization approach enables us
 * @author Baris Cakmak, Student ID: 2022400000
 * @since Date : 02.05.2024
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("input04.txt");
        Scanner sc = new Scanner(file);

        ArrayList<Nodes> nodesArrayList = new ArrayList<>();

        //Reading the file while simultaneously creating and adding nodes objects to nodesArrayList
        int index = 0;
        while(sc.hasNextLine()){
            String[] coordinates = sc.nextLine().split(",");
            double currentX = Double.parseDouble(coordinates[0]);
            double currentY = Double.parseDouble(coordinates[1]);
            Nodes node = new Nodes(currentX, currentY, index);
            nodesArrayList.add(node);
            index++;
        }


        int chosenMethod = 2; // if 1 -> call brute force method if 2 -> call ant colony optimization method
        if (chosenMethod == 1){
            bruteForce(nodesArrayList);
        }
        else if (chosenMethod == 2){
            int choice = 1; // 1 for drawing the path 2 for drawing the pheromone intensity graph
            antColonyOptimization(nodesArrayList, choice);
        }
    }

    /**
     * The brute force method calculates every permutation of possible roads and finds the shortest path and distance
     * After that, it draws the nodes and connections between them (Orange one is always the starting node in Brute-Force method)
     * @param nodesArrayList is the every node that we have created while reading the file in main
     */
    public static void bruteForce(ArrayList<Nodes> nodesArrayList){

        long startTime = System.currentTimeMillis(); // Start measuring the time


        int size = nodesArrayList.size(); // The number of total nodes
        int[] route = new int[size-1]; // We put -1 because in the initial route, zeroth index (migros) will not exist
        int index = 0;

        // Add nodes to the array if the index is not 0
        for (Nodes node : nodesArrayList){
            if (node.getNodeIndex()!=0){
                route[index] = node.getNodeIndex();
                index++;
            }
        }

        double [] shortestDistance = new double[1]; // In order to store the shortest distance without using a global variable, store it in an array of size 1
        shortestDistance[0] = Double.MAX_VALUE; // Initially assign it a maximum value
        ArrayList<ArrayList<Integer>> bestRouteStore = new ArrayList<>(); // This ArrayList will just contain 1 ArrayList, that is our shortest path
        ArrayList<Integer> bestRoute = new ArrayList<>(); // Our shortest path

        bestRoute.add(0);
        for (int elem : route){
            bestRoute.add(elem);
        }
        bestRoute.add(0);


        bestRouteStore.add(bestRoute); // Adding a temporary ArrayList
        permute(nodesArrayList, route, shortestDistance, bestRouteStore, 0);

        ArrayList<Integer> modifiedArrayList = new ArrayList<>();
        modifiedArrayList.addAll(bestRouteStore.getFirst()); // Copy our best path to modifiedArrayList

        for (int i = 0; i<bestRouteStore.getFirst().size(); i++){
            modifiedArrayList.set(i, modifiedArrayList.get(i)+1); // Increase the index numbers by 1 since we need index+1 while drawing
        }
        System.out.println("Method: Brute-Force Method");
        System.out.println(String.format("Shortest Distance: %.5f", shortestDistance[0]));
        System.out.println("Shortest Path: " + modifiedArrayList);

        long endTime = System.currentTimeMillis(); // End time
        double totalTime = (double) (endTime - startTime) /1000; // To second
        System.out.println(String.format("Time it takes to find the shortest path: %.2f seconds", totalTime));

        boolean isIndex1Orange = true; // If we are in Brute-Force method, index 0 (number 1 node) should be orange
        drawPath(nodesArrayList, bestRouteStore.getFirst(), isIndex1Orange); // Draw the path: send every node, the best path, and isOrange variable


        //End of Brute-Force method...
    }

    /**
     * Permute method gets an array and generates every possible permutation from it while simultaneously controlling if it is the shortest path
     * Every method calls the permute method array.length-k times.
     * This method functions recursively
     * @param nodesArrayList is the every node that we have created while reading the file in main
     * @param array the array we send to get the permutations of
     * @param shortestDistance shortest distance of the best path until now
     * @param bestRouteStore best path until now
     * @param k current position in the array
     */
    private static void permute(ArrayList<Nodes> nodesArrayList, int[] array, double[] shortestDistance, ArrayList<ArrayList<Integer>> bestRouteStore, int k) {
        if (k == array.length) {
            // If we are in here, we generated a permutation.
            calculateRoute(nodesArrayList, array, shortestDistance, bestRouteStore); // Check if this path is the smallest, if it is change the shortest path
        } else {
            // If we are in here, we are still generation the permutation
            for (int i = k; i < array.length; i++) {

                // Temporarily change the i'th and k'th element of the array
                int temp = array[i];
                array[i] = array[k];
                array[k] = temp;

                permute(nodesArrayList,array, shortestDistance, bestRouteStore, k + 1); // Send this changed array to permute again with k+1

                // Undo the temporary change
                temp = array[k];
                array[k] = array[i];
                array[i] = temp;
            }
        }
    }

    /**
     * This code calculates the total distance of the 'array' path and if it is shortest until now, make it the shortest
     * Furthermore, if the current path is the shortest distanced path, assign this path to the shortest path
     * @param nodesArrayList is the every node that we have created while reading the file in main
     * @param array the array(path) we are controlling the total distance of
     * @param shortestDistance shortest distance of the best path until now
     * @param bestRouteStore best path until now
     */
    public static void calculateRoute(ArrayList<Nodes> nodesArrayList, int[] array, double[] shortestDistance, ArrayList<ArrayList<Integer>> bestRouteStore){

        double totalDist = 0; // Initially total distance is 0
        int startingIndex = 0; // startingIndex is always 0 in Brute-Force method
        int previousIndex = 0; // previousIndex is the starting index initially

        // Calculate the distance between previous index and current index and add it to totalDist
        for (int currentIndex : array){
            // findDistance method gets 2 nodes as parameter
            totalDist += findDistance(nodesArrayList.get(previousIndex), nodesArrayList.get(currentIndex)); // Calculate with the help of findDistance method
            previousIndex = currentIndex; // Change the previous index since we are done with it
        }
        totalDist += findDistance(nodesArrayList.get(previousIndex), nodesArrayList.get(startingIndex)); // The distance of returning back to startingCity


        // If totalDist is less than overall smallest distance, change overall smallest distance
        if (totalDist < shortestDistance[0]){
            shortestDistance[0] = totalDist;

            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(0);
            // Add current path's elements to temp ArrayList
            for (int elem : array){
                temp.add(elem);
            }
            temp.add(0);

            bestRouteStore.set(0, temp); // Set best route to temp (our current best path)
        }

        // calculateRoute method ends here...
    }


    /**
     * Ant colony optimization creates antsPerIteration number of ants every iteration and then ants travel randomly(!)
     * Every ant leaves a pheromone of the edge it visited, after every iteration, pheromone intensities are decreased since they symbolise vaporization
     * After iterationCount iterations we find the smallest path and smallest path distance with the help of our ant friends :)
     * This approach enables us to solve problems that would normally take maybe 1000 years but, as expected, it does not give %100 true results
     * But for humanity, it is more meaningful to solve a problem in 5 seconds with %1 error rather than wait 1000 years to get the precise result
     * @param nodesArrayList is the every node that we have created while reading the file in main
     * @param choice if 1 -> draw the path if 2 -> draw the pheromone intensity graph
     */
    public static void antColonyOptimization(ArrayList<Nodes> nodesArrayList, int choice){

        long startTime = System.currentTimeMillis(); // Start the time

        int iterationCount = 300; // The number of total iterations we will make
        int antsPerIteration = 200; // The number of ants that is going to be produced every iteration
        double degradationFactor = 0.9; // The permanency constant of pheromones
        double alpha = 1; // Exponential coefficient of pheromone intensities
        double beta = 5; // Exponential coefficient of distances
        double initialPheromoneIntensity =0.1; // initial pheromone intensity of every edge
        double qValue = 0.0001; // Is a constant that determines how much an ant traversal contributes to an edge



        int size = nodesArrayList.size(); // Total node number


        // Creating our 2D matrix of distances. distances[i][j] and distances[j][i] are the same value
        double [][] distances = new double[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                distances[i][j] = findDistance(nodesArrayList.get(i), nodesArrayList.get(j));
            }
        }

        // Creating our 2D matrix of pheromones. pheromones[i][j] and pheromones[j][i] are the same value
        double [][] pheromones = new double[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                pheromones[i][j] = initialPheromoneIntensity;
            }
        }


        // Creating our 2D matrix of edgeWeights. edgeWeights[i][j] and edgeWeights[j][i] are the same value
        double [][] edgeWeights = new double[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                edgeWeights[i][j] = Math.pow(pheromones[i][j], alpha)/Math.pow(distances[i][j], beta);
            }
        }



        ArrayList<Integer> overallBestPath = new ArrayList<>(); // Shortest path will be stored in an ArrayList
        double overallSmallestDistance = Double.MAX_VALUE; // Shortest distance will be stored in a double variable


        for (int i = 0; i < iterationCount; i++){
            // The code below is executed in every iteration


            for (int j = 0; j < antsPerIteration; j++){
                // The code below is executed for every ant



                Random random = new Random(); // Creating a random object, it will be used for the entire code

                double totalDistance = 0; // Total path distance, initially 0
                ArrayList<Integer> path = new ArrayList<>(); // Current path will be stored in an ArrayList of Integers
                ArrayList<Nodes> unvisitedNodes = new ArrayList<>(); // Creating an ArrayList of Nodes to store unvisited nodes
                unvisitedNodes.addAll(nodesArrayList); // Initially, it contains every node


                int startingNodeIndex = random.nextInt(0, size); // Randomly pick a startingNode
                path.add(startingNodeIndex); // Add the index to the path
                int currentNodeIndex = startingNodeIndex;
                unvisitedNodes.remove(nodesArrayList.get(startingNodeIndex)); // Remove the starting node from unvisitedNodes since we are already on it

                // The code below is executed until there are no unvisitedNodes left (stops if every node is visited)
                while(!unvisitedNodes.isEmpty()){
                    double edgeSum = 0; // Sum of the edgeWeights of unvisited nodes
                    ArrayList<Double> possibilityArrayList = new ArrayList<>(); // We will store the possibilities such as 0.1, 0.3 in this ArrayList
                    possibilityArrayList.add(0.0); // Add 0.0 because we will use intervals

                    // Add every edgeWeights in edgeSum variable
                    for (Nodes a : unvisitedNodes){
                        edgeSum += edgeWeights[currentNodeIndex][a.getNodeIndex()];
                    }

                    // Find the relative possibilities according to edgeSum
                    for (Nodes a : unvisitedNodes){
                        possibilityArrayList.add((edgeWeights[currentNodeIndex][a.getNodeIndex()]) / edgeSum);
                    }

                    double probability = random.nextDouble(0, 1); // Pick a random possibility

                    // a represents the interval we are in for ex: a = 0 -> (0.0, 0.x) or a = 1 -> (0.x, 0.y)
                    for (int a = 1; a < possibilityArrayList.size(); a++){
                        // If the probability is between an interval, pick that interval
                        if ((possibilityArrayList.get(a-1) <= probability) && (possibilityArrayList.get(a) + possibilityArrayList.get(a-1) >= probability)){
                            int newNode = unvisitedNodes.get(a-1).getNodeIndex(); // Our new node will be the corresponding node of this interval
                            totalDistance += distances[currentNodeIndex][newNode]; // Adding the distance to totalDistance
                            path.add(newNode); // Adding newNode to our path since we picked it to visit
                            unvisitedNodes.remove(nodesArrayList.get(newNode)); // The node with index newNode is not unvisited anymore, so it should be removed
                            currentNodeIndex = newNode; // For the next iteration, assign newNode to currentNodeIndex
                            break; // This is very important! since we used <= and >= if we do not break here, we might pick another node at the same time and it is catastrophic
                        }
                    }
                }
                // After the while loop has finished, currentNodeIndex stayed at the last node.
                // But we also want to go back to our starting node, so we should calculate it manually
                totalDistance += distances[currentNodeIndex][startingNodeIndex];
                path.add(startingNodeIndex);


                double delta = qValue / totalDistance; // Delta value. It will be used below

                // Since the ant has traveled a path, edges of this path should be incremented in terms of pheromones
                for (int a = 0; a < size; a++){
                    int first = path.get(a); // starting node index of the current edge
                    int second = path.get(a+1); // ending node index of the current edge
                    // pheromones[5][3] and pheromones[3][5] should be the same for instance, so we must change both
                    pheromones[first][second] += delta;
                    pheromones[second][first] += delta;
                    // edgeWeights[5][3] and edgeWeights[3][5] should be the same for instance, so we must change both
                    edgeWeights[first][second] = Math.pow(pheromones[first][second], alpha) / Math.pow(distances[first][second], beta);
                    edgeWeights[second][first] = edgeWeights[first][second];
                }

                // After everything is calculated, we check if this is the best ant traversal
                // If it is, change overallSmallest and overallBest
                if (totalDistance < overallSmallestDistance){
                    overallSmallestDistance = totalDistance;
                    overallBestPath = path;
                }
            }


            // After antsPerIteration ants have traversed their paths, before going to new step, decrease the pheromones of
            // every edge (this represents the vaporization of the pheromones)
            // Also we should adjust the edgeWeights simultaneously since edgeWeights depend on current pheromone levels
            for (int a = 0; a < size; a++){
                for (int b = 0; b < size; b++){
                    pheromones[a][b] *= degradationFactor;
                    edgeWeights[a][b] = Math.pow(pheromones[a][b], alpha) / Math.pow(distances[a][b], beta);
                }
            }
        }

        slideIndex(overallBestPath);
        ArrayList<Integer> incrementNumbersByOne = new ArrayList<>(); // We will print this ArrayList

        for (int i = 0; i<overallBestPath.size(); i++){
            incrementNumbersByOne.add(overallBestPath.get(i)+1); // Increase the index numbers by 1 since we need index+1 while drawing
        }


        System.out.println("Method: Ant Colony Optimization");
        System.out.println(String.format("%.5f", overallSmallestDistance));
        System.out.println("Shortest Path: " + incrementNumbersByOne);

        long endTime = System.currentTimeMillis(); // End time
        double totalTime = (double) (endTime - startTime) /1000; // To second
        System.out.println(String.format("Time it takes to find the shortest path: %.2f seconds", totalTime));


        // Draw the shortest path or the pheromone intensity graph according to 'choice' parameter
        boolean isIndex1Orange = false; // Since this is not Brute-Force method, 0 indexed node will not be colored to orange
        if (choice == 1){
            drawPath(nodesArrayList, overallBestPath, isIndex1Orange);
        }
        else if (choice == 2){
            drawPheromoneGraph(nodesArrayList, pheromones);
        }

        // Ant Colony Optimization method ends here...
    }

    /**
     * Returns the distance between 2 nodes, this method is very helpful and reduces our code complexity
     * @param node1 first node
     * @param node2 second node
     * @return the distance between two nodes in double
     */
    public static double findDistance(Nodes node1, Nodes node2){
        double firstTerm = Math.pow(node1.getX()-node2.getX(), 2);
        double secondTerm = Math.pow(node1.getY()-node2.getY(), 2);
        return Math.pow(firstTerm + secondTerm, 0.5);
    }

    /**
     * Draws the path according to 'path' parameter
     * @param nodesArrayList is the every node that we have created while reading the file in main
     * @param path the path to draw
     * @param isIndex1Orange a boolean variable, if true -> 0 indexed node is colored to orange, else it is colored to gray
     */
    public static void drawPath(ArrayList<Nodes> nodesArrayList, ArrayList<Integer> path, boolean isIndex1Orange){

        StdDraw.setCanvasSize(800, 800);

        //Since the value pairs in input files are from (0,1), our x and y scale should match it
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering(); // In order to enable smoothness



        //Drawing lines
        for (int a = 0; a < path.size()-1; a++){
            Nodes currentNode = nodesArrayList.get(path.get(a));
            Nodes nextNode = nodesArrayList.get(path.get(a+1));
            StdDraw.setPenRadius(0.02);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.line(currentNode.getX(), currentNode.getY(), nextNode.getX(), nextNode.getY());
        }

        // Drawing the circles
        for (Nodes currentNode : nodesArrayList){
            if (isIndex1Orange && currentNode.getNodeIndex()==0){
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            }
            else {
                StdDraw.setPenColor(StdDraw.GRAY);
            }
            StdDraw.filledCircle(currentNode.getX(), currentNode.getY(), 0.03);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(currentNode.getX(), currentNode.getY(), Integer.toString(currentNode.getNodeIndex()+1));
        }

        StdDraw.show();
    }

    /**
     * Draws the pheromone intensity graph according to 'pheromones'
     * @param nodesArrayList is the every node that we have created while reading the file in main
     * @param pheromones a 2D double array of pheromone intensities
     */
    public static void drawPheromoneGraph(ArrayList<Nodes> nodesArrayList, double[][] pheromones){
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();

        for (int a = 0; a < pheromones.length; a++){
            for (int b = 0; b<pheromones.length; b++){
                Nodes aCity = nodesArrayList.get(a);
                Nodes bCity = nodesArrayList.get(b);
                StdDraw.setPenRadius(pheromones[a][b]/8); // Here we divide it by 8 to make it look good
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.line(aCity.getX(), aCity.getY(), bCity.getX(), bCity.getY());
            }
        }

        // Draw the nodes
        for (Nodes currentNode : nodesArrayList){
            StdDraw.setPenColor(StdDraw.GRAY);
            StdDraw.filledCircle(currentNode.getX(), currentNode.getY(), 0.03);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(currentNode.getX(), currentNode.getY(), Integer.toString(currentNode.getNodeIndex()+1)); // We add +1 because we need 1 for 0 or 3 for 2
        }

        StdDraw.show();
    }

    public static void slideIndex(ArrayList<Integer> path){
        path.removeFirst();
        int currentElement = path.getFirst();
        while (currentElement != 0){
            path.remove(path.indexOf(currentElement));
            path.add(currentElement);
            currentElement = path.getFirst();

        }

        path.add(currentElement);
    }
}
