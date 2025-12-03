# Traveling Salesman Visualization – Brute Force vs Ant Colony Optimization

This project implements and compares two approaches to the **Traveling Salesman Problem (TSP)** in Java:

- a **Brute-Force search** that tries all permutations of routes, and  
- an **Ant Colony Optimization (ACO)** algorithm that finds a good route much faster using pheromone-based stochastic search.

Both methods work on the same set of 2D points (cities) read from an input file, and the final routes are **visualized** using the `StdDraw` library.

---

## 📁 Project Structure

- `BarisCakmak.java`  
  Main class. Reads the input file, builds the list of nodes, selects the method (Brute Force or ACO), invokes the algorithm, and triggers drawing.

- `Nodes.java`  
  Represents a city/node with:
  - `x`, `y` coordinates in the unit square,
  - an integer `nodeIndex` used as an ID.

Other important **methods inside `BarisCakmak`**:

- `bruteForce(ArrayList<Nodes> nodesArrayList)`  
  Runs exhaustive search on all permutations of nodes (starting and ending at node 0), finds the shortest route, prints distance and path, and calls the drawing routine.

- `permute(...)` and `calculateRoute(...)`  
  Recursive helpers used by brute force to generate permutations and evaluate each candidate route.

- `antColonyOptimization(ArrayList<Nodes> nodesArrayList, int choice)`  
  Runs the Ant Colony Optimization algorithm:
  - creates a certain number of ants per iteration,
  - moves them probabilistically based on pheromone and distance,
  - updates pheromone levels and edge weights,
  - keeps track of the best tour found.

- `drawPath(...)`  
  Uses `StdDraw` to draw nodes and the best path found by an algorithm.  
  In Brute Force mode, the starting node is highlighted in orange.

- `slideIndex(ArrayList<Integer> path)`  
  Utility to rotate the path so that it starts from node 0 when drawing.

---

## 🧮 Algorithms

### Brute-Force Search

The brute-force approach:

1. Builds an array of node indices (excluding node 0, which is fixed as the start/end city).
2. Generates **all permutations** of this array using `permute(...)`.
3. For each permutation:
   - Prepends and appends the starting node to form a closed tour,
   - Uses `findDistance(...)` to compute the total distance,
   - Updates the best (shortest) tour seen so far.
4. Prints:
   - the method name,
   - the shortest distance,
   - the corresponding path (with node indices +1 for human readability),
   - the time taken in seconds.
5. Calls `drawPath(...)` to visualize the tour in a window.

This is exact but becomes extremely slow as the number of nodes grows.

---

### Ant Colony Optimization (ACO)

The ACO method models a colony of artificial ants:

- Each **ant**:
  - starts from a random node,
  - iteratively chooses the next city based on a probability proportional to:
    - **pheromone level** on the edge, and
    - **inverse distance** (heuristic).
  - completes a full tour and returns to the starting city.

- After each ant finishes:
  - the total distance is used to compute a pheromone update (`delta = qValue / totalDistance`),
  - pheromone on edges used in the tour is increased.

- After each **iteration**:
  - pheromones on all edges are slightly **evaporated** (multiplied by `evaporationRate`),
  - edge weights are recomputed from pheromones and distances.

Across iterations:

- the best tour distance and path are tracked,
- at the end, the algorithm prints the best distance, path, and time.

Optionally (depending on `choice`), the program can either:

- draw the **best found path**, or  
- draw a **pheromone intensity graph** (thicker/darker edges for higher pheromone).

---

## 🖼 Visualization

The program uses the `StdDraw` library (from Princeton’s introductory Java course) to draw:

- Cities as filled circles at `(x, y)` coordinates,
- City indices as labels (`index + 1`) next to each node,
- Path edges as lines between nodes,
- In Brute Force mode, the starting city (index 0) in **orange**.

Canvas details:

- Size: 800x800,
- X scale: 0 to 1,
- Y scale: 0 to 1,
- Double buffering enabled for smoother animations.

---

## 📥 Input Format

The program reads coordinates from a text file named **`input04.txt`** by default (you can change this in `main`).

Each line must contain:

```text
x,y
```

- `x` and `y` are doubles between 0 and 1 (for visualization convenience).
- The first line corresponds to node index 0, the second line to node index 1, and so on.

Example:

```text
0.1,0.8
0.4,0.2
0.7,0.9
0.9,0.3
```

You can create multiple input files (e.g., `input01.txt`, `input02.txt`, etc.) and modify the filename in `main` to test different graphs.

---

## 🧰 Requirements

- **Java 8 or higher**
- `StdDraw` class (you can download it from: https://introcs.cs.princeton.edu/java/stdlib/StdDraw.java and place it in the same directory or your classpath)
- A text input file such as `input04.txt` with node coordinates.

---

## ▶️ How to Run

1. **Place files in the same directory**

   Ensure you have:

   - `BarisCakmak.java`
   - `Nodes.java`
   - `StdDraw.java`
   - `input04.txt` (or another input file; adjust the filename in `main` if needed)

2. **Compile**

   ```bash
   javac BarisCakmak.java Nodes.java StdDraw.java
   ```

3. **Run**

   ```bash
   java BarisCakmak
   ```

4. **Choose Method (if you add input) or adjust in code**

   Currently, the method is chosen in code via:

   ```java
   int chosenMethod = 2; // 1 = brute force, 2 = ant colony optimization
   ```

   Change this value and recompile if you want to switch between methods.

---

## 🔍 Customization Tips

- **Change input file**  
  Edit the `File file = new File("input04.txt");` line in `main` to use another file.

- **Tweak ACO parameters**  
  Inside `antColonyOptimization(...)`, you can experiment with:
  - number of ants per iteration,
  - number of iterations,
  - pheromone importance (`alpha`),
  - heuristic importance (`beta`),
  - evaporation rate,
  - `qValue`.

  These will affect convergence speed and quality of the route found.

- **Starting city color / drawing style**  
  In `drawPath(...)`, you can modify how cities and edges are drawn (colors, sizes, etc.).

---

## 📚 Learning Outcomes

This project is a good reference if you want to learn:

- how to model graphs using objects and arrays/lists,
- how brute-force search behaves on TSP,
- the basics of Ant Colony Optimization for combinatorial problems,
- how to visualize algorithms in Java using `StdDraw`.

---
