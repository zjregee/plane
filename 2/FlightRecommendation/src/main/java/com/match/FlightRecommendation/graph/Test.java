package com.match.FlightRecommendation.graph;

import com.match.FlightRecommendation.data.*;
import com.match.FlightRecommendation.service.LowPriceRec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Test {
    private static int count = 1001;
    private static Random random = new Random();
    private final byte [] key = "flight_data".getBytes(StandardCharsets.UTF_8);
    private final List<Integer> agenciesNumList = new ArrayList<>(List.of(1, 2));
    private final List<String> agenciesList = new ArrayList<>(List.of("SHA001", "CAN001", "SHA002", "CAN002", "TAO001", "TAO002", "PEK001", "PEK002"));
    private final static List<String> carrierCode = new ArrayList<>(Arrays.asList("MU", "CA", "CZ", "PN", "MF", "SC"));
    private final static List<Character> remainNum = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A'));
    private final static List<String> cityCode = new ArrayList<>(Arrays.asList("PEK", "SHA", "TSN", "SIA"));
    private final static List<String> fPriceList = new ArrayList<>(Arrays.asList("2650", "2660"));
    private final static List<String> cPriceList = new ArrayList<>(Arrays.asList("1600", "1650", "1700", "1750", "1800", "1850"));
    private final static List<String> yPriceList = new ArrayList<>(Arrays.asList("1000", "1050", "1100", "1150", "1200", "1250"));
    private final static List<String> flightNums = new ArrayList<>(Arrays.asList("0100", " 0101", "0102", "0103", "0104", "0105"));

    public static void main(String[] args)
    {
        List<AllData> data = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            initData(data);
        }
        long start = System.currentTimeMillis();
        test1(data);
        long end = System.currentTimeMillis();
        System.out.println("算法一所需时间:" + (end - start));
        start = System.currentTimeMillis();
        test2(data);
        end = System.currentTimeMillis();
        System.out.println("算法二所需时间:" + (end - start));
    }

    private static void test1(List<AllData> dataList) {
        FlightGraph flightGraph = new FlightGraph();
        for (AllData data : dataList) {
            if (data != null) {
                //当请求的时间与航班的出发日期相同、并且余座超过旅客数量时，将航班和城市插入图中
                if("20220607".equals(data.getFlightData().getDepartureDatetime().substring(0,8))){
                    //航班还必须满足多代理人、多乘客
                    //多代理人暂时没加
                    if(true){

                        //添加顶点
                        int aVertex = flightGraph.insertVertex(data.getDeparture());
                        int bVertex = flightGraph.insertVertex(data.getArrival());

                        //添加边
                        flightGraph.insertEdge(aVertex,bVertex,data);
                    }


                }


            }
        }

        //查询航班,按照到达城市初步生成符合的航班路径
        int dVertex = flightGraph.getVertexList().indexOf("PEK");   //起点城市下标
        int aVertex = flightGraph.getVertexList().indexOf("SHA"); //终点城市下标
        flightGraph.broadFirstSearch(dVertex,aVertex,3);  //生成答案

    }

    private static void test2(List<AllData> data) {
        LowPriceRec lowPriceRec = new LowPriceRec();
        List<List<Node>> paths = new ArrayList<>();
        List<Node> nodeList = new ArrayList<>();
        for (AllData allData : data) {
            nodeList.add(new Node(allData.getDeparture(), allData.getArrival(), allData.getFreightRuleData().getSequenceNo(), allData.getFlightData().getDepartureDatetime(), allData.getFlightData().getArrivalDatetime()));
        }
        for (AllData allData : data) {
            if (allData.getDeparture().equals("SHA")) {
                List<Node> onePath = new ArrayList<>();
                lowPriceRec.findPath(nodeList, allData.getFreightRuleData().getSequenceNo(), allData.getFlightData().getDepartureDatetime(),
                        "SHA", "PEK", onePath, paths);
                if (onePath.size() > 0) {
                    paths.add(onePath);
                }
            }
        }
    }

    private static void initData(List<AllData> data) {
        int start = random.nextInt() % 10;
        int end = random.nextInt() % 10;
        while (end < start) {
            end++;
        }
        AllData allData = new AllData(carrierCode.get(Math.abs(random.nextInt()) % carrierCode.size()), cityCode.get(Math.abs(random.nextInt()) % cityCode.size()), cityCode.get(Math.abs(random.nextInt()) % cityCode.size()));
        allData.setFlightData(new FlightData(flightNums.get(Math.abs(random.nextInt()) % flightNums.size()), "20220607" + String.format("%02d", Math.abs(random.nextInt() % 12)) + String.format("%02d", Math.abs(random.nextInt() % 60)), "20220607" + String.format("%02d", Math.abs(random.nextInt() % 12)) + String.format("%02d", Math.abs(random.nextInt() % 60))));
        List<FreightData> freightDataList = new ArrayList<>();
        freightDataList.add(new FreightData('F', fPriceList.get(Math.abs(random.nextInt()) % fPriceList.size())));
        freightDataList.add(new FreightData('C', cPriceList.get(Math.abs(random.nextInt()) % cPriceList.size())));
        freightDataList.add(new FreightData('Y', yPriceList.get(Math.abs(random.nextInt()) % yPriceList.size())));
        allData.setFreightData(freightDataList);
        allData.setFlightRemainData(new FlightRemainData(remainNum.get(Math.abs(random.nextInt()) % remainNum.size()), remainNum.get(Math.abs(random.nextInt()) % remainNum.size()), remainNum.get(Math.abs(random.nextInt()) % remainNum.size())));
        allData.setFreightRuleData(new FreightRuleData("0000" + (count++), "CA"
                , new ArrayList<>(Arrays.asList("SHA001", "CAN001")), 0));
        data.add(allData);

    }
}
