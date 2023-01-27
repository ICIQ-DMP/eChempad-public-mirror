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

import org.ICIQ.eChempad.web.definitions.Constants.ScreenSize;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ReportManager extends SelectorComposer<Window> {

	private EventQueue<Event> displayQueue = null;
	EventQueue<Event> reportManagementQueue = null;
	
	private static final Logger logger = LogManager.getLogger(ReportManager.class.getName());
	private static final long serialVersionUID = 1L; 
	     
	@Wire
	Tree reportTree;
	
	@Wire
	Treechildren reportTreeChildren;
	
	@Wire 
	Menupopup newReportPopup;  
	
	@Wire
	Menuitem editReportOpenItem;
	
	@Wire 
	Menuitem editReportRemoveItem;	
	
	@Wire 
	Textbox searchTxt;
	
	@Wire
	Button repClearBtn;
	
	@Wire 
	Menupopup editReportPopup;
	
	@Wire 
	Treecol descriptionTreecol;
	
	@Wire
    Treecol typeTreecol;
	
	@Wire
	Treecol creationDateTreecol;
	
	@Wire
	Treecol publishedTreecol;
	
	@Listen("onChanging=#searchTxt")
	public void onSearchTxtChange(InputEvent e) {		
		// filterReportTree(e.getValue());
	}
	
	@Listen("onClick=#editReportOpenItem") 
	public void onEditReportOpenItemClick(){		
		Treeitem treeItem = reportTree.getSelectedItem();
		Treecell cell = (Treecell)treeItem.getFirstChild().getFirstChild();
		int reportId = Integer.valueOf(cell.getLabel());
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reportId", reportId);					
		reportManagementQueue.publish(new Event("openReport", null, parameters));
	}
	
	@Listen("onClick=#editReportRemoveItem") 
	public void onEditReportRemoveItemClick(){
		Treeitem treeItem = reportTree.getSelectedItem();
		Treecell cell = (Treecell)treeItem.getFirstChild().getFirstChild();
		int reportId = Integer.valueOf(cell.getLabel());
		Messagebox.show("Are you sure you want to remove this report?", "Remove report", Messagebox.YES+Messagebox.NO, Messagebox.QUESTION, new DeleteReportEventListener(reportId));		
	}

	
	@Listen("onClick=#repClearBtn")
	public void onRepClearBtnClick(){
		// clearFilterReportTree();
	}
	
	
	
	public void doAfterCompose(Window window) {      			
		try {				
			super.doAfterCompose(window);
			loadReportTypes();
			loadUserReports();
			initActionQueues();
			setupLayout((ScreenSize) Executions.getCurrent().getDesktop().getAttribute("display"));
		} catch (Exception e) {				
			e.printStackTrace();
		}					
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadReportTypes(){
		/*
		List<ReportType> reportTypes = ReportTypeService.getActiveReportTypes();
		for(ReportType reportType : reportTypes){			
			Menuitem menuitem = new Menuitem();
			menuitem.setLabel(reportType.getName());
			menuitem.setAttribute("reportType", reportType);			
			menuitem.addEventListener("onClick", new EventListener(){
				@Override
				public void onEvent(Event event) throws Exception {
					Menuitem menuitem = (Menuitem)event.getTarget();
					int reportId = ((ReportType)menuitem.getAttribute("reportType")).getId();
					createNewReport(reportId, false);						
				}
			});
			newReportPopup.appendChild(menuitem);
		}
		*/

	}

	private void loadUserReports() throws Exception{
		/*
		clearReportTree();
		List<Report> userReports = ReportService.getUserReports(ShiroManager.getCurrent().getUserId());
		for(Report report : userReports)
			reportTreeChildren.appendChild(renderReportToTreeitem(report));

		 */
	}

	/*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Treeitem renderReportToTreeitem(Report report) {
		/*
		Treeitem item = new Treeitem();
		Treerow row = new Treerow();			
		Treecell publishedCell = new Treecell();
		Checkbox publishedChk = new Checkbox();
		publishedChk.setChecked(report.isPublished());
		publishedChk.setDisabled(true);
		publishedCell.appendChild(publishedChk);
		
		row.appendChild(new Treecell(String.valueOf(report.getId())));
		row.appendChild(new Treecell(report.getName()));
		row.appendChild(new Treecell(report.getDescription()));
		row.appendChild(new Treecell(report.getType().getName()));
		row.appendChild(new Treecell(new SimpleDateFormat("YYYY-MM-dd").format(report.getCreationDate()).toString()));
		row.appendChild(publishedCell);
		row.setParent(item);			
		item.appendChild(row);
		item.setContext(editReportPopup);
		item.addEventListener("onDoubleClick", new EventListener(){
			 public void onEvent(Event event) throws Exception {
				 Treeitem item = (Treeitem)event.getTarget();
				 Treerow row = (Treerow) item.getFirstChild();
				 int id = Integer.parseInt(((Treecell)row.getChildren().get(0)).getLabel());				 
				 HashMap<String, Object> parameters = new HashMap<String, Object>();
				 parameters.put("reportId", id);
           		 reportManagementQueue.publish(new Event("openReport",null,parameters));
     	   }
		});
		return item;


	}
	*/

	
	private void clearReportTree(){
		while (reportTreeChildren.getItemCount() > 0) {
			reportTreeChildren.removeChild(reportTreeChildren.getFirstChild());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initActionQueues() {		
		reportManagementQueue = EventQueues.lookup("reportmanagement", EventQueues.DESKTOP,true);
		reportManagementQueue.subscribe(new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("refreshList")){
					loadUserReports();							
				}else if(event.getName().equals("deleteReport")){
					loadUserReports();	//Refresh list
				}else if(event.getName().equals("createreportfromselection")){
					int id = (int) event.getData();
					createNewReport(id, true);
				}
			}					
		});						
		
        displayQueue = EventQueues.lookup("display", EventQueues.DESKTOP,true);
        displayQueue.subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				switch(event.getName()) {
					case "sizeChanged":	ScreenSize size = (ScreenSize)event.getData();  
										setupLayout(size);	
				}				
			}

        });        
	}
	
	

	private void setupLayout(ScreenSize size) {		
		if(size == null)
			size = ScreenSize.X_LARGE;
		
		boolean isSmallLayout = size == ScreenSize.SMALL || size == ScreenSize.X_SMALL;
		descriptionTreecol.setVisible(!isSmallLayout);
	    typeTreecol.setVisible(!isSmallLayout);
	    creationDateTreecol.setVisible(!isSmallLayout);
	    publishedTreecol.setVisible(false);		//Not operative
	}	
	
	
	private void createNewReport(int reportTypeId, boolean appendSessionElements) throws InterruptedException{
		/*
		try {
			int reportId = ReportService.createReport(reportTypeId, ShiroManager.getCurrent().getUserId());
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("reportId", reportId);
			parameters.put("appendSessionElements", appendSessionElements);		
			if(reportId != -1)
				reportManagementQueue.publish(new Event("openReport",null,parameters));	
		}catch(Exception e) {
			
		}*/
	}
	
	protected class DeleteReportEventListener implements EventListener<Event>{
		int reportId = 0;
		
		public DeleteReportEventListener(int reportId){
			this.reportId = reportId;
		}
		
		@Override
		public void onEvent(Event e){
			/*
			if(Messagebox.ON_YES.equals(e.getName()))
				try {
					ReportService.deleteReport(reportId);
					reportManagementQueue.publish(new Event("deleteReport",null, reportId));					
				} catch (Exception e1) {
					logger.error(e1.getMessage());
				}

			 */
		}
	}
	
	private void clearFilterReportTree(){
		searchTxt.setValue("");
		Iterator<Component> iter = reportTreeChildren.getChildren().iterator();
		while(iter.hasNext())
			((Treeitem)iter.next()).setVisible(true);		
	}
	
	
	
	private void filterReportTree(String filter){
			/*
		FilterReportPredicate predicate = new FilterReportPredicate(filter);	
		Collection<Treeitem> filtered = CollectionUtils.select(reportTreeChildren.getChildren(), predicate);
		Treeitem[] filteredArray = new Treeitem[filtered.size()];
		filteredArray = filtered.toArray(filteredArray);	
		hideAllItems();
		for(int inx = 0; inx < filteredArray.length; inx++){
			Treeitem item = filteredArray[inx];
			item.setVisible(true);
			logger.info(item.toString());
		} */
	}
	
	private void hideAllItems(){
		Iterator<Component> iter = reportTreeChildren.getChildren().iterator();
		while(iter.hasNext())
			((Treeitem)iter.next()).setVisible(false);		
	}

	// I have changed the org.apache.collections.Predicate to the Predicate class that comes with Java. The
	// implementation should be equivalent
	class FilterReportPredicate implements Predicate{
		
		String regex = null;
		
		public FilterReportPredicate(String regex){
			StringBuilder sb = new StringBuilder();				
			regex = regex.replaceAll("().*?", "");
			sb.append(".*?");
			sb.append(regex);
			sb.append(".*?");
			this.regex = sb.toString();			
		}


		@Override
		public boolean test(Object o) {
			try{
				Treeitem item = (Treeitem) o;
				String name = ((Treecell)item.getFirstChild().getChildren().get(1)).getLabel();
				String description = ((Treecell)item.getFirstChild().getChildren().get(2)).getLabel();
				if(Pattern.matches(regex, name) || Pattern.matches(regex, description))
					return true;
			}catch(Exception e){}
			return false;		}
	}
}
