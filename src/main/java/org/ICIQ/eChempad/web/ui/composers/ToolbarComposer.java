package org.ICIQ.eChempad.web.ui.composers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.SignalsImportService;
import org.ICIQ.eChempad.services.genericJPAServices.EntityConversionService;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.context.annotation.Scope;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ToolbarComposer extends SelectorComposer<Window> {

    @Wire
    private Button importSignals;

    @Wire
    private Button importDataverse;

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
    }


    @Listen("onClick = #importSignals")
    public void onClickImportSignalButton() throws IOException {

        Logger.getGlobal().warning(journalService.findAll().toString());

        this.signalsImportService.importWorkspace();
    }
}
