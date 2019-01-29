package storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.MyCountBolt;
import storm.MySplitBolt;
import storm.MySpout;


/**
 * Created by maoxiangyi on 2016/4/27.
 */
public class WordCountTopologMain {
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        //1��׼��һ��TopologyBuilder
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("mySpout",new MySpout(),2);
        topologyBuilder.setBolt("mybolt1",new MySplitBolt(),2).shuffleGrouping("mySpout");
        topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).fieldsGrouping("mybolt1", new Fields("word"));
//        topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).shuffleGrouping("mybolt1");
        //  config.setNumWorkers(2);
        /**
         * i
         * am
         * lilei
         * love
         * hanmeimei
         */


        //2������һ��configuration������ָ����ǰtopology ��Ҫ��worker������
        Config config =  new Config();
        config.setNumWorkers(2);

        //3���ύ����  -----����ģʽ ����ģʽ�ͼ�Ⱥģʽ
//        StormSubmitter.submitTopology("mywordcount",config,topologyBuilder.createTopology());
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("mywordcount",config,topologyBuilder.createTopology());
    }
}