package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity;

import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.Embeddable;

@Embeddable
public class Evolution {
    @CsvBindByPosition(position = 0)
    String OracleURL;//甲骨文

    @CsvBindByPosition(position = 1)
    String BronzeInscriptionURL; //金文
    @CsvBindByPosition(position = 2)
    String SealScriptURL; //篆书
    @CsvBindByPosition(position = 3)
    String ClerialScriptURL; // 隶书
    @CsvBindByPosition(position = 4)
    String RegularScriptURL; //楷书

    public Evolution(String oracleURL, String bronzeInscriptionURL, String sealScriptURL, String clerialScriptURL, String regularScriptURL) {
        OracleURL = oracleURL;
        BronzeInscriptionURL = bronzeInscriptionURL;
        SealScriptURL = sealScriptURL;
        ClerialScriptURL = clerialScriptURL;
        RegularScriptURL = regularScriptURL;
    }

    public Evolution() {
    }

    public String getRegularScriptURL() {
        return RegularScriptURL;
    }

    public void setRegularScriptURL(String regularScriptURL) {
        RegularScriptURL = regularScriptURL;
    }

    public String getOracleURL() {
        return OracleURL;
    }

    public void setOracleURL(String oracleURL) {
        OracleURL = oracleURL;
    }

    public String getBronzeInscriptionURL() {
        return BronzeInscriptionURL;
    }

    public void setBronzeInscriptionURL(String bronzeInscriptionURL) {
        BronzeInscriptionURL = bronzeInscriptionURL;
    }

    public String getSealScriptURL() {
        return SealScriptURL;
    }

    public void setSealScriptURL(String sealScriptURL) {
        SealScriptURL = sealScriptURL;
    }

    public String getClerialScriptURL() {
        return ClerialScriptURL;
    }

    public void setClerialScriptURL(String clerialScriptURL) {
        ClerialScriptURL = clerialScriptURL;
    }
}
