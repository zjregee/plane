package com.match.FlightRecommendation.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * 数据预处理，根据全市场（或特定市场）的商营直飞航班信息构建航班网络图
 */
public class CreateFlightGraph {
    public static void main(String args[]) {
        int n=8,e=9;//分别代表结点个数和边的数目
        String labels[]={"PEK","SHE","FOC","CAN","SZX","SHA","HAK","CGD"};//结点的标识
        FlightGraph graph=new FlightGraph();
        for(String label:labels) {
            graph.insertVertex(label);//插入结点
        }




        System.out.println("深度优先搜索序列为：");
        boolean[] isVisited = new boolean[n];
        //graph.depthFirstSearch(isVisited,0);
        System.out.println();
        System.out.println("广度优先搜索序列为：");
        //graph.broadFirstSearch(isVisited,0);

        String departure = "SHE";
        String arrival = "CAN";
        List<String> L = new ArrayList<>();


    }
}
