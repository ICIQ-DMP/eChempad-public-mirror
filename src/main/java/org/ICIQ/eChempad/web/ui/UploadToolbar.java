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

import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Iterator;

public class UploadToolbar extends SelectorComposer<Window> {
	
	
	@Wire
	Progressmeter uploadProgressMeter;
	
	@Wire 
	Label uploadProgressMeterLabel;
	
	@Wire 
	Popup uploadProgressMeterPopup;
	
	@Wire 
	Vlayout uploadingCalculationVlayout;
	
	@Wire
	A clearFinishedUploads;
	
	@Listen("onOpen=#uploadProgressMeterPopup")
	public void onUploadProgressMeterPopupOpen() {
		refreshProgress();
	}
	
	@Listen("onClick=#clearFinishedUploads")
	public void onClearFinishedUploadsClick(){
		clearFinishedUploads();
	}
	
	private static final long serialVersionUID = 1L;
	private EventQueue<Event> loadCalculationQueue = null;
	
	private int pendingProcesses = 0;
	private int doneProcesses = 0;
	
	private HashMap<String, Hbox> processLines = null;
	
	@SuppressWarnings("unchecked")
	public UploadToolbar(){
		loadCalculationQueue = EventQueues.lookup("calculationloading",	WebApps.getCurrent(), true);
		loadCalculationQueue.subscribe(new EventListener<Event>(){			
			public void onEvent(Event event) throws Exception {
				HashMap<String,String> params = (HashMap<String, String>) event.getData();
				if (true) { //if(params.get(CalculationInsertion.PARAM_USERNAME).equals(ShiroManager.getCurrent().getUserName())){
					if(event.getName().equals("filesQueued"))
						appendNewConversion(params);
					else if(event.getName().equals("filesConverting"))
						setFilesAsConverting(params);
					else if(event.getName().equals("filesConverted"))
						setFilesAsConverted(params);
					else if(event.getName().equals("filesSaved")) {
						setFilesAsSaved(params);
						sendUpdateTreeEvent(params);
					}
					else if(event.getName().equals("conversionFailed"))
						setFilesAsError(params);
					refreshProgress();					
				} 
			}
		});	
		processLines = new HashMap<String, Hbox>();
	}
	
	private void appendNewConversion(HashMap<String, String> params){
		/*
		pendingProcesses++;
		Hbox hbox = new Hbox();
		hbox.setStyle("width:100%");
		hbox.setAlign("center");
		Progressmeter progress = new Progressmeter(0);	
		progress.setWidth("100px");
		String uuid = params.get(CalculationInsertion.PARAM_UUID);		
		Label description = new Label(params.get("name"));
		description.setHflex("1");
		description.setSclass("overflow-ellipsis");		
		hbox.appendChild(progress);
		hbox.appendChild(description);		
		uploadingCalculationVlayout.appendChild(hbox);
		processLines.put(uuid, hbox);
		*/

	}

	private void setFilesAsConverting(HashMap<String, String> params){
		/*
		String processUUID = params.get(CalculationInsertion.PARAM_UUID);
		Hbox process = processLines.get(processUUID);
		Progressmeter progress = (Progressmeter) process.getChildren().get(0);
		progress.setValue(25);
		Label description = (Label) process.getChildren().get(1);				
		description.setValue(params.get(CalculationInsertion.PARAM_NAME));

		 */
		
	}
	
	private void setFilesAsConverted(HashMap<String, String> params){
		/*
		String processUUID = params.get(CalculationInsertion.PARAM_UUID);
		Hbox process = processLines.get(processUUID);
		Progressmeter progress = (Progressmeter) process.getChildren().get(0);
		progress.setValue(75);
		Label description = (Label) process.getChildren().get(1);	
		description.setValue(params.get(CalculationInsertion.PARAM_NAME));

		 */
		
	}
	
