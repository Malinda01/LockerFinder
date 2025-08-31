# 🎢 Amusement Park SmartLocker Finder

A Java Swing application that helps guests in an amusement park find the **nearest available locker station** using **graph-based pathfinding (Dijkstra’s algorithm)**.
The system provides a **visual map**, **locker status table**, and a **log view**, making it easy for guests and administrators to manage lockers efficiently.

---

## 🚀 Features

* **Interactive Park Map**

  * Nodes represent locations (entrances, junctions, attractions, locker stations).
  * Edges represent walkable paths with real distances (calculated via coordinates).
  * Visual highlights show the shortest path to a locker.

* **Smart Locker Management**

  * Assigns the **nearest available locker** to a guest automatically.
  * Supports **locker release** and **waiting queue management** when lockers are full.
  * Displays **real-time locker status** in a table (Available / Occupied, Assigned To).

* **Pathfinding with Dijkstra’s Algorithm**

  * Efficiently finds the **shortest path** from a guest’s location to the nearest free locker.
  * Displays both the path and total walking distance.

* **User-Friendly GUI**

  * Built with **Java Swing**.
  * Split-pane design: **Map view** (left) and **Locker/Control Panel** (right).
  * Includes preview, request, release, and reset actions.
  * Log panel records all system actions.

---

## 🖥️ Screenshot

<img width="1693" height="872" alt="image" src="https://github.com/user-attachments/assets/a59931c0-1b98-4b04-8fa8-72d447a12ebc" />

---

## 🏗️ Project Structure

```
Main.java                 # Main entry point
Graph.java                # Graph representation + Dijkstra
PathResult.java           # Stores shortest path results
LockerSystem.java         # Locker allocation, release, waiting queue
GraphPanel.java           # Map visualization
LockerTableModel.java     # Locker table data model
MainFrame.java            # Main GUI window
SimpleQueue.java          # Handles the queue data structure
```

---

## ⚙️ How It Works

1. **Graph Creation**

   * Nodes = park locations & locker stations
   * Edges = paths between nodes (distances calculated using Euclidean distance)

2. **Guest Locker Request Flow**

   * Guest enters **name** + **current location**
   * System finds **nearest available locker** using Dijkstra’s algorithm
   * Locker is **assigned** and the **shortest path** is shown on the map

3. **Locker Release**

   * Select a locker from the table → Release
   * If guests are waiting, the system **automatically reassigns**

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 8+)
* **GUI:** Swing (JFrame, JPanel, JTable, etc.)
* **Algorithms:** Dijkstra’s Shortest Path
* **Data Structures:** Graph adjacency matrix, Queue for waiting list

---

## ▶️ How to Run

1. Clone the repo:

   ```bash
   git clone https://github.com/Malinda01/LockerFinder.git
   cd LockerFinder
   ```

2. Compile the project:

   ```bash
   javac Main.java
   ```

3. Run the application:

   ```bash
   java LockerFinder
   ```

---

## 📌 Demo Data

* **Locations:** Entrances, junctions, attractions, paths
* **Lockers:**

  * Locker Station A (Hub North)
  * Locker Station B (Hub South)
  * Locker Station C (Adventure)
  * Locker Station D (Fantasy)

---

## 🔮 Possible Extensions

* Add **multiple floors or zones** with weighted distances.
* Support **mobile app frontend** (React/Flutter).
* Integrate **database backend** for persistent locker states.
* Add **analytics dashboard** for locker usage trends.

---



