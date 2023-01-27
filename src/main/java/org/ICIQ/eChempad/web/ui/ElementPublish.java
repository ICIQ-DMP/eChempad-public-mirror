/*
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

import org.ICIQ.eChempad.web.definitions.Constants;
import org.ICIQ.eChempad.web.definitions.Constants.ScreenSize;
import org.ICIQ.eChempad.web.definitions.CustomProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ElementPublish extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(ElementPublish.class);

	private static final int MAX_DOI_REQUEST_NUMBER = 10;
	private static final String DOI_REGEX = "(10[.][0-9]{4,}(?:[.][0-9]+)*\\/(?:(?![\"&\\'<>])\\S)+)";
	private static final Pattern doiPattern = Pattern.compile(DOI_REGEX);
	private static final String NAME_REGEX = "[^\\d\\?]*[^\\d\\?\\s]+[^\\d\\?]*";
	private static final Pattern namePattern = Pattern.compile(NAME_REGEX);
	
	private static final String DOI_URI_PREFIX = "http://dx.doi.org/";

	private EventQueue<Event> userEventsQueue = null;
	private EventQueue<Event> navigationQueue = null;
	private EventQueue<Event> elementPublishQueue = null;
	private EventQueue<Event> displayQueue = null;

	// private Set<Entity> selectedElements = null;
	// private PublishTreeModel elementTreeModel;
	// private Rest50ApiManager restManager = null;

	// private RestDoiDaemonManager doiManager = null;
	private int publicationStatus = Constants.INSTITUTION_STATUS_DISABLED;
	// private ElementPublishOperation operation;

	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;

	@Wire
	Window elementPublish;

	@Wire
	East publishEast;

	@Wire
	Tree publishTree;
	@Wire
	Tabbox publishTbox;

	@Wire
	Tab namingFieldsTab;
	@Wire
	Radiogroup namingConventionRdg;
	@Wire
	Radio fullPathRd;
	@Wire
	Radio nameRd;
	@Wire
	Checkbox appendDescriptionChk;
	@Wire
	Button publishBtn;

	@Wire
	Tab publicationTab;
	@Wire
	Checkbox generateDoiChk;

	@Wire
	A generateDoiRealA;
	@Wire
	A generateDoiTestA;
	@Wire
	A generateDoiDisabledA;
	@Wire
	Checkbox embargoChk;

	@Wire
	Tab datasetTypeTab;
	@Wire
	Radiogroup datasetTypeGrp;
	@Wire
	Radio independentTypeRb;
	@Wire
	Radio supportingTypeRb;
	@Wire
	Checkbox notPublishedChk;
	@Wire
	Div paperInformationDiv;
	@Wire
	Combobox journalCbo;
	@Wire
	Textbox paperDoiTxt;
	@Wire
	Textbox paperTitleTxt;

	@Wire
	Tab additionalFieldsTab;
	@Wire
	Grid authorsGrid;
	@Wire
	Textbox institutionTxt;
	@Wire
	Combobox keywordsCbo;
	@Wire
	Groupbox keywordsGbx;

	@Wire
	Button additionalFieldsPreviousBtn;

	@Wire
	Tabpanel manuscriptInfoTabpanel;
	// Publish progress window
	@Wire
	Div elementPublishProgress;
	@Wire
	Button cancelBtn;
	@Wire
	Label progressLbl;
	@Wire
	Progressmeter progressMtr;
	
	@Wire 
	Timer timerKeepAliveSession;

	@Listen("onClick=#namingConventionStepNextBtn")
	public void onNamingConventionStepNextBtnClick() {
		publishTbox.setSelectedTab(publicationTab);
	}

	@Listen("onClick=#independentTypeRb")
	public void onIndependentTypeRbClick() {
		changePaperInformationVisibility();
	}

	@Listen("onClick=#supportingTypeRb")
	public void onSupportingTypeRbClick() {
		changePaperInformationVisibility();
	}

	@Listen("onClick=#notPublishedChk")
	public void onNotPublishedChkClick() {
		journalCbo.setDisabled(notPublishedChk.isChecked());
		paperDoiTxt.setDisabled(notPublishedChk.isChecked());
		paperTitleTxt.setDisabled(notPublishedChk.isChecked());
	}

	@Listen("onClick=#publicationStepPreviousBtn")
	public void onPublicationStepPreviousBtnClick() {
		publishTbox.setSelectedTab(namingFieldsTab);
	}

	@Listen("onClick=#publicationStepNextBtn")
	public void onPublicationStepNextBtnClick() {
		publishTbox.setSelectedTab(datasetTypeTab);
	}

	@Listen("onClick=#datasetStepPreviousBtn")
	public void onDatasetStepPreviousBtnClick() {
		publishTbox.setSelectedTab(publicationTab);
	}

	@Listen("onClick=#datasetStepNextBtn")
	public void onDatasetStepNextBtnClick() {
		publishTbox.setSelectedTab(additionalFieldsTab);
	}

	@Listen("onClick=#additionalFieldsPreviousBtn")
	public void onAdditionalFieldsPreviousBtnClick() {
		publishTbox.setSelectedTab(datasetTypeTab);
	}

	@Listen("onOK=#keywordsCbo")
	public void onKeywordsCboOk() {
		appendNewKeyword();
	}

	@Listen("onSelect=#keywordsCbo")
	public void onKeywordsCboSelect() {
		appendNewKeyword();
	}

	private void changePaperInformationVisibility() {
		paperInformationDiv.setVisible(supportingTypeRb.isChecked());
		// manuscriptInfoTabpanel.invalidate();
	}

	@Listen("onCheck=#namingConventionRdg")
	public void setElementsName() {
		String selectedNamingConvention = (String) namingConventionRdg.getSelectedItem().getValue();
		//setElementsName(elementTreeModel.getRoot(), selectedNamingConvention, "");
		refreshTree();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#publishBtn")
	public void publishBtnClick() {
		int unpublishedTopCollections = 0; // = getNotPublishedTopCollectionCount();
		try {
			// Check that user agrees to publish more than one top collection in the same
			// process
			if (unpublishedTopCollections > 1 && generateDoiChk.isChecked()) {
				Messagebox.show(
						"You have selected " + unpublishedTopCollections
								+ " top projects to be published that will request " + unpublishedTopCollections
								+ " independent DOI indentifiers, is this correct?\n",
						"Multiple DOI request", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
						new EventListener() {
							@Override
							public void onEvent(Event event) throws Exception {
								if (Messagebox.ON_YES.equals(event.getName()))
									publish();
							}
						});
			} else {
				publish();
			}
		} catch (Exception e) {
			Messagebox.show("An error ocurred during publication process. " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public ElementPublish() {
		super();
		userEventsQueue = EventQueues.lookup(getUsername() + "userevents", WebApps.getCurrent(), true);
		navigationQueue = EventQueues.lookup("navigation", EventQueues.DESKTOP, true);
		elementPublishQueue = EventQueues.lookup("elementpublish", EventQueues.DESKTOP, true);
		elementPublishQueue.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("show")) {
					/* selectedElements = (Set<Entity>) _desktopScope.get("selectedElements");
					if (selectedElements.size() == 0) {
						Messagebox.show("There is no element selected to publish.", "Resources error.", Messagebox.OK,
								Messagebox.ERROR);
						return;
					} else {
						doiManager = new RestDoiDaemonManager();
						display();
					} */
				}
			}
		});
		displayQueue = EventQueues.lookup("display", EventQueues.DESKTOP, true);
		displayQueue.publish(new Event("addBootstrapClasses", null, null));
	}

	private void refreshTree() {
		publishTree.setModel(publishTree.getModel());
	}

	private void display() {
		try {
			disableForm(false);
			//buildPublishTree();
			//clearPublishForm();
			setupLayout();
			elementPublish.setVisible(true);
		} catch (Exception e) {
			Messagebox.show(
					"There has been an error retrieving Browse collection structure, please contact ioChem-BD administrator.\n"
							+ e.getMessage(),
					"Resources error.", Messagebox.OK, Messagebox.ERROR);
		}
	}

	private String getUsername() {
		try {
			return "";
			// return ShiroManager.getCurrent().getUserName();
		} catch (Exception e) {
			return UUID.randomUUID().toString();
		}
	}

	/*
	private void clearPublishForm() throws WrongValueException, BrowseCredentialsException {
		publishTbox.setSelectedTab(namingFieldsTab);
		namingConventionRdg.setSelectedItem(fullPathRd);
		appendDescriptionChk.setChecked(true);		
		generateDoiChk.setDisabled(
				!(existsNotPublishedTopCollection() && publicationStatus != Constants.INSTITUTION_STATUS_DISABLED));
		generateDoiChk.setTooltip(
				!existsNotPublishedTopCollection() ? "Can't request DOIs for already published elements" : "");
		generateDoiChk.setChecked(!generateDoiChk.isDisabled());	// By default we will always request DOI 
		
		embargoChk.setChecked(false);
		datasetTypeGrp.setSelectedItem(independentTypeRb);
		notPublishedChk.setChecked(false);
		onNotPublishedChkClick();
		paperInformationDiv.setVisible(false);
		journalCbo.setValue(null);
		paperDoiTxt.setValue("");
		paperTitleTxt.setValue("");
		// Clear authors
		Iterator<Component> txtIterator = authorsGrid.queryAll("textbox").iterator();
		while (txtIterator.hasNext())
			((Textbox) txtIterator.next()).setValue("");
		institutionTxt.setValue("");
		// Clear user defined keywords
		keywordsCbo.setValue("");
		while (!keywordsGbx.getChildren().isEmpty())
			keywordsGbx.removeChild(keywordsGbx.getFirstChild());
		// Set default values
		try {
			institutionTxt.setValue(CustomProperties.getProperty("mets.institution.name"));
			Row authorRow = (Row) authorsGrid.getRows().getFirstChild();
			((Textbox) authorRow.getChildren().get(0).getFirstChild())
					.setValue(ShiroManager.getCurrent().getUserFullName().split(",")[0].trim());
			((Textbox) authorRow.getChildren().get(1).getFirstChild())
					.setValue(ShiroManager.getCurrent().getUserFullName().split(",")[1].trim());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		// Set default calculation names
		setElementsName();
	}
	*/


	/*
	private int getNotPublishedTopCollectionCount() {
		int unpublishedTopCollections = 0;
		List<TreeNode<Element>> communities = elementTreeModel.getRoot().getChildren();
		for (TreeNode<Element> community : communities)
			for (TreeNode<Element> collection : community.getChildren())
				if (!collection.getData().isPublished())
					unpublishedTopCollections++;
		return unpublishedTopCollections;
	}
	*/


	/*
	private boolean existsNotPublishedTopCollection() {
		return getNotPublishedTopCollectionCount() > 0;
	} */

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		setDoiStatus();
		//initComboboxes();
		elementPublish.addEventListener("onClose", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				elementPublish.setVisible(false);
				elementPublishQueue.publish(new Event("closed"));
				event.stopPropagation();
				navigationQueue.publish(new Event("resetHome"));
				navigationQueue.publish(new Event("newnavigation"));
				displayQueue.publish(new Event("showNavigationButtons", null, null));
			}
		});
	}

	private void setDoiStatus() {
		try {
			//doiManager = new RestDoiDaemonManager();
			publicationStatus = (int) WebApps.getCurrent().getAttribute("doiDaemonStatus");
			switch (publicationStatus) {
			case Constants.INSTITUTION_STATUS_TEST_MANUAL:
			case Constants.INSTITUTION_STATUS_TEST_AUTO:
				generateDoiTestA.setVisible(true);
				break;
			case Constants.INSTITUTION_STATUS_REAL_MANUAL:
			case Constants.INSTITUTION_STATUS_REAL_AUTO:
				generateDoiRealA.setVisible(true);
				break;
			case Constants.INSTITUTION_STATUS_DISABLED:
			default:
				generateDoiDisabledA.setVisible(true);
				break;
			}
		} catch (Exception e) {

		}
	}

	/*
	private void initComboboxes() {
		FuzzySearchListModel journallist = new FuzzySearchListModel(
				Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/keyword-journals.txt"), 20,
				FuzzySearchListModel.SearchMode.CONTAINS, FuzzySearchListModel.SourceFields.LABEL_AND_VALUES);
		journalCbo.setModel(journallist);
		journalCbo.setItemRenderer(new ComboitemRenderer<Comboitem>() {
			@Override
			public void render(Comboitem item, Comboitem data, int index) throws Exception {
				item.setLabel(data.getLabel());
				item.setValue(data.getValue());
			}
		});
		FuzzySearchListModel chemlist = new FuzzySearchListModel(
				Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/keyword-compchem.txt"), 20,
				FuzzySearchListModel.SearchMode.CONTAINS, FuzzySearchListModel.SourceFields.VALUES_ONLY);
		keywordsCbo.setModel(chemlist);
		keywordsCbo.setItemRenderer(new ComboitemRenderer<Comboitem>() {
			@Override
			public void render(Comboitem item, Comboitem data, int index) throws Exception {
				item.setLabel(data.getLabel());
				item.setValue(data.getValue());
			}
		});
	}
	*/


	private void setupLayout() {
		displayQueue.publish(new Event("hideNavigationButtons", null, null));
		ScreenSize layout = (ScreenSize) Executions.getCurrent().getDesktop().getAttribute("display");
		if (layout == null)
			layout = ScreenSize.X_LARGE;

		if (layout == ScreenSize.X_SMALL || layout == ScreenSize.SMALL)
			publishEast.setWidth("100%");
		else
			publishEast.setWidth("35%");

	}

	@SuppressWarnings("unchecked")
	private void appendNewKeyword() {
		String keyword = keywordsCbo.getValue();
		if (keyword.equals(""))
			return;

		Button keywordBtn = new Button();
		keywordBtn.setLabel(keyword + "  X");
		keywordBtn.setSclass("badge badge-pill ");
		keywordBtn.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				Button currentBtn = (Button) event.getTarget();
				currentBtn.getParent().removeChild(currentBtn);
			}
		});
		keywordsGbx.appendChild(keywordBtn);
		keywordsCbo.setValue("");
	}

	/*
	private void buildPublishTree() throws Exception {
		elementTreeModel = new PublishTreeModel(new ElementListBuilder(selectedElements).getRoot());
		elementTreeModel.sort(new ElementComparator(true), true);
		publishTree.setItemRenderer(new ElementTreeRenderer(elementPublish, elementTreeModel));
		publishTree.setModel(elementTreeModel);		
	}
	*/


	private void publish() throws Exception {
		// if (!isFormValid()) return;
		disableForm(true);
		sortTreeElements(false);
		// startPublishOperation();
	}

	private void disableForm(boolean disable) {
		namingFieldsTab.setDisabled(disable);
		publicationTab.setDisabled(disable);
		datasetTypeTab.setDisabled(disable);
		additionalFieldsTab.setDisabled(disable);
		additionalFieldsPreviousBtn.setDisabled(disable);
		publishBtn.setDisabled(disable);
	}

	private void sendEvents() {
		// initRestManager();
		// sendCreateRequests();
		// sendUpdateRequests();
		// sendDoiRequests();
	}

	private void endPublish() {
		// displayResume();
		// refreshNavigationTree();
		sortTreeElements(true);
		refreshTree();
	}

	/*
	private void initRestManager() {
		try {
			restManager = new Rest50ApiManager();
		} catch (Exception e) {
			log.error("Error creating RestManager for publication notification");
			log.error(e.getMessage());
		}
	}
	 */

	private void sortTreeElements(boolean ascending) {
		// elementTreeModel.sort(new ElementComparator(ascending), ascending);
	}

	/*
	private void startPublishOperation() throws Exception {
		// Create long process operation and provide it with the appropriate parameters
		operation = new ElementPublishOperation(elementTreeModel) {
			@Override
			protected void onFinish() {
				try {
					this.showStatus("<p>Finishing publication process</p>");
					sendEvents();
				} catch (InterruptedException e) {
					log.error("Error raised during publication finish process!");
					log.error(e.getMessage());
				}

			}

			@Override
			protected void onCleanup() {
				try {
					this.activate();
					endPublish();
					this.deactivate();
					this.clearStatus();
				} catch (Exception e) {
					log.error("Error raised during publication cleanup process!");
					log.error(e.getMessage());
				}
			}
		};
		//Activate timer to avoid session expiration
		timerKeepAliveSession.setRunning(true);
		operation.setGenerateDOIs(generateDoiChk.isChecked());
		operation.setAppendDescription(appendDescriptionChk.isChecked());
		operation.setAdditionalMetadata(buildAdditionalMetadata());		
		operation.start();

	}

	private void sendCreateRequests() {
		if (!embargoChk.isChecked()) {
			for (Element collection : operation.getCreateCollectionRequests()) {
				try {
					restManager.postCreateCollection(collection.getHandle());
				} catch (Exception e) {
					log.error("Error sending collection create request. " + e.getMessage());
				}
			}
		}
	}

	private void sendUpdateRequests() {
		if (!embargoChk.isChecked()) {
			for (Element collection : operation.getUpdateCollectionRequests()) {
				try {
					restManager.putUpdateCollection(collection.getHandle());
				} catch (Exception e) {
					log.error("Error sending collection create request. " + e.getMessage());
				}
			}
		}
	}

	private void sendDoiRequests() {
		if (operation.getDoiRequestCollection().size() == 0)
			return;
		try {
			ArrayList<String> authorNames = new ArrayList<String>();
			ArrayList<String> authorSurnames = new ArrayList<String>();
			String email = ShiroManager.getCurrent().getUserEmail();
			String url = "";
			String depositorName = CustomProperties.getProperty("mets.institution.name");
			String title = "";
			// Build author names list
			for (Component component : authorsGrid.getRows().getChildren()) {
				Row authorRow = (Row) component;
				String surname = ((Textbox) authorRow.getChildren().get(0).getFirstChild()).getValue().trim();
				String name = ((Textbox) authorRow.getChildren().get(1).getFirstChild()).getValue().trim();
				if (!surname.equals("") && !name.equals("")) {
					authorNames.add(name);
					authorSurnames.add(surname);
				}
			}
			// Iterate newly created collections sending its request to DOI daemon. In case
			// it fails to connect DOI daemon we will display a text to send by mail
			StringBuilder sendByMailText = new StringBuilder();
			for (Element collection : operation.getDoiRequestCollection()) {
				try {
					url = Main.getBrowseBaseUrl() + Constants.BROWSE_HANDLE_ENDPOINT + "/" + collection.getHandle();
					title = collection.getName();
					doiManager.requestDoi(url, email, depositorName, title, authorNames, authorSurnames);
				} catch (Exception e) {
					sendByMailText.append(e.getMessage());
				}
			}
			if (!sendByMailText.toString().equals("")) {
				Window window = (Window) Executions.createComponents("errors/errorDialog.zul", elementPublish, null);
				ErrorDialog errorDialog = (ErrorDialog) window.getAttribute("$composer");
				errorDialog.setTitle("DOI service unavailable");
				errorDialog.setError(
						"Can not contact external service to request DOI identifiers for published collections.");
				errorDialog.setCause(new Html(
						"<span class='z-label'>One possible cause is that DOI service is temporally down or unavailabe. Check also that this </span><span class='z-label' style='font-style:italic'>"
								+ CustomProperties.getProperty(Constants.DOI_URL_PROPERTY)
								+ "</span><span class='z-label'> is accessible from your server and that no firewall is blocking the communication.</span> "));
				errorDialog.setSolution(new Html(
						"<span class='z-label'>Please send and email to </span><span style='font-weight:bold' class='zlabel'>"
								+ Constants.CONTACT_EMAIL
								+ "</span><span class='z-label'> with the following text and our team will request the DOIs for you.</span> "));
				errorDialog.setErrorMessage(sendByMailText.toString());
				window.doModal();
			}

		} catch (Exception e) {
			Messagebox.show("There has been an error generating doi requests.");
		}
	}

	private void displayResume() {
		Window window = (Window) Executions.createComponents("main/elementPublishResume.zul", elementPublish, null);
		ElementPublishResume resume = (ElementPublishResume) window.getAttribute("$composer");
		resume.setStatistics(operation.getPublishedProjectCount(), operation.getPublishedCalculationCount(),
				operation.getDoiRequestCollection().size());
		// Get all top collections
		ArrayList<Element> collections = new ArrayList<Element>();
		List<TreeNode<Element>> communities = elementTreeModel.getRoot().getChildren();
		for (TreeNode<Element> community : communities)
			for (TreeNode<Element> collection : community.getChildren())
				collections.add(collection.getData());
		resume.setPublishedElements(collections);
		window.doModal();
		//Disable session renew timer
		timerKeepAliveSession.setRunning(false);
	}

	private void refreshNavigationTree() {
		userEventsQueue.publish(new Event("refresh"));
	}

	private boolean isFormValid() {
		int unpublishedTopCollections = getNotPublishedTopCollectionCount();
		if (unpublishedTopCollections > MAX_DOI_REQUEST_NUMBER && generateDoiChk.isChecked()) {
			Messagebox.show("You have selected " + operation.getDoiRequestCollection().size()
					+ " top collections to be published with DOI identifiers. You can't generate more than 10 DOI on each publication process. "
					+ "It is recomended that you move all related collections into a parent collection and publish that collection to avoid generating so many DOI indentifiers.",
					"Too many collection publication to request DOI", Messagebox.OK, Messagebox.EXCLAMATION);
			return false;
		}
		if (paperInformationDiv.isVisible()) {
			String doi = paperDoiTxt.getValue().trim();
			if (!doi.equals("") && !doiPattern.matcher(doi).matches()) {
				Messagebox.show("An invalid DOI number has been provided", "Wrong journal information.", Messagebox.OK,
						Messagebox.ERROR);
				return false;
			}
		}
		for (Component component : authorsGrid.getRows().getChildren()) {
			Row authorRow = (Row) component;
			String surname = ((Textbox) authorRow.getChildren().get(0).getFirstChild()).getValue().trim();
			String name = ((Textbox) authorRow.getChildren().get(1).getFirstChild()).getValue().trim();
			if (!surname.equals("") && !name.equals(""))
				if (!namePattern.matcher(surname).matches() || !namePattern.matcher(name).matches()) {
					Messagebox.show("Invalid autor name/surname provided, please correct it.", "Wrong author name",
							Messagebox.OK, Messagebox.ERROR);
					return false;
				}
		}
		return true;
	}

	private String getLocationFromHttpsCertificate() {
		StringBuilder sb = new StringBuilder();
		try {
			X509Certificate cert = (X509Certificate) RestManager.getCurrentCertificate();
			String dn = cert.getSubjectX500Principal().getName();
			LdapName ldapDN = new LdapName(dn);
			for (Rdn rdn : ldapDN.getRdns()) {
				if (rdn.getType().matches("L|ST|C") && !((String) rdn.getValue()).trim().equals(""))
					sb.append((String) rdn.getValue() + " ");
			}
		} catch (InvalidNameException e) {
			log.error(e.getMessage());
		}
		return sb.toString();
	}

	private void setElementsName(TreeNode<Element> node, String convention, String parentPath) { 	    
	    // First set element name	   
		setElementPath(node.getData(), convention, parentPath);
		// Then its children
		if (node.getChildren() != null) {
			String newParentpath = getNodePath(parentPath, node);
			for (TreeNode<Element> childElement : node.getChildren())
				setElementsName(childElement, convention, newParentpath);
		}
	}
	
	private void setElementPath(Element element, String convention, String parentPath) {	    
	    if (element != null) { 
	        if(element.getType() != ElementType.COMMUNITY)
	            element.setPath(element.getName().trim());
            if (element.isPublished())
                element.setPath(element.getPublishedName());
            else if(convention.equals("fullpath") || element.getType() == ElementType.COLLECTION)
                element.setPath((parentPath + " " + element.getName()).trim());
            else if(convention.equals("nameonly"))
                element.setPath(element.getName().trim());
        }
	}

	private String getNodePath(String parentPath, TreeNode<Element> node) {
		Element element = node.getData();
		if (element == null 
		        || element.getType() == ElementType.COMMUNITY		      
		        || node.getParent().getData().getType() == ElementType.COMMUNITY)
		    return "";
			
		if(parentPath.equals("") && !element.getName().startsWith("/"))
		    return  "/" + element.getName();
		else 
		    return parentPath + "/" + element.getName();
	}

	 */

	private HashMap<String, String> buildAdditionalMetadata() {
		HashMap<String, String> additionalMetadata = new HashMap<String, String>();
		// Add only first author
		Row row = (Row) authorsGrid.getRows().getChildren().get(0);
		String surname = ((Textbox) row.query("textbox[placeholder='Surname']")).getValue().trim();
		String name = ((Textbox) row.query("textbox[placeholder='Name']")).getValue().trim();
		additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:contributor.dcterms:author",
				surname + ", " + name);

		for (String institution : institutionTxt.getValue().split("\n"))
			if (!institution.trim().equals(""))
				additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:publisher", institution.trim());
		for (Component keyword : keywordsGbx.getChildren())
			additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:subject",
					((Button) keyword).getLabel().replace("  X", ""));

		additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:rights",
				"CC BY 4.0 (c) " + CustomProperties.getProperty("mets.institution.name") + ", "
						+ Calendar.getInstance().get(Calendar.YEAR));
		additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:rights.dcterms:URI",
				"http://creativecommons.org/licenses/by/4.0/");
		additionalMetadata.put("metadata" + additionalMetadata.size() + "#dcterms:accessRights",
				"info:eu-repo/semantics/" + (embargoChk.isChecked() ? "embargoedAccess" : "openAccess"));

		String coverageSpatial = ""; // getLocationFromHttpsCertificate();
		if (!coverageSpatial.equals(""))
			additionalMetadata.put("metadata" + additionalMetadata.size() + "#dcterms:spatial", coverageSpatial);

		if (embargoChk.isChecked()) {
			additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:embargo.dcterms:lift", "none");
			additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:embargo.dcterms:terms", "forever");
		}
		if (supportingTypeRb.isChecked()) {
			String paperDoi = paperDoiTxt.getValue().trim().replaceAll("\n", " ");
			String paperJournal = journalCbo.getSelectedItem() == null ? ""
					: ((String) journalCbo.getSelectedItem().getValue()).trim();
			String paperTitle = paperTitleTxt.getValue().trim().replaceAll("\n", " ");

			if (!paperDoi.equals(""))
				additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:relation.dcterms:URI",
						DOI_URI_PREFIX + paperDoi);
			if (!paperJournal.equals("") || !paperTitle.equals("")) {
				if (!paperTitle.equals(""))
					paperTitle = "Original title: " + paperTitle;
				if (!paperDoi.equals(""))
					paperDoi = "DOI: " + paperDoi;
				if (!paperJournal.equals(""))
					paperJournal = "Journal: " + paperJournal;
				additionalMetadata.put("metadata" + additionalMetadata.size() + "#dc:relation",
						(paperTitle + "   " + paperDoi + "   " + paperJournal).trim());
			}
		}
		return additionalMetadata;
	}

}
