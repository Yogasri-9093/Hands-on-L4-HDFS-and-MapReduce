package org.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private final IntWritable out = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws java.io.IOException, InterruptedException {
        int sum = 0;
        for (IntWritable v : values) sum += v.get();
        out.set(sum);
        context.write(key, out);
    }
}
