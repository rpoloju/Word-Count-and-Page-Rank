package com.cise.cloud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapPhase extends Mapper<LongWritable, Text, Text, IntWritable> {

	private Text word = new Text();
	private Set localCacheFile = new HashSet();

	@Override
	// Load the cache file first - word-patterns.txt
	protected void setup(Context context) throws InterruptedException {
		try {
			Path[] localCacheFile = DistributedCache.getLocalCacheFiles(context.getConfiguration());
			for (Path aFile : localCacheFile)
				readCacheFile(aFile);
		} catch (IOException ex) {
			System.err.println("Error in reading the local cache file: " + ex.getMessage());
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();

		// Tokenize the strings based on delimiter - space
		StringTokenizer stringTokenizer = new StringTokenizer(line, " ");

		while (stringTokenizer.hasMoreTokens()) {
			String eachWord = stringTokenizer.nextToken();

			// Ignoring case and comparing the words
			if (localCacheFile.contains(eachWord.toLowerCase())) {
				word.set(eachWord);
				context.write(word, new IntWritable(1));
			}
		}

	}

	private void readCacheFile(Path filePath) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()));
		String eachLine = null;
		while ((eachLine = bufferedReader.readLine()) != null) {

			// Split words based on delimiter - " "
			String[] wordsInALine = eachLine.split(" ");
			for (String eachWord : wordsInALine) {
				localCacheFile.add(eachWord.toLowerCase());
			}

		}
	}
}