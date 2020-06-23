package com.sogou.bu.basic.theme;

import com.sogou.bu.basic.ResourceFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Ini文件操作类
 */
public class IniFileHelper {
    private static final String FILE_THEME = "theme.ini";
    private static final String FILE_MORE_CANDIDATE = "MoreCandsContainer.ini";
    private static final String FILE_SYMBOL_MORE_CANDIDATE = "SymbolAndHWBihuaMoreCands.ini";
    private static final String FILE_TEMPLATE = "template.ini";

    private String getPath(String fileName) {
        return ResourceFile.getFilePath(getClass(), fileName);
    }

    private INIFile getIniFile(String fileName, boolean legacy) {
        String path = getPath(fileName);
        return legacy ? INIFileImpl.Builder.build(path) :
                INIFile.Builder.build(path);
    }

    public INIFile getThemeFile(boolean legacy) {
        return getIniFile(FILE_THEME, legacy);
    }

    public INIFile getTemplateFile(boolean legacy) {
        return getIniFile(FILE_TEMPLATE, legacy);
    }

    public INIFile getMoreCandidateFile(boolean legacy) throws Exception {
        InputStream is = new FileInputStream(new File(ResourceFile.getFilePath(getClass(), FILE_MORE_CANDIDATE)));
        return legacy ? INIFileImpl.Builder.buildMoreCandsIni(is) : INIFile.Builder.buildMoreCandsIni(is);
    }

    public INIFile getSymbolMoreCandidateFile(boolean legacy) throws Exception {
        InputStream is = new FileInputStream(new File(ResourceFile.getFilePath(getClass(), FILE_SYMBOL_MORE_CANDIDATE)));
        return legacy ? INIFileImpl.Builder.buildMoreCandsIni(is) : INIFile.Builder.buildMoreCandsIni(is);
    }
}
