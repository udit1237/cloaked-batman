package com.cs.clemson.cloaked.batman;

//import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Dimension;
import javax.swing.JFrame;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
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
import org.apache.commons.collections15.Transformer;

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
            String line = "";
            Airport airport;
            String data[];
            int line_num = 0;
            String iata;
            while ((line = buf.readLine()) != null) {
                if (line_num != 0) { //skip first line
                    data = line.split(",");
                    airport = new Airport();
                    iata = data[0].replace("\"", "").trim();
                    airport.setIata(iata);
                    airports.put(iata, airport);
                }
                line_num++;
            }

        }

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (!line.contains("Year")) { //skip the first line
                String data[] = line.split(",");

                month = Integer.parseInt(data[1].trim());
                year = Integer.parseInt(data[0].trim());
                day = Integer.parseInt(data[2].trim());
                if (month == 1 && year == 2008) {
                    originAirport = data[16].trim();
                    destAirport = data[17].trim();
                    if (airports.containsKey(originAirport)) {
                        Airport a = airports.get(originAirport);
                        a.addFlight(destAirport);
                        airports.put(originAirport, a);
                    }
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            HashMap<String, Integer> flights;
            for (String key : airports.keySet()) {
                flights = airports.get(key).getFlights();
                for (String k : flights.keySet()) {
                    //System.out.println("From: "+key+" To: "+k +" Count: "+flights.get(k));
                    context.write(new Text(key), new Text(k + "::" + flights.get(k)));
                }
            }
        }

    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

        Graph<AirportNode, AirportLink> g = new DirectedSparseGraph<AirportNode, AirportLink>();
        String vertex;
        int nodes = 0;
        private int edgeCount =0;
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            AirportNode fromNode = new AirportNode(key.toString());
            if (!g.containsVertex(fromNode)) {
                g.addVertex(fromNode);
                nodes++;
            }

            for (Text val : values) {
                System.out.println(key+" -- "+ val);
                String data[] = val.toString().split("::");
                AirportNode toNode = new AirportNode(data[0]);
                if (!g.containsVertex(toNode)) {
                    g.addVertex(toNode);
                    nodes++;
                }

                g.addEdge(new AirportLink(Integer.parseInt(data[1]),edgeCount++), fromNode, toNode, EdgeType.DIRECTED);
            }

        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

            System.out.println(nodes);

            // The Layout<V, E> is parameterized by the vertex and edge types
            Layout<AirportNode, AirportLink> layout = new FRLayout(g);
            
            layout.setSize(new Dimension(1200, 1000)); // sets the initial size of the space
            // The BasicVisualizationServer<V,E> is parameterized by the edge types
            BasicVisualizationServer<AirportNode, AirportLink> vv
                    = new BasicVisualizationServer<AirportNode, AirportLink>(layout);
            vv.setPreferredSize(new Dimension(1250, 1050)); //Sets the viewing area size
           
            // Setup up a new vertex to paint transformer...
            Transformer<AirportNode, Paint> vertexPaint = new Transformer<AirportNode, Paint>() {
                public Paint transform(AirportNode i) {
                    return Color.GREEN;
                }
            };
            // Set up a new stroke Transformer for the edges
            float dash[] = {10.0f};
            final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
            Transformer<AirportLink, Stroke> edgeStrokeTransformer = new Transformer<AirportLink, Stroke>() {
                public Stroke transform(AirportLink s) {
                    return edgeStroke;
                }
            };
            Transformer<AirportLink, Paint> edgeColorTransformer = new Transformer<AirportLink, Paint>() {
                public Paint transform(AirportLink s) {
                    return Color.DARK_GRAY;
                }
            };

            vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
            vv.getRenderContext().setEdgeDrawPaintTransformer(edgeColorTransformer);
            vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
            vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

            JFrame frame = new JFrame("Simple Graph View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(vv);
            frame.pack();
            frame.setVisible(true);

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
