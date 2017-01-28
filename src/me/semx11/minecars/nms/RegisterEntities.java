package me.semx11.minecars.nms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_9_R1.EntityTypes;

public class RegisterEntities {

    // I could easily rewrite this using NMS. In fact, I have a very high urge to do so.
    // Please forgive this terrible code.
    public static void registerCAS() {
        int d_map;
        Method[] e;
        Class[] paramTypes = {Class.class, String.class, Integer.TYPE};
        String registerName = "Car";
        int id = 150;
        try {
            List<Map<?, ?>> dataMap = new ArrayList<Map<?, ?>>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }
            if (dataMap.get(2).containsKey(id)) {
                dataMap.get(0).remove(registerName);
                dataMap.get(2).remove(id);
            }
            Method method = EntityTypes.class.getDeclaredMethod("a", paramTypes);
            method.setAccessible(true);
            method.invoke(null, ControllableArmorStand.class, registerName, id);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex1) {
            try {
                d_map = (e = EntityTypes.class.getDeclaredMethods()).length;
                for (int d1 = 0; d1 < d_map; d1++) {
                    Method method = e[d1];
                    if (Arrays.equals(paramTypes, method.getParameterTypes())) {
                        method.invoke(null, ControllableArmorStand.class, registerName, id);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException ex2) {
                ex2.printStackTrace();
            }
        }
    }

}
