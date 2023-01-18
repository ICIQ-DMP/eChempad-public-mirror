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

public class Queries {
	
	/**
	 * Calculations' table definition
	 */
	public static final String CALCULATIONS_TABLE = "calculations";	
	public static final String CALCULATIONS_ID_COLUMN = "id";
	public static final String CALCULATIONS_TYPE_ID_COLUMN = "type_id";
	public static final String CALCULATIONS_CREATION_TIME_COLUMN = "creation_time";
	public static final String CALCULATIONS_NAME_COLUMN = "name";
	public static final String CALCULATIONS_PATH_COLUMN = "path";
	public static final String CALCULATIONS_DESCRIPTION_COLUMN = "description";
	public static final String CALCULATIONS_METS_XML_COLUMN = "mets_xml";
	public static final String CALCULATIONS_PUBLISHED_COLUMN = "published";
	public static final String CALCULATIONS_HANDLE_COLUMN = "handle";
	public static final String CALCULATIONS_PUBLISHED_NAME_COLUMN = "published_name";
	public static final String CALCULATIONS_PUBLICATION_TIME_COLUMN = "publication_time";
	public static final String CALCULATIONS_ELEMENT_ORDER_COLUMN = "element_order";

	
	public static String existsCalculationWithPathAndName = "SELECT count(" + CALCULATIONS_ID_COLUMN + ") " + 
															" FROM " +  CALCULATIONS_TABLE + 
															" WHERE " + CALCULATIONS_PATH_COLUMN + " = ? " +
														  	" AND " +  CALCULATIONS_NAME_COLUMN + " = ? ;";
	
	public static String getCalculationByPathAndName = "SELECT " + CALCULATIONS_ID_COLUMN + " , " + 
																CALCULATIONS_TYPE_ID_COLUMN + " , " + 
																CALCULATIONS_CREATION_TIME_COLUMN + " , " + 
																CALCULATIONS_NAME_COLUMN + " , " + 
																CALCULATIONS_PATH_COLUMN + " , " + 
																CALCULATIONS_DESCRIPTION_COLUMN + " , " + 
																CALCULATIONS_METS_XML_COLUMN + " , " + 
																CALCULATIONS_PUBLISHED_COLUMN + " , " + 
																CALCULATIONS_HANDLE_COLUMN + " , " + 
																CALCULATIONS_PUBLISHED_NAME_COLUMN + " , " + 
																CALCULATIONS_PUBLICATION_TIME_COLUMN + " , " +
																CALCULATIONS_ELEMENT_ORDER_COLUMN + 
														" FROM " +  CALCULATIONS_TABLE + 
													    " WHERE " + CALCULATIONS_PATH_COLUMN + " = ? " +
													    " AND " +  CALCULATIONS_NAME_COLUMN + " = ? ;";
	
	public static final String PROJECTS_TABLE = "projects";
	public static final String PROJECTS_ID_COLUMN = "id";
	public static final String PROJECTS_PERMISSIONS_COLUMN = "permissions";
	public static final String PROJECTS_OWNER_USER_ID_COLUMN = "owner_user_id";
	public static final String PROJECTS_OWNER_GROUP_ID_COLUMN = "owner_group_id";	
	public static final String PROJECTS_CREATION_TIME_COLUMN = "creation_time";
	public static final String PROJECTS_CONCEPT_GROUP_COLUMN = "concept_group";
	public static final String PROJECTS_NAME_COLUMN = "name";
	public static final String PROJECTS_PATH_COLUMN = "path";
	public static final String PROJECTS_DESCRIPTION_COLUMN = "description";
	public static final String PROJECTS_MODIFICATION_TIME_COLUMN = "modification_time";
	public static final String PROJECTS_CERTIFICATION_TIME_COLUMN = "certification_time";
	public static final String PROJECTS_PUBLISHED_COLUMN = "published";
	public static final String PROJECTS_HANDLE_COLUMN = "handle";
	public static final String PROJECTS_PUBLISHED_NANE_COLUMN = "published_name";
	public static final String PROJECTS_PUBLICATION_TIME_COLUMN = "publication_time";
	public static final String PROJECTS_STATE_COLUMN = "state";
	public static final String PROJECTS_ELEMENT_ORDER_COLUMN = "element_order";
			
