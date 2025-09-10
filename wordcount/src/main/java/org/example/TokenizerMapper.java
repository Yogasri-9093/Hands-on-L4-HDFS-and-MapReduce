package org.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TokenizerMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private static final IntWritable ONE = new IntWritable(1);
    private final Text wordOut = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws java.io.IOException, InterruptedException {
        String line = value.toString().toLowerCase();

        // split on non-letter/number (keeps apostrophes inside words like don't)
        String[] raw = line.split("[^a-z0-9']+");

        for (String token : raw) {
            if (token == null || token.isEmpty()) continue;

            // trim leading/trailing apostrophes (e.g., 'tis -> tis)
            String w = token.replaceAll("^'+|'+$", "");

            if (w.length() >= 3) {
                wordOut.set(w);
                context.write(wordOut, ONE);
            }
        }
    }
}
