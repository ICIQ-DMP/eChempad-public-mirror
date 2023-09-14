/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.web.composers;

import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.services.importServices.SignalsImportService;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.List;
import java.util.logging.Logger;

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
        String currentUserAPIKey = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getSignalsAPIKey();

        List<DataEntity> rootEntities = this.signalsImportService.readRootEntities(currentUserAPIKey);
        Logger.getGlobal().warning("root entities read " + rootEntities.toString());

        for (DataEntity dataEntity : rootEntities)
        {
            this.signalsImportService.updateRootContainer((Container) dataEntity, currentUserAPIKey);
        }

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
