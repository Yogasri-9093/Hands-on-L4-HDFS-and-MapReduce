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


# 1) Start the Hadoop cluster
Run the Compose stack in the background.
``` bash
docker compose up -d

```

# 2) Build the code (on your host)
Compile the project with Maven; the JAR lands in target/.
```bash
mvn clean package
   resulting jar: target/wordcount-1.0-SNAPSHOT.jar
```
 
# 3) Copy the JAR into the container
Place your JAR where you’ll reference it from inside the container.
```bash
docker cp target/wordcount-1.0-SNAPSHOT.jar hadoop:/root/jar/wordcount-1.0-SNAPSHOT.jar
```
# 4) Copy the dataset into the container
Put your input text file into the container.
``` bash
docker cp data/input.txt hadoop:/root/data/input.txt
```

# 5) (Optional) Open a shell in the container
Useful if you want to run the HDFS commands interactively.
``` bash
docker exec -it hadoop /bin/bash
```
# 6) Put the input into HDFS
Create an input dir in HDFS and upload the dataset.
``` bash
docker exec -it hadoop bash -lc '
  hdfs dfs -mkdir -p /user/root/wordcount/input &&
  hdfs dfs -put -f /root/data/input.txt /user/root/wordcount/input/
```
'
# 7) Run the MapReduce job
Run your job with three args: <input> <tmp_out> <final_out>.
```bash docker exec -it hadoop bash -lc '
  hdfs dfs -rm -r -f /user/root/wordcount/tmp /user/root/wordcount/output || true &&
  hadoop jar /root/jar/wordcount-1.0-SNAPSHOT.jar org.example.WordCount \
    /user/root/wordcount/input \
    /user/root/wordcount/tmp \
    /user/root/wordcount/output
```
'
If you ever hit “Output directory already exists,” delete it and re-run:
docker exec -it hadoop bash -lc 'hdfs dfs -rm -r -f /user/root/wordcount/output'
# 8) View the output in HDFS
List the output and print the results.
```bash
docker exec -it hadoop bash -lc '
  hdfs dfs -ls  /user/root/wordcount/output &&
  hdfs dfs -cat /user/root/wordcount/output/part-*
```
'
# 9) Copy results back to your host (optional)
Fetch from HDFS to the container, then from container to your host.
```bash
docker exec -it hadoop bash -lc 'hdfs dfs -get -f /user/root/wordcount/output /root/wc_out'
docker cp hadoop:/root/wc_out ./wc_out
cat wc_out/part-*
```
# 10) Commit input & output to GitHub
Add your input dataset and the produced output to your repo.
 ensure these paths exist locally:
 - data/input.txt (your dataset)
 - wc_out/part-r-00000 (downloaded result)

```bash git add data/input.txt wc_out
git commit -m "Add WordCount input dataset and output"
git push

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
