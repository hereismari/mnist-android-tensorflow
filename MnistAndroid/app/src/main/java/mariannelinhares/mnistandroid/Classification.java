package mariannelinhares.mnistandroid;

/**
 * Created by marianne-linhares on 20/04/17.
 */

public class Classification {

    private float conf;
    private String label;

    public Classification(float conf, String label) {
        update(conf, label);
    }

    public Classification() {
        this.conf = (float)-1.0;
        this.label = null;
    }

    public void update(float conf, String label) {
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
