package mariannelinhares.mnistandroid.models;

/**
 * Created by Piasy{github.com/Piasy} on 29/05/2017.
 */

public interface Classifier {
    String name();

    Classification recognize(final float[] pixels);
}
