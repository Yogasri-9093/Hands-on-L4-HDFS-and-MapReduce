package org.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SortMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
    private final IntWritable countKey = new IntWritable();
    private final Text wordVal = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws java.io.IOException, InterruptedException {
        // input lines: "word\tcount"
        String line = value.toString();
        int tab = line.lastIndexOf('\t');
        if (tab <= 0) return;

        String word = line.substring(0, tab);
        String countStr = line.substring(tab + 1).trim();
        try {
            int c = Integer.parseInt(countStr);
            countKey.set(c);
            wordVal.set(word);
            context.write(countKey, wordVal);
        } catch (NumberFormatException ignored) {}
    }
}
