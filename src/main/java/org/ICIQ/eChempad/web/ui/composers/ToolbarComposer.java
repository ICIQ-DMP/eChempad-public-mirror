package org.ICIQ.eChempad.web.ui.composers;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.SignalsImportService;
import org.ICIQ.eChempad.services.genericJPAServices.EntityConversionService;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.springframework.context.annotation.Scope;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Logger;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ToolbarComposer extends SelectorComposer<Window> {

    @Wire
    private Button importSignals;

    @Wire
    private Button importDataverse;

    @Wire
    private Button refreshButton;

    /**
     * Used to send events to the tree.
     */
    private EventQueue<Event> treeQueue;

    @WireVariable("signalsImportService")
    private SignalsImportService signalsImportService;

    @WireVariable("journalService")
    private JournalService<Journal, UUID> journalService;

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

    private void initActionQueues(){
        this.treeQueue = EventQueues.lookup(EventQueueNames.TREE_QUEUE, EventQueues.DESKTOP, true);
    }

    @Listen("onClick = #importSignals")
    public void onClickImportSignalButton() throws IOException {

        Logger.getGlobal().warning(journalService.findAll().toString());

        this.signalsImportService.importWorkspace();
    }


    @Listen("onClick = #refreshButton")
    public void onClickRefreshButton() throws IOException {
        this.treeQueue.publish(new Event(EventNames.REFRESH_EVENT, null, null));
    }
}
