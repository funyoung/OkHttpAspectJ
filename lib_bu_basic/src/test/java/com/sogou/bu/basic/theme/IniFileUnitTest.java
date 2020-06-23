package com.sogou.bu.basic.theme;

import com.sogou.bu.basic.ResourceFile;

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
    private static final String FILE_THEME = "theme.ini";
    private static final String FILE_TEMPLATE = "template.ini";
    private static final String SECTION_KEY = "Key";

    private INIFile legacyFile;
    private INIFile iniFile;

    private INIFile getThemeFile() {
        return INIFileImpl.Builder.build(ResourceFile.getFilePath(getClass(), FILE_THEME));
    }

    @Before
    public void init() {
        String templatePath = ResourceFile.getFilePath(getClass(), FILE_TEMPLATE);
        legacyFile = INIFileImpl.Builder.build(templatePath);
        iniFile = INIFile.Builder.build(templatePath);
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

//    @Test
//    public void getPropertiesTest() throws Exception {
//        assertArrayEquals(iniFile.getProperties(SECTION_KEY).keySet(), legacyFile.getProperties(SECTION_KEY).keySet());
//    }

    @Test
    public void loadForeignComponentsSetTest() {
        INIFile themeIni = getThemeFile();
        Map<CharSequence, CharSequence> map = new HashMap<>();
        themeIni.loadForeignComponentsSet(map, "FOREIGN_COMPONENT", null);
        assertFalse(map.isEmpty());

        int size = map.size();
        map.clear();
        themeIni.loadForeignComponentsSet(map, "FOREIGN_COMPONENT", "f_");
        assertTrue(map.size() == 2 * size);
        assertFalse(map.isEmpty());
    }
}