package com.cise.cloud;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WordCount {

	public static void main(String[] args) throws Exception {

		// Create a job to perform word counting task
		Job wordcountjob = Job.getInstance(new Configuration());

		wordcountjob.setJarByClass(WordCount.class);

		wordcountjob.setOutputKeyClass(Text.class);
		wordcountjob.setOutputValueClass(IntWritable.class);

		wordcountjob.setMapperClass(MapPhase.class);
		wordcountjob.setReducerClass(ReducePhase.class);

		wordcountjob.setInputFormatClass(TextInputFormat.class);
		wordcountjob.setOutputFormatClass(TextOutputFormat.class);

		// The first argument is input path and the second argument is output path
		FileInputFormat.setInputPaths(wordcountjob, new Path(args[0]));
		FileOutputFormat.setOutputPath(wordcountjob, new Path(args[1]));

		System.exit(wordcountjob.waitForCompletion(true) ? 0 : 1);
	}

}
