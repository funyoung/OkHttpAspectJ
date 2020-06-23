package com.sogou.bu.basic.theme;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * IniFileUnitTest, both implements of INIFile should have the same result while loading
 * any ini file of theme conf.
 *
 * @author yangfeng
 */
@SuppressWarnings("PMD")
public class IniFileUnitTest {
    private static final String SECTION_KEY = "Key";
    private static final String SECTION_FOREIGN_COMPONENT_KEY = "FOREIGN_COMPONENT";

    private static final String PREFIX_FLOAT_MODE = "f_";

    private final IniFileHelper helper = new IniFileHelper();

    private INIFile legacyFile;
    private INIFile iniFile;

    @Before
    public void init() {
        legacyFile = helper.getTemplateFile(true);
        iniFile = helper.getTemplateFile(false);
    }

    @Test
    public void initTest() throws Exception {
        assertNotNull(legacyFile);
        assertNotNull(iniFile);
    }

    @Test
    public void getFileNameTest() throws Exception {
        assertEquals(iniFile.getFileName(), legacyFile.getFileName());
    }

    @Test
    public void getAllSectionNamesTest() throws Exception {
        assertArrayEquals(iniFile.getAllSectionNames(), legacyFile.getAllSectionNames());
    }

    @Test
    public void getPropertyNamesTest() throws Exception {
        assertArrayEquals(iniFile.getPropertyNames(SECTION_KEY), legacyFile.getPropertyNames(SECTION_KEY));
    }

    @Test
    public void loadForeignComponentsSetTest() throws Exception  {
        Map<CharSequence, CharSequence> legacyMap = loadForeignComponentSet(true, false);
        Map<CharSequence, CharSequence> map = loadForeignComponentSet(false, false);
        assertFalse("有非悬浮模式的外文配置项，不能为空。", map.isEmpty());
        assertEquals("新旧解析类得到非悬浮模式的配置项数量相同。", map.size(), legacyMap.size());

        int size = map.size();

        legacyMap = loadForeignComponentSet(true, true);
        map = loadForeignComponentSet(false, true);
        assertEquals("两种解析方式得到悬浮模式的配置项数量相同。", map.size(), legacyMap.size());
        assertTrue("悬浮模式的配置项数量翻倍。", map.size() == 2 * size);
    }

    @Test
    public void getMoreCandidateFileTest() throws Exception {
        INIFile legacy = helper.getMoreCandidateFile(true);
        INIFile iniFile = helper.getMoreCandidateFile(false);
        assertNotNull(iniFile);
        assertEquals("两种解析方式更多候选配置得到section数相同。", legacy.getTotalSections(), iniFile.getTotalSections());
        assertTrue("两种解析方式更多候选配置得到section数大于0.", iniFile.getTotalSections() > 0);
    }

    @Test
    public void getSymbolMoreCandidateFileTest() throws Exception {
        INIFile legacy = helper.getSymbolMoreCandidateFile(true);
        INIFile iniFile = helper.getSymbolMoreCandidateFile(false);
        assertNotNull(iniFile);
        assertEquals("两种解析方式符号键盘更多候选配置得到section数相同。", legacy.getTotalSections(), iniFile.getTotalSections());
        assertTrue("两种解析方式符号键盘更多候选配置得到section数大于0.", iniFile.getTotalSections() > 0);
    }

    private Map loadForeignComponentSet(boolean legacy, boolean isFloatModeOnPad) {
        INIFile iniFile = helper.getThemeFile(legacy);
        Map<CharSequence, CharSequence> map = new HashMap<>();
        iniFile.loadForeignComponentsSet(map, SECTION_FOREIGN_COMPONENT_KEY, isFloatModeOnPad ? PREFIX_FLOAT_MODE : null);
        return map;
    }
}