/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.orm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class Model {

    protected LinkedHashMap<String, Object> data;

    private static int LAST_LINE = 0;
    private static int INDEX = -1;
    private static String LAST_METHOD = "";

    /**
     * Recuperar nome da classe que invocou método estático
     *
     * @param <T>
     * @param methodName Nome do método invocado
     * @return
     * @throws br.com.ctecinf.orm.ORMException
     */
    private static <T> Class<T> getClassInvokedStaticMethod(String methodName) throws ORMException {

        try {

            StackTraceElement s = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1];

            File file = new File(s.getFileName().replace(".java", ".class"));
            file.delete();

            InputStream link = Class.forName(s.getClassName()).getResourceAsStream(s.getFileName().replace(".java", ".class"));

            Files.copy(link, file.getAbsoluteFile().toPath());

            Process p = Runtime.getRuntime().exec("javap -c " + file);

            List<Class<?>> classes = new ArrayList();

            try (BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()))) {

                String line;
                boolean isMethod = false;

                while ((line = bf.readLine()) != null) {

                    if (line.trim().contains(" " + s.getMethodName() + "(")) {
                        isMethod = true;
                    }

                    if (isMethod && line.contains("invokestatic") && line.contains(methodName)) {
                        String[] split = line.trim().split("." + methodName)[0].split(" ");
                        classes.add((Class<?>) Class.forName(split[split.length - 1].trim().replace("/", ".")));
                    }
                }

            } finally {
                file.delete();
            }

            if (methodName.equals(LAST_METHOD)) {

                if (s.getLineNumber() > LAST_LINE) {
                    INDEX++;
                } else if (s.getLineNumber() < LAST_LINE) {
                    INDEX = 0;
                }

            } else {
                INDEX = 0;
            }

            LAST_METHOD = methodName;
            LAST_LINE = s.getLineNumber();

            return (Class<T>) classes.get(INDEX);

        } catch (ClassNotFoundException | IOException ex) {
            throw new ORMException(ex);
        }
    }

    private static <T extends Model> List<T> find(String filter, int offset, int limit, String... columns) throws ORMException {

        Class<T> cls = Model.getClassInvokedStaticMethod("find");

        try {
            T model = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new ORMException(ex);
        }

        return null;
    }
}
