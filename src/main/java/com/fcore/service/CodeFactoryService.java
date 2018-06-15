package com.fcore.service;

import com.fcore.entity.DbEntity;
import com.fcore.entity.FileEntity;
import com.fcore.entity.FlagEntity;
import com.fcore.entity.TableEntity;
import com.fcore.utils.code.CreateCodeUtil;
import com.fcore.utils.database.CreateEntityUtil;
import com.fcore.utils.page.CreatePateUtil;
import com.fcore.utils.xml.SpringXmlUtil;
import com.fcore.utils.xml.XmlUtil;

public class CodeFactoryService {
	
	/** 
	* @author zhangkui
	* @Title: codeGenerateRun 
	* @Description: 代码生成器方法入口
	* @param tablename
	* @param dbEntity
	* @param fileEntity
	* @throws Exception void
	*/
	public static void codeGenerateRun(String tablename,DbEntity dbEntity,FileEntity fileEntity,FlagEntity flagEntity)throws Exception{
		//bean
		TableEntity table = CreateEntityUtil.generateEntityRun(tablename, dbEntity, fileEntity);
		//code
		CreateCodeUtil.createCode(table, fileEntity,flagEntity);
		//page
		CreatePateUtil.createPage(table, fileEntity,flagEntity);
		//web.xml
		XmlUtil.createWebXml(table,fileEntity,flagEntity);
		//spring-dao spring-service xml
		SpringXmlUtil.createDaoServiceXml(fileEntity, flagEntity, table);
	}
	
	public static void main(String[] args){
		/**
		 * String realPath = CodeGenerateTest.class.getResource("/").toString();
			realPath = realPath.substring(0, realPath.indexOf("/target/"))+"/";
			String projectPath = (realPath + "src/main/").replaceAll("file:/", "");
		 * */
		try {
			DbEntity dbEntity = new DbEntity("com.mysql.jdbc.Driver", "jdbc:mysql://123.57.147.211:3306/zhangkui123", "root", "zhangkui123");
			String tablename = "demo";
			FileEntity fileEntity = new FileEntity("/Users/zhangkui/Documents/testpage/","com.huihoo.entity","zhangkui",true);
			codeGenerateRun(tablename, dbEntity, fileEntity,new FlagEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
