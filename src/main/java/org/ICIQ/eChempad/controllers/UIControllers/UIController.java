/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.controllers.UIControllers;

/**
 * Defines the operations that a login controller should have. These operations define URL endpoints that a client can
 * attack in order to return the corresponding ZK page, using view resolver.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1/3/2022
 */
public interface UIController {

    /**
     * Returns the needed information to access the login form.
     *
     * @return the {@code String} that needs to be put after the base URL to access the login form.
     */
    String login();

    /**
     * Returns the needed information to access the exit page.
     *
     * @return The required ZK view as a {@code String}.
     */
    String exit();

    /**
     * Returns the needed information to access the timeout page.
     *
     * @return The required ZK view as a {@code String}.
     */
    String timeout();

    /**
     * Returns the needed information to access the main page.
     *
     * @return The required ZK view as a {@code String}.
     */
    String main();

    /**
     * Returns the needed information to access the help page.
     *
     * @return The required ZK view as a {@code String}.
     */
    String help();

    /**
     * Returns the needed information to access the help page.
     *
     * @return The required ZK view as a {@code String}.
     */
    String profile();
}
