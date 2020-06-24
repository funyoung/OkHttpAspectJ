package com.sogou.bu.basic.theme;

import android.util.Log;

import com.sogou.lib.bu.basic.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The lightweight implement of INIFile interface.
 * 内存结构优化：HashMap的空置率比较高且产生相对较多的小对象（皮肤加载时近1K个对象）
 * （1）集合定量：HashMap$Node[]800多，所占用大小87K，空置率一半以上，如果预先确定大小，预计节省30~40K；
 * （2）优化数据：INIProperty3000多个，貌似只用到了mstrValue属性，能否直接只存储该属性？预计节省60~70K；
 * （3）对Key进行优化：如果key无需获取原始String字符串，且确定key的范围，可以使用key的hashcode（哈希算法可能需要优化）存储，预计节省30~40K内存；且在查找时省去String的equals操作，提升对比效率；
 * （4）将HashMap替换为SparseArray：预计减少3000多个Node对象，内存节省预计在70~80K；
 * 预计整个优化对于内存节省在190K~230K左右；
 *
 * @author yangfeng
 */
@SuppressWarnings("PMD")
public class IniFileLite implements INIFile {
    private static final String TAG = IniFileLite.class.getSimpleName();

    /** Variable to hold the ini file name and full path */
    private String mstrFile = null;

    /** Variable to hold the sections in an ini file. */
    private final Map<String, INISection> mhmapSections = new HashMap();

    public static class Builder {
        public static IniFileLite build(String fileName) {
            return new IniFileLite(fileName);
        }
        private Builder() {
        }
        public static IniFileLite buildMoreCandsIni(InputStream is) {
            return new IniFileLite(is);
        }
    }

    /**
     * Create a INIFile object from the file named in the parameter.
     * @param pstrPathAndName The full path and name of the ini file to be used.
     */
    public IniFileLite(String pstrPathAndName) {
        // Load the specified INI file.
        if (checkFile(pstrPathAndName)) {
            this.mstrFile = pstrPathAndName;
            loadFile();
        }
    }

    public IniFileLite(InputStream is) {
        if (is != null) {
            try {
                loadFile(is);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                        is = null;
                    } catch (IOException e) {
//                Log.e(LOG_TAG, "Could not close stream", e);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }
    }

    /*------------------------------------------------------------------------------
     * Getters
     ------------------------------------------------------------------------------*/
    /**
     * Returns the ini file name being used.
     * @return the INI file name.
     */
    @Override
    public String getFileName() {
        return this.mstrFile;
    }

//    @Override
//    public void setFileName(String fileName) {
//        mstrFile = fileName;
//    }

    /**
     * Returns the specified string property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the string property value.
     */
    @Override
    public String getStringProperty(String pstrSection, String pstrProp) {
        String strRet = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            strRet = objSec.getProperty(pstrProp);
        }
        return strRet;
    }

    /**
     * Returns the specified boolean property from the specified section.
     * This method considers the following values as boolean values.
     * <ol>
     *      <li>YES/yes/Yes - boolean true</li>
     *      <li>NO/no/No  - boolean false</li>
     *      <li>1 - boolean true</li>
     *      <li>0 - boolean false</li>
     *      <li>TRUE/True/true - boolean true</li>
     *      <li>FALSE/False/false - boolean false</li>
     * </ol>
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the boolean value
     */
    @Override
    public Boolean getBooleanProperty(String pstrSection, String pstrProp) {
        Boolean blnRet = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            String objProp = objSec.getProperty(pstrProp);
            if (objProp != null) {
                final String strVal = objProp.toUpperCase();
                blnRet = (strVal.equals("1") || strVal.equals("YES") || strVal.equals("TRUE"));
            }
        }
        return blnRet;
    }

