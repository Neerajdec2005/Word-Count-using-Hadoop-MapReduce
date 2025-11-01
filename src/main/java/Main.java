import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Main {
    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 4) {
            System.err.println("Usage: Main <input> <output> <splitMaxSize> <numReducers>");
            System.exit(2);
        }

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://127.0.0.1:9000");
        conf.set("mapreduce.jobtracker.address", "localhost:54311");
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.address", "localhost:8032");

        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(Main.class);
        job.setMapperClass(Map.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.getConfiguration().set("mapreduce.input.fileinputformat.split.maxsize", args[2]);
        job.setNumReduceTasks(Integer.parseInt(args[3]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
