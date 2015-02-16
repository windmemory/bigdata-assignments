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
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import tl.lin.data.map.HMapStIW;
import tl.lin.data.pair.PairOfStrings;

/**
 *
 * <blockquote>Yuan Gao. <b>Scalable Language Processing Algorithms for the Masses: A Case Study in
 * Computing Word Co-occurrence Matrices with MapReduce.</b> <i>Proceedings of the 2008 Conference
 * on Empirical Methods in Natural Language Processing (EMNLP 2008)</i>, pages 419-428.</blockquote>
 *
 * @author Yuan Gao
 */
public class PMIStripes extends Configured implements Tool {
  private static final Logger LOG = Logger.getLogger(PMIStripes.class);

  private static class MyFirstMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
      private static final Text WORD = new Text();
      private static final IntWritable NUM = new IntWritable(1);

      @Override
      public void map(LongWritable key, Text line, Context context)
          throws IOException, InterruptedException {
            String text = ((Text)line).toString();
            
            StringTokenizer iter = new StringTokenizer(text);
            HashSet<String> set = new HashSet<String>();
        
            while (iter.hasMoreTokens()) {
            set.add(iter.nextToken());
        }

        Iterator it = set.iterator();
        while (it.hasNext()) {
          text = (String)it.next();
          if (text.length() < 1) continue;
          
          WORD.set(text);
          context.write(WORD, NUM);
        }
      } 
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



  private static class MySecondMapper extends Mapper<LongWritable, Text, Text, HMapStIW> {
    private static final HMapStIW MAP = new HMapStIW();
    private static final Text KEY = new Text();

    @Override
    public void map(LongWritable key, Text line, Context context)
        throws IOException, InterruptedException {
        String text = line.toString();
        String[] rawterms = text.split("\\s+");
        ArrayList<String> terms = new ArrayList<String>();
        HashSet<String> set = new HashSet<String>();
        String pair;
        for (int i = 0; i < rawterms.length; i++) {
          if (rawterms[i].length() < 1) continue;
          set.add(rawterms[i]);
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
          terms.add((String)it.next());
        }

        for (int i = 0; i < terms.size(); i++) {
          String term = terms.get(i);

          MAP.clear();

          for (int j = 0; j < terms.size(); j++) {
            if (j == i)
              continue;
            MAP.increment(terms.get(j));
          }

          KEY.set(term);
          context.write(KEY, MAP);
        }
    }
  }

  private static class MySecondCombiner extends Reducer<Text, HMapStIW, Text, HMapStIW> {
    @Override
    public void reduce(Text key, Iterable<HMapStIW> values, Context context)
        throws IOException, InterruptedException {
      Iterator<HMapStIW> iter = values.iterator();
      HMapStIW map = new HMapStIW();

      while (iter.hasNext()) {
        map.plus(iter.next());
      }

      context.write(key, map);
    }
  }

  private static class MySecondReducer extends Reducer<Text, HMapStIW, PairOfStrings, DoubleWritable> {
    private final static PairOfStrings PAIR = new PairOfStrings();
    private final static DoubleWritable RES = new DoubleWritable();
    private HashMap<String, Integer> dict = new HashMap<String, Integer>();
    
    @Override
    protected void setup(Context context) {
      Configuration conf = context.getConfiguration();
      Integer num = Integer.parseInt(conf.get("num"));
      String path = conf.get("path");
      BufferedReader br = null;
      for (int i = 0; i < num; i++) {
        try {
          br = new BufferedReader(new FileReader(path + "/part-r-0000" + i)); 
          String line = br.readLine();
          
          Integer val;
          while (line != null) {
            String[] pair = line.split("\t");
            val = Integer.parseInt(pair[1]);
            dict.put(pair[0], val);
            line = br.readLine();
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          try {
            br.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    @Override
    public void reduce(Text key, Iterable<HMapStIW> values, Context context)
        throws IOException, InterruptedException {
      Iterator<HMapStIW> iter = values.iterator();
      HMapStIW map = new HMapStIW();

      while (iter.hasNext()) {
        map.plus(iter.next());
      }
      Iterator iter2 = map.keySet().iterator();
      while (iter2.hasNext()) {
        String key2 = (String)iter2.next();
        int combineVal = map.get(key2);
        if (combineVal < 10) continue;
        Double result = (double)combineVal / (double)dict.get(key.toString()) / (double)dict.get(key2);
        result = Math.log(result) / Math.log(10);
        PAIR.set(key.toString(), key2);
        RES.set(result);
        context.write(PAIR, RES);
      }
      
    }
  }


  protected static class MyFirstPartitioner extends Partitioner<Text, IntWritable> {
    @Override
      public int getPartition(Text key, IntWritable value, int numReduceTasks) {
          return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
      }
    }

  protected static class MyPartitioner extends Partitioner<Text, HMapStIW> {
  @Override
    public int getPartition(Text key, HMapStIW value, int numReduceTasks) {
        return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    }
  }
  /**
   * Creates an instance of this tool.
   */
  public PMIStripes() {}

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

    LOG.info("Tool: " + PMIPairs.class.getSimpleName());
    LOG.info(" - input path: " + inputPath);
    LOG.info(" - output path: " + outputPath);
    LOG.info(" - number of reducers: " + reduceTasks);

    Job job = Job.getInstance(getConf());
    job.setJobName(PMIPairs.class.getSimpleName());
    job.setJarByClass(PMIPairs.class);
    // Delete the output directory if it exists already.
    Path interDir = new Path("temp");
    FileSystem.get(getConf()).delete(interDir, true);

    // job.setNumMapTasks(reduceTasks);
    job.setNumReduceTasks(reduceTasks);

    FileInputFormat.setInputPaths(job, new Path(inputPath));
    FileOutputFormat.setOutputPath(job, interDir);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    job.setMapperClass(MyFirstMapper.class);
    job.setCombinerClass(MyFirstReducer.class);
    job.setReducerClass(MyFirstReducer.class);
    job.setPartitionerClass(MyFirstPartitioner.class);

    Job job2 = Job.getInstance(getConf());
    job2.setJobName(PMIPairs.class.getSimpleName());
    job2.setJarByClass(PMIPairs.class);
    // Delete the output directory if it exists already.
    Path outputDir = new Path(outputPath);
    FileSystem.get(getConf()).delete(outputDir, true);

    job2.getConfiguration().set("path", interDir.toString());
    job2.getConfiguration().setInt("num", reduceTasks);


    job2.setNumReduceTasks(reduceTasks);

    FileInputFormat.setInputPaths(job2, new Path(inputPath));
    FileOutputFormat.setOutputPath(job2, new Path(outputPath));

    job2.setMapOutputKeyClass(Text.class);
    job2.setMapOutputValueClass(HMapStIW.class);
    job2.setOutputKeyClass(PairOfStrings.class);
    job2.setOutputValueClass(DoubleWritable.class);

    job2.setMapperClass(MySecondMapper.class);
    job2.setCombinerClass(MySecondCombiner.class);
    job2.setReducerClass(MySecondReducer.class);
    job2.setPartitionerClass(MyPartitioner.class);
    
    long startTime = System.currentTimeMillis();
    job.waitForCompletion(true);
    job2.addCacheFile(interDir.toUri());
    job2.waitForCompletion(true);
    // FileSystem.get(getConf()).delete(interDir, true);
    System.out.println("Job Finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");

    return 0;
  }

  /**
   * Dispatches command-line arguments to the tool via the {@code ToolRunner}.
   */
  public static void main(String[] args) throws Exception {
    ToolRunner.run(new PMIStripes(), args);
  }
}
