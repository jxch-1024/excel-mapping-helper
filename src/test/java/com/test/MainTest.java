package com.test;

import com.luhui.framework.excel.ExcelResolverTemplate;
import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainTest {

    @Data
    public static class ProdValuation {
        private Set<String> valDate;
        private BankDeposit bankDeposit;
        private String defaultValue;
        private List<TotalPricesInfo> totalPricesInfos = new ArrayList<>();
        private String person;
    }

    @Data
    public static class BankDeposit {
        private List<Double> cost;
    }

    @Data
    public static class TotalPricesInfo {
        private double amount;
        private String subjectName;
        private FaceInfo faceInfo;
    }

    @Data
    public static class FaceInfo{
        private String amount;
    }

    public static void main(String[] args) throws IOException {
        final String path = MainTest.class.getClassLoader().getResource("excel.yaml").getPath();
        Reader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer context = new StringBuffer();
        while (true){
            final String line = bufferedReader.readLine();
            if(line==null){
                break;
            }else{
                context.append(line + "\n");
            }
        }
        String excelPath = MainTest.class.getClassLoader().getResource("1.xls").getPath();
        final List<Object> parse = new ExcelResolverTemplate().parse(excelPath, context.toString());
        System.out.println(ReflectionToStringBuilder.toString(parse.get(0)));
    }
}
