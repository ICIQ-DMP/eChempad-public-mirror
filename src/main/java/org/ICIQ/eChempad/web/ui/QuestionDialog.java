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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class QuestionDialog extends SelectorComposer<Window> {
	
	private static final long serialVersionUID = 1L;

	@Wire Window questionDialog;
	
	@Wire Row messageRow;
	
	@Wire Row questionRow;
	
	@Wire Cell messageCell;
	
	@Wire Cell questionCell;
	
	@Wire Button yesBtn;
	
	@Wire Button noBtn;
		
	private EventQueue<Event> genericQueue = null;	
	private Object parameters = null;
	private String yesEvent;
	private String noEvent;
	private String closeEvent = null;
	
	@Listen("onClick=#closeBtn")
	public void onCloseBtnClick() {
		questionDialog.detach();
	}

	public void setTitle(String title) {
		questionDialog.setTitle(title);	
	}
	
	public void setMessage(Object message) {
		if(message == null)
			messageRow.setVisible(false);
		else{			
			if(message instanceof String)
				messageCell.appendChild(new Label((String)message));
			else if(message instanceof Html)
				messageCell.appendChild((Html)message);	
			messageRow.setVisible(true);
		}
	}
		
	public void setQuestion(Object question) {
		if(question == null)
			questionRow.setVisible(false);
		else {
			if(question instanceof String)
				questionCell.appendChild(new Label((String)question));
			else if(question instanceof Html)
				questionCell.appendChild((Html)question);
			questionRow.setVisible(true);
		}	
	}
	
	public Object getParameters() {
		return parameters;
	}

	public void setContent(Object message, Object question, String yesButtonText, String noButtonText) {
		setMessage(message);
		setQuestion(question);
		if(yesButtonText != null && !yesButtonText.isEmpty())
			yesBtn.setLabel(yesButtonText);
		if(noButtonText != null && !noButtonText.isEmpty())
			noBtn.setLabel(noButtonText);		
	}	
	
	public void setCloseEvent(String eventName) {
		closeEvent = eventName;		
	}
	
	public void configEventQueue(String queueName, String yesEvent, String noEvent) {
		configEventQueue(queueName, yesEvent, noEvent, null);
	}
	
	public void configEventQueue(String queueName, String yesEvent, String noEvent, String closeEvent) {
		if(queueName != null)
			genericQueue =  EventQueues.lookup(queueName, EventQueues.DESKTOP, true);
		this.yesEvent = yesEvent; 
		this.noEvent = noEvent;
		this.closeEvent = closeEvent;
	}
	
	@Listen("onClick=#yesBtn")
	public void okBtnClick() throws Exception{
		questionDialog.setAttribute("result", "yes");
		if(yesEvent != null)
			genericQueue.publish(new Event(yesEvent, null, parameters));		
		clear();		
	}
	
	@Listen("onClick=#noBtn")
	public void cancelBtnClick() throws Exception{
		questionDialog.setAttribute("result", "no");
		if(noEvent != null)
			genericQueue.publish(new Event(noEvent, null, parameters));
		clear();
	}
	
	@Listen("onClose=#questionDialog")
	public void closeDialog() {
		if(closeEvent != null)
			genericQueue.publish(new Event(closeEvent, null, parameters));
	}
	
	private void clear() {
		questionDialog.detach();		
	}

	public void setParameters(Object parameters) {
		this.parameters = parameters;
	}
}