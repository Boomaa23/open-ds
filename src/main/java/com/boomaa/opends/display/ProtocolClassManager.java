package com.boomaa.opends.display;

import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.util.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProtocolClassManager<T extends ProtocolClass> {
    public static final String RANGED_CLAZZ_NAME_SEPARATOR = "to";
    private final String simpleBaseName;
    private final Map<Integer, Class<?>> protoYearClassMap;
    private int year = -1;

    public ProtocolClassManager(Class<? super T> baseClass) {
        this.simpleBaseName = baseClass.getSimpleName();
        Map<String, Class<?>> yearStrClassMap = extractYearStrClassMap(baseClass.getPackage().getName());
        this.protoYearClassMap = expandYearRanges(yearStrClassMap);
    }

    private Map<String, Class<?>> extractYearStrClassMap(String canonicalPkgName) {
        String pkgPath = canonicalPkgName.replace('.', '/');
        try (BufferedReader br = readerOf(ClassLoader.getSystemClassLoader().getResourceAsStream(pkgPath))) {
            return br.lines()
                    .filter(line -> line.endsWith(".class"))
                    .filter(line -> line.startsWith(simpleBaseName))
                    .filter(line -> !line.contains("$"))
                    .map(line -> line.replace(".class", ""))
                    .map(clazzSimpleName -> canonicalPkgName + "." + clazzSimpleName)
                    .map(this::classStrToObj)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(this::protoClassToYearStr, clazz -> clazz));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private BufferedReader readerOf(InputStream is) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
    }

    private Class<?> classStrToObj(String clazzCanonicalName) {
        try {
            return Class.forName(clazzCanonicalName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String protoClassToYearStr(Class<?> clazz) {
        return clazz.getSimpleName().replaceAll(simpleBaseName, "");
    }

    public Map<Integer, Class<?>> expandYearRanges(Map<String, Class<?>> yearStrClassMap) {
        Map<Integer, Class<?>> yearClassMap = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : yearStrClassMap.entrySet()) {
            String protoYearOrRange = entry.getKey();
            Class<?> protoClass = entry.getValue();
            try {
                int protoYear = Integer.parseInt(protoYearOrRange);
                yearClassMap.put(protoYear, protoClass);
            } catch (NumberFormatException ignored) {
                try {
                    String[] rangeBoundYears = protoYearOrRange.split(RANGED_CLAZZ_NAME_SEPARATOR);
                    if (rangeBoundYears.length != 2) {
                        continue;
                    }
                    int rangeStartYear = Integer.parseInt(rangeBoundYears[0]);
                    int rangeEndYear = Integer.parseInt(rangeBoundYears[1]);
                    for (int protoYear = rangeStartYear; protoYear <= rangeEndYear; protoYear++) {
                        yearClassMap.put(protoYear, protoClass);
                    }
                } catch (NumberFormatException ignored2) {
                }
            }
        }
        return yearClassMap;
    }

    public ProtocolClassManager<T> update() {
        this.year = MainJDEC.getProtocolYear();
        return this;
    }

    public Class<?> getProtoClass() {
        if (year == -1) {
            update();
        }
        return protoYearClassMap.get(this.year);
    }

    public T construct() {
        try {
            return (T) getProtoClass().getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                 | InvocationTargetException e) {
            e.printStackTrace();
            MessageBox.show(ArrayUtils.printStackTrace(e, 10), MessageBox.Type.ERROR);
            System.exit(1);
        }
        return null;
    }

    @Override
    public String toString() {
        return getProtoClass().getCanonicalName();
    }
}