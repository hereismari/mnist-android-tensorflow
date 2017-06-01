package mariannelinhares.mnistandroid.models;

/**
 * Created by marianne-linhares on 20/04/17.
 */

public class Classification {

    private float conf;
    private String label;

    Classification() {
        this.conf = -1.0F;
        this.label = null;
    }

    void update(float conf, String label) {
        this.conf = conf;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public float getConf() {
        return conf;
    }
}
