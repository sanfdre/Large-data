package ShareFriend;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ShareFriendStepOne {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);
        job.setJarByClass(ShareFriendStepOne.class);

        job.setMapperClass(ShareFriendMapper.class);
        job.setReducerClass(ShareFriendReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job,args[0]);
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);
    }
    static class ShareFriendMapper extends Mapper<LongWritable,Text,Text,Text>{
        private static Text person = new Text();
        private static Text friend = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] lines = line.split(":");
            person.set(lines[0]);
            for (String friendStr : lines[1].split(",")){
                friend.set(friendStr);
                context.write(friend,person);
            }
        }
    }

    static class ShareFriendReducer extends Reducer<Text,Text,Text,Text>{
        private static Text friend = new Text();
        private static Text personsText = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            friend.set(key.toString());
            List<Text> persons = new ArrayList<Text>();
            Iterator<Text> iterator = values.iterator();
            while (iterator.hasNext()){
                persons.add(iterator.next());
            }
            Collections.sort(persons);
            StringBuffer line = new StringBuffer();
            for (Text person : persons){
                line.append(person.toString()+",");
            }
            line.deleteCharAt(line.length()-1);
            personsText.set(line.toString());
            context.write(friend,personsText);
        }
    }
}