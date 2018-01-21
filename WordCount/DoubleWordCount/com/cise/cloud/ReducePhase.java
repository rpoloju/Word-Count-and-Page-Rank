package com.cise.cloud;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducePhase extends Reducer<Text, IntWritable, Text, IntWritable> {

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		
		int numberOfOccurancesOfEachWord = 0;
		for (IntWritable eachVal : values) {
			numberOfOccurancesOfEachWord += eachVal.get();
		}
		
		//Write the number of occurrences of each word to the output file
		context.write(key, new IntWritable(numberOfOccurancesOfEachWord));
	}

}
