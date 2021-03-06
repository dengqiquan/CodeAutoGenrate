package com.fcore.utils.code;

import java.io.IOException;
import java.util.Map;

import com.fcore.commons.Constans;
import com.fcore.entity.CodeEntity;
import com.fcore.entity.FileEntity;
import com.fcore.entity.FlagEntity;
import com.fcore.entity.TableEntity;
import com.fcore.utils.StringUtil;
import com.fcore.utils.freemarker.FreemarkerUtil;
import com.fcore.utils.map.ObjectMapUtil;


/**   
* @Title: CreateCodeUtil.java 
* @Package com.gogotown.utils.code 
* @Description: 通过模版创建dao、service、action代码
* @author zhangkui
* @date 2016年03月08日 下午10:15:38 
* @version V1.0   
*/
public class CreateCodeUtil {
	
	/** 
	* @author zhangkui
	* @Title: createCode 
	* @Description: 生成service dao action实体类
	* @param tableName
	* @param tableDesc
	* @param fileEntity void
	*/
	public static void createCode(TableEntity table,FileEntity fileEntity,FlagEntity flagEntity){
		try {
			String entityName = table.getEntityName();
			if(null != fileEntity){
				//AdminAction
				String bigEntityName = StringUtil.firstChar2Up(entityName);
				//封装datamap实体类
				CodeEntity codeEntity = new CodeEntity(table, fileEntity.getBasePackage(),fileEntity.getAuthorName());
				Map<String, Object> datamap = ObjectMapUtil.obj2Map(codeEntity);
				String projectPath = fileEntity.getProjectPath();
				projectPath = projectPath + ((projectPath.endsWith("/") || projectPath.endsWith("\\")) ? "java" : "/java");
				if(flagEntity.isCreateDao()){
					//DaoImpl or Dao
					String packagePath = flagEntity.isCreateMapperXml() ? Constans.TYPE_DAO+"/impl" : Constans.TYPE_DAO;
					String lastName = flagEntity.isCreateMapperXml() ? "DAOImpl.java" : "Dao.java";
					String document_dao = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), packagePath);
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_DAO,document_dao,bigEntityName+lastName,datamap,fileEntity);
				}
				if(flagEntity.isCreatePropertie()){
					//XxDao.properties
					String document_dao = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_DAO);
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_PROPERTIE,document_dao,bigEntityName+"Dao.properties",datamap,fileEntity);
				}
				if(flagEntity.isCreateService()){
					//ServiceImpl or Service
					String packagePath = flagEntity.isCreateMapperXml() ? Constans.TYPE_SERVICE+"/impl" : Constans.TYPE_SERVICE;
					String document_service = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), packagePath);
					String lastName = flagEntity.isCreateMapperXml() ? "ServiceImpl.java" : "Service.java";
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_SERVICE,document_service,bigEntityName+lastName,datamap,fileEntity);
				}
				if(flagEntity.isCreateAction()){
					//Action
					String document_Action = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_ACTION);
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_ACTION,document_Action,bigEntityName+"Controller.java",datamap,fileEntity);
				}
				if(flagEntity.isCreateIDao()){
					//idao
					String i_document_dao = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_DAO);
					FreemarkerUtil.analysisTemplate(Constans.TEMPATE_I_DAO,i_document_dao,bigEntityName+"Dao.java",datamap,fileEntity);
				}
				if(flagEntity.isCreateIservice()){
					//iservice
					String i_document_service = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_SERVICE);
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_I_SERVICE,i_document_service,bigEntityName+"Service.java",datamap,fileEntity);
				}
				if(flagEntity.isCreateMapperXml()){
					//mapper
					String document = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_DAO);
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_MAPPER,document+"mapper/",bigEntityName+"Dao.xml",datamap,fileEntity);
				}
				if(flagEntity.isCreateMapperJava()){
					//mapper java
					String document = StringUtil.getFilePath(projectPath, fileEntity.getBasePackage(), Constans.TYPE_DAO);
					FreemarkerUtil.analysisTemplate(Constans.TEMPLATE_MAPPER_JAVA,document,bigEntityName+"Dao.java",datamap,fileEntity);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
