//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                        B a s i c S t u b                                       //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//  Copyright © Hervé Bitteur and others 2000-2014. All rights reserved.
//  This software is released under the GNU General Public License.
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package omr.sheet;

import omr.OMR;

import omr.image.FilterDescriptor;

import omr.sheet.ui.SheetAssembly;
import omr.sheet.ui.StubsController;

import omr.step.Step;
import omr.step.StepException;

import omr.ui.Colors;

import omr.util.Jaxb;
import omr.util.LiveParam;
import omr.util.Navigable;
import omr.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import javax.annotation.PostConstruct;
import javax.swing.SwingUtilities;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Class {@code BasicStub} is the implementation of SheetStub.
 *
 * @author Hervé Bitteur
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BasicStub
        implements SheetStub
{
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(
            BasicStub.class);

    //~ Instance fields ----------------------------------------------------------------------------
    //
    // Persistent data
    //----------------
    //
    /** Index of sheet, counted from 1, in the image file. */
    @XmlAttribute(name = "number")
    private final int number;

    /** All steps already performed on this sheet. */
    @XmlList
    @XmlElement(name = "steps")
    private final EnumSet<Step> doneSteps = EnumSet.noneOf(Step.class);

    /** Indicate a sheet that contains no music. */
    @XmlElement
    private Jaxb.True invalid;

    // Transient data
    //---------------
    //
    /** Containing book. */
    @Navigable(false)
    private Book book;

    /** Full sheet material, if any. */
    private Sheet sheet;

    /** The step being performed on the sheet. */
    private Step currentStep;

    /** Has sheet been modified, WRT its project data. */
    private boolean modified = false;

    /** Related assembly instance, if any. */
    private SheetAssembly assembly;

    /** Param for pixel filter. */
    private LiveParam<FilterDescriptor> filterContext;

    /** Param for text language. */
    private LiveParam<String> textContext;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new {@code SheetStub} object.
     *
     * @param book   the containing book instance
     * @param number the 1-based sheet number within the containing book
     */
    public BasicStub (Book book,
                      int number)
    {
        this.number = number;

        initTransients(book);
    }

    /**
     * No-arg constructor meant for JAXB.
     */
    private BasicStub ()
    {
        this.number = 0;
    }

    //~ Methods ------------------------------------------------------------------------------------
    //------//
    // done //
    //------//
    /**
     * Remember that the provided step has been completed on the sheet.
     *
     * @param step the provided step
     */
    public final void done (Step step)
    {
        doneSteps.add(step);
    }

    //-------//
    // close //
    //-------//
    @Override
    public void close ()
    {
        // If no stub is left, force book closing
        if (!book.isClosing()) {
            if (!book.getStubs().isEmpty()) {
                logger.info("{}Sheet closed", getLogPrefix());
            } else {
                book.close();
            }
        }
    }

    //-------------//
    // createSheet //
    //-------------//
    @Override
    public Sheet createSheet ()
    {
        try {
            sheet = new BasicSheet(this, null);
        } catch (StepException ex) {
            logger.warn("Error creating blank sheet", ex);
        }

        return sheet;
    }

    //------------//
    // ensureStep //
    //------------//
    @Override
    public boolean ensureStep (Step step)
    {
        if (!isDone(step)) {
            return getSheet().doStep(step, null);
        }

        return true;
    }

    //-------------//
    // getAssembly //
    //-------------//
    @Override
    public SheetAssembly getAssembly ()
    {
        return assembly;
    }

    //---------//
    // getBook //
    //---------//
    @Override
    public Book getBook ()
    {
        return book;
    }

    //----------------//
    // getCurrentStep //
    //----------------//
    @Override
    public Step getCurrentStep ()
    {
        return currentStep;
    }

    //----------------//
    // getFilterParam //
    //----------------//
    @Override
    public LiveParam<FilterDescriptor> getFilterParam ()
    {
        return filterContext;
    }

    //-------//
    // getId //
    //-------//
    @Override
    public String getId ()
    {
        if (book.isMultiSheet()) {
            return book.getRadix() + "#" + number;
        } else {
            return book.getRadix();
        }
    }

    //------------------//
    // getLanguageParam //
    //------------------//
    @Override
    public LiveParam<String> getLanguageParam ()
    {
        return textContext;
    }

    //---------------//
    // getLatestStep //
    //---------------//
    @Override
    public Step getLatestStep ()
    {
        Step latest = null;

        for (Step step : Step.values()) {
            if (isDone(step)) {
                latest = step;
            }
        }

        return latest;
    }

    //--------------//
    // getLogPrefix //
    //--------------//
    @Override
    public String getLogPrefix ()
    {
        if (BookManager.isMultiBook()) {
            return "[" + getId() + "] ";
        } else if (book.isMultiSheet()) {
            return "[#" + getNumber() + "] ";
        } else {
            return "";
        }
    }

    //-----------//
    // getNumber //
    //-----------//
    @Override
    public int getNumber ()
    {
        return number;
    }

    //----------//
    // getSheet //
    //----------//
    @Override
    public Sheet getSheet ()
    {
        if (sheet == null) {
            // Actually load the sheet
            if (!isDone(Step.LOAD)) {
                // LOAD not yet performed: load from book image file
                try {
                    sheet = new BasicSheet(this, null);
                } catch (StepException ignored) {
                    logger.info("Could not load sheet for stub {}", this);
                }
            } else {
                // LOAD already performed: load from project file
                StopWatch watch = new StopWatch("Load Sheet " + this);

                try {
                    watch.start("unmarshal");

                    // Open the project file system
                    Path sheetFile = book.openSheetFolder(number).resolve(
                            BasicSheet.getSheetFileName(number));
                    InputStream is = Files.newInputStream(sheetFile, StandardOpenOption.READ);
                    sheet = BasicSheet.unmarshal(is);

                    // Close the stream as well as the project file system
                    is.close();
                    sheetFile.getFileSystem().close();

                    // Complete sheet reload
                    watch.start("afterReload");
                    sheet.afterReload(this);
                    logger.info("Loaded {}", sheetFile);
                    watch.print();
                } catch (Exception ex) {
                    logger.warn("Error in loading sheet structure " + ex, ex);
                }
            }
        }

        return sheet;
    }

    //----------//
    // hasSheet //
    //----------//
    @Override
    public boolean hasSheet ()
    {
        return sheet != null;
    }

    //------------//
    // invalidate //
    //------------//
    @Override
    public void invalidate ()
    {
        invalid = Jaxb.TRUE;
        setModified(true);

        if (OMR.getGui() != null) {
            StubsController.getInstance().markTab(this, Colors.SHEET_INVALID);
        }

        logger.info("Sheet {} flagged as invalid.", getId());
    }

    //--------//
    // isDone //
    //--------//
    @Override
    public boolean isDone (Step step)
    {
        return doneSteps.contains(step);
    }

    //------------//
    // isModified //
    //------------//
    @Override
    public boolean isModified ()
    {
        return modified;
    }

    //---------//
    // isValid //
    //---------//
    @Override
    public boolean isValid ()
    {
        return invalid == null;
    }

    //-------//
    // reset //
    //-------//
    @Override
    public void reset ()
    {
        doneSteps.clear();
        invalid = null;
        sheet = null;
        currentStep = null;

        if (assembly != null) {
            assembly.reset();
        }

        setModified(true);
        logger.info("Sheet {} reset as valid.", getId());

        if (OMR.getGui() != null) {
            SwingUtilities.invokeLater(
                    new Runnable()
            {
                @Override
                public void run ()
                {
                    StubsController.getInstance().stateChanged(null);
                }
            });
        }
    }

    //----------------//
    // setCurrentStep //
    //----------------//
    @Override
    public void setCurrentStep (Step step)
    {
        currentStep = step;
    }

    //-------------//
    // setModified //
    //-------------//
    @Override
    public void setModified (boolean modified)
    {
        this.modified = modified;

        if (modified) {
            book.setModified(true);
        }
    }

    //------------//
    // storeSheet //
    //------------//
    @Override
    public void storeSheet ()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //-----------//
    // swapSheet //
    //-----------//
    @Override
    public void swapSheet ()
    {
        if (isModified()) {
            logger.info("{} storing", book);
            book.store();
        }

        if (sheet != null) {
            logger.info("{} disposed", sheet);
            sheet = null;
        }

        if (OMR.getGui() != null) {
            SwingUtilities.invokeLater(
                    new Runnable()
            {
                @Override
                public void run ()
                {
                    // Gray out the related tab
                    StubsController ctrl = StubsController.getInstance();
                    ctrl.markTab(BasicStub.this, Color.LIGHT_GRAY);

                    // Close stub UI, if any
                    if (assembly != null) {
                        ///ctrl.deleteAssembly(BasicStub.this);
                        assembly.reset();
                        assembly.close();
                    }
                }
            });
        }
    }

    //----------//
    // toString //
    //----------//
    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{#").append(number);
        sb.append('}');

        return sb.toString();
    }

    //----------------//
    // afterUnmarshal //
    //----------------//
    /**
     * Called after all the properties (except IDREF) are unmarshalled
     * for this object, but before this object is set to the parent object.
     * All non-persistent members are null.
     */
    @PostConstruct // Don't remove this method, invoked by JAXB through reflection

    private void afterUnmarshal (Unmarshaller um,
                                 Object parent)
    {
        initTransients((Book) parent);
    }

    //----------------//
    // initTransients //
    //----------------//
    /**
     * Initialize needed transient members.
     * (which by definition have not been set by the unmarshalling).
     *
     * @param book the containing book
     */
    private void initTransients (Book book)
    {
        logger.debug("{} initTransients", this);
        this.book = book;

        filterContext = new LiveParam<FilterDescriptor>(book.getFilterParam());
        textContext = new LiveParam<String>(book.getLanguageParam());

        if (OMR.getGui() != null) {
            assembly = new SheetAssembly(this);
        }
    }

    //~ Inner Classes ------------------------------------------------------------------------------
    //---------//
    // Adapter //
    //---------//
    /**
     * Meant for JAXB handling of SheetStub interface.
     */
    public static class Adapter
            extends XmlAdapter<BasicStub, SheetStub>
    {
        //~ Methods --------------------------------------------------------------------------------

        @Override
        public BasicStub marshal (SheetStub s)
        {
            return (BasicStub) s;
        }

        @Override
        public SheetStub unmarshal (BasicStub s)
        {
            return s;
        }
    }
}