	private void setFilesAsSaved(HashMap<String, String> params){
		/*
		String processUUID = params.get(CalculationInsertion.PARAM_UUID);
		Hbox process = processLines.get(processUUID);
		Progressmeter progress = (Progressmeter) process.getChildren().get(0);
		progress.setValue(100);		
		Label description = (Label) process.getChildren().get(1);	
		description.setValue(params.get(CalculationInsertion.PARAM_NAME));		
		pendingProcesses--;
		doneProcesses++;

		 */
	}
	
	private void sendUpdateTreeEvent(HashMap<String, String> params) {
		// TreeEvent.sendEventToUserQueue(new Event("calculationAdded", null, params.get(CalculationInsertion.PARAM_CALC_ID)));
	}

	private void setFilesAsError(HashMap<String,String> params){
		/* String processUUID = params.get(CalculationInsertion.PARAM_UUID);
		Hbox process = processLines.get(processUUID);
		Progressmeter progress = (Progressmeter) process.getChildren().get(0);
		progress.setStyle("border: 1px solid rgba(228, 23, 23, 1)");
		progress.setValue(100);				
		Label description = (Label) process.getChildren().get(1);
		String name = description.getValue().replaceAll("\\d+%\\s*","");		
		description.setValue(name);
		
		Popup popup = new Popup();		
		Div errorIcon = new Div();
		errorIcon.setSclass("z-notification-icon");
		// errorIcon.setAttribute("errorDescription", params.get(CalculationInsertion.PARAM_EXCEPTION_MESSAGE));
		errorIcon.addEventListener("onClick", new EventListener<Event>(){
			@Override
			public void onEvent(Event event) throws Exception {
				Div errorIcon = (Div) event.getTarget();
				String errorDescription = (String)errorIcon.getAttribute("errorDescription");
				Clients.showNotification(errorDescription, "warning", null,null,3000);				
			}			
		});
		errorIcon.setStyle("cursor: pointer");		
		errorIcon.setWidth("16px");
		errorIcon.setHeight("16px");
		errorIcon.setPopup(popup);
		process.appendChild(errorIcon);
		pendingProcesses--;
		doneProcesses++; */
		
	}
	private void refreshProgress(){
		refreshProgressBar();
		refreshPopupBars();	
	}
	
	private void refreshProgressBar() {
		if(pendingProcesses == 0 && doneProcesses == 0){
			uploadProgressMeter.setVisible(false);
			uploadProgressMeterLabel.setVisible(false);
			uploadProgressMeterPopup.close();
			return;
		}
		else{
			uploadProgressMeter.setVisible(true);
			uploadProgressMeterLabel.setVisible(true);
		}
		float totalProcesses = pendingProcesses + doneProcesses;
		int currentPercentage = Math.round((doneProcesses / totalProcesses)*100);
		if(currentPercentage >100 || currentPercentage < 0)
			uploadProgressMeter.setValue(100);
		else
			uploadProgressMeter.setValue(currentPercentage);
		uploadProgressMeterLabel.setValue(String.valueOf(doneProcesses) + "/" + String.valueOf(Math.round(totalProcesses)));
		uploadingCalculationVlayout.invalidate();
		
	}
	
	
	private void refreshPopupBars() {		
		for(String processUUID : processLines.keySet()) {
			Hbox process = processLines.get(processUUID);
			Progressmeter progress = (Progressmeter) process.getChildren().get(0);
			progress.invalidate();
		}
	}
	
	

	private void clearFinishedUploads(){
		Iterator<String> iter = processLines.keySet().iterator();
		while(iter.hasNext()){
			String processUUID = iter.next();
			Hbox processLine = processLines.get(processUUID);
			Progressmeter progress = (Progressmeter) processLine.getChildren().get(0);			
			if(progress.getValue() == 100){
				iter.remove();
				uploadingCalculationVlayout.removeChild(processLine);
			}
		}
		doneProcesses = 0;
		refreshProgress();
	}
		
}
