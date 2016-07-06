package radio.pps.android.com.radio;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Prabhpreet on 06-12-2015.
 */
public class ToolbarInitializer {

    private static final String TAG = "ToolbarInitializer";
    private Context context;
    private Toolbar toolbar;
    private String title = "";
    private String subTitle = "";
//    private SearchView searchView;

    public ToolbarInitializer(Context context, Toolbar toolbar) {
        setContext(context);
        setToolbar(toolbar);
    }

    public ToolbarInitializer(Context context, Toolbar toolbar, String mTitle) {
        setContext(context);
        setToolbar(toolbar);
        setTitle(mTitle);
    }


    public ToolbarInitializer(Context context, Toolbar toolbar, String mTitle, String subTitle) {
        setContext(context);
        setToolbar(toolbar);
        setSubTitle(subTitle);
        setTitle(mTitle);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(@NonNull String subTitle) {
        this.subTitle = subTitle;
        getToolbar().setSubtitle(subTitle);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
        toolbar.setTitle(title);
    }

    public void setMenuToolbar(@MenuRes int resId, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        try {
            getToolbar().inflateMenu(resId);

            if (onMenuItemClickListener != null) {
                getToolbar().setOnMenuItemClickListener(onMenuItemClickListener);
            } else {
                throw new NullPointerException("Initialize onMenuItemClickListener " + TAG);
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (Exception es) {
            es.printStackTrace();
        }


    }

    public void setNavigationIcon(@DrawableRes int ic_arrow_back_white_24dp, View.OnClickListener onClickListener) {
        getToolbar().setNavigationIcon(ic_arrow_back_white_24dp);
        getToolbar().setNavigationOnClickListener(onClickListener);
    }

    public void addView(View v) {
        getToolbar().removeAllViews();
        getToolbar().addView(v, 10);
    }
}
