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

/*

import cat.iciq.tcg.labbook.datatype.Calculation;
import cat.iciq.tcg.labbook.datatype.CalculationFile;
import cat.iciq.tcg.labbook.datatype.services.ActionService;
import cat.iciq.tcg.labbook.datatype.services.CalculationService;
import cat.iciq.tcg.labbook.shell.utils.MetsFileHandler;
import cat.iciq.tcg.labbook.web.definitions.XpathQueries;
import cat.iciq.tcg.labbook.web.utils.XMLFileManager;
import cat.iciq.tcg.labbook.zk.manager.actions.Action;
import cat.iciq.tcg.labbook.zk.manager.actions.DownloadAction;

public class ActionManager {

    //Dynamic class loading
    private static final String ACTIONS_PACKAGE = "cat.iciq.tcg.labbook.zk.manager.actions";
    private static final String DEFAULT_ACTION = DownloadAction.class.getName();
    private static final String BAD_ACTION_CLASS_NAME = "Action class name error!";

    //Database action assignment
    private HashMap<String,Action> actionObjectByName = new HashMap<String,Action>();
    private HashMap<String,HashSet<String>> actionNamesByMimeJumbo = new HashMap<String,HashSet<String>>();
    private XMLFileManager metsFileManager = null;
    private Tabbox tabBox = null;					//Content holder
    private Tabs tabs = null;
    private Tabpanels tabPanels = null;


    //Display variables
    protected EventQueue<Event> actionQueue = null;		//Communication with actions and Main composer
    private boolean isMaximized = false;


    public ActionManager () throws InterruptedException{
        initActionQueues();
        loadActionClasses();
        loadActionParameters();
        initTabbox();
    }

    private void initTabbox() {
        tabBox = new Tabbox();
        tabBox.setId("dcmTabBox");
        tabs = new Tabs();
        tabs.setId("dcmTabs");
        tabPanels = new Tabpanels();
        tabPanels.setId("dcmTabPanels");
        tabBox.setHeight("100%");
        tabBox.appendChild(tabs);
        tabBox.appendChild(tabPanels);
    }

    private void initActionQueues(){
        actionQueue = EventQueues.lookup("dcmactions", EventQueues.DESKTOP, true);
        actionQueue.subscribe(new EventListener<Event>(){
            @Override
            public void onEvent(Event event) throws Exception {
                if(event.getName().equals("maximizeRequested")){
                    isMaximized = !isMaximized;
                    actionQueue.publish(new Event("dcmActionsMaximize",null,isMaximized));
                    actionQueue.publish(new Event("actionsMaximize",null,isMaximized));
                }
            }
        });
    }

    private void loadActionParameters() throws InterruptedException{
        //Load database action configurations per mimetype and jumbo format, one mimetype+jumbo_format can have multiple actions (download + view on Jmol for example)
        for(cat.iciq.tcg.labbook.datatype.Action action : ActionService.getAll())	{
            String mimetype = action.getMimetype();
            String jumbo_format	= action.getJumboFormat();
            String actionType = action.getAction();
            String parameters = action.getParameters();
            String key = mimetype + "$" + jumbo_format;

            if(actionNamesByMimeJumbo.containsKey(key)){
                actionNamesByMimeJumbo.get(key).add(actionType);
            }else{
                HashSet<String> actionNames = new HashSet<String>();
                actionNames.add(actionType);
                actionNamesByMimeJumbo.put(key, actionNames);
            }
            if(actionObjectByName.containsKey(actionType)){
                actionObjectByName.get(actionType).setParameters(mimetype, jumbo_format, parameters);
            }
        }
    }

    private void loadActionClasses(){
        //Load defined action classes
        Set<Class<? extends Action>> actions = getActionClasses(ACTIONS_PACKAGE);
        Iterator iter = actions.iterator();

        while(iter.hasNext()){
            try {
                String className = ((Class)iter.next()).getName();
                Class actionClass = Class.forName(className);

                Action newAction = (Action)actionClass.newInstance();
                newAction.init();
                actionObjectByName.put(actionClass.getName(), newAction);
            }
            catch (ClassNotFoundException e) {} 	//If class is not present we won't add it to our map
            catch (InstantiationException e) {}
            catch (IllegalAccessException e) {}
        }
    }



    private Set<Class<? extends Action>> getActionClasses(String packageName){
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends Action>> subTypes =  reflections.getSubTypesOf(Action.class);
        return subTypes;
    }

    public boolean  containsActions(){
        return tabBox != null && !tabBox.getChildren().isEmpty();
    }

    public Tabbox getContent(){
        return tabBox;
    }

    public void loadMetsFile(Calculation calculation) throws SAXException, IOException, ParserConfigurationException  {
        resetActions();
        loadFilesInActions(calculation);
        renderActions();
    }



    private void loadFilesInActions(Calculation calculation) throws SAXException, IOException, ParserConfigurationException{
        metsFileManager = new XMLFileManager(MetsFileHandler.METS_NAMESPACE, MetsFileHandler.METS_ADDITIONAL_NAMESPACES, calculation.getMetsXml());
        //Load every file into it's defined Action
        NodeList iter = metsFileManager.getItemIteratorQuery(XpathQueries.GET_CALCULATION_FILES_FILEID);
        for(int inx = 0; inx < iter.getLength(); inx++){
            String fileID 			= ((Attr) iter.item(inx)).getTextContent();
            String mimeType			= metsFileManager.getSingleAttributeValueQuery(XpathQueries.GET_MIMETYPE_FROM_FILEID.replace("?", fileID));
            String jumboOutputType 	= metsFileManager.getSingleAttributeValueQuery(XpathQueries.GET_JUMBOTYPE_FROM_FILEID.replace("?", fileID));
            String fileName			= metsFileManager.getSingleAttributeValueQuery(XpathQueries.GET_FILENAME_FROM_FILEID.replace("?", fileID));
            String key 				= mimeType + "$" + jumboOutputType;

            List<CalculationFile> files = CalculationService.getCalculationFiles(calculation.getId());
            if(actionNamesByMimeJumbo.containsKey(key))	{
                for(String actionName : actionNamesByMimeJumbo.get(key)) {		//Attach this file to all defined actions
                    try{
                        Action action = actionObjectByName.get(actionName);
                        action.addFile(mimeType, jumboOutputType, fileName);
                        action.setCalculationFiles(files);
                        action.setCalculation(calculation);
                        action.setMetsFileManager(metsFileManager);
                    }catch(NullPointerException e){
                        Messagebox.show("Bad defined action class name. Check action " + actionNamesByMimeJumbo.get(key) +  " definition in action table.", BAD_ACTION_CLASS_NAME , Messagebox.OK, Messagebox.INFORMATION);
                    }
                }
            }
            else {
                Action action = actionObjectByName.get(DEFAULT_ACTION);
                action.addFile(mimeType, jumboOutputType, fileName);
                action.setCalculationFiles(files);
                action.setCalculation(calculation);
                action.setMetsFileManager(metsFileManager);
            }
        }
    }

    private void renderActions(){
        // Always set to selected by default, otherwise won't load jsmol,
        // Please read: http://wiki.jmol.org/index.php/Loading_Notice#How_to_preload_Java_and_the_Jmol_Applet_in_background
        if(!tabs.getChildren().isEmpty()) {
            ((Tab)tabs.getFirstChild()).setSelected(true);
        }

        // First: generate tabs and it's content
        for(String actionName:actionObjectByName.keySet()){
            actionObjectByName.get(actionName).render();
        }
        // Second: order actions by it's internal order
        TreeMap<Integer, Action> orderedActions = new TreeMap<Integer,Action>();
        for(String actionName:actionObjectByName.keySet()){
            Action currentAction = actionObjectByName.get(actionName);
            if(!currentAction.isEmpty()){
                if(orderedActions.containsKey(currentAction.getInternalOrder()))	//Order collision!!
                    orderedActions.put(getRandomNumber(), currentAction);
                else
                    orderedActions.put(currentAction.getInternalOrder(), currentAction);
            }
        }
        // Third: Append action tabs and tabpanels, every action holds it's own logic
        if(orderedActions.size() == 0)
            return;

        for(int order : orderedActions.keySet()){
            Action action = orderedActions.get(order);
            if(!tabs.getChildren().contains(action.getTab())) {
                tabs.appendChild	 (action.getTab());
                tabPanels.appendChild(action.getTabpanel());
            }
        }
    }

    public void clear(){
        resetActions();
    }

    private void resetActions(){
        for(String actionName:actionObjectByName.keySet())
            actionObjectByName.get(actionName).reset();
    }

    private int getRandomNumber(){
        Random generator = new Random( 19580427 );
        return generator.nextInt() + 100;
    }
}
*/