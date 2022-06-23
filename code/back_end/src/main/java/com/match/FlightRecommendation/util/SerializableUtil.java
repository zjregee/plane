package com.match.FlightRecommendation.util;

import java.io.*;

public class SerializableUtil {
    public static byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bo);
        oos.writeObject(obj);
        return bo.toByteArray();
    }
    public static Object deserialize(byte[] bytes) {
        Object object = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);//
            ObjectInputStream ois = new ObjectInputStream(bis);
            object = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return object;
    }
}
