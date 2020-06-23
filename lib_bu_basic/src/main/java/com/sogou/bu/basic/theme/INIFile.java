package com.sogou.bu.basic.theme;

import java.io.InputStream;
import java.util.Map;

/**
 * INIFile interface, separate origin INIFile as this interface, and legacy implement INIFileImpl
 * @author yangfeng
 */
@SuppressWarnings("PMD")
public interface INIFile {
    int BUF_SIZE = 8192;

    class Builder {
        private Builder() {
        }

        // todo: 使用下发开关，重置切换使用新的加载方案IniFileLite与旧方案INIFileImpl???
        public static INIFile build(String fileName) {
            return IniFileLite.Builder.build(fileName);
        }
        // todo: 使用下发开关，重置切换使用新的加载方案IniFileLite与旧方案INIFileImpl???
        public static INIFile buildMoreCandsIni(InputStream is) {
            return IniFileLite.Builder.buildMoreCandsIni(is);
        }
    }

    /*------------------------------------------------------------------------------
     * Getters
     ------------------------------------------------------------------------------*/
    /**
     * Returns the ini file name being used.
     * @return the INI file name.
     */
    String getFileName();
    
    void setFileName(String fileName);

    /**
     * Returns the specified string property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the string property value.
     */
    String getStringProperty(String pstrSection, String pstrProp);

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
    Boolean getBooleanProperty(String pstrSection, String pstrProp);

    /**
     * Returns the specified integer property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the integer property value.
     */
    Integer getIntegerProperty(String pstrSection, String pstrProp);

    /**
     * Returns the specified long property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the long property value.
     */
    Long getLongProperty(String pstrSection, String pstrProp);

    /**
     * Returns the specified double property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the double property value.
     */
    Double getDoubleProperty(String pstrSection, String pstrProp);

    /**
     * Returns the specified float property from the specified section.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be retrieved.
     * @return the float property value.
     */
    Float getFloatProperty(String pstrSection, String pstrProp);

    /*------------------------------------------------------------------------------
     * Setters
     ------------------------------------------------------------------------------*/
    /**
     * Sets the comments associated with a section.
     * @param pstrSection the section name
     * @param pstrComments the comments.
     */
    void addSection(String pstrSection, String pstrComments);

    /**
     * Sets the specified string property.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be set.
     * @pstrVal the string value to be persisted
     */
    void setStringProperty(String pstrSection, String pstrProp,
                                  String pstrVal, String pstrComments);

    /**
     * Sets the specified boolean property.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be set.
     * @param pblnVal the boolean value to be persisted
     */
    void setBooleanProperty(String pstrSection, String pstrProp,
                                   boolean pblnVal, String pstrComments);

    /**
     * Sets the specified integer property.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be set.
     * @param pintVal the int property to be persisted.
     */
    void setIntegerProperty(String pstrSection, String pstrProp,
                                   int pintVal, String pstrComments);

    /**
     * Sets the specified long property.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be set.
     * @param plngVal the long value to be persisted.
     */
    void setLongProperty(String pstrSection, String pstrProp,
                                long plngVal, String pstrComments);

    /**
     * Sets the specified double property.
     * @param pstrSection the INI section name.
     * @param pstrProp the property to be set.
     * @param pdblVal the double value to be persisted.
     */
    void setDoubleProperty(String pstrSection, String pstrProp,
                                  double pdblVal, String pstrComments);

    /*------------------------------------------------------------------------------
     * Public methods
     ------------------------------------------------------------------------------*/
    boolean containSection(String pstrSection);

    boolean containProperty(String pstrSection, String pstrProp);

    int getTotalSections();

    /**
     * Returns a string array containing names of all sections in INI file.
     * @return the string array of section names
     */
    String[] getAllSectionNames();

    /**
     * Returns a string array containing names of all the properties under specified section.
     * @param pstrSection the name of the section for which names of properties is to be retrieved.
     * @return the string array of property names.
     */
    String[] getPropertyNames(String pstrSection);

    /**
     * Returns a map containing all the properties under specified section.
     * @param pstrSection the name of the section for which properties are to be retrieved.
     * @return the map of properties.
     */
    Map getProperties(String pstrSection);

    /**
     * Removed specified property from the specified section. If the specified
     * section or the property does not exist, does nothing.
     * @param pstrSection the section name.
     * @param pstrProp the name of the property to be removed.
     */
    void removeProperty(String pstrSection, String pstrProp);

    /**
     * Removes the specified section if one exists, otherwise does nothing.
     * @param pstrSection the name of the section to be removed.
     */
    void removeSection(String pstrSection);

    /**
     * Flush changes back to the disk file. If the disk file does not exists then
     * creates the new one.
     */
    boolean save();

    void loadForeignComponentsSet(Map<CharSequence, CharSequence> foreignComponents, String section, String extraPrefix);
}

