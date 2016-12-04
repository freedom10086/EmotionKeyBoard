package me.yluo.testkeyboard.keboard.interfaces;

/**
 * Created by Jacksgong on 3/26/16.
 * <p/>
 * For align the height of the keyboard to panel height as much as possible.
 */
public interface IPanelHeightTarget {

    /**
     * for handle the panel's height, will be equal to the keyboard height which had saved last time.
     */
    void refreshHeight(final int panelHeight);

    /**
     * @return get the height of target-view.
     */
    int getHeight();

    /**
     * Be invoked by onGlobalLayoutListener call-back.
     *
     * @param showing whether the keyboard is showing or not.
     */
    void onKeyboardShowing(boolean showing);
}
