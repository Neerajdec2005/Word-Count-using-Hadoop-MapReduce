import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;




public class Map extends Mapper<Object,Text,Text,IntWritable>{
    private static IntWritable one=new IntWritable(1);
    private Text word=new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
        String sentence=value.toString();
        StringTokenizer it=new StringTokenizer(sentence);

        while(it.hasMoreTokens()){
            word.set(it.nextToken());
            context.write(word,one);
        }
    }
    
}
