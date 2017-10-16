package com.cise.cloud;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Sum implements Function2<Double, Double, Double> {

	public Double call(Double a, Double b) {
		return a + b;
	}
}

public class PageRank {
	public static void main(String[] args) {
		String inputFile = args[0];
		final Pattern pattern = Pattern.compile("(?<=<target>)(.*?)(?=</target>)");
		final Pattern pattern1 = Pattern.compile("\\t+");

		SparkConf conf = new SparkConf().setAppName("find_links");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> input = sc.textFile(inputFile);
		System.out.println("hello this is count " + input.count());

		JavaPairRDD<String, List<String>> links1 = input.mapToPair(new PairFunction<String, String, List<String>>() {

			public Tuple2<String, List<String>> call(String s) throws Exception {
				// TODO Auto-generated method stub
				String[] components = s.split("\\t");
				System.out.println("this is component 0 " + components[0]);
				Matcher matcher = pattern.matcher(s);
				ArrayList<String> link = new ArrayList<String>();
				while (matcher.find()) {

					link.add(matcher.group());
				}
				if (link.size() != 0)
					return new Tuple2<String, List<String>>(components[1], link);
				else
					return null;
			}

		}).cache();

		JavaPairRDD<String, List<String>> links = links1.filter(new Function<Tuple2<String, List<String>>, Boolean>() {

			public Boolean call(Tuple2<String, List<String>> t) throws Exception {
				return t != null;
			}

		}).cache();

		JavaPairRDD<String, Double> ranks = links.mapValues(new Function<List<String>, Double>() {

			public Double call(List<String> rs) {

				for (String i : rs)
					System.out.println("--------------------rs----------------------" + i);

				return 1.0;
			}
		});

		// Calculates and updates URL ranks continuously using PageRank algorithm.
		for (int current = 0; current < Integer.parseInt("10"); current++) {
			// Calculates URL contributions to the rank of other URLs.
			JavaPairRDD<String, Double> contribs = links.join(ranks).values()
					.flatMapToPair(new PairFlatMapFunction<Tuple2<List<String>, Double>, String, Double>() {

						public List<Tuple2<String, Double>> call(Tuple2<List<String>, Double> s) {
							int urlCount = s._1.size(); // Iterables.size(s._1);
							List<Tuple2<String, Double>> results = new ArrayList<Tuple2<String, Double>>();
							for (String n : s._1) {
								results.add(new Tuple2<String, Double>(n, s._2() / urlCount));
							}
							return results;
						}
					});

			// Re-calculates URL ranks based on neighbor contributions.
			ranks = contribs.reduceByKey(new Sum()).mapValues(new Function<Double, Double>() {

				public Double call(Double sum) {
					return 0.15 + sum * 0.85;
				}
			});
		}
		System.out.println("Computed weights, to be sorted");

		JavaPairRDD<String, Double> idRankName = ranks;

		JavaPairRDD<Double, String> swappedPair = ranks
				.mapToPair(new PairFunction<Tuple2<String, Double>, Double, String>() {
					public Tuple2<Double, String> call(Tuple2<String, Double> item) throws Exception {
						return item.swap();
					}
				}).sortByKey(false);

		List<Tuple2<Double, String>> output = swappedPair.collect();

		System.out.println("size:" + output.size());
		int i = 0;
		for (Tuple2<?, ?> tuple : output) {
			System.out.println(tuple._2() + " has rank -> " + tuple._1() + ".");
			i++;
			if (i > 100)
				break;
		}

		sc.stop();
	}
}