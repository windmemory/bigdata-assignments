/*
 * Cloud9: A Hadoop toolkit for working with big data
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.windmemory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.io.File;
import java.net.URI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import tl.lin.data.pair.PairOfStrings;

/**
 *
 * <blockquote>Yuan Gao. <b>Scalable Language Processing Algorithms for the Masses: A Case Study in
 * Computing Word Co-occurrence Matrices with MapReduce.</b> <i>Proceedings of the 2008 Conference
 * on Empirical Methods in Natural Language Processing (EMNLP 2008)</i>, pages 419-428.</blockquote>
 *
 * @author Yuan Gao
 */
public class PMIPairsR extends Configured implements Tool {
  	private static final Logger LOG = Logger.getLogger(PMIPairsR.class);

  	private static class MyFirstMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	    private static final Text WORD = new Text();
	    private static final IntWritable NUM = new IntWritable(1);
	    // private static HashMap<String, Integer> hash = new HashMap<String, Integer>();
	    // @Override
	    // protected void setup(Context context) {
	      	
	    // }

	    @Override
	    public void map(LongWritable key, Text line, Context context)
	        throws IOException, InterruptedException {
            String text = ((Text)line).toString();
            // String[] terms = text.split("\\s+");
            StringTokenizer iter = new StringTokenizer(text);
            HashSet<String> set = new HashSet<String>();
  			// for (int i = 0; i < terms.length; i++) {
  			// 	set.add(terms[i]);
  			// }
            while (iter.hasMoreTokens()) {
		        set.add(iter.nextToken());
		    }

  			Iterator it = set.iterator();
  			while (it.hasNext()) {
  				text = (String)it.next();
  				if (text.length() < 1) continue;
  				// if (hash.containsKey(text))
  				// 	hash.put(text, 1);
  				// else 
  				// 	hash.put(text, hash.get(text) + 1);
  				WORD.set(text);
  				context.write(WORD, NUM);
  			}
		}

