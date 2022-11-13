import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CounterTest  {
    private Map<String, Integer> basket = new HashMap<>();
    private Map<String, String> tsv = new HashMap<>();

    public Map<String, Integer> basketADD(){
        basket.put("������",100000);
        basket.put("������",20000);
        basket.put("�������",3000);
        basket.put("���",1000);
        return basket;
    }

    public Map<String, String> tsvADD(){
        tsv.put("�����","������");
        tsv.put("�����","������");
        tsv.put("����","���");
        tsv.put("�����","�������");
        return tsv;
    }
    Map<String, String> tsv1=tsvADD();
    Map<String, Integer> basket1=basketADD();
    Counter counter=new Counter(tsv1,basket1);

    @ParameterizedTest
    @CsvSource( {
            "�����, 1000",
            "�����,3000",
            "����, 1000",
            "����,3000"
    })
    public void testTestCategoryCount(Object key, Integer sumClient) {
        String category = tsv.get(key);
        basket.put(category,sumClient);
        assertSame(basket,counter.categoryCount(key,sumClient));
    }

    @ParameterizedTest
    @CsvSource( {
            "����, 1000",
            "����,3000",
            "����, 200"
    })

    public void testKeyFalse_TrueBasketFalseCount(Object key, Integer sumClient) {
        Counter counter=new Counter(tsv1,basket);
        String category = tsv.get(key);
        basket.put(category,sumClient);
        Assertions.assertSame(basket,counter.categoryCount(key,sumClient));
    }
    @Test
    public void testTestCount() {
        List<String> listTest = new ArrayList<>();
        listTest.add(String.valueOf(100000));
        listTest.add("������");
        assertEquals(listTest,counter.count());
    }
}