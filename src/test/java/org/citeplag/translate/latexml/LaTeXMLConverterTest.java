package org.citeplag.translate.latexml;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Vincent Stange
 */
public class LaTeXMLConverterTest {

    @Test
    public void configToUrlString() throws Exception {
        // prepare
        Map<String, String> map = new LinkedHashMap<>();
        map.put("A", "1");
        map.put("B", "");
        map.put("C", "2,3,4,5");

        // test it
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(null);
        String result = laTeXMLConverter.configToUrlString(map);

        // verify
        assertThat(result, equalTo("&A=1&B&C=2&C=3&C=4&C=5"));
    }

}