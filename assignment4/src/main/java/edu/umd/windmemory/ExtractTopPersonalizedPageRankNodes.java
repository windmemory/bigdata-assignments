package edu.umd.windmemory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.lang.Float;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.fs.Path;

public class ExtractTopPersonalizedPageRankNodes {
  private ExtractTopPersonalizedPageRankNodes() {}

  private static final String INPUT = "input";
  private static final String TOP = "top";
  private static final String SOURCE = "sources";

  @SuppressWarnings({ "static-access" })
  public static void main(String[] args) throws IOException {
    Options options = new Options();

    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("input path").create(INPUT));
    options.addOption(OptionBuilder.withArgName("top").hasArg()
        .withDescription("top number").create(TOP));
    options.addOption(OptionBuilder.withArgName("nodes").hasArg()
        .withDescription("source nodes").create(SOURCE));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();

    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(INPUT) || !cmdline.hasOption(TOP) || !cmdline.hasOption(SOURCE)) {
      System.out.println("args: " + Arrays.toString(args));
      HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(120);
      formatter.printHelp(ExtractTopPersonalizedPageRankNodes.class.getName(), options);
      ToolRunner.printGenericCommandUsage(System.out);
      System.exit(-1);
    }

    String infile = cmdline.getOptionValue(INPUT);

    int top = Integer.parseInt(cmdline.getOptionValue(TOP));
    String sources[] = cmdline.getOptionValue(SOURCE).split(",");

    ArrayList<ArrayList<PageRankNode>> result = new ArrayList<ArrayList<PageRankNode>>();
    for (int i = 0; i < sources.length; i++) {
      result.add(new ArrayList<PageRankNode>());
    }

    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.getLocal(conf);
    for (int k = 0; k < 5; k++) {
      SequenceFile.Reader reader = new SequenceFile.Reader(conf, Reader.file(new Path(infile + "/" + "part-m-0000" + k)));

      IntWritable key = new IntWritable();
      PageRankNode val = new PageRankNode();

      while (reader.next(key, val)) {
        
        for (int i = 0; i < sources.length; i++) {
          // System.err.println(key + "\t" + val);
          PageRankNode tmp = new PageRankNode(val);
          tmp.setCurPoint(i);
          if (result.get(i).size() < top) {
            if (tmp.getPageRank() == Float.NEGATIVE_INFINITY) continue;
            result.get(i).add(tmp);
            Collections.sort(result.get(i));
          } else {
            if (tmp.getPageRank() > result.get(i).get(0).getPageRank()) {
              result.get(i).set(0, tmp);
              // System.out.println(tmp);
              Collections.sort(result.get(i));
            }
          }
           
          // System.out.println("Press Any Key To Continue...");
          //   new java.util.Scanner(System.in).nextLine();          
        }
        
      }

    }
    for (int i = 0; i < sources.length; i++) {
      System.out.println(String.format("Source: %s", sources[i]));
      for (int j = result.get(i).size() - 1; j >= 0; j--) {
        // System.out.println(result.get(j));
        System.out.println(String.format("%.5f %d", (float) StrictMath.exp(result.get(i).get(j).getPageRank()), result.get(i).get(j).getNodeId())); 
      }  
      System.out.println("");
    }
    
    
  }
}




























