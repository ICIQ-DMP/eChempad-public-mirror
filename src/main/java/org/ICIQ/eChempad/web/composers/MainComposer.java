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
package org.ICIQ.eChempad.web.composers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

/**
 * This is a custom controller, which in ZK is called a composer. To implement it, extend SelectorComposer and bound
 * the type of component that you want to control with this class.
 * <p>
 * This class is used to answer all the events that are fired from the ZK UI programmatically from Java instead of
 * javascript, which is the usual way to control HTML elements.
 *
 * It also contains all the properties that we can control from the Java side annotated with the @wire annotation.
 * Modifying properties of those @wired objects will modify the properties of hte HTML components in the UI.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1.0
 */
public class MainComposer extends SelectorComposer<Window> {

	/**
	 * Returns the base URL where eChempad is currently running from. Needs to be refactored in order to increase the
	 * separation of concerns since this information should come from a class specialized in retrieving constants from
	 * the application.properties file. Furthermore, this method is static...
	 *
	 * @return String containing the URL where eChempad is currently hosted.
	 */
	public static String getBaseUrl() {
    	return "https://echempad.iciq.es:8081";
	}

	/**
	 * De-facto constructor for composer components.
	 *
	 * @param comp Window component
	 * @throws Exception If something goes wrong during initialization.
	 */
	public void doAfterCompose(Window comp) throws Exception {
		// First thing to do is execute the same method in the parent
		super.doAfterCompose(comp);

		// If not authenticated redirect to login page
		//if (! this.isUserAuthenticated()) {Executions.sendRedirect("/login");}
	}

	/**
	* Returns true if a user is authenticated and false if the authentication is anonymous.
	*
	* @return Boolean depending on the state of authentication.
	*/
	public boolean isUserAuthenticated()
	{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return !(authentication instanceof AnonymousAuthenticationToken);
	}
}