		// @Override
		// protected void cleanup(Context context) 
		// 	throws IOException, InterruptedException {
		// 	for (Iterator it = hash.keySet().iterator(); it.hasNext(); ) {
		// 		String key = (String) it.next();
		// 		WORD.set(key);
		// 		NUM.set(hash.get(key));
		// 		context.write(WORD, NUM);
		// 	}
		// }
  	}

  	private static class MyFirstReducer extends
      Reducer<Text, IntWritable, Text, IntWritable> {
    	private final static IntWritable SUM = new IntWritable();

    	@Override
    	public void reduce(Text key, Iterable<IntWritable> values, Context context)
        	throws IOException, InterruptedException {
      	Iterator<IntWritable> iter = values.iterator();
      	int sum = 0;
      	while (iter.hasNext()) {
        	sum += iter.next().get();
      	}

      	SUM.set(sum);
      	context.write(key, SUM);
    	}
  	}

  	private static class MySecondMapper extends Mapper<LongWritable, Text, PairOfStrings, IntWritable> {
  		private static final PairOfStrings PAIR = new PairOfStrings();
  		private static final IntWritable NUM = new IntWritable(1);
  		// private HashMap<String, Integer> hash = new HashMap<String, Integer>();

  		// @Override
  		// public void setup(Context context) {

  		// }

  		@Override
  		public void map(LongWritable key, Text line, Context context) 
  			throws IOException, InterruptedException {
  			String text = line.toString();
  			String[] rawterms = text.split("\\s+");
  			Arrays.sort(rawterms);
  			String prei = " ", prej;

  			for (int i = 0; i < rawterms.length; i++) {
  				if (prei.equals(rawterms[i]) || rawterms[i].length() == 0) continue;
  				prej = " ";
  				for (int j = i + 1; j < rawterms.length; j++) {
  					if (rawterms[i].equals(rawterms[j])) {
  						i = j;
  						continue;
  					} else if (rawterms[j].length() == 0 || prej.equals(rawterms[j])) continue;
					PAIR.set(rawterms[i], rawterms[j]);
					context.write(PAIR, NUM);
					prej = rawterms[j];
  				}
  				prei = rawterms[i];
  			}

  		}

  	// 	@Override
  	// 	protected void cleanup(Context context) 
  	// 		throws IOException, InterruptedException {
  	// 		String pair;
  	// 		String[] terms;
  	// 		for (Iterator it = hash.keySet().iterator(); it.hasNext(); ) {
			// 	pair = (String)it.next();
			// 	terms = pair.split(",");
			// 	PAIR.set(terms[0], terms[1]);
			// 	NUM.set(hash.get(pair));
			// 	context.write(PAIR, NUM);
			// }
  	// 	}
  	}

  	private static class MySecondReducer extends 
  	  Reducer<PairOfStrings, IntWritable, PairOfStrings, DoubleWritable> {
  	  	private final static PairOfStrings PAIR = new PairOfStrings();
  	  	private final static DoubleWritable RES = new DoubleWritable();
  	  	private HashMap<String, Integer> map = new HashMap<String, Integer>();
  	  	@Override
  	  	protected void setup(Context context) 
  	  		throws IOException, InterruptedException {
  	  		if (context.getCacheFiles() != null) {
	        	URI inputURI = context.getCacheFiles()[0];
	        	if (inputURI != null) {
	          		String dataStream = FileUtils.readFileToString(new File("./part-r-00000"));
	          		String[] terms = dataStream.split("\\s+");

	          		for (int i = 0; i < terms.length; i+=2) {
	            		map.put(terms[i], Integer.parseInt(terms[i + 1]));
	          		}
	        	}
	      	}
  	  		// Configuration conf = context.getConfiguration();
  	  		// Integer num = Integer.parseInt(conf.get("num"));
  	  		// String path = conf.get("path");
  	  		// BufferedReader br = null;
  	  		// for (int i = 0; i < num; i++) {
	  	  	// 	try {
	  	  	// 		br = new BufferedReader(new FileReader(path + "/part-r-0000" + i));	
	  	  	// 		String line = br.readLine();
	  	  			
	  	  	// 		Integer val;
	  	  	// 		while (line != null) {
	  	  	// 			String[] pair = line.split("\t");
	  	  	// 			val = Integer.parseInt(pair[1]);
	  	  	// 			map.put(pair[0], val);
	  	  	// 			line = br.readLine();
	  	  	// 		}
	  	  	// 	} catch (IOException e) {
	  	  	// 		e.printStackTrace();
	  	  	// 	} finally {
	  	  	// 		try {
	  	  	// 			br.close();
	  	  	// 		} catch (IOException e) {
	  	  	// 			e.printStackTrace();
	  	  	// 		}
	  	  	// 	}
  	  		// }
  	  	}

  	  	@Override
  	  	public void reduce(PairOfStrings key, Iterable<IntWritable> val, Context context) 
  	  		throws IOException, InterruptedException {
  	  		Iterator<IntWritable> iter = val.iterator();
  	  		int sum = 0;
  	  		while (iter.hasNext()) {
  	  			sum += iter.next().get();
  	  		}
  	  		if (sum >= 10) {
  	  			Double result = (double)sum / (double)map.get(key.getLeftElement()) / (double)map.get(key.getRightElement());
	  	  		result = Math.log(result) / Math.log(10);
	  	  		RES.set(result);
	  	  		context.write(key, RES);
  	  		}
  	  		
  	  	}
  	}

  	private static class MySecondCombiner extends
  	  Reducer<PairOfStrings, IntWritable, PairOfStrings, IntWritable> {
  	  	private final static PairOfStrings PAIR = new PairOfStrings();
  	  	private final static IntWritable NUM = new IntWritable();

  	  	@Override
  	  	public void reduce(PairOfStrings key, Iterable<IntWritable> val, Context context)
  	  		throws IOException, InterruptedException {
  	  		Iterator<IntWritable> iter = val.iterator();
  	  		int sum = 0;
  	  		while (iter.hasNext()) {
  	  			sum += iter.next().get();
  	  		}
  	  		NUM.set(sum);
  	  		context.write(key, NUM);
  	  	}
  	}


  	protected static class MyFirstPartitioner extends Partitioner<Text, IntWritable> {
    @Override
    	public int getPartition(Text key, IntWritable value, int numReduceTasks) {
      		return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    	}
  	}

  	protected static class MyPartitioner extends Partitioner<PairOfStrings, IntWritable> {
    @Override
    	public int getPartition(PairOfStrings key, IntWritable value, int numReduceTasks) {
      		return (key.getLeftElement().hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    	}
  	}

  	/**
   	* Creates an instance of this tool.
   	*/
	public PMIPairsR() {}

	private static final String INPUT = "input";
	private static final String OUTPUT = "output";
	private static final String WINDOW = "window";
	private static final String NUM_REDUCERS = "numReducers";

	/**
	* Runs this tool.
	*/
	@SuppressWarnings({ "static-access" })
	public int run(String[] args) throws Exception {
	Options options = new Options();

		options.addOption(OptionBuilder.withArgName("path").hasArg()
		    .withDescription("input path").create(INPUT));
		options.addOption(OptionBuilder.withArgName("path").hasArg()
		    .withDescription("output path").create(OUTPUT));
		options.addOption(OptionBuilder.withArgName("num").hasArg()
		    .withDescription("window size").create(WINDOW));
		options.addOption(OptionBuilder.withArgName("num").hasArg()
		    .withDescription("number of reducers").create(NUM_REDUCERS));

		CommandLine cmdline;
		CommandLineParser parser = new GnuParser();

		try {
		  	cmdline = parser.parse(options, args);
		} catch (ParseException exp) {
		  	System.err.println("Error parsing command line: " + exp.getMessage());
		  	return -1;
		}

		if (!cmdline.hasOption(INPUT) || !cmdline.hasOption(OUTPUT)) {
		  	System.out.println("args: " + Arrays.toString(args));
		  	HelpFormatter formatter = new HelpFormatter();
		  	formatter.setWidth(120);
		  	formatter.printHelp(this.getClass().getName(), options);
		  	ToolRunner.printGenericCommandUsage(System.out);
		  	return -1;
		}

		String inputPath = cmdline.getOptionValue(INPUT);
		String outputPath = cmdline.getOptionValue(OUTPUT);
		int reduceTasks = cmdline.hasOption(NUM_REDUCERS) ?
		Integer.parseInt(cmdline.getOptionValue(NUM_REDUCERS)) : 1;
		
		LOG.info("Tool: " + PMIPairsR.class.getSimpleName());
		LOG.info(" - input path: " + inputPath);
		LOG.info(" - output path: " + outputPath);
		LOG.info(" - number of reducers: " + reduceTasks);

		Job job = Job.getInstance(getConf());
		job.setJobName(PMIPairsR.class.getSimpleName());
		job.setJarByClass(PMIPairsR.class);
		// Delete the output directory if it exists already.
		Path interDir = new Path("temp");
		FileSystem.get(getConf()).delete(interDir, true);

		// job.setNumMapTasks(reduceTasks);
		job.setNumReduceTasks(1);

		FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, interDir);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(MyFirstMapper.class);
		// job.setCombinerClass(MyFirstReducer.class);
		job.setReducerClass(MyFirstReducer.class);
		job.setPartitionerClass(MyFirstPartitioner.class);


		Job job2 = Job.getInstance(getConf());
		job2.setJobName(PMIPairsR.class.getSimpleName());
		job2.setJarByClass(PMIPairsR.class);
		// Delete the output directory if it exists already.
		Path outputDir = new Path(outputPath);
		FileSystem.get(getConf()).delete(outputDir, true);

		// job2.getConfiguration().set("path", "temp");
		// job2.getConfiguration().setInt("num", reduceTasks);


		job2.setNumReduceTasks(reduceTasks);

		FileInputFormat.setInputPaths(job2, new Path(inputPath));
		FileOutputFormat.setOutputPath(job2, new Path(outputPath));

		job2.setMapOutputKeyClass(PairOfStrings.class);
		job2.setMapOutputValueClass(IntWritable.class);
		job2.setOutputKeyClass(PairOfStrings.class);
		job2.setOutputValueClass(DoubleWritable.class);

		job2.setMapperClass(MySecondMapper.class);
		// job2.setCombinerClass(MySecondCombiner.class);
		job2.setReducerClass(MySecondReducer.class);
		job2.setPartitionerClass(MyPartitioner.class);

		long startTime = System.currentTimeMillis();
		job2.addCacheFile(new URI("temp/part-r-00000"));
		job.waitForCompletion(true);
		job2.waitForCompletion(true);
		// FileSystem.get(getConf()).delete(interDir, true);
		System.out.println("Job Finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");

		return 0;
	}

	/**
	* Dispatches command-line arguments to the tool via the {@code ToolRunner}.
	*/
	public static void main(String[] args) throws Exception {
		ToolRunner.run(new PMIPairsR(), args);
	}
}
