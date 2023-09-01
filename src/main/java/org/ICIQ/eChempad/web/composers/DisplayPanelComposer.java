package org.ICIQ.eChempad.web.composers;

import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.services.LobService;
import org.ICIQ.eChempad.services.LobServiceImpl;
import org.ICIQ.eChempad.web.definitions.EventNames;
import org.ICIQ.eChempad.web.definitions.EventQueueNames;
import org.springframework.context.annotation.Scope;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DisplayPanelComposer extends SelectorComposer<Window> {

    /**
     * Div where we will display the content.
     */
    @Wire
    private Div contentDiv;

    /**
     * Receiving event queue
     */
    private EventQueue<Event> displayPanelQueue;

    @WireVariable("lobServiceImpl")
    private LobServiceImpl lobServiceImpl;

    // UI COMPOSER METHODS
    /**
     * De-facto constructor for composer components.
     *
     * @param comp Window component
     * @throws Exception If something goes wrong during initialization.
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        // Set the news
        Logger.getGlobal().warning("rendered news");
        //this.changeContent("news", null);

        this.initActionQueues();
    }


    /**
     * Initialize the action queues used to receive events and also declared the event listeners to attend to events.
     */
    private void initActionQueues(){

        this.displayPanelQueue = EventQueues.lookup(EventQueueNames.DISPLAY_PANEL_QUEUE, EventQueues.DESKTOP, true);
        this.displayPanelQueue.subscribe(event -> {
            switch (event.getName()) {
                case EventNames.DISPLAY_FILE_DISPLAY_PANEL_EVENT:
                {
                    Logger.getGlobal().warning("This is triggeringevent is panel");
                    Document file_data = (Document) event.getData();
                    this.changeContent(file_data.getOriginType(), this.lobServiceImpl.readBlob(file_data.getBlob()));
                    break;
                }
                default:
                {
                    Logger.getGlobal().warning("not recognized event");
                }
            }
        });
    }

    public Image convertInputStreamToImage(InputStream inputStream)
    {
        Image zkImage = null;
        try {
            // Read the data from the InputStream into a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Create a ZK Image component and set its content using the byte array
            zkImage = new org.zkoss.zul.Image();
            AImage aImage = new AImage("MyImage", byteArrayOutputStream.toByteArray());
            zkImage.setContent(aImage);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return zkImage;
    }

    // Method to change content dynamically
    public void changeContent(String contentType, InputStream content) {
        contentDiv.getChildren().clear(); // Clear previous content

        Logger.getGlobal().warning("the type is" + contentType);
        if ("text".equals(contentType)) {
            // Display plain text
            Label label = null;
            try {
                label = new Label(new String(content.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            contentDiv.appendChild(label);
        } else if ("imageResource".equals(contentType)) {
            // Display an image
            Image image = new Image();
            Image image_converted = this.convertInputStreamToImage(content);
            image.setContent((RenderedImage) image_converted);
            contentDiv.appendChild(image);
        } else if ("html".equals(contentType)) {
            // Display HTML using an iframe
            Iframe iframe = new Iframe();
            try {
                iframe.setSrc(new String(content.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            iframe.setWidth("100%");
            iframe.setHeight("100%");
            contentDiv.appendChild(iframe);
        }
        else if ("news".equals(contentType))
        {
            Iframe iframe = new Iframe();
            iframe.setSrc("/html/news.html");
            iframe.setHflex("1");
            iframe.setVflex("1");
            iframe.setId("newsFrame");

            contentDiv.appendChild(iframe);
        }
    }


}