	public static final String existsProjectWithPathAndName = "SELECT count(" + PROJECTS_ID_COLUMN + ") " + 
																" FROM " + PROJECTS_TABLE + 
																" WHERE " + PROJECTS_PATH_COLUMN + " = ? " + 
																" AND " +  PROJECTS_NAME_COLUMN + " = ? ";
	
	

	/*
	 * Wilchard words: 
	 * 		@USER_GROUPS : replaced for a list of user groups
	 */
	public static final String CHECK_WRITE_PERMISSIONS = "select name from projects \n"+ 
														 "where ( \n"+
													     "((owner_group_id in (@USER_GROUPS)) \n"+
													      "and \n"+ 
													      "((projects.permissions & B'000100' ) = B'000100') \n"+
													     ")  -- write permission on the group \n"+
													     "or \n" +
													     "((projects.owner_user_id=?) \n" +
													       "and \n" + 
													       "((projects.permissions & B'010000' ) = B'010000') \n" + 
													     ")  -- write permission on owner \n" + 
													     "or \n"+ 
													     "((projects.permissions & B'000001' ) = B'000001') \n" +
													     "-- write permissions on other \n" + 
													     ") \n" + 
													     "and (path=? and name=?) \n";

	
	public static final String READ_CHECK			  =
													     "(((owner_group_id in (@USER_GROUPS)) \n"+
													      "and \n"+ 
													      "((projects.permissions & B'001000' ) = B'001000') \n"+
													     ")  -- read permission on the group \n"+
													     "or \n" +
													     "((projects.owner_user_id=?) \n" +
													       "and \n" + 
													       "((projects.permissions & B'100000' ) = B'100000') \n" + 
													     ")  -- read permission on owner \n" + 
													     "or \n"+ 
													     "((projects.permissions & B'000010' ) = B'000010') \n" +
													     ")-- read permissions on other \n" ; 

	
	public static final String CHECK_READ_PERMISSIONS =  "select name from projects \n"+ 
														 "where \n"+
														 READ_CHECK + 
													     "and (path=? and name=?) \n";

	public static final String INSERT_PROJECT = "insert into projects ( name,path,description,"+
												   "owner_user_id,owner_group_id,"+
												   "concept_group, element_order) values (?,?,?,?,?,?,?) returning id";
	

	public static final String LIST_PROJECT = 	"select  name,"+
														"description,\n"+
														"permissions,\n"+
														"owner_user_id as owner,\n"+
														"owner_group_id as group_name,\n"+
														"to_char(creation_time,'YYYY-MM-DD HH24:MI:SS') as time,\n"+
														"concept_group,\n"+
														"state\n" +
												"from projects\n";
											
	
	public static final String LIST_PARENT_PROJECT 	= LIST_PROJECT + "where path=?\n"; 
	
	public static final String LIST_CURRENT_PROJECT = LIST_PROJECT + "where path=? and name=?\n";
	
	public static final String LIST_CALC_OF_PROJECT = 	"select (select abr from calculation_types where id=calculations.type_id),  name,\n"+
															 	"description,\n"+
															 	"to_char(creation_time,'YYYY-MM-DD HH24:MI:SS') as time\n"+
														"from calculations\n"+
														"where path = ?\n";
	// order by statements
	
	public static final String OBName				= " order by name\n";
	public static final String OBCreationTime		= " order by creation_time\n";
	public static final String OBOwner				= " order by owner\n";
	public static final String OBGroup				= " order by group_name\n";
	public static final String OBConceptGroup		= " order by concept_group\n";
	public static final String OBState				= " order by state\n";
		
