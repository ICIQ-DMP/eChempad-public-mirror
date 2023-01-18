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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class Search extends SelectorComposer<Window>{

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LogManager.getLogger(Search.class.getName());
	private EventQueue<Event> navigationQueue			= null;
	private EventQueue<Event> addUsersQueue 			= null;
	private EventQueue<Event> addGroupsQueue 			= null;
	
	private static enum SearchType { PROJECT_AND_CALCULATIONS, PROJECTS, CALCULATIONS}; 
	
	private final static String[] AVAILABLE_FILTERS = {"name", "description", "type", "path", "owner", "group", "creationDate", "conceptGroup"};
	private static final HashMap<SearchType, HashMap<String,String>> textFilterAssociatedQuery;
	private static final HashMap<SearchType, HashMap<String,String>> groupFilterAssociatedQuery;
	private static final HashMap<SearchType, HashMap<String, String>> rangeFilterAssociatedQuery;
	
	private SearchType searchType = SearchType.PROJECT_AND_CALCULATIONS;	//Current search type
	private int resultsPerPage = 0;

	static {		
		textFilterAssociatedQuery = new HashMap<>();
		groupFilterAssociatedQuery = new HashMap<>();
		rangeFilterAssociatedQuery = new HashMap<>();
		
		//Define text field SQLs for each search type  
		HashMap<String, String> projectsAndCalculations = new HashMap<>();
		projectsAndCalculations.put("name", "((lower(projects.name) like '%?%') OR (lower(calculations.name) like '%?%'))");
		projectsAndCalculations.put("description", "((lower(projects.description) like '%?%') OR (lower(calculations.description) like '%?%'))");
		projectsAndCalculations.put("path", "((lower(projects.path) like '%?%') OR (lower(calculations.path) like '%?%'))");
		projectsAndCalculations.put("type", "calculations.type_id = ?");
		projectsAndCalculations.put("conceptGroup", "projects.concept_group = '?'");
		
		HashMap<String, String> projects = new HashMap<>();
		projects.put("name", "lower(projects.name) like '%?%'");
		projects.put("description", "lower(projects.description) like '%?%'");
		projects.put("path", "lower(projects.path) like '%?%'");
		projects.put("type", "calculations.type_id = ?");
		projects.put("conceptGroup", "projects.concept_group = '?'");
		
		HashMap<String, String> calculations = new HashMap<>();
		calculations.put("name", "lower(calculations.name) like '%?%'");
		calculations.put("description", "lower(calculations.description) like '%?%'");
		calculations.put("path", "lower(calculations.path) like '%?%'");
		calculations.put("type", "calculations.type_id = ?");
		calculations.put("conceptGroup", "projects.concept_group = '?'");
		
		textFilterAssociatedQuery.put(SearchType.PROJECT_AND_CALCULATIONS, projectsAndCalculations);
		textFilterAssociatedQuery.put(SearchType.PROJECTS, projects);
		textFilterAssociatedQuery.put(SearchType.CALCULATIONS, calculations);
		
		//Define groupbox field SQLs for each search type		
		HashMap<String, String> allTypes = new HashMap<>();
		allTypes.put("owner", "projects.owner_user_id in (?)");
		allTypes.put("group", "projects.owner_group_id in (?)");
		
		groupFilterAssociatedQuery.put(SearchType.PROJECT_AND_CALCULATIONS, allTypes);	
		groupFilterAssociatedQuery.put(SearchType.PROJECTS, allTypes);
		groupFilterAssociatedQuery.put(SearchType.CALCULATIONS, allTypes);
		
		//Define date range field SQLs for each search type		
		projectsAndCalculations = new HashMap<>();
		projectsAndCalculations.put("creationDate_start", "(projects.creation_time > '?' OR calculations.creation_time > '?')");
		projectsAndCalculations.put("creationDate_start_end", "(projects.creation_time BETWEEN '?' AND '#' OR calculations.creation_time BETWEEN '?' AND '#')");
		projectsAndCalculations.put("creationDate_end", "(projects.creation_time < '?' OR calculations.creation_time < '?')");
		
		projects = new HashMap<>();
		projects.put("creationDate_start", "projects.creation_time > '?' ");
		projects.put("creationDate_start_end", "projects.creation_time BETWEEN '?' AND '#'");
		projects.put("creationDate_end", "projects.creation_time < '?' ");
		
		calculations = new HashMap<>();
		calculations.put("creationDate_start", "calculations.creation_time > '?'");
		calculations.put("creationDate_start_end", "calculations.creation_time BETWEEN '?' AND '#'");
		calculations.put("creationDate_end", "calculations.creation_time < '?'");
		
		rangeFilterAssociatedQuery.put(SearchType.PROJECT_AND_CALCULATIONS, projectsAndCalculations);
		rangeFilterAssociatedQuery.put(SearchType.PROJECTS, projects);
		rangeFilterAssociatedQuery.put(SearchType.CALCULATIONS, calculations);
	}
	
	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;
	
	//Search form
	@Wire 
	Div searchDiv;
	@Wire
	Button searchBtn;
    @Wire
    Listbox type;
	@Wire 
	Groupbox owner;
	@Wire 
	Groupbox group;    
	//Results form
	@Wire
	Button resetSearchBtn;
	@Wire
	Div resultsDiv;
	@Wire 
	Tree searchTree;
	@Wire
    Treechildren searchTreeChildren;
    @Wire 
    Popup thumbnailPopup;
    
    
	@Listen("onAfterSize=#searchWindow")
	public void afterSize(AfterSizeEvent e) throws InterruptedException{
		resultsPerPage = Math.round( (e.getHeight() - 150) / 24);
		if(resultsPerPage < 5) 
			resultsPerPage = 30;			
		searchTree.setPageSize(resultsPerPage);
		searchTree.setRows(resultsPerPage);					
	}
    	
	@Listen("onClick=#searchBtn")
	public void onSearchBtnClick() throws InterruptedException, Exception{
		/* String query = buildSearchQuery();
		if(query != null)
		    doSearch(query); */
	}
   
	@Listen("onClick=#resetSearchBtn")
	public void onResetSearchBtnClick(){
		resetSearch();
	}
	
	@Listen("onOpen=#thumbnailPopup")
	public void showThumbnail(Event e) throws MalformedURLException{
		/*
		URL reconstructedURL = new URL(Executions.getCurrent().getScheme(),Executions.getCurrent().getServerName(),Executions.getCurrent().getServerPort(),Executions.getCurrent().getContextPath());
		Treerow row = (Treerow)((OpenEvent) e).getReference();
		if(row != null){
			Entity dc = (Entity) row.getAttribute("properties");										
			((Html)thumbnailPopup.getChildren().get(0)).setContent("<img style='width:200px;height:200px' src='" + reconstructedURL.toString() +"/innerServices/getfile?id=" + dc.getId() + "&file=" + CalculationInsertion.THUMBNAIL_FILE_NAME + "' alt='No image available'></image>");				
		} */
	}
	
	@Listen("onClick=#ownerBtn")
	public void onOwnerBtnClick(){
		Window window = (Window)Executions.createComponents("/zul/main/search/selectUser.zul", null, null);
        window.doModal();
        Events.postEvent(Events.ON_OPEN, window, null);
	}

	@Listen("onClick=#groupBtn")
	public void onGroupBtnClick(){
		Window window = (Window)Executions.createComponents("/zul/main/search/selectGroup.zul", null, null);
        window.doModal();
        Events.postEvent(Events.ON_OPEN, window, null);
	}
	
    @SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		/*
		super.doAfterCompose(comp);
		initActionQueues();
		fillTypeListbox();
		searchTree.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {				
				Treeitem item = ((Tree)event.getTarget()).getSelectedItem();
				Entity dc = (Entity)item.getAttribute("properties");
				HashSet<Entity> selectedElements = new HashSet<Entity>();
				selectedElements.add(dc.isProject()? 
							ProjectService.getById(dc.getId()): 
								CalculationService.getById(dc.getId()));				
				_desktopScope.put("selectedSearchElements", selectedElements); 
				navigationQueue.publish(new Event("displaySearchElement"));				
			}			
		}); */
	}
     
    @SuppressWarnings("unchecked")
	private void initActionQueues(){    	
    	navigationQueue = EventQueues.lookup("navigation", 	EventQueues.DESKTOP, true);    	
     	addUsersQueue = EventQueues.lookup("searchaddusers", 	EventQueues.DESKTOP, true);
     	addGroupsQueue = EventQueues.lookup("searchaddgroups", 	EventQueues.DESKTOP, true);
     	
     	navigationQueue.subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("resetSearch")) {
					resetSearch();
				}				
			}
     		
     	});
     	     	
     	addUsersQueue.subscribe(new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				String newOwner = (String)event.getData();
				owner.setVisible(true);
				// addOwner(newOwner, ShiroManager.getCurrent().getUserIdByName(newOwner));
			}
     	});
     	
     	addGroupsQueue.subscribe(new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				String newGroup = (String)event.getData();
				group.setVisible(true);
				// addGroup(newGroup, ShiroManager.getCurrent().getGroupIdByName(newGroup));
			}
     		
     	});
     	
    }
    
    private void addOwner(String newOwner, int newOwnerId){
    	if(optionIsAlreadySelected(owner, newOwner))
    		return;    	
    	Button button = new Button(newOwner);
    	button.setAttribute("value", newOwnerId);
    	button.setZclass("btn btn-outline-secondary ml-1");
    	button.addEventListener("onClick", new EventListener<Event>(){
			@Override
			public void onEvent(Event event) throws Exception {	
				Component button = event.getTarget();
				button.detach();
				if(owner.getChildren().size() == 0)
					owner.setVisible(false);
			}    		
    	});
    	owner.appendChild(button);
    }
    
    private void addGroup(String newGroup, int newGroupId){
    	if(optionIsAlreadySelected(group, newGroup))
    		return;    	
    	Button button = new Button(newGroup);
    	button.setAttribute("value", newGroupId);
    	button.setZclass("btn btn-outline-secondary ml-1");
    	button.addEventListener("onClick", new EventListener<Event>(){
			@Override
			public void onEvent(Event event) throws Exception {	
				Component button = event.getTarget();
				button.detach();
				if(group.getChildren().size() == 0)
					group.setVisible(false);
			}    		
    	});
    	group.appendChild(button);
    }
    
    private boolean optionIsAlreadySelected(Groupbox groupbox, String option){
    	Vector<String> options = groupbox2Vector(groupbox);
    	return options.contains(option);    	    	
    }

    private void fillTypeListbox(){
    	type.appendItem("ALL", "");    	
    	type.appendItem("Project", "");
    	type.appendItem("Calculation", "");    	
    	fillCalculationTypesFromDatabase();
    	type.setSelectedIndex(0);
    }
    
    private void fillCalculationTypesFromDatabase(){
    	/* for(CalculationType calcType : CalculationTypeService.getAll())
    		type.appendItem(calcType.getName(), String.valueOf(calcType.getId())); */
    }

	@SuppressWarnings("rawtypes")
	public String buildSearchQuery() throws InterruptedException,Exception{
		/*
		if(!hasSelectedCriteria()){
			Messagebox.show("Must select criteria to perform search", "Missing criteria", Messagebox.OK, "fas fa-exclamation-circle fa-3x z-div messagebox", null);
			return null;
		}
		
		StringBuilder query = new StringBuilder();
		searchType = getSearchType();	
		switch(searchType) {
			case PROJECT_AND_CALCULATIONS: 	query.append(Queries.SELECT_PROJECTS_AND_CALCULATIONS);
											break;
			case PROJECTS:					query.append(Queries.SELECT_PROJECTS);
											break;
			case CALCULATIONS:				query.append(Queries.SELECT_CALCULATIONS);
											break;
		}				
		
		//Add permissions control
		query.append(" WHERE ((projects.owner_user_id = " + ShiroManager.getCurrent().getUserId() + ") ");		//Is owner?
		query.append(" OR ((projects.owner_group_id IN (" +ShiroManager.getCurrent().getUserGroupsSQL() + ")) AND ((projects.permissions & b'001000') = b'001000'))");		//Belongs to an allowed group?
		query.append(" OR ((projects.permissions & b'000010') = b'000010'))");							//Is open to others?		

		
		//Group filters by AND/OR condition type
		List<String> andFilters = new ArrayList<>();
		List<String> orFilters = new ArrayList<>();		
		for(String filter : AVAILABLE_FILTERS) {
			String values = buildFilterQuery(filter);
			if(values != "") {
				Listbox element = (Listbox)searchDiv.query("#" + filter + "Cond");
				if(element.getSelectedItem().getLabel().equals("AND"))
					andFilters.add(values);
				else
					orFilters.add(values);
			}
		}
		
		if(andFilters.size() > 0) {
			query.append(" AND (( ");
			query.append(StringUtils.join(andFilters, " ) AND ( "));
			query.append(" )) ");			
		}
		if(orFilters.size() > 0) {
			query.append(" AND (( ");
			query.append(StringUtils.join(orFilters, " ) OR ("));
			query.append(" )) ");
		}    		
		return query.toString(); */
		return "";
    }
	
		
	private boolean hasSelectedCriteria(){		
		Iterator<Component> textboxes = searchDiv.queryAll("textbox").iterator();
		while(textboxes.hasNext()){
			Textbox child = (Textbox) textboxes.next();
			if(!child.getValue().equals(""))
				return true;
		}			
		
		Iterator<Component> groupboxes = searchDiv.queryAll("groupbox").iterator();
		while(groupboxes.hasNext()){
			Groupbox child = (Groupbox) groupboxes.next();
			if(child.getChildren().size() > 0)
				return true;
		}
		
		Iterator<Component> dateboxes = searchDiv.queryAll("datebox").iterator();
		while(dateboxes.hasNext()){
			Datebox child = (Datebox) dateboxes.next();
			if(child.getValue() != null)
				return true;
		}
		return false;
	}
	
	private SearchType getSearchType() {
		String type = this.type.getSelectedItem().getLabel(); 
		switch(type) {
			case "ALL":			return SearchType.PROJECT_AND_CALCULATIONS;	 
			case "Project": 	return SearchType.PROJECTS;
			case "Calculation": 
			default:			return SearchType.CALCULATIONS;		
		}			
	}
	
	private String buildFilterQuery(String field) {
		/*
		XulElement element = (XulElement) searchDiv.query("#" + field);		
		if(element instanceof Textbox) { 			
			return getTextboxQuery(field, ((Textbox)element).getText());
		}else if(element instanceof Listbox) {						
			return getTextboxQuery(field, (String)((Listbox)element).getSelectedItem().getValue());
		}else if(element instanceof Groupbox) {			
			List<String> values = new ArrayList<String>();
			Groupbox groupBox = ((Groupbox)element);
			for(Component child: groupBox.getChildren()) 
				values.add(((Integer)((Button)child).getAttribute("value")).toString());
			return getGroupboxQuery(field, StringUtils.join(values,","));					
		}else if(element instanceof Datebox) {					
			String start = ((Datebox)element).getText();			
			String end = ((Datebox)searchDiv.query("#" + field + "End")).getText();
			return getDateQuery(field, start, end);
		}
		return ""; */
		return "";
	}
	
	private String getTextboxQuery(String field, String value) {
		if(value == null || value.equals(""))
			return "";		
		return textFilterAssociatedQuery.get(searchType).get(field).replaceAll("\\?", escapeSQLParameter(value.toLowerCase())); //All textual searches use lowercase
	}
	
	private String getGroupboxQuery(String field, String value) {
		if(value == null || value.equals(""))
			return "";
		return groupFilterAssociatedQuery.get(searchType).get(field).replaceAll("\\?", value); 		
	}

	private String getDateQuery(String field, String start, String end) {
		String query = "";
		if(start != null && !start.equals("")) {			
			if(end != null && !end.equals("")) {
				query = rangeFilterAssociatedQuery.get(searchType).get(field + "_start_end");
				query = query.replaceAll("\\?", escapeSQLParameter(start));
				query = query.replaceAll("#", escapeSQLParameter(end));				
			}else{
				query = rangeFilterAssociatedQuery.get(searchType).get(field + "_start");
				query = query.replaceAll("\\?", escapeSQLParameter(start));				
			}
		}else if(end != null && !end.equals("")){
			query = rangeFilterAssociatedQuery.get(searchType).get(field + "_end");
			query = query.replaceAll("\\?", escapeSQLParameter(end));
		}
		return query;
	}

	/* public void doSearch(String query) throws InterruptedException, BrowseCredentialsException{
    	List<TreeMap<String, Entity>> results = SearchService.search(query);
    	TreeMap<String, Entity> projects = results.get(0);
    	TreeMap<String, Entity> calculations = results.get(1);
    	
    	if(!projects.isEmpty() || !calculations.isEmpty()){
    		fillResultTree(projects, calculations);
    		navigationQueue.publish(new Event("resetHome"));
			searchDiv.setVisible(false);
			resultsDiv.setVisible(true);
    	}else
    		Messagebox.show("Current criteria did not match any result.", "No results", Messagebox.OK, Messagebox.INFORMATION);
    } */

    /* private void fillResultTree(TreeMap<String, Entity> projects, TreeMap<String, Entity> calculations) {
    	HashMap<String, Treeitem> ownerPaths = new HashMap<>();
    	HashMap<String, Treeitem> insertedPaths = new HashMap<>();
    	
    	for(String path: projects.keySet()) {    		    		
    		Entity entity = projects.get(path);
    		String owner = entity.getParentPath().replaceAll("^/db/", "").replaceAll("/.*", "");
    		if(!ownerPaths.containsKey(owner)) { 
    			ownerPaths.put(owner, buildOwnerTreeitem(owner));
    			searchTreeChildren.appendChild(ownerPaths.get(owner));
    			
    		}    		
    		Treeitem item = buildTreeitem(entity, false);
    		item.setOpen(false);
    		insertedPaths.put(entity.getPath(), item);
    		ownerPaths.get(owner).getLastChild().appendChild(item);
    		
    	}
    	
    	for(String path: calculations.keySet()) {
    		Entity entity = calculations.get(path);
    		Treeitem item = buildTreeitem(entity, true);    		
    		if(insertedPaths.containsKey(entity.getParentPath()))
    			insertedPaths.get(entity.getParentPath()).getLastChild().appendChild(item);    		
    		else 
    			log.error("Missing parent for calculation " + path + "on search result");    		    
    	}
    } */
    
    private Treeitem buildOwnerTreeitem(String owner) {
    	Treerow row = new Treerow();
		Treecell name = new Treecell(owner);
		name.setSclass("searchOwnerTreeitem");
		row.appendChild(name);
		Treeitem item = new Treeitem();
		item.appendChild(row);		
		item.appendChild(new Treechildren());
		item.setSelectable(false);		
		return item;    	
    }
    
    
    /* private Treeitem buildTreeitem(Entity entity, boolean isCalculation) {
        Treerow row = new Treerow();
		Treecell name = new Treecell(isCalculation ? entity.getPath().replaceAll(entity.getParentPath(), ""): entity.getPath());
		row.appendChild(name);
		row.setAttribute("properties", entity);
		Treeitem item = new Treeitem();
		item.appendChild(row);
		item.setAttribute("properties", entity);
		if(entity.isCalculation())
			row.setTooltip("thumbnailPopup, position=after_center, delay=700");
		
		if(!isCalculation)
			item.appendChild(new Treechildren());
		
		return item;
    } */

    private Vector<String> groupbox2Vector(Groupbox g){
    	Vector<String> ret = new Vector<String>();
    	for(Component child : g.getChildren()){
    		String username = ((Button)child).getLabel();
    		ret.add(username);
    	}
    	return ret;
    } 

    private void resetSearch(){
    	clearSearchForm();
    	resultsDiv.setVisible(false);
    	while(searchTreeChildren.getChildren().size() > 0)    	
    		searchTreeChildren.removeChild(searchTreeChildren.getFirstChild());
    	searchDiv.setVisible(true);
    	navigationQueue.publish(new Event("resetHome"));
    }
    
    private void clearSearchForm(){
    	Iterator<Component> textboxes = searchDiv.queryAll("textbox").iterator();
		while(textboxes.hasNext()){
			Textbox child = (Textbox) textboxes.next();
			child.setValue("");
		}			
		
		Iterator<Component> dateboxes = searchDiv.queryAll("datebox").iterator();
		while(dateboxes.hasNext()){
			Datebox child = (Datebox) dateboxes.next();
			child.setValue(null);
		}
		
		Iterator<Component> listboxes = searchDiv.queryAll("listbox").iterator();
		while(listboxes.hasNext()){
			Listbox child = (Listbox) listboxes.next();
			child.setSelectedIndex(0);
		}    	
		clearOwners();
        clearGroups();
    }
    
    private void clearOwners(){
    	while(owner.getFirstChild() != null)
    		owner.getFirstChild().detach();
    	owner.setVisible(false);
    }
    
    private void clearGroups(){
    	while(group.getFirstChild() != null)
    		group.getFirstChild().detach();
    	group.setVisible(false);
    } 
    
    //Escape special symbols to avoid SQL injections
    private static String escapeSQLParameter(String parameter) {		
    	return parameter.replaceAll("\\$\\^\\'\\\"", "");    	    	
    }
}
