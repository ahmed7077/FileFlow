# FileFlow

## Overview

**FileFlow** is a Java-based file organization system that demonstrates the practical application of Data Structures, Algorithms, and hashing techniques to efficiently organize, categorize, and manage files. The project is designed with a modular architecture and focuses on optimizing file management operations through efficient data structures and algorithmic approaches.

It serves as a practical implementation of core computer science concepts in a real-world file management application.

---

## Features

- Intelligent file organization based on customizable categorization rules
- Automatic file sorting using multiple file attributes
- Recursive directory traversal for processing nested folders
- Duplicate file detection using **SHA-256 hashing**
- Efficient file lookup and organization using appropriate data structures
- Modular architecture for easy maintenance and future scalability
- Clean and extensible codebase following Object-Oriented Programming principles

---

## Technologies & Algorithms

### Programming Language
- Java

### Build Tool
- Maven

### Core Concepts
- Object-Oriented Programming (OOP)
- Data Structures and Algorithms
- File I/O
- Recursive directory traversal
- Exception handling

### Data Structures
- Arrays
- Lists
- Queues
- Hash Maps
- Trees

### Algorithms & Techniques
- SHA-256 hashing for duplicate file detection
- File sorting algorithms
- Recursive file system traversal
- Efficient searching and categorization logic

---

## Project Structure

```text
FileFlow/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── dsafileorganiser/
│                   ├── ...
│                   └── DSAFileorganiserApplication.java
├── pom.xml
├── README.md
└── .gitignore
```

---

## Getting Started

### Prerequisites

- Java JDK 17 or later
- Apache Maven
- IntelliJ IDEA, Eclipse, or Visual Studio Code

### Installation

Clone the repository:

```bash
git clone https://github.com/<your-username>/FileFlow.git
```

Navigate to the project directory:

```bash
cd FileFlow
```

Build the project:

```bash
mvn clean install
```

Run the application from your IDE or execute:

```text
DSAFileorganiserApplication.java
```

---

## System Workflow

```text
Input Directory
       │
       ▼
Recursive Directory Scanner
       │
       ▼
File Metadata Extraction
       │
       ▼
SHA-256 Hash Generation
       │
       ▼
Duplicate Detection
       │
       ▼
Sorting & Categorization
       │
       ▼
Organized Output Directory
```

---

## Future Enhancements

- Graphical User Interface (GUI) using JavaFX
- Database integration for metadata storage
- Advanced search and filtering
- Duplicate file removal options
- Rule-based automatic file organization
- File compression support
- Cross-platform desktop packaging

---

## Author

**Muhammad Ahmed**
