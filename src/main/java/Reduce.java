
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Reduce extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable cnt= new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
        int sum=0;
        for(IntWritable v:values){
            sum+=v.get();
        }
        cnt.set(sum);
        context.write(key,cnt);
    }

}