    /**
     * Returns the specified integer property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the integer property value.
     */
    @Override
    public Integer getIntegerProperty(String pstrSection, String pstrProp) {
        Integer intRet = null;
        String objProp = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            objProp = objSec.getProperty(pstrProp);
            try {
                if (objProp != null) {
                    if (objProp != null) {
                        intRet = Integer.decode(objProp);
                    }
                }
            }
            catch (NumberFormatException NFExIgnore) {
                if(BuildConfig.DEBUG) {
                    Log.e(TAG, "Wrong Integer Format String - " + objProp);
                }
            }
            finally {
                if (objProp != null) {
                    objProp = null;
                }
            }
            objSec = null;
        }
        return intRet;
    }

    /**
     * Returns the specified long property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the long property value.
     */
    @Override
    public Long getLongProperty(String pstrSection, String pstrProp) {
        Long lngRet = null;
        String strVal = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            strVal = objSec.getProperty(pstrProp);
            try {
                if (strVal != null) {
                    lngRet = Long.decode(strVal);
                }
            }
            catch (NumberFormatException NFExIgnore) {
                if(BuildConfig.DEBUG) {
                    Log.e(TAG, "Wrong Long Format String - " + strVal);
                }
            }
        }
        return lngRet;
    }

    /**
     * Returns the specified double property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the double property value.
     */
    @Override
    public Double getDoubleProperty(String pstrSection, String pstrProp) {
        Double dblRet = null;
        String strVal = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            strVal = objSec.getProperty(pstrProp);
            try {
                if (strVal != null) {
                    dblRet = Double.valueOf(strVal);
                }
            }
            catch (NumberFormatException NFExIgnore) {
            }
        }
        return dblRet;
    }

    /**
     * Returns the specified float property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the float property value.
     */
    @Override
    public Float getFloatProperty(String pstrSection, String pstrProp) {
        Float dblRet = null;
        String strVal = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            strVal = objSec.getProperty(pstrProp);
            try {
                if (strVal != null) {
                    dblRet = Float.valueOf(strVal);
                }
            }
            catch (NumberFormatException NFExIgnore) {
            }
        }
        return dblRet;
    }

//    /*------------------------------------------------------------------------------
//     * Setters
//     ------------------------------------------------------------------------------*/
//    /**
//     * Sets the comments associated with a section.
//     * @param pstrSection the section name
//     * @param pstrComments the comments.
//     */
//    @Override
//    public void addSection(String pstrSection, String pstrComments) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec == null) {
//            objSec = new INISection();
//            this.mhmapSections.put(pstrSection, objSec);
//        }
//    }

//    /**
//     * Sets the specified string property.
//     * @param pstrSection the INI section name.
//     * @param pstrProp the property to be set.
//     * @pstrVal the string value to be persisted
//     */
//    @Override
//    public void setStringProperty(String pstrSection, String pstrProp,
//                                  String pstrVal, String pstrComments) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec == null) {
//            objSec = new INISection();
//            this.mhmapSections.put(pstrSection, objSec);
//        }
//        objSec.setProperty(pstrProp, pstrVal, pstrComments);
//    }

//    /**
//     * Sets the specified boolean property.
//     * @param pstrSection the INI section name.
//     * @param pstrProp the property to be set.
//     * @param pblnVal the boolean value to be persisted
//     */
//    @Override
//    public void setBooleanProperty(String pstrSection, String pstrProp,
//                                   boolean pblnVal, String pstrComments) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec == null) {
//            objSec = new INISection();
//            this.mhmapSections.put(pstrSection, objSec);
//        }
//        if (pblnVal) {
//            objSec.setProperty(pstrProp, "TRUE", pstrComments);
//        }
//        else {
//            objSec.setProperty(pstrProp, "FALSE", pstrComments);
//        }
//    }

//    /**
//     * Sets the specified integer property.
//     * @param pstrSection the INI section name.
//     * @param pstrProp the property to be set.
//     * @param pintVal the int property to be persisted.
//     */
//    @Override
//    public void setIntegerProperty(String pstrSection, String pstrProp,
//                                   int pintVal, String pstrComments) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec == null) {
//            objSec = new INISection();
//            this.mhmapSections.put(pstrSection, objSec);
//        }
//        objSec.setProperty(pstrProp, Integer.toString(pintVal), pstrComments);
//    }

