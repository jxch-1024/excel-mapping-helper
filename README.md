# excel-mapping-helper

#### 介绍
excel映射解析框架。
本工具开发旨在通过配置文件的方式进行解析excel指定数据的方案。



#### 使用说明

通过配置yaml文件，来解析一个excel文件内容到指定对象，比如说：yaml文件配置内容：



```yaml
excel:

  com.test.MainTest$ProdValuation:

    valDate: cond:[0].startsWith('日期')|default:default|[0].substring(3)

    #这里用到了rowOffset代表行偏移一行

    person: cond:[0].startsWith('统计人')|[0].substring(4)

    bankDeposit:

      entryCnd: cond:[1].equals('银行存款')

      properties:

        cost: default:10|rowOffset:1|[4]

    totalPricesInfos:

      entryCnd: cond:[1].contains('价格')

      properties:

        amount: [2]

        subjectName: cond:[1].equals('面额')|rowOffset:1|[0]

        faceInfo:

          properties:

            amount: [2]

```




我们想映射的类是com.test.MainTest$ProdValuation。

```java
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
```
相应的每个字段映射方法。
最后直接调用ExcelResolverTemplate.parse就可以解析成指定的对象，具体使用方法可以参照maven test包下的MainTest的测试用例。


涉及技术：
1，easyExcel ：扩展了poi的一些功能。实际上，他还是基于POI的实现。
2，snake: yaml文件解析技术。
3，ognl: 表达式引擎。
4，Apache bean操作工具类 commons-beanutils，提供一些对bean的常用操作，基于反射技术的封装。如设置属性值，获取属性值，创建对象等。



#### 
