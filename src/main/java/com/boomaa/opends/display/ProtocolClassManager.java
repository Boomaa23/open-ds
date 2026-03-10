package com.boomaa.opends.display;

import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.util.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

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
        Stream<String> classNameStream = Stream.empty();
        URL resource = ClassLoader.getSystemClassLoader().getResource(pkgPath);

        try {
            if (resource != null) {
                if (resource.getProtocol().equals("jar")) {
                    String jarPath = resource.getPath().substring("file:".length(), resource.getPath().indexOf("!"));
                    JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                    classNameStream = jar.stream()
                            .map(ZipEntry::getName)
                            .filter(n -> n.startsWith(pkgPath))
                            .filter(n -> n.endsWith(".class"))
                            .map(n -> n.replace('/', '.'));
                } else {
                    classNameStream = Files.list(Paths.get(resource.toURI()))
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .map(f -> canonicalPkgName + "." + f);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return classNameStream
                .filter(n -> !n.contains("$"))
                .map(n -> n.replace(".class", ""))
                .map(this::classStrToObj)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(this::protoClassToYearStr, c -> c));
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