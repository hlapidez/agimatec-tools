package com.agimatec.utility.fileimport;

/**
 * Description: Factory that creates CSVStringTokenizer <br/>
 * User: roman.stumm <br/>
 * Date: 28.08.2007 <br/>
 * Time: 16:32:14 <br/>
 * Copyright: Agimatec GmbH
 */
public class CSVStringTokenizerFactory implements LineTokenizerFactory {
    private String delimeter = ";";

    /**
     * @param aLine
     * @return a new CSVStringTokenizer
     */
    public LineTokenizer createTokenizer(String aLine) {
        return new CSVStringTokenizer(aLine, getDelimiter());
    }

    private String getDelimiter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }
}