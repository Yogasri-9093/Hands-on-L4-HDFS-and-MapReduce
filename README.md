# Hadoop WordCount Project

## Project Overview
This project implements the **classic WordCount program** using **Hadoop MapReduce**.  
The goal is to count the frequency of each word in a given input text file stored in **HDFS**.  
It was developed as part of *Cloud Computing for Data Analysis (ITCS 6190/8190, Fall 2025)*.

---

## Approach and Implementation
The program uses two main components:

- **Mapper (`WordCount.java`)**  
  - Reads lines of text from the input file.  
  - Splits each line into words.  
  - Emits each word as a key with value `1`.  

- **Reducer (`SumReducer.java`)**  
  - Receives grouped keys (words) with their list of values.  
  - Sums up all the counts for each word.  
  - Emits the word and its final count.  

- **Optional Sort Step (`SortMapper.java`)**  
  - Can be used to reorder outputs by frequency or alphabetically.  

---

## Execution Steps

### 1. Build the JAR with Maven
```bash
cd ~/Downloads/wordcount
mvn clean package
The compiled JAR will be inside target/, for example:
target/wordcount-1.0-SNAPSHOT.jar
2. Start Hadoop (HDFS + YARN)
start-dfs.sh
start-yarn.sh
3. Upload Input File to HDFS
hdfs dfs -mkdir -p /user/$USER/wordcount/input
hdfs dfs -put -f data/input.txt /user/$USER/wordcount/input
4. Run the MapReduce Job
hadoop jar target/wordcount-1.0-SNAPSHOT.jar \
  org.example.WordCount \
  /user/$USER/wordcount/input \
  /user/$USER/wordcount/output
5. View Results
hdfs dfs -cat /user/$USER/wordcount/output/part-r-00000 
```
# **Challenges Faced & Solutions**

Compilation errors
**Public classes** (WordCount, SumReducer, SortMapper) were not in correctly named files.
 Solution: Renamed files to match class names (WordCount.java, SumReducer.java, etc.).
HDFS connection refused errors

**Hadoop services** (NameNode, DataNode, ResourceManager) were not running.
 Solution: Restarted cluster using start-dfs.sh and start-yarn.sh. Verified with jps.
Old output files blocking new runs

**Hadoop** throws an error if the output directory already exists.
 Solution: Always delete old output before rerunning:
hdfs dfs -rm -r -f /user/$USER/wordcount/output

**Native library warnings**
Messages about NativeCodeLoader appeared, but they are harmless.
 Solution: Ignored since job still runs correctly.

# **Input and Obtained Output**

Example Input (data/input.txt)
``` bash
hello hadoop
hadoop is powerful
big data is used for hello world
test test test example wordcount mapreduce hadoop

 
Example Output (part-r-00000)

1    world

1    used

1    powerful

1    for

1    data

1    big

2    hello

3    hadoop

1    wordcount

1    mapreduce

1    example

3    test
```
# Comments on own input 

Each line is a word followed by its count.
Words are counted across the entire input file.

**Example checks:**

hello appears 2 times → matches output.

hadoop appears 3 times → matches output.

test appears 3 times → matches output.

Other words like world, used, data, etc., each appear once → correctly shown as 1.
Overall, this output confirms that:
Mapper is splitting text into words correctly.
Reducer is summing up counts correctly.
The pipeline (HDFS input → Map → Reduce → HDFS output) is working end-to-end.

# Author
Yogasri Lella

# Course: Cloud Computing for Data Analysis (ITCS 6190/8190, Fall 2025)
# Instructor: Prof. Marco Vieira
