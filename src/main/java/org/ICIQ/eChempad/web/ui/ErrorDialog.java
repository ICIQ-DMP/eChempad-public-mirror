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

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ErrorDialog extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;

	@Wire Window errorDialog;
	
	@Wire Row errorRow;
	
	@Wire Row causeRow;
	
	@Wire Row solutionRow;
	
	@Wire Row messageRow;
	
	@Wire Cell errorCell;
	
	@Wire Cell causeCell;
	
	@Wire Cell solutionCell;
	
	@Wire Textbox errorMessageTxt;
	
	@Listen("onClick=#closeBtn")
	public void onCloseBtnClick(){
		errorDialog.detach();
	}
	
	public void setTitle(String title){
		errorDialog.setTitle(title);	
	}
	
	public void setError(String errorStr){
		if(errorStr == null)
			errorRow.setVisible(false);
		else{
			errorCell.appendChild(new Label(errorStr));
			errorRow.setVisible(true);
		}
							
	}
	
	public void setError(Html errorHtml){
		if(errorHtml == null)
			errorRow.setVisible(false);
		else{
			errorCell.appendChild(errorHtml);
			errorRow.setVisible(true);
		}
	}
	
	public void setCause(String causeStr){
		if(causeStr == null)
			causeRow.setVisible(false);
		else{
			causeCell.appendChild(new Label(causeStr));
			causeRow.setVisible(true);
		}
	}
	
	public void setCause(Html causeHtml){
		if(causeHtml == null)
			causeRow.setVisible(false);
		else{
			causeCell.appendChild(causeHtml);
			causeRow.setVisible(true);
		}
	}
	
	public void setSolution(String solutionStr){
		if(solutionStr == null)
			solutionRow.setVisible(false);
		else {
			solutionCell.appendChild(new Label(solutionStr));
			solutionRow.setVisible(true);
		}	
	}

	public void setSolution(Html solutionHtml){
		if(solutionHtml == null)
			solutionRow.setVisible(false);
		else {
			solutionCell.appendChild(solutionHtml);
			solutionRow.setVisible(true);
		}
	}
	
	public void setErrorMessage(String errorMessageStr){
		if(errorMessageStr == null)
			messageRow.setVisible(false);
		else{
			errorMessageTxt.setValue(errorMessageStr);
			messageRow.setVisible(true);
		}
	}
	
	public void setContent(Html errorHtml, Html causeHtml,  Html solutionHtml, String errorMessage){
		if(errorHtml == null)
			errorRow.setVisible(false);
		else{
			errorCell.appendChild(errorHtml);
			errorRow.setVisible(true);
		}
		
		if(causeHtml == null)
			causeRow.setVisible(false);
		else{
			causeCell.appendChild(causeHtml);
			causeRow.setVisible(true);
		}
		
		if(solutionHtml == null)
			solutionRow.setVisible(false);
		else {
			solutionCell.appendChild(solutionHtml);
			solutionRow.setVisible(true);
		}
		
		if(errorMessage == null)
			messageRow.setVisible(false);
		else{
			errorMessageTxt.setValue(errorMessage);
			messageRow.setVisible(true);
		}
	}	
	
}
