package com.match.FlightRecommendation.graph;

import com.match.FlightRecommendation.bean.LowPriceRecBean;
import com.match.FlightRecommendation.data.AllData;
import com.match.FlightRecommendation.data.FlightData;
import com.match.FlightRecommendation.data.FlightRemainData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FlightGraph {
    private ArrayList<String> vertexList = new ArrayList<>(300);   //存储点的链表
    private int[][] edges;  //邻接矩阵，用来存储两个城市之间航班的数量
    private FlightDataEdge[][] flightDataEdges; //存储每条边的航班信息
    private int numOfEdges; //边的数目
    private int numOfVertex;

    public FlightGraph() {
        //初始化矩阵，一维数组，和边的数目；中国的民用机场约200多个
        edges=new int[300][300];
        flightDataEdges=new FlightDataEdge[300][300];
        numOfEdges = 0;
        numOfVertex = 0;
    }

    public ArrayList<String> getVertexList() {
        return vertexList;
    }

    public void setVertexList(ArrayList<String> vertexList) {
        this.vertexList = vertexList;
    }

    //得到结点的个数
    public int getNumOfVertex() {
        return numOfVertex;
    }

    //得到边的数目
    public int getNumOfEdges() {
        return numOfEdges;
    }

    //返回结点i的数据
    public Object getValueByIndex(int i) {
        return vertexList.get(i);
    }

    //返回v1,v2的权值
    public int getWeight(int v1,int v2) {
        return edges[v1][v2];
    }

    //插入结点,返回结点在结点链中的下标
    public int insertVertex(String vertex) {
        if(!vertexList.contains(vertex)){
            vertexList.add(vertex);
            numOfVertex++;  //总城市数增加
        }
        return vertexList.indexOf(vertex);
    }

    //插入边
    public void insertEdge(int v1, int v2,AllData data) {
        if(edges[v1][v2] == 0)
            flightDataEdges[v1][v2] = new FlightDataEdge();
        flightDataEdges[v1][v2].getFlightDataList().add(data);  //两点之间的航班增加
        edges[v1][v2]=flightDataEdges[v1][v2].getFlightDataList().size();   ////权重为两点之间航班数
        numOfEdges++;   //总航班数增加
    }

    //删除结点
    public void deleteEdge(int v1,int v2) {
        edges[v1][v2]=0;
        numOfEdges--;
    }

    //得到第一个后结点的下标
    public int getFirstAfter(int index) {
        for(int j=0;j<vertexList.size();j++) {
            if (edges[index][j]>0) {
                return j;
            }
        }
        return -1;
    }

    //得到第一个前结点的下标
    public int getFirstBefore(int index) {
        for(int i=0;i<vertexList.size();i++) {
            if (edges[i][index]>0) {
                return i;
            }
        }
        return -1;
    }

    //根据前一个邻接结点的下标来取得下一个后结点
    public int getNextAfter(int v1,int v2) {
        for (int j=v2+1;j<vertexList.size();j++) {
            if (edges[v1][j]>0) {
                return j;
            }
        }
        return -1;
    }

    //根据前一个邻接结点的下标来取得下一个前结点
    public int getNextBefore(int v1,int v2) {
        for (int i=v1-1;i<vertexList.size();i--) {
            if (edges[i][v2]>0) {
                return i;
            }
        }
        return -1;
    }

    //私有函数，深度优先遍历
    public void depthFirstSearch(boolean[] isVisited,int  i) {
        System.out.print(getValueByIndex(i)+"  ");  //首先访问该结点，在控制台打印出来
        isVisited[i]=true;  //置该结点为已访问

        int w=getFirstAfter(i);
        while (w!=-1) {
            if (!isVisited[w]) {
                depthFirstSearch(isVisited,w);
            }
            w=getNextAfter(i, w);
        }
    }

    //广度优先遍历,第一次筛选符合的路径
    public void broadFirstSearch(int dVertex, int aVertex, int passengerNum) {
        int a = dVertex,b,c,d,f=aVertex;
        List<List<Integer>> routeList = new ArrayList<>();  //存放第一次筛选后符合路径经过的城市下标集合

        //遍历找出可能路径，转机次数不大于3次，故最多3层循环
        //循环的条件：上一个顶点到该顶点有边，且路径上没有顶点重复
        //后续遍历时可以增加剪枝：若当前票价已超过


        for(b = 0; b<numOfVertex && edges[a][b]>0; b++){
            //遍历到终点时跳到循环的下一个对象
            if(b==f){
                List<Integer> road = new ArrayList<>();
                road.addAll(Arrays.asList(a, b));
                routeList.add(road);
                continue;
            }
            for(c = 0; c<numOfVertex && edges[b][c]>0 && c!=a; c++){
                if(c==f){
                    List<Integer> road = new ArrayList<>();
                    road.addAll(Arrays.asList(a, b, c));
                    routeList.add(road);
                    continue;
                }
                for(d = 0; d<numOfVertex && edges[c][d]>0 && d!=a && d!=b; d++) {
                    if(d==f){
                        List<Integer> road = new ArrayList<>();
                        road.addAll(Arrays.asList(a, b, c, d));
                        routeList.add(road);
                        continue;
                    }
                }
            }
        }

        //进一步按照票价、航班之间转机的间隔<120分钟来筛选结果
        int routeListSize = routeList.size();
        for (int i = 0; i < routeListSize; i++) {
            int price = 0;
            List<Integer> route = routeList.get(i);
            int routeSize = route.size();   //每条路径上经过的城市数量：2~6
            switch (routeSize){
                case 2: {   //路径为：a-b,即直达
                    List<AllData> flightDataList = flightDataEdges[dVertex][aVertex].getFlightDataList();
                    for(int v1 = 0; v1 < flightDataList.size(); v1++){
                        AllData data = flightDataList.get(v1);
                        FlightRemainData remainData = data.getFlightRemainData();
                        if (remainData.getSeatY() != 'A' && remainData.getSeatY() < passengerNum + 48) {
                            //result.add(new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('Y')), data.getFreightData().get(2).getAmount(), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo()));
                           // System.out.println(data.getDeparture()+","+data.getArrival());
                        } else if (remainData.getSeatC() != 'A' && remainData.getSeatC() < passengerNum + 48) {
                            //result.add(new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('C')), data.getFreightData().get(1).getAmount(), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo()));
                            //System.out.println(data.getDeparture()+","+data.getArrival());
                        } else if (remainData.getSeatF() != 'A' && remainData.getSeatF() < passengerNum + 48) {
                            //result.add(new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('F')), data.getFreightData().get(0).getAmount(), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo()));
                           // System.out.println(data.getDeparture()+","+data.getArrival());
                        }
                    }

                    break;
                }

                case 3:{    //路径为：a-b-c
                    List<AllData> flightDataList1 = flightDataEdges[route.get(0)][route.get(1)].getFlightDataList();
                    //判断转机时间，计算票价
                    for(int v1 = 0; v1 < flightDataList1.size(); v1++){
                        AllData data1 = flightDataList1.get(v1);
                        List<AllData> flightDataList2 = flightDataEdges[route.get(1)][route.get(2)].getFlightDataList();
                        for(int v2 = 0; v2 < flightDataList2.size(); v2++){
                            AllData data2 = flightDataList2.get(v2);
                            int h1 = Integer.parseInt(data1.getFlightData().getDepartureDatetime().substring(8,10));
                            int m1 = Integer.parseInt(data1.getFlightData().getDepartureDatetime().substring(10));
                            int h2 = Integer.parseInt(data2.getFlightData().getDepartureDatetime().substring(8,10));
                            int m2 = Integer.parseInt(data2.getFlightData().getDepartureDatetime().substring(10));
                            if(60*h2+m2-60*h1-m1 > 120){
                                //符合转机条件，可以出票

                                //System.out.println(data1.getDeparture()+","+data1.getArrival()+","+data2.getArrival());

                            }
                        }
                    }
                    break;
                }

                case 4:{    //路径为：a-b-c-d
                    List<AllData> flightDataList1 = flightDataEdges[route.get(0)][route.get(1)].getFlightDataList();
                    //判断转机时间，计算票价
                    for(int v1 = 0; v1 < flightDataList1.size(); v1++){
                        AllData data1 = flightDataList1.get(v1);
                        List<AllData> flightDataList2 = flightDataEdges[route.get(1)][route.get(2)].getFlightDataList();
                        for(int v2 = 0; v2 < flightDataList2.size(); v2++){
                            AllData data2 = flightDataList2.get(v2);
                            List<AllData> flightDataList3 = flightDataEdges[route.get(2)][route.get(3)].getFlightDataList();
                            int h1 = Integer.parseInt(data1.getFlightData().getDepartureDatetime().substring(8,10));
                            int m1 = Integer.parseInt(data1.getFlightData().getDepartureDatetime().substring(10));
                            int h2 = Integer.parseInt(data2.getFlightData().getDepartureDatetime().substring(8,10));
                            int m2 = Integer.parseInt(data2.getFlightData().getDepartureDatetime().substring(10));
                            if(60*h2+m2-60*h1-m1 > 120){    //前两个航班符合转机时间间隔
                                for(int v3 = 0; v3 < flightDataList3.size(); v3++) {
                                    AllData data3 = flightDataList3.get(v3);
                                    int h3 = Integer.parseInt(data3.getFlightData().getDepartureDatetime().substring(8,10));
                                    int m3 = Integer.parseInt(data3.getFlightData().getDepartureDatetime().substring(10));
                                    if(60*h3+m3-60*h2-m2 > 120){
                                        //符合转机条件,可以出票

                                       // System.out.println(data1.getDeparture()+","+data1.getArrival()+","+data2.getArrival()+","+data3.getArrival());

                                    }
                                }
                            }

                            else continue;

                        }
                    }
                    break;
                }

            }
        }
    }


}