//    /**
//     * Sets the specified long property.
//     * @param pstrSection the INI section name.
//     * @param pstrProp the property to be set.
//     * @param plngVal the long value to be persisted.
//     */
//    @Override
//    public void setLongProperty(String pstrSection, String pstrProp,
//                                long plngVal, String pstrComments) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec == null) {
//            objSec = new INISection();
//            this.mhmapSections.put(pstrSection, objSec);
//        }
//        objSec.setProperty(pstrProp, Long.toString(plngVal), pstrComments);
//    }

//    /**
//     * Sets the specified double property.
//     * @param pstrSection the INI section name.
//     * @param pstrProp the property to be set.
//     * @param pdblVal the double value to be persisted.
//     */
//    @Override
//    public void setDoubleProperty(String pstrSection, String pstrProp,
//                                  double pdblVal, String pstrComments) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec == null) {
//            objSec = new INISection();
//            this.mhmapSections.put(pstrSection, objSec);
//        }
//        objSec.setProperty(pstrProp, Double.toString(pdblVal), pstrComments);
//    }

    /*------------------------------------------------------------------------------
     * Public methods
     ------------------------------------------------------------------------------*/
    @Override
    public boolean containSection(String pstrSection) {
        return this.mhmapSections.containsKey(pstrSection);
    }

    @Override
    public boolean containProperty(String pstrSection, String pstrProp) {
        boolean ret = false;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            ret = objSec.containProperty(pstrProp);
        }
        return ret;
    }

//    @Override
//    public int getTotalSections() {
//        return this.mhmapSections.size();
//    }

    /**
     * Returns a string array containing names of all sections in INI file.
     * @return the string array of section names
     */
    @Override
    public String[] getAllSectionNames() {
        int iCntr = 0;
        Iterator iter = null;
        String[] arrRet = null;

        try {
            if (!this.mhmapSections.isEmpty()) {
                arrRet = new String[this.mhmapSections.size()];
                for (iter = this.mhmapSections.keySet().iterator(); ; iter.hasNext()) {
                    arrRet[iCntr] = (String) iter.next();
                    iCntr++;
                }
            }
        }
        catch (NoSuchElementException NSEExIgnore) {
        }
        finally {
            if (iter != null) {
                iter = null;
            }
        }
        return arrRet;
    }

    /**
     * Returns a string array containing names of all the properties under specified section.
     * @param pstrSection the name of the section for which names of properties is to be retrieved.
     * @return the string array of property names.
     */
    @Override
    public String[] getPropertyNames(String pstrSection) {
        String[] arrRet = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            arrRet = objSec.getPropNames();
        }
        return arrRet;
    }

    /**
     * Returns a map containing all the properties under specified section.
     * @param pstrSection the name of the section for which properties are to be retrieved.
     * @return the map of properties.
     */
    @Override
    public Map getProperties(String pstrSection) {
        Map hmRet = null;
        INISection objSec = this.mhmapSections.get(pstrSection);
        if (objSec != null) {
            hmRet = objSec.getProperties();
        }
        return hmRet;
    }

//    /**
//     * Removed specified property from the specified section. If the specified
//     * section or the property does not exist, does nothing.
//     * @param pstrSection the section name.
//     * @param pstrProp the name of the property to be removed.
//     */
//    @Override
//    public void removeProperty(String pstrSection, String pstrProp) {
//        INISection objSec = this.mhmapSections.get(pstrSection);
//        if (objSec != null) {
//            objSec.removeProperty(pstrProp);
//        }
//    }

    /**
     * Removes the specified section if one exists, otherwise does nothing.
     * @param pstrSection the name of the section to be removed.
     */
    @Override
    public void removeSection(String pstrSection) {
        this.mhmapSections.remove(pstrSection);
    }

