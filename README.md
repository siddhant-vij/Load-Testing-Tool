# Multithreading Load Testing Tool

This project consists of two main components:
1. A simple Java-based server that counts the frequency of words in the text file "war_and_peace.txt".
2. A load testing tool that sends GET requests to this server to retrieve the frequency of words listed in "search_words.csv".

The goal is to measure and compare the latency and throughput of the server under different loads by varying the number of threads in the load testing tool.

## Table of Contents

1. [Server Implementation](#server-implementation)
1. [Load Testing Tool](#load-testing-tool)
1. [Running the Tests](#running-the-tests)
1. [Output Format](#output-format)
1. [Notes](#notes)
1. [Contributing](#contributing)
1. [License](#license)

## Server Implementation
- The server is implemented using Java's Networking API (no framework is used).
- It listens for HTTP GET requests and processes them to return the frequency of a specified word in `resources/war_and_peace.txt`.

## Load Testing Tool

- The tool reads words from `resources/search_words.csv` and generates HTTP GET requests for each word.
- It supports running multiple threads to simulate concurrent requests.
- The tool measures the latency (time taken for each request) and throughput (total requests completed in a time frame) for each run.

## Running the Tests
- Start the server application.
- Run the load testing tool with a specified number of threads (from 1 to 16).
- The tool will display the average latency and throughput for each run in the terminal.

## Output Format
Results will be displayed in a formatted table in the terminal, showing latency and throughput against the number of threads.

## Notes
- Ensure that the server is running before starting the load testing tool.
- The server and load testing tool are designed for educational purposes to demonstrate the effects of multithreading on performance.

## Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.
1. **Fork the Project**
2. **Create your Feature Branch**: 
    ```bash
    git checkout -b feature/AmazingFeature
    ```
3. **Commit your Changes**: 
    ```bash
    git commit -m 'Add some AmazingFeature'
    ```
4. **Push to the Branch**: 
    ```bash
    git push origin feature/AmazingFeature
    ```
5. **Open a Pull Request**

## License
Distributed under the MIT License. See [`LICENSE`](https://github.com/siddhant-vij/Load-Testing-Tool/blob/main/LICENSE) for more information.