	public static final String CREATE_PROJECT 		=   "insert into projects ( " +
															"id, " + 
															"name, " + 
															"path, " + 
															"description, " + 
															"owner_user_id, " + 
															"owner_group_id,"+
															"concept_group, " +															
															"element_order, " +
															"permissions " +
														") values (?,?,?,?,?,?,?,?,?::bit(6))";
  
	public static final String GET_NEXT_PROJECT_ID  =  "SELECT nextval('projects_id_seq')"; 
	
	public static final String GET_PROJECT 			= "select  name,"+
														"description,\n"+
														"permissions,\n"+
														"owner_user_id  as owner,\n"+
														"owner_group_id as group,\n"+
														"path,\n"+
														"concept_group,\n"+
														"to_char(creation_time,'YYYY-MM-DD HH24:MI:SS') as time,\n"+
														"to_char(modification_time,'YYYY-MM-DD HH24:MI:SS') as time,\n"+
														"to_char(certification_time,'YYYY-MM-DD HH24:MI:SS') as time,\n"+
														"state\n" +
													"from projects\n"+
													"where path=? and name=?\n";
	
	public static final String GET_PROJECT_BY_ID  = "select permissions,"+
														"owner_user_id,"+
														"owner_group_id,"+
														"id," + 
														"creation_time,"+
														"concept_group,"+
														"name,"+
														"path,"+
														"description," +
														"modification_time," +
														"certification_time," +
														"published," + 
														"handle," +
														"published_name," +
														"publication_time," +
														"state, " +
														"element_order " +
													"from projects "+
													"where id = ?";
	
	public static final String GET_PROJECT_BY_PATH  = "select permissions,"+
															"owner_user_id,"+
															"owner_group_id,"+
															"id," + 
															"creation_time,"+
															"concept_group,"+
															"name,"+
															"path,"+
															"description," +
															"modification_time," +
															"certification_time," +
															"published," + 
															"handle," +
															"published_name," +
															"publication_time," +
															"state, " +
															"element_order " +
														"from projects "+
														"where path=? and name=?\n";
	
	public static final String GET_CHILD_PROJECTS_BY_PARENT_PATH = "select permissions,"+
																		"owner_user_id,"+
																		"owner_group_id,"+
																		"id," + 
																		"creation_time,"+
																		"concept_group,"+
																		"name,"+
																		"path,"+
																		"description," +
																		"modification_time," +
																		"certification_time," +
																		"published," + 
																		"handle," +
																		"published_name," +
																		"publication_time," +
																		"state, " +
																		"element_order " +
																	"from projects "+
																	"where path=?";
	
	
	public static final String DELETE_PROJECT_BY_ID = "delete from projects where id = ?";
	
	public static final String GET_PROJECT_ALL_CHILD_CALCULATIONS_IDS = "select id from calculations where path like ? or path like ?";
	
	public static final String GET_PROJECT_OWNERS_AND_PERMISSSIONS_BY_PATH = "select owner_user_id, owner_group_id, permissions from projects where path = ? and name = ?";
	
	public static final String GET_CALCULATION_BY_ID = "select id," +
																"type_id," +
															    "creation_time," +
																"name," +
															    "path," +
															    "description," +
															    "mets_xml, " +
															    "published," +
															    "handle," +
															    "published_name," +
															    "publication_time, "+
															    "element_order " + 
													   "from calculations where id=?";

	public static final String GET_CALCULATION_BY_PATH = "select id," +
																"type_id," +
															    "creation_time," +
																"name," +
															    "path," +
															    "description," +
															    "mets_xml, " +
															    "published," +
															    "handle," +
															    "published_name," +
															    "publication_time, "+
															    "element_order " + 
													   "from calculations where path=? and name=?";

	
	public static final String GET_CHILD_CALCULATIONS_BY_PARENT_PATH = "select id," +
																				"type_id," +
																			    "creation_time," +
																				"name," +
																			    "path," +
																			    "description," +
																			    "mets_xml, " +
																			    "published," +
																			    "handle," +
																			    "published_name," +
																			    "publication_time, "+
																			    "element_order " + 
																	   "from calculations where path=?";

