# 📊 Interactive Sorting Visualizer

An interactive Sorting Algorithm Visualizer built using Java, Spring Boot, HTML, CSS, and JavaScript. This project demonstrates how different sorting algorithms work through real-time animations and visual feedback.

## 🚀 Features

- Visualize sorting algorithms step-by-step
- Generate random arrays dynamically
- Adjustable array size and animation speed
- Real-time comparison and swap counters
- Execution time tracking
- Modern responsive UI
- Stop sorting anytime during execution

## 📚 Supported Algorithms

- Bubble Sort
- Selection Sort
- Insertion Sort
- Merge Sort
- Quick Sort

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript (ES6)

## 🏗️ Architecture

1. Frontend sends array data and selected algorithm to the Spring Boot backend.
2. Backend records each comparison and swap as sorting steps.
3. Steps are returned as JSON.
4. Frontend animates the sorting process in real time.

## 📂 Project Structure

```text
Visualizer
├── src
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 🔌 API Endpoints

### Get Supported Algorithms

```http
GET /api/algorithms
```

### Start Sorting

```http
POST /api/sort
```

## ▶️ Run Locally

Clone the repository:

```bash
git clone https://github.com/Pawan1Mali/Visualizer.git
```

Navigate to project:

```bash
cd Visualizer
```

Run application:

### Linux / macOS

```bash
./mvnw spring-boot:run
```

### Windows

```cmd
mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8080
```

## 🔮 Future Enhancements

- Heap Sort
- Radix Sort
- Shell Sort
- Dark/Light Theme Toggle
- Sound Effects

## 👨‍💻 Author

Pawan Mali
