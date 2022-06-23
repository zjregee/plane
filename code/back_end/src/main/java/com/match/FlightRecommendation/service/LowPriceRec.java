package com.match.FlightRecommendation.service;

import com.match.FlightRecommendation.bean.*;
import com.match.FlightRecommendation.data.*;
import com.match.FlightRecommendation.inter.LowPriceRecService;
import com.match.FlightRecommendation.util.SerializableUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LowPriceRec implements LowPriceRecService {
    private static final String dbGetUrl = "http://47.114.99.34:8080/key/get";
    private static final String dbSetUrl = "http://47.114.99.34:8080/key/set";
    private int count = 1001;
    private Random random;
    private final byte [] key = "flight_data".getBytes(StandardCharsets.UTF_8);
    private final List<Integer> agenciesNumList = new ArrayList<>(List.of(1, 2));
    private final List<String> agenciesList = new ArrayList<>(List.of("SHA001", "CAN001", "SHA002", "CAN002", "TAO001", "TAO002", "PEK001", "PEK002"));
    private final List<String> carrierCode = new ArrayList<>(Arrays.asList("MU", "CA", "CZ", "PN", "MF", "SC"));
    private final List<Character> remainNum = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A'));
    private final List<String> cityCode = new ArrayList<>(Arrays.asList("PEK", "SHA", "TSN", "SIA", "HGH", "DLC", "TAO", "FOC"));
    private final List<String> fPriceList = new ArrayList<>(Arrays.asList("2650", "2660"));
    private final List<String> cPriceList = new ArrayList<>(Arrays.asList("1600", "1650", "1700", "1750", "1800", "1850"));
    private final List<String> yPriceList = new ArrayList<>(Arrays.asList("1000", "1050", "1100", "1150", "1200", "1250"));
    private final List<String> flightNums = new ArrayList<>(Arrays.asList("0100", " 0101", "0102", "0103", "0104", "0105"));

    public List<LowPriceRecBean> findLowPriceFlightCarriers(String [] time, String [] departure, String [] arrival) throws Exception {
        List<LowPriceRecBean> result = findLowPriceFlightPassengers(1, time, departure, arrival);
        if (result.isEmpty()) {
            return result;
        }
        List<Integer> indexList = new ArrayList<>();
        int index = 1;
        int tempPrice = Integer.parseInt(result.get(0).getPrice());
        for (int i = 1; i < result.size(); i++) {
            if (Integer.parseInt(result.get(i).getPrice()) != tempPrice) {
                LowPriceRecBean bean = result.get(index - 1);
                for (int j = index; j < i; j++) {
                    bean.getAgencies().addAll(result.get(i).getAgencies());
                    indexList.add(j);
                }
                index = i + 1;
            }
        }
        if (index == 1) {
            LowPriceRecBean bean = result.get(0);
            for (int i = 1; i < result.size(); i++) {
                bean.getAgencies().addAll(result.get(i).getAgencies());
                indexList.add(i);
            }
        }
        for (int i = indexList.size() - 1; i >= 0; i--) {
            result.remove((int) indexList.get(i));
        }
        for (LowPriceRecBean bean : result) {
            bean.setAgencies(bean.getAgencies().stream().distinct().collect(Collectors.toList()));
        }
        return result;
    }

    public List<LowPriceRecBean> findLowPriceFlightPassengers(int passengerNum, String [] time, String [] departure, String [] arrival) throws Exception {
        List<LowPriceRecBean> result = new ArrayList<>();
        int count = getCountFromDB();
        setDataToDB("data_num", String.valueOf(1));
        for (int i = 1; i <= count; i++) {
            AllData data = getDataFromDB("flight_" + 2);
            if (data != null) {
                for (int j = 0; j < time.length; j++) {
                    if (time[j].equals(data.getFlightData().getDepartureDatetime())
                            && departure[j].equals(data.getDeparture())
                            && arrival[j].equals(data.getArrival())) {
                        FlightRemainData remainData = data.getFlightRemainData();
                        if (remainData.getSeatY() != 'A' && remainData.getSeatY() < passengerNum + 48) {
                            result.add(new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('Y')), data.getFreightData().get(2).getAmount(), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo()));
                        } else if (remainData.getSeatC() != 'A' && remainData.getSeatC() < passengerNum + 48) {
                            result.add(new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('C')), data.getFreightData().get(1).getAmount(), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo()));
                        } else if (remainData.getSeatF() != 'A' && remainData.getSeatF() < passengerNum + 48) {
                            result.add(new LowPriceRecBean(new ArrayList<>(List.of(data.getCarrier())), data.getFlightData().getFlightNo(), new ArrayList<>(List.of('F')), data.getFreightData().get(0).getAmount(), data.getFreightRuleData().getAgencies(), data.getFreightRuleData().getSequenceNo()));
                        }
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public UpdateBean updateRemainData(String flightNum, String[] cabins, String [] changeNum) throws Exception {
        int count = getCountFromDB();
        for (int i = 0; i < count; i++) {
            AllData data = getDataFromDB("flight_" + i);
            if (data != null) {
                if (Objects.equals(data.getFlightData().getFlightNo(), flightNum)) {
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
                        setDataToDB("flight_" + j, Arrays.toString(SerializableUtil.getBytesFromObject(data)));
                    }
                }
            }
        }
        return new UpdateBean("success");
    }

    @Override
    public UpdateBean insertData(String carrier, String flightNum, String departure, String arrival, String startTime, String endTime, String price) throws Exception {
        int count = getCountFromDB();
        random = new Random();
        AllData lastData = getDataFromDB("flight_" + count);
        AllData data = new AllData(carrier, departure, arrival);
        data.setFlightData(new FlightData(flightNum, startTime, endTime));
        data.setFlightRemainData(new FlightRemainData('A', 'A', 'A'));
        List<String> agencies = new ArrayList<>();
        int agenciesNum = agenciesNumList.get(Math.abs(random.nextInt() % 2));
        for (int i = 0; i < agenciesNum; i++) {
            agencies.add(agenciesList.get(Math.abs(random.nextInt() % agenciesList.size())));
        }
        data.setFreightRuleData(new FreightRuleData(String.format("%08d", Integer.parseInt(lastData.getFreightRuleData().getSequenceNo()) + 1),
                "CA", agencies, 0));
        String [] prices = price.split(",");
        List<FreightData> freightDataList = new ArrayList<>();
        freightDataList.add(new FreightData('F', prices[0]));
        freightDataList.add(new FreightData('C', prices[1]));
        freightDataList.add(new FreightData('Y', prices[2]));
        data.setFreightData(freightDataList);
        setDataToDB("data_num", String.valueOf(count + 1));
        setDataToDB("flight_" + (count + 1), Arrays.toString(SerializableUtil.getBytesFromObject(data)));
        return new UpdateBean("success");
    }

    private AllData getDataFromDB(String key) {
        RestTemplate template = new RestTemplate();
        KeyBean bean = new KeyBean(key);
        ResponseEntity<ResultBean> result = template.postForEntity(dbGetUrl, bean, ResultBean.class);
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
        return Integer.parseInt(Objects.requireNonNull(result.getBody()).getData());
    }

    private boolean setDataToDB(String key, String value) {
        RestTemplate template = new RestTemplate();
        SetBean bean = new SetBean(key, value);
        ResponseEntity<ResultBean> result = template.postForEntity(dbSetUrl, bean, ResultBean.class);
        return result.getStatusCodeValue() == 200;
    }

    private void initData(Jedis jedis) throws Exception {
        int start = random.nextInt() % 10;
        int end = random.nextInt() % 10;
        while (end < start) {
            end++;
        }
        AllData allData = new AllData(carrierCode.get(Math.abs(random.nextInt()) % carrierCode.size()), cityCode.get(Math.abs(random.nextInt()) % cityCode.size()), cityCode.get(Math.abs(random.nextInt()) % cityCode.size()));
        allData.setFlightData(new FlightData(flightNums.get(Math.abs(random.nextInt()) % flightNums.size()), "2022060" + start, "2022060" + end));
        List<FreightData> freightDataList = new ArrayList<>();
        freightDataList.add(new FreightData('F', fPriceList.get(Math.abs(random.nextInt()) % fPriceList.size())));
        freightDataList.add(new FreightData('C', cPriceList.get(Math.abs(random.nextInt()) % cPriceList.size())));
        freightDataList.add(new FreightData('Y', yPriceList.get(Math.abs(random.nextInt()) % yPriceList.size())));
        allData.setFreightData(freightDataList);
        allData.setFlightRemainData(new FlightRemainData(remainNum.get(Math.abs(random.nextInt()) % remainNum.size()), remainNum.get(Math.abs(random.nextInt()) % remainNum.size()), remainNum.get(Math.abs(random.nextInt()) % remainNum.size())));
        allData.setFreightRuleData(new FreightRuleData("0000" + (count++), "CA"
                , new ArrayList<>(Arrays.asList("SHA001", "CAN001")), 0));
        jedis.lpush("flight_data".getBytes(StandardCharsets.UTF_8), SerializableUtil.getBytesFromObject(allData));
    }
}
