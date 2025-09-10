package org.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparator;

public class IntDescendingComparator extends WritableComparator {
    public IntDescendingComparator() {
        super(IntWritable.class, true);
    }
    @Override
    public int compare(Object a, Object b) {
        IntWritable x = (IntWritable) a;
        IntWritable y = (IntWritable) b;
        return -1 * x.compareTo(y); // descending
    }
}