	public static final String DELETE_CALCULATION_AREAS_REF_BY_CALC_ID = "delete from areas_file_ref where calculation_id = ?";
	public static final String DELETE_CALCULATION_BY_ID = "delete from calculations where id = ?";
	
	public static final String GET_CALCULATION_FILES_BY_CALC_ID = "select id, calculation_id, name, file from areas_file_ref where calculation_id = ?";
	
	public static final String UPDATE_CALCULATION = "update calculations set " +
														"name = ?, " +
														"description = ?, " +														
														"path = ? " +
													"where id = ?";
	
	public static final String GET_ACTIONS = "select id, mimetype, jumbo_format, action, parameters from actions";
	
	
	
	public static final String START_FIND_PRO 		= "select name,path from projects where "+
													   READ_CHECK;

	public static final String NAME_SEARCH 			= "and (name ~* ?) \n ";
	public static final String PATH_SEARCH 			= "and (path ~* ?) \n ";
	public static final String DESC_SEARCH 			= "and (description ~* ?) \n ";
	
	public static final String GET_CAL_TYPES		=	"select  cutting_area_definitions.file_name as cal_file,"	+
														"calculation_types.name as cal_type,"						+
														"cutting_area_definitions.url as url,"					+
														"cutting_area_definitions.default_type_sel as def_type,"	+
														"cutting_area_definitions.abbreviation as abbreviation,"	+
														"cutting_area_definitions.jumbo_converter_class as jumbo_class," +
													    "cutting_area_definitions.jumbo_converter_in_type as jumbo_in_type," +
													    "cutting_area_definitions.jumbo_converter_out_type as jumbo_out_type," +
													    "cutting_area_definitions.mimetype as mimetype," +
													    "cutting_area_definitions.use as use," +
													    "cutting_area_definitions.label as label," +													    
													    "cutting_area_definitions.behaviour as behaviour, " +
													    "cutting_area_definitions.requires as requires, " +													    
													    "cutting_area_definitions.renameto as renameto " +													    													    
														"from calculation_types,cutting_area_definitions "	+
														"where calculation_types.id = cutting_area_definitions.calculation_type_id\n";
		
		
	
	public static final String GET_CALCULATION_NEXT_ID  = "SELECT nextval('dirs_id')";
	public static final String CREATE_CALC 				= "insert into calculations (id, type_id, name, path, description, element_order) values (?,?,?,?,?,?)";
	

	public static final String CREATE_AREA_FILE_REF = "insert into areas_file_ref (calculation_id,name,file) values (?,?,?)";
	public static final String SET_CALCULATION_METS = "update calculations set mets_xml = ? where id = ?";
	
	public static final String GET_CALCULATION = "select id,name,description,path,creation_time," +
														"(select abr from calculation_types where id=calculations.type_id) as abr, mets_xml as mets, " +
														"published, handle, published_name, publication_time \n"+
														"from calculations where path=? and name=?\n";
		
	public static final String GET_CALCULATION_METS = 	"select mets_xml as mets from calculations where id=?";
	

	public static final String CALCULATION_EXIST = "select id from calculations where path=? and name=?";
	public static final String GET_PROJECT_PATH = 	"select path from calculations "+
													"where id=(select calculation_id from areas_file_ref where file=?)";
	public static final String GET_AREAS_FILE_REF = "select file from areas_file_ref where calculation_id=?";
	
	public static final String GET_CALCULATION_TYPES = "select id, creation_time, abr, name, description, metadata_template from calculation_types";
	public static final String GET_CALCULATION_TYPE_BY_NAME = "select id, creation_time, abr, name, description, metadata_template from calculation_types where name = ?";
	public static final String GET_CALCULATION_TYPE_BY_ID = "select id, creation_time, abr, name, description, metadata_template from calculation_types where id = ?";

