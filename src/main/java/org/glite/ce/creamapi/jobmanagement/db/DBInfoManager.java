/*
 * Copyright (c) Members of the EGEE Collaboration. 2004. 
 * See http://www.eu-egee.org/partners/ for details on the copyright
 * holders.  
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
 
/*
 *
 * Authors: Eric Frizziero <eric.frizziero@pd.infn.it>
 *
 */

package org.glite.ce.creamapi.jobmanagement.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.commonj.db.DatasourceManager;

public class DBInfoManager {

	/** The logger */
	private static final Logger logger = Logger.getLogger(DBInfoManager.class);

	public final static String NAME_TABLE         = "db_info";
	public final static String VERSION_FIELD      = "version";
	public final static String STORED_PROCEDURE_VERSION_FIELD = "stored_proc_version";
	public final static String CREATIONTIME_FIELD = "creationTime";
	public final static String STARTUPTIME_FIELD = "startUpTime";
	public final static String DELEGATIONSUFFIX_FIELD = "delegationSuffix";
	public final static String SUBMISSIONENABLED_FIELD = "submissionEnabled";

	private final static String selectVersionQuery      = getSelectVersionQuery();
	private final static String selectCreationTimeQuery = getSelectCreationTimeQuery();
	private final static String selectDelegationSuffixQuery = getSelectDelegationSuffixQuery();
    private final static String selectSubmissionEnabledQuery = getSelectSubmissionEnabledQuery();

