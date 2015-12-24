//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                   V i e w P a r a m e t e r s                                  //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//  Copyright © Hervé Bitteur and others 2000-2014. All rights reserved.
//  This software is released under the GNU General Public License.
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package omr.ui;

import omr.constant.Constant;
import omr.constant.ConstantSet;

import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

/**
 * Class {@code ViewParameters} handles parameters for various views,
 * using properties referenced through their programmatic name to avoid typos.
 *
 * @author Hervé Bitteur
 */
public class ViewParameters
        extends AbstractBean
{
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(ViewParameters.class);

    private static final Constants constants = new Constants();

    /** Should the invalid sheets be displayed. */
    public static final String INVALID_SHEET_DISPLAY = "invalidSheetDisplay";

    /** Should the letter boxes be painted */
    public static final String LETTER_BOX_PAINTING = "letterBoxPainting";

    /** Should the staff lines be painted */
    public static final String STAFF_LINE_PAINTING = "staffLinePainting";

    /** Should the stick lines be painted */
    public static final String LINE_PAINTING = "linePainting";

    /** Should the Sections selection be enabled */
    public static final String SECTION_MODE = "sectionMode";

    /** Should the stick attachments be painted */
    public static final String ATTACHMENT_PAINTING = "attachmentPainting";

    /** Should the translation links be painted */
    public static final String TRANSLATION_PAINTING = "translationPainting";

    /** Should the sentence links be painted */
    public static final String SENTENCE_PAINTING = "sentencePainting";

    //~ Instance fields ----------------------------------------------------------------------------
    /** Dynamic flag to remember if section mode is enabled. */
    private boolean sectionMode = false;

    /** Staff line painting is chosen to be not persistent. */
    private boolean staffLinePainting = true;

    //~ Methods ------------------------------------------------------------------------------------
    //-------------//
    // getInstance //
    //-------------//
    public static ViewParameters getInstance ()
    {
        return Holder.INSTANCE;
    }

    //----------------------//
    // isAttachmentPainting //
    //----------------------//
    public boolean isAttachmentPainting ()
    {
        return constants.attachmentPainting.getValue();
    }

    //---------------------//
    // isLetterBoxPainting //
    //---------------------//
    public boolean isLetterBoxPainting ()
    {
        return constants.letterBoxPainting.getValue();
    }

    //----------------//
    // isLinePainting //
    //----------------//
    public boolean isLinePainting ()
    {
        return constants.linePainting.getValue();
    }

    //---------------------//
    // isStaffLinePainting //
    //---------------------//
    public boolean isStaffLinePainting ()
    {
        return staffLinePainting;
    }

    //-----------------------//
    // isInvalidSheetDisplay //
    //-----------------------//
    public boolean isInvalidSheetDisplay ()
    {
        return constants.invalidSheetDisplay.getValue();
    }

    //---------------//
    // isSectionMode //
    //---------------//
    public boolean isSectionMode ()
    {
        return sectionMode;
    }

    //--------------------//
    // isSentencePainting //
    //--------------------//
    public boolean isSentencePainting ()
    {
        return constants.sentencePainting.getValue();
    }

    //-----------------------//
    // isTranslationPainting //
    //-----------------------//
    public boolean isTranslationPainting ()
    {
        return constants.translationPainting.getValue();
    }

    //-----------------------//
    // setAttachmentPainting //
    //-----------------------//
    public void setAttachmentPainting (boolean value)
    {
        boolean oldValue = constants.attachmentPainting.getValue();
        constants.attachmentPainting.setValue(value);
        firePropertyChange(ATTACHMENT_PAINTING, oldValue, value);
    }

    //----------------------//
    // setLetterBoxPainting //
    //----------------------//
    public void setLetterBoxPainting (boolean value)
    {
        boolean oldValue = constants.letterBoxPainting.getValue();
        constants.letterBoxPainting.setValue(value);
        firePropertyChange(LETTER_BOX_PAINTING, oldValue, value);
    }

    //-----------------//
    // setLinePainting //
    //-----------------//
    public void setLinePainting (boolean value)
    {
        boolean oldValue = constants.linePainting.getValue();
        constants.linePainting.setValue(value);
        firePropertyChange(LINE_PAINTING, oldValue, value);
    }

    //----------------------//
    // setStaffLinePainting //
    //----------------------//
    public void setStaffLinePainting (boolean value)
    {
        boolean oldValue = staffLinePainting;
        staffLinePainting = value;
        firePropertyChange(STAFF_LINE_PAINTING, oldValue, value);
    }

    //------------------------//
    // setInvalidSheetDisplay //
    //------------------------//
    public void setInvalidSheetDisplay (boolean value)
    {
        boolean oldValue = constants.invalidSheetDisplay.getValue();
        constants.invalidSheetDisplay.setValue(value);
        firePropertyChange(INVALID_SHEET_DISPLAY, oldValue, value);
    }

    //----------------//
    // setSectionMode //
    //----------------//
    public void setSectionMode (boolean value)
    {
        boolean oldValue = sectionMode;
        sectionMode = value;
        firePropertyChange(SECTION_MODE, oldValue, value);
    }

    //---------------------//
    // setSentencePainting //
    //---------------------//
    public void setSentencePainting (boolean value)
    {
        boolean oldValue = constants.sentencePainting.getValue();
        constants.sentencePainting.setValue(value);
        firePropertyChange(SENTENCE_PAINTING, oldValue, value);
    }

    //------------------------//
    // setTranslationPainting //
    //------------------------//
    public void setTranslationPainting (boolean value)
    {
        boolean oldValue = constants.translationPainting.getValue();
        constants.translationPainting.setValue(value);
        firePropertyChange(TRANSLATION_PAINTING, oldValue, value);
    }

    //-------------------//
    // toggleAttachments //
    //-------------------//
    /**
     * Action that toggles the display of attachments in selected sticks
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = ATTACHMENT_PAINTING)
    public void toggleAttachments (ActionEvent e)
    {
    }

    //---------------//
    // toggleLetters //
    //---------------//
    /**
     * Action that toggles the display of letter boxes in selected glyphs
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = LETTER_BOX_PAINTING)
    public void toggleLetters (ActionEvent e)
    {
    }

    //-------------//
    // toggleLines //
    //-------------//
    /**
     * Action that toggles the display of mean line in selected sticks
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = LINE_PAINTING)
    public void toggleLines (ActionEvent e)
    {
    }

    //------------------//
    // toggleStaffLines //
    //------------------//
    /**
     * Action that toggles the display of staff lines
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = STAFF_LINE_PAINTING)
    public void toggleStaffLines (ActionEvent e)
    {
    }

    //---------------------//
    // toggleInvalidSheets //
    //---------------------//
    /**
     * Action that toggles the display of invalid sheets
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = INVALID_SHEET_DISPLAY)
    public void toggleInvalidSheets (ActionEvent e)
    {
    }

    //----------------//
    // toggleSections //
    //----------------//
    /**
     * Action that toggles the ability to select Sections (rather than Glyphs)
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = SECTION_MODE)
    public void toggleSections (ActionEvent e)
    {
    }

    //-----------------//
    // toggleSentences //
    //-----------------//
    /**
     * Action that toggles the display of sentences in selected glyphs
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = SENTENCE_PAINTING)
    public void toggleSentences (ActionEvent e)
    {
    }

    //--------------------//
    // toggleTranslations //
    //--------------------//
    /**
     * Action that toggles the display of translations in selected glyphs
     *
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = TRANSLATION_PAINTING)
    public void toggleTranslations (ActionEvent e)
    {
    }

    //~ Inner Interfaces ---------------------------------------------------------------------------
    //--------//
    // Holder //
    //--------//
    private static interface Holder
    {
        //~ Static fields/initializers -------------------------------------------------------------

        public static final ViewParameters INSTANCE = new ViewParameters();
    }

    //~ Inner Classes ------------------------------------------------------------------------------
    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
            extends ConstantSet
    {
        //~ Instance fields ------------------------------------------------------------------------

        private final Constant.Boolean invalidSheetDisplay = new Constant.Boolean(
                true,
                "Should the invalid sheets be displayed");

        private final Constant.Boolean letterBoxPainting = new Constant.Boolean(
                true,
                "Should the letter boxes be painted");

        private final Constant.Boolean linePainting = new Constant.Boolean(
                false,
                "Should the stick lines be painted");

        private final Constant.Boolean attachmentPainting = new Constant.Boolean(
                false,
                "Should the staff & glyph attachments be painted");

        private final Constant.Boolean translationPainting = new Constant.Boolean(
                true,
                "Should the glyph translations links be painted");

        private final Constant.Boolean sentencePainting = new Constant.Boolean(
                true,
                "Should the sentence words links be painted");
    }
}