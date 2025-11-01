# Word Count using Hadoop MapReduce

A classic MapReduce implementation that counts word frequencies in text files using Apache Hadoop on a pseudo-distributed cluster.

## Overview

This project demonstrates the MapReduce programming model by implementing a word count application that:
- Reads text files from HDFS
- Maps each word to a key-value pair `(word, 1)`
- Reduces by summing counts for each unique word
- Outputs sorted word frequencies to HDFS

## Features

- **Custom Mapper**: Tokenizes input text and emits word-count pairs
- **Combiner**: Optimizes performance by performing local aggregation
- **Custom Reducer**: Aggregates final word counts
- **Configurable**: Supports custom split size and reducer count via command-line arguments
- **YARN Integration**: Runs on Hadoop YARN resource manager

## Prerequisites

- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **Hadoop**: 3.x (configured for pseudo-distributed mode)
- **HDFS**: Running NameNode and DataNode
- **YARN**: Running ResourceManager and NodeManager

## Project Structure

```
.
├── src/main/java/
│   ├── Main.java          # Driver program
│   ├── Map.java           # Mapper implementation
│   └── Reduce.java        # Reducer implementation
├── sample.txt             # Sample input file
├── pom.xml                # Maven configuration
├── LICENSE                # MIT License
└── README.md              # This file
```

## Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Neerajdec2005/Word-Count-using-Hadoop-MapReduce.git
cd Word-Count-using-Hadoop-MapReduce
```

### 2. Build the Project

```bash
mvn clean package -DskipTests
```

This creates `target/word_count-1.0.jar`

### 3. Configure Hadoop (Pseudo-Distributed Mode)

Ensure your Hadoop configuration files are properly set:

**`core-site.xml`**:
```xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
</configuration>
```

**`yarn-site.xml`**:
```xml
<configuration>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
  <property>
    <name>yarn.nodemanager.local-dirs</name>
    <value>/path/to/your/workspace/yarn-nm-local</value>
  </property>
  <property>
    <name>yarn.nodemanager.log-dirs</name>
    <value>/path/to/your/workspace/yarn-nm-logs</value>
  </property>
</configuration>
```

**`mapred-site.xml`**:
```xml
<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
  <property>
    <name>yarn.app.mapreduce.am.env</name>
    <value>HADOOP_MAPRED_HOME=/path/to/hadoop</value>
  </property>
  <property>
    <name>mapreduce.map.env</name>
    <value>HADOOP_MAPRED_HOME=/path/to/hadoop</value>
  </property>
  <property>
    <name>mapreduce.reduce.env</name>
    <value>HADOOP_MAPRED_HOME=/path/to/hadoop</value>
  </property>
</configuration>
```

### 4. Start Hadoop Services

```bash
# Format NameNode (only first time)
hdfs namenode -format

# Start HDFS
start-dfs.sh

# Start YARN
start-yarn.sh

# Verify services are running
jps
```

You should see:
- NameNode
- DataNode
- ResourceManager
- NodeManager
- SecondaryNameNode

## Usage

### 1. Prepare Input Data

```bash
# Create HDFS input directory
hdfs dfs -mkdir -p /user/$USER/wordcount/input

# Upload sample text file
hdfs dfs -put sample.txt /user/$USER/wordcount/input/
```

### 2. Run the MapReduce Job

```bash
yarn jar target/word_count-1.0.jar Main \
  /user/$USER/wordcount/input \
  /user/$USER/wordcount/output \
  134217728 \
  1
```

**Arguments**:
1. Input path in HDFS
2. Output path in HDFS (must not exist)
3. Split max size in bytes (134217728 = 128MB)
4. Number of reduce tasks

### 3. View Results

```bash
# List output files
hdfs dfs -ls /user/$USER/wordcount/output

# View word counts
hdfs dfs -cat /user/$USER/wordcount/output/part-r-00000
```

### 4. Clean Up (Optional)

```bash
# Remove output directory for next run
hdfs dfs -rm -r /user/$USER/wordcount/output
```

## Example Output

Input text: `"Hello World!, I am Neeraj, I specialize in ML, Big Data and SQL."`

Output:
```
Big         1
Data        1
Hello       1
I           2
ML,         1
Neeraj,     1
SQL.        1
World!,     1
am          1
and         1
in          1
specialize  1
```

## Architecture

### MapReduce Flow

1. **Input Split**: HDFS divides input into configurable-size splits
2. **Map Phase**: Each mapper tokenizes text and emits `(word, 1)` pairs
3. **Shuffle & Sort**: Framework groups values by key
4. **Combine Phase**: Local aggregation reduces network traffic
5. **Reduce Phase**: Final aggregation produces word counts
6. **Output**: Results written to HDFS

### Code Components

**`Main.java`**:
- Configures and submits the MapReduce job
- Sets mapper, combiner, reducer classes
- Defines input/output formats and paths

**`Map.java`**:
- Extends `Mapper<LongWritable, Text, Text, IntWritable>`
- Tokenizes input lines using `StringTokenizer`
- Emits `(word, 1)` for each token

**`Reduce.java`**:
- Extends `Reducer<Text, IntWritable, Text, IntWritable>`
- Sums values for each word key
- Outputs final `(word, count)` pairs

## Performance Tuning

- **Split Size**: Adjust 3rd argument to control parallelism
  - Smaller splits = more mappers = higher parallelism
  - Larger splits = fewer mappers = less overhead

- **Reducer Count**: Adjust 4th argument based on data size
  - More reducers = better parallelism but more output files

- **Combiner**: Already configured to reduce shuffle data volume

## Troubleshooting

### JAR File Not Found
```bash
# Rebuild the project
mvn clean package -DskipTests
```

### HDFS Connection Refused
```bash
# Check NameNode is running
jps | grep NameNode

# Restart HDFS if needed
stop-dfs.sh && start-dfs.sh
```

### ClassNotFoundException: MRAppMaster
- Verify `HADOOP_MAPRED_HOME` is set correctly in `mapred-site.xml`
- Ensure `mapreduce.application.classpath` includes MapReduce JARs

### Output Directory Already Exists
```bash
# Remove existing output
hdfs dfs -rm -r /user/$USER/wordcount/output
```

## Monitoring

- **YARN ResourceManager UI**: http://localhost:8088
- **HDFS NameNode UI**: http://localhost:9870
- **Job History**: http://localhost:19888

## Dependencies

Defined in `pom.xml`:
- Hadoop Common (3.3.6)
- Hadoop HDFS (3.3.6)
- Hadoop MapReduce Client Core (3.3.6)

Dependencies are marked as `provided` since they're available in the Hadoop runtime environment.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Neeraj** - [Neerajdec2005](https://github.com/Neerajdec2005)

## Acknowledgments

- Apache Hadoop documentation and community
- MapReduce programming model by Google

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please Don't forget to star it up⭐️!!