	public static final String GET_CALCULATION_TYPE_FILES = "select " +
																"id, " + 
																"calculation_type_id, " + 
																"default_type_sel, " + 
																"abbreviation, " + 
																"file_name, " + 
																"url, " + 
																"description, " + 
																"jumbo_converter_class, " + 
																"jumbo_converter_in_type, " + 
																"jumbo_converter_out_type, " + 
																"mimetype, " + 
																"use, " + 
																"label, " + 
																"behaviour " + 
															"from cutting_area_definitions";
																	
	// project modification 
	public static final String UPDATE_PROJECT_BY_ID	= 	"update projects set " + 
															"owner_user_id = ?, " +
															"owner_group_id = ?, " +
															"description = ?, " +
															"name = ?, " + "path = ?, " +
															"concept_group = ?, " +
															"modification_time = current_timestamp, " +
															"permissions = ?::bit(6), " +
															"state='modified' " +
														"where id = ?";
	
	public static final String UPDATE_PROJECT_PATHS = "update projects set path=regexp_replace(path,? ,?) where (path like ?) or (path=?)";
	
	public static final String UPDATE_CALCULATION_PATHS = "update calculations set path=regexp_replace(path, ?, ?) where (path like ?) or (path=?)";
		
	public static final String UPDATE_SUBPROJECTS_PERMISSIONS = "update projects set owner_group_id = ?, permissions = ?::bit(6) where path = ? OR path like ?";
	
	// calculation modification:
	public static final String START_MOD_CALC		= 	"update calculations\n"+
														"set ";
	public static final String END_MOD_CALC			=   " where path=? and name=?\n";
	
	// project / calculation publication
	public static final String PUBLISH_PROJECT 		= "update projects     set published=true, handle=?, published_name=?, publication_time=current_timestamp, state='published' where id=?";
	public static final String PUBLISH_CALCULATION	= "update calculations set published=true, handle=?, published_name=?, publication_time=current_timestamp  where id=?";
	
	
	
	/**
	 * Report queries
	 */
	
	public static final String GET_REPORT_BY_ID = "select " + 
														"id, " + 
														"name, " + 
														"title, " + 
														"description, " + 
														"creation_time, " + 
														"owner_id, " + 
														"configuration, " + 
														"published, " + 
														"type " +
													"from reports " + 
													"where id = ?";
		
	
	public static final String GET_USER_REPORTS =	"select " +
														"id, " + 
														"name, " +
														"title, " + 
														"description, " + 
														"creation_time, " + 
														"configuration, " + 
														"type, " + 
														"published " + 
													"from reports " +
													"where owner_id = ? order by creation_time";	


	public static final String GET_REPORT_TYPES = "select id, name, output_type, associated_xslt, associated_zul, enabled from report_type";
	public static final String GET_REPORT_TYPE_BY_ID = "select id, name, output_type, associated_xslt, associated_zul, enabled from report_type where id = ?";	

	public static final String GET_REPORT_CALCULATIONS = "select " + 
															"report_calculations.id, " + 
															"report_calculations.calc_order, " + 
															"report_calculations.title, " + 
															"report_calculations.calc_id, " + 
															"calculations.path || '/' || calculations.name as calc_path, " +
															"report_calculations.report_id " +
														"from report_calculations, calculations " + 
														"where report_id= ? and report_calculations.calc_id = calculations.id order by calc_order";	

	public static final String DELETE_REPORT = "delete from reports where id = ?";
	public static final String DELETE_REPORT_CALCULATIONS = "delete from report_calculations where report_id = ?";
	public static final String UPDATE_REPORT = "update reports set name = ?, title = ?, description = ? , configuration = ?, published = ? where id= ?";

