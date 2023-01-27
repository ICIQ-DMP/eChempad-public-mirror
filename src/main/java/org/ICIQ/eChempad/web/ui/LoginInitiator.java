/**
 * Create module - Create module inside the ioChem-BD software.
 * Copyright © 2014 ioChem-BD (contact@iochem-bd.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.web.ui;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Map;

/**
 * This class will implement user authorization before accessing zul pages.
 * This is necessary because "Browse only" users have a valid CAS session ticket but access is forbidden in Create module. 
 * @author malvarez
 *
 */
public class LoginInitiator implements Initiator {

	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {	
		//if(!ShiroManager.isValidSubject()){
    		Execution exec = Executions.getCurrent();
    		if(exec != null){
    			HttpServletResponse response = (HttpServletResponse)exec.getNativeResponse();    		
    			URL reconstructedURL = new URL(Executions.getCurrent().getScheme(),
        								   Executions.getCurrent().getServerName(),
        								   Executions.getCurrent().getServerPort(),
        								   Executions.getCurrent().getContextPath());
    			response.sendRedirect(response.encodeRedirectURL(reconstructedURL.toString().concat("/zul/errors/notAuthAccount.zul")));
    			exec.setVoided(true);
    		}else{
    			Executions.sendRedirect("/zul/errors/notAuthAccount.zul");
    		}
    	//}
	}

}
