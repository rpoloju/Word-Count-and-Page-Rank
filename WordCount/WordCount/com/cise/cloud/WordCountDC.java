package com.cise.cloud;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountDC extends Configured {

	/*
	 * public static void main(String[] args) throws Exception { int exitCode =
	 * ToolRunner.run(new WordCountDC(), args); System.exit(exitCode); }
	 */

	public static void main(String[] args) throws Exception {

		// Initialize the Hadoop job and set the jar as well as the name of the Job
		Job wordcountjob = new Job();
		wordcountjob.setJarByClass(WordCountDC.class);

		// Add input and output file paths to job based on the arguments passed
		FileInputFormat.addInputPath(wordcountjob, new Path(args[0]));
		FileOutputFormat.setOutputPath(wordcountjob, new Path(args[1]));

		wordcountjob.setOutputKeyClass(Text.class);
		wordcountjob.setOutputValueClass(IntWritable.class);
		wordcountjob.setOutputFormatClass(TextOutputFormat.class);

		// Set the MapClass and ReduceClass in the job
		wordcountjob.setMapperClass(MapPhase.class);
		wordcountjob.setReducerClass(ReducePhase.class);

		DistributedCache.addCacheFile(new Path(args[2]).toUri(), wordcountjob.getConfiguration());

		System.exit(wordcountjob.waitForCompletion(true) ? 0 : 1);

	}
}