	public static final String CREATE_REPORT = "insert into reports(id, creation_time, owner_id, name, title, description, configuration, type, published) values (?,?,?,?,?,?,?,?,?)";
	public static final String GET_REPORT_NEXT_ID = "select nextval('reports_id_seq')";
	public static final String CREATE_REPORT_CALCULATION = "insert into report_calculations(report_id,calc_order,title, calc_id) values (?,?,?,?) returning id;";
	
	//Search type queries
	public static final String SELECT_PROJECTS_AND_CALCULATIONS = "select projects.id, projects.path, projects.name, projects.description, calculations.id, calculations.path, calculations.name, calculations.description from projects full outer join calculations on (projects.path|| '/'|| projects.name) = calculations.path"; 
	public static final String SELECT_PROJECTS = "select projects.id, projects.path, projects.name, projects.description from projects";
	public static final String SELECT_CALCULATIONS = "select  projects.id, projects.path, projects.name, projects.description, calculations.id, calculations.path, calculations.name, calculations.description from calculations calculations left join projects projects on calculations.path = (projects.path|| '/'|| projects.name)";
	
	
	public static final String getCHANGE_PERM(String perm)
	{
		return 	"permissions = B'"+perm+"',";				
	}

	public static String getMOD_PROJ_CALC_PATH(String oldParentPath,String newParentPath) {
		String oldParentPathEscaped = escape(oldParentPath);				
		return  "update projects\n"+
				"set path=regexp_replace(path,'^"+oldParentPathEscaped+"','"+newParentPath+"')\n"+ 
				"where (path like '"+oldParentPath+"/%') or (path='"+oldParentPath+"');\n"+
				"update calculations\n"+
				"set path=regexp_replace(path,'^"+oldParentPathEscaped+"','"+newParentPath+"')\n"+ 
				"where (path like '"+oldParentPath+"/%') or (path='"+oldParentPath+"');\n";
	}
	public static String getREMOVE_CALC(String path,String name)
	{
		return  "delete from areas_file_ref\n" +  
				"where calculation_id in (select id from calculations where path='"+path+"' and name='"+name+"' );\n"+ 				 
				"delete from calculations\n" + 
				"where path='"+path+"' and name='"+name+"';\n";
	}
	
	public static String getINSERT_PROJECT_WITH_PERMS(String permissions){
		String insertProject = "insert into projects (name,path,description,"+	
													  "owner_user_id,owner_group_id,"+
													  "concept_group, element_order, permissions) "
													  + "values (?,?,?,?,?,?,?," + permissions + ") returning id";
		return insertProject;
	}
	
	public static String getREMOVE_PROJECT(String path,String name)
	{
		String pathToRemove = path + "/" + name;
		return  "delete from areas_file_ref\n" +  
				"where calculation_id in (select id from calculations where (path like '"+pathToRemove+"/%') or (path='"+pathToRemove+"'));\n"+
				"delete from calculations\n" + 
				"where (path like '"+pathToRemove+"/%') or (path='"+pathToRemove+"');\n" + 
				"delete from projects\n" +
				"where path = '"+path+"' and name = '"+name+"';\n"+
				"delete from projects\n" + 
				"where (path like '"+pathToRemove+"/%') or (path='"+pathToRemove+"');\n";
		
	}
	
	public static String getREMOVE_CALC(String calcId)
	{
		return  "delete from areas_file_ref\n" +  
				"where calculation_id ='" + calcId + "';\n"+ 	
				"delete from calculations\n" + 
				"where id='"+ calcId+"';\n";		
	}
	
	
	
	
	public static String escape(String inString)
	{
	    StringBuilder builder = new StringBuilder(inString.length() * 2);
	    String toBeEscaped = "\\{}()[]*+?.|^$";
	    for (int i = 0; i < inString.length(); i++){
	        char c = inString.charAt(i);
	        if (toBeEscaped.contains(Character.toString(c))){
	            builder.append('\\');
	        }
	        builder.append(c);
	    }
	    return builder.toString();
	}
 
}
