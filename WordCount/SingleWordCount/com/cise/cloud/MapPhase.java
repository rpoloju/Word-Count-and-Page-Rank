package com.cise.cloud;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapPhase extends Mapper<LongWritable, Text, Text, IntWritable> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String inputString = value.toString();

		// Split the input String into individual words with space as delimiter
		String[] wordsTokenized = inputString.split(" ");

		for (String eachWord : wordsTokenized) {
			Text outputKey = new Text(eachWord.trim());
			IntWritable outputValue = new IntWritable(1);
			context.write(outputKey, outputValue);
		}
	}

}