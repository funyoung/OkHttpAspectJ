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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
    public void containSectionTest() throws Exception {
        assertTrue("模板配置应包含Key配置。", template.containSection(SECTION_KEY));
        assertTrue("皮肤配置应包含ThemeInfo配置", theme.containSection(SECTION_THEME_INFO));
        assertTrue("更多候选配置应包含MoreCandsContainerInfo配置。", candidate.containSection(SECTION_MORE_CAND));
        assertTrue("符号的更多候选配置应包含ThemeInfo配置", symbol.containSection(SECTION_SYMBOL_MORE));
    }

    @Test
    public void getTotalSectionsTest() throws Exception {
        assertTrue(template.getTotalSections() == 499);
        assertTrue(theme.getTotalSections() == 3);
        assertTrue(candidate.getTotalSections() == 7);
        assertTrue(symbol.getTotalSections() == 3);
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
