package com.cs.clemson.cloaked.batman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class AirportGraph {
    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
        private HashMap<String, Airport> airports = new HashMap<String, Airport>();
        private int month;
        private int year;
        private int day;
        private String originAirport, destAirport;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            File airfile = new File("airports.csv");
            BufferedReader buf = new BufferedReader(new FileReader(airfile));
            String line="";
            Airport airport;
            String data[];
            int line_num =0;
            String iata;
            while((line=buf.readLine())!=null){
                if(line_num!=0){ //skip first line
                    data = line.split(",");
                    airport = new Airport();
                    iata = data[0].replace("\"", "").trim();
                    airport.setIata(iata);
                    airports.put(iata,airport);
                }
                line_num++;
            }
            
        }
        
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if(!line.contains("Year")){ //skip the first line
                String data[] = line.split(",");
            
                month = Integer.parseInt(data[1].trim());
                year = Integer.parseInt(data[0].trim());
                day = Integer.parseInt(data[2].trim());
                if(month==1 && year==2008){
                    originAirport = data[16].trim();
                    destAirport = data[17].trim();
                    if(airports.containsKey(originAirport)){
                        Airport a = airports.get(originAirport);
                        a.setIncomingCount(a.getIncomingCount() +1);
                        airports.put(originAirport,a );
                    }
                    if(airports.containsKey(destAirport)){
                        Airport a = airports.get(destAirport);
                        a.setOutgoingCount(a.getOutgoingCount() +1);
                        airports.put(destAirport,a );
                    }
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            for(String key: airports.keySet()){
                if(airports.get(key).getIncomingCount()>0 || airports.get(key).getOutgoingCount() >0 ){
                    //System.out.println(key +" : Incoming: " +airports.get(key).getIncomingCount()
                   // +" Outgoing count "+airports.get(key).getOutgoingCount());
                    context.write(new Text(key),new Text(airports.get(key).getIncomingCount()+":"+
                            airports.get(key).getOutgoingCount()));
                }
            }
        }
        
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            
        }
    }

    public static void main(String[] args) {
        try {
            //validate number arguments
            if (args.length != 2) {
                System.out.println("You need to pass two arguments:\n 1. Input "
                        + "directory containing input files followed by:\n 2. output directory");
            }
            //Delete the output folder if it already exist
            File output_dir = new File(args[1]);
            if (output_dir.exists() && output_dir.isDirectory()) {
                for (File f : output_dir.listFiles()) {
                    f.delete();
                }
                output_dir.delete();
            }

            Configuration conf = new Configuration();

            Job job = new Job(conf, "AirportGraph");
            job.setJarByClass(AirportGraph.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            job.setMapperClass(Map.class);
            job.setReducerClass(Reduce.class);

            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);

            FileInputFormat.addInputPath(job, new Path(args[0]));

            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            job.waitForCompletion(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AirportGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AirportGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AirportGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
