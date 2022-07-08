package com.match.FlightRecommendation.service;

import com.match.FlightRecommendation.bean.*;
import com.match.FlightRecommendation.data.*;
import com.match.FlightRecommendation.graph.Node;
import com.match.FlightRecommendation.inter.LowPriceRecService;
import com.match.FlightRecommendation.util.SerializableUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LowPriceRec implements LowPriceRecService {
    private static final String dbGetUrl = "http://127.0.0.1:8080/key/get";
    private static final String dbSetUrl = "http://127.0.0.1:8080/key/set";
    private final List<Integer> agenciesNumList = new ArrayList<>(List.of(1, 2));
    private final List<String> agenciesList = new ArrayList<>(List.of("SHA001", "CAN001", "SHA002", "CAN002", "TAO001", "TAO002", "PEK001", "PEK002"));
    private final Map<String, AllData> buffer = new ConcurrentHashMap<>();

    public LowPriceRec() {
        int count = getCountFromDB();
        for (int i = 1; i <= count; i++) {
            AllData data = getDataFromDB("flight_" + i);
            if (data != null) {
                buffer.put(data.getFreightRuleData().getSequenceNo(), data);
            }
        }
    }

    @Async("asyncServiceExecutor")
    public CompletableFuture<List<PathBean>> findLowPriceFlightCarriers(int passengerNum, String time, String departure, String arrival) {
        List<PathBean> result = getFlightPassengers(passengerNum, time, departure, arrival);
        Collections.sort(result);
        if (result.isEmpty()) {
            return CompletableFuture.completedFuture(result);
        }
        for (PathBean bean: result) {
            List<Integer> indexList = new ArrayList<>();
            List<LowPriceRecBean> onePath = bean.getPath();
            int index = 1;
            int tempPrice = Integer.parseInt(onePath.get(0).getPrice());
            for (int i = 1; i < onePath.size(); i++) {
                if (Integer.parseInt(onePath.get(i).getPrice()) != tempPrice) {
                    LowPriceRecBean recBean = onePath.get(index - 1);
                    for (int j = index; j < i; j++) {
                        recBean.getAgencies().addAll(onePath.get(i).getAgencies());
                        indexList.add(j);
                    }
                    index = i + 1;
                }
            }
            if (index == 1) {
                LowPriceRecBean recBean = onePath.get(0);
                for (int i = 1; i < onePath.size(); i++) {
                    recBean.getAgencies().addAll(onePath.get(i).getAgencies());
                    indexList.add(i);
                }
            }
            for (int i = indexList.size() - 1; i >= 0; i--) {
                onePath.remove((int) indexList.get(i));
            }
            for (LowPriceRecBean recBean : onePath) {
                recBean.setAgencies(recBean.getAgencies().stream().distinct().collect(Collectors.toList()));
            }
        }
        if (result.size() > 20) {
            result = result.subList(0, 20);
        }
        return CompletableFuture.completedFuture(result);
    }

    @Async("asyncServiceExecutor")
    public CompletableFuture<List<PathBean>> findLowPriceFlightPassengers(int passengerNum, String time, String departure, String arrival) {
        List<PathBean> result = getFlightPassengers(passengerNum, time, departure, arrival);
        Collections.sort(result);
        return CompletableFuture.completedFuture(result);
    }

    @Async
    @Override
    public CompletableFuture<UpdateBean> updateRemainData(String flightNum, String[] cabins, String [] changeNum) throws Exception {
        int count = getCountFromDB();
        AllData data = buffer.get(flightNum);
        if (data != null) {
            if (Objects.equals(data.getFreightRuleData().getSequenceNo(), flightNum)) {
                for (int j = 0; j < cabins.length; j++) {
                    char num;
                    switch (cabins[j].charAt(0)) {
                        case 'Y' -> {
                            num = data.getFlightRemainData().getSeatY();
                            if (num - Integer.parseInt(changeNum[j]) >= '0') {
                                data.getFlightRemainData().setSeatY((char) (num - Integer.parseInt(changeNum[j])));
                            } else {
                                data.getFlightRemainData().setSeatY('0');
                            }
                        }
                        case 'C' -> {
                            num = data.getFlightRemainData().getSeatC();
                            if (num - Integer.parseInt(changeNum[j]) >= '0') {
                                data.getFlightRemainData().setSeatC((char) (num - Integer.parseInt(changeNum[j])));
                            } else {
                                data.getFlightRemainData().setSeatC('0');
                            }
                        }
                        case 'F' -> {
                            num = data.getFlightRemainData().getSeatF();
                            if (num - Integer.parseInt(changeNum[j]) >= '0') {
                                data.getFlightRemainData().setSeatF((char) (num + Integer.parseInt(changeNum[j])));
                            } else {
                                data.getFlightRemainData().setSeatF('0');
                            }
                        }
                        default -> {
                        }
                    }
                }
                buffer.put(data.getFreightRuleData().getSequenceNo(), data);
            }
        }
        for (int i = 0; i < count; i++) {
            AllData bean = getDataFromDB("flight_" + i);
            if (bean != null && bean.getFreightRuleData().getSequenceNo().equals(flightNum)) {
                setDataToDB("flight_" + i, Arrays.toString(SerializableUtil.getBytesFromObject(data)));
            }
        }
        return CompletableFuture.completedFuture(new UpdateBean("success"));
    }

    @Async
    @Override
    public CompletableFuture<UpdateBean> insertData(String carrier, String flightNum, String departure, String arrival, String startTime, String endTime, String price) throws Exception {
        int count = getCountFromDB();
        if (count == 0) {
            setDataToDB("data_num", "0");
        }
        Random random = new Random();
        AllData lastData = getDataFromDB("flight_" + count);
        AllData data = new AllData(carrier, departure, arrival);
        data.setFlightData(new FlightData(flightNum, startTime, endTime));
        data.setFlightRemainData(new FlightRemainData('A', 'A', 'A'));
        List<String> agencies = new ArrayList<>();
        int agenciesNum = agenciesNumList.get(Math.abs(random.nextInt() % 2));
        for (int i = 0; i < agenciesNum; i++) {
            agencies.add(agenciesList.get(Math.abs(random.nextInt() % agenciesList.size())));
        }
        String num = lastData == null ? "0001" : ("" + (Integer.parseInt(lastData.getFreightRuleData().getSequenceNo()) + 1));
        data.setFreightRuleData(new FreightRuleData(String.format("%08d", Integer.parseInt(num)),
                "CA", agencies, 0));
        String [] prices = price.split(",");
        List<FreightData> freightDataList = new ArrayList<>();
        freightDataList.add(new FreightData('F', prices[0]));
        freightDataList.add(new FreightData('C', prices[1]));
        freightDataList.add(new FreightData('Y', prices[2]));
        data.setFreightData(freightDataList);
        buffer.put(data.getFreightRuleData().getSequenceNo(), data);
        setDataToDB("flight_" + (count + 1), Arrays.toString(SerializableUtil.getBytesFromObject(data)));
        setDataToDB("data_num", String.valueOf(count + 1));
        return CompletableFuture.completedFuture(new UpdateBean("success"));
    }

    private AllData getDataFromDB(String key) {
        RestTemplate template = new RestTemplate();
        KeyBean bean = new KeyBean(key);
        ResponseEntity<ResultBean> result = template.postForEntity(dbGetUrl, bean, ResultBean.class);
        if (result.getBody() == null) {
            return null;
        }
        String s = result.getBody().getData();
        if (s.length() == 0) {
            return null;
        }
        s = s.substring(1, s.length() - 1);
        String[] sByte = s.split(",");
        byte [] data = new byte[sByte.length];
        data[0] = Byte.parseByte(sByte[0]);
        for (int i = 1; i < sByte.length; i++) {
            data[i] = Byte.parseByte(sByte[i].substring(1));
        }
        return (AllData) SerializableUtil.deserialize(data);
    }

    private int getCountFromDB() {
        RestTemplate template = new RestTemplate();
        KeyBean bean = new KeyBean("data_num");
        ResponseEntity<ResultBean> result = template.postForEntity(dbGetUrl, bean, ResultBean.class);
        if (result.getBody().getData().equals("")) {
            return 0;
        }
        return Integer.parseInt(Objects.requireNonNull(result.getBody()).getData());
    }

    private void setDataToDB(String key, String value) {
        RestTemplate template = new RestTemplate();
        SetBean bean = new SetBean(key, value);
        ResponseEntity<ResultBean> result = template.postForEntity(dbSetUrl, bean, ResultBean.class);
        result.getStatusCodeValue();
    }

    private List<PathBean> getFlightPassengers(int passengerNum, String time, String departure, String arrival) {
        List<PathBean> result = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        List<AllData> sources = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//            AllData data = getDataFromDB("flight_" + i);
//            if (data != null) {
//                if (time.equals(data.getFlightData().getDepartureDatetime().substring(0, 8)) && data.getFlightRemainData().getAllRemain() > passengerNum) {
//                    buffer.put(data.getFreightRuleData().getSequenceNo(), data);
//                    nodes.add(new Node(data.getDeparture(), data.getArrival(), data.getFreightRuleData().getSequenceNo(), data.getFlightData().getDepartureDatetime(), data.getFlightData().getArrivalDatetime()));
//                    if (departure.equals(data.getDeparture())) {
//                        sources.add(data);
//                    }
//                }
//            }
//        }
        for(Map.Entry<String, AllData> entry: buffer.entrySet()) {
            AllData data = entry.getValue();
            if (data != null) {
                if (time.equals(data.getFlightData().getDepartureDatetime().substring(0, 8)) && data.getFlightRemainData().getAllRemain() > passengerNum) {
                    nodes.add(new Node(data.getDeparture(), data.getArrival(), data.getFreightRuleData().getSequenceNo(), data.getFlightData().getDepartureDatetime(), data.getFlightData().getArrivalDatetime()));
                    if (departure.equals(data.getDeparture())) {
                        sources.add(data);
                    }
                }
            }
        }
        List<List<Node>> paths = new ArrayList<>();
        for (AllData data : sources) {
            List<Node> onePath = new ArrayList<>();
            findPath(nodes, data.getFreightRuleData().getSequenceNo(), data.getFlightData().getArrivalDatetime(),
                    departure, arrival, onePath, paths);
        }
        for (List<Node> nodeList: paths) {
            List<LowPriceRecBean> oneResult = new ArrayList<>();
            for (Node node: nodeList) {
                AllData data = buffer.get(node.getSequenceNum());
                List<Character> cabin = new ArrayList<>();
                LowPriceRecBean bean = new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('F')), String.valueOf(Integer.parseInt(data.getFreightData().get(0).getAmount()) * passengerNum), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo(), data.getFlightData().getDepartureDatetime(), data.getFlightData().getArrivalDatetime(), data.getDeparture(), data.getArrival());
                int total = 0;
                int price = 0;
                for (int i = 0; i < passengerNum && i < data.getFlightRemainData().getSeatYNum(); i++) {
                    cabin.add('Y');
                    total++;
                    price += Integer.parseInt(data.getFreightData().get(2).getAmount());
                }
                int tempTotal = total;
                for (int i = tempTotal; i < passengerNum && i - tempTotal < data.getFlightRemainData().getSeatCNum(); i++) {
                    cabin.add('C');
                    total++;
                    price += Integer.parseInt(data.getFreightData().get(1).getAmount());
                }
                tempTotal = total;
                for (int i = tempTotal; i < passengerNum && i - tempTotal < data.getFlightRemainData().getSeatFNum(); i++) {
                    cabin.add('F');
                    total++;
                    price += Integer.parseInt(data.getFreightData().get(0).getAmount());
                }
                bean.setCabin(cabin);
                bean.setPrice(String.valueOf(price));
                oneResult.add(bean);
            }
            result.add(new PathBean(oneResult));
        }
        return result;
    }

    public void findPath(List<Node> nodeList, String sequenceNum, String time, String source, String target, List<Node> path, List<List<Node>> result) {
        for (Node node: path) {
            if (node.getSource().equals(source)) {
                return;
            }
        }
        path = cleanMutilPath(path);
        if (path.size() > 3) {
            return;
        }
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (path.size() == 0 && !sequenceNum.equals(node.getSequenceNum())) {
                continue;
            }
            if (path.size() > 3) {
                return;
            }
            if (node.getSource().equals(source)) {
                //如果相等则找到路径
                if (!isNumeric(node.getTime()) || !isNumeric(time) || time.length() < 10 || node.getTime().length() < 10) {
                    return;
                }
                int deltaTime = (Integer.parseInt(node.getTime().substring(8, 10)) - Integer.parseInt(time.substring(8, 10))) * 60
                        + (Integer.parseInt(node.getTime().substring(10)) - Integer.parseInt(time.substring(10)));
                if (target.equals(node.getTarget()) && (deltaTime >= 120 || path.size() == 0)) {
                    path.add(node);
                    List<Node> tmpList = cleanMutilPath(path);
                    result.add(tmpList);
                    path.clear();
                    return;
                }
                if (deltaTime >= 120 || deltaTime == 0) {
                    path.add(node);
                    findPath(nodeList, sequenceNum, node.getEndTime(), node.getTarget(), target, path, result);
                }
            }
        }
    }

    private List<Node> cleanMutilPath(List<Node> path) {
        List<Node> tmp = new ArrayList<>();
        List<String> city = new ArrayList<>();
        if (path.size() > 0) {
            city.add(path.get(0).getSource());
        }
        for (Node integer : path) {
            if (!city.contains(integer.getTarget())) {
                city.add(integer.getTarget());
                tmp.add(integer);
            }
        }
        return tmp;
    }

    private boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
