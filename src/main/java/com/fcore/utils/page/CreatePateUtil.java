package com.fcore.utils.page;

import java.io.IOException;
import java.util.Map;

import com.fcore.commons.Constans;
import com.fcore.entity.DbEntity;
import com.fcore.entity.FileEntity;
import com.fcore.entity.FlagEntity;
import com.fcore.entity.PageEntity;
import com.fcore.entity.TableEntity;
import com.fcore.utils.StringUtil;
import com.fcore.utils.database.CreateEntityUtil;
import com.fcore.utils.freemarker.FreemarkerUtil;
import com.fcore.utils.map.ObjectMapUtil;


public class CreatePateUtil {
	
	/** 
	* @author zhangkui
	* @Title: createPage 
	* @Description: 通过模版生成jsp页面
	* @param templateName
	* @param distinctPath
	* @param distinctName
	* @param datamap
	* @throws IOException void
	*/
	public static void createPage(TableEntity table,FileEntity fileEntity,FlagEntity flagEntity) throws IOException{
		PageEntity pageEntity = new PageEntity(table);
		Map<String, Object> datamap = ObjectMapUtil.obj2Map(pageEntity);
		String folder_name = StringUtil.firstChar2Little(table.getEntityName());
		if(flagEntity.isCreatePage()){
			//edit.jsp
			String document_edit = StringUtil.getFilePath(fileEntity.getProjectPath(), Constans.WEB_JSP_PATH,folder_name);
			FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_EDIT,document_edit,"edit.html",datamap,fileEntity);
			//list.jsp
			String document_list = StringUtil.getFilePath(fileEntity.getProjectPath(), Constans.WEB_JSP_PATH,folder_name);
			FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_LIST,document_list,"list.html",datamap,fileEntity);
		}
	}
	
	public static void main(String[] args) {
		try {
			DbEntity dbConstans = new DbEntity("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.0.155:3306/lisendb", "root", "root");
			String tableName = "admin";
			FileEntity fileEntity = new FileEntity("E:/test/hz/abc/def", "com.manage", "zhangkui", false);
			TableEntity table = CreateEntityUtil.generateEntityRun(tableName,dbConstans,fileEntity);
			createPage(table,fileEntity,new FlagEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
