package com.sogou.bu.basic.theme;

import android.support.test.InstrumentationRegistry;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yangfeng
 */
public class AssetsHelper {
    private static final String FILE_THEME = "theme/theme.ini";
    private static final String FILE_CANDIDATE = "theme/cands.ini";
    private static final String FILE_MORE_CANDIDATE = "MoreCandsIni/MoreCandsContainer.ini";
    private static final String FILE_SYMBOL_MORE_CANDIDATE = "MoreCandsIni/SymbolAndHWBihuaMoreCands.ini";
    private static final String FILE_TEMPLATE = "theme/template.ini";

    public INIFile getMoreCandidateContainer() throws IOException {
        return getMoreCandidateContainer(false);
    }

    public INIFile getMoreCandidateContainer(boolean legacy) throws IOException {
        return getIniFile(FILE_MORE_CANDIDATE, legacy);
    }

    public INIFile getThemeFile(boolean legacy) throws IOException {
        return getIniFile(FILE_THEME, legacy);
    }

    public INIFile getCandidateFile(boolean legacy) throws IOException {
        return getIniFile(FILE_CANDIDATE, legacy);
    }

    private INIFile getIniFile(String path, boolean legacy) throws IOException {
        try (InputStream is = InstrumentationRegistry.getContext().getAssets().open(path)) {
            return legacy ? INIFileImpl.Builder.buildMoreCandsIni(is) : INIFile.Builder.buildMoreCandsIni(is);
        }
    }

    public INIFile getTemplateFile(boolean legacy) throws IOException {
        return getIniFile(FILE_TEMPLATE, legacy);
    }

    public INIFile getSymbolMoreCandidateFile(boolean legacy) throws Exception {
        return getIniFile(FILE_SYMBOL_MORE_CANDIDATE, legacy);
    }
}
