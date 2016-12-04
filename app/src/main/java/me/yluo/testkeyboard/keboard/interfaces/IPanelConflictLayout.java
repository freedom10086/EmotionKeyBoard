
package me.yluo.testkeyboard.keboard.interfaces;

/**
 * The interface used for the panel's container layout and it used in the case of non-full-screen
 * theme window.
 */
public interface IPanelConflictLayout {
    boolean isKeyboardShowing();

    /**
     * @return The real status of Visible or not
     */
    boolean isVisible();

    /**
     * Keyboard->Panel
     *
     */
    void handleShow();

    /**
     * Panel->Keyboard
     *
     */
    void handleHide();
}
