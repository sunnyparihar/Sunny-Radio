package radio.pps.android.com.radio.interfaces;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

/**
 * Created by Prabhpreet on 10-12-2015.
 */
public interface IAsyncCallBack
{
    void callBack(int position, boolean favValue, MaterialFavoriteButton btnFavorite);
}
