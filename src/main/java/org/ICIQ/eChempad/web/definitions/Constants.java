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
package org.ICIQ.eChempad.web.definitions;

public class Constants {

	public static final String CONTACT_EMAIL = "contact@iochem-bd.org";

	public enum ScreenSize { X_LARGE, LARGE, MEDIUM, SMALL, X_SMALL};
	
	public static final String PATH_DELIMITER = "/";
	public static final String BASE_PATH = PATH_DELIMITER + "db";	
	
	public static final String REPORT_TEMPLATES_FOLDER = "reports/";
	
	public static final String LOCAL_APP_DIR = "LOCAL_APP_DIR";	
	public static final String LOCAL_DIR = "LOCAL_DIR_OF_AREAS";	
	public static final String LOAD_CALC_TMP_DIR = "load_calc_tmp_dir";
	public static final String LOAD_CALC_WEB = "load_calc_web";
	
	public static final String BROWSE_LOGOUT_ENDPOINT = "/logout";
	public static final String BROWSE_SWORD_ENDPOINT = "/swordv2";
	public static final String BROWSE_SWORD_SERVICEDOCUMENT_ENDPOINT = BROWSE_SWORD_ENDPOINT +  "/servicedocument";
	public static final String BROWSE_HANDLE_ENDPOINT = "/handle";
	public static final String BROWSE_HANDLE_EDIT_ENDPOINT = "/edit-collection";
	public static final String BROWSE_HANDLE_REVIEW_ENDPOINT = "/review-collection";
	
	public static final String DOI_URL_PROPERTY = "doi.daemon.url";	
	public static final String REST_INSTITUTION_STATUS_ENDPOINT = "/institution/status/";
	public static final int INSTITUTION_STATUS_TEST_MANUAL = 0;			
	public static final int INSTITUTION_STATUS_REAL_MANUAL = 1;
	public static final int INSTITUTION_STATUS_TEST_AUTO = 2;
	public static final int INSTITUTION_STATUS_REAL_AUTO = 3;
	public static final int INSTITUTION_STATUS_DISABLED = 4;

	public static final String REST_ENDPOINT = "/rest";
	public static final String REST_DOIREQUEST_ENDPOINT = "/doi-request";
	public static final String REST_LOGIN_ENDPOINT = "/login-internal";
	public static final String REST_LOGOUT_ENDPOINT = "/logout";
	public static final String REST_ADMINISTRATOR_COMMUNITIES = "/community-administrator";
	public static final String REST_GET_TOPCOMMUNITIES_ENDPOINT = "/communities/top-communities";
	public static final String REST_GET_COMMUNITIES_ENDPOINT = "/communities";
	public static final String REST_GET_COMMUNITY_COMMUNITIES_ENDPOINT = "/communities/{communityId}/communities";
	public static final String REST_GET_COLLECTION_ENDPOINT = "/collections/{collectionId}";
	public static final String REST_POST_COLLECTION_ENDPOINT = "/communities/{communityId}/collections";
	public static final String REST_GET_COMMUNITY_COLLECTIONS_ENDPOINT = "/communities/{communityId}/collections";
	public static final String REST_OAI_COLLECTION_ENDPOINT = "/oai/collections/{collectionHandle}";
	
	public static final String IOCHEMBD_DIR_SYS_VAR = "IOCHEMBD_DIR";
	public static final String WEBAPPS_DIR = "webapps";
	public static final String CREATE_DIR = "create";	
	public static final String CREATE_ASSETSTORE_DIR = "assetstore";
	public static final String CREATE_PROPERTIES_FILE = "resources.properties";
	
	public static enum UploadType {soft, hard};
	
		
}
