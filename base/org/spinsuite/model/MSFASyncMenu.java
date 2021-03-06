/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2013 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpconsultoresyasociados.com               *
 *****************************************************************************/
package org.spinsuite.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.DB;
import org.compiere.util.Env;


/**
 * @author <a href="mailto:yamelsenih@gmail.com">Yamel Senih</a>
 *
 */
public class MSFASyncMenu extends X_SFA_SyncMenu implements I_SFA_SyncMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6909895480750744213L;
	
	/**
	 * *** Constructor ***
	 * @author <a href="mailto:yamelsenih@gmail.com">Yamel Senih</a> 22/05/2013, 01:25:27
	 * @param ctx
	 * @param SFA_SyncMenu_ID
	 * @param trxName
	 */
	public MSFASyncMenu(Properties ctx, int SFA_SyncMenu_ID, String trxName) {
		super(ctx, SFA_SyncMenu_ID, trxName);
	}

	/**
	 * *** Constructor ***
	 * @author <a href="mailto:yamelsenih@gmail.com">Yamel Senih</a> 22/05/2013, 01:25:27
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MSFASyncMenu(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * 
	 * @author <a href="mailto:carlosaparadam@gmail.com">Carlos Parada</a> 11/02/2014, 23:42:07
	 * @param p_ParentNode
	 * @param p_CurrentItems
	 * @return
	 * @return List<MSFASyncMenu>
	 */
	public static List<MSFASyncMenu> getNodes(int p_ParentNode,String p_WebServiceDefinition) {
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MSFASyncMenu> items = new ArrayList<MSFASyncMenu>();
				
		sql.append( "SELECT treend.Parent_ID,treend.Node_ID,treend.SeqNo,CASE WHEN Qty IS NULL THEN 'N' ELSE 'Y' END As HasNodes " + 
					"FROM " +
					"AD_Tree tree " + 
					"INNER JOIN AD_Table tab ON tree.AD_Table_ID = tab.AD_Table_ID " +
					"INNER JOIN AD_TreeNode treend On treend.AD_Tree_ID = tree.AD_Tree_ID "+
					"LEFT JOIN (SELECT Count(1) Qty,Parent_ID,AD_Tree_ID FROM AD_TreeNode GROUP BY Parent_ID,AD_Tree_ID) hasnodes ON hasnodes.Parent_ID=treend.Node_ID AND hasnodes.AD_Tree_ID=treend.AD_Tree_ID " +
					"LEFT JOIN SFA_SyncMenu sm ON treend.Node_ID = sm.SFA_SyncMenu_ID " +
					"LEFT JOIN WS_WebService ws ON ws.WS_WebService_ID = sm.WS_WebService_ID " +
					"WHERE tab.TableName = ? AND treend.Parent_ID = ? AND ws.Value = ? AND sm.IsActive ='Y' AND ws.IsActive='Y' " +
					"ORDER By treend.SeqNo ");
		
		try{
			ps = DB.prepareStatement(sql.toString(),null);
			ps.setString(1, MSFASyncMenu.Table_Name);
			ps.setInt(2, p_ParentNode);
			ps.setString(3, p_WebServiceDefinition);
			rs = ps.executeQuery();
			
			while (rs.next()){
				if (rs.getString("HasNodes").equals("Y"))
					items.addAll(getNodes(rs.getInt("Node_ID"),p_WebServiceDefinition));
				else
					items.add(new MSFASyncMenu(Env.getCtx(), rs.getInt("Node_ID"), null));
			}
		}
		catch(SQLException ex){
			new AdempiereException(ex.getMessage());
		}
		finally{
			DB.close(rs, ps);
		    rs = null; ps = null;	
		}
		return items;
	}//getNodes

	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord
	 *	@return save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		//	Changed from Not to Active
		/*if (!newRecord && is_ValueChanged("IsActive") && isActive())
		{
			log.severe ("Cannot Re-Activate deactivated Allocations");
			return false;
		}
		
		//		
		if(getArea() == null
				|| getArea().equals(Env.ZERO)) {
			throw new AdempiereException("@Area@ = @0@");
		} else if(getArea().compareTo(getFTA_FarmDivision().getArea()) > 0){
			throw new AdempiereException("@Area@ > @Area@ @of@ @FTA_FarmDivision_ID@");
		}*/
		return true;
	}	//	beforeSave

	
	
}