package top.geek_studio.chenlongcould.musicplayer.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

/**
 * 状态栏
 */
public class StatusBarView extends View {

    public StatusBarView(Context context) {
        super(context);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置尺寸
     */
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height = insets.getSystemWindowInsetTop();
            setLayoutParams(lp);
        }
        return super.onApplyWindowInsets(insets);
    }

}
