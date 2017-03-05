/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import pl.betoncraft.flier.api.core.Damager;

/**
 * Represents an action which attacks another player with a Damager.
 *
 * @author Jakub Sapalski
 */
public interface Attack extends Action, Damager {
}