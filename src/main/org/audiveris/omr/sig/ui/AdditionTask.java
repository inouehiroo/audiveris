//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                     A d d i t i o n T a s k                                    //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//
//  Copyright © Audiveris 2017. All rights reserved.
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the
//  GNU Affero General Public License as published by the Free Software Foundation, either version
//  3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this
//  program.  If not, see <http://www.gnu.org/licenses/>.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package org.audiveris.omr.sig.ui;

import java.awt.Rectangle;
import org.audiveris.omr.sig.SIGraph;
import org.audiveris.omr.sig.inter.Inter;
import org.audiveris.omr.sig.relation.Partnership;

import java.util.Collection;

/**
 * Class {@code AdditionTask} adds an Inter instance, together with its relations.
 *
 * @author Hervé Bitteur
 */
public class AdditionTask
        extends InterTask
{
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Add an inter instance with its provided relations.
     *
     * @param sig           the underlying sig
     * @param inter         the inter to add
     * @param initialBounds the initial bounds for this inter
     * @param partnerships  the provided relations around inter
     */
    public AdditionTask (SIGraph sig,
                         Inter inter,
                         Rectangle initialBounds,
                         Collection<Partnership> partnerships)
    {
        super(sig, inter, initialBounds, partnerships);
    }

    //~ Methods ------------------------------------------------------------------------------------
    @Override
    public void performDo ()
    {
        inter.setBounds(initialBounds);
        sig.addVertex(inter);

        for (Partnership partnership : partnerships) {
            partnership.applyTo(inter);
        }
    }

    @Override
    public void performRedo ()
    {
        performDo();
    }

    @Override
    public void performUndo ()
    {
        inter.remove();
    }

    @Override
    protected String actionName ()
    {
        return "add";
    }
}