    /**
     * This method updates the submissionEnabled field in the database.
     * @param datasourceName the datasource of the database. 
     * @param submissionEnabled the value to set.
     * @return 
     */
    public static void updateSubmissionEnabled(String datasourceName, int submissionEnabled) throws IllegalArgumentException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }
        logger.debug("Begin updateSubmissionEnabled for " + datasourceName  + " and submissionEnabled = " + submissionEnabled);
        Connection connection = null;
        PreparedStatement updatePreparedStatement = null;
        try{
            connection = DatasourceManager.getConnection(datasourceName);
            updatePreparedStatement = connection.prepareStatement(DBInfoManager.getUpdateSubmissionEnabledQuery(submissionEnabled));
            updatePreparedStatement.executeUpdate();
            connection.commit();
            logger.info("set submissionEnabled to " + submissionEnabled + " in the database");
        } catch (Exception e) {
            logger.warn("Problem to update submissionEnabled in the database");
        } finally {
            if (updatePreparedStatement != null) {
                try {
                    updatePreparedStatement.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle) {
                    logger.error(sqle);
                }
            }
        }
        logger.debug("End updateSubmissionEnabled for " + datasourceName  + " and submissionEnabled = " + submissionEnabled);
    }    

    /**
     * This method updates the startUpTime field in the database.
     * @param datasourceName the datasource of the database. 
     * @param startUpTime the value to set.
     * @return 
     */
    public static void updateStartUpTime(String datasourceName, Calendar startUpTime) throws DatabaseException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }
        logger.debug("Begin updateStartUpTime");
        Connection connection = null;
        PreparedStatement updatePreparedStatement = null;
        try{
            connection = DatasourceManager.getConnection(datasourceName);
            updatePreparedStatement = connection.prepareStatement(DBInfoManager.getUpdateStartUpTimeQuery());
            if (startUpTime == null) {
                updatePreparedStatement.setTimestamp(1, null);
            } else {
                updatePreparedStatement.setTimestamp(1, new java.sql.Timestamp(startUpTime.getTimeInMillis()));
            }
            updatePreparedStatement.executeUpdate();
            connection.commit();
            logger.info("set startUpTime in the database");
        } catch (Exception e) {
            logger.warn("Problem to update the startUpTime in the database");
            throw new DatabaseException("Problem to update the startUpTime in the database");
        } finally {
            if (updatePreparedStatement != null) {
                try {
                    updatePreparedStatement.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle) {
                    logger.error(sqle);
                }
            }
        }
        logger.debug("End updateStartUpTime");
    }  
    
    /**
     * This method returns the submissionEnabled field. Are submissions enabled?
     * @param datasourceName the datasource of the database
     * @return SubmissionEnabled (0=submission enabled); (1=submission disabled by limiter); (1=submission disabled by admin).  
     * If an exception occurres, the method returns the default value: 0.
     */
    public static int getSubmissionEnabled(String datasourceName) throws IllegalArgumentException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }

        logger.debug("Begin getSubmissionEnabled for " + datasourceName);
        int submissionEnabled = 0; //default
        Connection connection = null;
        PreparedStatement selectPreparedStatement = null;

        try{
            connection = DatasourceManager.getConnection(datasourceName);
            selectPreparedStatement = connection.prepareStatement(DBInfoManager.selectSubmissionEnabledQuery);
            // execute query, and return number of rows created
            ResultSet resultSet = selectPreparedStatement.executeQuery();
            if(resultSet.next()) {
                submissionEnabled = resultSet.getInt(DBInfoManager.SUBMISSIONENABLED_FIELD);
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + " It will be used the default value (=true). Submissions enabled!");
            submissionEnabled = 0; //default
        } finally {
            if (selectPreparedStatement != null) {
                try {
                    selectPreparedStatement.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle) {
                    logger.error(sqle);
                }
            }
        }
        logger.info("submissionEnabled = " + submissionEnabled + " for " + datasourceName);
        logger.debug("End getSubmissionEnabled for " + datasourceName);
        return submissionEnabled;
    }
    
    /**
     * This method returns the delegation suffix of the database identified by the datasource parameter.
     * @param datasourceName the datasource of the database
     * @return DelegationSuffix the delegation suffix.  If an exception occurres, the method returns null.
     */
    public static String getDelegationSuffix(String datasourceName) throws IllegalArgumentException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }

        logger.debug("Begin getDelegationSuffix for " + datasourceName);
        String delegationSuffix = null;
        Connection connection = null;
        PreparedStatement selectPreparedStatement = null;

        try{
            connection = DatasourceManager.getConnection(datasourceName);
            selectPreparedStatement = connection.prepareStatement(DBInfoManager.selectDelegationSuffixQuery);
            // execute query, and return number of rows created
            ResultSet resultSet = selectPreparedStatement.executeQuery();
            if(resultSet.next()) {
                delegationSuffix = resultSet.getString(DBInfoManager.DELEGATIONSUFFIX_FIELD);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            delegationSuffix = null;
        } finally {
            if (selectPreparedStatement != null) {
                try {
                    selectPreparedStatement.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle) {
                    logger.error(sqle);
                }
            }
        }
        logger.info("delegationSuffix = " + delegationSuffix + " for " + datasourceName);
        logger.debug("End getDelegationSuffix for " + datasourceName);
        return delegationSuffix;
    }

	/**
	 * This method returns the version of the database identified by the datasource parameter.
	 * @param datasourceName the datasource of the database
	 * @return DBVersion the database version.  If an exception occurres, the method returns null.
	 */
	public static String getDBVersion(String datasourceName) throws IllegalArgumentException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }

		logger.debug("Begin getDBVersion for " + datasourceName);
		String dbVersion = null;
		Connection connection = null;
		PreparedStatement selectPreparedStatement = null;

		try{
			connection = DatasourceManager.getConnection(datasourceName);
			selectPreparedStatement = connection.prepareStatement(DBInfoManager.selectVersionQuery);
			// execute query, and return number of rows created
			ResultSet resultSet = selectPreparedStatement.executeQuery();
			if(resultSet.next()) {
				dbVersion = resultSet.getString(DBInfoManager.VERSION_FIELD);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			dbVersion = null;
		} finally {
			if (selectPreparedStatement != null) {
				try {
					selectPreparedStatement.close();
				} catch (SQLException sqle1) {
					logger.error(sqle1);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqle) {
					logger.error(sqle);
				}
			}
		}
		logger.info("DB version = " + dbVersion + " for " + datasourceName);
		logger.debug("End getDBVersion for " + datasourceName);
		return dbVersion;
	}

	   /**
     * This method returns the version of the stored procedures on the db identified by the datasource parameter.
     * @param datasourceName the datasource of the database
     * @return The stored procedures version.  If an exception occurres, the method returns null.
     */
    public static String getStoredProcedureVersion(String datasourceName) throws IllegalArgumentException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }

        logger.debug("Begin getStoredProcedureVersion for " + datasourceName);
        String storedProcedureVersion = null;
        Connection connection = null;
        PreparedStatement selectPreparedStatement = null;

        try{
            connection = DatasourceManager.getConnection(datasourceName);
            selectPreparedStatement = connection.prepareStatement(DBInfoManager.getSelectStoredProcedureVersionQuery());
            // execute query, and return number of rows created
            ResultSet resultSet = selectPreparedStatement.executeQuery();
            if(resultSet.next()) {
                storedProcedureVersion = resultSet.getString(DBInfoManager.STORED_PROCEDURE_VERSION_FIELD);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            storedProcedureVersion = null;
        } finally {
            if (selectPreparedStatement != null) {
                try {
                    selectPreparedStatement.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle) {
                    logger.error(sqle);
                }
            }
        }
        logger.info("Stored Procedure Version = " + storedProcedureVersion + " for " + datasourceName);
        logger.debug("End getStoredProcedureVersion for " + datasourceName);
        return storedProcedureVersion;
    }

	//if there is some problem, getCreationTime method returns -1!
	public static long getCreationTime(String datasourceName) throws IllegalArgumentException {
        if(datasourceName == null) {
            throw new IllegalArgumentException("datasourceName not specified!");
        }

		logger.debug("Begin getCreationTime for " + datasourceName);
		long creationTime = -1;
		Connection connection = null;
		PreparedStatement selectPreparedStatement = null;

		try{
			connection = DatasourceManager.getConnection(datasourceName);
			selectPreparedStatement = connection.prepareStatement(DBInfoManager.selectCreationTimeQuery);
			// execute query, and return number of rows created
			ResultSet resultSet = selectPreparedStatement.executeQuery();
			if(resultSet.next()) {
				creationTime = resultSet.getTimestamp(DBInfoManager.CREATIONTIME_FIELD).getTime();
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			creationTime = -1;
		} finally {
			if (selectPreparedStatement != null) {
				try {
					selectPreparedStatement.close();
				} catch (SQLException sqle1) {
					logger.error(sqle1);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqle) {
					logger.error(sqle);
				}
			}
		}
		logger.info("CreationTime DB = " + creationTime + " for " + datasourceName);
		logger.debug("End getCreationTime for " + datasourceName);
		return creationTime;
	}
	
   private static String getUpdateStartUpTimeQuery(){
        StringBuffer updateStartUpTimeQuery = new StringBuffer();
        updateStartUpTimeQuery.append("update " + DBInfoManager.NAME_TABLE);
        updateStartUpTimeQuery.append(" SET " + DBInfoManager.STARTUPTIME_FIELD + " = ?");
        return updateStartUpTimeQuery.toString();
	}

   private static String getUpdateSubmissionEnabledQuery(int submissionEnabled){
       StringBuffer updateSubmissionEnabledQuery = new StringBuffer();
       updateSubmissionEnabledQuery.append("update " + DBInfoManager.NAME_TABLE); 
       updateSubmissionEnabledQuery.append(" SET " + DBInfoManager.SUBMISSIONENABLED_FIELD + " = " + submissionEnabled);
       logger.debug("updateSubmissionEnabledQuery = " + updateSubmissionEnabledQuery.toString());
       return updateSubmissionEnabledQuery.toString();
   }
	
	private static String getSelectSubmissionEnabledQuery(){
        StringBuffer selectSubmissionEnabledQuery = new StringBuffer();
        selectSubmissionEnabledQuery.append("select " + DBInfoManager.SUBMISSIONENABLED_FIELD + " AS " + DBInfoManager.SUBMISSIONENABLED_FIELD);
        selectSubmissionEnabledQuery.append(" from " + DBInfoManager.NAME_TABLE);
        logger.debug("selectSubmissionEnabledQuery = " + selectSubmissionEnabledQuery.toString());
        return selectSubmissionEnabledQuery.toString();
    }
	
	private static String getSelectDelegationSuffixQuery(){
        StringBuffer selectDelegationSuffixQuery = new StringBuffer();
        selectDelegationSuffixQuery.append("select " + DBInfoManager.DELEGATIONSUFFIX_FIELD + " AS " + DBInfoManager.DELEGATIONSUFFIX_FIELD);
        selectDelegationSuffixQuery.append(" from " + DBInfoManager.NAME_TABLE);
        logger.debug("selectDelegationSuffixQuery = " + selectDelegationSuffixQuery.toString());
        return selectDelegationSuffixQuery.toString();
    }
	   
	private static String getSelectVersionQuery(){
		StringBuffer selectVersionQuery = new StringBuffer();
		selectVersionQuery.append("select " + DBInfoManager.VERSION_FIELD + " AS " + DBInfoManager.VERSION_FIELD);
		selectVersionQuery.append(" from " + DBInfoManager.NAME_TABLE);
		logger.debug("selectVersionQuery = " + selectVersionQuery.toString());
		return selectVersionQuery.toString();
	}
	
	   private static String getSelectStoredProcedureVersionQuery(){
	        StringBuffer selectStoredProcedureVersionQuery = new StringBuffer();
	        selectStoredProcedureVersionQuery.append("select " + DBInfoManager.STORED_PROCEDURE_VERSION_FIELD + " AS " + DBInfoManager.STORED_PROCEDURE_VERSION_FIELD);
	        selectStoredProcedureVersionQuery.append(" from " + DBInfoManager.NAME_TABLE);
	        logger.debug("selectStoredProcedureVersionQuery = " + selectStoredProcedureVersionQuery.toString());
	        return selectStoredProcedureVersionQuery.toString();
	    }
	
	private static String getSelectCreationTimeQuery(){
		StringBuffer selectCreationTimeQuery = new StringBuffer();
		selectCreationTimeQuery.append("select " + DBInfoManager.CREATIONTIME_FIELD + " AS " + DBInfoManager.CREATIONTIME_FIELD);
		selectCreationTimeQuery.append(" from " + DBInfoManager.NAME_TABLE);
		logger.debug("selectCreationTimeQuery = " + selectCreationTimeQuery.toString());
		return selectCreationTimeQuery.toString();
	}
}
