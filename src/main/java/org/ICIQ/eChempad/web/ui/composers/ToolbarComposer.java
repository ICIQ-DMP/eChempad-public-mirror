package org.ICIQ.eChempad.web.ui.composers;

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
import java.util.logging.Logger;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ToolbarComposer extends SelectorComposer<Window> {

    @Wire
    private Button import_signals;

    @Wire
    private Button import_dataverse;

    @WireVariable("signalsImportService")
    private SignalsImportService signalsImportService;

    @WireVariable("journalService")
    private JournalService journalService;

    /**
     * De-facto constructor for composer components.
     *
     * @param comp Window component
     * @throws Exception If something goes wrong during initialization.
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        this.signalsImportService = (SignalsImportService) SpringUtil.getBean("signalsImportService");
    }


    @Listen("onClick = #import_signals")
    public void onClickImportDataverseButton() throws IOException {
        Logger.getGlobal().warning(journalService.findAll().toString());

        this.signalsImportService.importWorkspace();
    }
}