//    /**
//     * Flush changes back to the disk file. If the disk file does not exists then
//     * creates the new one.
//     */
//    @Override
//    public boolean save() {
//        boolean blnRet = false;
//        File objFile = null;
//        String strName = null;
//        String strTemp = null;
//        Iterator itrSec = null;
//        INISection objSec = null;
//        FileWriter objWriter = null;
//
//        try {
//            if (this.mhmapSections.size() == 0) {
//                return false;
//            }
//            objFile = new File(this.mstrFile);
//            if (objFile.exists()) {
//                objFile.delete();
//            }
//            objWriter = new FileWriter(objFile);
//            itrSec = this.mhmapSections.keySet().iterator();
//            while (itrSec.hasNext()) {
//                strName = (String) itrSec.next();
//                objSec = this.mhmapSections.get(strName);
//                strTemp = objSec.toString();
//                objWriter.write(strTemp);
//                objWriter.write("\r\n");
//                objSec = null;
//            }
//            blnRet = true;
//        }
//        catch (IOException IOExIgnore) {
//        } catch (Exception e) {
//        }
//        finally {
//            if (objWriter != null) {
//                closeWriter(objWriter);
//                objWriter = null;
//            }
//            if (objFile != null) {
//                objFile = null;
//            }
//            if (itrSec != null) {
//                itrSec = null;
//            }
//        }
//        return blnRet;
//    }

    @Override
    public void loadForeignComponentsSet(Map<CharSequence, CharSequence> foreignComponents, String section, String extraPrefix) {
        if (containSection(section)) {
            Map<String, String> foreignComponentsConfig = getProperties(section);
            boolean isFloatModeOnPad = null != extraPrefix && !extraPrefix.isEmpty();
            for (Map.Entry<String, String> entry : foreignComponentsConfig.entrySet()) {
                String value = entry.getValue();
                if (null != value && !value.isEmpty()) {
                    StringTokenizer token = new StringTokenizer(value, ',');
                    while (token.hasMoreTokens()) {
                        String name = token.nextToken();
                        foreignComponents.put(name, entry.getKey());
                        if (isFloatModeOnPad)
                            foreignComponents.put(extraPrefix + name, entry.getKey());
                    }
                }
            }
        }
    }

    private void loadFile(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, BUF_SIZE);
        int iPos = -1;
        String strLine = null;
        String strSection = null;
        String strRemarks = null;
        INISection objSec = null;       
        try {
            int i = 0;
            while (br.ready()) {
                iPos = -1;
                String line = br.readLine();
                if (line != null)
                    strLine = line.trim();
                i++;

                if ((strLine == null) || (strLine.length() == 0)) {
                } else if (strLine.charAt(0) == ';') {
                } else if (strLine.startsWith("[") && strLine.endsWith("]")) {
                    // Section start reached create new section
                    if (objSec != null) {
                        this.mhmapSections.put(strSection, objSec);
                    }
                    objSec = null;
                    strSection = strLine.substring(1, strLine.length() - 1).trim();
                    objSec = new INISection();
                    strRemarks = null;
                }
                else if ( (iPos = strLine.indexOf('=')) > 0 && objSec != null) {
                    objSec.setProperty(strLine.substring(0, iPos).trim(),
                                       strLine.substring(iPos + 1).trim(),
                                       strRemarks);
                    strRemarks = null;
                }
            }
            if (objSec != null) {
                this.mhmapSections.put(strSection, objSec);
            }
        }
        catch (FileNotFoundException FNFExIgnore) {
            this.mhmapSections.clear();
        }
        catch (IOException IOExIgnore) {
            this.mhmapSections.clear();
        }
        catch (NullPointerException NPExIgnore) {
            this.mhmapSections.clear();
        }
        finally {
            closeReader(br);
            closeReader(isr);
            br = null;
            isr = null;
            if (objSec != null) {
                objSec = null;
            }
        }
    }

    /**
     * Reads the INI file and load its contentens into a section collection after
     * parsing the file line by line.
     */
    private void loadFile() {
        int iPos = -1;
        String strLine = null;
        String strSection = null;
        String strRemarks = null;
        BufferedReader objBRdr = null;
        FileReader objFRdr = null;
        INISection objSec = null;

        try {
            objFRdr = new FileReader(this.mstrFile);
            if (objFRdr != null) {
                // default is 8k for android
                objBRdr = new BufferedReader(objFRdr, BUF_SIZE);
                if (objBRdr != null) {

                    while (objBRdr.ready()) {
                        iPos = -1;
                        strLine = objBRdr.readLine();
//                        i++;
                        if (strLine == null) {
                            continue;
                        } else {
                            strLine = strLine.trim();
                        }
                       
                        if (/*(strLine == null) || */(strLine.length() == 0)) {
                        } else if (strLine.charAt(0) == ';') {
                        } else if (strLine.startsWith("[") && strLine.endsWith("]")) {
                            // Section start reached create new section
                            if (objSec != null) {
                                this.mhmapSections.put(strSection, objSec);
//                                bw.write("mhmapSections.put(\"" + strSection + "\", objSec);\n");
                            }
                            objSec = null;
                            strSection = strLine.substring(1, strLine.length() - 1).trim();
//                            Log.d(TAG+getFileName(), "Got 1 section!!! " + strSection);
                            objSec = new INISection();
//                            bw.write("objSec = new INISection(\"" + strSection + "\", \"" + strRemarks + "\");\n");
                            strRemarks = null;
                        }
                        else if ( (iPos = strLine.indexOf('=')) > 0 && objSec != null) {
                            //Log.d(TAG, "Got 1 property!!!");
                            // read the key value pair 012345=789
                            objSec.setProperty(strLine.substring(0, iPos).trim(),
                                               strLine.substring(iPos + 1).trim(),
                                               strRemarks);
                            strRemarks = null;
                        }
                    }
                    if (objSec != null) {
                        this.mhmapSections.put(strSection, objSec);
                    } 
                }
            }
        }
        catch (Exception e) {
            this.mhmapSections.clear();
        }
        finally {
            if (objBRdr != null) {
                closeReader(objBRdr);
                objBRdr = null;
            }
            if (objFRdr != null) {
                closeReader(objFRdr);
                objFRdr = null;
            }
            if (objSec != null) {
                objSec = null;
            }
        }
    }

    /**
     * Helper function to close a reader object.
     * @param pobjRdr the reader to be closed.
     */
    private void closeReader(Reader pobjRdr) {
        if (pobjRdr == null) {
            return;
        }
        try {
            pobjRdr.close();
        }
        catch (IOException IOExIgnore) {
        }
    }

    /**
     * Helper function to close a writer object.
     * @param pobjWriter the writer to be closed.
     */
    private void closeWriter(Writer pobjWriter) {
        if (pobjWriter == null) {
            return;
        }

        try {
            pobjWriter.close();
        }
        catch (IOException IOExIgnore) {
        }
    }

    /**
     * Helper method to check the existance of a file.
     * @param pstrFile the full path and name of the file to be checked.
     * @return true if file exists, false otherwise.
     */
    private boolean checkFile(String pstrFile) {
        boolean blnRet = false;
        File objFile = null;

        try {
            objFile = new File(pstrFile);
            blnRet = (objFile.exists() && objFile.isFile());
        }
        catch (Exception e) {
            blnRet = false;
            // TODO: 2018/9/6 处理皮肤的异常
//            if (pstrFile != null && pstrFile.startsWith(Environment.SYSTEM_PATH)) {
//                SettingManager.getInstance(SogouRealApplication.getApplication()).setIsNeedExtractTheme(true, true, true);
//            }
        }
        finally {
            if (objFile != null) {
                objFile = null;
            }
        }
        return blnRet;
    }

    /**
     * This function deletes the remark characters ';' from source string
     * @param pstrSrc the source  string
     * @return the converted string
     */
    private String delRemChars(String pstrSrc) {
        int intPos = 0;

        if (pstrSrc == null) {
            return null;
        }
        while ( (intPos = pstrSrc.indexOf(";")) >= 0) {
            if (intPos == 0) {
                pstrSrc = pstrSrc.substring(intPos + 1);
            }
            else if (intPos > 0) {
                pstrSrc = pstrSrc.substring(0, intPos) + pstrSrc.substring(intPos + 1);
            }
        }
        return pstrSrc;
    }

    /**
     * This function adds a remark character ';' in source string.
     * @param pstrSrc source string
     * @return converted string.
     */
    private static String addRemChars(String pstrSrc) {
        int intLen = 2;
        int intPos = 0;
        int intPrev = 0;

        String strLeft = null;
        String strRight = null;

        if (pstrSrc == null) {
            return null;
        }
        while (intPos >= 0) {
            intLen = 2;
            intPos = pstrSrc.indexOf("\r\n", intPrev);
            if (intPos < 0) {
                intLen = 1;
                intPos = pstrSrc.indexOf("\n", intPrev);
                if (intPos < 0) {
                    intPos = pstrSrc.indexOf("\r", intPrev);
                }
            }
            if (intPos == 0) {
                pstrSrc = ";\r\n" + pstrSrc.substring(intPos + intLen);
                intPrev = intPos + intLen + 1;
            }
            else if (intPos > 0) {
                strLeft = pstrSrc.substring(0, intPos);
                strRight = pstrSrc.substring(intPos + intLen);
                if (strRight == null) {
                    pstrSrc = strLeft;
                }
                else if (strRight.length() == 0) {
                    pstrSrc = strLeft;
                }
                else {
                    pstrSrc = strLeft + "\r\n;" + strRight;
                }
                intPrev = intPos + intLen + 1;
            }
        }
        if (!pstrSrc.substring(0, 1).equals(";")) {
            pstrSrc = ";" + pstrSrc;
        }
        pstrSrc = pstrSrc + "\r\n";
        return pstrSrc;
    }

    /*------------------------------------------------------------------------------
     * Private class representing the INI Section.
     *----------------------------------------------------------------------------*/
    /**
     * Class to represent the individual ini file section.
     * @author Prasad P. Khandekar
     * @version 1.0
     * @since 1.0
     */
    private static class INISection {
        /** Variable to hold the properties falling under this section. */
        private final Map<String, String> mhmapProps = new HashMap<>();

        /**
         * Removes specified property value from this section.
         * @param pstrProp The name of the property to be removed.
         */
        public void removeProperty(String pstrProp) {
            this.mhmapProps.remove(pstrProp);
        }

        /**
         * Creates or modifies the specified property value.
         * @param pstrProp The name of the property to be created or modified.
         * @param pstrValue The new value for the property.
         * @param pstrComments the associated comments
         */
        public void setProperty(String pstrProp, String pstrValue,
                                String pstrComments) {
            this.mhmapProps.put(pstrProp, pstrValue);
        }

        /**
         * Returns a map of all properties.
         * @return a map of all properties
         */
        public Map getProperties() {
            return Collections.unmodifiableMap(this.mhmapProps);
        }

        /**
         * Returns a string array containing names of all the properties under
         * this section.
         * @return the string array of property names.
         */
        public String[] getPropNames() {
            int iCntr = 0;
            String[] arrRet = null;
            Iterator iter = null;

            try {
                if (this.mhmapProps.size() > 0) {
                    arrRet = new String[this.mhmapProps.size()];
                    for (iter = this.mhmapProps.keySet().iterator(); iter.hasNext(); ) {
                        arrRet[iCntr] = (String) iter.next();
                        iCntr++;
                    }
                }
            }
            catch (NoSuchElementException NSEExIgnore) {
                arrRet = null;
            }
            return arrRet;
        }

        public boolean containProperty(String pstrProp) {
            return this.mhmapProps.containsKey(pstrProp);
        }

        /**
         * Returns underlying value of the specified property.
         * @param pstrProp the property whose underlying value is to be etrieved.
         * @return the property value.
         */
        public String getProperty(String pstrProp) {
            return this.mhmapProps.get(pstrProp);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            Set colKeys = null;
            String strRet = "";
            Iterator iter = null;
            StringBuilder objBuf = new StringBuilder();

            colKeys = this.mhmapProps.keySet();
            if (colKeys != null) {
                iter = colKeys.iterator();
                if (iter != null) {
                    while (iter.hasNext()) {
                        objBuf.append(this.mhmapProps.get(iter.next()));
                        objBuf.append("\r\n");
                    }
                }
            }
            strRet = objBuf.toString();
            return strRet;
        }
    }
}

