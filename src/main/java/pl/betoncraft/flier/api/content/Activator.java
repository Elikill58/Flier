/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package pl.betoncraft.flier.api.content;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Named;
import pl.betoncraft.flier.api.core.Owned;

/**
 * Represents a condition, under which a Usage can be activated.
 *
 * @author Jakub Sapalski
 */
public interface Activator extends Named, Owned {

	/**
	 * @param player
	 *            the player to check
	 * @param item
	 *            the item, from which this Activator originates
	 * 
	 * @return whenever the Activator is active for this player
	 */
	public boolean isActive(InGamePlayer player, InGamePlayer source);
	
	/**
	 * Applies passed modification to this Activator.
	 * 
	 * @param mod
	 */
	public void addModification(Modification mod);

	/**
	 * Removes passed modification from this Activator.
	 * 
	 * @param mod
	 */
	public void removeModification(Modification mod);

}
