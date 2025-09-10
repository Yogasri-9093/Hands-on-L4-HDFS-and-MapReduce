package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WordCount {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: hadoop jar wordcount.jar org.example.WordCount <input> <tmp_out> <final_out>");
            System.exit(1);
        }
        Path in = new Path(args[0]);
        Path tmp = new Path(args[1]);      // Job 1 output (word\tcount)
        Path out = new Path(args[2]);      // Job 2 output (count\tword sorted desc)

        // ---------- Job 1: word -> count ----------
        Configuration conf1 = new Configuration();
        Job job1 = Job.getInstance(conf1, "wordcount-count");
        job1.setJarByClass(WordCount.class);

        job1.setMapperClass(TokenizerMapper.class);
        job1.setCombinerClass(SumReducer.class);
        job1.setReducerClass(SumReducer.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);

        TextInputFormat.addInputPath(job1, in);
        TextOutputFormat.setOutputPath(job1, tmp);

        if (!job1.waitForCompletion(true)) System.exit(2);

        // ---------- Job 2: sort by count desc ----------
        Configuration conf2 = new Configuration();
        Job job2 = Job.getInstance(conf2, "wordcount-sort");
        job2.setJarByClass(WordCount.class);

        job2.setMapperClass(SortMapper.class);
        job2.setMapOutputKeyClass(IntWritable.class);
        job2.setMapOutputValueClass(Text.class);

        // identity reducer OK; Hadoop will sort keys before reduce
        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(Text.class);

        job2.setSortComparatorClass(IntDescendingComparator.class);

        TextInputFormat.addInputPath(job2, tmp);
        TextOutputFormat.setOutputPath(job2, out);

        System.exit(job2.waitForCompletion(true) ? 0 : 3);
    }
}
