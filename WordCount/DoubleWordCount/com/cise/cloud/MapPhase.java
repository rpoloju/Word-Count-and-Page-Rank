package com.cise.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapPhase extends Mapper<LongWritable, Text, Text, IntWritable> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String inputPhrase = value.toString();

		List<List<String>> twowords = new ArrayList<>();

		// Split the text into lines delimited by "."
		String[] lines = inputPhrase.split("\\.");
		int numberOfLines = lines.length;

		// Let the line index start from 0
		for (int lineNumber = 0; lineNumber < numberOfLines; lineNumber++) {

			// Remove leading or trailing spaces from each line.
			String trimmedLine = new String(lines[lineNumber].trim());

			// Split each line into words and then combine successive pairs of words
			String[] wordsInEachLine = trimmedLine.split(" ");
			List<String> wordList = new ArrayList<>();

			// Iterate from first word and create strings by concatenating each word with
			// its next word
			for (int i = 0; i < wordsInEachLine.length - 1; i++) {
				wordList.add(wordsInEachLine[i] + " " + wordsInEachLine[i + 1]);
			}
			twowords.add(wordList);

		}

		// Now convert the list of list of strings to list of strings
		List<String> twins = new ArrayList<String>();

		for (int i = 0; i < twowords.size(); i++)
			for (int j = 0; j < twowords.get(i).size(); j++)
				twins.add(twowords.get(i).get(j));

		// Trim each word for leading or trailing spaces and add it to map
		for (String eachWord : twins) {
			Text outputKey = new Text(eachWord.trim());
			IntWritable outputValue = new IntWritable(1);
			context.write(outputKey, outputValue);
		}
	}

}