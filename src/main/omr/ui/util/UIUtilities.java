//----------------------------------------------------------------------------//
//                                                                            //
//                           U I U t i l i t i e s                            //
//                                                                            //
//  Copyright (C) Herve Bitteur and Brenton Partridge 2000-2007.              //
//  All rights reserved.                                                      //
//  This software is released under the GNU General Public License.           //
//  Contact author at herve.bitteur@laposte.net to report bugs & suggestions. //
//----------------------------------------------------------------------------//
//
package omr.ui.util;

import omr.util.Logger;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Class <code>UIUtilities</code> gathers utilities related to User Interface
 *
 * @author Herv&eacute Bitteur and Brenton Partridge
 * @version $Id$
 */
public class UIUtilities
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(UIUtilities.class);

    /**
     * Customized border for tool buttons, to use consistently in all UI
     * components
     */
    private static Border toolBorder;

    //~ Methods ----------------------------------------------------------------

    //---------------//
    // getToolBorder //
    //---------------//
    /**
     * Report a standard tool border entity, which is a raised bevel border
     *
     * @return the standard tool border
     */
    public static Border getToolBorder ()
    {
        if (toolBorder == null) {
            toolBorder = BorderFactory.createRaisedBevelBorder();
        }

        return toolBorder;
    }

    //---------------//
    // enableActions //
    //---------------//
    /**
     * Given a list of actions, set all these actions (whether they descend
     * from AbstractAction or AbstractButton) enabled or not, according to
     * the bool parameter provided.
     *
     * @param actions list of actions to enable/disable as a whole
     * @param bool    true for enable, false for disable
     */
    public static void enableActions (Collection actions,
                                      boolean    bool)
    {
        for (Iterator it = actions.iterator(); it.hasNext();) {
            Object next = it.next();

            if (next instanceof AbstractAction) {
                ((AbstractAction) next).setEnabled(bool);
            } else if (next instanceof AbstractButton) {
                ((AbstractButton) next).setEnabled(bool);
            } else {
                logger.warning("Neither Button nor Action : " + next);
            }
        }
    }

    //-------------//
    // fileChooser //
    //-------------//
    /**
     * A replacement for standard JFileChooser, to allow better look & feel
     * on the MAc platform.
     *
     * @param save true for a SAVE dialog, false for a LOAD dialog
     * @param parent the parent component for the dialog
     * @param startFile default file, or just default directory, or null
     * @param filter a filter to by applied on files
     * @return the selected file, or null
     */
    public static File fileChooser (boolean    save,
                                    Component  parent,
                                    File       startFile,
                                    FileFilter filter)
    {
        File file = null;

        if (omr.Main.MAC_OS_X) {
            if ((parent == null) && (omr.Main.getGui() != null)) {
                parent = omr.Main.getGui()
                                 .getFrame();
            }

            Component parentFrame = parent;

            while (!(parentFrame instanceof Frame) &&
                   (parentFrame.getParent() != null)) {
                parentFrame = parentFrame.getParent();
            }

            try {
                final FileDialog fd = new FileDialog((Frame) parentFrame);

                if (startFile != null) {
                    fd.setDirectory(
                        startFile.isDirectory() ? startFile.getPath()
                                                : startFile.getParent());
                }

                fd.setMode(save ? FileDialog.SAVE : FileDialog.LOAD);
                fd.setFilenameFilter(filter);

                String title = save ? "Saving: " : "Loading: ";
                title += filter.getDescription();
                fd.setTitle(title);

                fd.setVisible(true);

                String fileName = fd.getFile();
                String dir = fd.getDirectory();

                if ((dir != null) && (fileName != null)) {
                    String fullName = dir +
                                      System.getProperty("file.separator") +
                                      fileName;
                    file = new File(fullName);
                }
            } catch (ClassCastException e) {
                logger.warning("no ancestor is Frame");
            }
        } else {
            final JFileChooser fc = new JFileChooser(startFile);

            // Pre-select the directory, and potentially the file to save to
            if (startFile != null) {
                fc.setCurrentDirectory(startFile);

                if (save) {
                    fc.setSelectedFile(startFile);
                }
            }

            fc.addChoosableFileFilter(filter);

            int result = save ? fc.showSaveDialog(parent)
                         : fc.showOpenDialog(parent);

            if (result == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
            }
        }

        return file;
    }

    //------------------//
    // directoryChooser //
    //------------------//
    /**
     * Let the user select a directory
     *
     * @param parent the parent component for the dialog
     * @param startDir the default directory
     * @return the chosen directory, or null
     */
    public static File directoryChooser (Component parent,
                                         File      startDir)
    {
        String oldMacProperty = System.getProperty(
            "apple.awt.fileDialogForDirectories",
            "false");
        System.setProperty("apple.awt.fileDialogForDirectories", "true");

        FileFilter directoryFilter = new FileFilter(
            "directory",
            new String[] {  }) {
            @Override
            public boolean accept (File f)
            {
                return (f.isDirectory());
            }
        };

        File file = fileChooser(false, parent, startDir, directoryFilter);
        System.setProperty(
            "apple.awt.fileDialogForDirectories",
            oldMacProperty);

        return file;
    }
}
