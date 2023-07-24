package org.ICIQ.eChempad.web.composers;

import org.ICIQ.eChempad.services.SignalsImportService;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import java.io.IOException;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ToolbarComposer extends SelectorComposer<Window> {

    /**
     * Button UI component to import from Signals
     */
    @Wire
    private Button importSignals;

    /**
     * Button UI component to export to Dataverse
     */
    @Wire
    private Button exportDataverse;

    /**
     * Button UI to refresh views
     */
    @Wire
    private Button refreshButton;

    /**
     * Used to send events to the tree
     */
    private EventQueue<Event> treeQueue;

    /**
     * Service to connect to Signals using the API key of the current user to retrieve all available data
     */
    @WireVariable("signalsImportService")
    private SignalsImportService signalsImportService;

    /**
     * De-facto constructor for composer components.
     *
     * @param comp Window component
     * @throws Exception If something goes wrong during initialization.
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        this.initActionQueues();
    }

    private void initActionQueues() {
        this.treeQueue = EventQueues.lookup(EventQueueNames.TREE_QUEUE, EventQueues.DESKTOP, true);
    }

    @Listen("onClick = #importSignals")
    public void onClickImportSignalButton() throws IOException {
        // Import all visible data from Signals into eChempad workspace.
        this.signalsImportService.importWorkspace();

        // Publish refresh event in order to reload the UI
        this.treeQueue.publish(new Event(EventNames.REFRESH_EVENT, null, null));
    }


    /**
     * Method that communicates with the tree and gets the UUID of the selected elements. Then, uses those UUID to
     * export those entities to Dataverse using the Dataverse API key of the currently logged user.
     *
     * @throws IOException If something goes wrong during connection.
     */
    @Listen("onClick = #exportDataverse")
    public void onClickExportDataverseButton() throws IOException {
        this.treeQueue.publish(new Event(EventNames.EXPORT_SELECTED_ENTITY_EVENT, null, null));
    }

    /**
     * Sends an event to the tree to reload the data model and rerender it.
     */
    @Listen("onClick = #refreshButton")
    public void onRefreshButton() {
        this.treeQueue.publish(new Event(EventNames.REFRESH_EVENT, null, null));
    }
}
