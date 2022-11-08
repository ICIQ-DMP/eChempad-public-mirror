package org.ICIQ.eChempad.controllers;

/**
 * Exposes basic CRUD API calls in order to export data from the application into a Dataverse instance. As a controller
 * it basically handles the input-output data exchanges, such as the request and its answer. It forwards the call to the
 * {@code DataverseExporterService}.
 *
 * @see <a href="https://guides.dataverse.org/en/latest/api/intro.html">...</a>
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 14/10/2022
 */
public interface DataverseExporterController extends ExporterController{

}
