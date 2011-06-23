//----------------------------------------------------------------------------//
//                                                                            //
//                           S y s t e m F r a m e                            //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright (C) Herve Bitteur 2000-2010. All rights reserved.               //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.sheet.grid;

import omr.log.Logger;

import omr.util.HorizontalSide;

import java.util.List;

/**
 * Class {@code SystemFrame} handles physical information about a system
 *
 * @author Hervé Bitteur
 */
public class SystemFrame
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(SystemFrame.class);

    //~ Instance fields --------------------------------------------------------

    /** System id */
    private final int id;

    /** Range of system staves */
    private List<StaffInfo> staves;

    /** Bar line that defines the system left side */
    private BarInfo leftBar;

    //~ Constructors -----------------------------------------------------------

    //-------------//
    // SystemFrame //
    //-------------//
    /**
     * Creates a new SystemFrame object.
     *
     * @param id the system id
     * @param staves the sequence of staves for this system
     */
    public SystemFrame (int             id,
                        List<StaffInfo> staves)
    {
        this.id = id;
        this.staves = staves;
    }

    //~ Methods ----------------------------------------------------------------

    //---------------//
    // getFirstStaff //
    //---------------//
    /**
     * @return the first staff
     */
    public StaffInfo getFirstStaff ()
    {
        return staves.get(0);
    }

    //-------//
    // getId //
    //-------//
    /**
     * @return the id
     */
    public int getId ()
    {
        return id;
    }

    //--------------//
    // getLastStaff //
    //--------------//
    /**
     * @return the lastStaff
     */
    public StaffInfo getLastStaff ()
    {
        return staves.get(staves.size() - 1);
    }

    //------------//
    // getLeftBar //
    //------------//
    /**
     * Determine the precise physical info about the barline that defines this
     * system left side
     */
    public BarInfo getLeftBar ()
    {
        if (leftBar == null) {
            // Use the common part(s) of the contained staves BarInfo's
            BarInfo sysBar = null;

            for (StaffInfo staff : staves) {
                BarInfo staffBar = staff.getBar(HorizontalSide.LEFT);

                if (sysBar == null) {
                    sysBar = staffBar;
                } else {
                    // Keep only the common items
                    sysBar.getSticks()
                          .retainAll(staffBar.getSticks());
                }
            }

            leftBar = sysBar;
        }

        return leftBar;
    }

    //-----------//
    // setStaves //
    //-----------//
    /**
     * @param staves the range of staves
     */
    public void setStaves (List<StaffInfo> staves)
    {
        this.staves = staves;
    }

    //-----------//
    // getStaves //
    //-----------//
    /**
     * @return the range of staves
     */
    public List<StaffInfo> getStaves ()
    {
        return staves;
    }

    //----------//
    // toString //
    //----------//
    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder("{");
        sb.append(getClass().getSimpleName());
        sb.append("#")
          .append(id);
        sb.append(" firstStaff:")
          .append(getFirstStaff().getId());
        sb.append(" lastStaff:")
          .append(getLastStaff().getId());

        if (leftBar != null) {
            sb.append(" leftBar:")
              .append(leftBar);
        }

        sb.append("}");

        return sb.toString();
    }
}