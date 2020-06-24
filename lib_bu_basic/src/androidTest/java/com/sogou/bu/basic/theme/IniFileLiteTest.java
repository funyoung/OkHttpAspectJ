package com.sogou.bu.basic.theme;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * IniFileLite测试类, 因为要用到SparseArray这样非Java标准HashMap，因此UT代码放在androidTest里。
 *
 * @author yangfeng
 */
@RunWith(AndroidJUnit4.class)
public class IniFileLiteTest {
    private static final String SECTION_KEY = "Key";
    private static final String SECTION_THEME_INFO = "ThemeInfo";
    private static final String SECTION_MORE_CAND = "MoreCandsContainerInfo";
    private static final String SECTION_SYMBOL_MORE = "SymbolInfo";

    private static final String FG_STYLE = "FG_STYLE";
    private static final String KEY_FG_STYLE = "FGStyle";

    private static final String NAME = "NAME";
    private static final String THEME_NAME = "默认皮肤";

    private static final String FILTER_ITEM_W = "Filter_Item_W";
    private static final String HIDE_FILTER_BAR = "Hide_Filter_Bar";

    private static final String BUTTON_H_GAP = "Button_H_Gap";

    private static final String SECTION_FOREIGN_COMPONENT_KEY = "FOREIGN_COMPONENT";

    private static final String PREFIX_FLOAT_MODE = "f_";

    private final AssetsHelper helper = new AssetsHelper();
    private INIFile template;
    private INIFile theme;
    private INIFile candidate;
    private INIFile symbol;
    private boolean legacy = false;

    @Before
    public void init() throws Exception {
        template = helper.getTemplateFile(legacy);
        theme = helper.getThemeFile(legacy);
        candidate = helper.getMoreCandidateContainer(legacy);
        symbol = helper.getSymbolMoreCandidateFile(legacy);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertNotNull(appContext.getPackageName());
    }

    @Test
    public void openIniFile() throws Exception {
        INIFile iniFile = helper.getMoreCandidateContainer();
        assertNotNull(iniFile);
        assertNotNull(template);
        assertNotNull(theme);
        assertNotNull(candidate);
        assertNotNull(symbol);
    }

    @Test
    public void getFileNameTest() throws Exception {
        assertNull("通过InputStream打开，文件名为空。", template.getFileName());
        assertNull("通过InputStream打开，文件名为空。", theme.getFileName());
        assertNull("通过InputStream打开，文件名为空。", candidate.getFileName());
        assertNull("通过InputStream打开，文件名为空。", symbol.getFileName());
    }

    @Test
    public void containSectionTest() throws Exception {
        assertTrue("模板配置应包含Key配置。", template.containSection(SECTION_KEY));
        assertTrue("皮肤配置应包含ThemeInfo配置", theme.containSection(SECTION_THEME_INFO));
        assertTrue("更多候选配置应包含MoreCandsContainerInfo配置。", candidate.containSection(SECTION_MORE_CAND));
        assertTrue("符号的更多候选配置应包含ThemeInfo配置", symbol.containSection(SECTION_SYMBOL_MORE));
    }

    @Test
    public void getStringPropertyTest() throws Exception {
        assertEquals(KEY_FG_STYLE, template.getStringProperty(SECTION_KEY, FG_STYLE));
        assertEquals(THEME_NAME, theme.getStringProperty(SECTION_THEME_INFO, NAME));
        assertEquals("0", candidate.getStringProperty(SECTION_MORE_CAND, FILTER_ITEM_W));
        assertEquals("0", symbol.getStringProperty(SECTION_SYMBOL_MORE, BUTTON_H_GAP));
    }

    @Test
    public void getBooleanPropertyTest() throws Exception {
        assertFalse(candidate.getBooleanProperty(SECTION_MORE_CAND, HIDE_FILTER_BAR));
    }

    @Test
    public void getIntegerPropertyTest() throws Exception {
        assertEquals(0, (int)candidate.getIntegerProperty(SECTION_MORE_CAND, HIDE_FILTER_BAR));
    }

    @Test
    public void getLongPropertyTest() throws Exception {
        INIFile iniFile = helper.getCandidateFile(false);
        assertEquals(0xFFC8E0E8, (long)iniFile.getLongProperty("CloudView", "BG_COLOR"));
    }

    @Test
    public void containPropertyTest() {
        assertTrue(template.containProperty(SECTION_KEY, FG_STYLE));
    }

    @Test
    public void getPropertiesTest() {
        Set<String> sections = template.getProperties(SECTION_KEY).keySet();
        assertEquals("模板中Key配有9个键值对。", 9, sections.size());
        assertTrue(sections.contains(FG_STYLE));
    }

    @Test
    public void removeSectionTest() {
        int count = template.getAllSectionNames().length;
        template.removeSection(SECTION_KEY);
        assertEquals("删除1个section后的section总数减少1个。", count - 1, template.getAllSectionNames().length);
    }

    @Test
    public void getAllSectionLengthTest() throws Exception {
        assertTrue(template.getAllSectionNames().length == 499);
        assertTrue(theme.getAllSectionNames().length == 3);
        assertTrue(candidate.getAllSectionNames().length == 7);
        assertTrue(symbol.getAllSectionNames().length == 3);
    }

    @Test
    public void getAllSectionNamesTest() throws  Exception {
        assertArrayEquals(template.getAllSectionNames(), helper.getTemplateFile(true).getAllSectionNames());
        assertArrayEquals(theme.getAllSectionNames(), helper.getThemeFile(true).getAllSectionNames());
        assertArrayEquals(candidate.getAllSectionNames(), helper.getMoreCandidateContainer(true).getAllSectionNames());
        assertArrayEquals(symbol.getAllSectionNames(), helper.getSymbolMoreCandidateFile(true).getAllSectionNames());
    }

    @Test
    public void getPropertyNamesTest() throws  Exception {
        assertArrayEquals(template.getPropertyNames(SECTION_KEY), helper.getTemplateFile(true).getPropertyNames(SECTION_KEY));
        assertArrayEquals(theme.getPropertyNames(SECTION_THEME_INFO), helper.getThemeFile(true).getPropertyNames(SECTION_THEME_INFO));
        assertArrayEquals(candidate.getPropertyNames(SECTION_MORE_CAND), helper.getMoreCandidateContainer(true).getPropertyNames(SECTION_MORE_CAND));
        assertArrayEquals(symbol.getPropertyNames(SECTION_SYMBOL_MORE), helper.getSymbolMoreCandidateFile(true).getPropertyNames(SECTION_SYMBOL_MORE));
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

    private Map loadForeignComponentSet(boolean legacy, boolean isFloatModeOnPad) throws IOException {
        INIFile iniFile = helper.getThemeFile(legacy);
        Map<CharSequence, CharSequence> map = new HashMap<>();
        iniFile.loadForeignComponentsSet(map, SECTION_FOREIGN_COMPONENT_KEY, isFloatModeOnPad ? PREFIX_FLOAT_MODE : null);
        return map;
    }
}
