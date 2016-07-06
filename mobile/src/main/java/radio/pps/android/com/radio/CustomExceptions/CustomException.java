package radio.pps.android.com.radio.CustomExceptions;

/**
 * Created by Prabhpreet on 23-01-2016.
 */

import android.content.Context;

public class CustomException extends RuntimeException {
    public CustomException() {
    }

    public CustomException(String name) {
        super(name);
    }
}

